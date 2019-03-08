package fr.loria.qgar.tesysp.engine;

import fr.unistra.pelican.BooleanImage;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.util.Pixel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CompareAmplitudes {
    private Image input;
    private BooleanImage filtered;
    private ArrayList<Pixel> filteredPoints;
    private ArrayList<Integer> amplitudeDifferences;
    private double[] hist;

    public CompareAmplitudes(Image input, BooleanImage filtered) {
        this.input = input;
        this.filtered = filtered;
        this.filteredPoints = new ArrayList<>();
        this.amplitudeDifferences = new ArrayList<>();
        this.hist = new double[100];

        buildFilteredPoints();
        buildAmplitudeDifferences();
        buildHistogram();
    }

    public void buildFilteredPoints() {
        for (int x = 0; x < filtered.xdim; x++) {
            for (int y = 0; y < filtered.ydim; y++) {
                if (filtered.getPixelXYBoolean(x, y)) {
                    filteredPoints.add(new Pixel(x, y));
                }
            }
        }
    }

    public void buildAmplitudeDifferences() {
        for (int k = 0; k < filteredPoints.size(); k++) {
            Pixel p = filteredPoints.get(k);
            int pAmplitude = input.getPixelByte(p);
            int difference = 0;
            // For each neighbor of p
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    //Compute the amplitude difference between p and his neighbor
                    if (p.x + i > 0 && p.x + i < input.getXDim() && p.y + j > 0 && p.y + j < input.getYDim()) {

                        int neighborAmplitude = input.getPixelXYByte(p.x + i, p.y + j);
                        difference += pAmplitude - neighborAmplitude;

                    }
                }
            }
            difference /= 8;
            amplitudeDifferences.add(difference);
        }
    }

    public void buildHistogram() {
        for (int i = 0; i < amplitudeDifferences.size(); i++) {
            if (amplitudeDifferences.get(i) > 0) {
                hist[amplitudeDifferences.get(i)] += 1;
            }
        }
    }

    public void saveDifferences(String path) {
        FileWriter fw;
        if (filteredPoints.size() != amplitudeDifferences.size()) {
            System.err.println("Error, different sizes for lists filteredPoints and amplitudeDifferences");
        } else try {
            fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("XCoordinate;YCoordinate;AmplitudeDifference \n");
            for (int i = 0; i < filteredPoints.size(); i++) {
                bw.write(filteredPoints.get(i).x + ";" + filteredPoints.get(i).y + ";" + amplitudeDifferences.get(i) + "\n");
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void saveHistogram(String path) {
        FileWriter fw;
        try {
            fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < hist.length; i++) {
                bw.write(hist[i] + "\n");
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public Image getInput() {
        return input;
    }

    public void setInput(Image input) {
        this.input = input;
    }

    public BooleanImage getFiltered() {
        return filtered;
    }

    public void setFiltered(BooleanImage filtered) {
        this.filtered = filtered;
    }

    public ArrayList<Pixel> getFilteredPoints() {
        return filteredPoints;
    }

    public void setFilteredPoints(ArrayList<Pixel> filteredPoints) {
        this.filteredPoints = filteredPoints;
    }

    public ArrayList<Integer> getAmplitudeDifferences() {
        return amplitudeDifferences;
    }

    public void setAmplitudeDifferences(ArrayList<Integer> amplitudeDifferences) {
        this.amplitudeDifferences = amplitudeDifferences;
    }

    public double[] getHist() {
        return hist;
    }

    public void setHist(double[] hist) {
        this.hist = hist;
    }
}
