package de.phoenixstaffel.decodetools.res.payload;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.extensions.VoidExtension;

public class KCAPPayload extends ResPayload {
    private static final int VERSION = 1;
    
    private long startAddress;
    
    private int unknown2; // flags?
    
    private int numEntries;
    private int numPayloadEntries;
    private int headerSize;
    private int extensionPayloadStart;
    
    private HeaderExtension extension = new VoidExtension();
    private List<KCAPPointer> pointer = new ArrayList<>();
    private HeaderExtensionPayload extensionPayload = extension.loadPayload(null, 0);
    
    private List<ResPayload> entries = new ArrayList<>();
    
    private boolean genericAligned;
    
    public KCAPPayload(Access source, int dataStart, KCAPPayload parent, int size) {
        this(source, dataStart, parent);
    }
    
    public KCAPPayload(Access source, int dataStart, KCAPPayload parent) {
        super(parent);
        startAddress = source.getPosition();
        
        source.readInteger(); // magic value
        source.readInteger(); // version
        source.readInteger(); // size
        
        unknown2 = source.readInteger();
        numEntries = source.readInteger();
        numPayloadEntries = source.readInteger();
        headerSize = source.readInteger();
        extensionPayloadStart = source.readInteger();
        
        if (headerSize > 0x20)
            extension = HeaderExtension.craft(source);
        
        if (source.getPosition() - startAddress < startAddress + headerSize)
            source.setPosition(startAddress + headerSize);
        
        for (int i = 0; i < numEntries; i++)
            pointer.add(new KCAPPointer(source.readInteger(), source.readInteger()));
        
        if (extension != null)
            extensionPayload = extension.loadPayload(source, numPayloadEntries);
        
        genericAligned = !pointer.stream().anyMatch(a -> (a.getOffset() % 0x10) != 0);
        
        for (KCAPPointer entry : pointer) {
            source.setPosition(entry.getOffset() + startAddress);
            
            if (entry.getOffset() == 0 && entry.getLength() == 0)
                entries.add(new VoidPayload(parent));
            else
                entries.add(ResPayload.craft(source, dataStart, this, entry.getLength()));
        }
        
        source.setPosition(Utils.getPadded(source.getPosition(), 0x10));
    }
    
    @Override
    public int getSize() {
        int value = 0x20; // magic value, KCAP base size
        
        // header extension, always padded to a multiple of 0x10
        value += Utils.getPadded(extension.getSize(), 0x10);
        
        // pointer table
        int payload = entries.size() * 8;
        
        // payload of the extension, so far only FileNameExtensionPayload
        payload += extensionPayload.getSize();
        
        // special cases for GMIP headers, where the KCAP content starts directly after the header payload
        payload = Utils.getPadded(payload, extension.getType().getPadding());
        
        value += payload;
        value = Utils.getPadded(value, extension.getContentAlignment(this));
        
        //
        for (ResPayload entry : entries) {
            if (entry.getType() == null)
                continue;
            
            value = Utils.getPadded(value, extension.getContentAlignment(this));
            value += entry.getSize();
        }
        
        value = Utils.getPadded(value, 0x4);
        
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
        return getParent().getGenericAlignment();
    }
    
    @Override
    public MutableTreeNode getTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        
        entries.forEach(a -> node.add(a.getTreeNode()));
        
        return node;
    }
    
    @Override
    public Payload getType() {
        return Payload.KCAP;
    }
    
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        long start = dest.getPosition();
        
        dest.writeInteger(getType().getMagicValue()); // Header Indicator
        dest.writeInteger(VERSION); // Version
        dest.writeInteger(0); // Size Dummy
        dest.writeInteger(getSize(), start + 0x8); // Size
        dest.writeInteger(unknown2); // Flags
        
        dest.writeInteger(entries.size()); // number of entries
        dest.writeInteger(extensionPayload.getEntryNumber()); // number of extension payload entries
        
        int pointerPointer = 0x20 + extension.getSize();
        dest.writeInteger(pointerPointer); // header size
        
        int extPayloadStart = pointerPointer + entries.size() * 8;
        dest.writeInteger(extensionPayload.getEntryNumber() == 0 ? 0 : extPayloadStart);
        
        extension.writeKCAP(dest); // extension header
        
        int fileStart = Utils.getPadded(extPayloadStart + extensionPayload.getSize(), extension.getType().getPadding());
        fileStart = Utils.getPadded(fileStart, extension.getContentAlignment(this));
        int tmp = fileStart;
        
        // pointer table
        for (ResPayload entry : entries) {
            if (entry.getType() != null)
                fileStart = Utils.getPadded(fileStart, extension.getContentAlignment(this));
            
            dest.writeInteger(entry.getSize() == 0 ? 0 : fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        extensionPayload.writeKCAP(dest, extPayloadStart); // extension payload
        
        dest.setSize(start + tmp); // padding
        
        // write sub structures
        entries.forEach(a -> {
            if (a.getType() == null)
                return;
            
            int i = (int) (dest.getPosition() - start);
            int d = Utils.getPadded(i, extension.getContentAlignment(this)) - i;
            
            dest.setPosition(dest.getPosition() + d);
            a.writeKCAP(dest, dataStream);
        });
        
        int diff = (int) (Utils.getPadded(dest.getPosition(), 0x4) - dest.getPosition());
        dest.writeByteArray(new byte[diff]);
    }
    
    public int getGenericAlignment() {
        return genericAligned ? 0x10 : 0x4;
    }
    
    public int getNumEntries() {
        return numEntries;
    }
    
    @Override
    public List<ResPayload> getElementsWithType(Payload type) {
        List<ResPayload> list = super.getElementsWithType(type);
        
        entries.forEach(a -> list.addAll(a.getElementsWithType(type)));
        
        return list;
    }
    
    @Override
    public void fillDummyResData(DummyResData data) {
        entries.forEach(a -> a.fillDummyResData(data));
    }
    
    @Override
    public String toString() {
        return getType().name() + " " + (getExtension() != null ? getExtension().getType().name() : "");
    }
}
