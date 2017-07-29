package de.phoenixstaffel.decodetools.res.payload.xtvo;

public enum XTVORegisterType {
    POSITION((short) 0),
    NORMAL((short) 1),
    COLOR((short) 2),
    IDX((short) 4),
    WEIGHT((short) 5),
    TEXTURE0((short) 6),
    TEXTURE1((short) 7);
    
    private final short registerId;
    
    private XTVORegisterType(short registerId) {
        this.registerId = registerId;
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
    
}
