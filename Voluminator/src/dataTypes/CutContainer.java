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
 * Container for storing information which is calculated when cutting plane and line:
 * 
 * (x)         (x)         (x)   (x)       (x)
 * (y) +  r1 * (y)  + r2 * (y) = (y) + l * (y)
 * (z)         (z)         (z)   (z)       (z)
 * 
 *  r1 and r2 above are stored in attribute p2d, and l in attribute l.
 * 
 *  @author Horst Steuer
 *
 */
public class CutContainer {

	public double l;
	public Point2D p2d;
	
	
	public CutContainer()
	{
		l = 0.0;
		p2d = new Point2D(0.0,0.0);
	}
	
	public CutContainer(double a, double b, double c)
	{
		l = c;
		p2d = new Point2D(a,b);
	}
	
	public CutContainer(Point2D a, double c)
	{
		l = c;
		p2d = a;
	}
}
