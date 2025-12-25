package fr.seeden.renderer.math;

import fr.seeden.core.math.Matrix;
import fr.seeden.core.math.Vector3;

public class RMatrix extends Matrix {

    public RMatrix(double[][] data) {
        super(data);
    }

    public Matrix rotateX(double degree){
        return multiply(SpecialMatrices.X_ROTATION_MATRIX.apply(degree));
    }

    public Matrix rotateY(double degree){
        return multiply(SpecialMatrices.Y_ROTATION_MATRIX.apply(degree));
    }

    public Matrix rotateZ(double degree){
        return multiply(SpecialMatrices.Z_ROTATION_MATRIX.apply(degree));
    }

    public Matrix translate(double x, double y, double z){
        return multiply(SpecialMatrices.TRANSLATION_MATRIX.apply(new Vector3(x, y, z)));
    }

    public Matrix rescale(double x, double y, double z){
        return multiply(SpecialMatrices.SCALE_MATRIX.apply(new Vector3(x, y, z)));
    }

    public Matrix rotateXYZ(double xRot, double yRot, double zRot) {
        Matrix rotX = SpecialMatrices.X_ROTATION_MATRIX.apply(xRot);
        Matrix rotY = SpecialMatrices.Y_ROTATION_MATRIX.apply(yRot);
        Matrix rotZ = SpecialMatrices.Z_ROTATION_MATRIX.apply(zRot);
        return rotZ.multiply(rotY).multiply(rotX); // ← classical order
    }
}