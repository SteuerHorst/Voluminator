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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * Container for Voxelvolume
 * 
 * Volume is aligned with the global coordinate system. Each voxel is represented by an int . 
 * Voxel are equilateral even though the voxel size can be given according to each of the three axis.
 * 
 * @author Horst
 *
 */
public class Voxelvolume {

	public int[][][] voxel;
	public Point minPoint; //top left Point
	public Point maxPoint; //bottom right Point
	public double voxsizex, voxsizey, voxsizez; //size of a single voxel
	public int sizex, sizey, sizez; //number of voxel along the three axis
	private int count; // just some private counters for internal reasons
	private int[] countProbabilityVersion;	// just some private counters for internal reasons

	
	/**
	 * Constructor, computes the size of of the volume in voxel-length units.
	 * @param topleft
	 * @param bottomright
	 * @param voxelsize
	 */
	public Voxelvolume(Point topleft, Point bottomright, double voxelsize )
	{
		voxsizex = voxsizey = voxsizez = voxelsize;
		sizex = (int) (((bottomright.coords[0] - topleft.coords[0]) / voxsizex) + 1);
		sizey = (int) (((bottomright.coords[1] - topleft.coords[1]) / voxsizey) + 1);
		sizez = (int) (((bottomright.coords[2] - topleft.coords[2]) / voxsizez) + 1);
		voxel = new int[sizex][sizey][sizez];
		minPoint = topleft;
		maxPoint = bottomright;
	}
	
	/**
	 * Returns the global coordinates of a voxel at x,y,z
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Point getPointAt(int x, int y, int z)
	{
		return new Point(minPoint.coords[0] + x*voxsizex, minPoint.coords[1] + y*voxsizey, minPoint.coords[2] + z*voxsizez);
	}
	
	/**
	 * Computes the volume by adding the volume of each filled voxel (=voxel with value > 0 ).
	 *  
	 * @param probabilityversion
	 * @return
	 */
	public double getVolume()
	{
		double factor = voxsizex * voxsizey * voxsizez;
		double result = 0.0;
		for(int x = 0 ; x < sizex; x++ )
		{
			for(int y = 0 ; y <sizey; y++ )
			{
				for(int z = 0 ; z < sizez; z++ )
				{
					if(voxel[x][y][z] == 1.0)
					{							
						result += 1.0;
					}
				}
			}
		}
		return result * factor;
	}
	
	/**
	 * Computes the expected volume for the probability version as:
	 * 	Volume = Sum over all voxel ( Volume_of_single_voxel * vote / max_rays )  
	 * @param probabilityversion
	 * @return
	 */
	public double getVolumeProbabilityVersion(int maxVotes)
	{
		double factor = voxsizex * voxsizey * voxsizez;
		double result = 0.0;
		for(int x = 0 ; x < sizex; x++ )
		{
			for(int y = 0 ; y < sizey; y++ )
			{
				for(int z = 0 ; z < sizez; z++ )
				{
					result += ((double) voxel[x][y][z])/(double) maxVotes; 
				}
			}
		}
		return result * factor;
	}
	
