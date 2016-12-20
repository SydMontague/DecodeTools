package de.phoenixstaffel.decodetools.res.extensions;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.res.HeaderExtension;
import de.phoenixstaffel.decodetools.res.HeaderExtensionPayload;

public class RTCLExtension implements HeaderExtension {

    private int magicValue = Extensions.RTCL.getMagicValue();
    
    private int padding1;
    private int padding2;
    private int padding3;

    public RTCLExtension(Access source) {
        this.padding1 = source.readInteger();
        this.padding2 = source.readInteger();
        this.padding3 = source.readInteger();
    }
    
    @Override
    public HeaderExtensionPayload loadPayload(Access source, int kcapEntries) {
        return new FileNameExtensionPayload(kcapEntries, source);
    }
    
    @Override
    public Extensions getType() {
        return Extensions.RTCL;
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
}