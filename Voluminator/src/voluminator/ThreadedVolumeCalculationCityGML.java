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
    
    
    
    This file is contains parts of citygml4j.
	Copyright (c) 2007 - 2010
	Institute for Geodesy and Geoinformation Science
	Technische Universitaet Berlin, Germany
	http://www.igg.tu-berlin.de/
	
	The citygml4j library is free software:
	you can redistribute it and/or modify it under the terms of the
	GNU Lesser General Public License as published by the Free
	Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	 
	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	 
	You should have received a copy of the GNU Lesser General Public
	License along with this library. If not, see 
	<http://www.gnu.org/licenses/>.
	 
*/

package voluminator;

import helperClasses.BuildingReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dataTypes.BuildingCallable;
import dataTypes.Options;

/**
 * Starts the Voluminator for each building of a given CityGML -  XML - file.
 * This version supports multithreading!!
 * 
 * @author Horst Steuer
 * @author Maximilian Sindram
 *
 */
public class ThreadedVolumeCalculationCityGML {
	
	/**
	 * Starts the threaded computation for each building of a given CityGML - XML - file given by inputfile with a given 
	 * voxel size (= side length of a equilateral voxel) and number of threads.
	 * Probability Version indicates if volume is calculated as expectation value (cf. class Voluminator) 
	 * logBasename is the base name of the log file: log file name = logBasename + voxelSize + ".log";
	 * @param inputfile
	 * @param voxelsize
	 * @param numberOfThreads
	 * @param probabilityversion
	 * @param logBasename
	 * @param removeDuplicateIntersections
	 * 
	 * @throws Exception
	 */
	public static void compute(String inputfile, double voxelsize, int numberOfThreads, boolean probabilityversion, String logBasename, boolean removeDuplicateIntersections) throws Exception
	{
		long start = System.currentTimeMillis();
		
		// setting global options for calculation
		Options options = new Options();
		options.voxelsize = voxelsize;
		options.removeDuplicateIntersections = removeDuplicateIntersections;
		options.inputfile = inputfile;
		options.numberOfThreads = numberOfThreads;
		options.probabilityversion = probabilityversion;
		options.logBasename = logBasename;
		
		String resultFileName = logBasename + voxelsize + ".log";

		// reading the Building from CityGML file 
		BuildingReader buildingreader = new BuildingReader();
		List<BuildingCallable> buildings = buildingreader.readCityGMLFile(inputfile, options);
		
		// Threaded calculation
		ExecutorService executerService = Executors.newFixedThreadPool(numberOfThreads);
		Set<BuildingCallable> callables = new HashSet<BuildingCallable>();
		callables.addAll(buildings);
		
		List<Future<BuildingCallable>> futures = executerService.invokeAll(callables);
		
		for(Future<BuildingCallable> future : futures){
			if(probabilityversion){
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(resultFileName, true)))) {
				    out.println(future.get().getBuildingId()+ ", " + future.get().getVolume() + ", " + (future.get().gettAfter() - future.get().gettBefore()) + 
				    		" " + future.get().getNumVoxels0() +
				    		" " + future.get().getNumVoxels1() +
				    		" " + future.get().getNumVoxels2() +
				    		" " + future.get().getNumVoxels3() +
				    		" " + future.get().getNumVoxels4() +
				    		" " + future.get().getNumVoxels5() +
				    		" " + future.get().getNumVoxels6());
					}catch (IOException e) {
						System.out.println("Could not write to file");
					}
			}
			else{
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(resultFileName, true)))) {
				    out.println(future.get().getBuildingId()+ ", " + future.get().getVolume() + ", " + (future.get().gettAfter() - future.get().gettBefore()));
				}
			}
		}
		executerService.shutdown();
		long end = System.currentTimeMillis();
		System.out.println("calculation time: " + (end-start));
	}						
}

