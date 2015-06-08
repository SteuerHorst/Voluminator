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
package helperClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import dataTypes.Point;
import dataTypes.Polygon;
import dataTypes.Ring;

/**
 * Reads an OBJ-File and returns a Vector of Polygons
 * @author Horst
 *
 */
public class OBJReader {

	Vector<Point> punkte;
	
	
	/**
	 * Reads an OBJ-File and returns a Vector of Polygons
	 * 
	 * @param filepath
	 * @return
	 * @throws FileNotFoundException
	 */
	public Vector<Polygon> readfile(String filepath) throws FileNotFoundException
	{
		punkte = new Vector<Point>() ;
		Vector<Polygon> result = new Vector<Polygon>();
		Scanner scanner = new Scanner(new File(filepath));

		while(scanner.hasNextLine())
		{
			String l = scanner.nextLine();
			
			if(l.startsWith("v "))
				gleanVector(l);
			if(l.startsWith("f "))
				result.add(gleanPolygon(l));
		}
		scanner.close();
		return result;
	}
	
	/**
	 * Parses a line of the OBJ-file containing a vector
	 * @param line
	 */
	private void gleanVector(String line)
	{
		Scanner scanner = new Scanner(line);
		scanner.useLocale( Locale.US);
		scanner.next();
		double x = scanner.nextDouble();
		double y = scanner.nextDouble();
		double z = scanner.nextDouble();
		punkte.add(new Point(x,y,z));
		scanner.close();
	}
	
	/**
	 * Parses a line of the OBJ-file containing a face
	 * @param line
	 * @return
	 */
	private Polygon gleanPolygon(String line)
	{
		Polygon p = new Polygon();
		Ring r = new Ring();
		
		
		Scanner scanner = new Scanner(line);
		scanner.next();
		while(scanner.hasNext())
		{
			String tmp = scanner.next();
			String[] tmp2 = tmp.split("/");
			r.addPoint(punkte.get( Integer.parseInt(tmp2[0]) -1));
		}
		p.addExterior(r);
		scanner.close();
		return p;
	}

}
