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


import dataTypes.Options;
import voluminator.Voluminator;
import helperClasses.OBJReader;

import java.io.FileNotFoundException;
import java.util.Vector;

/**
 * Class for a thread starting a voluminator.
 * @author Horst Steuer
 *
 */
public class ObjVoluminatorThread extends Thread {
	
	String fname;
	Options options;
	public String result;
	boolean probabilityVersion;
	
	/**
	 * Constructor
	 * @param filename
	 * @param o
	 */
	public ObjVoluminatorThread(String filename, Options o, boolean probabilityVersion)
	{
		fname = filename;
		options = o;
		this.probabilityVersion = probabilityVersion;
	}
	
	
	/* Method starting one thread for computing the volume of a single building.
	 * @see java.lang.Thread#run()
	 */
	@Override public void run()
	{
		long zstVorher;
		long zstNachher;
	
		OBJReader objr = new OBJReader();
	
		Vector<dataTypes.Polygon> bsp;
		try 
		{
			bsp = objr.readfile(fname);
			
			//Starting time measurement after IO
			zstVorher = System.currentTimeMillis();
			
			
			Voluminator v = new Voluminator(options);
			options.OBJfileInner = fname + "_" + options.voxelsize + "inner.obj";
			options.OBJfileOuter = fname + "_" + options.voxelsize + "outer.obj";
			double volume = 0.0;
			if( probabilityVersion)
			{
				volume = v.computeVolumeProbabilityVersion(bsp, options);
			}
			else
			{
				volume = v.computeVolume(bsp, options);
			}
	
			zstNachher = System.currentTimeMillis();
			result = fname + "  " + volume 	+ "     " + (zstNachher - zstVorher);
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
