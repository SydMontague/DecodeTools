package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class GenericPayload extends KCAPPayload {
    private int[] data;
    
    public GenericPayload(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);
        
        data = new int[(int) ((size == -1 ? source.getSize() : size) / 4)];
        
        for (int i = 0; i < data.length; i++)
            data[i] = source.readInteger();
    }
    
    @Override
    public int getSize() {
        return data.length * 4;
    }
    
    @Override
    public Payload getType() {
        return Payload.GENERIC;
    }
    
    @Override
    public int getAlignment() {
        return getParent().getGenericAlignment();
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        for (int i : data)
            dest.writeInteger(i);
    }
}
