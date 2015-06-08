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

package helperClasses;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.CityGMLBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;

import dataTypes.Polygon;




/**
 * Helper class which converts buildings from a CityGML-file to OBJ-files.
 * @author Horst
 *
 */
public class CityGMLToOBJ {


	
		/**
		 * Checks if list contains id
		 *  
		 * @param bliste
		 * @param id
		 * @return
		 */
		private static boolean isInList(String[] list, String id)
		{
			for(String it: list)
			{
				if(id.contains(it))
					return true;
			}
			return false;
		}
		
		
		/**
		 * Converts all files of bliste from CityGML defined in inputfile to an OBJ-file in outputFolder.
		 * @param inputfile
		 * @param outputFolder
		 * @param bliste
		 * @throws Exception
		 */
		public static void convert(String inputfile, String outputFolder, String[] bliste ) throws Exception
		{
			
			
			SimpleDateFormat df = new SimpleDateFormat("[HH:mm:ss] ");
			System.out.println(df.format(new Date()) + "setting up citygml4j context and JAXB builder");
			CityGMLContext ctx = new CityGMLContext();
			CityGMLBuilder builder = ctx.createCityGMLBuilder();
			System.out.println(df.format(new Date()) + "reading CityGML file " + inputfile + " completely into main memory");
			CityGMLInputFactory in = builder.createCityGMLInputFactory();
			CityGMLReader reader = in.createCityGMLReader(new File(inputfile));
			
			while (reader.hasNext()) {
				CityGML citygml = reader.nextFeature();
				
				System.out.println("Found " + citygml.getCityGMLClass() + 
						" version " + citygml.getCityGMLModule().getVersion());
				
				if (citygml.getCityGMLClass() == CityGMLClass.CITY_MODEL) {
					CityModel cityModel = (CityModel)citygml;

					System.out.println(df.format(new Date()) + "going through city model and counting building instances");
					
					for (CityObjectMember cityObjectMember : cityModel.getCityObjectMember()) {
						AbstractCityObject cityObject = cityObjectMember.getCityObject();
						if (cityObject.getCityGMLClass() == CityGMLClass.BUILDING)
						{
							
							Building building = (Building)cityObject;
							
							String buildingID = building.getId();

							if(bliste != null && isInList(bliste,buildingID ))
							{

									List<BoundarySurfaceProperty> bsp = building
											.getBoundedBySurface();

								PolygonConstructor pc = new PolygonConstructor();
						
								Polygon temp = new Polygon();
								
								
								String filename = outputFolder+ "/" + buildingID + ".obj";
								temp.saveObj(filename, pc.constructPolygons(bsp));
									
								
								}
						}
							}
								
						}
							
					}
			reader.close();
			System.out.println(df.format(new Date()) + "CityGMLToOBJ successfully finished");
		}
}