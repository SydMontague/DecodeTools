package de.phoenixstaffel.decodetools.dataminer;

import java.io.Closeable;

public interface Access extends Closeable {
    public byte readByte();
    
    public byte readByte(long address);
    
    public short readShort();
    
    public short readShort(long address);
    
    public int readInteger();
    
    public int readInteger(long address);
    
    public long readLong();
    
    public long readLong(long address);
    
    public float readFloat();
    
    public float readFloat(long address);
    
    public double readDouble();
    
    public double readDouble(long address);
    
    public String readString(int length, String encoding);
    
    public String readString(long address, int length, String encoding);

    public byte[] readByteArray(int length);
    
    public byte[] readByteArray(int length, long start);
    
    public void writeByte(byte value);
    
    public void writeByte(byte value, long address);
    
    public void writeShort(short value);
    
    public void writeShort(short value, long address);
    
    public void writeInteger(int value);
    
    public void writeInteger(int value, long address);
    
    public void writeLong(long value);
    
    public void writeLong(long value, long address);
    
    public void writeFloat(float value);
    
    public void writeFloat(float value, long address);
    
    public void writeDouble(double value);
    
    public void writeDouble(double value, long address);
    
    public void writeString(String value, String encoding);
    
    public void writeString(String value, String encoding, long address);

    public void writeByteArray(byte[] data);
    
    public void writeByteArray(byte[] data, long start);
    
    public long getPosition();
    
    public void setPosition(long address);
    
    public String readASCIIString();

    public String readASCIIString(long address);

    public long getSize();
    
    public void setSize(long size);
}
