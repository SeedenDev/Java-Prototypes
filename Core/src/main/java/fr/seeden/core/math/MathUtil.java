package fr.seeden.core.math;

public final class MathUtil {

    public static float lerp(float min, float max, float ratio){
        //TODO: assertion everywhere if(ratio<0 || ratio>1)
        return min + ratio * (max - min);
    }

    public static double lerp(int min, int max, double ratio){
        //TODO: assertion everywhere if(ratio<0 || ratio>1)
        return min + ratio * (max - min);
    }

    public static float inverseLerp(float min, float max, float value){
        return (value-min) / (max-min);
    }

    public static double inverseLerp(int min, int max, double value){
        return (value-min) / (max-min);
    }

    public static double sq(int n){
        return Math.pow(n, 2);
    }

    public static double cos(double degree){
        return Math.cos(Math.toRadians(degree));
    }

    public static double sin(double degree){
        return Math.sin(Math.toRadians(degree));
    }

    private MathUtil(){}
}