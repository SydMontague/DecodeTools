package de.phoenixstaffel.decodetools.res.payload;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.NameablePayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

/*
 * Shading Type:
 * 1 -> flat shading (no lighting) | with lighting section
 * 2 -> regular shading (lighting) | with lighting section
 * 3 -> toon shading (lighting, uses texture #2 as toon shader) | with lighting section
 * 4 -> unshaded (completely white) | without lighting section
 * 
 * Unknown Type:
 * 0 -> unknownSection size 0
 * 1 -> unknownSection size 8 (two values)
 */
public class LRTMPayload extends NameablePayload {
    private int index;
    private LRTMShadingType shadingType; // shading type? 4 = unshaded? 0-4
    private LRTMUnkownType unknownType; // ??? type? 0-1
    private int colorFilter; // ?
    
    private int color1; // ambient?
    private int color2; // specular?
    private int color3; // emission?
    
    private int color4;
    private int color5;
    
    
    public LRTMPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent, name);
        
        index = source.readInteger();
        shadingType = LRTMShadingType.valueOf(source.readShort());
        unknownType = LRTMUnkownType.valueOf(source.readShort());
        colorFilter = source.readInteger();
        short lightingSize = source.readShort();
        source.readShort(); // lightingPointer
        
        short unknownSize = source.readShort();
        source.readShort(); // unknownPointer
        
        source.readInteger(); // padding
        source.readInteger(); // padding
        source.readInteger(); // padding
        
        if(lightingSize == 0x0C) {
            color1 = source.readInteger();
            color2 = source.readInteger();
            color3 = source.readInteger();
        }
        if(unknownSize == 0x08) {
            color4 = source.readInteger();
            color5 = source.readInteger();
        }
    }
    
    @Override
    public int getSize() {
        int lSize = shadingType.hasData() ? 0x0C : 0;
        int uSize = unknownType.hasData() ? 0x08 : 0;
        
        return 0x20 + lSize + uSize;
    }
    
    @Override
    public Payload getType() {
        return Payload.LRTM;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(index);
        dest.writeShort(shadingType.getId());
        dest.writeShort(unknownType.getId());
        dest.writeInteger(colorFilter);
        
        dest.writeShort(shadingType.hasData() ? (short) 0x0C : 0);
        dest.writeShort((short) 0x20);
        
        dest.writeShort(unknownType.hasData() ? (short) 0x08 : 0);
        dest.writeShort(shadingType.hasData() ? (short) 0x2C : (short) 0x20);
        
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        dest.writeInteger(0); // padding
        
        if(shadingType.hasData()) {
            dest.writeInteger(color1);
            dest.writeInteger(color2);
            dest.writeInteger(color3);
        }
        if(unknownType.hasData()) {
            dest.writeInteger(color4);
            dest.writeInteger(color5);
        }
    }
    
    public int getColor1() {
        return color1;
    }
    
    public int getColor2() {
        return color2;
    }
    
    public int getColor3() {
        return color3;
    }
    
    public int getColor4() {
        return color4;
    }
    
    public int getColor5() {
        return color5;
    }
    
    public int getColorFilter() {
        return colorFilter;
    }
    
    public LRTMShadingType getShadingType() {
        return shadingType;
    }
    
    public LRTMUnkownType getUnknownType() {
        return unknownType;
    }
    
    public void setColor1(int color) {
        this.color1 = color;
    }
    
    public void setColor2(int color) {
        this.color2 = color;
    }
    
    public void setColor3(int color) {
        this.color3 = color;
    }
    
    public void setColor4(int color) {
        this.color4 = color;
    }
    
    public void setColor5(int color) {
        this.color5 = color;
    }
    
    public void setColorFilter(int color) {
        this.colorFilter = color;
    }
    
    public void setShadingType(LRTMShadingType shadingType) {
        this.shadingType = shadingType;
    }
    
    public void setUnknownType(LRTMUnkownType unknownType) {
        this.unknownType = unknownType;
    }
    
    public enum LRTMShadingType {
        FLAT((short) 1, true),
        REGULAR((short) 2, true),
        TOON((short) 3, true),
        UNSHADED((short) 4, false);
        
        private final short id;
        private boolean hasData;
        
        private LRTMShadingType(short id, boolean hasData) {
            this.id = id;
            this.hasData = hasData;
        }
        
        public short getId() {
            return id;
        }
        
        public boolean hasData() {
            return hasData;
        }
        
        public static LRTMShadingType valueOf(short id) {
            for(LRTMShadingType val : values())
                if(val.getId() == id)
                    return val;
            
            return FLAT;
        }
    }
    
    public enum LRTMUnkownType {
        VAL0((short) 0, false),
        VAL1((short) 1, true);
        
        private final short id;
        private boolean hasData;
        
        private LRTMUnkownType(short id, boolean hasData) {
            this.id = id;
            this.hasData = hasData;
        }
        
        public short getId() {
            return id;
        }
        
        public boolean hasData() {
            return hasData;
        }
        
        public static LRTMUnkownType valueOf(short id) {
            for(LRTMUnkownType val : values())
                if(val.getId() == id)
                    return val;
            
            return VAL0;
        }
    }
}
