package de.phoenixstaffel.decodetools.res.payload.qstm;

import java.util.ArrayList;
import java.util.List;

import de.phoenixstaffel.decodetools.core.Access;

public class QSTM00Entry implements QSTMEntry {
    private byte unk1;

    private List<Float> values = new ArrayList<>();
    
    public QSTM00Entry(Access source) {
        unk1 = source.readByte();
        byte entryCount = source.readByte();
        source.readShort(); // padding
        
        for(int i = 0; i < entryCount; i++)
            values.add(source.readFloat());
    }
    
    @Override
    public short getSize() {
        return (short) (4 + values.size() * 4);
    }
    
    @Override
    public QSTMEntryType getType() {
        return QSTMEntryType.UNK00;
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(getType().getId());
        dest.writeShort(getSize());
        
        dest.writeByte(unk1);
        dest.writeByte((byte) values.size());
        values.forEach(dest::writeFloat);
    }
}