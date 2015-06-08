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

/**
 * Container for a 3x3 matrix with some simple functions.
 * @author Horst Steuer
 *
 */
public class Matrix33 {
	public double A[][];
	
	public Matrix33()
	{
		A = new double[3][3];
	}
	
	/**
	 * Inverts this matrix.
	 */
	public void invert() throws DetZeroException
	{
		
		
		double I[][] = new double[3][3];
		
		double a = A[0][0];
		double b = A[0][1];
		double c = A[0][2];
		
		double d = A[1][0];
		double e = A[1][1];
		double f = A[1][2];
		
		double g = A[2][0];
		double h = A[2][1];
		double i = A[2][2];
		
		double det = a*e*i + b*f*g + c*d*h - c*e*g - b*d*i - a*f*h;
		if(det == 0.0)
			throw new DetZeroException();
		double indet = 1.0 / det;
		
		I[0][0] = indet * ( e*i-f*h );
		I[0][1] = indet * ( c*h-b*i );
		I[0][2] = indet * ( b*f-c*e );
		
		I[1][0] = indet * ( f*g-d*i );
		I[1][1] = indet * ( a*i-c*g );
		I[1][2] = indet * ( c*d-a*f );
		
		I[2][0] = indet * ( d*h-e*g );
		I[2][1] = indet * ( b*g-a*h );
		I[2][2] = indet * ( a*e-b*d );
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				A[x][y] = I[x][y];
			}
		}
	}
	
	/**
	 * 
		Multiplies this matrix = A and a vector x. returns Ax .

	 */
	public double[] multiply(double x[])
	{
		
		double b[] = new double[3];
		b[0] = A[0][0] * x[0] + A[0][1] * x[1] + A[0][2] * x[2];
		b[1] = A[1][0] * x[0] + A[1][1] * x[1] + A[1][2] * x[2];
		b[2] = A[2][0] * x[0] + A[2][1] * x[1] + A[2][2] * x[2];
		
		
		return b;
	}

}
