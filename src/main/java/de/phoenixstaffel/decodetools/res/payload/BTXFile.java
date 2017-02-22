package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.ResData;

public class BTXFile extends KCAPPayload {
    private short[] data;
    
    public BTXFile(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);
        
        data = new short[size / 2];
        
        for (int i = 0; i < data.length; i++)
            data[i] = source.readShort();
    }
    
    @Override
    public int getSize() {
        return data.length * 2;
    }
    
    @Override
    public int getAlignment() {
        return 0x4;
    }
    
    @Override
    public KCAPPayload.Payload getType() {
        return Payload.BTX;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        for (short i : data)
            dest.writeShort(i);
    }
}
