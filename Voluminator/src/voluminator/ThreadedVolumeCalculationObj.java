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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import dataTypes.Options;

/**
 * Main class for multithreaded calculation of volumes. For each building a new thread is started.
 * At the moment this works only with OBJ files but not CityGML.
 * @author Horst Steuer
 *
 */
public class ThreadedVolumeCalculationObj {


	/**
	 * Starts the computation for all OBJ-file in inputDirectory with a given voxel size (= side length of a equilateral voxel).
	 * nrThreads is the number of parallel active threads. Should be equal or less than the number of processors on host machine.
	 * Probability Version indicates if volume is calculated as expectation value (cf. class Voluminator) 
	 * 
	 * @param inputDirectory
	 * @param voxelSize
	 * 
	 * @param logBasename is the base name of the log file: log file name = logBasename + voxelSize + ".log";
	 * @throws Exception
	 */
	public static void compute(String inputDirectory, double voxelSize, boolean probabilityVersion, String logBasename, int nrThreads,  boolean removeDuplicateIntersections) throws Exception {
		
		// Create log-file
		String resultFileName = logBasename + voxelSize + ".log";
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter( resultFileName, true)));
		
		//Set options
		Options options = new Options();
		options.voxelsize = voxelSize;
		options.removeDuplicateIntersections = removeDuplicateIntersections;
		
		
		//Generate Array for threads
		ObjVoluminatorThread[] threads = new ObjVoluminatorThread[nrThreads];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = null;
		}
		
		//Iterate through all files in inputDirectory
		File dir = new File(inputDirectory);
		File[] files = dir.listFiles();
		if (files != null) {
			
			boolean end = false;
			int count = 0;
			
			//While there are still file to open
			while (count < files.length || !end) {
				end = true;
				for (int i = 0; i < threads.length; i++) {
					//Check if a thread has not yet been initialised yet or has terminated
					if (threads[i] == null 	|| threads[i].getState() == Thread.State.TERMINATED) {
					
						if (threads[i] != null) {
							// A thread has terminated, get result and set thread to null
							System.out.println(threads[i].result);
							out.println(threads[i].result);
							threads[i] = null;
						}
						
						//get next file in directory
						while (count < files.length && files[count].isDirectory()) {
							count++;
						}
						
						if (count < files.length) {
							//start a new thread for a new file
							threads[i] = new ObjVoluminatorThread(files[count].getAbsolutePath(), options, probabilityVersion);
							threads[i].start();
							count++;
							end = false;
						}

					} else {
						end = false;
					}

				}
			}
		}
		out.close();

	}
}
