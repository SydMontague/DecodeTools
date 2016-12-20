package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;

public class XTVPExtension implements HeaderExtension {
    
    private int magicValue = Extensions.XTVP.getMagicValue();
    private int version; //TODO remove in case it is actually fixed
    private int numEntries;
    private int padding;
    
    public XTVPExtension(Access source) {
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
        return Extensions.XTVP;
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
}