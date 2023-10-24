package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.NameablePayload;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class RTCLPayload extends NameablePayload {
    private int id;
    private int parentBone;
    private int locIndex;
    
    /*
     * Some kind of mode/flag
     * 
     * see getRTCLMatrix in Ghidra for the math...
     * 
     * Valid values 0-7?
     */
    private int flag; // padding?
    
    private float[] matrix = new float[16];
    
    public RTCLPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent, name);
        
        id = source.readInteger();
        parentBone = source.readInteger();
        locIndex = source.readInteger();
        flag = source.readInteger();
        
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
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(id);
        dest.writeInteger(parentBone);
        dest.writeInteger(locIndex);
        dest.writeInteger(flag);
        
        for (float i : matrix)
            dest.writeFloat(i);
    }
}
