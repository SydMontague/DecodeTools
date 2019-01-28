package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class TMEPPayload extends ResPayload {
    private short unknown1;
    private short unknown2;
    private short unknown3;
    private short unknown4;
    private int unknown5;
    private int unknown6;
    
    private short unknown7;
    private short unknown8;
    private short unknown9;
    private short unknown10;
    private float unknown11;
    private float unknown12;
    
    private float unknown13;
    private float unknown14;
    private int unknown15;
    private float unknown16;
    
    private float unknown17;
    private float unknown18;
    private int unknown19;
    private int unknown20;
    
    public TMEPPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        
        this.unknown1 = source.readShort();
        this.unknown2 = source.readShort();
        this.unknown3 = source.readShort();
        this.unknown4 = source.readShort();
        this.unknown5 = source.readInteger();
        this.unknown6 = source.readInteger();
        
        this.unknown7 = source.readShort();
        this.unknown8 = source.readShort();
        this.unknown9 = source.readShort();
        this.unknown10 = source.readShort();
        this.unknown11 = source.readFloat();
        this.unknown12 = source.readFloat();
        
        this.unknown13 = source.readFloat();
        this.unknown14 = source.readFloat();
        this.unknown15 = source.readInteger();
        this.unknown16 = source.readFloat();
        
        this.unknown17 = source.readFloat();
        this.unknown18 = source.readFloat();
        this.unknown19 = source.readInteger();
        this.unknown20 = source.readInteger();
    }
    
    @Override
    public int getSize() {
        return 0x40;
    }
    
    @Override
    public Payload getType() {
        return Payload.TMEP;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeShort(unknown1);
        dest.writeShort(unknown2);
        dest.writeShort(unknown3);
        dest.writeShort(unknown4);
        dest.writeInteger(unknown5);
        dest.writeInteger(unknown6);
        
        dest.writeShort(unknown7);
        dest.writeShort(unknown8);
        dest.writeShort(unknown9);
        dest.writeShort(unknown10);
        dest.writeFloat(unknown11);
        dest.writeFloat(unknown12);
        
        dest.writeFloat(unknown13);
        dest.writeFloat(unknown14);
        dest.writeInteger(unknown15);
        dest.writeFloat(unknown16);
        
        dest.writeFloat(unknown17);
        dest.writeFloat(unknown18);
        dest.writeInteger(unknown19);
        dest.writeInteger(unknown20);
    }
}
