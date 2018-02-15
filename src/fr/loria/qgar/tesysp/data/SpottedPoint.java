package fr.loria.qgar.tesysp.data;

public class SpottedPoint {

    private int x;
    private int y;
    private int configuration;
    private double orientation;
    private boolean symmetry;

    public SpottedPoint(int x, int y, int configuration, double orientation, boolean symmetry) {
        this.x = x;
        this.y = y;
        this.configuration = configuration;
        this.orientation = orientation;
        this.symmetry = symmetry;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getConfiguration() {
        return configuration;
    }

    public void setConfiguration(int configuration) {
        this.configuration = configuration;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public boolean isSymmetry() {
        return symmetry;
    }

    public void setSymmetry(boolean symmetry) {
        this.symmetry = symmetry;
    }
}
