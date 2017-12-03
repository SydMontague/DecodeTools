package de.phoenixstaffel.decodetools.res.payload.hsem;

import java.util.ArrayList;
import java.util.List;

import de.phoenixstaffel.decodetools.core.Access;

//something with effects? and animations?
public class HSEM03Entry implements HSEMEntryPayload {
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
        dest.writeByte(unkn1);
        dest.writeByte(unkn2);
        dest.writeShort(unkn3);
        
        values.forEach(dest::writeFloat);
    }
    
    @Override
    public int getSize() {
        return 0x04 + 0x04 * values.size();
    }
    
}
