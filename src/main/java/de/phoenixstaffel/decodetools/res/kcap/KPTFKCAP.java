package de.phoenixstaffel.decodetools.res.kcap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;

public class KPTFKCAP extends AbstractKCAP {

    private TNFOPayload tnfo;
    private List<GMIOPayload> images = new ArrayList<>();
    
    private int unknown;

    public KPTFKCAP(AbstractKCAP parent, Access source, int dataStart, KCAPInformation info) {
        super(parent, info.flags);
        
        // make sure it's actually a KPTF
        if(source.readInteger() != KCAPType.KPTF.getMagicValue())
            throw new IllegalArgumentException("Tried to instanciate KPTF KCAP, but didn't find a KPTF header.");
        
        source.readInteger(); //version? always 0x100
        unknown = source.readInteger(); 
        source.readInteger(); //padding, always 0
        
        // load the KCAP pointers to the entries
        List<KCAPPointer> pointer = loadKCAPPointer(source, info.entries);
        
        // make sure we actually have exactly two entries, a TNFO and a GMIO/GMIP
        if(pointer.size() != 2)
            throw new IllegalArgumentException("A KPTF KCAP has always two elements, but this one has " + pointer.size() + "!");
        
        // load TNFO
        source.setPosition(info.startAddress + pointer.get(0).getOffset());
        tnfo = new TNFOPayload(source, dataStart, null /* TODO this */, pointer.get(0).getSize(), null);

        // load second entry and verify that it's a GMIO/GMIP
        source.setPosition(info.startAddress + pointer.get(1).getOffset());
        ResPayload tmpImages = ResPayload.craft(source, dataStart, this, pointer.get(1).getSize(), null);
        
        if(tmpImages.getType() != ResPayload.Payload.GMIO && !(tmpImages instanceof GMIPKCAP))
            throw new IllegalArgumentException("The second entry of a KPTF KCAP must be either GMIO or GMIP-KCAP, but wasn't." + tmpImages);
        
        // put the images in the list, a potential GMIPKCAP
        if(tmpImages.getType() == ResPayload.Payload.GMIO)
            images.add((GMIOPayload) tmpImages);
        else
            ((GMIPKCAP) tmpImages).forEach(a -> images.add((GMIOPayload) a));
        
        // make sure we're at the end of the KCAP
        long expectedEnd = info.startAddress + info.size;
        if(source.getPosition() != expectedEnd)
            Main.LOGGER.warning(() -> "Final position for normal KCAP does not match the header. Current: " + source.getPosition() + " Expected: " + expectedEnd);
    }
    
    @Override
    public List<ResPayload> getEntries() {
        List<ResPayload> entries = new ArrayList<>();
        entries.add(tnfo);
        if(images.size() == 1)
            entries.add(images.get(0));
        else
            entries.add(new GMIPKCAP(this, images));
        
        return Collections.unmodifiableList(entries);
    }

    @Override
    public ResPayload get(int i) {
        switch(i) {
            case 0: return tnfo;
            case 1: return images.size() == 1 ? images.get(0) : new GMIPKCAP(this, images);
            default: throw new NoSuchElementException();
        }
    }

    @Override
    public int getEntryCount() {
        return 2;
    }

    @Override
    public KCAPType getKCAPType() {
        return KCAPType.KPTF;
    }

    @Override
    public int getSize() {
        int size = 0x30; // size of header
        size += getEntryCount() * 0x08; // size of pointer map
        
        // size of TNFO
        size = Utils.align(size, 0x10); // align to specific alignment
        size += tnfo.getSize(); 

        // size of GMIO/GMIP
        size = Utils.align(size, 0x10); // align to specific alignment
        
        if(images.size() == 1)
            size += images.get(0).getSize();
        else 
            size += new GMIPKCAP(this, images).getSize();

        return size;
    }

    @Override
    public void writeKCAP(Access dest, IResData dataStream) {
        long start = dest.getPosition();
        ResPayload imagePayload = images.size() == 1 ? images.get(0) : new GMIPKCAP(this, images);
        
        // write header
        dest.writeInteger(getType().getMagicValue());
        dest.writeInteger(VERSION);
        dest.writeInteger(getSize());
        dest.writeInteger(getUnknown());

        dest.writeInteger(getEntryCount());
        dest.writeInteger(0x00); // type count, always 0 for this type
        dest.writeInteger(0x30); // header size, always 0x30 for this type
        dest.writeInteger(0x00); // type payload start, always 0 for this type

        dest.writeInteger(getKCAPType().getMagicValue());
        dest.writeInteger(0x100); // padding
        dest.writeInteger(unknown); // padding
        dest.writeInteger(0x00); // padding
        
        // write pointer table
        int fileStart = Utils.align(0x30 + getEntryCount() * 0x08, 0x10);
        int contentStart = fileStart;

        dest.writeInteger(fileStart);
        dest.writeInteger(tnfo.getSize());
        fileStart += tnfo.getSize();

        fileStart = Utils.align(fileStart, 0x10); // align content start
        
        dest.writeInteger(fileStart);
        dest.writeInteger(imagePayload.getSize());
        
        // move write pointer to start of content
        dest.setPosition(start + contentStart);
        
        // write TNFO
        long aligned = Utils.align(dest.getPosition() - start, 0x10);
        dest.setPosition(start + aligned);
        tnfo.writeKCAP(dest, dataStream);
        
        // write GMIO/GMIP
        aligned = Utils.align(dest.getPosition() - start, 0x10);
        dest.setPosition(start + aligned);
        imagePayload.writeKCAP(dest, dataStream);
    }
    
}
