package net.digimonworld.decodetools.res.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.DummyResData;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOAttribute;
import net.digimonworld.decodetools.res.payload.xtvo.XTVORegisterType;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOVertex;

public class XTVOPayload extends ResPayload {
    private final long dataStartOnLoad;
    
    // int magic value
    private int unknown1; // 5? version?
    private short unknown2; // 0x3001?
    private short id; // ?
    // int dataPointer
    
    // int numEntries
    // int entrySize
    // int dataSize
    private int unknown7; // 0x00010309?
    
    private int shaderId;
    private int unknown9; // 0x73?
    private int unknown10; // 1?
    // int shaderVariablesSize
    
    // short attributeCount
    // short attributePtr, always 0x74?
    
    /*
     * Each mTex array gets used to build a mTex matrix in the shader.
     * The final texture coordinate is calculated by creating the dot product of each
     * [ mTex[2], 0, 0, mTex[0] ]
     * [ 0, mTex[3], 0, mTex[1] ]
     * [ 0, 0, 0, 0 ]
     * [ 0, 0, 0, 0 ]
     */
    private float[] mTex0 = { 0.0f, 0.0f, 1.0f, 1.0f };
    private float[] mTex1 = { 0.0f, 0.0f, 1.0f, 1.0f };
    private float[] mTex2 = { 0.0f, 0.0f, 1.0f, 1.0f };
    private float[] mTex3 = { 0.0f, 0.0f, 1.0f, 1.0f };
    
    private Map<XTVORegisterType, XTVOAttribute> attributes = new EnumMap<>(XTVORegisterType.class);
    
    private List<XTVOVertex> data = new ArrayList<>();
    
    public XTVOPayload(AbstractKCAP parent, List<XTVOAttribute> attributes, List<XTVOVertex> data, int shaderId, int unknown1, short unknown2, short id,
            int unknown7, int unknown9, int unknown10) {
        super(parent);
        
        attributes.forEach(a -> this.attributes.put(a.getRegisterId(), a));
        this.data = new ArrayList<>(data);
        this.shaderId = shaderId;
        this.id = id;
        this.unknown1 = unknown1;
        this.unknown2 = unknown2;
        this.unknown7 = unknown7;
        this.unknown9 = unknown9;
        this.unknown10 = unknown10;
        
        dataStartOnLoad = 0;
    }
    
    public XTVOPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        this(source, dataStart, parent);
    }
    
    private XTVOPayload(Access source, int dataStart, AbstractKCAP parent) {
        super(parent);
        
        source.readInteger(); // magic value
        unknown1 = source.readInteger();
        unknown2 = source.readShort();
        id = source.readShort();
        int dataPointer = source.readInteger();
        
        int numEntries = source.readInteger();
        int entrySize = source.readInteger();
        int dataSize = source.readInteger();
        unknown7 = source.readInteger();
        
        shaderId = source.readInteger();
        unknown9 = source.readInteger();
        unknown10 = source.readInteger();
        source.readInteger(); // shaderVariableSize
        
        short attributeCount = source.readShort();
        source.readShort(); // attributePtr, always 0x74?
        
        for (int i = 0; i < 4; i++)
            mTex0[i] = source.readFloat();
        for (int i = 0; i < 4; i++)
            mTex1[i] = source.readFloat();
        for (int i = 0; i < 4; i++)
            mTex2[i] = source.readFloat();
        for (int i = 0; i < 4; i++)
            mTex3[i] = source.readFloat();
        
        for (int i = 0; i < attributeCount; i++) {
            XTVOAttribute attrib = new XTVOAttribute(source);
            attributes.put(attrib.getRegisterId(), attrib);
        }
        
        ByteBuffer b = ByteBuffer.wrap(source.readByteArray(dataSize, (long) dataStart + dataPointer));
        b.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int i = 0; i < numEntries; i++) {
            byte[] buffArray = new byte[entrySize];
            b.get(buffArray);
            ByteBuffer buff = ByteBuffer.wrap(buffArray);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            data.add(new XTVOVertex(buff, attributes.values()));
        }
        
        dataStartOnLoad = (long) dataStart + dataPointer;
    }
    
    @Override
    public int getSize() {
        return 0x74 + attributes.size() * 0xC;
    }
    
    @Override
    public Payload getType() {
        return Payload.XTVO;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        int size = 0;
        for (XTVOAttribute attr : attributes.values()) {
            size = Utils.align(size, attr.getValueType().getAlignment());
            size += attr.getCount() * attr.getValueType().getAlignment();
        }
        
        size = Utils.align(size, 2);
        
        byte[] array = new byte[data.size() * size];
        ByteBuffer buff = ByteBuffer.wrap(array);
        data.forEach(a -> buff.put(a.write()));
        
        int dataAddress = dataStream.add(array, false);
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(unknown1);
        dest.writeShort(unknown2);
        dest.writeShort(id);
        dest.writeInteger(dataAddress);
        
        dest.writeInteger(data.size());
        dest.writeInteger(size);
        dest.writeInteger(array.length);
        dest.writeInteger(unknown7);
        
        dest.writeInteger(shaderId);
        dest.writeInteger(unknown9);
        dest.writeInteger(unknown10);
        dest.writeInteger(getSize() - 0x30);
        
        dest.writeShort((short) attributes.size());
        dest.writeShort((short) 0x74); //
        
        for (float f : mTex0)
            dest.writeFloat(f);
        for (float f : mTex1)
            dest.writeFloat(f);
        for (float f : mTex2)
            dest.writeFloat(f);
        for (float f : mTex3)
            dest.writeFloat(f);
        
        for (XTVOAttribute attr : attributes.values())
            attr.writeKCAP(dest);
    }
    
    @Override
    public void fillDummyResData(DummyResData resData) {
        int size = 0;
        for (XTVOAttribute attr : attributes.values()) {
            size = Utils.align(size, attr.getValueType().getAlignment());
            size += attr.getCount() * attr.getValueType().getAlignment();
        }
        
        size = Utils.align(size, 2);
        
        byte[] array = new byte[data.size() * size];
        resData.add(array, false);
    }
    
    public List<XTVOVertex> getVertices() {
        return data;
    }
    
    public Optional<XTVOAttribute> getAttribute(XTVORegisterType type) {
        return Optional.ofNullable(attributes.get(type));
    }
    
    public Map<XTVORegisterType, XTVOAttribute> getAttributes() {
        return attributes;
    }
    
    public short getId() {
        return id;
    }
    
    public float[] getMTex0() {
        return mTex0;
    }
    
    public float[] getMTex1() {
        return mTex1;
    }
    
    public float[] getMTex2() {
        return mTex2;
    }
    
    public float[] getMTex3() {
        return mTex3;
    }
    
    public void setMTex0(float[] mTex0) {
        this.mTex0 = mTex0;
    }
    
    public void setMTex1(float[] mTex1) {
        this.mTex1 = mTex1;
    }
    
    public void setMTex2(float[] mTex2) {
        this.mTex2 = mTex2;
    }
    
    public void setMTex3(float[] mTex3) {
        this.mTex3 = mTex3;
    }
    
    @Override
    public String toString() {
        return super.toString() + " 0x" + Long.toHexString(dataStartOnLoad).toUpperCase();
    }
}
