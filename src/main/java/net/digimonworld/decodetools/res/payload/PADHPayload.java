package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class PADHPayload extends ResPayload {
    private int[] data; // TODO structure
    
    public PADHPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
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
        return Payload.PADH;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        for (int i : data)
            dest.writeInteger(i);
    }
}
