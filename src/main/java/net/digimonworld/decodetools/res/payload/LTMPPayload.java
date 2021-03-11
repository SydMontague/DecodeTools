package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class LTMPPayload extends ResPayload {
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    
    private float unknown5;
    private float unknown6;
    private int unknown7;
    private int unknown8;
    
    private int unknown9;
    private int unknown10;
    private int unknown11;
    private int unknown12;
    
    private int unknown13;
    private int unknown14;
    private int unknown15;
    private int unknown16;
    
    private int unknown17;
    private int unknown18;
    
    public LTMPPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
        
        unknown5 = source.readFloat();
        unknown6 = source.readFloat();
        unknown7 = source.readInteger();
        unknown8 = source.readInteger();
        
        unknown9 = source.readInteger();
        unknown10 = source.readInteger();
        unknown11 = source.readInteger();
        unknown12 = source.readInteger();
        
        unknown13 = source.readInteger();
        unknown14 = source.readInteger();
        unknown15 = source.readInteger();
        unknown16 = source.readInteger();
        
        unknown17 = source.readInteger();
        unknown18 = source.readInteger();
    }
    
    @Override
    public int getSize() {
        return 0x48;
    }
    
    @Override
    public Payload getType() {
        return Payload.LTMP;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        
        dest.writeFloat(unknown5);
        dest.writeFloat(unknown6);
        dest.writeInteger(unknown7);
        dest.writeInteger(unknown8);
        
        dest.writeInteger(unknown9);
        dest.writeInteger(unknown10);
        dest.writeInteger(unknown11);
        dest.writeInteger(unknown12);
        
        dest.writeInteger(unknown13);
        dest.writeInteger(unknown14);
        dest.writeInteger(unknown15);
        dest.writeInteger(unknown16);
        
        dest.writeInteger(unknown17);
        dest.writeInteger(unknown18);
    }
}
