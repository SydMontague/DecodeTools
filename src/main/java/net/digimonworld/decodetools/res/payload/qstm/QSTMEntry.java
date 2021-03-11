package net.digimonworld.decodetools.res.payload.qstm;

import net.digimonworld.decodetools.core.Access;

public interface QSTMEntry {
    public void writeKCAP(Access dest);
    
    public short getSize();
    
    public QSTMEntryType getType();
    
    public static QSTMEntry loadEntry(Access source) {
        short id = source.readShort();
        source.readShort(); // size
        
        switch(id) {
            case 0:
                return new QSTM00Entry(source);
            case 1:
                return new QSTM01Entry(source);
            case 2:
                return new QSTM02Entry(source);
            default:
                throw new IllegalArgumentException("Unknown QSTMEntry: " + id + " at " + Long.toHexString(source.getPosition()));
        }
    }
}