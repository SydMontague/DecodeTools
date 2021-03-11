package net.digimonworld.decodetools.res.payload.xdio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

import net.digimonworld.decodetools.Main;

public enum XDIOModes {
    BYTE(a -> Byte.toUnsignedInt(a.get()), a -> ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(a.byteValue()).array()),
    SHORT(a -> Short.toUnsignedInt(a.getShort()), a -> ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(a.shortValue()).array()),
    INT(a -> a.getInt(), a -> ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(a.intValue()).array());
    
    private final Function<ByteBuffer, Integer> readFunction;
    private final Function<Integer, byte[]> writeFunction;
    
    private XDIOModes(Function<ByteBuffer, Integer> f, Function<Integer, byte[]> ff) {
        this.readFunction = f;
        this.writeFunction = ff;
    }
    
    public static XDIOModes getFittingMode(int max) {
        if (max > 0xFFFF || max < 0)
            return INT;
        else if (max > 0xFF)
            return SHORT;
        
        return BYTE;
    }
    
    public Function<ByteBuffer, Integer> getReadFunction() {
        return readFunction;
    }
    
    public Function<Integer, byte[]> getWriteFunction() {
        return writeFunction;
    }
    
    public static XDIOModes valueOf(int size) {
        switch (size) {
            case 1:
                return BYTE;
            case 2:
                return SHORT;
            case 4:
                return INT;
            default:
                Main.LOGGER.severe(() -> "Unknown XDIO entry size detected: " + size);
                return null;
        }
    }
    
    public int getSize() {
        switch (this) {
            case BYTE:
                return 1;
            case SHORT:
                return 2;
            case INT:
                return 4;
        }
        
        return 4;
    }
}
