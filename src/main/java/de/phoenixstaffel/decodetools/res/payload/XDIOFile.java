package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class XDIOFile extends KCAPPayload {
    private int unknown1; // version?
    private int unknown2;
    private int dataPointer;
    
    private int numEntries;
    private int entrySize;
    private int dataSize; // numEntries * entrySize
    private int unknown3;
    
    private byte[] data;
    
    public XDIOFile(Access source, int dataStart, KCAPFile parent, int size) {
        this(source, dataStart, parent);
    }
    
    private XDIOFile(Access source, int dataStart, KCAPFile parent) {
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
    public void fillResData(IResData resData) {
        resData.add(this.data, false, getParent());
    }
}
