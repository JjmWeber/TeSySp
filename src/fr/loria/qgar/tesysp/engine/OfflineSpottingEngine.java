package fr.loria.qgar.tesysp.engine;

import com.jfoenix.controls.JFXProgressBar;
import fr.loria.qgar.tesysp.data.OfflineSpottingResult;
import fr.loria.qgar.tesysp.data.SettingsData;
import fr.loria.qgar.tesysp.data.SpottedPoint;
import fr.loria.qgar.tesysp.util.Constants;
import fr.loria.qgar.tesysp.util.Message;
import fr.loria.qgar.tesysp.util.Tool;
import fr.unistra.pelican.*;
import fr.unistra.pelican.algorithms.geometric.Rotation2D;
import fr.unistra.pelican.algorithms.geometric.VerticalAxialSymmetry2D;
import fr.unistra.pelican.algorithms.histogram.Histogram;
import fr.unistra.pelican.algorithms.segmentation.ManualThresholding;
import fr.unistra.pelican.algorithms.segmentation.flatzones.BooleanConnectedComponentsLabeling;
import fr.unistra.pelican.util.Pixel;
import fr.unistra.pelican.util.Point;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Arrays;

public class OfflineSpottingEngine {

    private static int[] pixelCount;
    private static int nbPixelsToCompute;
    private static long startTime;
    private static JFXProgressBar progressBar;
    private static Label labelRemainingTime;

    private static void updateProgressBarSpotting() {

        double nbPixelsComputed = 0;
        for (int i = 0; i < pixelCount.length; i++) {
            nbPixelsComputed += pixelCount[i];
        }

        double value = nbPixelsComputed / nbPixelsToCompute;

        progressBar.setProgress(value);
        //jpbSpotting.setString(Math.round(jpbSpotting.getPercentComplete() * 100) + " %");
        if (nbPixelsComputed == nbPixelsToCompute) {
            progressBar.setProgress(0);
        }

        if (nbPixelsComputed != 0) {
            long currentDuration = System.currentTimeMillis() - startTime;
            long remainingDuration = (long) (currentDuration * ((double) nbPixelsToCompute / nbPixelsComputed) - currentDuration);

            Platform.runLater(() -> labelRemainingTime.setText("[SPOTTING] Remaining time : " + Tool.getHMSFromMS(remainingDuration)));

        }
    }

    private static void updateProgressBarFiltering(int value) {


        progressBar.setProgress((double) value / 100);

        //jpbFiltering.setString(Math.round(jpbFiltering.getPercentComplete() * 100) + " %");

        if (value == 100) {
            //this.dispose();
            progressBar.setProgress(0);
            Platform.runLater(() -> labelRemainingTime.setText("Spotting and filtering finished !"));
        } else if (value != 0) {
            long currentDuration = System.currentTimeMillis() - startTime;
            long remainingDuration = (long) (currentDuration * ((double) 100 / value) - currentDuration);
            Platform.runLater(() -> labelRemainingTime.setText("[FILTERING] Remaining time : " + Tool.getHMSFromMS(remainingDuration)));
        }


    }

