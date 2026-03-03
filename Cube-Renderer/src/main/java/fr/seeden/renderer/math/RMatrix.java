package fr.seeden.renderer.math;

import fr.seeden.core.math.Matrix;
import fr.seeden.core.math.Vector3;

public class RMatrix extends Matrix {

    //TODO: clean this shit

    public RMatrix(double[][] data) {
        super(data);
    }

    public RMatrix multiply(RMatrix mat){
        return (RMatrix) super.multiply(mat); //TODO: not that. Just don't have the time to implement the real method rn
    }

    public RMatrix rotateX(double degree){
        return new RMatrix(multiply(SpecialMatrices.X_ROTATION_MATRIX.apply(degree)).getRawData());
    }

    public RMatrix rotateY(double degree){
        return new RMatrix(multiply(SpecialMatrices.Y_ROTATION_MATRIX.apply(degree)).getRawData());
    }

    public RMatrix rotateZ(double degree){
        return new RMatrix(multiply(SpecialMatrices.Z_ROTATION_MATRIX.apply(degree)).getRawData());
    }

    public RMatrix translate(double x, double y, double z){
        return new RMatrix(multiply(SpecialMatrices.TRANSLATION_MATRIX.apply(new Vector3(x, y, z))).getRawData());
    }

    public RMatrix rescale(double x, double y, double z){
        return new RMatrix(multiply(SpecialMatrices.SCALE_MATRIX.apply(new Vector3(x, y, z))).getRawData());
    }

    public RMatrix rotateXYZ(double xRot, double yRot, double zRot) {
        Matrix rotX = SpecialMatrices.X_ROTATION_MATRIX.apply(xRot);
        Matrix rotY = SpecialMatrices.Y_ROTATION_MATRIX.apply(yRot);
        Matrix rotZ = SpecialMatrices.Z_ROTATION_MATRIX.apply(zRot);
        return new RMatrix(rotZ.multiply(rotY).multiply(rotX).getRawData()); // ← classical order
    }
}