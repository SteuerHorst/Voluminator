/*
    This file is part of Voluminator.

    Voluminator is free software:
	you can redistribute it and/or modify it under the terms of the
	GNU Lesser General Public License as published by the Free
	Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	 
	Voluminator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
    You should have received a copy of the GNU Lesser General Public
	License along with this library. If not, see 
	<http://www.gnu.org/licenses/>
    
 
    If you are using this for scientific purposes, please cite

    Horst Steuer, Thomas Machl, Maximilian Sindram, Lukas Liebel, and Thomas H.
	Kolbe. Voluminator - Approximating the Volume of 3D Buildings to Overcome
	Topological Errors. In AGILE 2015, pages 343-362. Springer, 2015.
	
	
	Copyright 2015 
	Horst Steuer, Thomas Machl, Maximilian Sindram, Lukas Liebel and Thomas H.
	Kolbe
	LS Geoinformatik, TU Muenchen
	Technische Universitaet Muenchen, Germany
    https://www.gis.bgu.tum.de/
    
*/

package dataTypes;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import exceptions.DetZeroException;
import exceptions.ParallelException;

/**
 * Container for a 3D Polygon (Polygon should be flat)
 * @author Horst
 *
 */
public class Polygon {
	
	//one exterior and multiple interior rings
	private Ring exterior;
	private Vector<Ring> interior;
	//plane in which the polygon lies
	private Plane plane;
	//marker showing if coordinates of rings have already been projected to the local 2D coordinate system of the plane
	private boolean alreadyprojected = false;
	
	/**
	 * adds an exterior ring r
	 * @param r
	 */
	public void addExterior(Ring r)
	{
		exterior = r;
		alreadyprojected = false;
		computePlane();	
	}
	
	/**
	 * returns the exterior ring
	 * @return
	 */
	public Ring getExterior() {
		return exterior;
	}
	
	/**
	 * adds an interior ring r
	 * @param r
	 */
	public void addInterior(Ring r)
	{
		interior.add(r);
		alreadyprojected = false;
	}
	
	/**
	 * computes the plane parameters based on the first three points of the exrterior ring
	 * Note: it is not checked if the polygon is flat. If it is not, this may lead to inconsistencies!
	 */
	private void computePlane()
	{
		plane = new Plane(exterior.pos.get(0),exterior.pos.get(1),exterior.pos.get(2));
	}
	
	/**
	 * Gets point before p on the exterior ring
	 * @param p
	 * @return
	 */
	public Point getPointBefore( Point p)
	{
		return getPointBefore(exterior.pos.indexOf(p));	
	}
	
	/**
	 * Gets point behind p on the exterior ring
	 * @param p
	 * @return
	 */
	public Point getPointBehind( Point p)
	{
		return getPointBehind(exterior.pos.indexOf(p));
	}
	
	
	/**
	 * Gets point before the i-th point  on the exterior ring
	 * @param index
	 * @return
	 */
	public Point getPointBefore( int index)
	{
		if( index <= 0 )
			return exterior.pos.lastElement();
		else
			return exterior.pos.get( index - 1);
	}
	/**
	 * Gets point behind the i-th point  on the exterior ring
	 * @param index
	 * @return
	 */
	public Point getPointBehind( int index)
	{
		if (index >= exterior.pos.size() - 1)
			return exterior.pos.firstElement();
		else
			return exterior.pos.get(index+1);
	}
	
	/**
	 * Returns an iterator over the points of the exterior ring
	 * This is really ugly and should be solved differently...
	 * @return
	 */
	public Iterator<Point> getIterator()
	{
		return exterior.pos.iterator();
	}
	
	/**
	 * Constructor
	 */
	public Polygon()
	{
		exterior = new Ring();
		interior = new Vector<Ring>();
	}
	
	/**
	 * Computes the intersection/cut of line with the polygon.
	 * 
	 * returns 0 if not cutting
	 *         - a negative value if cut is on the "left hand" of the linepoint
	 *         + a positive value if cut is on the "right hand" of the linepoint
	 * @param l
	 * @return
	 */
	public double LineCutsPolygon(Line l)
	{
	
		if(!alreadyprojected)
		{
			project();
		}
		
		CutContainer c;
		//Try to find intersection between line and plane of the polygon
		try {
			c = plane.cutLine(l);
			Point p3d = new Point(l.point.coords[0] + c.l *l.direction.coords[0],
					              l.point.coords[1] + c.l *l.direction.coords[1],
					              l.point.coords[2] + c.l *l.direction.coords[2]);
			c.p2d = plane.project(p3d);
		} catch (ParallelException | DetZeroException e) {
			//no cut
			return 0.0;
		}
		//If there is a valid  intersection between line and plane of the polygon, check if the intersection is within the polygon
		if(isPointInPolygon(c.p2d))
		{
			return c.l;
		}
		return 0;
	}
	
	/**
	 * computes the axis oriented bounding box of the plane
	 * Note: it is assumed that the interior rings are fully inside the exterior ring. If it is not, this may lead to inconsistencies!
	 * @return
	 */
	public BoundingBox getBoundingBox()
	{

		BoundingBox result = new BoundingBox();
		for(Point p: exterior.pos)
		{
			result.minPoint.coords[0] = Math.min(p.coords[0], result.minPoint.coords[0]);
			result.maxPoint.coords[0] = Math.max(p.coords[0], result.maxPoint.coords[0]);
			
			result.minPoint.coords[1] = Math.min(p.coords[1], result.minPoint.coords[1]);
			result.maxPoint.coords[1] = Math.max(p.coords[1], result.maxPoint.coords[1]);
			
			result.minPoint.coords[2] = Math.min(p.coords[2], result.minPoint.coords[2]);
			result.maxPoint.coords[2] = Math.max(p.coords[2], result.maxPoint.coords[2]);
			
		}
		return result;		
	}
	
	/**
	 * tests if point is inside polygon
	 * point is inside if it is in the outer ring but out of any inner ring
	 * @param p
	 * @return
	 */
	private boolean isPointInPolygon(Point2D p)
	{
		if(exterior.isPointInRing(p))
		{
			for(Ring r : interior)
			{
				if(r.isPointInRing(p))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * projects all points of the exterior ring from 3D space into 2D space using the local coordinate system of the plane of the polygon
	 */
	public void project()
	{
		computePlane();
		exterior.project(plane);
		
		for(Ring r : interior)
		{
			r.project(plane);
		}
		alreadyprojected = true;
	}
	
	/**
	 * writes the polygons in pols into a *.obj file
	 * this method should probably be elsewhere...
	 * @param filename
	 * @param pols
	 * @throws IOException
	 */
	public void saveObj(String filename, Vector<Polygon> pols) throws IOException
	{
		int count = 1;
		FileWriter out_ext = null;
		 if(filename.length()>1)
		 {
			 File fext = new File(filename);
			 out_ext = new FileWriter(fext);
			 out_ext.write("#no comment");
			 out_ext.write(System.getProperty("line.separator"));
		 }
		 
		 
		 for(Polygon p: pols)
		 {
			 String face = "f ";
			 for(Point point: p.exterior.pos)
			 {
				 out_ext.write("v " + point.coords[0] + " " + point.coords[1] + " " + point.coords[2] + System.getProperty("line.separator"));
				 face += count + " ";
				 count++;
			 }
		 
			 face += System.getProperty("line.separator");
			 out_ext.write(face);
			 
		 }
		
		 if(filename.length()>1)
			 out_ext.flush();
	
	}
 
}
