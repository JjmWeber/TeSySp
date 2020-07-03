package fr.uha.tesysp.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class FindHMTThreshold {
    private int threshold;
    private double[] histogram;

    public FindHMTThreshold(double[] histogram) {
        this.threshold = 0;
        this.histogram = histogram;

        computeThreshold();
    }

    public void computeThreshold() {
        histogram[0] = 0; //eliminate the peak of black pixels

        /*
         * Find the maximum value of the histogram
         */

        int max = 0;
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[max] < histogram[i]) {
                max = i;
            }
        }

        /*
         * Estimation of sigma value using the full width at half maximum value
         * Approximate Width
         */

        int leftMin = 0;
        int leftMax = 0;
        int rightMin = 0;
        int rightMax = 0;

        for (int i = 0; i < max; i++) {
            if (histogram[i] > histogram[max] / 2) {
                leftMin = i - 1;
                leftMax = i;
                break;
            }
        }

        for (int i = max; i < histogram.length; i++) {
            if (histogram[i] < histogram[max] / 2) {
                rightMin = i;
                rightMax = i - 1;
                break;
            }
        }

        /*
         * Approximate sigma
         */
        double sigmaMin = (rightMax - leftMax) / 2.3548;
        double sigmaMax = (rightMin - leftMin) / 2.3548;
        int sigma = (int) (Math.round((sigmaMax + sigmaMin) / 2));

        threshold = max + 3 * sigma;
    }

    public int getThreshold() {
        return threshold;
    }

    public void saveHistogram(String path) {
        FileWriter fw;
        try {
            fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            double[] cumulHist = new double[256];

            for (int i = 1; i < histogram.length; i++) {
                cumulHist[i] += cumulHist[i - 1] + histogram[i];
                bw.write(histogram[i] + ";" + cumulHist[i] + "\n");
            }

            bw.newLine();
            bw.newLine();
            bw.close();
            fw.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public double[] getHistogram() {
        return histogram;
    }

    public void setHistogram(double[] histogram) {
        this.histogram = histogram;

        computeThreshold();
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
