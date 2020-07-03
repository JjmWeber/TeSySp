package fr.uha.tesysp.util;

import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.geometric.Crop2D;
import fr.unistra.pelican.util.Point;

import java.awt.*;
import java.util.ArrayList;

public class Crop {
    private Image document;
    private ArrayList<ByteImage> tiles;
    private int xTilesNumber;
    private int yTilesNumber;
    private int symbolSize;

    public Crop(Image document, int symbolSize) {
        this.document = document;
        this.tiles = new ArrayList<ByteImage>();
        computeTilesNumber();
        this.symbolSize = symbolSize;
        crop();
    }


    public void computeTilesNumber() {
        // Search screen resolution
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();

        //Compute the number of necessary tiles
        this.xTilesNumber = (int) (Math.ceil(document.getXDim() / maximumWindowBounds.width) * 1.2);
        this.yTilesNumber = (int) (Math.ceil(document.getYDim() / maximumWindowBounds.height) * 1.2);
        if (xTilesNumber == 0) {
            xTilesNumber = 1;
        }
        if (yTilesNumber == 0) {
            yTilesNumber = 1;
        }
    }

    public void crop() {
        //Compute tiles dimension
        final int xdim = document.xdim / xTilesNumber;
        final int ydim = document.ydim / yTilesNumber;


        for (int y = 0; y < xTilesNumber; y++) {
            for (int x = 0; x < yTilesNumber; x++) {
                // xovr and yovr are user to overlap tiles, avoiding cut symbols
                int xovr = symbolSize;
                int yovr = symbolSize;
                if (x + 1 == xTilesNumber) {
                    xovr = 0;
                }
                if (y + 1 == yTilesNumber) {
                    yovr = 0;
                }
                Point p1 = new Point(x * xdim, y * ydim);
                Point p2 = new Point((x + 1) * xdim + xovr, (y + 1) * ydim + yovr);
                tiles.add((ByteImage) Crop2D.exec(document, p1, p2));

            }
        }
    }


    public Image getDocument() {
        return document;
    }


    public void setDocument(Image document) {
        this.document = document;
        this.tiles = new ArrayList<ByteImage>();
        computeTilesNumber();
        crop();
    }


    public ArrayList<ByteImage> getTiles() {
        return tiles;
    }

}
