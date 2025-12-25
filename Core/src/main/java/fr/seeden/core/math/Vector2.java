package fr.seeden.core.math;

public class Vector2 {

    public final double x, y;
    public final double length;

    public Vector2(Point2 from, Point2 to){
        this(to.x - from.x,to.y - from.y);
    }
    public Vector2(double x, double y){
        this.x = x;
        this.y = y;
        this.length = length();
    }

    private double length(){
        return Math.abs(Math.sqrt(x*x+y*y));
    }

    public Vector2 mult(double n){
        return new Vector2(x*n, y*n);
    }

    public Vector2 add(Vector2 vec){
        return new Vector2(x+vec.x, y+vec.y);
    }

    public Vector2 rot(double deg){
        final double rad = Math.toRadians(deg);
        final double cos = Math.cos(rad);
        final double sin = Math.sin(rad);
        final double x1 = cos*this.x - sin*this.y;
        final double y1 = sin*this.x + cos*this.y;
        return new Vector2(x1, y1);
    }

    public Vector2 normalize(){
        return new Vector2(x/length, y/length);
    }

    public double dot(Vector2 vec){
        return this.x*vec.x + this.y*vec.y;
    }

    public double normalizeDot(Vector2 vec){
        return this.normalize().dot(vec.normalize());
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                ", length=" + length +
                '}';
    }
}