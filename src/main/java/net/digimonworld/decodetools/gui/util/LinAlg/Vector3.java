package net.digimonworld.decodetools.gui.util.LinAlg;

public class Vector3 extends Vector {
    //////////////////
    // CONSTRUCTORS //
    //////////////////
    public Vector3(float x, float y, float z) {
        super(new float[] {x, y, z});
    }

    public Vector3(Vector v) {
        super(v.storage);
        if (v.length != 3)
            throw new IllegalArgumentException("Attempted to initialize a Vector3 with a vector that did not have a length of 3");
    }

    public static Vector3 normalise(Vector3 v) {
        return new Vector3(Vector.normalise(v));
    }

    //////////////////
    //      OPS     //
    //////////////////
    public Vector3 cross(Vector3 other) {
        float[] out = new float[3];
        float x = this.get(1) * other.get(2) - this.get(2) * other.get(1);
        float y = this.get(2) * other.get(0) - this.get(0) * other.get(2);
        float z = this.get(0) * other.get(1) - this.get(1) * other.get(0);
        return new Vector3(x, y, z);
    }
}
