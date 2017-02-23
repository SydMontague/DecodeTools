package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class VCTMFile extends KCAPPayload {
    private int numEntries;
    private int coordStart;
    private int entriesStart;
    
    private int unknown1;
    private short sizeValue1;
    private short sizeValue2;
    private int unknown4;
    private int unknown5;
    
    private VCTMEntry[] data1;
    private VCTMEntry[] data2;
    
    // 1 byte per numEntries array
    // 8 byte per numEntries array
    public VCTMFile(Access source, int dataStart, KCAPFile parent, int size) {
        this(source, dataStart, parent);
    }
    
    public VCTMFile(Access source, int dataStart, KCAPFile parent) {
        super(parent);
        long start = source.getPosition();
        
        source.readInteger(); // magic value
        numEntries = source.readInteger();
        coordStart = source.readInteger();
        entriesStart = source.readInteger();
        
        unknown1 = source.readInteger();
        sizeValue1 = source.readShort();
        sizeValue2 = source.readShort();
        unknown4 = source.readInteger();
        unknown5 = source.readInteger();
        
        data1 = new VCTMEntry[numEntries];
        data2 = new VCTMEntry[numEntries];
        
        source.setPosition(start + entriesStart);
        for (int i = 0; i < numEntries; i++)
            data1[i] = new VCTMEntry(source.readByteArray(sizeValue2));
        
        source.setPosition(start + coordStart);
        for (int i = 0; i < numEntries; i++)
            data2[i] = new VCTMEntry(source.readByteArray(sizeValue1));
    }
    
    class VCTMEntry {
        private final byte[] data;
        
        public VCTMEntry(byte[] data) {
            this.data = data;
        }
        
        public byte[] getData() {
            return data;
        }
    }
    
    @Override
    public int getSize() {
        return 0x20 + Utils.getPadded(data1.length * sizeValue2, 4) + Utils.getPadded(data2.length * sizeValue1, 4);
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.VCTM;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(numEntries);
        dest.writeInteger(coordStart);
        dest.writeInteger(entriesStart);
        
        dest.writeInteger(unknown1);
        dest.writeShort(sizeValue1);
        dest.writeShort(sizeValue2);
        dest.writeInteger(unknown4);
        dest.writeInteger(unknown5);
        
        for (VCTMEntry entry : data1)
            for (byte b : entry.getData())
                dest.writeByte(b);
            
        dest.setPosition(Utils.getPadded(dest.getPosition(), 0x4));
        
        for (VCTMEntry entry : data2)
            for (byte b : entry.getData())
                dest.writeByte(b);
    }
}