	/**
	 * Counts the  Voxel which have a value of v.
	 * @param v
	 * @return
	 */
	public int countVoxelsWithValue(int v)
	{
		int sum = 0;
		for(int x = 0 ; x<sizex; x++ )
		 {
			for(int y = 0 ; y<sizey; y++ )
			{
				for(int z = 0 ; z<sizez; z++ )
				{
					if(voxel[x][y][z] == v)
						sum++;
				}
			}
		 }
		return sum;
	}
	
	
	/**
	 * Appends a single Voxel to some OBJ-File. 
	 * If the voxel has value 0.0 it is appended to out_ext, if it has value 1.0 it is appended to out_int .
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param out_ext
	 * @param out_int
	 * @throws IOException
	 */
	private void appendVoxelToFile(int x, int y, int z, FileWriter out_ext, FileWriter out_int ) throws IOException
	{
		Point points[] = new Point[8];
		points[0] = getPointAt(x  , y  , z  ); 
		points[1] = getPointAt(x+1, y  , z  ); 
		points[2] = getPointAt(x+1, y  , z+1); 
		points[3] = getPointAt(x  , y  , z+1); 
		points[4] = getPointAt(x  , y+1, z  ); 
		points[5] = getPointAt(x+1, y+1, z  ); 
		points[6] = getPointAt(x+1, y+1, z+1); 
		points[7] = getPointAt(x  , y+1, z+1); 
		
		for(int i=0; i<8;i++)
		{
			if(out_ext != null)
			{
				out_ext.write("v " + points[i].coords[0] + " " + points[i].coords[1] + " " + points[i].coords[2] + System.getProperty("line.separator"));
			}
			if(out_int != null)
			{
				out_int.write("v " + points[i].coords[0] + " " + points[i].coords[1] + " " + points[i].coords[2] + System.getProperty("line.separator"));
			}
		}
		
		
		String faces[] = new String[6];
		faces[0] = new String("f " + (count  ) + " " + (count+3) +" " + (count+2) + " " + (count+1)+System.getProperty("line.separator"));
		faces[1] = new String("f " + (count  ) + " " + (count+1) +" " + (count+5) + " " + (count+4)+System.getProperty("line.separator"));
		faces[2] = new String("f " + (count+1) + " " + (count+2) +" " + (count+6) + " " + (count+5)+System.getProperty("line.separator"));
		faces[3] = new String("f " + (count+2) + " " + (count+3) +" " + (count+7) + " " + (count+6)+System.getProperty("line.separator"));
		faces[4] = new String("f " + (count+3) + " " + (count+0) +" " + (count+4) + " " + (count+7)+System.getProperty("line.separator"));
		faces[5] = new String("f " + (count+4) + " " + (count+5) +" " + (count+6) + " " + (count+7)+System.getProperty("line.separator"));
		
		for(int i=0; i<6 ;i++)
		{
			if(out_ext != null && voxel[x][y][z] == 0.0)
				out_ext.write(faces[i]);
			if(out_int != null && voxel[x][y][z] == 1.0)
				out_int.write(faces[i]);
		}
		count+=8;
	}
	
