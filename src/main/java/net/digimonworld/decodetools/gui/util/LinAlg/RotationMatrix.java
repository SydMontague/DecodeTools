package net.digimonworld.decodetools.gui.util.LinAlg;

import java.lang.Math;

public class RotationMatrix extends Matrix {
    //////////////////
    // CONSTRUCTORS //
    //////////////////
    public RotationMatrix(float[][] contents) {
        super(contents);
        if (contents.length != 3 || contents[0].length != 3) // Raggedness will be checked by superclass
            throw new IllegalArgumentException("Attempted to initialize a RotationMatrix with an incompatible float[][]");
    }

    public static RotationMatrix fromAxisAngle(Vector3 axis, float angle) {
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        float x = axis.get(0);
        float y = axis.get(1);
        float z = axis.get(2);

        float[][] out = new float[3][3];
        out[0][0] = c + x*x*(1 - c);
        out[0][1] = x*y*(1 - c) - z*s;
        out[0][2] = x*z*(1 - c) + y*s;

        out[1][0] = x*y*(1 - c) + z*s;
        out[1][1] = c + y*y*(1 - c);
        out[1][2] = y*z*(1 - c) - x*s;

        out[2][0] = x*z*(1 - c) - y*s;
        out[2][1] = y*z*(1 - c) + x*s;
        out[2][2] = c + z*z*(1 - c);

        return new RotationMatrix(out);
    }

    // https://blender.stackexchange.com/a/38337
    public static RotationMatrix fromVectorRoll(Vector3 vec, float roll) {
        Vector3 target = new Vector3( 0, 1, 0 );
        Vector3 nor = Vector3.normalise(target);
        Vector3 axis = target.cross(nor);

        RotationMatrix bMatrix;
        if (axis.dot(axis) > 0.0000001) {
            axis = Vector3.normalise(axis);
            float theta = target.angleTo(nor);
            bMatrix = RotationMatrix.fromAxisAngle(axis, theta);
        }
        else {
            float updown = target.dot(nor) > 0 ? 1 : -1;
            bMatrix = new RotationMatrix(
                    new float[][]{
                            {updown,      0,      0},
                            {     0, updown,      0},
                            {     0,      0,      1}
                    }
            );
        }

        RotationMatrix rMatrix = RotationMatrix.fromAxisAngle(nor, roll);
        return rMatrix.dot(bMatrix);
    }

    public void invert() {
        float carrier = 0;
        for (int r=0; r < rows; ++r) {
            for (int c=0; c < cols; ++c) {
                carrier = storage[c][r];
                storage[c][r] = storage[r][c];
                storage[r][c] = carrier;
            }
        }
    }

    //////////////////
    //      OPS     //
    //////////////////
    public RotationMatrix dot(RotationMatrix other) {
        return new RotationMatrix(super.dot(other).storage);
    }

    public float[] toVecRoll() {
        Vector3 vec = new Vector3(this.col(1)); // Convert to Vec3 to access cross product
        RotationMatrix vecmat = RotationMatrix.fromVectorRoll(vec, 0);
        vecmat.invert();

        RotationMatrix rollmat = vecmat.dot(this);
        float roll = (float) java.lang.Math.atan2(rollmat.get(0, 2), rollmat.get(2, 2));

        return new float[]{vec.get(0), vec.get(1), vec.get(2), roll};
    }
}
