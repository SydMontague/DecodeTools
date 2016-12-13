package de.phoenixstaffel.decodetools.res.extensions;

import java.util.ArrayList;
import java.util.List;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;

public class TDTMExtension implements HeaderExtension {
    private int magicValue = Extensions.TDTM.getMagicValue();
    
    private int version;
    private int numEntries;
    private int padding;
    
    private float unknown1;
    private float unknown2;
    private float unknown3;
    private float unknown4;
    
    private List<TDTMExtensionEntry> entries = new ArrayList<>();
    
    public TDTMExtension(Access source) {
        this.version = source.readInteger();
        this.numEntries = source.readInteger();
        this.padding = source.readInteger();
        
        this.unknown1 = source.readFloat();
        this.unknown2 = source.readFloat();
        this.unknown3 = source.readFloat();
        this.unknown4 = source.readFloat();
        
        for (int i = 0; i < numEntries; i++)
            entries.add(new TDTMExtensionEntry(source));
        
        // padding
        if ((source.getPosition() & 0x8) != 0)
            source.readLong();
    }
    
    @Override
    public Extensions getType() {
        return Extensions.TDTM;
    }
    
    class TDTMExtensionEntry {
        private int unknown1;
        private int unknown2;
        
        public TDTMExtensionEntry(Access source) {
            this.unknown1 = source.readInteger();
            this.unknown2 = source.readInteger();
        }
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new HeaderExtensionPayload() {
        };
    }
    
    @Override
    public int getSize() {
        return Utils.getPadded(0x20 + entries.size() * 8, 16);
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(version);
        dest.writeInteger(numEntries);
        dest.writeInteger(padding);
        dest.writeFloat(unknown1);
        dest.writeFloat(unknown2);
        dest.writeFloat(unknown3);
        dest.writeFloat(unknown4);
        
        entries.forEach(a -> {
            dest.writeInteger(a.unknown1);
            dest.writeInteger(a.unknown2);
        });
        
        if(entries.size() % 2 != 0)
            dest.writeLong(0L);
    }
}
