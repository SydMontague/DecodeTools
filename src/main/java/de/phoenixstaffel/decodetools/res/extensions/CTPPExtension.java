package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

public class CTPPExtension implements HeaderExtension {
    private int padding1;
    private int padding2;
    private int padding3;
    
    public CTPPExtension(Access source) {
        this.padding1 = source.readInteger();
        this.padding2 = source.readInteger();
        this.padding3 = source.readInteger();
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new HeaderExtensionPayload() {
        };
    }
    
    @Override
    public Extensions getType() {
        return Extensions.CTPP;
    }
    
    @Override
    public int getSize() {
        return 0x10;
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(padding1);
        dest.writeInteger(padding2);
        dest.writeInteger(padding3);
    }
    
    @Override
    public int getContentAlignment(KCAPFile parent) {
        return 0x10;
    }
}
