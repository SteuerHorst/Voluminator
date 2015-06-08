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
 * Container for a vector
 * @author Horst
 *
 */
public class Vector extends Point {

	/**
	 * Constructor
	 * @param p
	 */
	public Vector(Point p)
	{
		this.coords[0] = p.coords[0];
		this.coords[1] = p.coords[1];
		this.coords[2] = p.coords[2];
	}
	
		/**
	 * Constructor
	 * 
	 */
	public Vector() {
	}

	/**
	 * computes the length of the vector
	 * @return
	 */
	public double getLength()
	{
		return Math.sqrt(coords[0]*coords[0]+coords[1]*coords[1]+coords[2]*coords[2]);
	}
	
	
	/**
	 * Computes the angle in the x-y plane and ignores z. Quite ugly...
	 * Projects angle on [0-90]
	 * @param a
	 * @return
	 */
	public double getAngle( Point a)
	{
		
		double value = a.coords[0] * coords[0] + a.coords[1] * coords[1];
		if ( value > 1.0)
			value = 1.0;
		if( value < -1.0)
			value = -1.0;
		double winkel = Math.acos( value )*(180.0/Math.PI);
		
		if( winkel > 90.0)
			winkel = 180.0 - winkel;
		return winkel;
	}
	
	
	/**
	 * Computes the angle in the x-y plane and ignores z. Quite ugly...
	 * @param a
	 * @return
	 */
	public double getAngleWithoutIntervalProjection( Point a)
	{
		
		double value = a.coords[0] * coords[0] + a.coords[1] * coords[1];
		if ( value > 1.0)
			value = 1.0;
		if( value < -1.0)
			value = -1.0;
		double winkel = Math.acos( value )*(180.0/Math.PI);
		
		
		return winkel;
	}
	
	
	/**
	 * normalizes a vector
	 */
	public void normalize()
	{
		double l = getLength();
		coords[0] /= l;
		coords[1] /= l;
		coords[2] /= l;
	}
	
	/**
	 * Construct a vector as a-b
	 * @param a
	 * @param b
	 */
	public void generateByDiff(Point a, Point b)
	{
		coords[0] = a.coords[0] - b.coords[0];
		coords[1] = a.coords[1] - b.coords[1];
		coords[2] = a.coords[2] - b.coords[2];		
	}
	
	/**
	 * constructs a vector as a vertical on the plane defined by vectors a and b
	 * @param a
	 * @param b
	 */
	public void generateAsNormal(Vector a, Vector b)
	{
		coords[0] =  a.coords[1]*b.coords[2] - a.coords[2]*b.coords[1];
		coords[1] = (a.coords[2]*b.coords[0] - a.coords[0]*b.coords[2]);
		coords[2] =  a.coords[0]*b.coords[1] - a.coords[1]*b.coords[0];
		normalize();
	}
	
	/**
	 * returns true if a vector b is parallel to this vector
	 * @param b
	 * @return
	 */
	public boolean isParalell(Vector b)
	{
		double epsilon = 0.9999; // Epsilon - if cos( angle ) between vectors is less than epsilon they are assumed parallel
		double len_a  = Math.sqrt(coords[0]* coords[0] + coords[1]*coords[1] + coords[2]*coords[2]);
		double len_b  = Math.sqrt(b.coords[0]* b.coords[0] + b.coords[1]*b.coords[1] +  b.coords[2]*b.coords[2]);
		
		//normalized scalar product = cos ( angle )
		double cos_angle = Math.abs((coords[0] * b.coords[0] + coords[1] * b.coords[1] + coords[2] * b.coords[2])/(len_a*len_b));
		if((cos_angle > epsilon))
			return true;
		else 
			return false;
	}
}
