package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class GenericPayload extends ResPayload {
    private byte[] data;
    
    public GenericPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        
        data = new byte[(int) (size == -1 ? source.getSize() : size)];
        
        for (int i = 0; i < data.length; i++)
            data[i] = source.readByte();
    }
    
    public GenericPayload(AbstractKCAP parent, byte[] data) {
        super(parent);
        this.data = data;
    }

    @Override
    public int getSize() {
        return data.length;
    }
    
    @Override
    public Payload getType() {
        return Payload.GENERIC;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        for (byte i : data)
            dest.writeByte(i);
    }

    public void setData(byte[] bytes) {
        this.data = bytes;
    }
    
    public byte[] getData() {
        return data;
    }
}
