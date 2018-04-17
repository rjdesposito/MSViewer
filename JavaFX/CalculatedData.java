package sample;

import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import flanagan.analysis.CurveSmooth;

public class CalculatedData {

    double[] basePeak;
    double integrationValue;
    double[] loessSmooth;
    double[] sgSmooth;

    public CalculatedData(RawData rawData) {

        // Base Peak Chromatogram
        this.basePeak = new double[rawData.massChromatogram.length];

        for (int i = 0; i < basePeak.length; i++) {

            for (int j = 1; j < rawData.massSpectra.length; j++) {

                if (rawData.massSpectra[i+1][j] > rawData.massSpectra[i+1][j-1]) {
                    basePeak[i] = rawData.massSpectra[i+1][j];
                }
            }
        }

    }

    void integrate (double[] x, double[] y) {

        double integrationVal= 0;

        for (int i = 0; i < x.length - 1; i++) {
            double height = Math.abs(x[i+1] - x[i]);
            integrationVal += 0.5 * height * (y[i] + y[i+1]);
        }

        this.integrationValue = integrationVal;

    }

    void setLoessSmooth(double[] x, double[] y) {

        LoessInterpolator loess = new LoessInterpolator();
        this.loessSmooth = loess.smooth(x, y);

    }

    void setSGSmooth(double[] x, double[] y, int filter) {

        CurveSmooth csm = new CurveSmooth(x, y);
        this.sgSmooth = csm.savitzkyGolay(filter);

    }



}
