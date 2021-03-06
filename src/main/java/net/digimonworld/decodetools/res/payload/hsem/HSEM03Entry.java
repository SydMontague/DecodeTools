package net.digimonworld.decodetools.res.payload.hsem;

import java.util.ArrayList;
import java.util.List;

import net.digimonworld.decodetools.core.Access;

//something with effects? and animations?
public class HSEM03Entry implements HSEMEntry {
    private byte unkn1;
    private byte unkn2;
    private short unkn3;
    
    private List<Float> values = new ArrayList<>();
    
    public HSEM03Entry(Access source) {
        this.unkn1 = source.readByte();
        this.unkn2 = source.readByte();
        this.unkn3 = source.readShort();
        
        // TODO not really proper :/
        for (int i = 0; i < unkn1 - 1; i++)
            values.add(source.readFloat());
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort((short) getHSEMType().getId());
        dest.writeShort((short) getSize());
        
        dest.writeByte(unkn1);
        dest.writeByte(unkn2);
        dest.writeShort(unkn3);
        
        values.forEach(dest::writeFloat);
    }
    
    @Override
    public int getSize() {
        return 0x08 + 0x04 * values.size();
    }
    
    @Override
    public HSEMEntryType getHSEMType() {
        return HSEMEntryType.UNK03;
    }
    
    @Override
    public String toString() {
        return String.format("Entry03 | U1: %s | U2: %s | U3: %s", unkn1, unkn2, unkn3);
    }
}
