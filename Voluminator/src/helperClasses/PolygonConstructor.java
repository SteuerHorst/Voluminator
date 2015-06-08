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

import java.util.List;
import java.util.Vector;

import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.gml.GMLClass;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;

import dataTypes.Point;
import dataTypes.Polygon;
import dataTypes.Ring;

/**
 * Constructs polygons from CityGML buildings
 * @author Horst Steuer
 *
 */
public class PolygonConstructor {

	
	/**
	 * Constructs Polygons from CityGML boundary surface properties
	 * @param bsp
	 * @return
	 */
	public Vector<Polygon> constructPolygons(List<BoundarySurfaceProperty> bsp)
	{
		Vector<Polygon> pols = new Vector<Polygon>();
		for( BoundarySurfaceProperty a : bsp)
		{
			AbstractBoundarySurface b = a.getBoundarySurface();
			MultiSurfaceProperty 	c = b.getLod2MultiSurface();
			MultiSurface 			d = c.getGeometry();
			List<SurfaceProperty>   e = d.getSurfaceMember();
			for( SurfaceProperty f  : e)
			{
				if(f.getGeometry().getGMLClass() == GMLClass.POLYGON)
				{
					pols.add(constructPolygon(f));
				} else if(f.getGeometry().getGMLClass() == GMLClass.COMPOSITE_SURFACE)
				{
					for( SurfaceProperty g: ((CompositeSurface)f.getGeometry()).getSurfaceMember())
					{
						pols.add(constructPolygon(g));
					}
				}
			}
		}
		return pols;
	}
	/**
	 * Constructs a Polygon from CityGML surface property
	 * @param f
	 * @return
	 */
	private Polygon constructPolygon( SurfaceProperty f)
	{
		Polygon pol = new Polygon();	
		org.citygml4j.model.gml.geometry.primitives.Polygon p = (org.citygml4j.model.gml.geometry.primitives.Polygon)f.getSurface();		
		LinearRing r = (LinearRing) (p.getExterior().getRing());
		DirectPositionList q = r.getPosList();
		List<Double> a1 = q.getValue();
		
		//exterior
		pol.addExterior(constructRing(a1));
		
		//interior
		List<AbstractRingProperty> liste = (List<AbstractRingProperty>)p.getInterior();
		for(AbstractRingProperty ar : liste)
		{
			LinearRing r2 = (LinearRing)ar.getRing();
			DirectPositionList q2 = r2.getPosList();
			List<Double> a2 = q2.getValue();
			pol.addInterior(constructRing(a2));
		}
		
		pol.project();
		return pol;

	}
	
	/**
	 * Constructs a linear ring from a list of doubles (x1 y1 z1 x2 y2 z2 ... ). 
	 * @param poslist
	 * @return
	 */
	private Ring constructRing(List<Double> poslist)
	{
		Ring r = new Ring();
		for(int i = 0; i < (poslist.size()/3)-1;i++)
		{
			r.addPoint(new Point(poslist.get(i*3),poslist.get(i*3+1),poslist.get(i*3+2)));
		}
		return r;
	}
}
