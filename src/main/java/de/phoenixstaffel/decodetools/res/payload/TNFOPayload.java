package de.phoenixstaffel.decodetools.res.payload;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;

/*
 * 4 byte - magic TNFO
 * 4 byte - Version? (0x101)
 * 4 byte - unknown (0)
 * 2 byte - unknown 
 * 2 byte - unknown, something Y offset?
 *  
 * 2 byte - width of space (0x20) character
 * 2 byte - unknown
 * 4 byte - first character to be used
 * 4 byte - last character to be used
 * 2 byte - reference font size?
 * 2 byte - unknown, also reference?
 * 
 * 4 byte - unknown (0)
 * 4 byte - unknown (0)
 * 4 byte - unknown (0)
 * 4 byte - unknown (1/2)
 * 
 * 4 byte - number of characters?
 * 4 byte - pointer to assignTableDef? char table?
 * 4 byte - unknown (0)
 * 4 byte - unknown (0)
 * 
 * 4 byte - unknown (0)
 * 4 byte - unknown (0)
 * 4 byte - unknown (0)
 * 16 byte - default TNFO entry
 * 
 * 4 byte - assignment header size (0x10)
 * 
 * 4 byte - pointer to char assignment table? 
 * 4 byte - chars in assignment table?
 * 4 byte - assignment entry size 
 * 
 * ??? Table, one entry per character
 * 16 byte
 * - 2 byte GMIO id
 * - 1 byte x translation?
 * - 1 byte y translation?
 * - 1 byte width
 * - 1 byte height
 * - 1 byte width in text
 * - 1 byte unknown/unused?
 * - 2 byte x upper left
 * - 2 byte x lower right
 * - 2 byte y upper left
 * - 2 byte y lower right
 * 
 * Assignment table, one entry per character
 * 2 byte UTF-16 character (optional, not for main TNFO)
 * 2 byte id 
 */
public class TNFOPayload extends ResPayload {
    private static final int VERSION = 0x101;
    private static final int HEADER_SIZE = 0x5C;
    private static final int TABLE_HEADER_SIZE = 0x10;
    private static final int TOTAL_HEADER_SIZE = HEADER_SIZE + TABLE_HEADER_SIZE;
    
    private short unknown1;
    private short yOffset;
    
    private short spaceWidth;
    private short unknown2;
    private int firstCharacter;
    private int lastCharacter;
    private short referenceSize;
    private short unknown3;
    
    private int tnfoType; // 1 = firstCharacter to lastCharacter; 2 = each character defined in assignment
    
    private TNFOEntry defaultEntry;
    
    private int assignTablePointer;
    private int assignTableEntries;
    private int assignEntrySize;
    
    private List<TNFOEntry> entries = new ArrayList<>();
    private SortedMap<Integer, TNFOEntry> assignments = new TreeMap<>();
    
    public TNFOPayload(Access source, int dataStart, KCAPPayload parent, int size, String name) {
        super(parent);
        
        long startPosition = source.getPosition();
        
        source.readInteger(); // magic TNFO
        source.readInteger(); // version
        source.readInteger(); // always 0
        unknown1 = source.readShort();
        yOffset = source.readShort();
        
        spaceWidth = source.readShort();
        unknown2 = source.readShort();
        firstCharacter = source.readInteger();
        lastCharacter = source.readInteger();
        referenceSize = source.readShort();
        unknown3 = source.readShort();
        
        source.readInteger(); // always 0
        source.readInteger(); // always 0
        source.readInteger(); // always 0
        tnfoType = source.readInteger();
        
        source.readInteger(); // char count, font0 has too many so useless
        source.readInteger(); // always HEADER_SIZE, so 0x5C
        source.readInteger(); // always 0
        source.readInteger(); // always 0
        
        source.readInteger(); // always 0
        source.readInteger(); // always 0
        source.readInteger(); // always 0
        
        defaultEntry = new TNFOEntry(source);
        
        source.setPosition(startPosition + HEADER_SIZE);
        
        source.readInteger(); // always TABLE_HEADER_SIZE, so 0x10
        
        assignTablePointer = source.readInteger();
        assignTableEntries = source.readInteger();
        assignEntrySize = source.readInteger();
        
        while (source.getPosition() < startPosition + HEADER_SIZE + assignTablePointer)
            entries.add(new TNFOEntry(source));
        
        source.setPosition(startPosition + assignTablePointer + HEADER_SIZE);
        
        short charCounter = (short) firstCharacter;
        for (int i = 0; i < assignTableEntries; i++) {
            short utf16Char = assignEntrySize == 4 ? source.readShort() : charCounter++;
            short id = source.readShort();
            
            assignments.put(Short.toUnsignedInt(utf16Char), id == (short) 0xFFFF ? null : entries.get(id));
        }
    }
    
