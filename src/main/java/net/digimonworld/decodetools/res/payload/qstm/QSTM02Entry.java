package net.digimonworld.decodetools.res.payload.qstm;

import net.digimonworld.decodetools.core.Access;

public class QSTM02Entry implements QSTMEntry {
    private Unk1Value unk1;
    private int vctmId;
    
    public QSTM02Entry(Access source) {
        this.unk1 = Unk1Value.values()[source.readInteger()];
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
        
        dest.writeInteger(unk1.ordinal());
        dest.writeInteger(vctmId);
    }
    
    enum Unk1Value {
        VAL_0,
        VAL_1,
        VAL_2,
    }
}