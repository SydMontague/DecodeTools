package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class MFTPPayload extends ResPayload {
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    
    private float unknown5;
    private float unknown6;
    private float unknown7;
    private float unknown8;
    
    private float unknown9;
    private float unknown10;
    private float unknown11;
    private float unknown12;
    
    private float unknown13;
    private float unknown14;
    private float unknown15;
    private float unknown16;
    
    private float unknown17;
    private float unknown18;
    private float unknown19;
    private float unknown20;
    
    public MFTPPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        unknown3 = source.readInteger();
        unknown4 = source.readInteger();
        
        unknown5 = source.readFloat();
        unknown6 = source.readFloat();
        unknown7 = source.readFloat();
        unknown8 = source.readFloat();
        
        unknown9 = source.readFloat();
        unknown10 = source.readFloat();
        unknown11 = source.readFloat();
        unknown12 = source.readFloat();
        
        unknown13 = source.readFloat();
        unknown14 = source.readFloat();
        unknown15 = source.readFloat();
        unknown16 = source.readFloat();
        
        unknown17 = source.readFloat();
        unknown18 = source.readFloat();
        unknown19 = source.readFloat();
        unknown20 = source.readFloat();
    }
    
    @Override
    public int getSize() {
        return 0x50;
    }
    
    @Override
    public Payload getType() {
        return Payload.MFTP;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeInteger(unknown3);
        dest.writeInteger(unknown4);
        
        dest.writeFloat(unknown5);
        dest.writeFloat(unknown6);
        dest.writeFloat(unknown7);
        dest.writeFloat(unknown8);
        
        dest.writeFloat(unknown9);
        dest.writeFloat(unknown10);
        dest.writeFloat(unknown11);
        dest.writeFloat(unknown12);
        
        dest.writeFloat(unknown13);
        dest.writeFloat(unknown14);
        dest.writeFloat(unknown15);
        dest.writeFloat(unknown16);
        
        dest.writeFloat(unknown17);
        dest.writeFloat(unknown18);
        dest.writeFloat(unknown19);
        dest.writeFloat(unknown20);
    }
}
