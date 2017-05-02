package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class XDIOPayload extends ResPayload {
    private int unknown1; // version?
    private int unknown2;
    private int dataPointer;
    
    private int numEntries;
    private int entrySize;
    private int dataSize; // numEntries * entrySize
    private int unknown3;
    
    private byte[] data;
    
    public XDIOPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        this(source, dataStart, parent);
    }
    
    private XDIOPayload(Access source, int dataStart, KCAPPayload parent) {
        super(parent);
        
        source.readInteger(); // magic value
        unknown1 = source.readInteger();
        unknown2 = source.readInteger();
        dataPointer = source.readInteger();
        
        numEntries = source.readInteger();
        entrySize = source.readInteger();
        dataSize = source.readInteger();
        unknown3 = source.readInteger();
        
        data = source.readByteArray(dataSize, dataPointer + dataStart);
    }
    
    @Override
    public int getSize() {
        return 0x20;
    }
    
    @Override
    public Payload getType() {
        return Payload.XDIO;
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
        dest.writeInteger(unknown3);
    }
    
    @Override
    public void fillDummyResData(DummyResData resData) {
        resData.add(this.data, false, getParent());
    }
}
