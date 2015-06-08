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

package voluminator;

import helperClasses.PolygonConstructor;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;

import dataTypes.BoundingBox;
import dataTypes.Line;
import dataTypes.Options;
import dataTypes.Point;
import dataTypes.Polygon;
import dataTypes.Voxelvolume;


/**
 * Class for computing the volume of objects given by boundary polygons.
 * @author Horst Steuer
 *
 */
public class Voluminator {

	Voxelvolume vox;
	
	static Options options; 
	
	/**
	 * Constructor
	 * @param o
	 */
	public Voluminator(Options o )
	{
		options =  o;
	}
	
	

	/**
	 * Main method of Voluminator: computes the volume of an object as defined by  Vector<Polygon> polygons
	 * @param polygons
	 * @return
	 */
	public double computeVolume(Vector<Polygon> polygons)
	{
		//Compute BoundingBox
		BoundingBox bb = computeBoundingBox(polygons);
	
		//Offset the bounding box for stability reasons
		double offset = 0.005;
		Point min = new Point(bb.minPoint.coords[0]-offset,bb.minPoint.coords[1]-offset,bb.minPoint.coords[2]-offset);
		Point max = new Point(bb.maxPoint.coords[0]+offset,bb.maxPoint.coords[1]+offset,bb.maxPoint.coords[2]+offset);
		//Create Voxelvolume
		vox = new Voxelvolume(min, max, options.voxelsize);
	
		//per Voxel
		for(int x = 0 ; x < vox.sizex; x++ )
		{
			for(int y = 0 ; y < vox.sizey; y++ )
			{
				for(int z = 0 ; z < vox.sizez; z++ )
				{
					vox.voxel[x][y][z] = 0;
					//construct lines
					Point p = vox.getPointAt(x, y, z);
					
					Vector<Line> lines = new Vector<Line>();
					lines.add(new Line(p, new Point(1,0,0)));
					lines.add(new Line(p, new Point(0,1,0)));
					lines.add(new Line(p, new Point(0,0,1)));
					
					int counter = getVote(lines, polygons);
					
					
					if(counter >= options.mindirections)
						vox.voxel[x][y][z] = 1;
				}
			}
		}

		//Store Voxel_volume
		try {
			if(options.writeVoxelAsOBJ)
				vox.writeOBJs(options.OBJfileOuter, options.OBJfileInner, false);
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		try {
			if(options.writeVoxelAsSmallOBJ)
				vox.writeSmallOBJs(options.OBJfileOuter, options.OBJfileInner);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vox.getVolume();
	}
	

	/**
	 * Computes the volume of an object as defined by Vector<Polygon> polygons. 
	 * The difference to computeVolume(Vector<Polygon> polygons) is that instead of deciding by vote if a voxel is inside or outside,
	 * this method saves the amount of 'inside-votes' in the voxel volume and computes a expectation value: 
	 *  Volume = Sum over all voxel ( Volume_of_single_voxel * vote / max_rays ) 
	 * @param polygons
	 * @return
	 */
	public double computeVolumeProbabilityVersion(Vector<Polygon> polygons)
	{
	
		//Compute BoundingBox
		BoundingBox bb = computeBoundingBox(polygons);
	
		//Offset the bounding box for stability reasons
		double offset = 0.005;
		Point min = new Point(bb.minPoint.coords[0]-offset,bb.minPoint.coords[1]-offset,bb.minPoint.coords[2]-offset);
		Point max = new Point(bb.maxPoint.coords[0]+offset,bb.maxPoint.coords[1]+offset,bb.maxPoint.coords[2]+offset);
		//Create Voxelvolume
		vox = new Voxelvolume(min, max, options.voxelsize);
		
		
		//per Voxel
		for(int x = 0 ; x < vox.sizex; x++ )
		{
			for(int y = 0 ; y < vox.sizey; y++ )
			{
				for(int z = 0 ; z < vox.sizez; z++ )
				{
					vox.voxel[x][y][z] = 0;
					//construct lines
					Point p = vox.getPointAt(x, y, z);
					
					Line lx = new Line(p, new Point(1,0,0));
					Line ly = new Line(p, new Point(0,1,0));
					Line lz = new Line(p, new Point(0,0,1));
					
					
					Vector<Line> lines = new Vector<Line>();
					lines.add(lx);
					lines.add(lz);
					lines.add(ly);
						
					vox.voxel[x][y][z] = getVote(lines, polygons);
				}
			}
		}
		
		//Save Voxel_volume
		try {
			if(options.writeVoxelAsOBJ)
				vox.writeOBJs(options.OBJfileOuter, options.OBJfileInner, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(options.writeVoxelAsSmallOBJ)
			System.out.println("Writing small obj files is currently not implemented for the Probability Version!");
		return vox.getVolumeProbabilityVersion(options.maxdirections);
	}
	
	
	
	
	
	/**
	 * Main method of Voluminator: computes the volume of an object as defined by  Vector<Polygon> polygons
	 * This is the old version! Here lines are tested individually instead of getting a vote of 6 rays!!!
	 * @param polygons
	 * @return
	 */
	@Deprecated
	public double computeVolumeOldVersion(Vector<Polygon> polygons)
	{
		//Compute BoundingBox
		BoundingBox bb = computeBoundingBox(polygons);
	
		//Offset the bounding box for stability reasons
		double offset = 0.005;
		Point min = new Point(bb.minPoint.coords[0]-offset,bb.minPoint.coords[1]-offset,bb.minPoint.coords[2]-offset);
		Point max = new Point(bb.maxPoint.coords[0]+offset,bb.maxPoint.coords[1]+offset,bb.maxPoint.coords[2]+offset);
		//Create Voxelvolume
		vox = new Voxelvolume(min, max, options.voxelsize);
	
		//per Voxel
		for(int x = 0 ; x < vox.sizex; x++ )
		{
			for(int y = 0 ; y < vox.sizey; y++ )
			{
				for(int z = 0 ; z < vox.sizez; z++ )
				{
					vox.voxel[x][y][z] = 0;
					//construct lines
					Point p = vox.getPointAt(x, y, z);
					
					Vector<Line> lines = new Vector<Line>();
					lines.add(new Line(p, new Point(1,0,0)));
					lines.add(new Line(p, new Point(0,1,0)));
					lines.add(new Line(p, new Point(0,0,1)));
					
					// Old version... not as described in paper!! Here lines are tested individually!!
					  
				 	Line lx = new Line(p, new Point(1,0,0));
					Line ly = new Line(p, new Point(0,1,0));
					Line lz = new Line(p, new Point(0,0,1));
					
					//Tests in 3 directions
					//OR - composition
					int counter = 0;
					if( testLinePolygons(lx, polygons) ) 
						counter++;						
					
					if( testLinePolygons(ly, polygons) )
						counter++;
					
					if( testLinePolygons(lz, polygons) )
						counter++;

					if(counter >= options.mindirections)
						vox.voxel[x][y][z] = 1;
				}
			}
		}

		//Store Voxel_volume
		try {
			if(options.writeVoxelAsOBJ)
				vox.writeOBJs(options.OBJfileOuter, options.OBJfileInner, false);
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		try {
			if(options.writeVoxelAsSmallOBJ)
				vox.writeSmallOBJs(options.OBJfileOuter, options.OBJfileInner);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vox.getVolume();
	}
	
	/**
	 * Computes the Bounding Box of a set of polygons
	 * @param polygons
	 * @return
	 */
	public BoundingBox computeBoundingBox(Vector<Polygon> polygons)
	{
		BoundingBox bb = new BoundingBox();
		
		for(Polygon p : polygons)
		{
			bb.updateByBBox(p.getBoundingBox());
		}
		
		return bb;
	}
	


	/**
	 * Computes the 'vote' for all lines (meaning twice as many rays)
	 * Returns the number of rays which vote for the point being inside the object.
	 * Attention: this makes sense only if the starting point of all lines are identical!
	 * @param lines
	 * @param polygons
	 * @return
	 */
	private int getVote(Vector<Line> lines, Vector<Polygon> polygons)
	{
		int sumVotes = 0;
		
		for(Line l : lines)
		{
			sumVotes +=  getVote(l, polygons);
		}
		
		return sumVotes;
	}
	
	/**
	 * Gets the 'vote' for one line = two rays. Both rays start at the starting point of line l, one ray goes into the positive,
	 * the other into the negative direction defined by the line.
	 * Returns 0 if both rays result in a even number of intersections meaning that both rays vote for the point being outside of the object.
	 * Returns 1 if only one rays result in an uneven number of intersections meaning that one ray votes for the point being inside of the object.
	 * Returns 2 if both rays result in an uneven number of intersections meaning that both rays vote for the point being inside of the object.
	 * @param l
	 * @param polygons
	 * @return
	 */
	private int getVote(Line l, Vector<Polygon> polygons)
	{
		
		if(options.removeDuplicateIntersections)
		{
			int result = 0;
			//By using TreeSet as data structure, duplicate intersections are removed
			TreeSet<Double> left = new TreeSet<Double>();
			TreeSet<Double> right = new TreeSet<Double>();
			for(Polygon p : polygons)
			{
				double cut = p.LineCutsPolygon(l);
				//Test if the intersection is in positive or negative direction on the line
				if(cut > 0.0)
				{
					right.add(cut);
				}
				
				if(cut < 0.0)
				{
					left.add(cut);
				}
			}
			
			if( left.size() > 0 && (left.size()%2) == 1)
				result++;
			if( right.size() > 0 && (right.size()%2) == 1)
				result++;
			return result;
		}
		else
		{
			int result = 0;
			int left = 0, right = 0;
		
			for(Polygon p : polygons)
			{
				double cut = p.LineCutsPolygon(l);
				
				//Test if the intersection is in positive or negative direction on the line
				if(cut > 0.0)
				{
					right++;
				}
				
				if(cut < 0.0)
				{
					left++;
				}
			}
			
			if( left > 0 && (left%2) == 1)
				result++;
			if( right > 0 && (right%2) == 1)
				result++;
			return result;
		}
	}
	
	
	
	/**
	 * Tests if line cuts a uneven number of polygons on the "left" side of its starting point and also on the "right" side
	 * (if the answer is yes for both, the starting point is probably inside an object)
	 * @param l
	 * @param polygons
	 * @return
	 */
	private boolean testLinePolygons(Line l, Vector<Polygon> polygons) 
	{
	
		if(options.removeDuplicateIntersections)
			{
			//By using TreeSet as data structure, duplicate intersections are removed
			TreeSet<Double> left = new TreeSet<Double>();
			TreeSet<Double> right = new TreeSet<Double>();
			for(Polygon p : polygons)
			{
				double cut = p.LineCutsPolygon(l);
				
				//Test if the intersection is in positive or negative direction on the line
				if(cut > 0.0)
				{
					right.add(cut);
				}
				
				if(cut < 0.0)
				{
					left.add(cut);
				}
			}
			
			if( (left.size()%2 == 1) && (right.size()%2 == 1) && (left.size()>0) && (right.size()>0))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			int left = 0, right = 0;
			for(Polygon p : polygons)
			{
				double cut = p.LineCutsPolygon(l);
				
				//Test if the intersection is in positive or negative direction on the line
				if(cut > 0.0)
				{
					right++;
				}
				
				if(cut < 0.0)
				{
					left++;
				}
			}
			
			if( (left%2 == 1) && (right%2 == 1) && (left>0) && (right>0))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	
	/**
	 *  Overloaded method setting options and calling computeVolume(Vector<Polygon> polygons) 
	 *  
	 * @param bsp
	 * @param o
	 * @return
	 */
	public double computeVolume(List<BoundarySurfaceProperty> bsp, Options o)
	{
		options = o;
		Vector<Polygon> polygons = new PolygonConstructor().constructPolygons(bsp);
		return  computeVolume(polygons);
	}
	/**
	 * Overloaded method calling computeVolume(Vector<Polygon> polygons)
	 * 
	 * @param bsp
	 * @return
	 */
	public double computeVolume(List<BoundarySurfaceProperty> bsp)
	{
		Vector<Polygon> polygons = new PolygonConstructor().constructPolygons(bsp);
		return  computeVolume(polygons);
	}
	
	/**
	 * Overloaded method setting options and calling computeVolume(Vector<Polygon> polygons)
	 * 
	 * @param polygons
	 * @param o
	 * @return
	 */
	public double computeVolume(Vector<Polygon> polygons, Options o)
	{
		options = o;
		return computeVolume(polygons);
	}
	
	
	/**
	 *  Overloaded method setting options and calling computeVolumeProbabilityVersion(Vector<Polygon> polygons) 
	 *  
	 * @param bsp
	 * @param o
	 * @return
	 */
	public double computeVolumeProbabilityVersion(List<BoundarySurfaceProperty> bsp, Options o)
	{
		options = o;
		Vector<Polygon> polygons = new PolygonConstructor().constructPolygons(bsp);
		return  computeVolumeProbabilityVersion(polygons);
	}
	/**
	 * Overloaded method calling computeVolumeProbabilityVersion(Vector<Polygon> polygons)
	 * 
	 * @param bsp
	 * @return
	 */
	public double computeVolumeProbabilityVersion(List<BoundarySurfaceProperty> bsp)
	{
		Vector<Polygon> polygons = new PolygonConstructor().constructPolygons(bsp);
		return  computeVolumeProbabilityVersion(polygons);
	}
	
	/**
	 * Overloaded method setting options and calling computeVolumeProbabilityVersion(Vector<Polygon> polygons)
	 * 
	 * @param polygons
	 * @param o
	 * @return
	 */
	public double computeVolumeProbabilityVersion(Vector<Polygon> polygons, Options o)
	{
		options = o;
		return computeVolumeProbabilityVersion(polygons);
	}
	
	/**
	 * Returns the model of the voxelvolume.
	 * @return
	 */
	public Voxelvolume getVoxelvolume()
	{
		return vox;
	}
}
