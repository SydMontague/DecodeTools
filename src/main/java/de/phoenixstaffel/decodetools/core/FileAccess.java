package de.phoenixstaffel.decodetools.core;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileAccess implements Access {
    private static final Logger LOGGER = Logger.getLogger(Access.class.getName());
    
    private static final String ERROR_READ = "FileAccess: failed to read from FileChannel";
    private static final String ERROR_WRITE = "FileAccess: failed to write into FileChannel";
    
    private static final StandardOpenOption openOptions[] = { StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, };
    
    // We're not thread safe, as we read a file, so we can reuse the buffers
    private final ByteBuffer byteBuf = ByteBuffer.allocate(1);
    private final ByteBuffer shortBuf = ByteBuffer.allocate(2);
    private final ByteBuffer intBuf = ByteBuffer.allocate(4);
    private final ByteBuffer longBuf = ByteBuffer.allocate(8);
    
    private final String name;
    private final FileChannel chan;
    
    /**
     * Initialises a new instances of this class.
     * 
     * @param chan the FileChannel to read and write from
     * @param name the name to give this instance
     * @param byteOrder the ByteOrder to use when reading/writing, i.e. Big/Little Endian
     */
    public FileAccess(FileChannel chan, String name, ByteOrder byteOrder) {
        this.chan = chan;
        this.name = name;
        
        byteBuf.order(byteOrder);
        shortBuf.order(byteOrder);
        intBuf.order(byteOrder);
        longBuf.order(byteOrder);
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * The byte order will be set to Little Endian.
     * </p>
     * 
     * @param chan the FileChannel to read and write from
     * @param name the name to give this instance
     */
    public FileAccess(FileChannel chan, String name) {
        this(chan, name, ByteOrder.LITTLE_ENDIAN);
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * The name will be set to an empty String.
     * </p>
     * 
     * @param chan the FileChannel to read and write from
     * @param byteOrder the ByteOrder to use when reading/writing, i.e. Big/Little Endian
     */
    public FileAccess(FileChannel chan, ByteOrder byteOrder) {
        this(chan, "", byteOrder);
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * The name will be set to an empty String. The byte order will be set to Little Endian.
     * </p>
     * 
     * @param chan the FileChannel to read and write from
     */
    public FileAccess(FileChannel chan) {
        this(chan, "");
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * A FileChannel will be opened based on the given file, using
     * {@link FileChannel#open(java.nio.file.Path, java.nio.file.OpenOption...)}. The name will be set to the name of
     * the file. The byte order will be set to Little Endian.
     * </p>
     * 
     * @param file the file to read and write from
     * @throws IOException if anything goes wrong opening the file
     */
    public FileAccess(File file) throws IOException {
        this(file, file.getPath());
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * A FileChannel will be opened based on the given file, using
     * {@link FileChannel#open(java.nio.file.Path, java.nio.file.OpenOption...)}. The name will be set to the name of
     * the file.
     * </p>
     * 
     * @param file the file to read and write from
     * @param byteOrder the ByteOrder to use when reading/writing, i.e. Big/Little Endian
     * @throws IOException if anything goes wrong opening the file
     */
    public FileAccess(File file, ByteOrder byteOrder) throws IOException {
        this(file, file.getPath(), byteOrder);
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * A FileChannel will be opened based on the given file, using
     * {@link FileChannel#open(java.nio.file.Path, java.nio.file.OpenOption...)}. The byte order will be set to Little
     * Endian.
     * </p>
     * 
     * @param file the file to read and write from
     * @param name the name to give this instance
     * @throws IOException if anything goes wrong opening the file
     */
    public FileAccess(File file, String name) throws IOException {
        this(FileChannel.open(file.toPath(), openOptions), name);
    }
    
    /**
     * Initialises a new instances of this class.
     * <p>
     * A FileChannel will be opened based on the given file, using
     * {@link FileChannel#open(java.nio.file.Path, java.nio.file.OpenOption...)}.
     * </p>
     * 
     * @param file the file to read and write from
     * @param name the name to give this instance
     * @param byteOrder the ByteOrder to use when reading/writing, i.e. Big/Little Endian
     * @throws IOException if anything goes wrong opening the file
     */
    public FileAccess(File file, String name, ByteOrder byteOrder) throws IOException {
        this(FileChannel.open(file.toPath(), openOptions), name, byteOrder);
    }
    
    @Override
    public long readLong(long position) {
        readBuffer(longBuf, position);
        return longBuf.getLong();
    }
    
    @Override
    public int readInteger(long position) {
        readBuffer(intBuf, position);
        return intBuf.getInt();
    }
    
    @Override
    public short readShort(long position) {
        readBuffer(shortBuf, position);
        return shortBuf.getShort();
    }
    
    @Override
    public byte readByte(long position) {
        readBuffer(byteBuf, position);
        return byteBuf.get();
    }
    
    @Override
    public float readFloat(long address) {
        readBuffer(intBuf, address);
        return intBuf.getFloat();
    }
    
    @Override
    public double readDouble(long address) {
        readBuffer(longBuf, address);
        return longBuf.getDouble();
    }
    
    @Override
    public long readLong() {
        readBuffer(longBuf);
        return longBuf.getLong();
    }
    
    @Override
    public int readInteger() {
        readBuffer(intBuf);
        return intBuf.getInt();
    }
    
    @Override
    public short readShort() {
        readBuffer(shortBuf);
        return shortBuf.getShort();
    }
    
    @Override
    public byte readByte() {
        readBuffer(byteBuf);
        return byteBuf.get();
    }
    
    @Override
    public float readFloat() {
        readBuffer(intBuf);
        return intBuf.getFloat();
    }
    
    @Override
    public double readDouble() {
        readBuffer(longBuf);
        return longBuf.getFloat();
    }
    
    @Override
    public String readASCIIString() {
        StringBuilder b = new StringBuilder();
        
        byte value;
        while ((value = readByte()) != 0)
            b.append((char) value);
        
        return b.toString();
    }
    
    @Override
    public String readASCIIString(long address) {
        StringBuilder b = new StringBuilder();
        
        byte value;
        while ((value = readByte(address++)) != 0)
            b.append((char) value);
        
        return b.toString();
    }
    
    @Override
    public String readString(int bytes, String charset) {
        Charset localCharset = getCharset(charset);
        
        ByteBuffer buff = ByteBuffer.allocate(bytes);
        readBuffer(buff);
        return localCharset.decode(buff).toString();
    }
    
    @Override
    public String readString(long address, int bytes, String charset) {
        Charset localCharset = getCharset(charset);
        
        ByteBuffer buff = ByteBuffer.allocate(bytes);
        readBuffer(buff, address);
        return localCharset.decode(buff).toString();
    }
    
    @Override
    public byte[] readByteArray(int length) {
        byte[] data = new byte[length];
        
        ByteBuffer buff = ByteBuffer.wrap(data);
        readBuffer(buff);
        
        return data;
    }
    
    @Override
    public byte[] readByteArray(int length, long start) {
        byte[] data = new byte[length];
        
        ByteBuffer buff = ByteBuffer.wrap(data);
        readBuffer(buff, start);
        
        return data;
    }
    
    @Override
    public void writeByte(byte value) {
        byteBuf.clear();
        byteBuf.put(value);
        
        writeBuffer(byteBuf);
    }
    
    @Override
    public void writeByte(byte value, long address) {
        byteBuf.clear();
        byteBuf.put(value);
        
        writeBuffer(byteBuf, address);
    }
    
    @Override
    public void writeShort(short value) {
        shortBuf.clear();
        shortBuf.putShort(value);
        
        writeBuffer(shortBuf);
    }
    
    @Override
    public void writeShort(short value, long address) {
        shortBuf.clear();
        shortBuf.putShort(value);
        
        writeBuffer(shortBuf, address);
    }
    
    @Override
    public void writeInteger(int value) {
        intBuf.clear();
        intBuf.putInt(value);
        
        writeBuffer(intBuf);
    }
    
    @Override
    public void writeInteger(int value, long address) {
        intBuf.clear();
        intBuf.putInt(value);
        
        writeBuffer(intBuf, address);
    }
    
    @Override
    public void writeLong(long value) {
        longBuf.clear();
        longBuf.putLong(value);
        
        writeBuffer(longBuf);
    }
    
    @Override
    public void writeLong(long value, long address) {
        longBuf.clear();
        longBuf.putLong(value);
        
        writeBuffer(longBuf, address);
    }
    
    @Override
    public void writeFloat(float value) {
        intBuf.clear();
        intBuf.putFloat(value);
        
        writeBuffer(intBuf);
    }
    
    @Override
    public void writeFloat(float value, long address) {
        intBuf.clear();
        intBuf.putFloat(value);
        
        writeBuffer(intBuf, address);
    }
    
    @Override
    public void writeDouble(double value) {
        longBuf.clear();
        longBuf.putDouble(value);
        
        writeBuffer(longBuf);
    }
    
    @Override
    public void writeDouble(double value, long address) {
        longBuf.clear();
        longBuf.putDouble(value);
        
        writeBuffer(longBuf, address);
    }
    
    @Override
    public void writeString(String value, String charset) {
        Charset localCharset = getCharset(charset);
        byte[] bytes = value.getBytes(localCharset);
        
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        writeBuffer(buffer);
    }
    
    @Override
    public void writeString(String value, String charset, long address) {
        Charset localCharset = getCharset(charset);
        byte[] bytes = value.getBytes(localCharset);
        
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        
        writeBuffer(buffer, address);
    }
    
    @Override
    public void writeByteArray(byte[] data) {
        ByteBuffer buff = ByteBuffer.allocate(data.length);
        buff.put(data);
        writeBuffer(buff);
    }
    
    @Override
    public void writeByteArray(byte[] data, long start) {
        ByteBuffer buff = ByteBuffer.allocate(data.length);
        buff.put(data);
        writeBuffer(buff, start);
    }
    
    @Override
    public long getPosition() {
        try {
            return chan.position();
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_READ, e);
            return -1;
        }
    }
    
    @Override
    public void setPosition(long address) {
        try {
            chan.position(address);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_READ, e);
        }
    }
    
    @Override
    public long getSize() {
        try {
            return chan.size();
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not get size of FileChannel", e);
            return -1;
        }
    }
    
    @Override
    public void close() throws IOException {
        chan.close();
    }
    
    /**
     * Returns the {@link FileChannel} this instance is using.
     * 
     * @return the FileChannel used
     */
    public FileChannel getChannel() {
        return chan;
    }
    
    /**
     * Returns the name of the FileAccess, as given to the constructor.
     * 
     * @return the name of the FileAccess
     */
    public String getName() {
        return name;
    }
    
    private void readBuffer(ByteBuffer buff) {
        buff.clear();
        try {
            chan.read(buff);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_READ, e);
        }
        buff.flip();
    }
    
    private void readBuffer(ByteBuffer buff, long address) {
        buff.clear();
        try {
            chan.read(buff, address);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_READ, e);
        }
        buff.flip();
    }
    
    private void writeBuffer(ByteBuffer buff) {
        buff.flip();
        try {
            chan.write(buff);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_WRITE, e);
        }
    }
    
    private void writeBuffer(ByteBuffer buff, long address) {
        buff.flip();
        try {
            chan.write(buff, address);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_WRITE, e);
        }
    }
}
