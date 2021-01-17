package de.phoenixstaffel.decodetools.dataminer;

import java.util.Map;
import java.util.function.BiFunction;

import de.phoenixstaffel.decodetools.core.Access;

public enum StructureType {
    BYTE((a, b) -> a.readByte()),
    UBYTE((a, b) -> Byte.toUnsignedInt(a.readByte())),
    SHORT((a,b) -> a.readShort()),
    USHORT((a,b) -> Short.toUnsignedInt(a.readShort())),
    INT((a,b) -> a.readInteger()),
    LONG((a,b) -> a.readLong()),
    FLOAT((a,b) -> a.readFloat()),
    DOUBLE((a,b) -> a.readDouble()),
    STRING((a,b) -> {
        int length = Integer.parseInt(b.getOrDefault("Length", "0").toString());
        String encoding = b.getOrDefault("Encoding", "UTF-8").toString();
        boolean trim = Boolean.parseBoolean(b.getOrDefault("Trim", false).toString());
        
        String string = a.readString(length, encoding);
        return trim ? string.trim() : string;
    });

    private final BiFunction<Access, Map<String, Object>, Object> function;
    
    private StructureType(BiFunction<Access, Map<String, Object>, Object> function) {
        this.function = function;
    }
    
    public Object readFrom(Access source, Map<String, Object> extra) {
        return function.apply(source, extra);
    }
}