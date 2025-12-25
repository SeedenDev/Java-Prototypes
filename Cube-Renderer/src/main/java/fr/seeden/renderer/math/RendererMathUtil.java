package fr.seeden.renderer.math;

import fr.seeden.core.math.MathUtil;

public class RendererMathUtil {

    public static double cosCheckZero(double degree){
        return checkZero(MathUtil.cos(degree));
    }
    public static double sinCheckZero(double degree){
        return checkZero(MathUtil.sin(degree));
    }

    private static double checkZero(double value){
        return (value>0 && value<1.0E-10)||(value<0 && value> -1.0E-10) ? 0 : value;
    }
}