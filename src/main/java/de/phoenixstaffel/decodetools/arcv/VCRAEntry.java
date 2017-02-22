package de.phoenixstaffel.decodetools.arcv;

import java.nio.ByteBuffer;

import de.phoenixstaffel.decodetools.dataminer.Access;

/*
* VCRA Entry Format – 0x20
* int - unknown (always 0x00000000?)
* int - unknown (compressed size?)
* int - file size unpacked
* int - file path/name pointer
* int - unknown (sector?)
* int - MARV pointer
* int - unknown
* int - unknown
*/
public class VCRAEntry {
    private int compressedSize;
    private int uncompressedSize;
    private int sector;
    
    private boolean isCompressed;
    
    private String filePath;
    private MARVEntry marv;
    
    public VCRAEntry(Access source) {
        source.readInteger(); // always 0x00000000
        compressedSize = source.readInteger();
        uncompressedSize = source.readInteger();
        
        filePath = source.readASCIIString(source.readInteger());
        sector = source.readInteger();
        
        marv = new MARVEntry(ByteBuffer.wrap(source.readByteArray(0x20, source.readInteger())));
        source.readInteger(); // always 0x00000000
        isCompressed = source.readInteger() == 1;
    }
    
    public VCRAEntry(int compressedSize, int uncompressedSize, String path, int startSector, MARVEntry marv) {
        this.compressedSize = compressedSize;
        this.uncompressedSize = uncompressedSize;
        this.sector = startSector;
        this.isCompressed = compressedSize != uncompressedSize;
        this.filePath = path;
        this.marv = marv;
    }
    
    public String getPath() {
        return filePath;
    }
    
    public MARVEntry getMARV() {
        return marv;
    }
    
    public int getCompressedSize() {
        return compressedSize;
    }
    
    public int getUnpackedSize() {
        return uncompressedSize;
    }
    
    public int getSector() {
        return sector;
    }
    
    public boolean isCompressed() {
        return isCompressed;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof VCRAEntry))
            return false;
        VCRAEntry other = (VCRAEntry) obj;
        if (filePath == null) {
            if (other.filePath != null)
                return false;
        }
        else if (!filePath.equals(other.filePath))
            return false;
        return true;
    }
}
