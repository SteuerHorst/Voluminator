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
import java.util.Vector;
import exceptions.DetZeroException;
import exceptions.ParallelException;

/**
 * Container for a ring
 * @author Horst
 *
 */
public class Ring {
	//3D position list and projections of these position in local coordiante system of the ring 
	public Vector<Point> pos;
	public Vector<Point2D> pos2D;
	
	/**
	 * Constructor
	 */
	public Ring()
	{
		pos = new Vector<Point>();
		pos2D = new Vector<Point2D>();
	}
	
	
	/**
	 * Projects the 3D coordinates into the plane of the ring
	 * @param plane
	 */
	public void project(Plane plane)
	{
	
		pos2D = new Vector<Point2D>();
		for(Point p: pos)
		{
			try {
				pos2D.add(plane.project(p));
			} catch (ParallelException | DetZeroException e) {
				System.out.println("shouldn't have happened: projection of polygon-point into plane failed");
			}
		}
	}
	
	public void addPoint(Point p)
	{
		pos.add(p);
	}
	
	/**
	 * Checks if point p lies inside the ring
	 * implements winding number algorithm
	 * @param p
	 * @return
	 */
	public boolean isPointInRing(Point2D p)
	{
		
		int counter = 0;
		int npoints = pos2D.size();
		Point2D p1 = pos2D.get(pos2D.size()-1);
		Point2D p2 = pos2D.get(0);
		double x = p.coords[0];
		double y = p.coords[1];
		
		boolean startIsAbove = (p1.coords[1] >= y);
		
		for(int i = 1; i <= npoints ; i++) 
		{
			boolean endIsAbove = (p2.coords[1] >= y);
			if(startIsAbove != endIsAbove) 
			{
				if((p2.coords[1] - y) * (p2.coords[0] - p1.coords[0]) <= (p2.coords[1] - p1.coords[1]) * (p2.coords[0] - x)) 
				{
					if(endIsAbove) 
					{   
						counter ++;   
					}
				}
				else
				{
					if(!endIsAbove) 
					{   
						counter --; 
					}
				}
			}
			startIsAbove = endIsAbove;
			if( i <	npoints)
			{
				p1 = p2;
				p2 = pos2D.get(i);
			}
		}
		  
		return (counter != 0);
	}
}
