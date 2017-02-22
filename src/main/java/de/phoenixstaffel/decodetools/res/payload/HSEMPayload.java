package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.ResData;

public class HSEMPayload extends KCAPPayload {
    private int[] data;
    
    public HSEMPayload(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);
        
        data = new int[size / 4];
        
        for (int i = 0; i < data.length; i++)
            data[i] = source.readInteger();
    }
    
    @Override
    public int getSize() {
        return data.length * 4;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.HSEM;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        for (int i : data)
            dest.writeInteger(i);
    }
}
