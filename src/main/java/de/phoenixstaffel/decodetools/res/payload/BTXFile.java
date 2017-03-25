package de.phoenixstaffel.decodetools.res.payload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class BTXFile extends KCAPPayload {
    private static final String READ_ENCODING = "UTF-16BE";
    private static final String WRITE_ENCODING = "UTF-16LE";
    private static final Charset cset = Charset.forName(READ_ENCODING);
    
    private Map<Integer, BTXEntry> entries = new LinkedHashMap<>();
    
    //TODO make cleaner/nicer
    public BTXFile(Access source, int dataStart, KCAPFile parent, int size) {
        super(parent);
        long start = source.getPosition();
        int postStart = 0;
        
        int magic = source.readInteger();
        if (magic != getType().getMagicValue()) {
            postStart = magic;
            source.readInteger(); // magic value
        }
        
        source.readInteger(); // always 1
        int headerSize = source.readInteger();
        
        source.setPosition(start + headerSize + (postStart == 0 ? 0 : 4));
        
        source.readInteger(); // always 1
        int numEntries = source.readInteger();
        
        Map<Integer, Long> pointers = new LinkedHashMap<>();
        
        for (int i = 0; i < numEntries; i++) {
            long pos = source.getPosition();
            int id = source.readInteger();
            
            //FIXME ignores Collisions, causing inconsistencies – compromised data or bug?
            if (pointers.put(id, source.readInteger() + pos) != null)
                Main.LOGGER.warning("Collision: " + id);
        }
        
        Map<Integer, String> strings = new LinkedHashMap<>();
        
        for (Entry<Integer, Long> entry : pointers.entrySet()) {
            long pointer = entry.getValue();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            source.setPosition(pointer);
            
            short val;
            do {
                val = source.readShort();
                byte[] arr = new byte[] { (byte) (val >> 8), (byte) (val & 0xFF) };
                try {
                    stream.write(arr);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            } while (val != 0);
            
            ByteBuffer b = ByteBuffer.wrap(stream.toByteArray());
            strings.put(entry.getKey(), cset.decode(b).toString());
        }
        
        Map<Integer, BTXMeta> metas = new LinkedHashMap<>();
        if (postStart != 0) {
            source.setPosition(postStart + start);
            
            for (int i = 0; i < numEntries; i++) {
                BTXMeta meta = new BTXMeta(source);
                metas.put(meta.getId(), meta);
            }
        }
        
        for (int entry : pointers.keySet()){
            entries.put(entry, new BTXEntry(strings.get(entry), metas.get(entry)));
        }
    }
    
    @Override
    public int getSize() {
        int size = 0x18 + entries.size() * 8;
        
        for (Entry<Integer, BTXEntry> a : entries.entrySet()) {
            size += 2 + (a.getValue().getString().length() * 2);
            
            if (a.getValue().getMeta() != null)
                size += 0x30;
        }
        
        return size;
    }
    
    @Override
    public int getAlignment() {
        return 0x4;
    }
    
    @Override
    public KCAPPayload.Payload getType() {
        return Payload.BTX;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        if (entries.entrySet().stream().anyMatch(a -> a.getValue().getMeta() != null))
            dest.writeInteger((int) (getSize() - entries.entrySet().stream().filter(a -> a.getValue().getMeta() != null).count() * 0x30));
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(1); // version
        dest.writeInteger(entries.entrySet().stream().anyMatch(a -> a.getValue().getMeta() != null) ? 0xC : 0x10);
        if (entries.entrySet().stream().noneMatch(a -> a.getValue().getMeta() != null))
            dest.writeInteger(0); // padding
            
        dest.writeInteger(1);
        dest.writeInteger(entries.size());
        
        long pointer = dest.getPosition() + entries.size() * 8;
        
        for (Entry<Integer, BTXEntry> a : entries.entrySet()) {
            dest.writeInteger(a.getKey());
            dest.writeInteger((int) (pointer - dest.getPosition() + 4));
            
            dest.writeString(a.getValue().getString(), WRITE_ENCODING, pointer);
            pointer += 2 + a.getValue().getString().length() * 2;
        }
        
        dest.setPosition(pointer);
        
        entries.entrySet().stream().filter(a -> a.getValue().getMeta() != null).forEach(a -> a.getValue().getMeta().writeKCAP(dest));
    }
    
    static class BTXMeta {
        //for textboxes
        
        private int id; //???
        private int unknown1; //actor sprite
        private int unknown2;
        private int unknown3;
        
        private int unknown4;
        private int unknown5;
        private int unknown6;
        private int unknown7;
        
        private int unknown8;
        private int unknown9;
        private int unknown10;
        private int unknown11;
        
        public BTXMeta(Access source) {
            id = source.readInteger();
            unknown1 = source.readInteger();
            unknown2 = source.readInteger();
            unknown3 = source.readInteger();
            
            unknown4 = source.readInteger();
            unknown5 = source.readInteger();
            unknown6 = source.readInteger();
            unknown7 = source.readInteger();
            
            unknown8 = source.readInteger();
            unknown9 = source.readInteger();
            unknown10 = source.readInteger();
            unknown11 = source.readInteger();
        }
        
        public void writeKCAP(Access dest) {
            dest.writeInteger(id);
            dest.writeInteger(unknown1);
            dest.writeInteger(unknown2);
            dest.writeInteger(unknown3);
            
            dest.writeInteger(unknown4);
            dest.writeInteger(unknown5);
            dest.writeInteger(unknown6);
            dest.writeInteger(unknown7);
            
            dest.writeInteger(unknown8);
            dest.writeInteger(unknown9);
            dest.writeInteger(unknown10);
            dest.writeInteger(unknown11);
        }
        
        public int getId() {
            return id;
        }
    }
    
    static class BTXEntry {
        private String string;
        private BTXMeta meta;
        
        public BTXEntry(String string, BTXMeta meta) {
            this.string = string;
            this.meta = meta;
        }
        
        public BTXMeta getMeta() {
            return meta;
        }
        
        public String getString() {
            return string;
        }
        
        public void setString(String string) {
            this.string = string;
        }
        
    }
}
