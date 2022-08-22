package net.digimonworld.decodetools.res.payload.qstm;

import net.digimonworld.decodetools.core.Access;

public class QSTM01Entry implements QSTMEntry {
    private byte destId; // known good 1 2
    private byte srcId; // known good 0 1
    private byte size; // known good 1
    
    /*
     * 0 -> copy src to dest
     * 1 -> dest = -src
     * 2 -> dest = 1.0f / src
     * 3 -> dest = dest + src
     * 4 -> dest = dest - src
     * 5 -> dest = dest * src
     * 6 -> dest = dest / src
     * 7 -> dest = src - dest
     * 8 -> dest = src / dest
     */
    private byte mode; // known good 0
    
    public QSTM01Entry(Access source) {
        destId = source.readByte();
        srcId = source.readByte();
        size = source.readByte();
        mode = source.readByte();
    }
    
    @Override
    public QSTMEntryType getType() {
        return QSTMEntryType.UNK01;
    }
    
    @Override
    public short getSize() {
        return 8;
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(getType().getId());
        dest.writeShort((short) 4);
        
        dest.writeByte(destId);
        dest.writeByte(srcId);
        dest.writeByte(size);
        dest.writeByte(mode);
    }
}