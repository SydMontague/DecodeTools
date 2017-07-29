package de.phoenixstaffel.decodetools.res.payload.hsem;

import java.util.HashMap;
import java.util.Map;

import de.phoenixstaffel.decodetools.dataminer.Access;

public class HSEMJointEntry implements HSEMEntryPayload {
    private short unkn1;
    private short jointCount;
    
    private Map<Short, Short> jointAssignment = new HashMap<>();
    
    public HSEMJointEntry(Access source) {
        unkn1 = source.readShort();
        jointCount = source.readShort();
        
        for (int i = 0; i < jointCount; i++)
            jointAssignment.put(source.readShort(), source.readShort());
    }
    
    @Override
    public void writeKCAP(Access dest) {
        dest.writeShort(unkn1);
        dest.writeShort((short) jointAssignment.size());
        
        jointAssignment.forEach((a, b) -> {
            dest.writeShort(a);
            dest.writeShort(b);
        });
    }
    
    @Override
    public int getSize() {
        return 0x04 + 0x04 * jointAssignment.size();
    }
}
