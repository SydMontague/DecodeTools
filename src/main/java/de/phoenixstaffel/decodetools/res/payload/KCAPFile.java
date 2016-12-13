package de.phoenixstaffel.decodetools.res.payload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.extensions.VoidExtension;

public class KCAPFile extends KCAPPayload {
    private static final int KCAP_MAGIC_VALUE = 0x5041434B;
    
    private long startAddress;
    
    private int magicValue;
    private int version;
    private int size;
    private int unknown2; // flags?
    
    private int numEntries;
    private int numPayloadEntries;
    private int headerSize;
    private int extensionPayloadStart;
    
    private HeaderExtension extension = new VoidExtension();
    private List<KCAPPointer> pointer = new ArrayList<>();
    private HeaderExtensionPayload extensionPayload = extension.loadPayload(null, 0);
    
    private List<KCAPPayload> entries = new ArrayList<>();
    
    public KCAPFile(Access source, int dataStart, KCAPFile parent, int size) {
        this(source, dataStart, parent);
    }
    
    public KCAPFile(Access source, int dataStart, KCAPFile parent) {
        super(parent);
        startAddress = source.getPosition();
        
        magicValue = source.readInteger();
        
        if (magicValue != KCAP_MAGIC_VALUE)
            throw new IllegalArgumentException("Access is currently not pointing at a KCAP Structure! " + magicValue);
        
        version = source.readInteger();
        size = source.readInteger();
        
        unknown2 = source.readInteger();
        numEntries = source.readInteger();
        numPayloadEntries = source.readInteger();
        headerSize = source.readInteger();
        extensionPayloadStart = source.readInteger();
        
        if (headerSize > 0x20)
            extension = HeaderExtension.craft(source);
        
        for (int i = 0; i < numEntries; i++)
            pointer.add(new KCAPPointer(source.readInteger(), source.readInteger()));
        
        if (extension != null)
            extensionPayload = extension.loadPayload(source, numPayloadEntries);
        
        KCAPFile p = this;
        
        while ((p = p.getParent()) != null)
            System.out.print("  ");
        
        System.out.print(Integer.toHexString((int) startAddress) + " KCAP ");
        if (extension != null)
            System.out.print(extension.getType());
        
        System.out.print(" " + Integer.toHexString(unknown2) + " " + Integer.toHexString(numEntries));
        System.out.println();
        
        for (KCAPPointer entry : pointer) {
            source.setPosition(entry.getOffset() + startAddress);
            
            if (entry.getOffset() == 0 && entry.getLength() == 0) {
                entries.add(new KCAPPayload(parent) {
                    @Override
                    public int getSize() {
                        return 0;
                    }
                    
                    @Override
                    public Payload getType() {
                        return null;
                    }
                    
                    @Override
                    public void writeKCAP(Access dest, ByteArrayOutputStream dataStream) {
                        // nothing to write
                    }
                });
                continue;
            }
            
            entries.add(KCAPPayload.craft(source, dataStart, this, entry.getLength()));
        }
        
    }
    
    @Override
    public int getSize() {
        int value = 0x20; // magic value, KCAP base size
        
        // header extension, always padded to a multiple of 0x10
        value += Utils.getPadded(extension.getSize(), 16);
        
        // pointer table
        int payload = entries.size() * 8;
        // payload of the extension, so far only FileNameExtensionPayload
        payload += extensionPayload.getSize();
        
        // special cases for GMIP headers, where the KCAP content starts directly after the header payload
        payload = Utils.getPadded(payload, extension.getType().getPadding());
        
        value += payload;
        
        //
        for (KCAPPayload entry : entries) {
            value = Utils.getPadded(value, entry.getAlignment());
            value += entry.getSize();
        }
        
        if (value != size) {
            System.out.println("AAA " + Long.toHexString(startAddress) + " " + Integer.toHexString(value) + " "
                    + Integer.toHexString(size) + " " + this);
        }
        
        return value;
    }
    
    class KCAPPointer {
        private final int offset;
        private final int length;
        
        public KCAPPointer(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public int getLength() {
            return length;
        }
    }
    
    public HeaderExtension getExtension() {
        return extension;
    }
    
    @Override
    public int getAlignment() {
        return 0x10;
    }
    
    @Override
    public MutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this.getType());
        
        entries.forEach(a -> node.add(a.getTreeNode()));
        
        return node;
    }
    
    @Override
    public Payload getType() {
        return Payload.KCAP;
    }
    
    @Override
    public void writeKCAP(Access dest, ByteArrayOutputStream dataStream) {
        dest.writeInteger(KCAP_MAGIC_VALUE); // Header Indicator
        dest.writeInteger(1); // Version
        dest.writeInteger(getSize()); // Size
        dest.writeInteger(unknown2); // Flags
        
        dest.writeInteger(entries.size()); // number of entries
        dest.writeInteger(extensionPayload.getEntryNumber()); // number of extension payload entries
        dest.writeInteger(0x20 + extension.getSize()); // header size
        
        int extPayloadStart = Utils.getPadded(0x20 + extension.getSize(), 0x10) + entries.size() * 8;
        dest.writeInteger(extensionPayload.getEntryNumber() == 0 ? 0 : extPayloadStart);
        
        extension.writeKCAP(dest); //extension header
        
        int fileStart = Utils.getPadded(0x20 + extension.getSize(), 0x10) + entries.size() * 8 + extensionPayload.getSize();
        fileStart = Utils.getPadded(fileStart, extension.getType().getPadding());
        
        //pointer table
        for(KCAPPayload entry : entries) {
            fileStart = Utils.getPadded(fileStart, entry.getAlignment());
            dest.writeInteger(entry.getSize() == 0 ? 0 : fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        extensionPayload.writeKCAP(dest, extPayloadStart); //extension payload
        
        dest.setPosition(Utils.getPadded(dest.getPosition(), extension.getType().getPadding())); //padding
        
        //write sub structures
        entries.forEach(a -> {
            System.out.println(a);
            dest.setPosition(Utils.getPadded(dest.getPosition(), a.getAlignment()));
            a.writeKCAP(dest, dataStream);
            
            int padding = Utils.getPadded(dataStream.size(), 0x80) - dataStream.size();
            
            dataStream.write(new byte[padding], 0, padding);
        });
    }
}
