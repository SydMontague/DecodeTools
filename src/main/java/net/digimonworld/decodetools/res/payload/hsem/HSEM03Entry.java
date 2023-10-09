package net.digimonworld.decodetools.res.payload.hsem;

import java.util.ArrayList;
import java.util.List;

import net.digimonworld.decodetools.core.Access;

// something with effects? and animations? Does some matrix stuff
public class HSEM03Entry implements HSEMEntry {
    private byte unkn1;
    private byte unkn2;
    private short jointId;

    private List<Float> values = new ArrayList<>();

    public HSEM03Entry(Access source) {
        this.unkn1 = source.readByte();
        this.unkn2 = source.readByte();
        this.jointId = source.readShort();

        if (unkn1 == 4) {
            values.add(source.readFloat());
            values.add(source.readFloat());
            values.add(source.readFloat());
        }
    }

    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort((short) getHSEMType().getId());
        dest.writeShort((short) getSize());

        dest.writeByte(unkn1);
        dest.writeByte(unkn2);
        dest.writeShort(jointId);

        values.forEach(dest::writeFloat);
    }

    @Override
    public int getSize() {
        return 0x08 + 0x04 * values.size();
    }

    @Override
    public HSEMEntryType getHSEMType() {
        return HSEMEntryType.UNK03;
    }

    @Override
    public String toString() {
        return String.format("Entry03 | U1: %s | U2: %s | Joint: %s", unkn1, unkn2, jointId);
    }
}
