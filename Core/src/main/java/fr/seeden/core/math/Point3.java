package fr.seeden.core.math;

public class Point3 {

    public float x, y, z;

    public Point3(double x, double y, double z){
        this((float)x, (float)y, (float)z);
    }
    public Point3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double dist(Point3 point){
        final double distX = point.x - this.x;
        final double distY = point.y - this.y;
        final double distZ = point.z - this.z;
        return Math.abs(Math.sqrt(Math.pow(distX, 2)+Math.pow(distY, 2)+Math.pow(distZ, 2)));
    }

    public int distInt(Point3 vec){
        return (int) this.dist(vec);
    }

    @Override
    public String toString() {
        return "Point2{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}