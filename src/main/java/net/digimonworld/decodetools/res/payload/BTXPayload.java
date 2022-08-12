package net.digimonworld.decodetools.res.payload;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.Tuple;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.ResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

public class BTXPayload extends ResPayload {
    private static final String WRITE_ENCODING = "UTF-16LE";
    
    private final int fileId;
    
    private List<Tuple<Integer, BTXEntry>> entries = new LinkedList<>();
    
    // TODO make cleaner/nicer
    public BTXPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        this.fileId = parent != null ? parent.getEntryCount() : 0;
        
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
            while ((val = source.readChar()) != 0) {
                builder.append(val);
            }
            
            char secondNull = source.readChar();
            if (postStart == 0 && secondNull != 0x0000) // read final 0x0000
                Main.LOGGER.warning(() -> "Tried reading the second terminator char (0x0000) but got " + secondNull);
            
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
    
    public BTXPayload(AbstractKCAP parent) {
        super(parent);
        this.fileId = parent != null ? parent.getEntryCount() : 0;
    }
    
    @Override
    public int getSize() {
        int size = 0x18 + entries.size() * 8;
        
        for (Tuple<Integer, BTXEntry> a : entries) {
            size += ((a.getValue().getString().length() + 1) * 2);
            
            if (a.getValue().getMeta().isPresent()) {
                size += 0x30;
                size = Utils.align(size, 4);
            }
            else
                size += 2;
        }
        
        return size;
    }
    
    @Override
    public ResPayload.Payload getType() {
        return Payload.BTX;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        if (entries.stream().anyMatch(a -> a.getValue().getMeta().isPresent()))
            dest.writeInteger((int) (getSize() - entries.stream().filter(a -> a.getValue().getMeta().isPresent()).count() * 0x30));
        
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(1); // version
        dest.writeInteger(entries.stream().anyMatch(a -> a.getValue().getMeta().isPresent()) ? 0xC : 0x10);
        if (entries.stream().noneMatch(a -> a.getValue().getMeta().isPresent()))
            dest.writeInteger(0); // padding
            
        dest.writeInteger(1); // always 1
        dest.writeInteger(entries.size());
        
        long pointer = dest.getPosition() + entries.size() * 8;
        
        for (Tuple<Integer, BTXEntry> a : entries) {
            dest.writeInteger(a.getKey());
            
            int lPointer = (int) (pointer - dest.getPosition() + 2);
            if (a.getValue().getMeta().isPresent())
                lPointer = Utils.align(lPointer, 4);
            else
                lPointer += 2;
            
            dest.writeInteger(lPointer);
            
            dest.writeString(a.getValue().getString() + "\0", WRITE_ENCODING, pointer);
            pointer += (a.getValue().getString().length() + 1) * 2;
            if (a.getValue().getMeta().isPresent())
                pointer = Utils.align(pointer, 4);
            else
                pointer += 2;
        }
        
        dest.setPosition(pointer);
        
        entries.stream().forEach(a -> a.getValue().getMeta().ifPresent(b -> b.writeKCAP(dest)));
    }
    
    public List<Tuple<Integer, BTXEntry>> getEntries() {
        return entries;
    }
    
    public Optional<BTXEntry> getEntryById(int id) {
        return entries.stream().filter(a -> a.getKey() == id).findFirst().map(Tuple::getValue);
    }
    
    public String getStringById(int id) {
        return getEntryById(id).map(BTXEntry::getString).orElse(null);
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + fileId;
    }
    
    public int getFileId() {
        return fileId;
    }
    
    /**
     * Contains additional information for text strings, like the speaker for text messages.
     * 
     * Always 0x30 bytes.
     */
    public static class BTXMeta {
        private int id; // TODO shouldn't be stored but instead generated
        private int speaker;
        private short unknown2_1;
        private byte unknown2_2;
        private byte unknown2_3;
        private int unknown3;
        
        // TODO voice line name?
        private int unknown4;
        private int unknown5;
        private int unknown6;
        private int unknown7;
        
        private int unknown8;
        private int unknown9;
        private int unknown10;
        private int unknown11;
        private int unknown12;
        private int unknown13;
        private int unknown14;
        
        public BTXMeta(Access source) {
            id = source.readInteger();
            speaker = source.readInteger();
            
            unknown2_1 = source.readShort();
            unknown2_2 = source.readByte();
            unknown2_3 = source.readByte();
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
                 
	public BTXMeta(int btxid, int speakerid, short unk1, byte unk2, byte unk3, int unk4, int unk5, int unk6, int unk7,
				int unk8, int unk9, int unk10, int unk11, int unk12) {
            id = btxid;
            speaker = speakerid;
            unknown2_1 = unk1;
            unknown2_2 = unk2;
            unknown2_3 = unk3;
            unknown3 = unk4;
            unknown4 = unk5;
            unknown5 = unk6;
            unknown6 = unk7;
            unknown7 = unk8;         
            unknown8 = unk9;
            unknown9 = unk10;
            unknown10 = unk11;
            unknown11 = unk12;
	}


	public void writeKCAP(Access dest) {
            dest.writeInteger(id);
            dest.writeInteger(speaker);
            dest.writeShort(unknown2_1);
            dest.writeByte(unknown2_2);
            dest.writeByte(unknown2_3);
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
         * Their names are found in files 11-14 in the LanguageKeep_jp.res,
         * 0XXX -> BTX 11
         * 1XXX -> BTX 13
         * 2XXX -> BTX 14
         * 
         * @return the speaker ID
         */
        public int getSpeaker() {
            return speaker;
        }

        public short getUnk1() {
            return unknown2_1;
        }
        
        public byte  getUnk2() {
            return unknown2_2;
        }

        public byte  getUnk3() {
            return unknown2_3;
        }
        
        public int  getUnk4() {
            return unknown3;
        }
        
        public int  getUnk5() {
            return unknown4;
        }
        
        public int  getUnk6() {
            return unknown5;
        }
        
        public int  getUnk7() {
            return unknown6;
        }
        
        public int  getUnk8() {
            return unknown7;
        }  
        
        public int  getUnk9() {
            return unknown8;
        }
        
        public int  getUnk10() {
            return unknown9;
        }
        
        public int  getUnk11() {
            return unknown10;
        }
        
        public int  getUnk12() {
            return unknown11;
        }

        }
    
    /**
     * An entry to a BTX, containing a String and an optional {@link BTXMeta}.
     */
    public static class BTXEntry {
        private String string;
        private BTXMeta meta;
        
        public BTXEntry(String string, BTXMeta meta) {
            // remove furigana from the string
            this.string = string.replaceAll("<r[0-9][^>]*>", ""); 
            this.meta = meta;
        }
        
        /**
         * Gets the optional {@link BTXMeta} of this entry.
         * 
         * @return the BTXMeta of this entry, null if non-existent
         */
        public Optional<BTXMeta> getMeta() {
            return Optional.ofNullable(meta);
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
