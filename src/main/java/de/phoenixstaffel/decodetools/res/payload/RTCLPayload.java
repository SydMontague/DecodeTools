package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class RTCLPayload extends ResPayload {
    private String name;
    
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    
    private float[] matrix = new float[16];
    
    public RTCLPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        super(parent);
        this.name = name;
        
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
        
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = source.readFloat();
    }
    
    @Override
    public int getSize() {
        return 0x50;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.RTCL;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        
        for (float i : matrix)
            dest.writeFloat(i);
    }

    public boolean hasName() {
        return name != null;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
