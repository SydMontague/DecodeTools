package net.digimonworld.decodetools.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An implementation of {@link Access} that reads and writes from a given buffer.
 * 
 * As such it can not change it's size.
 */
public class StreamAccess extends BufferedAccess {
    private final ByteBuffer chan;
    
    /**
     * Initialises a new instances of this class.
     * 
     * @param chan the FileChannel to read and write from
     * @param byteOrder the ByteOrder to use when reading/writing, i.e. Big/Little Endian
     */
    public StreamAccess(ByteBuffer chan, ByteOrder byteOrder) {
        super(byteOrder);
        
        this.chan = chan;
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * The byte order will be set to Little Endian.
     * </p>
     * 
     * @param chan the FileChannel to read and write from
     */
    public StreamAccess(ByteBuffer chan) {
        this(chan, ByteOrder.LITTLE_ENDIAN);
    }
    
    public StreamAccess(byte[] chan) {
        this(ByteBuffer.wrap(chan));
    }
    
    @Override
    public long getPosition() {
        return chan.position();
    }
    
    @Override
    public void setPosition(long address) {
        if(address > getSize())
            address = getSize();
        
        chan.position((int) address);
    }
    
    @Override
    public long getSize() {
        return chan.capacity();
    }
    
    @Override
    public void close() {
        // nothing to close
    }
    
    /**
     * Returns the {@link ByteBuffer} this instance is using.
     * 
     * @return the ByteBuffer used
     */
    public ByteBuffer getBuffer() {
        return chan;
    }
    
    @Override
    void readBuffer(ByteBuffer buff) {
        buff.clear();
        
        byte[] arr = new byte[buff.capacity()];
        chan.get(arr);
        buff.put(arr);

        buff.flip();
    }
    
    @Override
    void readBuffer(ByteBuffer buff, long address) {
        buff.clear();

        byte[] arr = new byte[buff.capacity()];
        
        int oldPos = chan.position();
        
        chan.position((int) address);
        chan.get(arr, 0, arr.length);
        buff.put(arr);
        
        chan.position(oldPos);

        buff.flip();
    }
    
    @Override
    void writeBuffer(ByteBuffer buff) {
        buff.flip();
        
        chan.put(buff);
    }
    
    @Override
    void writeBuffer(ByteBuffer buff, long address) {
        buff.flip();

        for(int i = 0; i < buff.remaining(); i++)
            chan.put((int) (address + i), buff.get(i));
    }

}
