package net.digimonworld.decodetools.gui.util.LinAlg;

public class Quaternion {
    public final float[] storage;

    public Quaternion(float x, float y, float z, float w) {
        this.storage = new float[]{ x, y, z, w };
    }

    public RotationMatrix toMatrix() {
        float[][] out = new float[3][3];

        float xSq = 2*storage[0]*storage[0];
        float ySq = 2*storage[1]*storage[1];
        float zSq = 2*storage[2]*storage[2];

        float xy = 2*storage[0]*storage[1];
        float xz = 2*storage[0]*storage[2];
        float xw = 2*storage[0]*storage[3];

        float yz = 2*storage[1]*storage[2];
        float yw = 2*storage[1]*storage[3];

        float zw = 2*storage[2]*storage[3];

        // Create rotation matrix
        out[0][0] = 1 - ySq - zSq;
        out[0][1] = xy - zw;
        out[0][2] = xz + yw;

        out[1][0] = xy + zw;
        out[1][1] = 1 - xSq - zSq;
        out[1][2] = yz - xw;

        out[2][0] = xz - yw;
        out[2][1] = yz + xw;
        out[2][2] = 1 - xSq - ySq;

        return new RotationMatrix(out);
    }
}
