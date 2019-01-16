package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class TREPPayload extends ResPayload {
    private int unknown1;
    private short unknown2;
    private short unknown3;
    // shortarraySize
    private short unknown5;
    private float unknown6;
    
    private float unknown7;
    private float unknown8;
    private float unknown9;
    private short unknown10;
    private short unknown11;

    private int unknown12; // always 0?
    private int unknown13; // always 0?
    private int unknown14; // always 0?
    private int unknown15; // always 0?
    
    private short unknown16;
    // short align(arraySize * 2, 0x04)
    // int arraySize * 2
    
    private short[] array;
    
    public TREPPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        super(parent);
        
        unknown1 = source.readInteger();
        unknown2 = source.readShort();
        unknown3 = source.readShort();
        short arraySize = source.readShort();
        unknown5 = source.readShort();
        unknown6 = source.readFloat();
        
        unknown7 = source.readFloat();
        unknown8 = source.readFloat();
        unknown9 = source.readFloat();
        unknown10 = source.readShort();
        unknown11 = source.readShort();
        
        unknown12 = source.readInteger();
        unknown13 = source.readInteger();
        unknown14 = source.readInteger();
        unknown15 = source.readInteger();
        
        unknown16 = source.readShort();
        source.readShort(); // align(arraySize * 2, 0x04)
        source.readInteger(); // arraySize * 2
        
        array = new short[arraySize];
        
        for (int i = 0; i < arraySize; i++)
            array[i] = source.readShort();
    }
    
    @Override
    public int getSize() {
        return Utils.align(0x38 + array.length * 2, 0x04);
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.TREP;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(unknown1);
        dest.writeShort(unknown2);
        dest.writeShort(unknown3);
        dest.writeShort((short) array.length);
        dest.writeShort(unknown5);
        dest.writeFloat(unknown6);

        dest.writeFloat(unknown7);
        dest.writeFloat(unknown8);
        dest.writeFloat(unknown9);
        dest.writeShort(unknown10);
        dest.writeShort(unknown11);

        dest.writeInteger(unknown12);
        dest.writeInteger(unknown13);
        dest.writeInteger(unknown14);
        dest.writeInteger(unknown15);

        dest.writeShort(unknown16);
        dest.writeShort((short) Utils.align(array.length * 2, 0x04));
        dest.writeInteger(array.length * 2);
        
        for(short val : array)
            dest.writeShort(val);
        
        if(array.length % 2 == 1)
            dest.writeShort((short) 0);
    }
}
