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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for logging information to file.
 * @author Lukas Liebel
 *
 */
public class Logger {
	
	File outputfile;
	
	public Logger(File outputfile) {
		this.outputfile = outputfile;
	}
	
	public void write(String msg){
		write(msg, true, true, true);
	}
	
	public void write(String msg, boolean timestamp, boolean toFile, boolean toConsole) {
		
		if (toConsole){
			System.out.println(timestampMessage(msg));
		}
		
		if (toFile) {
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfile, true)))) {
			    
				if (timestamp) {
					out.println(timestampMessage(msg));
				} else {
					out.println(msg);
				}
				
			}catch (IOException e) {
			    //exception handling left as an exercise for the reader
				System.out.println(timestampMessage("Could not write to file"));
			}
		}
	}
	
	private String timestampMessage(String msg) {
		SimpleDateFormat df = new SimpleDateFormat("[HH:mm:ss] ");
		return(df.format(new Date()) + msg);
	}

	// Deletes existing file and creates a new one. If a String argument is passed, it will be written as the first line (e.g. a CSV column header)
	public void init() throws IOException {
		outputfile.delete();
		outputfile.createNewFile();
	}
	
	public void init(String header) throws IOException {
		init();
		write(header, false, true, false);
	}
}
