package net.digimonworld.decodetools.res.payload;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

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
    public void writeKCAP(Access dest, ResData dataStream) {
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
