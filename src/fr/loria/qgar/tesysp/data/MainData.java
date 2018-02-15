package fr.loria.qgar.tesysp.data;

import fr.loria.qgar.tesysp.util.Constants;
import fr.loria.qgar.tesysp.util.Message;
import fr.unistra.pelican.ByteImage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class MainData {
    private ByteImage document = null;
    private ArrayList<ByteImage> tiles = null;
    private ByteImage symbol = null;
    private String documentPath = null;
    private String symbolPath = null;
    private File pathLocation;
    private String csvOutputFilename = null;
    private SettingsData settings;
    private int mode;
    private ArrayList<ArrayList<Rectangle>> selectedAreas = null;
    private ArrayList<ByteImage> hMTs = null;
    private OfflineSpottingResult offlineSpottingResult = null;

    public MainData(int mode) {
        this.mode = mode;
        settings = new SettingsData();
        pathLocation = new File(".");
        setCsvOutputFilename(Constants.DEFAULTMATCHINGFILENAME);
    }

    public void setDocument(ByteImage document) {
        this.document = document;
    }

    public ByteImage getDocument() {
        return document;
    }

    public boolean hasDocument() {
        return document != null;
    }

    public void setSymbol(ByteImage symbol) {
        this.symbol = symbol;
    }

    public ByteImage getSymbol() {
        return symbol;
    }

    public boolean hasSymbol() {
        return symbol != null;
    }

    public void reset() {
        document = null;
        symbol = null;
        offlineSpottingResult = null;
        System.out.println(Message.RESETDATA);
    }

    public SettingsData getSettingsData() {
        return settings;
    }

    public SettingsData getSettings() {
        return settings;
    }

    public void setSettings(SettingsData settings) {
        this.settings = settings;
    }

    public boolean hasOfflineSpottingResult() {
        return offlineSpottingResult != null;
    }

    public boolean hasReferences() {
        return (hMTs != null && selectedAreas != null);
    }

    public File getPathLocation() {
        return pathLocation;
    }

    public void setPathLocation(File pathLocation) {
        this.pathLocation = pathLocation;
    }

    public String getCsvOutputFilename() {
        return csvOutputFilename;
    }

    public void setCsvOutputFilename(String csvOutputFilename) {
        this.csvOutputFilename = csvOutputFilename;
    }

    public int getMode() {
        return mode;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getSymbolPath() {
        return symbolPath;
    }

    public void setSymbolPath(String symbolPath) {
        this.symbolPath = symbolPath;
    }

    public ArrayList<ByteImage> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<ByteImage> tiles) {
        this.tiles = tiles;
    }

    public ArrayList<ByteImage> getHMTs() {
        return hMTs;
    }

    public void sethMTs(ArrayList<ByteImage> hMTs) {
        this.hMTs = hMTs;
    }

    public OfflineSpottingResult getOfflineSpottingResult() {
        return offlineSpottingResult;
    }

    public void setOfflineSpottingResult(OfflineSpottingResult offlineSpottingResult) {
        this.offlineSpottingResult = offlineSpottingResult;
    }

    public ArrayList<ArrayList<Rectangle>> getSelectedAreas() {
        return selectedAreas;
    }

    public void setSelectedAreas(ArrayList<ArrayList<Rectangle>> selectedAreas) {
        this.selectedAreas = selectedAreas;
    }


}
