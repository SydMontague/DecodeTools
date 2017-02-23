package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class XTVOFile extends KCAPPayload {
    private int unknown1;
    private int unknown2;
    private int dataPointer;
    
    private int numEntries;
    private int entrySize;
    private int dataSize;
    private int unknown7;
    
    private int unknown8;
    private int unknown9;
    private int unknown10;
    private int unknown11;
    
    private float[] unknown12;
    
    private byte[] data;
    
    public XTVOFile(Access source, int dataStart, KCAPFile parent, int size) {
        this(source, dataStart, parent);
    }
    
    private XTVOFile(Access source, int dataStart, KCAPFile parent) {
        super(parent);
        
        source.readInteger(); // magic value
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        dataPointer = source.readInteger();
        
        numEntries = source.readInteger();
        entrySize = source.readInteger();
        dataSize = source.readInteger();
        unknown7 = source.readInteger();
        
        unknown8 = source.readInteger();
        unknown9 = source.readInteger();
        unknown10 = source.readInteger();
        unknown11 = source.readInteger();
        
        unknown12 = new float[unknown11 / 4];
        
        for (int i = 0; i < unknown12.length; i++)
            unknown12[i] = source.readFloat();
        
        data = source.readByteArray(dataSize, dataPointer + dataStart);
    }
    
    @Override
    public int getSize() {
        return 0x30 + unknown12.length * 4;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.XTVO;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        int dataAddress = dataStream.add(data, false, getParent());
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(unknown1);
        dest.writeInteger(unknown2);
        dest.writeInteger(dataAddress);
        
        dest.writeInteger(numEntries);
        dest.writeInteger(entrySize);
        dest.writeInteger(dataSize);
        dest.writeInteger(unknown7);
        
        dest.writeInteger(unknown8);
        dest.writeInteger(unknown9);
        dest.writeInteger(unknown10);
        dest.writeInteger(unknown11);
        
        for (float f : unknown12)
            dest.writeFloat(f);
    }
    
    @Override
    public void fillResData(IResData resData) {
        resData.add(this.data, false, getParent());
    }
}
