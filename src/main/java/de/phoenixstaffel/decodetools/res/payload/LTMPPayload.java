package de.phoenixstaffel.decodetools.res.payload;

import java.io.ByteArrayOutputStream;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class LTMPPayload extends KCAPPayload {
    private int[] data;
    
    public LTMPPayload(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);

        
        KCAPPayload p = this;
        while((p = p.getParent()) != null)
            System.out.print("  ");
        
        System.out.println(Long.toHexString(source.getPosition()) + " LTMR ");
        
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
        return Payload.LTMP;
    }

    @Override
    public void writeKCAP(Access dest, ByteArrayOutputStream dataStream) {
        for(int i : data)
            dest.writeInteger(i);
    }
}