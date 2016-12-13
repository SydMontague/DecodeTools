package de.phoenixstaffel.decodetools.res;

import de.phoenixstaffel.decodetools.dataminer.Access;

public interface HeaderExtensionPayload {
    public default int getSize() {
        return 0;
    }

    public default int getEntryNumber() {
        return 0;
    }

    public default void writeKCAP(Access dest, int start) {
        //nothing to write
    }
}
