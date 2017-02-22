package de.phoenixstaffel.decodetools.dataminer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Logger;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import de.phoenixstaffel.decodetools.jna.Kernel32;

public class MemoryAccess implements Access {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private static final int PROCESS_VM_READ = 0x0010;
    private static final int PROCESS_VM_WRITE = 0x0020;
    private static final int PROCESS_VM_OPERATION = 0x0008;
    
    private Kernel32 kernel = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
    private HANDLE process;
    
    private long offset;
    private long currentPointer = 0;
    
    public MemoryAccess(int pid, int offset) {
        if (pid == -1)
            throw new IllegalArgumentException("PID -1 stands for \"no process given\"");
        
        this.offset = offset;
        process = kernel.OpenProcess(PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, false, pid);
    }
    
    @Override
    public byte readByte(long address) {
        Memory memory = new Memory(Byte.BYTES);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, Byte.BYTES, null);
        
        return memory.getByte(0);
    }
    
    @Override
    public short readShort(long address) {
        Memory memory = new Memory(Short.BYTES);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, Short.BYTES, null);
        
        return memory.getShort(0);
    }
    
    @Override
    public int readInteger(long address) {
        Memory memory = new Memory(Integer.BYTES);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, Integer.BYTES, null);
        
        return memory.getInt(0);
    }
    
    @Override
    public long readLong(long address) {
        Memory memory = new Memory(Long.BYTES);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, Long.BYTES, null);
        
        return memory.getLong(0);
    }
    
    @Override
    public float readFloat(long address) {
        Memory memory = new Memory(Float.BYTES);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, Float.BYTES, null);
        
        return memory.getFloat(0);
    }
    
    @Override
    public double readDouble(long address) {
        Memory memory = new Memory(Double.BYTES);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, Double.BYTES, null);
        
        return memory.getFloat(0);
    }
    
    @Override
    public String readString(long address, int length, String charset) {
        Charset localCharset = getCharset(charset);
        
        Memory memory = new Memory(length);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, length, null);

        return localCharset.decode(memory.getByteBuffer(0, length)).toString();
    }
    
    @Override
    public byte readByte() {
        byte value = readByte(currentPointer);
        currentPointer += Byte.BYTES;
        return value;
    }
    
    @Override
    public short readShort() {
        short value = readShort(currentPointer);
        currentPointer += Short.BYTES;
        return value;
    }
    
    @Override
    public int readInteger() {
        int value = readInteger(currentPointer);
        currentPointer += Integer.BYTES;
        return value;
    }
    
    @Override
    public long readLong() {
        long value = readLong(currentPointer);
        currentPointer += Long.BYTES;
        return value;
    }
    
    @Override
    public float readFloat() {
        float value = readFloat(currentPointer);
        currentPointer += Float.BYTES;
        return value;
    }
    
    @Override
    public double readDouble() {
        double value = readDouble(currentPointer);
        currentPointer += Double.BYTES;
        return value;
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
    public String readASCIIString(long address) {
        StringBuilder b = new StringBuilder();

        byte value;
        while((value = readByte(address++)) != 0)
            b.append((char) value);
        
        return b.toString();
    }
    
    @Override
    public String readString(int length, String charset) {
        String value = readString(currentPointer, length, charset);
        currentPointer += length;
        return value;
    }
    
    @Override
    public byte[] readByteArray(int length) {
        byte[] data = readByteArray(length, currentPointer);
        currentPointer += data.length;
        return data;
    }

    @Override
    public byte[] readByteArray(int length, long address) {
        Memory memory = new Memory(length);
        kernel.ReadProcessMemory(process, new Pointer(address + offset), memory, length, null);
        return memory.getByteArray(0, length);
    }
    
    @Override
    public void writeByte(byte value) {
        writeByte(value, currentPointer);
        currentPointer += Byte.BYTES;
    }
    
    @Override
    public void writeByte(byte value, long address) {
        Memory memory = new Memory(Byte.BYTES);
        memory.setByte(0, value);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, Byte.BYTES, null);
    }
    
    @Override
    public void writeShort(short value) {
        writeShort(value, currentPointer);
        currentPointer += Short.BYTES;
    }
    
    @Override
    public void writeShort(short value, long address) {
        Memory memory = new Memory(Short.BYTES);
        memory.setShort(0, value);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, Short.BYTES, null);
    }
    
    @Override
    public void writeInteger(int value) {
        writeInteger(value, currentPointer);
        currentPointer += Integer.BYTES;
    }
    
    @Override
    public void writeInteger(int value, long address) {
        Memory memory = new Memory(Integer.BYTES);
        memory.setInt(0, value);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, Integer.BYTES, null);
    }
    
    @Override
    public void writeLong(long value) {
        writeLong(value, currentPointer);
        currentPointer += Long.BYTES;
    }
    
    @Override
    public void writeLong(long value, long address) {
        Memory memory = new Memory(Long.BYTES);
        memory.setLong(0, value);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, Long.BYTES, null);
    }
    
    @Override
    public void writeFloat(float value) {
        writeFloat(value, currentPointer);
        currentPointer += Float.BYTES;
    }
    
    @Override
    public void writeFloat(float value, long address) {
        Memory memory = new Memory(Float.BYTES);
        memory.setFloat(0, value);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, Float.BYTES, null);
    }
    
    @Override
    public void writeDouble(double value) {
        writeDouble(value, currentPointer);
        currentPointer += Double.BYTES;
    }
    
    @Override
    public void writeDouble(double value, long address) {
        Memory memory = new Memory(Double.BYTES);
        memory.setDouble(0, value);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, Double.BYTES, null);
    }
    
    @Override
    public void writeString(String value, String encoding) {
        byte[] bytes = value.getBytes(getCharset(encoding));
        writeString(value, encoding, currentPointer);
        currentPointer += bytes.length;
    }
    
    @Override
    public void writeString(String value, String encoding, long address) {
        byte[] bytes = value.getBytes(getCharset(encoding));
        Memory memory = new Memory(bytes.length);
        memory.setString(0, value, encoding);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, bytes.length, null);
    }
    
    @Override
    public void writeByteArray(byte[] data) {
        writeByteArray(data, currentPointer);
        currentPointer += data.length;
    }

    @Override
    public void writeByteArray(byte[] data, long address) {
        Memory memory = new Memory(data.length);
        memory.write(0, data, 0, data.length);
        kernel.WriteProcessMemory(process, new Pointer(address + offset), memory, data.length, null);
    }
    
    @Override
    public long getPosition() {
        return currentPointer;
    }
    
    @Override
    public void setPosition(long address) {
        this.currentPointer = address;
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
        kernel.CloseHandle(process);
    }

    @Override
    public long getSize() {
        throw new UnsupportedOperationException("Can't get size of MemoryAccess.");
    }

    @Override
    public void setSize(long size) {
        throw new UnsupportedOperationException("Can't set size of MemoryAccess.");
    }
}
