package fr.loria.qgar.tesysp.data;

import fr.unistra.pelican.ByteImage;

import java.util.ArrayList;

public class OfflineSpottingResult {
    private ArrayList<ArrayList<SpottedPoint>> spottedPoints;
    private ArrayList<ByteImage> orientedSymbols;
    private long spottingTime;
    private long filteringTime;
    private long startTime;
    private ArrayList<Double> symbolOrientationConfiguration;
    private ArrayList<Boolean> symbolSymmetryConfiguration;
    private int threshold;
    private ByteImage hmt;
    private String documentPath;
    private String symbolPath;

    public OfflineSpottingResult(ByteImage hmt, ArrayList<ArrayList<SpottedPoint>> spottedPoints, int threshold, ArrayList<ByteImage> orientedSymbols, ArrayList<Double> symbolOrientationConfiguration, ArrayList<Boolean> symbolSymmetryConfiguration, long startTime, long spottingTime, long filteringTime, String documentPath, String symbolPath) {
        this.hmt = hmt;
        this.spottedPoints = spottedPoints;
        this.orientedSymbols = orientedSymbols;
        this.spottingTime = spottingTime;
        this.filteringTime = filteringTime;
        this.startTime = startTime;
        this.symbolOrientationConfiguration = symbolOrientationConfiguration;
        this.symbolSymmetryConfiguration = symbolSymmetryConfiguration;
        this.threshold = threshold;
        this.documentPath = documentPath;
        this.symbolPath = symbolPath;

    }

    public ArrayList<ArrayList<SpottedPoint>> getSpottedPoints() {
        return spottedPoints;
    }

    public ArrayList<ByteImage> getOrientedSymbols() {
        return orientedSymbols;
    }

    public long getSpottingTime() {
        return spottingTime;
    }

    public long getFilteringTime() {
        return filteringTime;
    }

    public ArrayList<Double> getSymbolOrientationConfiguration() {
        return symbolOrientationConfiguration;
    }

    public ArrayList<Boolean> getSymbolSymmetryConfiguration() {
        return symbolSymmetryConfiguration;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public ByteImage getHmt() {
        return hmt;
    }

    public void setHmt(ByteImage hmt) {
        this.hmt = hmt;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getSymbolPath() {
        return symbolPath;
    }
}
