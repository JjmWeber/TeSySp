package fr.loria.qgar.tesysp.util;

public class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "Tuple : " + x + " " + y + "\n";
    }
}