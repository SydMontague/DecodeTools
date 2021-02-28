package de.phoenixstaffel.decodetools.keepdata;

import de.phoenixstaffel.decodetools.core.Access;

public interface GenericKeepData {
    public void write(Access access);
    
    public int getSize();
}
