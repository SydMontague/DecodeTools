package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;

public class AccessoryData implements GenericKeepData {
    private short unk1;
    private short unk2;
    private int group;
    private int unk3;
    private int unk4;

    public AccessoryData(Access access) {
        this.unk1 = access.readShort();
        this.unk2 = access.readShort();
        this.group = access.readInteger();
        this.unk3 = access.readInteger();
        this.unk4 = access.readInteger();
    }


    public int getGroup() {
        return group;
    }

    public int getSize() {
        return 0x10;
    }

    @Override
    public void write(Access access) {
        access.writeShort(unk1);
        access.writeShort(unk2);
        access.writeInteger(group);
        access.writeInteger(unk3);
        access.writeInteger(unk4);
    }
}