    protected static OfflineSpottingResult launchOfflineSpotting(ByteImage plan, ByteImage symbol, int mode,
                                                                 SettingsData settings, boolean returnHMT, boolean autoThreshold,
                                                                 JFXProgressBar progressBar, Label labelRemainingTime, String documentPath, String symbolPath) {
        System.out.println(Message.LAUNCHOFFSPOTTING);
        long startTime = System.currentTimeMillis();
        final int xDim = plan.xdim;
        final int yDim = plan.ydim;
        symbol.setCenter(new Point(symbol.xdim / 2, symbol.ydim / 2));

        final DoubleImage hMT = plan.newDoubleImage();
        hMT.fill(0.);
        final ByteImage bestSymbolConfigurationImage = plan.newByteImage();
        bestSymbolConfigurationImage.fill(0);

        //Computational time measure
        long spottingTime = 0;
        long filteringTime = 0;

        //Compute Otsu Threshold
        settings.setFGBGThresh((int) getOtsuThreshold(symbol));

        //Compute integral image to speed-up hmt computation
        final IntegralImage integral = new IntegralImage(plan);
        final long symbolPixelSum = symbol.getSum();

        final ArrayList<ByteImage> symbolConfigurations = new ArrayList<>();
        final ArrayList<Double> symbolAngularConfiguration = new ArrayList<>();
        final ArrayList<Boolean> symbolSymmetryConfiguration = new ArrayList<>();
        symbolConfigurations.add(symbol);
        symbolAngularConfiguration.add(0.);
        symbolSymmetryConfiguration.add(false);
        if (settings.isSymmetry()) {
            symbolConfigurations.add(VerticalAxialSymmetry2D.exec(symbol));
            symbolAngularConfiguration.add(0.);
            symbolSymmetryConfiguration.add(true);
        }
        //Set all the orientation
        if (settings.getAngularStep() > 0 && settings.getAngularStep() < 360) {
            for (int i = 1; i * settings.getAngularStep() < 360; i++) {
                double degree = i * settings.getAngularStep();
                symbolConfigurations.add(Rotation2D.exec(symbol, degree, Rotation2D.NOINTERPOLATION));
                symbolAngularConfiguration.add(degree);
                symbolSymmetryConfiguration.add(false);
                if (settings.isSymmetry()) {
                    symbolConfigurations.add(Rotation2D.exec(VerticalAxialSymmetry2D.exec(symbol), degree, Rotation2D.NOINTERPOLATION));
                    symbolAngularConfiguration.add(degree);
                    symbolSymmetryConfiguration.add(true);
                }
            }
        }

        //Launching thread for computation
        Thread[] threads = new Thread[settings.getNbThreads()];
        final int step = (yDim / settings.getNbThreads()) + 1;
        int threadCount = 0;

        final int[] pixelCount = new int[settings.getNbThreads()];
        Arrays.fill(pixelCount, 0);

        // Compute number of computed pixels by the spotting
        int nbPixelsToComputeTemp = 0;
        for (int i = 0; i < symbolConfigurations.size(); i++) {
            Image currentConfiguration = symbolConfigurations.get(i);
            nbPixelsToComputeTemp += (xDim - currentConfiguration.xdim / 2 - currentConfiguration.xdim / 2) * (yDim - currentConfiguration.ydim / 2 - currentConfiguration.ydim / 2);
        }
        final int nbPixelsToCompute = nbPixelsToComputeTemp;

        final ArrayList<ArrayList<SpottedPoint>> spottedPoints = new ArrayList<ArrayList<SpottedPoint>>(100);
        ByteImage bHMT = new ByteImage(plan.xdim, plan.ydim, 1, 1, 1);
        bHMT.fill(0);
        int threshold = -1;

        //If the image is not empty
        if (nbPixelsToComputeTemp > 0) {

            // Initializing progress frame (set to null in Console Mode for compatibility purpose)

            //launch symbol spotting
            for (int itmp = 0; itmp < yDim; itmp += step, threadCount++) {
                final int i = itmp;
                final int threadId = threadCount;

                OfflineSpottingEngine.labelRemainingTime = labelRemainingTime;
                OfflineSpottingEngine.progressBar = progressBar;
                OfflineSpottingEngine.startTime = startTime;

                threads[threadCount] = new Thread(() -> {
                    for (int symbolConfiguration = 0; symbolConfiguration < symbolConfigurations.size(); symbolConfiguration++) {
                        Image currentConfiguration = symbolConfigurations.get(symbolConfiguration);
                        int yMin = Math.max(currentConfiguration.ydim / 2, i);
                        int yMax = Math.min(yDim - currentConfiguration.ydim / 2, i + step);
                        for (int y = yMin; y < yMax; y++) {
                            for (int x = currentConfiguration.xdim / 2; x < xDim - currentConfiguration.xdim / 2; x++) {
                                //if sum of pixels under the template is less than half of the template pixel sum do not compute hmt
                                //TODO : Decide if half is fixed or must be a parameter
                                if (integral.area(x - currentConfiguration.xdim / 2, y - currentConfiguration.ydim / 2, currentConfiguration.xdim, currentConfiguration.ydim) >= symbolPixelSum * 0.5) {
                                    double HMTValue;
                                    if (!(settings.isOnlyBG() || settings.isOnlyFG())) {
                                        HMTValue = getHMTValue(x, y, plan, currentConfiguration, settings);
                                    } else if (settings.isOnlyFG()) {
                                        HMTValue = getHMTValueOnlyFG(x, y, plan, currentConfiguration, settings);
                                    } else {
                                        HMTValue = getHMTValueOnlyBG(x, y, plan, currentConfiguration, settings);
                                    }
                                    if (HMTValue > hMT.getPixelXYDouble(x, y)) {
                                        hMT.setPixelXYDouble(x, y, HMTValue);
                                        bestSymbolConfigurationImage.setPixelXYByte(x, y, symbolConfiguration);
                                    }
                                }
                                pixelCount[threadId]++;
                            }
                            if (mode == Constants.GRAPHICALMODE) {
                                OfflineSpottingEngine.pixelCount = pixelCount;
                                OfflineSpottingEngine.nbPixelsToCompute = nbPixelsToCompute;
                                updateProgressBarSpotting();
                            }
                        }
                    }
                    if (mode == Constants.GRAPHICALMODE) {
                        OfflineSpottingEngine.pixelCount = pixelCount;
                        OfflineSpottingEngine.nbPixelsToCompute = nbPixelsToCompute;
                        updateProgressBarSpotting();
                    }
                });

            }


            if (mode == Constants.GRAPHICALMODE) {
                OfflineSpottingEngine.startTime = System.currentTimeMillis();
            }

            spottingTime = System.currentTimeMillis();
            //launch spotting threads
            for (int i = 0; i < settings.getNbThreads(); i++) {
                threads[i].start();
            }

            //wait end of spotting threads
            for (int i = 0; i < settings.getNbThreads(); i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            spottingTime = System.currentTimeMillis() - spottingTime;

            System.out.println(Message.SPOTTINGOKSTARTFILTERING);

            for (int x = 0; x < hMT.xdim; x++) {
                for (int y = 0; y < hMT.ydim; y++) {
                    bHMT.setPixelXYByte(x, y, (int) (hMT.getPixelXYDouble(x, y) * 100));
                }
            }

            if (autoThreshold) {
                double[] hist = Histogram.exec(bHMT);
                FindHMTThreshold find = new FindHMTThreshold(hist);
                threshold = find.getThreshold();
                BooleanImage filtered = ManualThresholding.exec(bHMT, threshold);
                CompareAmplitudes compare = new CompareAmplitudes(bHMT, filtered);
                final ArrayList<Pixel> pixels = compare.getFilteredPoints();
                final ArrayList<Integer> values = compare.getAmplitudeDifferences();
                find.setHistogram(compare.getHist());
                threshold = find.getThreshold() + 1;
                System.out.println(threshold);


                //filtered to avoiding close points
                for (int i = 0; i < pixels.size(); i++) {

                    Pixel pi = pixels.get(i);
                    int ix = pi.x;
                    int iy = pi.y;
                    int iorientation = bestSymbolConfigurationImage.getPixelByte(pi);
                    int ixdimD2 = symbolConfigurations.get(iorientation).xdim / 2;
                    int iydimD2 = symbolConfigurations.get(iorientation).ydim / 2;
                    for (int j = i + 1; j < pixels.size(); j++) {
                        Pixel pj = pixels.get(j);
                        int jx = pj.x;
                        int jy = pj.y;
                        int jorientation = bestSymbolConfigurationImage.getPixelByte(pj);
                        int jxdimD2 = symbolConfigurations.get(jorientation).xdim / 2;
                        int jydimD2 = symbolConfigurations.get(jorientation).ydim / 2;
                        if ((ix >= jx - jxdimD2 && ix <= jx + jxdimD2 && iy >= jy - jydimD2 && iy <= jy + jydimD2) || (jx >= ix - ixdimD2 && jx <= ix + ixdimD2 && jy >= iy - iydimD2 && jy <= iy + iydimD2)) {
                            if (values.get(i) > values.get(j)) {
                                values.set(j, 0);
                            } else {
                                values.set(i, 0);
                            }
                        }
                    }
                }

                Thread t = new Thread(() -> {
                    for (int thresh = 1; thresh <= 100; thresh++) {
                        ArrayList<SpottedPoint> sPoints = new ArrayList<>();
                        for (int i = 0; i < pixels.size(); i++) {
                            if (values.get(i) > thresh) {
                                Pixel p = pixels.get(i);
                                int bestSymbolConfiguration = bestSymbolConfigurationImage.getPixelXYByte(p.x, p.y);
                                sPoints.add(new SpottedPoint(p.x, p.y, bestSymbolConfiguration, symbolAngularConfiguration.get(bestSymbolConfiguration), symbolSymmetryConfiguration.get(bestSymbolConfiguration)));
                            }
                        }
                        spottedPoints.add(sPoints);
                        updateProgressBarFiltering(thresh);
                    }
                });
                if (mode == Constants.GRAPHICALMODE)
                    OfflineSpottingEngine.startTime = System.currentTimeMillis();
                filteringTime = System.currentTimeMillis();

                t.start();

                try {
                    t.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {

                Thread t = new Thread(() -> {
                    //spotting thresholding and filtering
                    for (int thresh = 1; thresh <= 100; thresh++) {
                        ArrayList<SpottedPoint> sPoints = new ArrayList<SpottedPoint>();
                        IntegerImage cc = BooleanConnectedComponentsLabeling.exec(ManualThresholding.exec(hMT, ((double) thresh) / 100));
                        int maxLabel = cc.maximumInt();
                        double[] maxValues = new double[maxLabel + 1];
                        Point[] extremums = new Point[maxLabel + 1];
                        Arrays.fill(maxValues, 0.);
                        for (int y = 0; y < yDim; y++)
                            for (int x = 0; x < xDim; x++) {
                                if (cc.getPixelXYInt(x, y) > 0) {
                                    if (hMT.getPixelXYDouble(x, y) > maxValues[cc.getPixelXYInt(x, y)]) {
                                        maxValues[cc.getPixelXYInt(x, y)] = hMT.getPixelXYDouble(x, y);
                                        extremums[cc.getPixelXYInt(x, y)] = new Point(x, y);
                                    }
                                }
                            }
                        //point filtering avoid close point
                        for (int i = 0; i < extremums.length; i++) {
                            for (int j = i + 1; j < extremums.length; j++) {
                                if (extremums[i] != null && extremums[j] != null) {
                                    int ix = extremums[i].x;
                                    int iy = extremums[i].y;
                                    int iorientation = bestSymbolConfigurationImage.getPixelXYByte(ix, iy);
                                    int ixdimD2 = symbolConfigurations.get(iorientation).xdim / 2;
                                    int iydimD2 = symbolConfigurations.get(iorientation).ydim / 2;
                                    int jx = extremums[j].x;
                                    int jy = extremums[j].y;
                                    int jorientation = bestSymbolConfigurationImage.getPixelXYByte(jx, jy);
                                    int jxdimD2 = symbolConfigurations.get(jorientation).xdim / 2;
                                    int jydimD2 = symbolConfigurations.get(jorientation).ydim / 2;
                                    if ((ix >= jx - jxdimD2 && ix <= jx + jxdimD2 && iy >= jy - jydimD2 && iy <= jy + jydimD2) || (jx >= ix - ixdimD2 && jx <= ix + ixdimD2 && jy >= iy - iydimD2 && jy <= iy + iydimD2)) {
                                        if (maxValues[i] > maxValues[j]) {
                                            extremums[j] = null;
                                        } else {
                                            extremums[i] = null;
                                        }
                                    }
                                }
                            }
                        }
                        for (Point p : extremums) {
                            if (p != null) {
                                int bestSymbolConfiguration = bestSymbolConfigurationImage.getPixelXYByte(p.x, p.y);
                                sPoints.add(new SpottedPoint(p.x, p.y, bestSymbolConfiguration, symbolAngularConfiguration.get(bestSymbolConfiguration), symbolSymmetryConfiguration.get(bestSymbolConfiguration)));
                            }
                        }
                        spottedPoints.add(sPoints);
                        if (mode == Constants.GRAPHICALMODE) {
                            updateProgressBarFiltering(thresh);
                        }
                    }
                });

                if (mode == Constants.GRAPHICALMODE) {
                    //ospf.setStartTime(System.currentTimeMillis());
                    OfflineSpottingEngine.startTime = System.currentTimeMillis();
                }
                filteringTime = System.currentTimeMillis();

                t.start();

                try {
                    t.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            filteringTime = System.currentTimeMillis() - filteringTime;
            System.out.println(Message.FILTERINGOK);
            System.out.println("Spotting time : " + spottingTime + "ms");
            System.out.println("Filtering time : " + filteringTime + "ms");

        }

        System.out.println(Message.DONEOFFSPOTTING);

        if (returnHMT) {
            return new OfflineSpottingResult(bHMT, spottedPoints, threshold, symbolConfigurations, symbolAngularConfiguration, symbolSymmetryConfiguration, startTime, spottingTime, filteringTime, documentPath, symbolPath);
        }
        return new OfflineSpottingResult(null, spottedPoints, threshold, symbolConfigurations, symbolAngularConfiguration, symbolSymmetryConfiguration, startTime, spottingTime, filteringTime, documentPath, symbolPath);

    }

    private static double getHMTValue(int xInput, int yInput, Image plan, Image currentRequest, SettingsData settings) {
        double HMTValue = 0;
        int xDim = currentRequest.xdim;
        int yDim = currentRequest.ydim;
        int xRef = xInput - currentRequest.xdim / 2;
        int yRef = yInput - currentRequest.ydim / 2;
        double HMTValueFG = 0;
        double HMTValueBG = 0;
        int countFG = 0;
        int countBG = 0;
        for (int y = 0; y < yDim; y++)
            for (int x = 0; x < xDim; x++) {
                int locX = xRef + x;
                int locY = yRef + y;
                if (locX >= 0 && locX < plan.xdim && locY >= 0 && locY < plan.ydim) {
                    if (currentRequest.getPixelXYByte(x, y) >= settings.getFGBGThresh())    //FG
                    {
                        if (plan.getPixelXYByte(locX, locY) >= currentRequest.getPixelXYByte(x, y)) {
                            HMTValueFG++;
                        } else {
                            HMTValueFG += plan.getPixelXYByte(locX, locY) / (double) currentRequest.getPixelXYByte(x, y);
                        }
                        countFG++;
                    } else                                                        //BG
                    {
                        if (plan.getPixelXYByte(locX, locY) <= currentRequest.getPixelXYByte(x, y)) {
                            HMTValueBG++;
                        } else {
                            HMTValueBG += 1 - (plan.getPixelXYByte(locX, locY) - currentRequest.getPixelXYByte(x, y)) / (double) (255 - currentRequest.getPixelXYByte(x, y));
                        }
                        countBG++;
                    }
                }
            }
        HMTValueFG /= countFG;
        HMTValueBG /= countBG;
        HMTValue = (Math.pow(HMTValueFG, settings.getAlpha()) + Math.pow(HMTValueBG, settings.getBeta())) / 2;
        return HMTValue;
    }

    private static double getHMTValueOnlyFG(int xInput, int yInput, Image plan, Image currentRequest, SettingsData
            settings) {
        int xDim = currentRequest.xdim;
        int yDim = currentRequest.ydim;
        int xRef = xInput - currentRequest.xdim / 2;
        int yRef = yInput - currentRequest.ydim / 2;
        double HMTValueFG = 0;
        int countFG = 0;
        for (int y = 0; y < yDim; y++)
            for (int x = 0; x < xDim; x++) {
                if (currentRequest.getPixelXYByte(x, y) >= settings.getFGBGThresh())    //FG
                {
                    int locX = xRef + x;
                    int locY = yRef + y;
                    if (locX >= 0 && locX < plan.xdim && locY >= 0 && locY < plan.ydim) {
                        if (plan.getPixelXYByte(locX, locY) >= currentRequest.getPixelXYByte(x, y)) {
                            HMTValueFG++;
                        } else {
                            HMTValueFG += plan.getPixelXYByte(locX, locY) / (double) currentRequest.getPixelXYByte(x, y);
                        }
                        countFG++;
                    }
                }
            }
        return HMTValueFG / countFG;
    }

    private static double getHMTValueOnlyBG(int xInput, int yInput, Image plan, Image currentRequest, SettingsData
            settings) {
        int xDim = currentRequest.xdim;
        int yDim = currentRequest.ydim;
        int xRef = xInput - currentRequest.xdim / 2;
        int yRef = yInput - currentRequest.ydim / 2;
        double HMTValueBG = 0;
        int countBG = 0;
        for (int y = 0; y < yDim; y++)
            for (int x = 0; x < xDim; x++) {
                if (currentRequest.getPixelXYByte(x, y) < settings.getFGBGThresh())                                //BG
                {
                    int locX = xRef + x;
                    int locY = yRef + y;
                    if (locX >= 0 && locX < plan.xdim && locY >= 0 && locY < plan.ydim) {
                        if (plan.getPixelXYByte(locX, locY) <= currentRequest.getPixelXYByte(x, y)) {
                            HMTValueBG++;
                        } else {
                            HMTValueBG += 1 - (plan.getPixelXYByte(locX, locY) - currentRequest.getPixelXYByte(x, y)) / (double) (255 - currentRequest.getPixelXYByte(x, y));
                        }
                        countBG++;
                    }
                }
            }
        return HMTValueBG / countBG;

    }

    //Otsu thresholding, according to OtsuThresholding class in fr.unistra.pelican.algorithms.segmentation
    private static double getOtsuThreshold(Image currentRequest) {

        double[] variances = new double[254];
        Arrays.fill(variances, 0.0);

        double[] histo = Histogram.exec(currentRequest, false);

        // for every threshold compute the interclass variances
        for (int i = 1; i < 255; i++) {

            // number of pixels in each class
            double n1 = 0.0;
            double n2 = 0.0;

            // mean of each class
            double mean1 = 0.0;
            double mean2 = 0.0;

            for (int j = 0; j < i; j++) {
                n1 += histo[j];
                mean1 += histo[j] * j;
            }

            for (int j = i; j < 256; j++) {
                n2 += histo[j];
                mean2 += histo[j] * j;
            }

            if (n1 == 0 || n2 == 0)
                continue;

            mean1 = mean1 / n1;
            mean2 = mean2 / n2;

            // inner class variance..the official and the simplified versions
            // variances[i - 1] = n1 * (mean1 - mean) * (mean1 - mean) + n2 *
            // (mean2 - mean) * (mean2 - mean);
            variances[i - 1] = n1 * n2 * (mean1 - mean2) * (mean1 - mean2);
        }

        // get the maximum
        int max = 0;
        for (int i = 1; i < variances.length; i++) {
            if (variances[i] > variances[max])
                max = i;
        }
        System.out.println(max + 1);
        return max + 1;

    }

}
