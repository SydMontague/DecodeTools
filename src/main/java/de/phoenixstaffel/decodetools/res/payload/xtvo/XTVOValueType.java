package de.phoenixstaffel.decodetools.res.payload.xtvo;

import java.nio.ByteBuffer;

public enum XTVOValueType {
    BYTE((byte) 1),
    SHORT((byte) 2),
    UBYTE((byte) 4),
    FLOAT((byte) 8);
    
    private final byte value;
    
    private XTVOValueType(byte value) {
        this.value = value;
    }
    
    public static XTVOValueType valueOf(byte value) {
        for (XTVOValueType entry : values())
            if (value == entry.getValue())
                return entry;
            
        throw new IllegalArgumentException("No XTVOValueType of value " + value + " found!");
    }
    
    public byte getValue() {
        return value;
    }
    
    public int getAlignment() {
        switch (this) {
            case BYTE:
            case UBYTE:
                return 1;
            case FLOAT:
                return 4;
            case SHORT:
                return 2;
        }
        return 1;
    }
    
    public Number read(ByteBuffer source) {
        switch (this) {
            case BYTE:
                return source.get();
            case UBYTE:
                return Byte.toUnsignedInt(source.get());
            case FLOAT:
                return source.getFloat();
            case SHORT:
                return Short.toUnsignedInt(source.getShort());
        }
        
        return null;
    }
    
    public void write(ByteBuffer buff, Number b) {
        switch (this) {
            case BYTE:
                buff.put(b.byteValue());
                break;
            case UBYTE:
                buff.put(b.byteValue());
                break;
            case SHORT:
                buff.putShort(b.shortValue());
                break;
            case FLOAT:
                buff.putFloat(b.floatValue());
                break;
        }
    }
}
