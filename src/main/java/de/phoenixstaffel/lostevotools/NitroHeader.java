package de.phoenixstaffel.lostevotools;

import de.phoenixstaffel.decodetools.core.Access;

public class NitroHeader {
    private int magicValueNCLR;
    private short endian;
    private short version; //
    private int fileSize;
    private int headerSize; //-> 0x10

    public NitroHeader(Access access) {
        magicValueNCLR = access.readInteger();
        endian = access.readShort();
        version = access.readShort();
        fileSize = access.readInteger();
        headerSize = access.readInteger();
    }

    public void save(Access dest) {
        dest.writeInteger(magicValueNCLR);
        dest.writeShort(endian);
        dest.writeShort(version);
        dest.writeInteger(fileSize);
        dest.writeInteger(headerSize);
    }
}
