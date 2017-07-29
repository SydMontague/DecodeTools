package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class TNOJPayload extends ResPayload {
    private String name;
    
    private int nameId;
    private int parentId;
    private int unknown1;
    private int unknown2;
    
    private float[] matrix = new float[16]; // rotation+translation matrix?
    
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float unknown3;
    
    private float unknown4;
    private float unknown5;
    private float unknown6;
    private float unknown7;
    
    private float scaleX; // ? scales the vertices of this joint + children
    private float scaleY; // ?
    private float scaleZ; // ?
    // padding
    
    private float localScaleX; // ? scales only the joint
    private float localScaleY; // ?
    private float localScaleZ; // ?
    // padding
    
    public TNOJPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        super(parent);
        this.name = name;
        
        nameId = source.readInteger();
        parentId = source.readInteger();
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        
        for (int i = 0; i < 16; i++)
            matrix[i] = source.readFloat();
        
        xOffset = source.readFloat();
        yOffset = source.readFloat();
        zOffset = source.readFloat();
        unknown3 = source.readFloat();
        
        unknown4 = source.readFloat();
        unknown5 = source.readFloat();
        unknown6 = source.readFloat();
        unknown7 = source.readFloat();
        
        scaleX = source.readFloat();
        scaleY = source.readFloat();
        scaleZ = source.readFloat();
        source.readFloat(); // padding
        
        localScaleX = source.readFloat();
        localScaleY = source.readFloat();
        localScaleZ = source.readFloat();
        source.readFloat(); // padding
        
        source.readInteger(); // padding
        source.readInteger(); // padding
        source.readInteger(); // padding
        source.readInteger(); // padding
    }
    
    @Override
    public int getSize() {
        return 0xA0;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.TNOJ;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(nameId);
        dest.writeInteger(parentId);
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        
        for (float f : matrix)
            dest.writeFloat(f);
        
        dest.writeFloat(xOffset);
        dest.writeFloat(yOffset);
        dest.writeFloat(zOffset);
        dest.writeFloat(unknown3);
        
        dest.writeFloat(unknown4);
        dest.writeFloat(unknown5);
        dest.writeFloat(unknown6);
        dest.writeFloat(unknown7);
        
        dest.writeFloat(scaleX);
        dest.writeFloat(scaleY);
        dest.writeFloat(scaleZ);
        dest.writeFloat(0);
        
        dest.writeFloat(localScaleX);
        dest.writeFloat(localScaleY);
        dest.writeFloat(localScaleZ);
        dest.writeFloat(0);
        
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
    }
}
