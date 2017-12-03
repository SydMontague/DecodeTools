package de.phoenixstaffel.decodetools.res.payload.hsem;

import de.phoenixstaffel.decodetools.core.Access;

public interface HSEMEntryPayload {
    
    public void writeKCAP(Access dest);
    
    public int getSize();
}
