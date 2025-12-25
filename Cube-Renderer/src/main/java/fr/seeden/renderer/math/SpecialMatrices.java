package fr.seeden.renderer.math;

import fr.seeden.core.math.Matrix;
import fr.seeden.core.math.Vector3;

import java.util.function.Function;

public final class SpecialMatrices {

    public static final Function<Double, Matrix> X_ROTATION_MATRIX = degree -> {
        double cos = RendererMathUtil.cosCheckZero(degree);
        double sin = RendererMathUtil.sinCheckZero(degree);
        return new Matrix(new double[][]{
                {1, 0, 0, 0},
                {0, cos, -sin, 0},
                {0, sin, cos, 0},
                {0, 0, 0, 1}
        });
    };

    public static final Function<Double, Matrix> Y_ROTATION_MATRIX = degree -> {
        double cos = RendererMathUtil.cosCheckZero(degree);
        double sin = RendererMathUtil.sinCheckZero(degree);
        return new Matrix(new double[][]{
                {cos, 0, sin, 0},
                {0, 1, 0, 0},
                {-sin, 0, cos, 0},
                {0, 0, 0, 1}
        });
    };

    public static final Function<Double, Matrix> Z_ROTATION_MATRIX = degree -> {
        double cos = RendererMathUtil.cosCheckZero(degree);
        double sin = RendererMathUtil.sinCheckZero(degree);
        return new Matrix(new double[][]{
                {cos, -sin, 0, 0},
                {sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    };

    public static final Function<Vector3, Matrix> TRANSLATION_MATRIX = vec3 -> {
        return new Matrix(new double[][]{
                {1, 0, 0, vec3.x},
                {0, 1, 0, vec3.y},
                {0, 0, 1, vec3.z},
                {0, 0, 0, 1}
        });
    };

    public static final Function<Vector3, Matrix> SCALE_MATRIX = vec3 -> {
        return new Matrix(new double[][]{
                {vec3.x, 0, 0, 0},
                {0, vec3.y, 0, 0},
                {0, 0, vec3.z, 0},
                {0, 0, 0, 1}
        });
    };
}