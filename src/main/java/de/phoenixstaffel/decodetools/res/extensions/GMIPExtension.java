package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

public class GMIPExtension implements HeaderExtension {
    private int version; // TODO remove in case it is actually fixed
    private int numEntries;
    private int padding;
    
    public GMIPExtension(Access source) {
        this.version = source.readInteger();
        this.numEntries = source.readInteger();
        this.padding = source.readInteger();
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new FileNameExtensionPayload(kcapEntries, source);
    }
    
    // TODO update when new GMIO images get added to root KCAP
    public int getEntryCount() {
        return numEntries;
    }
    
    @Override
    public Extensions getType() {
        return Extensions.GMIP;
    }
    
    @Override
    public int getSize() {
        return 0x10;
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(version);
        dest.writeInteger(numEntries);
        dest.writeInteger(padding);
    }
    
    @Override
    public int getContentAlignment(KCAPPayload parent) {
        return 0x4;
    }
}
