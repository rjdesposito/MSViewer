package specViewer;

/* SpectrometrySolution.java
 * 
 * This method creates a GUI to display a chart and use functions.
 * This Application used the WindowBuilder plugin.
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

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.ChartPanel;
import javax.swing.JPanel;

// The Savitzky-Golay Filter methods in MassSpecProcess.applySavitzkyGolayFilter() and executed by
// mntmSavitzkyGolayFilter are thanks to Dr. Michael Thomas Flanagan's Java Scientific Library at
// www.ee.ucl.ac.uk/~mflanaga

public class SpectrometrySolution {

	private JFrame frame;
	private JTextField txtIntegrationResult;
	private static MSChartCreator chartData;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		// For MacOS problems.
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
		} catch (Exception e) { 
				System.out.println("Error setting Java LAF: " + e); 
		}
		
		// Selects a .BND file.
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BND Files", "bnd");
		fileChooser.setFileFilter(filter);
		fileChooser.showOpenDialog(fileChooser);
		File openFile = fileChooser.getSelectedFile();
		
		// Creates a ChartPanel to input into the Application.
		MassSpecProcess specData = new MassSpecProcess();
		chartData = specData.process(openFile);
		ChartPanel myChart = chartData.createChart();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SpectrometrySolution window = new SpectrometrySolution(myChart);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SpectrometrySolution(ChartPanel chartPanel) {
		chartPanel.setMinimumSize(chartPanel.getPreferredSize());
		initialize(chartPanel);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(ChartPanel chart) {
		frame = new JFrame();
		frame.setBounds(100, 100, 1280, 800); // These dimensions are for 13.3-inch Mac.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Menu Bar for functions.
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		// Function menu.
		JMenu mnFunctions = new JMenu("Functions");
		menuBar.add(mnFunctions);
		
		// Integration menu item in the function menu.
		JMenuItem mntmIntegration = new JMenuItem("Integration");
		mnFunctions.add(mntmIntegration);
		
		// Savitzky-Golay Filter menu item in the function menu.
		JMenuItem mntmSavitzkyGolayFilter = new JMenuItem("Savitzky-Golay Filter");
		mnFunctions.add(mntmSavitzkyGolayFilter);
		
		// GridBagLayout in the JFrame.
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		// Creates JPanel for everything but the Chart.
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel); // Implements GridBagLayout in this JPanel.
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		// This clears the JTextField displaying the integration result.
		JButton btnReset = new JButton("Reset");
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.insets = new Insets(0, 0, 0, 5);
		gbc_btnReset.gridx = 0;
		gbc_btnReset.gridy = 0;
		panel.add(btnReset, gbc_btnReset);
		
		// Displays the integration result.
		txtIntegrationResult = new JTextField();
		txtIntegrationResult.setEditable(true);
		txtIntegrationResult.setText("Integration Result: ");
		GridBagConstraints gbc_txtIntegrationResult = new GridBagConstraints();
		gbc_txtIntegrationResult.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIntegrationResult.gridx = 3;
		gbc_txtIntegrationResult.gridy = 0;
		panel.add(txtIntegrationResult, gbc_txtIntegrationResult);
		txtIntegrationResult.setColumns(10);
		
		// Creates JPanel for the Chart.
		JPanel panel_1 = new JPanel();
		panel_1.add(chart);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		frame.getContentPane().add(panel_1, gbc_panel_1);
		
		// Displays the result of integration in the associated JTextField.
		mntmIntegration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String integrationResult = Double.toString(chartData.integrate());
				txtIntegrationResult.setText("Integration Result: " + integrationResult);
			}
		});
		
		// Creates a separate GUI window for a chart in a filter.
		mntmSavitzkyGolayFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String integrationResult = Double.toString(chartData.integrate());
				txtIntegrationResult.setText("Final Integration Result: " + integrationResult); // The integration result text field for the original graph 
				ChartPanel sgChartPanel = chartData.applySavitzkyGolayFilter();				  // showed the result for the Savitzky-Golay filter if that was created. 
																							  // This freezes it.
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SpectrometrySolution newWindow = new SpectrometrySolution(sgChartPanel);
							newWindow.frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		});
		
		// This clears the integration result field.
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtIntegrationResult.setText("Integration Result: ");
			}
		});
		
	}

}
