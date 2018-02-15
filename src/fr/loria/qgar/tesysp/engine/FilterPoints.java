package fr.loria.qgar.tesysp.engine;

import fr.unistra.pelican.ByteImage;

import java.awt.*;
import java.util.ArrayList;

public class FilterPoints {
    private ArrayList<ArrayList<Rectangle>> selectedAreas;
    private ArrayList<ByteImage> tiles;
    private ArrayList<ArrayList<Point>> filteredPoints;
    private ArrayList<ByteImage> hMTList;

    public FilterPoints(ArrayList<ArrayList<Rectangle>> selectedAreas, ArrayList<ByteImage> hMTList) {
        this.selectedAreas = selectedAreas;
        this.filteredPoints = new ArrayList<>(selectedAreas.size());
        this.hMTList = hMTList;

        filter();
    }

    public void filter() {
        for (int i = 0; i < selectedAreas.size(); i++) {

            ByteImage hMT = hMTList.get(i);
            ArrayList<Point> tprPoints = new ArrayList<>();
            for (int j = 0; j < selectedAreas.get(i).size(); j++) {
                Rectangle r = selectedAreas.get(i).get(j);
                int max = 0;
                Point pMax = new Point(r.x, r.y);
                for (int x = r.x; x < r.x + r.width; x++) {
                    for (int y = r.y; y < r.y + r.height; y++) {
                        if (hMT.getPixelXYByte(x, y) > max) {
                            max = hMT.getPixelXYByte(x, y);
                            pMax = new Point(x, y);
                        }
                    }
                }
                System.out.println(pMax);
                tprPoints.add(pMax);
            }
            filteredPoints.add(tprPoints);
        }


    }

    public ArrayList<ArrayList<Point>> getFilteredPoints() {
        return filteredPoints;
    }

    public void setFilteredPoints(ArrayList<ArrayList<Point>> filteredPoints) {
        this.filteredPoints = filteredPoints;
    }


}
