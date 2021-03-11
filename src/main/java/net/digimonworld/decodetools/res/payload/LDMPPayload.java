package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class LDMPPayload extends ResPayload {
    private int unknown1;
    private int unknown2;
    private float unknown3;
    private float unknown4;
    
    private int unknown5;
    private int unknown6;
    private int unknown7;
    private int unknown8;
    
    public LDMPPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        unknown3 = source.readFloat();
        unknown4 = source.readFloat();
        unknown5 = source.readInteger();
        unknown6 = source.readInteger();
        unknown7 = source.readInteger();
        unknown8 = source.readInteger();
    }
    
    @Override
    public int getSize() {
        return 0x20;
    }
    
    @Override
    public Payload getType() {
        return Payload.LDMP;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeFloat(unknown3);
        dest.writeFloat(unknown4);
        dest.writeInteger(unknown5);
        dest.writeInteger(unknown6);
        dest.writeInteger(unknown7);
        dest.writeInteger(unknown8);
    }
}
