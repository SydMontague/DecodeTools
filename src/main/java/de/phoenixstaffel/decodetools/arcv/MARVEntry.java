package de.phoenixstaffel.decodetools.arcv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.phoenixstaffel.decodetools.Utils;

/*
 * MARV Entry Format – 0x20
 * int - magic value (MARV)
 * int - unknown
 * int - unknown
 * int - unknown
 * int - memory size (-> memory allocated for DATA parts)
 * int - unknown
 * int - unknown
 * int - unknown
 */
public class MARVEntry {
    private static final int MAGIC_VALUE = 0x5652414D; // MARV
    private int unknown1; // always 0x00000001?
    private int structureSize;
    private int unknown3; // padding structure?
    
    private int dataSize;
    private int unknown4; // padding data?
    private int numEntries; // number of data entries
    private int unknown6; // structureSize increased till it ends with 0x20 or 0xA0?
    
    public MARVEntry(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.getInt(); // magic value
        unknown1 = buffer.getInt();
        structureSize = buffer.getInt();
        unknown3 = buffer.getInt();
        
        if(structureSize == 0x18CB3A)
            System.out.println("kk");
        
        dataSize = buffer.getInt();
        unknown4 = buffer.getInt();
        numEntries = buffer.getInt();
        unknown6 = buffer.getInt();
    }
    
    public MARVEntry(int structureSize, int dataSize, int dataEntries, boolean b) {
        this.unknown1 = 0x00000001;
        this.structureSize = structureSize;
        this.unknown3 = b ? 0x4 : 0x10;
        this.dataSize = dataSize;
        this.unknown4 = dataSize == 0 ? 1 : 0x80;
        this.numEntries = dataEntries;
        
        this.unknown6 = this.numEntries == 0 ? structureSize + 0x20 : Utils.getPadded(structureSize, 0x80) + 0x20;
    }
    
    public byte[] getBytes() {
        byte[] data = new byte[0x20];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.putInt(MAGIC_VALUE);
        buffer.putInt(unknown1);
        buffer.putInt(structureSize);
        buffer.putInt(unknown3);
        
        buffer.putInt(dataSize);
        buffer.putInt(unknown4);
        buffer.putInt(numEntries);
        buffer.putInt(unknown6);
        
        return data;
    }
}
