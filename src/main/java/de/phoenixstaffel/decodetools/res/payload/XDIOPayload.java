package de.phoenixstaffel.decodetools.res.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.ResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;
import de.phoenixstaffel.decodetools.res.payload.xdio.XDIOFace;
import de.phoenixstaffel.decodetools.res.payload.xdio.XDIOModes;

public class XDIOPayload extends ResPayload {
    // Magic Value
    private static final int VERSION = 2;
    private short unknown2_1;
    private short unknown2_2;
    // Data Pointer
    
    // Num Entries
    // Entry Size
    // Data Size -> Num Entries * Entry Size
    private int unknown3;
    
    private List<XDIOFace> data;
    
    public XDIOPayload(AbstractKCAP parent, List<XDIOFace> data, short unknown2_1, short unknown2_2, int unknown3) {
        super(parent);
        
        this.data = new ArrayList<>(data);
        this.unknown2_1 = unknown2_1; // 0x00033001
        this.unknown2_2 = unknown2_2;
        this.unknown3 = unknown3; // 0x00000005 or 0x00000004
    }
    
    public XDIOPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        this(source, dataStart, parent);
    }
    
    private XDIOPayload(Access source, int dataStart, AbstractKCAP parent) {
        super(parent);
        
        source.readInteger(); // magic value
        source.readInteger(); // Version
        unknown2_1 = source.readShort();
        unknown2_2 = source.readShort();
        int dataPointer = source.readInteger();
        
        int numEntries = source.readInteger();
        int entrySize = source.readInteger();
        int dataSize = source.readInteger();
        unknown3 = source.readInteger();
        
        data = new ArrayList<>();
        
        XDIOModes mode = XDIOModes.valueOf(entrySize);
        ByteBuffer b = ByteBuffer.wrap(source.readByteArray(dataSize, (long) dataPointer + dataStart));
        b.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int i = 0; i < numEntries / 3; i++)
            data.add(new XDIOFace(b, mode));
    }
    
    @Override
    public int getSize() {
        return 0x20;
    }
    
    @Override
    public Payload getType() {
        return Payload.XDIO;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        int max = data.stream().flatMapToInt(a -> IntStream.builder().add(a.getVert1()).add(a.getVert2()).add(a.getVert3()).build()).max().orElse(0);
        XDIOModes mode = XDIOModes.getFittingMode(max);
        
        byte[] array = new byte[data.size() * 3 * mode.getSize()];
        ByteBuffer buff = ByteBuffer.wrap(array);
        data.forEach(a -> a.write(buff, mode));
        
        int dataAddress = dataStream.add(array, false);
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeShort(unknown2_1);
        dest.writeShort(unknown2_2);
        dest.writeInteger(dataAddress);
        
        dest.writeInteger(data.size() * 3);
        dest.writeInteger(mode.getSize());
        dest.writeInteger(array.length);
        dest.writeInteger(unknown3);
    }
    
    @Override
    public void fillDummyResData(DummyResData resData) {
        int max = data.stream().flatMapToInt(a -> IntStream.builder().add(a.getVert1()).add(a.getVert2()).add(a.getVert3()).build()).max().orElse(0);
        XDIOModes mode = XDIOModes.getFittingMode(max);
        
        byte[] array = new byte[data.size() * 3 * mode.getSize()];
        resData.add(array, false);
    }
    
    public List<XDIOFace> getFaces() {
        return data;
    }
}
