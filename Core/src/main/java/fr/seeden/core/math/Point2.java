package fr.seeden.core.math;

public class Point2 {

    public final int x, y;

    public Point2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double dist(Point2 vec){
        final int distX = vec.x - this.x;
        final int distY = vec.y - this.y;
        return Math.abs(Math.sqrt(Math.pow(distX, 2)+Math.pow(distY, 2)));
    }

    public int distInt(Point2 vec){
        return (int) this.dist(vec);
    }

    @Override
    public String toString() {
        return "Point2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}