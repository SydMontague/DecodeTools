package net.digimonworld.decodetools.data.keepdata;

import net.digimonworld.decodetools.core.Access;

public class AccessoryOrder implements GenericKeepData {
    public int id;

    public AccessoryOrder(Access access) {
        this.id = access.readInteger();
    }

    public int getId() {
        return id;
    }

    public void setAccId(int id) {
         this.id = id;
    }

    @Override
    public void write(Access access) {
        access.writeInteger(id);
    }

    @Override
    public int getSize() {
        return 0x4;
    }

}