	/**
	 * Appends a single Voxel to some OBJ-File. 
	 * Depending on the value of the voxel this may be one ove several files.
	 * @param x
	 * @param y
	 * @param z
	 * @param ext
	 * @throws IOException
	 */
	private void appendVoxelToFileProbabilityVersion(int x, int y, int z, Vector<FileWriter> ext ) throws IOException
	{
				
		int index = voxel[x][y][z];
		Point points[] = new Point[8];
		points[0] = getPointAt(x  , y  , z  ); 
		points[1] = getPointAt(x+1, y  , z  ); 
		points[2] = getPointAt(x+1, y  , z+1); 
		points[3] = getPointAt(x  , y  , z+1); 
		points[4] = getPointAt(x  , y+1, z  ); 
		points[5] = getPointAt(x+1, y+1, z  ); 
		points[6] = getPointAt(x+1, y+1, z+1); 
		points[7] = getPointAt(x  , y+1, z+1); 
		
		String vectors = "";
		for(int i=0; i<8;i++)
		{
			vectors += "v " + points[i].coords[0] + " " + points[i].coords[1] + " " + points[i].coords[2] + System.getProperty("line.separator");
		}
		//overwrite count
		int count = this.countProbabilityVersion[index];
		String faces = "";
		
		faces += new String("f " + (count  ) + " " + (count+1) +" " + (count+2) + " " + (count+3) + System.getProperty("line.separator"));
		faces += new String("f " + (count  ) + " " + (count+4) +" " + (count+5) + " " + (count+1) + System.getProperty("line.separator"));
		faces += new String("f " + (count+1) + " " + (count+5) +" " + (count+6) + " " + (count+2) + System.getProperty("line.separator"));
		faces += new String("f " + (count+2) + " " + (count+6) +" " + (count+7) + " " + (count+3) + System.getProperty("line.separator"));
		faces += new String("f " + (count+3) + " " + (count+7) +" " + (count+4) + " " + (count+0) + System.getProperty("line.separator"));
		faces += new String("f " + (count+4) + " " + (count+7) +" " + (count+6) + " " + (count+5) + System.getProperty("line.separator"));
		
		ext.get(index).write(vectors);
		ext.get(index).write(faces);
		
		this.countProbabilityVersion[index] += 8;
	}

	
	/**
	 * Appends Voxel Face to some OBJ-File if neighboured voxels are note part of the model. 
	 * This leads to a smaller visualization model since not all internal faces are stored. 
	 * @param x
	 * @param y
	 * @param z
	 * @param out_ext
	 * @param out_int
	 * @throws IOException
	 */
	private void appendConditionalVoxel(int x, int y, int z, FileWriter out_ext, FileWriter out_int ) throws IOException
	{
		Point points[] = new Point[8];
		points[0] = getPointAt(x  , y  , z  ); 
		points[1] = getPointAt(x+1, y  , z  ); 
		points[2] = getPointAt(x+1, y  , z+1); 
		points[3] = getPointAt(x  , y  , z+1); 
		points[4] = getPointAt(x  , y+1, z  ); 
		points[5] = getPointAt(x+1, y+1, z  ); 
		points[6] = getPointAt(x+1, y+1, z+1); 
		points[7] = getPointAt(x  , y+1, z+1); 
		
		for(int i=0; i<8;i++)
		{
			if(out_ext != null)
			{
				out_ext.write("v " + points[i].coords[0] + " " + points[i].coords[1] + " " + points[i].coords[2] + System.getProperty("line.separator"));
			}
			if(out_int != null)
			{
				out_int.write("v " + points[i].coords[0] + " " + points[i].coords[1] + " " + points[i].coords[2] + System.getProperty("line.separator"));
			}
		}
		
		
		String faces[] = new String[6];
		faces[0] = new String("f " + (count  ) + " " + (count+3) +" " + (count+2) + " " + (count+1)+System.getProperty("line.separator"));
		faces[1] = new String("f " + (count  ) + " " + (count+1) +" " + (count+5) + " " + (count+4)+System.getProperty("line.separator"));
		faces[2] = new String("f " + (count+1) + " " + (count+2) +" " + (count+6) + " " + (count+5)+System.getProperty("line.separator"));
		faces[3] = new String("f " + (count+2) + " " + (count+3) +" " + (count+7) + " " + (count+6)+System.getProperty("line.separator"));
		faces[4] = new String("f " + (count+3) + " " + (count+0) +" " + (count+4) + " " + (count+7)+System.getProperty("line.separator"));
		faces[5] = new String("f " + (count+4) + " " + (count+5) +" " + (count+6) + " " + (count+7)+System.getProperty("line.separator"));
		
		if(out_ext != null && voxel[x][y][z] == 0.0)
		{
		

		//left face
			if(x-1<0 || voxel[x-1][y][z] != 0.0)
				out_ext.write(faces[4]);
			
		//right face
			if(x+1>=this.sizex || voxel[x+1][y][z] != 0.0)
				out_ext.write(faces[2]);
		//top face
			if(z+1>=this.sizez || voxel[x][y][z+1] != 0.0)
				out_ext.write(faces[3]);
		//bottom face
			if(z-1<0 || voxel[x][y][z-1] != 0.0)
				out_ext.write(faces[1]);
		//front face
			if(y-1<0 || voxel[x][y-1][z] != 0.0)
				out_ext.write(faces[0]);
		//back face
			if(y+1>=this.sizey || voxel[x][y+1][z] != 0.0)
				out_ext.write(faces[5]);
			
		}
		if(out_int != null && voxel[x][y][z] == 1.0)
		{

			
			//left face
				if(x-1<0 || voxel[x-1][y][z] != 1.0)
					out_int.write(faces[4]);
			//right face
				if(x+1>=this.sizex || voxel[x+1][y][z] != 1.0)
					out_int.write(faces[2]);
			//top face
				if(z+1>=this.sizez || voxel[x][y][z+1] != 1.0)
					out_int.write(faces[3]);
			//bottom face
				if(z-1<0 || voxel[x][y][z-1] != 1.0)
					out_int.write(faces[1]);
			//front face
				if(y-1<0 || voxel[x][y-1][z] != 1.0)
					out_int.write(faces[0]);
			//back face
				if(y+1>=this.sizey || voxel[x][y+1][z] != 1.0)
					out_int.write(faces[5]);
				
			
		}
	
		count+=8;
	}
	
	
	/**
	 * Exports Voxelvolume as OBJ. 
	 * The resulting OBJ-files contain all faces including surfaces inside the model. 
	 * Results in two files, one containing the inner voxels (value = 1.0) and one containing the outer voxels (value = 0.0 for debugging reasons).
	 * If the probabilityversion of this program is used, this results in a obj for each possible value of a voxel.
	 * 
	 * @param file_ext
	 * @param file_int
	 * @param probabilityversion
	 * @throws IOException
	 */
	public void writeOBJs(String file_ext, String file_int, boolean probabilityversion) throws IOException
	{
		if(probabilityversion && file_ext.length() > 0 )
		{
			
			//Initialising counter for all 6 files .... yikes... that constant again....
			countProbabilityVersion = new int[7];
			for(int i = 0; i < 7 ; i++)
				countProbabilityVersion[i] = 1;
			
			//Initializing of 6 File Writers
			Vector<FileWriter> outvector = new Vector<FileWriter>();
			for(int i = 0; i < 7; i++)
			{
				File fext = new File((file_ext + "_voting_" + i + ".obj"));
				outvector.add(new FileWriter(fext));	
				outvector.get(i).write("mtllib Materialsammlung.mtl"+ System.getProperty("line.separator") );
				outvector.get(i).write("usemtl Material"+ i + System.getProperty("line.separator") );
			}
			 
			for(int x = 0 ; x < sizex; x++ )
			 {
				for(int y = 0 ; y < sizey; y++ )
				{
					for(int z = 0 ; z < sizez; z++ )
					{
						appendVoxelToFileProbabilityVersion(x, y, z, outvector);		
					}
				}
			 }
			for(int i = 0; i < 7; i++)
			{
				outvector.get(i).flush();
			}
			
		}
		else
			{
			count = 1;
			FileWriter out_ext = null, out_int = null;
			 if(file_ext.length()>1)
			 {
				 File fext = new File(file_ext);
				 out_ext = new FileWriter(fext);
				 out_ext.write("#no comment");
				 out_ext.write(System.getProperty("line.separator"));
			 }
			 if(file_int.length()>1)
			 {
				 File fint = new File(file_int);
				 out_int = new FileWriter(fint);
				 out_int.write("#no comment");
				 out_int.write(System.getProperty("line.separator"));
			 }
			 
			 for(int x = 0 ; x < sizex; x++ )
			 {
				for(int y = 0 ; y < sizey; y++ )
				{
					for(int z = 0 ; z < sizez; z++ )
					{
						appendVoxelToFile(x, y, z, out_ext, out_int);
					}
				}
			 }
	
			 if(file_ext.length()>1)
				 out_ext.flush();
			 if(file_int.length()>1)
				 out_int.flush();
 		}
	}
	
	
	
	/**
	 * Exports voxelvolume as OBJ. 
	 * This method does not write inner faces resulting in a smaller file size.
	 * @param file_ext
	 * @param file_int
	 * @throws IOException
	 */
	public void writeSmallOBJs(String file_ext, String file_int) throws IOException
	{
		
			
		count = 1;
		FileWriter out_ext = null, out_int = null;
		 if(file_ext.length()>1)
		 {
			 File fext = new File(file_ext);
			 out_ext = new FileWriter(fext);
			 out_ext.write("#no comment");
			 out_ext.write(System.getProperty("line.separator"));
		 }
		 if(file_int.length()>1)
		 {
			 File fint = new File(file_int);
			 out_int = new FileWriter(fint);
			 out_int.write("#no comment");
			 out_int.write(System.getProperty("line.separator"));
		 }
		 
		 for(int x = 0 ; x < sizex; x++ )
		 {
			for(int y = 0 ; y < sizey; y++ )
			{
				for(int z = 0 ; z < sizez; z++ )
				{
					appendConditionalVoxel(x, y, z, out_ext, out_int);
					
				}
			}
		 }
		 if(file_ext.length()>1)
			 out_ext.flush();
		 if(file_int.length()>1)
			 out_int.flush();  
	}
	
}
