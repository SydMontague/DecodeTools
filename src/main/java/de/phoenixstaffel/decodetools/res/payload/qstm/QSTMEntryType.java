package de.phoenixstaffel.decodetools.res.payload.qstm;

public enum QSTMEntryType {
    UNK00((short) 0),
    UNK01((short) 1),
    UNK02((short) 2);
    
    private short id;
    
    private QSTMEntryType(short id) {
        this.id = id;
    }
    
    public short getId() {
        return id;
    }
}