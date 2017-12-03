package de.phoenixstaffel.decodetools.res.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.xtvo.XTVOAttribute;
import de.phoenixstaffel.decodetools.res.payload.xtvo.XTVOVertex;

public class XTVOPayload extends ResPayload {
    private int unknown1;
    private short unknown2;
    private short id;
    private int dataPointer;
    
    private int numEntries;
    private int entrySize;
    private int dataSize;
    private int unknown7;
    
    private int shaderId;
    private int unknown9;
    private int unknown10;
    private int shaderVariablesSize;
    
    private short attributeCount;
    private short unknown12;
    
    private float[] mTex0 = new float[4];
    private float[] mTex1 = new float[4];
    private float[] mTex2 = new float[4];
    private float[] mTex3 = new float[4];
    
    private List<XTVOAttribute> attributes = new ArrayList<>();
    
    private List<XTVOVertex> data = new ArrayList<>();
    
    public XTVOPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        this(source, dataStart, parent);
    }
    
    private XTVOPayload(Access source, int dataStart, KCAPPayload parent) {
        super(parent);
        
        source.readInteger(); // magic value
        unknown1 = source.readInteger();
        unknown2 = source.readShort();
        id = source.readShort();
        dataPointer = source.readInteger();
        
        numEntries = source.readInteger();
        entrySize = source.readInteger();
        dataSize = source.readInteger();
        unknown7 = source.readInteger();
        
        shaderId = source.readInteger();
        unknown9 = source.readInteger();
        unknown10 = source.readInteger();
        shaderVariablesSize = source.readInteger();
        
        attributeCount = source.readShort();
        unknown12 = source.readShort();
        
        for (int i = 0; i < 4; i++)
            mTex0[i] = source.readFloat();
        for (int i = 0; i < 4; i++)
            mTex1[i] = source.readFloat();
        for (int i = 0; i < 4; i++)
            mTex2[i] = source.readFloat();
        for (int i = 0; i < 4; i++)
            mTex3[i] = source.readFloat();
        
        for (int i = 0; i < attributeCount; i++)
            attributes.add(new XTVOAttribute(source));
        
        ByteBuffer b = ByteBuffer.wrap(source.readByteArray(dataSize, (long) dataStart + dataPointer));
        b.order(ByteOrder.LITTLE_ENDIAN);
        
        for(int i = 0; i < numEntries; i++) {
            byte[] buffArray = new byte[entrySize];
            b.get(buffArray);
            ByteBuffer buff = ByteBuffer.wrap(buffArray);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            data.add(new XTVOVertex(buff, attributes));
        }
    }
    
    @Override
    public int getSize() {
        return 0x30 + shaderVariablesSize;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.XTVO;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        int size = 0;
        for (XTVOAttribute attr : attributes) {
            size = Utils.getPadded(size, attr.getValueType().getAlignment());
            size += attr.getCount() * attr.getValueType().getAlignment();
        }
        
        size = Utils.getPadded(size, 2);
        
        byte[] array = new byte[data.size() * size];
        ByteBuffer buff = ByteBuffer.wrap(array);
        data.forEach(a -> buff.put(a.write()));
        
        int dataAddress = dataStream.add(array, false, getParent());
        
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
        dest.writeInteger(shaderVariablesSize);
        
        dest.writeShort(attributeCount);
        dest.writeShort(unknown12);
        
        for (float f : mTex0)
            dest.writeFloat(f);
        for (float f : mTex1)
            dest.writeFloat(f);
        for (float f : mTex2)
            dest.writeFloat(f);
        for (float f : mTex3)
            dest.writeFloat(f);
        
        for (XTVOAttribute attr : attributes)
            attr.writeKCAP(dest);
    }
    
    @Override
    public void fillDummyResData(DummyResData resData) {
        int size = 0;
        for (XTVOAttribute attr : attributes) {
            size = Utils.getPadded(size, attr.getValueType().getAlignment());
            size += attr.getCount() * attr.getValueType().getAlignment();
        }
        
        size = Utils.getPadded(size, 2);
        
        byte[] array = new byte[data.size() * size];
        resData.add(array, false, getParent());
    }
    
    public List<XTVOVertex> getVertices() {
        return data;
    }
    
    public List<XTVOAttribute> getAttributes() {
        return attributes;
    }
    
    public short getId() {
        return id;
    }
}
