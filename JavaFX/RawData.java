package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RawData {

    public int timePoints;
    public int binsNumber;
    public double scaleFactor;
    public double[][] massChromatogram;
    public double[][] massSpectra;


    public RawData(File processFile) {

        try {

            Scanner input = new Scanner(processFile); // input reads a .BND file

            String dataLine;

            // Searches the file for the timePoints value
            boolean timePointsFound = false;
            while (input.hasNext() && !timePointsFound) {

                dataLine = input.nextLine();

                if (dataLine.toLowerCase().contains(("Time Points").toLowerCase())) {
                    dataLine = dataLine.replaceAll("[^0-9]", "");
                    this.timePoints = Integer.parseInt(dataLine);
                    timePointsFound = true;
                }
            }

            // Searches the file for the binsNumber value
            boolean binsNumberFound = false;
            while (input.hasNext() && !binsNumberFound) {

                dataLine = input.nextLine();

                if (dataLine.toLowerCase().contains(("Bins Number").toLowerCase())) {
                    dataLine = dataLine.replaceAll("[^0-9]", "");
                    this.binsNumber = Integer.parseInt(dataLine);
                    binsNumberFound = true;
                }
            }

            // Creates two arrays for raw data
            this.massChromatogram = new double[this.timePoints][2];
            this.massSpectra = new double[this.binsNumber][this.timePoints + 1];
            int counter = 0;

            // Stores raw data and finds the scale factor
            while (input.hasNext()) {

                dataLine = input.nextLine();

                // Scale Factor
                if (dataLine.toLowerCase().contains(("Scale factor").toLowerCase())) {
                    dataLine = dataLine.replaceAll("[^0-9]", "");
                    this.scaleFactor = Double.parseDouble(dataLine);

                }

                // TIC Intensity
                if (dataLine.toLowerCase().contains(("TIC intensity").toLowerCase())) {
                    dataLine = dataLine.substring(dataLine.indexOf("[") + 1);
                    dataLine = dataLine.replaceAll(",", " ").replaceAll("]", " ");

                    String[] ticData = dataLine.split(" ");

                    for (int i = 0; i < ticData.length; i++) {
                        this.massChromatogram[i][1] = Double.parseDouble(ticData[i]);
                    }
                }

                // Retention Time
                if (dataLine.toLowerCase().contains(("Time Block").toLowerCase())) {
                    dataLine = dataLine.substring(dataLine.indexOf("]") + 1);
                    this.massChromatogram[counter][0] = Double.parseDouble(dataLine);
                }

                // Mass Spectra Data
                if (dataLine.toLowerCase().contains(("Mass Intensity").toLowerCase())) {

                    dataLine = dataLine.substring(dataLine.indexOf("[") + 1);
                    dataLine = dataLine.replaceAll(",", " ").replaceAll("]", " ");

                    String[] binData = dataLine.split(" ");

                    if (counter == 0) {

                        for (int i = 0; i < binData.length; i++) {

                            if (i % 2 == 0) {
                                this.massSpectra[i/2][0] = Double.parseDouble(binData[i]); // m/z values
                            } else {
                                this.massSpectra[i/2][1] = Double.parseDouble(binData[i]);
                            }
                        }

                        counter++;

                    } else {

                        for (int i = 0; i < binData.length; i++) {

                            if (i % 2 != 0) {
                                this.massSpectra[i/2][counter+1] = Double.parseDouble(binData[i]);
                            }
                        }

                        counter++;
                    }
                }
            }

            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
