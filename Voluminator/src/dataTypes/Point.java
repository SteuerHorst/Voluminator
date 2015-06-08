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
 * Container for 3D Points
 * @author Horst
 *
 */
public class Point {
	public double[] coords;
	
	/**
	 * Constructor
	 */
	public Point()
	{
		coords = new double[3];
	}

	/**
	 * Constructor
	 * @param a
	 * @param b
	 * @param c
	 */
	public Point(double a, double b, double c)
	{
		coords = new double[3];
		coords[0] = a;
		coords[1] = b;
		coords[2] = c;
	}
	
	/**
	 * prints the parameters of the point
	 */
	public void print()
	{
		System.out.println("point: " + coords[0] + " --- " + coords[1] + " --- " + coords[2]);
	}
	
	/**
	 * P = -1 * P
	 * @return
	 */
	public Point invert()
	{
		coords[0] = 0.0 - coords[0];
		coords[1] = 0.0 - coords[1];
		coords[2] = 0.0 - coords[2];
		
		return this;
	}
	
	/**
	 * translates the point
	 * @param moveVector
	 * @return
	 */
	public Point move(Point moveVector)
	{
		coords[0] = coords[0] + moveVector.coords[0];
		coords[1] = coords[1] + moveVector.coords[1];
		coords[2] = coords[2] + moveVector.coords[2];
		
		return this;
	}
	
	/**
	 * translates the point
	 * @param x1
	 * @param x2
	 * @param x3
	 * @return
	 */
	public Point move(double x1, double x2, double x3)
	{
		coords[0] = coords[0] + x1;
		coords[1] = coords[1] + x2;
		coords[2] = coords[2] + x3;
		
		return this;
	}
}