    @Override
    public int getSize() {
        return TOTAL_HEADER_SIZE + entries.size() * 0x10 + assignments.size() * assignEntrySize;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public Payload getType() {
        return Payload.TNFO;
    }
    
    public int getFirstCharacter() {
        return firstCharacter;
    }
    
    public int getLastCharacter() {
        return lastCharacter;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(0);
        dest.writeShort(unknown1);
        dest.writeShort(yOffset);
        
        dest.writeShort(spaceWidth);
        dest.writeShort(unknown2);
        dest.writeInteger(firstCharacter);
        dest.writeInteger(lastCharacter);
        dest.writeShort(referenceSize);
        dest.writeShort(unknown3);
        
        dest.writeInteger(0);
        dest.writeInteger(0);
        dest.writeInteger(0);
        dest.writeInteger(tnfoType);
        
        int count = entries.size();
        
        dest.writeInteger(count);
        dest.writeInteger(HEADER_SIZE); // directly after the header
        dest.writeInteger(0);
        dest.writeInteger(0);
        
        dest.writeInteger(0);
        dest.writeInteger(0);
        dest.writeInteger(0);
        
        defaultEntry.writeKCAP(dest);
        
        dest.writeInteger(TABLE_HEADER_SIZE); // seems to be 0x10 all the time
        
        dest.writeInteger(count * 0x10 + TABLE_HEADER_SIZE);
        dest.writeInteger(assignEntrySize == 2 ? getLastCharacter() - getFirstCharacter() + 1 : count);
        dest.writeInteger(assignEntrySize);
        
        entries.forEach(a -> a.writeKCAP(dest));
        
        assignments.forEach((a, b) -> {
            if (assignEntrySize == 4)
                dest.writeShort(a.shortValue());
            dest.writeShort((short) entries.indexOf(b));
        });
    }
    
    /*
     * ??? Table, one entry per character
     * 16 byte
     * - 2 byte GMIO id
     * - 1 byte x translation?
     * - 1 byte y translation?
     * - 1 byte width
     * - 1 byte height
     * - 1 byte width in text
     * - 1 byte unknown/unused?
     * - 2 byte x upper left
     * - 2 byte x lower right
     * - 2 byte y upper left
     * - 2 byte y lower right
     */
    class TNFOEntry {
        private short gmioId;
        private byte xTranslation;
        private byte yTranslation;
        
        private byte width;
        private byte height;
        private byte textWidth;
        private byte unused;
        // replace with floats?
        private short x1;
        private short x2;
        private short y1;
        private short y2;
        
        public TNFOEntry(Access source) {
            gmioId = source.readShort();
            xTranslation = source.readByte();
            yTranslation = source.readByte();
            
            width = source.readByte();
            height = source.readByte();
            textWidth = source.readByte();
            unused = source.readByte();
            
            x1 = source.readShort();
            x2 = source.readShort();
            y1 = source.readShort();
            y2 = source.readShort();
        }
        
        public void writeKCAP(Access dest) {
            dest.writeShort(gmioId);
            dest.writeByte(xTranslation);
            dest.writeByte(yTranslation);
            dest.writeByte(width);
            dest.writeByte(height);
            dest.writeByte(textWidth);
            dest.writeByte(unused);
            dest.writeShort(x1);
            dest.writeShort(x2);
            dest.writeShort(y1);
            dest.writeShort(y2);
        }
    }
}
