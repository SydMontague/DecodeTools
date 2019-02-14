package de.phoenixstaffel.decodetools.res.payload.xtvo;

public enum XTVORegisterType {
    POSITION((short) 0, (short) 0),
    NORMAL((short) 1, (short) 3),
    COLOR((short) 2, (short) 1),
    IDX((short) 4, (short) 8), // bone ID
    WEIGHT((short) 5, (short) 9),
    TEXTURE0((short) 6, (short) 16),
    TEXTURE1((short) 7, (short) 16);
    
    private final short registerId;
    private final short unknown2;
    
    private XTVORegisterType(short registerId, short unknown2) {
        this.registerId = registerId;
        this.unknown2 = unknown2;
    }
    
    public static XTVORegisterType valueOf(short value) {
        for (XTVORegisterType entry : values())
            if (value == entry.getRegisterId())
                return entry;
            
        throw new IllegalArgumentException("No XTVORegisterType of value " + value + " found!");
    }
    
    public short getRegisterId() {
        return registerId;
    }
    
    public short getUnknown2() {
        return unknown2;
    }
    
}
