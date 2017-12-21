package specViewer;

/* MSChartCreator.Java
 * 
 * This class creates JFreeCharts (Charts) and includes mathematical methods for the same. 
 * This requires jfreechart & flanagan JARs.
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

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import flanagan.analysis.CurveSmooth;

// The Savitzky-Golay Filter methods in MassSpecProcess.applySavitzkyGolayFilter() 
// are thanks to Dr. Michael Thomas Flanagan's Java Scientific Library at www.ee.ucl.ac.uk/~mflanaga

public class MSChartCreator {
	
	String title; // Chart title
	int binsNumber; // number of samples per time block
	float[][] mass; // stores mass samples per time block
	double[][] intensity; // stores intensity samples per time block
	int time; // time block that wants to be graphed
	float[] avgMass; // average mass per time block
	double[] avgIntensity; // average intensity per time block
	double[] timeArray; // times per time block
	int avgSmoother; // indicates if it is a time block average that removes the first average (an outlier)
	double[] sgSmooth; // updated y-values for the Savitzky-Golay filter
	int filterWidth; // filter width for the Savitzky-Golay filter
	boolean smooth; // indicates if a Savitzky-Golay filter is applied
	
	// This Constructor indicates a Chart for a particular time block.
	public MSChartCreator(String myTitle, int myBinsNumber, float[][] myMass, double[][] myIntensity, int myTime) {
		title = myTitle;
		binsNumber = myBinsNumber;
		mass = myMass;
		intensity = myIntensity;
		time = myTime;
	}
	
	// This Constructor indicates a Chart for the average of each time block.
	public MSChartCreator(String myTitle, float[] myAvgMass, double[] myAvgIntensity, double[] myTimeArray, int myAvgSmoother) {
		title = myTitle;
		binsNumber = 0;
		avgMass = myAvgMass;
		avgIntensity = myAvgIntensity;
		timeArray = myTimeArray;
		avgSmoother = myAvgSmoother;
	}
	
	// This method returns an XYDataset for a .BND file for Chart creation and functions.
	private XYDataset createDataset() {
		
		// An XYSeries is created to display the mass spectrometry data.
		XYSeries massSpecSeries = new XYSeries(this.title);
		
		// Adds data for the per time block Chart.
		if (this.binsNumber != 0) {
			for (int i = 0; i < this.binsNumber; i++) {
				massSpecSeries.add(this.mass[this.time][i], this.intensity[this.time][i]);
			}
		} else { // Adds data for the average Chart.
			
			if (this.avgSmoother == -2) { // Adds data for the smoothed average Chart
				for (int i = 1; i < this.timeArray.length; i++) {
					massSpecSeries.add(this.timeArray[i], this.avgIntensity[i]);
				}
			} else { // Adds data for the unsmoothed average Chart
				for (int i = 0; i < this.avgMass.length; i++) {
					massSpecSeries.add(this.timeArray[i], this.avgIntensity[i]);
				}
			}
		}
		
		// Populates a series collection with the relevant series. This method can allow for adding another Series more easily in the future.
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(massSpecSeries);
		
		return dataset;	
	}
	
	// This method returns an XYDataset for a Savitzky-Golay transformation for Chart creation and functions.
	private XYDataset createDataset(double[] sgSmoothing) {
		
		// Changes title to return the filter
		this.smooth = true;
		this.title = (this.title + " w/ Savitzky-Golay Filter (" + this.filterWidth + ")");
		
		XYSeries massSpecSeries = new XYSeries(this.title);
		
		if (this.binsNumber != 0) {
			for (int i = 0; i < this.binsNumber; i++) {
				massSpecSeries.add(this.mass[this.time][i], this.sgSmooth[i]);
			}
		} else {
			
			if (this.avgSmoother == -2) {
				for (int i = 1; i < this.timeArray.length; i++) {
					massSpecSeries.add(this.timeArray[i], this.sgSmooth[i]);
				}
			} else {
				for (int i = 0; i < this.avgMass.length; i++) {
					massSpecSeries.add(this.timeArray[i], this.sgSmooth[i]);
				}
			}
			
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(massSpecSeries);
		
		return dataset;	
	}

	// Creates a Chart and the ChartPanel which the Chart is added to for the JFrame.
	public ChartPanel createChart() {
		
		XYDataset dataset;
		
		// Differentiates which dataset creation method is used.
		if (this.smooth == true) {
			dataset = this.createDataset(this.sgSmooth);
		} else {
			dataset = this.createDataset();
		}
		
		String xAxisLabel;
		
		// Differentiates whether it is an average dataset or not.
		if (this.binsNumber != 0) {
			xAxisLabel = "m/z";
		} else {
			xAxisLabel = "Elapsed Time (s)";
		}
					
		JFreeChart chart = ChartFactory.createXYLineChart(
				this.title, // title
				xAxisLabel,
				"Intensity (cps)", // yAxisLabel
				dataset, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend
				true, // tooltips
				false); // urls
		chart.setBackgroundPaint(Color.white);
	
		// Creates shapes and labels for data points.
		// I preferred labels to tooltips so that it is explicit, at the cost of being visually busy.
		
		// This is thanks to XYLineAndShapeRendererDemo.java
		// (C) Copyright 2004, by Object Refinery Limited and Contributors.
		// by David Gilbert (for Object Refinery Limited).
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		NumberFormat decimal = new DecimalFormat("####.###########"); 
		String formatString = "({1}, {2})";
		StandardXYItemLabelGenerator generator = new StandardXYItemLabelGenerator(formatString, decimal, decimal);
		renderer.setSeriesItemLabelGenerator(0, generator);
		renderer.setSeriesItemLabelsVisible(0, true);
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesItemLabelsVisible(0, true);
        	plot.setRenderer(renderer);

		ChartPanel panel = new ChartPanel(
				chart,
				true, // properties
				true, // save
				true, // print
				true, // zoom
				true // toooltips
				);
		panel.setDisplayToolTips(true); 
		return panel;
	}
	
	// Integrates under the entire XYSeries.
	public double integrate() {
		
		XYDataset dataset;
		
		// Differentiates whether a Savitzky-Golay filter is applied or not.
		if (this.smooth == true) {
			dataset = this.createDataset(this.sgSmooth);
		} else {
			dataset = this.createDataset();
		}

		double integratedArea = 0;
		
		// This implements the Trapezoid Rule.
		for (int i = 0; i < dataset.getItemCount(0) - 1; i++) {
			double height = Math.abs(dataset.getXValue(0, i) - dataset.getXValue(0, i+1));
			double base = dataset.getYValue(0, i) + dataset.getYValue(0, i+1);
			integratedArea += 0.5 * height * base;	
		}
		
		return integratedArea;
	}
	
	// This method applies a Savitzky-Golay Filter based on Michael Thomas Flanagan's JAR.
	public ChartPanel applySavitzkyGolayFilter() {
		
		// The user is prompted for what they would like their filter width to be.
		String myFilterWidthString = JOptionPane.showInputDialog("What filter width would you like?");
		int myFilterWidth = Integer.parseInt(myFilterWidthString);
		
		// This Constructor can implement smoothing transformations.
		CurveSmooth csm;
		double[] sgMass;
		double[] sgIntensity;
		
		this.filterWidth = myFilterWidth;
		
		if (this.binsNumber == 0) { // Creates a csm for average Charts.
			csm = new CurveSmooth(timeArray, avgIntensity);
		} else { // Creates a csm for per time block Charts.
			sgMass = new double [mass[0].length];
			sgIntensity = new double [mass[0].length];
			
			for(int i = 0; i < mass[0].length; i++) {
				sgMass[i] = (double) mass[this.time][i];
				sgIntensity[i] = intensity[this.time][i];	
			}
			csm = new CurveSmooth(sgMass, sgIntensity);
		}
		
		this.sgSmooth = csm.savitzkyGolay(this.filterWidth); // A one-dimensional array of transformed y-values. 
		this.smooth = true;								    // the savitzkyGolay method only accepts two one dimensional arrays of the same type.
		ChartPanel sgPlot = this.createChart();
		return sgPlot;
	}
}
