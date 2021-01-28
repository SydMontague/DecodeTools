package de.phoenixstaffel.decodetools.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * An abstract implementation of {@link Access} that reads and writes into buffers.
 */
public abstract class BufferedAccess implements Access {

    // We're not thread safe, as we read a file, so we can reuse the buffers
    private final ByteBuffer byteBuf = ByteBuffer.allocate(1);
    private final ByteBuffer shortBuf = ByteBuffer.allocate(2);
    private final ByteBuffer intBuf = ByteBuffer.allocate(4);
    private final ByteBuffer longBuf = ByteBuffer.allocate(8);
    
    protected BufferedAccess(ByteOrder byteOrder) {
        byteBuf.order(byteOrder);
        shortBuf.order(byteOrder);
        intBuf.order(byteOrder);
        longBuf.order(byteOrder);
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
    public char readChar(long position) {
        readBuffer(shortBuf, position);
        return shortBuf.getChar();
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
    public char readChar() {
        readBuffer(shortBuf);
        return shortBuf.getChar();
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
    public void writeChar(char value) {
        shortBuf.clear();
        shortBuf.putChar(value);
        
        writeBuffer(shortBuf);
    }

    @Override
    public void writeChar(char value, long address) {
        shortBuf.clear();
        shortBuf.putChar(value);
        
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
    
    abstract void readBuffer(ByteBuffer buff);
    
    abstract void readBuffer(ByteBuffer buff, long address);
    
    abstract void writeBuffer(ByteBuffer buff);
    
    abstract void writeBuffer(ByteBuffer buff, long address);
}
