package de.phoenixstaffel.decodetools.res.extensions;

import java.util.ArrayList;
import java.util.List;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

public class TDTMExtension implements HeaderExtension {
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
    }
    
    @Override
    public Extensions getType() {
        return Extensions.TDTM;
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new HeaderExtensionPayload() {
        };
    }
    
    @Override
    public int getSize() {
        return Utils.getPadded(0x20 + entries.size() * 8, 0x10);
    }
    
    @Override
    public void writeKCAP(Access dest) {
        long start = dest.getPosition();
        
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
        
        dest.setPosition(start + getSize());
    }
    
    @Override
    public int getContentAlignment(KCAPFile parent) {
        return 0x10;
    }
    
    class TDTMExtensionEntry {
        int unknown1;
        int unknown2;
        
        public TDTMExtensionEntry(Access source) {
            this.unknown1 = source.readInteger();
            this.unknown2 = source.readInteger();
        }
    }
}
