package fr.seeden.core.math;

import java.util.Arrays;

public class Matrix {

    //TODO: clean this awful code and make a Matrix4x4 version
    //TODO: REWRITE ALL

    private final double[][] data;
    public Matrix(double[][] data) {
        this.data = data;
    }

    public Matrix multiply(Matrix matrix){
        return multiplyRowMajorPerhaps(matrix);
    }
    private Matrix multiplyRowMajorPerhaps(Matrix matrix){
        if(data.length==0 || matrix.data.length==0) return null;
        if(data[0].length!=matrix.data.length) return null;
        int n = data[0].length; // nombre de colonnes de la première matrice, et de lignes de la seconde
        int m = data.length; // nombre de lignes de la première matrice
        int p = matrix.data[0].length; // nombre de colonnes de la seconde matrice
        double[][] newData = new double[m][p];

        for (int l = 0; l < m; l++) {
            for (int c = 0; c < p; c++) {
                double k = 0;
                for (int i = 0; i < n; i++) {
                    k += data[l][i]*matrix.data[i][c];
                }
                newData[l][c] = k;
            }
        }
        return new Matrix(newData);
    }
    private Matrix multiplyColumnMajorPerhaps(Matrix matrix) {
        if (data.length == 0 || matrix.data.length == 0) return null;
        if (data.length != matrix.data[0].length) return null;

        int m = data[0].length; // number of rows
        int n = matrix.data[0].length; // number of rows in result
        int p = matrix.data.length; // number of columns in matrix

        double[][] newData = new double[p][m]; // p columns, m rows

        for (int col = 0; col < p; col++) {
            for (int row = 0; row < m; row++) {
                double sum = 0;
                for (int i = 0; i < data.length; i++) {
                    sum += data[i][row] * matrix.data[col][i];
                }
                newData[col][row] = sum;
            }
        }
        return new Matrix(newData);
    }

    public double get(int line, int column){
        return getRowMajor(line, column);
    }
    private double getRowMajor(int line, int column){
        return this.data[line][column];
    }
    private double getColumnMajor(int line, int column){
        return this.data[column][line];
    }

    public Matrix deepCopy(){
        int lines = data.length;
        int columns = data[0].length;
        double[][] copy = new double[lines][columns];
        for (int l = 0; l < lines; l++) {
            for (int c = 0; c < columns; c++) {
                copy[l][c] = data[l][c];
            }
        }
        return new Matrix(copy);
    }

    public final double[][] getRawData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (double[] line : data) {
            sb.append(Arrays.toString(line)).append(System.lineSeparator());
        }
        return "Matrix{" +
                "data=" + sb +
                '}';
    }
}