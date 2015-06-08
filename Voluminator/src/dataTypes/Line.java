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
 * Container for 3D lines.
 * @author Horst Steuer
 *
 */
public class Line {
	public Point point;
	public Point direction;
	

	/**
	 * Constructor.
	 * @param point positional vector of the line
	 * @param direction directional vector of the line
	 */
	public Line(Point point, Point direction)
	{
		this.point = point;
		this.direction = direction;
	}
	
	/**
	 * Prints the line parameters. 
	 */
	public void print()
	{
		System.out.println("*** line:");
		System.out.print("point: ");
		point.print();
		System.out.print("direction: ");
		direction.print();
	}
}
