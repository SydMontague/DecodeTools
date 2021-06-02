package net.digimonworld.decodetools.res.payload.qstm;

import net.digimonworld.decodetools.core.Access;

public class QSTM02Entry implements QSTMEntry {
    private Axis axis;
    private int vctmId;
    
    public QSTM02Entry(Access source) {
        this.axis = Axis.fromByte((byte) source.readInteger());
        this.vctmId = source.readInteger();
    }
    
    @Override
    public short getSize() {
        return 12;
    }
    
    @Override
    public QSTMEntryType getType() {
        return QSTMEntryType.UNK02;
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(getType().getId());
        dest.writeShort((short) 8);
        
        dest.writeInteger(axis.ordinal());
        dest.writeInteger(vctmId);
    }
}