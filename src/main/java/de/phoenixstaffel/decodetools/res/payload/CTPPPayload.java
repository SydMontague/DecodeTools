package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.ResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class CTPPPayload extends ResPayload {
    private int[] data; // TODO structure
    
    public CTPPPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
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
    public Payload getType() {
        return Payload.CTPP;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        for (int i : data)
            dest.writeInteger(i);
    }
}
