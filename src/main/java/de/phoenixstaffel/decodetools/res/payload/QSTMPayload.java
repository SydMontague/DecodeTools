package de.phoenixstaffel.decodetools.res.payload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.res.ResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;
import de.phoenixstaffel.decodetools.res.payload.qstm.QSTMEntry;

public class QSTMPayload extends ResPayload {
    private short unknown1;
    private short unknown2; // numAttribtues
    
    private List<QSTMEntry> entries = new ArrayList<>();
    
    public QSTMPayload(Access source, int dataStart, AbstractKCAP parent, int size, String name) {
        super(parent);
        
        source.readInteger(); // magic value
        unknown1 = source.readShort();
        unknown2 = source.readShort();
        
        for(int i = 0; i < unknown2; i++) 
            entries.add(QSTMEntry.loadEntry(source));
    }
    
    @Override
    public int getSize() {
        return 8 + entries.stream().collect(Collectors.summingInt(QSTMEntry::getSize));
    }
    
    @Override
    public Payload getType() {
        return Payload.QSTM;
    }
    
    @Override
    public void writeKCAP(Access dest, ResData dataStream) {
        dest.writeInteger(getType().getMagicValue());
        dest.writeShort(unknown1);
        dest.writeShort(unknown2);
        
        entries.forEach(a -> a.writeKCAP(dest));
    }
}
