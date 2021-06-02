package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

/*
 * 4-byte       magic value
 * 4-byte       number of entries
 * 4-byte       offset of entries #2
 * 4-byte       offset of entries #1
 * 
 * 4-byte       unknown
 * 2-byte       size of entry #2
 * 2-byte       size of entry #1
 * float        unknown
 * float        unknown
 * 
 * <entries #1> 
 * <entries #2> 
 * 
 * TODO implement proper structure
 */
public class VCTMPayload extends ResPayload {
    private int numEntries;
    private int coordStart;
    private int entriesStart;
    
    private byte unk1; // iterpolation type (< 0xC)
    private byte unk2;
    private byte unk3;
    private byte unk4;
    
    private short sizeValue1;
    private short sizeValue2;
    private float unknown4;
    private float unknown5;
    
    private VCTMEntry[] data1; // frame count
    private VCTMEntry[] data2; // frame data
    
    public VCTMPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        this(source, dataStart, parent);
    }
    
    public VCTMPayload(Access source, int dataStart, AbstractKCAP parent) {
        super(parent);
        long start = source.getPosition();
        
        source.readInteger(); // magic value
        numEntries = source.readInteger();
        coordStart = source.readInteger();
        entriesStart = source.readInteger();
        
        unk1 = source.readByte();
        unk2 = source.readByte();
        unk3 = source.readByte();
        unk4 = source.readByte();
        
        sizeValue1 = source.readShort();
        sizeValue2 = source.readShort();
        unknown4 = source.readFloat();
        unknown5 = source.readFloat();
        
        data1 = new VCTMEntry[numEntries];
        data2 = new VCTMEntry[numEntries];
        
        source.setPosition(start + entriesStart);
        for (int i = 0; i < numEntries; i++)
            data1[i] = new VCTMEntry(source.readByteArray(sizeValue2));
        
        source.setPosition(start + coordStart);
        for (int i = 0; i < numEntries; i++)
            data2[i] = new VCTMEntry(source.readByteArray(sizeValue1));
        
        source.setPosition(Utils.align(source.getPosition(), 0x04));
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
        return 0x20 + Utils.align(data1.length * sizeValue2, 0x04) + Utils.align(data2.length * sizeValue1, 0x04);
    }
    
    @Override
    public Payload getType() {
        return Payload.VCTM;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(numEntries);
        dest.writeInteger(coordStart);
        dest.writeInteger(entriesStart);
        
        dest.writeByte(unk1);
        dest.writeByte(unk2);
        dest.writeByte(unk3);
        dest.writeByte(unk4);
        
        dest.writeShort(sizeValue1);
        dest.writeShort(sizeValue2);
        dest.writeFloat(unknown4);
        dest.writeFloat(unknown5);
        
        for (VCTMEntry entry : data1)
            for (byte b : entry.getData())
                dest.writeByte(b);
            
        dest.setPosition(Utils.align(dest.getPosition(), 0x4));
        
        for (VCTMEntry entry : data2)
            for (byte b : entry.getData())
                dest.writeByte(b);
            
        long diff = Utils.align(dest.getPosition(), 0x04) - dest.getPosition();
        dest.writeByteArray(new byte[(int) diff]);
    }
}
