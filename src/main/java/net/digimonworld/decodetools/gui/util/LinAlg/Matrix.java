package net.digimonworld.decodetools.gui.util.LinAlg;

import org.lwjgl.assimp.AIMatrix4x4;

// Would like to use generics so it's easy to e.g. switch to doubles
public class Matrix {
    public final int rows;
    public final int cols;
    public final float[][] storage;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    public Matrix(float[][] contents) {
        this.cols = contents[0].length;
        for (float[] subarray : contents) {
            if (subarray.length != this.cols)
                throw new IllegalArgumentException("Matrix initialized with a ragged list");
        }
        this.storage = contents.clone();
        this.rows = contents.length;

    }

    public Matrix(float[] contents, int rows, int cols) {
        if (contents.length != rows*cols) {
            throw new IllegalArgumentException("Matrix initialized with an array inconsistent with the requested rows and columns");
        }

        this.rows = rows;
        this.cols = cols;
        this.storage = new float[rows][cols];
        for (int i=0; i < rows; ++i)
            for (int j=0; j < cols; ++j)
                this.storage[i][j] = contents[i*j + j];
    }

    public static Matrix fromAIMatrix4x4(AIMatrix4x4 matrix) {
        float[][] float_matrix = {
                { matrix.a1(), matrix.a2(), matrix.a3(), matrix.a4()},
                { matrix.b1(), matrix.b2(), matrix.b3(), matrix.b4()},
                { matrix.c1(), matrix.c2(), matrix.c3(), matrix.c4()},
                { matrix.d1(), matrix.d2(), matrix.d3(), matrix.d4()}
        };
        return new Matrix(float_matrix);
    }


    //////////////////
    //   ACCESSORS  //
    //////////////////
    public float get(int row, int col) {
        return storage[row][col];
    }

    public Vector col(int idx) {
        float[] column_data = new float[this.rows];
        for (int i=0; i < this.rows; ++i)
            column_data[i] = this.storage[i][idx];
        return new Vector(column_data);
    }

    public Vector row(int idx) {
        return new Vector(this.storage[idx]);
    }

    public float[] toFlatArray() {
        float[] out = new float[rows*cols];
        for (int r=0; r < rows; ++r)
            for (int c=0; c < cols; ++c)
                out[r*c + c] = storage[r][c];
        return out;
    }

    //////////////////
    //      OPS     //
    //////////////////
    public Matrix dot(Matrix other) {
        if (this.rows != other.cols)
            throw new IllegalArgumentException("Attempted to dot two incompatible matrices");
        float[][] result = new float[this.cols][other.rows];

        for (int r=0; r < this.rows; ++r)
            for (int c=0; c < other.cols; ++c)
                result[r][c] = this.row(r).dot(other.col(c));
        return new Matrix(result);
    }

    public Vector dot(Vector other) {
        if (this.rows != other.length)
            throw new IllegalArgumentException("Attempted to dot a matrix with an incompatible vector");
        float[] result = new float[this.cols];

        for (int r=0; r < this.rows; ++r)
            result[r] = this.row(r).dot(other);
        return new Vector(result);
    }
}
