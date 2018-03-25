package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

/*
 * 4-byte   QSTM magic value (0x4D545351) 
 * 2-byte   unknown
 * 2-byte   unknown, number of attributes?
 * <QSTM entries>
 * 
 * QSTM entry:
 * 2-byte   attribute ID
 * 2-byte   data size
 * <data>   content depending on attribute ID
 * 
 * TODO implement proper structure
 */
public class QSTMPayload extends ResPayload {
    private int[] data;
    
    public QSTMPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
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
        return Payload.QSTM;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        for (int i : data)
            dest.writeInteger(i);
    }
}
