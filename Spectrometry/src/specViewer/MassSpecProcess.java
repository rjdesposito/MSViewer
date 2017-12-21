package specViewer;

/* MassSpecProcess.java
 * 
 * This method process a .BND file and generates the inputs for the MSChartCreator constructors.
 * 
 */

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class MassSpecProcess {
	
	// These are the same values from the MSChartCreator
	int timeBlocks;
	int binsNumber;
	double[] time;
	float[][] mass;
	double[][] intensity;
	int relevantTime;
	float[] avgMass;
	double[] avgIntensity;
	
	// Constructor
	public MassSpecProcess() {
		this.timeBlocks = 0;
		this.binsNumber = 0;
	}
	
	// This method processes the data.
	public MSChartCreator process(File processFile) {
		
		// I use a try-catch block given a Scanner.
		try {
			Scanner input = new Scanner(processFile); // This Scanner is to read a .BND file.
			
			boolean stop = false; // this is to stop searching the file for the timeBlocks value
			
			String dataLine;
			
			// Searches the file for the timeBlocks value
			while (input.hasNext() && stop == false) {
				dataLine = input.nextLine();
				
				if (dataLine.toLowerCase().contains(("Time Points").toLowerCase())) {
					dataLine = dataLine.replaceAll("[^0-9]", "");
					this.timeBlocks = Integer.parseInt(dataLine);
					stop = true;
				}	
			}
			
			stop = false;
			
			int start = 0; // This indicates the timeBlock in the intensity and mass blocks
			
			// Searches the file for the number of samples for timeBlock
			while (input.hasNext() && stop == false) {
				dataLine = input.nextLine();
				
				if (dataLine.toLowerCase().contains(("Bins Number").toLowerCase())) {
					dataLine = dataLine.replaceAll("[^0-9]", "");
					this.binsNumber = Integer.parseInt(dataLine);
					stop = true;
				}
			}
			
			// Creates arrays to store values and average Values.
			time = new double[timeBlocks];
			mass = new float[timeBlocks][binsNumber];
			intensity = new double[timeBlocks][binsNumber];
			avgMass = new float[timeBlocks];
			avgIntensity = new double[timeBlocks];
			
			// Stores time, mass, and intensity values.
			while (input.hasNext()) {
				
				dataLine = input.nextLine();
				
				// Stores time values for each timeBlock
				if (dataLine.toLowerCase().contains(("Time Block").toLowerCase())) {
					dataLine = dataLine.substring(dataLine.indexOf("]") + 1);
					time[start] = Double.parseDouble(dataLine);
				}
				
				dataLine = input.nextLine();
				
				// Stores mass and intensity values.
				if (dataLine.toLowerCase().contains(("Mass Intensity").toLowerCase())) {
					dataLine = dataLine.substring(dataLine.indexOf("[") + 1);
					dataLine = dataLine.replaceAll(",", " ").replaceAll("]", " ");
					
					String[] binData = dataLine.split(" ");

					int counter = 0;
					
					float massSum = 0; // For the average mass per time block.
					int intensitySum = 0; // For the average intensity per time block.
					
					for (int i = 0; i < binData.length; i++) {
	
						if (i%2 == 0) {
							mass[start][counter] = Float.parseFloat(binData[i]);
							massSum += mass[start][counter];
						} else {
							intensity[start][counter] = Integer.parseInt(binData[i]);
							intensitySum += intensity[start][counter];
							counter++;
						}
					}
					
					avgMass[start] = (massSum / (mass[start].length));
					avgIntensity[start] = (intensitySum / (mass[start].length));
	
					start++; // To increase the time Block.
					
				}
			}

			input.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Asks the user to make a decision regarding average and timeBlock selection.
		String whichTime = JOptionPane.showInputDialog("Which time block would you like to analyze?\n"
				+ "If you would like an average for all time blocks, type 0. If you do not want it to indicate the first value, input -1.\n"
				+ "Else, pick an index between 1 and 21: ");
		this.relevantTime = Integer.parseInt(whichTime);
		this.relevantTime = this.relevantTime - 1;
		
		// Differentiates between average and timeBlock constructors.
		if (this.relevantTime >= 0 ) {
			MSChartCreator massSpecViewer = new MSChartCreator(("Intensity vs. Mass at " + this.time[this.relevantTime] + " s"), this.binsNumber, this.mass, this.intensity, this.relevantTime);
			return massSpecViewer;
		} else {
			MSChartCreator massSpecViewer = new MSChartCreator("Avg Intensity per Time Block", this.avgMass, this.avgIntensity, this.time, this.relevantTime);
			return massSpecViewer;
		}
		
	}
	
}
