package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

public class XDIPExtension implements HeaderExtension {
    private int version; // TODO remove in case it is actually fixed
    private int numEntries;
    private int padding;
    
    public XDIPExtension(Access source) {
        this.version = source.readInteger();
        this.numEntries = source.readInteger();
        this.padding = source.readInteger();
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new HeaderExtensionPayload() {
        };
    }
    
    @Override
    public Extensions getType() {
        return Extensions.XDIP;
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
        return 0x10;
    }
}
