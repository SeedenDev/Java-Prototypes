package fr.seeden.core.math;

public class Vector3 {

    public static Vector3 ZERO = new Vector3(0,0,0);
    public static Vector3 UP = new Vector3(0,1,0);

    public final float x, y, z;
    public final float length;

    public Vector3(Point3 from, Point3 to){
        this(to.x - from.x, to.y - from.y, to.z - from.z);
    }
    public Vector3(double x, double y, double z){
        this((float)x, (float)y, (float)z);
    }
    public Vector3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = length();
    }

    private float length(){
        return (float) Math.abs(Math.sqrt(x*x+y*y+z*z));
    }

    public Vector3 mult(float n){
        return new Vector3(x*n, y*n, z*n);
    }

    public Vector3 add(Vector3 vec){
        return new Vector3(this.x+vec.x, this.y+vec.y, this.z+vec.z);
    }
    public Vector3 substract(Vector3 vec){
        return new Vector3(this.x-vec.x, this.y-vec.y, this.z-vec.z);
    }

    // produit vectoriel
    public Vector3 cross(Vector3 vec){
        return new Vector3(this.y*vec.z-this.z*vec.y, this.z*vec.x-this.x*vec.z, this.x*vec.y-this.y*vec.x);
    }

    public Vector3 normalize(){
        return length==0 ? Vector3.ZERO : new Vector3(x/length, y/length, z/length);
    }

    public double dot(Vector3 vec){
        return this.x*vec.x + this.y*vec.y + this.z*vec.z;
    }

    public double normalizeDot(Vector3 vec){
        return this.normalize().dot(vec.normalize());
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", length=" + length +
                '}';
    }
}