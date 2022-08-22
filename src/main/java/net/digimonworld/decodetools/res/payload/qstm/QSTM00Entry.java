package net.digimonworld.decodetools.res.payload.qstm;

import java.util.ArrayList;
import java.util.List;

import net.digimonworld.decodetools.core.Access;

public class QSTM00Entry implements QSTMEntry {
    private Axis axis;
    // see QSTM01 mode
    private byte mode;
    
    private List<Float> values = new ArrayList<>(); // 1 or 3 values
    
    public QSTM00Entry(Access source) {
        axis = Axis.fromByte(source.readByte());
        byte entryCount = source.readByte();
        mode = source.readByte();
        source.readByte(); // padding or always 0
        
        for (int i = 0; i < entryCount; i++)
            values.add(source.readFloat());
    }
    
    public Axis getAxis() {
        return axis;
    }
    
    public List<Float> getValues() {
        return values;
    }
    
    @Override
    public short getSize() {
        return (short) (8 + values.size() * 4);
    }
    
    @Override
    public QSTMEntryType getType() {
        return QSTMEntryType.UNK00;
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(getType().getId());
        dest.writeShort((short) (4 + values.size() * 4));
        
        dest.writeByte(axis.byteValue());
        dest.writeByte((byte) values.size());
        dest.writeByte(mode);
        dest.writeByte((byte) 0); // padding
        
        values.forEach(dest::writeFloat);
    }
}
