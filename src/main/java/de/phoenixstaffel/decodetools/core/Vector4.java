package de.phoenixstaffel.decodetools.core;

public class Vector4 {
    float x = 0;
    float y = 0;
    float z = 0;
    float w = 0;
    
    public Vector4() {
    }
    
    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public float dot(Vector4 in) {
        return x * in.x + y * in.y + z * in.z + w * in.w;
    }
}