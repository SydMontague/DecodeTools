package de.phoenixstaffel.decodetools.res.payload;

import java.util.LinkedList;
import java.util.List;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Tuple;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

public class BTXPayload extends ResPayload {
    private static final String WRITE_ENCODING = "UTF-16LE";
    
    private List<Tuple<Integer, BTXEntry>> entries = new LinkedList<>();
    private int unknown;
    
    // TODO make cleaner/nicer
    public BTXPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
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
        
        unknown = source.readInteger(); 
        int numEntries = source.readInteger();
        
        List<Tuple<Integer, Long>> pointers = new LinkedList<>();
        
        for (int i = 0; i < numEntries; i++) {
            long pos = source.getPosition();
            int id = source.readInteger();
            
            pointers.add(new Tuple<>(id, source.readInteger() + pos));
        }
        
        List<String> strings = new LinkedList<>();
        
        for (Tuple<Integer, Long> entry : pointers) {
            long pointer = entry.getValue();
            StringBuilder builder = new StringBuilder();
            source.setPosition(pointer);
            
            char val;
            while((val = source.readChar()) != 0) {
                builder.append(val);
            }
            
            strings.add(builder.toString());
        }
        
        List<BTXMeta> metas = new LinkedList<>();
        if (postStart != 0) {
            source.setPosition(postStart + start);
            
            for (int i = 0; i < numEntries; i++)
                metas.add(new BTXMeta(source));
        }
        
        for (int i = 0; i < pointers.size(); i++) {
            entries.add(new Tuple<>(pointers.get(i).getKey(), new BTXEntry(strings.get(i), metas.size() > i ? metas.get(i) : null)));
        }
    }
    
    @Override
    public int getSize() {
        int size = 0x18 + entries.size() * 8;
        
        for (Tuple<Integer, BTXEntry> a : entries) {
            size += ((a.getValue().getString().length() + 1) * 2);
            
            if (a.getValue().getMeta() != null) {
                size += 0x30;
                size = Utils.align(size, 4);
            }
            else
                size += 2;
        }
        
        return size;
    }
    
    @Override
    public int getAlignment() {
        return 0x4;
    }
    
    @Override
    public ResPayload.Payload getType() {
        return Payload.BTX;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        if (entries.stream().anyMatch(a -> a.getValue().getMeta() != null))
            dest.writeInteger((int) (getSize() - entries.stream().filter(a -> a.getValue().getMeta() != null).count() * 0x30));
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(1); // version
        dest.writeInteger(entries.stream().anyMatch(a -> a.getValue().getMeta() != null) ? 0xC : 0x10);
        if (entries.stream().noneMatch(a -> a.getValue().getMeta() != null))
            dest.writeInteger(0); // padding
            
        dest.writeInteger(unknown);
        dest.writeInteger(entries.size());
        
        long pointer = dest.getPosition() + entries.size() * 8;
        
        for (Tuple<Integer, BTXEntry> a : entries) {
            dest.writeInteger(a.getKey());
            
            int lPointer = (int) (pointer - dest.getPosition() + 2);
            if (a.getValue().getMeta() != null)
                lPointer = Utils.align(lPointer, 4);
            else
                lPointer += 2;
            
            dest.writeInteger(lPointer);
            
            dest.writeString(a.getValue().getString() + "\0", WRITE_ENCODING, pointer);
            pointer += (a.getValue().getString().length() + 1) * 2;
            if (a.getValue().getMeta() != null)
                pointer = Utils.align(pointer, 4);
            else
                pointer += 2;
        }
        
        dest.setPosition(pointer);
        
        entries.stream().filter(a -> a.getValue().getMeta() != null).forEach(a -> a.getValue().getMeta().writeKCAP(dest));
    }
    
    public List<Tuple<Integer, BTXEntry>> getEntries() {
        return entries;
    }
    
    /**
     * Contains additional information for text strings, like the speaker for text messages.
     * 
     * Always 0x30 bytes.
     */
    public static class BTXMeta {
        private int id; // TODO shouldn't be stored but instead generated
        private int speaker;
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
            speaker = source.readInteger();
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
            dest.writeInteger(speaker);
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
        
        /**
         * Get the ID of the message this meta is associated with. 
         * 
         * @return the ID of the message this meta belong to
         */
        public int getId() {
            return id;
        }
        
        /**
         * Gets the id of the speaker associated to the message.
         * 
         * Their names are found in files 12-14 in the LanguageKeep_jp.res,
         * 0XXX -> File 12
         * 1XXX -> File 13
         * 2XXX -> File 14
         * 
         * @return the speaker ID
         */
        public int getSpeaker() {
            return speaker;
        }
    }
    
    /**
     * An entry to a BTX, containing a String and an optional {@link BTXMeta}.
     */
    public static class BTXEntry {
        private String string;
        private BTXMeta meta;
        
        public BTXEntry(String string, BTXMeta meta) {
            this.string = string;
            this.meta = meta;
        }
        
        /**
         * Gets the optional {@link BTXMeta} of this entry.
         * 
         * @return the BTXMeta of this entry, null if non-existent
         */
        public BTXMeta getMeta() {
            return meta;
        }
        
        /**
         * Gets the String of this entry.
         * 
         * @return the string of this entry
         */
        public String getString() {
            return string;
        }
        
        /**
         * Sets the String of this entry.
         * 
         * @param string the string to set update
         */
        public void setString(String string) {
            this.string = string;
        }
        
    }
}
