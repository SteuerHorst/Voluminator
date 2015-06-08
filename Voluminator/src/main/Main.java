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
package main;



import voluminator.ThreadedVolumeCalculationObj;
import voluminator.ThreadedVolumeCalculationCityGML;

/**
 * Main class with example calls to the volume calculation methods.
 * @author Horst Steuer & Maximilian Sindram
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {

		//Multithreaded Calculation using a folder of obj-files
		ThreadedVolumeCalculationObj.compute("testfiles/objs", 				//inputDirectory
												1.0,     				 	//voxel size
												false,   				 	//probabilityVersion   - experimental parameter
												"testfiles/objlog", 		//basename of logfiles
												4, 							//number of parallel threads
												false); 					//removing of duplicate intersections
		
		
		//Multithreaded calculation for CityGML	
		ThreadedVolumeCalculationCityGML.compute("testfiles/Testfile.gml",	//input file
													1.0, 					//voxel size
													8,						//number of parallel threads
													false,					//probabilityVersion   - experimental parameter
													"testfiles/CityGMLlog",	//basename of logfiles
													false);					//removing of duplicate intersections
		
		
		//Note, the results are stored in logfiles. First Column is the name of the building (CityGML) or file (OBJ),
		//the second column contains the computed volume and the third contains the time needed for the computation.
	}
}
