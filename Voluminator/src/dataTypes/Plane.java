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

import exceptions.DetZeroException;
import exceptions.ParallelException;


/**
 * 
 * Container for Planes
 * @author Horst
 *
 */
public class Plane {
	public Vector point;
	public Vector normal;
	public Vector r1, r2;
	public double lambda;
	
	/**
	 * 
	 * Generates a plane from 3 points
	 * direction vectors are orthogonal
	 * @param a
	 * @param b
	 * @param c
	 */
	public Plane(Point a, Point b, Point c)
	{
	
		point = new Vector( a);
		r1 = new Vector();
		r2 = new Vector();
		normal = new Vector();
		
		//compute normalized directional vectors
		r1.generateByDiff(a, b);
		r1.normalize();
		r2.generateByDiff(a, c);
		r2.normalize();
		
		//compute normal
		normal.generateAsNormal(r1,r2);
		r2.generateAsNormal(normal, r1);
		
		lambda = normal.coords[0] * point.coords[0] + normal.coords[1] * point.coords[1] + normal.coords[2] * point.coords[2];
	}
	

	/**
	 *  Computes the intersection/cut of Line and Plane:
	 * 
	 * (x)         (x)         (x)   (x)       (x)
	 * (y) +  r1 * (y)  + r2 * (y) = (y) + l * (y)
	 * (z)         (z)         (z)   (z)       (z)
	 * 
	 * 
	 * @param l
	 * @return
	 * @throws ParallelException
	 * @throws DetZeroException
	 */
	public CutContainer cutLine(Line l) throws ParallelException, DetZeroException
	{
		/*
		 * 
		 * 
		 */
		
		if( isParallel(l))
			throw new ParallelException();
		
		Matrix33 m = new Matrix33();
		m.A[0][0] = r1.coords[0];
		m.A[1][0] = r1.coords[1];
		m.A[2][0] = r1.coords[2];
		
		m.A[0][1] = r2.coords[0];
		m.A[1][1] = r2.coords[1];
		m.A[2][1] = r2.coords[2];
		
		m.A[0][2] = -l.direction.coords[0];
		m.A[1][2] = -l.direction.coords[1];
		m.A[2][2] = -l.direction.coords[2];
		
		double b[] = new double[3];
		b[0] = l.point.coords[0] - point.coords[0];
		b[1] = l.point.coords[1] - point.coords[1];
		b[2] = l.point.coords[2] - point.coords[2];
		
		
		m.invert();
		
		double x[] = m.multiply(b);

		return new CutContainer(new Point2D(x[0],x[1]), x[2]);
		
		
	}

	
	/**
	 * 
	 * projects a point p into the plane
	 * result is a 2D point in the local coordinate system
	 * @param p
	 * @return
	 * @throws ParallelException
	 * @throws DetZeroException
	 */
	public Point2D project(Point p) throws ParallelException, DetZeroException
	{
		CutContainer c = cutLine(new Line(p, normal));
		return c.p2d;
	}
	
	
	/**
	 * prints the plane parameters
	 */
	public void print()
	{
		System.out.print("point:");
		point.print();
		System.out.print("normal:");
		normal.print();
		System.out.print("r1:");
		r1.print();
		System.out.print("r2:");
		r2.print();
		System.out.print("lambda:");
		System.out.println(lambda);
	}
	
	
	
	/**
	 * 
	 * checks if line l is parallel to plane
	 * @param l
	 * @return
	 */
	public boolean isParallel(Line l)
	{
	
		Point b = l.direction;
		
		double epsilon = 0.0001;  // Epsilon - if cos( angle ) between line and plane is less than epsilon they are assumed parallel
		
		//length of plane normal and line directional vector
		double len_a = Math.sqrt(normal.coords[0]* normal.coords[0] + normal.coords[1]*normal.coords[1] + normal.coords[2]*normal.coords[2]);
		double len_b = Math.sqrt(b.coords[0]* b.coords[0] + b.coords[1]*b.coords[1] +  b.coords[2]*b.coords[2]);
		
		//normalized scalar product = cos ( angle )
		double cos_angle = Math.abs((normal.coords[0] * b.coords[0] + normal.coords[1] * b.coords[1] + normal.coords[2] * b.coords[2])/(len_a*len_b));

		if((cos_angle < epsilon))
			return true;
		else 
			return false;
	}

}
