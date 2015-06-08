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
 * Container for storing options of voluminator.
 * @author Horst Steuer
 * @author Maximilian Sindram
 *
 */
public class Options {
	
	/*
	 * When testing single ray - Vector<Polygon> intersections there might be several Intersections at the same point in space.
	 * This happens when there are duplicates of a polygons, but may happen also in other situations. This option allows for multiple 
	 * intersection points to be treated as just one point. This is an expensive operation (takes some time to compute).
	 */
	public boolean removeDuplicateIntersections = false; 
	
	/*
	 * The side length of a voxel. A voxel is assumed to be equilateral.
	 */
	public double voxelsize = 1.0;
	
	/*
	 * Allows for debug output. In the standard version this attribute is without effect.
	 * It is just here if changes to the code are made and debug output is added.
	 */
	public boolean debugMode = false;
	
	/*
	 * The minimal amount of directions voting for a voxel to be inside the object.
	 */
	public int mindirections = 3;
	
	/*
	 * The maximal amount of directions voting for a voxel to be inside the object. This is important for the Probability Version 
	 * of the Voluminator where the expectation value is computed.
	 */
	public int maxdirections = 6;
	
	/*
	 * Filenames for storing the resulting voxel output. OBJfileInner is for the voxels of the model itself, 
	 * while OBJfileOuter contains the outlying voxels.
	 * 
	 */
	public String OBJfileInner = "Inner.obj";
	public String OBJfileOuter = "Outer.obj";
	
	/*
	 * Indicator showing if voxel models should be stored. writeVoxelAsOBJ indicates full voxel model, while
	 * writeVoxelAsSmallOBJ indicates a reduced voxel model where inner surfaces are removed from the model so
	 * that only the outer shell remains.
	 * Only one should be activated since they write into the same file effectively overwritting one version.  
	 */
	public boolean writeVoxelAsOBJ = false;
	public boolean writeVoxelAsSmallOBJ = false;
	
	/*
	 * The number of threads to be used for the parallel volume calculation
	 */
	public int numberOfThreads = 1;
	
	/*
	 * Filename for the input file. If the value is not changed the default filename is of type txt.
	 */
	public String inputfile = "defaultInputFile.txt";
	
	/*
	 * Filename for the output file. If the value is not changed the default filename is of type txt.
	 */
	public String logBasename = "defaultOutputFile.txt";
	
	/*
	 * Indicator showing the option to use probability version of calculating the volume. the Default value is set to false.
	 */
	public boolean probabilityversion = false;
	
	/**
	 * Should be implemented.
	 */
	public void checkOptionsForValidity()
	{
	}
	
}
