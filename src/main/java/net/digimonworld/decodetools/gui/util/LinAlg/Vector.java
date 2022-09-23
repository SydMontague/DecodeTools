package net.digimonworld.decodetools.gui.util.LinAlg;

import java.lang.Math;

public class Vector {
    public final int length;
    public final float[] storage;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    public Vector(float[] contents) {
        this.storage = contents.clone();
        this.length = contents.length;
    }

    public static Vector normalise(Vector v) {
        float[] contents = v.storage.clone();
        float magnitude = v.getMagnitude();
        for (int i=0; i < v.length; ++i)
            contents[i] /= magnitude;
        return new Vector(contents);
    }

    //////////////////
    //   ACCESSORS  //
    //////////////////
    public float get(int idx) {
        return storage[idx];
    }

    //////////////////
    //      OPS     //
    //////////////////
    public float dot(Vector other) {
        float out = 0;
        if (other.length != this.length)
            throw new IllegalArgumentException("Attempted to dot two vectors of different lengths");
        for (int i=0; i < this.length; ++i)
            out += this.get(i) * other.get(i);
        return out;
    }

    public float getMagnitude() {
        return (float)Math.sqrt(this.dot(this));
    }

    public float angleTo(Vector other) {
        float numerator = this.dot(other);
        float denominator = this.getMagnitude() * other.getMagnitude();
        return (float)Math.acos(numerator/denominator);
    }
}
