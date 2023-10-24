package net.digimonworld.decodetools.res.payload;

import java.util.Arrays;
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

        // for BTX that contain a meta have the magic value at the second word of the struct
        int magic = source.readInteger();
        if (magic != getType().getMagicValue()) {
            postStart = magic;
            source.readInteger(); // magic value
        }

        int languageCount = source.readInteger(); // always 1, JP
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
            entries.add(new Tuple<>(pointers.get(i).getKey(),
                                    new BTXEntry(strings.get(i), metas.size() > i ? metas.get(i) : null)));
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
            dest.writeInteger((int) (getSize()
                                     - entries.stream().filter(a -> a.getValue().getMeta().isPresent()).count()
                                       * 0x30));

        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(1); // version
        dest.writeInteger(entries.stream().anyMatch(a -> a.getValue().getMeta().isPresent()) ? 0xC : 0x10);
        if (entries.stream().noneMatch(a -> a.getValue().getMeta().isPresent()))
            dest.writeInteger(0); // padding

        dest.writeInteger(1); // language count, always 1
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
    
    public void setEntries(List<Tuple<Integer, BTXEntry>> list) {
        this.entries = list;
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
        private short unknown1;
        private short unknown2;
        private short unknown3;
        private short unknown4;

        private String voiceLine;

        public BTXMeta(Access source) {
            this.id = source.readInteger();
            this.speaker = source.readInteger();

            this.unknown1 = source.readShort();
            this.unknown2 = source.readShort();
            this.unknown3 = source.readShort();
            this.unknown4 = source.readShort();

            this.voiceLine = source.readString(0x20, "ASCII").trim();
        }

        public BTXMeta(int btxid, int speakerid, short unk1, short unk2, short unk3, short unk4, String voiceLine) {
            this.id = btxid;
            this.speaker = speakerid;
            this.unknown1 = unk1;
            this.unknown2 = unk2;
            this.unknown3 = unk3;
            this.unknown4 = unk4;

            this.voiceLine = voiceLine;
        }

        public void writeKCAP(Access dest) {
            dest.writeInteger(id);
            dest.writeInteger(speaker);
            dest.writeShort(unknown1);
            dest.writeShort(unknown2);
            dest.writeShort(unknown3);
            dest.writeShort(unknown4);

            dest.writeByteArray(Arrays.copyOf(voiceLine.getBytes(), 0x20));
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
            return unknown1;
        }

        public short getUnk2() {
            return unknown2;
        }

        public short getUnk3() {
            return unknown3;
        }

        public short getUnk4() {
            return unknown4;
        }

        public String getVoiceLine() {
            return voiceLine;
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
