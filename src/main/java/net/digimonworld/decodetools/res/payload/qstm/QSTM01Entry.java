package net.digimonworld.decodetools.res.payload.qstm;

import net.digimonworld.decodetools.core.Access;

public class QSTM01Entry implements QSTMEntry {
    private byte unk1; // known good 1 2
    private byte unk2; // known good 0 1
    private byte unk3; // known good 1
    private byte unk4; // known good 0
    
    public QSTM01Entry(Access source) {
        unk1 = source.readByte();
        unk2 = source.readByte();
        unk3 = source.readByte();
        unk4 = source.readByte();
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
        
        dest.writeByte(unk1);
        dest.writeByte(unk2);
        dest.writeByte(unk3);
        dest.writeByte(unk4);
    }
}