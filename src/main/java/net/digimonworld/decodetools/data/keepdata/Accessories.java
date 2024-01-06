package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;

public class Accessories implements GenericKeepData {
    private static int nextId = 0;  // Static variable to keep track of sequential IDs
    private int id;                 // ID for each Accessories instance
    private short unk1;
    private short order;
    private int group;
    private int unk2;
    private int unk3;

    public Accessories(Access access) {
        this.id = nextId++;  // Assign the current ID and then increment for the next instance
        this.unk1 = access.readShort();
        this.order = access.readShort();
        this.group = access.readInteger();
        this.unk2 = access.readInteger();
        this.unk3 = access.readInteger();
    }

    public int getId() {
        return id;
    }

    public short getOrder() {
        return order;
    }

    public int getGroup() {
        return group;
    }

    public void setOrder(short order) {
        this.order = order;
    }

    public int getSize() {
        return 0x10;
    }

    @Override
    public void write(Access access) {
        access.writeShort(unk1);
        access.writeShort(order);
        access.writeInteger(group);
        access.writeInteger(unk2);
        access.writeInteger(unk3);
    }
}
