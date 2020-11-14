package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.NameablePayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class RTCLPayload extends NameablePayload {
    private int id;
    private int parentBone;
    private int locIndex;
    private int unknown4; // padding?
    
    private float[] matrix = new float[16];
    
    public RTCLPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent, name);
        
        id = source.readInteger();
        parentBone = source.readInteger();
        locIndex = source.readInteger();
        unknown4 = source.readInteger();
        
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = source.readFloat();
    }
    
    public int getId() {
        return id;
    }
    
    public int getParentBone() {
        return parentBone;
    }
    
    public int getLocIndex() {
        return locIndex;
    }
    
    public float[] getMatrix() {
        return matrix;
    }
    
    @Override
    public int getSize() {
        return 0x50;
    }
    
    @Override
    public Payload getType() {
        return Payload.RTCL;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(id);
        dest.writeInteger(parentBone);
        dest.writeInteger(locIndex);
        dest.writeInteger(unknown4);
        
        for (float i : matrix)
            dest.writeFloat(i);
    }
}
