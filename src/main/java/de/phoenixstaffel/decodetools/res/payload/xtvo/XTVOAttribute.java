package de.phoenixstaffel.decodetools.res.payload.xtvo;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;

public class XTVOAttribute implements Comparable<XTVOAttribute> {
    private XTVORegisterType registerId;
    private short unknown1; //stride/offset
    // short unknown2, attrib ID?
    private byte count;
    private XTVOValueType valueType;
    private float scale;
    
    public XTVOAttribute(XTVORegisterType registerId, short unknown1, byte count, XTVOValueType valueType, float scale) {
        this.registerId = registerId;
        this.unknown1 = unknown1;
        this.count = count;
        this.valueType = valueType;
        this.scale = scale;
    }
    
    public XTVOAttribute(Access source) {
        registerId = XTVORegisterType.valueOf(source.readShort());
        unknown1 = source.readShort();
        
        short unknown2 = source.readShort();
        if(unknown2 != registerId.getUnknown2())
            Main.LOGGER.warning(() -> "XTVOAttribute, expected to read " + registerId.getUnknown2() + ", but got " + unknown2);
        
        count = source.readByte();
        valueType = XTVOValueType.valueOf(source.readByte());
        scale = source.readFloat();
    }
    
    public void writeKCAP(Access dest) {
        dest.writeShort(registerId.getRegisterId());
        dest.writeShort(unknown1);
        dest.writeShort(registerId.getUnknown2());
        dest.writeByte(count);
        dest.writeByte(valueType.getValue());
        dest.writeFloat(scale);
    }
    
    public XTVORegisterType getRegisterId() {
        return registerId;
    }
    
    public byte getCount() {
        return count;
    }
    
    public short getStride() {
        return unknown1;
    }
    
    public XTVOValueType getValueType() {
        return valueType;
    }
    
    public float getValue(Number b) {
        switch (getValueType()) {
            case FLOAT:
                return b.floatValue() * scale;
            case BYTE:
                return b.byteValue() * scale;
            case UBYTE:
                return b.intValue() * scale;
            case SHORT:
                return b.shortValue() * scale;
        }
        return b.intValue() * scale;
    }
    
    @Override
    public int hashCode() {
        return registerId.getRegisterId();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof XTVOAttribute))
            return false;
        if (registerId != ((XTVOAttribute) obj).registerId)
            return false;
        
        return true;
    }

    @Override
    public int compareTo(XTVOAttribute o) {
        return getStride() - o.getStride(); 
    }
}
