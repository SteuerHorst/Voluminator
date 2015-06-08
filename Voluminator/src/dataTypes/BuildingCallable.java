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

package dataTypes;

import java.util.List;
import java.util.concurrent.Callable;

import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;

import voluminator.Voluminator;

/**
 * Container Class for building information. 
 * 
 * @author Horst Steuer
 * @author Maximilian Sindram
 *
 */

public class BuildingCallable implements Callable<BuildingCallable>{

	private String buildingId;
	private double volume;
	private int numVoxels0;
	private int numVoxels1;
	private int numVoxels2;
	private int numVoxels3;
	private int numVoxels4;
	private int numVoxels5;
	private int numVoxels6;
	private long tBefore;
	private long tAfter;
	private List<BoundarySurfaceProperty> bsp;
	private Options options;
	
	public BuildingCallable() {}

	
	/**
	 * @param options
	 */
	public BuildingCallable(Options options) {
		super();
		this.options = options;
	}



	@Override
	public BuildingCallable call() throws Exception {
		options.OBJfileInner = buildingId + "_" + options.voxelsize + "inner.obj";
		options.OBJfileOuter = buildingId + "_" + options.voxelsize + "outer.obj";
		tBefore = System.currentTimeMillis();
		
		Voluminator v = new Voluminator(options);
		
		if( options.probabilityversion){
			
			Voxelvolume voxels;
			volume = v.computeVolumeProbabilityVersion(bsp, options);				
			voxels = v.getVoxelvolume(); 	
			numVoxels0 = voxels.countVoxelsWithValue(0);			
			numVoxels1 = voxels.countVoxelsWithValue(1);		
			numVoxels2 = voxels.countVoxelsWithValue(2);		
			numVoxels3 = voxels.countVoxelsWithValue(3);		
			numVoxels4 = voxels.countVoxelsWithValue(4);		
			numVoxels5 = voxels.countVoxelsWithValue(5);		
			numVoxels6 = voxels.countVoxelsWithValue(6);
			tAfter = System.currentTimeMillis();
		}
		else{
			
			volume =  v.computeVolume(bsp, options);	
			tAfter = System.currentTimeMillis();
		}
		
		calculateValues();
		return this;
	}


	private void calculateValues(){
		
		
	}
	
	/**
	 * Get building information based on probability version
	 * 
	 * @return String
	 */
	public String getBuildingInformation(){
		
		if(options.probabilityversion){
			return buildingId + ";" + volume + ";" + (tAfter - tBefore)+
			";" + numVoxels0 +
    		";" + numVoxels1 +
    		";" + numVoxels2 +
    		";" + numVoxels3 +
    		";" + numVoxels4 +
    		";" + numVoxels5 +
    		";" + numVoxels6;
		}
		else{
			return buildingId + ";" + volume + ";" + (tAfter - tBefore);
		}
		
	}
	
	
	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public int getNumVoxels0() {
		return numVoxels0;
	}

	public void setNumVoxels0(int numVoxels0) {
		this.numVoxels0 = numVoxels0;
	}

	public int getNumVoxels1() {
		return numVoxels1;
	}

	public void setNumVoxels1(int numVoxels1) {
		this.numVoxels1 = numVoxels1;
	}

	public int getNumVoxels2() {
		return numVoxels2;
	}

	public void setNumVoxels2(int numVoxels2) {
		this.numVoxels2 = numVoxels2;
	}

	public int getNumVoxels3() {
		return numVoxels3;
	}

	public void setNumVoxels3(int numVoxels3) {
		this.numVoxels3 = numVoxels3;
	}

	public int getNumVoxels4() {
		return numVoxels4;
	}

	public void setNumVoxels4(int numVoxels4) {
		this.numVoxels4 = numVoxels4;
	}

	public int getNumVoxels5() {
		return numVoxels5;
	}

	public void setNumVoxels5(int numVoxels5) {
		this.numVoxels5 = numVoxels5;
	}

	public int getNumVoxels6() {
		return numVoxels6;
	}

	public void setNumVoxels6(int numVoxels6) {
		this.numVoxels6 = numVoxels6;
	}

	public List<BoundarySurfaceProperty> getBsp() {
		return bsp;
	}

	public void setBsp(List<BoundarySurfaceProperty> bsp) {
		this.bsp = bsp;
	}

	public long gettBefore() {
		return tBefore;
	}

	public void settBefore(long tBefore) {
		this.tBefore = tBefore;
	}

	public long gettAfter() {
		return tAfter;
	}

	public void settAfter(long tAfter) {
		this.tAfter = tAfter;
	}

	public Options getOptions() {
		return options;
	}


	public void setOptions(Options options) {
		this.options = options;
	}


}
