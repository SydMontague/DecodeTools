package de.phoenixstaffel.decodetools.res.payload;

import java.util.ArrayList;
import java.util.List;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.extensions.VoidExtension;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class KCAPPayload extends ResPayload {
    private static final int VERSION = 1;
    
    private int unknown2; // flags? only 0x10000 or 0x0, only for VOID KCAPs
    
    private HeaderExtension extension = new VoidExtension();
    private List<KCAPPointer> pointer = new ArrayList<>();
    private HeaderExtensionPayload extensionPayload = extension.loadPayload(null, 0);
    
    private List<ResPayload> entries = new ArrayList<>();
    
    private boolean genericAligned;
    
    public KCAPPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        // ignore size and name
        this(source, dataStart, parent);
    }
    
    public KCAPPayload(Access source, int dataStart, AbstractKCAP parent) {
        super(parent);
        long startAddress = source.getPosition();
        
        source.readInteger(); // magic value
        source.readInteger(); // version
        source.readInteger(); // size
        
        unknown2 = source.readInteger();
        int numEntries = source.readInteger();
        int numPayloadEntries = source.readInteger();
        int headerSize = source.readInteger();
        source.readInteger(); // extension payload start
        
        if (headerSize > 0x20)
            extension = HeaderExtension.craft(source);
        
        source.setPosition(startAddress + headerSize);
        
        for (int i = 0; i < numEntries; i++)
            pointer.add(new KCAPPointer(source.readInteger(), source.readInteger()));
        
        if (extension != null)
            extensionPayload = extension.loadPayload(source, numPayloadEntries);
        
        genericAligned = pointer.stream().allMatch(a -> (a.getOffset() % 0x10) == 0);
        
        for (int i = 0; i < numEntries; i++) {
            KCAPPointer entry = pointer.get(i);
            source.setPosition(entry.getOffset() + startAddress);
            
            if (entry.getOffset() == 0 && entry.getLength() == 0)
                entries.add(new VoidPayload(parent));
            else
                entries.add(ResPayload.craft(source, dataStart, (AbstractKCAP) null, entry.getLength(), extensionPayload.get(i)));
        }
        
        source.setPosition(Utils.align(source.getPosition(), 0x10));
    }
    
    @Override
    public int getSize() {
        int value = 0x20; // magic value, KCAP base size
        
        // header extension, always padded to a multiple of 0x10
        value += Utils.align(extension.getSize(), 0x10);
        
        // pointer table
        int payload = getNumEntries() * 8;
        
        // payload of the extension, so far only FileNameExtensionPayload
        payload += extensionPayload.getSize();
        
        // special cases for GMIP headers, where the KCAP content starts directly after the header payload
        payload = Utils.align(payload, extension.getType().getPadding());
        
        value += payload;
        value = Utils.align(value, extension.getContentAlignment(this));
        
        // sub entries
        for (ResPayload entry : entries) {
            if (entry.getType() == null)
                continue;
            
            // make sure it's padded to the content specific padding before adding more
            value = Utils.align(value, extension.getContentAlignment(this));
            value += entry.getSize();
        }
        
        // unneeded padding, hopefully
        // value = Utils.align(value, 0x4);
        
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
    
    public HeaderExtensionPayload getExtensionPayload() {
        return extensionPayload;
    }
    
    @Override
    public Payload getType() {
        return Payload.KCAP;
    }
    
    // FIXME generate extension payload during runtime
    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        long start = dest.getPosition();
        
        dest.writeInteger(getType().getMagicValue()); // Header Indicator
        dest.writeInteger(VERSION); // Version
        dest.writeInteger(getSize()); // Size
        dest.writeInteger(unknown2); // Flags
        
        dest.writeInteger(getNumEntries()); // number of entries
        dest.writeInteger(extensionPayload.getEntryCount()); // number of extension payload entries
        
        int pointerPointer = 0x20 + extension.getSize();
        dest.writeInteger(pointerPointer); // header size
        
        int extPayloadStart = pointerPointer + entries.size() * 8;
        dest.writeInteger(extensionPayload.getEntryCount() == 0 ? 0 : extPayloadStart);
        
        extension.writeKCAP(dest); // extension header
        
        int fileStart = Utils.align(extPayloadStart + extensionPayload.getSize(), extension.getType().getPadding());
        fileStart = Utils.align(fileStart, extension.getContentAlignment(this));
        int tmp = fileStart;
        
        // pointer table
        for (ResPayload entry : entries) {
            if (entry.getType() != null)
                fileStart = Utils.align(fileStart, extension.getContentAlignment(this));
            
            dest.writeInteger(entry.getSize() == 0 ? 0 : fileStart);
            dest.writeInteger(entry.getSize());
            fileStart += entry.getSize();
        }
        
        extensionPayload.writeKCAP(dest, extPayloadStart); // extension payload
        
        dest.setPosition(dest.getSize());
        dest.writeByteArray(new byte[(int) ((start + tmp) - dest.getSize())]); // padding
        
        // write sub structures
        entries.forEach(a -> {
            if (a.getType() == null)
                return;
            
            int i = (int) (dest.getPosition() - start);
            int d = Utils.align(i, extension.getContentAlignment(this)) - i;
            
            dest.setPosition(dest.getPosition() + d);
            a.writeKCAP(dest, dataStream);
        });
        
        // unneeded padding, hopefully
        // int diff = (int) (Utils.align(dest.getPosition(), 0x4) - dest.getPosition());
        // dest.writeByteArray(new byte[diff]);
    }
    
    public int getGenericAlignment() {
        return genericAligned ? 0x10 : 0x4;
    }
    
    public int getNumEntries() {
        return entries.size();
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
    
    public ResPayload get(int i) {
        return entries.get(i);
    }
    
    public void replace(ResPayload current, ResPayload replacement) {
        entries.replaceAll(a -> a == current ? replacement : a);
    }
    
    public List<ResPayload> getEntries() {
        return entries;
    }
}
