package de.phoenixstaffel.decodetools.dataminer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileAccess implements Access {
    private static final Logger log = Logger.getLogger("DataMiner");
    private static final String ERROR_READ = "FileAccess: failed to read a from FileChannel";
    private static final String ERROR_WRITE = "FileAccess: failed to write into a FileChannel";
    
    // We're not thread safe, as we read a file, so we can reuse the buffers
    private final ByteBuffer byteBuf = ByteBuffer.allocate(1);
    private final ByteBuffer shortBuf = ByteBuffer.allocate(2);
    private final ByteBuffer intBuf = ByteBuffer.allocate(4);
    private final ByteBuffer longBuf = ByteBuffer.allocate(8);
    
    private final String name;
    private FileChannel chan;
    
    public FileAccess(FileChannel chan, String name) {
        this.chan = chan;
        this.name = name;
    }
    
    public FileAccess(FileChannel chan) {
        this(chan, "");
    }
    
    public FileAccess(File file, String name) throws IOException {
        this.name = name;
        this.chan = FileChannel.open(file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
        
        byteBuf.order(ByteOrder.LITTLE_ENDIAN);
        shortBuf.order(ByteOrder.LITTLE_ENDIAN);
        intBuf.order(ByteOrder.LITTLE_ENDIAN);
        longBuf.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public FileAccess(File file) throws IOException {
        this(file, file.getPath());
    }
    
    public FileChannel getChannel() {
        return chan;
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
    
    public String getName() {
        return name;
    }

    @Override
    public String readASCIIString() {
        StringBuilder b = new StringBuilder();

        byte value;
        while((value = readByte()) != 0)
            b.append((char) value);
        
        return b.toString();
    }
    
    @Override
    public String readString(int length, String charset) {
        Charset localCharset = getCharset(charset);
        
        ByteBuffer buff = ByteBuffer.allocate(length);
        readBuffer(buff);
        return localCharset.decode(buff).toString();
    }
    
    @Override
    public String readString(long address, int length, String charset) {
        Charset localCharset = getCharset(charset);
        
        ByteBuffer buff = ByteBuffer.allocate(length);
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
            log.log(Level.SEVERE, ERROR_READ, e);
            return -1;
        }
    }
    
    @Override
    public void setPosition(long address) {
        try {
            chan.position(address);
        }
        catch (IOException e) {
            log.log(Level.SEVERE, ERROR_READ, e);
        }
    }
    
    private void readBuffer(ByteBuffer buff) {
        buff.clear();
        try {
            chan.read(buff);
        }
        catch (IOException e) {
            log.log(Level.SEVERE, ERROR_READ, e);
        }
        buff.flip();
    }
    
    private void readBuffer(ByteBuffer buff, long address) {
        buff.clear();
        try {
            chan.read(buff, address);
        }
        catch (IOException e) {
            log.log(Level.SEVERE, ERROR_READ, e);
        }
        buff.flip();
    }
    
    private void writeBuffer(ByteBuffer buff) {
        buff.flip();
        try {
            chan.write(buff);
        }
        catch (IOException e) {
            log.log(Level.SEVERE, ERROR_WRITE, e);
        }
    }
    
    private void writeBuffer(ByteBuffer buff, long address) {
        buff.flip();
        try {
            chan.write(buff, address);
        }
        catch (IOException e) {
            log.log(Level.SEVERE, ERROR_WRITE, e);
        }
    }
    
    private Charset getCharset(String charset) {
        try {
            return Charset.forName(charset);
        }
        catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            log.severe("Invalid Charset given, falling back to default: " + charset + " Stacktrace: " + e);
            return Charset.defaultCharset();
        }
    }

    @Override
    public void close() throws IOException {
        chan.close();
    }
}
