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
/**
 * Simple Container for a BoundingBox (BBox).
 * @author Horst Steuer & Lukas Liebel
 *
 */
public class BoundingBox {
	public Point minPoint;
	public Point maxPoint;
	
	
	/**
	 * Constructor initializing BBox with Double.POSITIVE_INFINITY and Double.NEGATIVE_INFINITY values 
	 */
	public BoundingBox()
	{
		minPoint = new Point(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
		maxPoint = new Point(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
	}
	
	
	/**
	 * Updates the BBox: this = BBox of (this + b)
	 * @param b BBox which this is updated by.
	 */
	public void updateByBBox(BoundingBox b)
	{
		minPoint.coords[0] = Math.min(b.minPoint.coords[0],minPoint.coords[0]);
		minPoint.coords[1] = Math.min(b.minPoint.coords[1],minPoint.coords[1]);
		minPoint.coords[2] = Math.min(b.minPoint.coords[2],minPoint.coords[2]);
		
		maxPoint.coords[0] = Math.max(b.maxPoint.coords[0], maxPoint.coords[0]);
		maxPoint.coords[1] = Math.max(b.maxPoint.coords[1], maxPoint.coords[1]);
		maxPoint.coords[2] = Math.max(b.maxPoint.coords[2], maxPoint.coords[2]);
	}
	
	
	/**
	 * Prints the BBox
	 */
	public void print()
	{
		System.out.println("x  min:" + minPoint.coords[0] + "    max: " + maxPoint.coords[0]);
		System.out.println("y  min:" + minPoint.coords[1] + "    max: " + maxPoint.coords[1]);
		System.out.println("z  min:" + minPoint.coords[2] + "    max: " + maxPoint.coords[2]);
	}
	
	
	/**
	 * Enlarges the BBox by adding v to all maximal coordinates and subtracting v from the minimal coordinates.
	 * @param v 
	 */
	public void bloat(double v)
	{
		minPoint.coords[0] -= v;
		minPoint.coords[1] -= v;
		minPoint.coords[2] -= v;
		
		maxPoint.coords[0] += v;
		maxPoint.coords[1] += v;
		maxPoint.coords[2] += v;
	}
	
	/**
	 * Returns the length of an axis
	 * 
	 * @param axis
	 * @return
	 */
	public double getLengthByAxis(int axis) {
		
		if (axis == 1) {
			return (maxPoint.coords[0] - minPoint.coords[0]);
		}
		else if (axis == 2) {
			return (maxPoint.coords[1] - minPoint.coords[1]);
		}
		else if (axis == 3) {
			return (maxPoint.coords[2] - minPoint.coords[2]);
		}
		else {
			return (-1);
		}
	}
	
	/**
	 * Returns the length of the longest axis.
	 * @return
	 */
	public double getMaxAxisLength() {
		return(Math.max(getLengthByAxis(1), Math.max(getLengthByAxis(2), getLengthByAxis(3))));
	}
	
	/**
	 * Returns the index of the longest axis.
	 * @return
	 */
	public int getMaxAxis() {
		if (getMaxAxisLength() == getLengthByAxis(1)) {return 1;}
		else if (getMaxAxisLength() == getLengthByAxis(2)) {return 2;}
		else if (getMaxAxisLength() == getLengthByAxis(3)) {return 3;}
		else {return -1;}
	}
	
}
