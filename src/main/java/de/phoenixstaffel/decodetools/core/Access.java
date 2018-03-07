package de.phoenixstaffel.decodetools.core;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Logger;

/**
 * Provides an interface to easily read and write primitive data types into some form of addressable data storage.
 * 
 * It has a current position within the storage that can be queried and modified. Reading and writing will increase the
 * current position respectively. Alternatively data may be read or write with absolute positions without affecting the
 * current position.
 */
public interface Access extends Closeable {
    
    /**
     * Reads a byte from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by 1.
     * </p>
     * 
     * @return the byte read
     */
    public byte readByte();
    
    /**
     * Reads a byte from the underlying data storage from the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the byte read
     */
    public default byte readByteOffset(long offset) {
        return readByte(getPosition() + offset);
    }
    
    /**
     * Reads a byte from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the byte read
     */
    public byte readByte(long address);
    
    /**
     * Reads a short from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by 2.
     * </p>
     * 
     * @return the short read
     */
    public short readShort();
    
    /**
     * Reads a short from the underlying data storage from the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the short read
     */
    public default short readShortOffset(long offset) {
        return readShort(getPosition() + offset);
    }
    
    /**
     * Reads a short from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the short read
     */
    public short readShort(long address);
    
    /**
     * Reads a Java char (16-bit Unicode character) from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by 2.
     * </p>
     * 
     * @return the char read
     */
    public char readChar();
    
    /**
     * Reads a Java char (16-bit Unicode character) from the underlying data storage from the current position with an
     * offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the char read
     */
    public default char readCharOffset(long offset) {
        return readChar(getPosition() + offset);
    }
    
    /**
     * Reads a Java char (16-bit Unicode character) from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the short read
     */
    public char readChar(long address);
    
    /**
     * Reads an integer from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by 4.
     * </p>
     * 
     * @return the integer read
     */
    public int readInteger();
    
    /**
     * Reads an integer from the underlying data storage from the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the integer read
     */
    public default int readIntegerOffset(long offset) {
        return readInteger(getPosition() + offset);
    }
    
    /**
     * Reads an integer from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the integer read
     */
    public int readInteger(long address);
    
    /**
     * Reads a long from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by 8.
     * </p>
     * 
     * @return the long read
     */
    public long readLong();
    
    /**
     * Reads a long from the underlying data storage from the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the long read
     */
    public default long readLongOffset(long offset) {
        return readLong(getPosition() + offset);
    }
    
    /**
     * Reads a long from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the long read
     */
    public long readLong(long address);
    
    /**
     * Reads a float from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by 4.
     * </p>
     * 
     * @return the float read
     */
    public float readFloat();
    
    /**
     * Reads a float from the underlying data storage from the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the float read
     */
    public default float readFloatOffset(long offset) {
        return readFloat(getPosition() + offset);
    }
    
    /**
     * Reads a float from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the float read
     */
    public float readFloat(long address);
    
    /**
     * Reads a double from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by 8.
     * </p>
     * 
     * @return the double read
     */
    public double readDouble();
    
    /**
     * Reads a double from the underlying data storage from the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the double read
     */
    public default double readDoubleOffset(long offset) {
        return readDouble(getPosition() + offset);
    }
    
    /**
     * Reads a double from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the double read
     */
    public double readDouble(long address);
    
    /**
     * Reads a {@link String} from the next number of bytes, given by {@code bytes}, from the current position.
     * The bytes will be decoded into a String with the given {@code encoding}, using the
     * {@link Charset#forName(String)} and {@link Charset#decode(java.nio.ByteBuffer)} methods.
     * <p>
     * This operation increases the current position by {@code bytes}.
     * </p>
     * 
     * @param bytes the number of bytes that should be read for this String
     * @param encoding the encoding of the String, see {@link Charset}
     * @return the {@link String} read
     */
    public String readString(int bytes, String encoding);
    
    /**
     * Reads a {@link String} from the next number of bytes, given by {@code bytes}, from the current position with an
     * offset.
     * The bytes will be decoded into a String with the given {@code encoding}, using the
     * {@link Charset#forName(String)} and {@link Charset#decode(java.nio.ByteBuffer)} methods.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @param bytes the number of bytes that should be read for this String
     * @param encoding the encoding of the String, see {@link Charset}
     * @return the {@link String} read
     */
    public default String readStringOffset(long offset, int bytes, String encoding) {
        return readString(getPosition() + offset, bytes, encoding);
    }
    
    /**
     * Reads a {@link String} from the next number of bytes, given by {@code bytes}, from the given address.
     * The bytes will be decoded into a String with the given {@code encoding}, using the
     * {@link Charset#forName(String)} and {@link Charset#decode(java.nio.ByteBuffer)} methods.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @param bytes the number of bytes that should be read for this String
     * @param encoding the encoding of the String, see {@link Charset}
     * @return the {@link String} read
     */
    public String readString(long address, int bytes, String encoding);
    
    /**
     * Reads a null-terminated {@link String} of one-byte characters from the current position, like a C-styled
     * string/char array.
     * Bytes will be read until a null-terminator (-> 0) has been found. Each read byte will be cast to char using
     * {@code char c = (char) readByte()}.
     * <p>
     * This operation increases the current position by the number of characters read plus the null-terminator.
     * </p>
     * 
     * @return the {@link String} read
     */
    public String readASCIIString();
    
    /**
     * Reads a null-terminated {@link String} of one-byte characters from the current position with an offset,
     * like a C-styled string/char array.
     * Bytes will be read until a null-terminator (-> 0) has been found. Each read byte will be cast to char using
     * {@code char c = (char) readByte()}.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @return the {@link String} read
     */
    public default String readASCIIStringOffset(long offset) {
        return readASCIIString(getPosition() + offset);
    }
    
    /**
     * Reads a null-terminated {@link String} of one-byte characters from the given address, like a C-styled
     * string/char array.
     * Bytes will be read until a null-terminator (-> 0) has been found. Each read byte will be cast to char using
     * {@code char c = (char) readByte()}.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param address the position to read from
     * @return the {@link String} read
     */
    public String readASCIIString(long address);
    
    /**
     * Reads an array of byte from the underlying data storage from the current position.
     * <p>
     * This operation increases the current position by {@code length}.
     * </p>
     * 
     * @param length the length of the array that should be read
     * @return the array of the given {@code length} filled with the data read
     */
    public byte[] readByteArray(int length);
    
    /**
     * Reads an array of byte from the underlying data storage from the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param offset the offset from the current position to read from
     * @param length the length of the array that should be read
     * @return the array of the given {@code length} filled with the data read
     */
    public default byte[] readByteArrayOffset(int length, long offset) {
        return readByteArray(length, getPosition() + offset);
    }
    
    /**
     * Reads an array of byte from the underlying data storage from the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param length the length of the array that should be read
     * @param address the position to read from
     * @return the array of the given {@code length} filled with the data read
     */
    public byte[] readByteArray(int length, long address);
    
    /**
     * Writes a byte to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by 1.
     * </p>
     * 
     * @param value the byte to write
     */
    public void writeByte(byte value);
    
    /**
     * Writes a byte to the underlying data storage at the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the byte to write
     * @param offset the offset from the current position to write to
     */
    public default void writeByteOffset(byte value, long offset) {
        writeByte(value, getPosition() + offset);
    }
    
    /**
     * Writes a byte to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the byte to write
     */
    public void writeByte(byte value, long address);
    
    /**
     * Writes a short to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by 2.
     * </p>
     * 
     * @param value the short to write
     */
    public void writeShort(short value);
    
    /**
     * Writes a short to the underlying data storage at the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the short to write
     * @param offset the offset from the current position to write to
     */
    public default void writeShortOffset(short value, long offset) {
        writeShort(value, getPosition() + offset);
    }
    
    /**
     * Writes a short to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the short to write
     */
    public void writeShort(short value, long address);
    
    /**
     * Writes a Java char (16-bit Unicode character) to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by 2.
     * </p>
     * 
     * @param value the char to write
     */
    public void writeChar(char value);
    
    /**
     * Writes a Java char (16-bit Unicode character) to the underlying data storage at the current position with an
     * offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the char to write
     * @param offset the offset from the current position to write to
     */
    public default void writeCharOffset(char value, long offset) {
        writeChar(value, getPosition() + offset);
    }
    
    /**
     * Writes a Java char (16-bit Unicode character) to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position..
     * </p>
     * 
     * @param value the char to write
     */
    public void writeChar(char value, long address);
    
    /**
     * Writes an integer to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by 4.
     * </p>
     * 
     * @param value the integer to write
     */
    public void writeInteger(int value);
    
    /**
     * Writes a integer to the underlying data storage at the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the integer to write
     * @param offset the offset from the current position to write to
     */
    public default void writeIntegerOffset(int value, long offset) {
        writeInteger(value, getPosition() + offset);
    }
    
    /**
     * Writes a integer to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the integer to write
     */
    public void writeInteger(int value, long address);
    
    /**
     * Writes a long to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by 8.
     * </p>
     * 
     * @param value the long to write
     */
    public void writeLong(long value);
    
    /**
     * Writes a long to the underlying data storage at the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the long to write
     * @param offset the offset from the current position to write to
     */
    public default void writeLongOffset(long value, long offset) {
        writeLong(value, getPosition() + offset);
    }
    
    /**
     * Writes a long to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the long to write
     */
    public void writeLong(long value, long address);
    
    /**
     * Writes a float to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by 4.
     * </p>
     * 
     * @param value the float to write
     */
    public void writeFloat(float value);
    
    /**
     * Writes a float to the underlying data storage at the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the float to write
     * @param offset the offset from the current position to write to
     */
    public default void writeFloatOffset(float value, long offset) {
        writeFloat(value, getPosition() + offset);
    }
    
    /**
     * Writes a float to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the float to write
     */
    public void writeFloat(float value, long address);
    
    /**
     * Writes a double to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by 8.
     * </p>
     * 
     * @param value the double to write
     */
    public void writeDouble(double value);
    
    /**
     * Writes a double to the underlying data storage at the current position with an offset.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the double to write
     * @param offset the offset from the current position to write to
     */
    public default void writeDoubleOffset(int value, long offset) {
        writeDouble(value, getPosition() + offset);
    }
    
    /**
     * Writes a double to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the double to write
     */
    public void writeDouble(double value, long address);
    
    /**
     * Writes a {@link String} to the underlying data storage at the current position.
     * The String will be encoded with the given {@code encoding} using the {@link #getCharset(String)} and
     * {@link String#getBytes(Charset)} methods.
     * <p>
     * This operation increases the current position by the size of the string in bytes.
     * </p>
     * 
     * @param value the String to write
     * @param encoding the encoding of the String, see {@link Charset}
     */
    
    public void writeString(String value, String encoding);
    
    /**
     * Writes a {@link String} to the underlying data storage at the current position with an offset.
     * The String will be encoded with the given {@code encoding} using the {@link #getCharset(String)} and
     * {@link String#getBytes(Charset)} methods.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the integer to write
     * @param encoding the encoding of the String, see {@link Charset}
     * @param offset the offset from the current position to write to
     */
    public default void writeStringOffset(String value, String encoding, long offset) {
        writeString(value, encoding, getPosition() + offset);
    }
    
    /**
     * Writes a {@link String} to the underlying data storage at the given address.
     * The String will be encoded with the given {@code encoding} using the {@link #getCharset(String)} and
     * {@link String#getBytes(Charset)} methods.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param value the String to write
     * @param encoding the encoding of the String, see {@link Charset}
     */
    public void writeString(String value, String encoding, long address);
    
    /**
     * Writes an array of bytes to the underlying data storage at the current position.
     * <p>
     * This operation increases the current position by the number of bytes in the array.
     * </p>
     * 
     * @param data the array to write
     */
    public void writeByteArray(byte[] data);
    
    /**
     * Writes an array of bytes to the underlying data storage at the current position with an offset.
     * <p>
     * This operation increases the current position by the number of bytes in the array.
     * </p>
     * 
     * @param data the array to write
     * @param offset the offset from the current position to write to
     */
    public default void writeByteArrayOffset(byte[] data, long offset) {
        writeByteArray(data, getPosition() + offset);
    }
    
    /**
     * Writes an array of bytes to the underlying data storage at the given address.
     * <p>
     * This operation does not affect the current position.
     * </p>
     * 
     * @param data the array to write
     */
    public void writeByteArray(byte[] data, long start);
    
    /**
     * Gets the current position in the underlying data storage, used for non-absolute access.
     * 
     * @return the position in the data storage, a non-negative long integer
     */
    public long getPosition();
    
    /**
     * Sets the current position in the underlying data storage, used for non-absolute access.
     * 
     * @param address The new position, a non-negative long integer
     */
    public void setPosition(long address);
    
    /**
     * Returns the current size of the underlying data storage.
     * <p>
     * Depending on the implementation this method may be unsupported. In that case it is supposed to throw a
     * {@link UnsupportedOperationException}.
     * 
     * @return The current size of the underlying data storage, measured in bytes
     */
    public long getSize();
    
    /**
     * Helper method to get a {@link Charset} based on its name, using {@link Charset#forName(String)}.
     * If the input is invalid, {@link Charset#defaultCharset()} is returned.
     * 
     * @param charset the name of the charset to use
     * @return the Charset that represents the given name, or {@link Charset#defaultCharset()} is it was invalid.
     */
    default Charset getCharset(String charset) {
        try {
            return Charset.forName(charset);
        }
        catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            Logger.getLogger(Access.class.getName()).severe("Invalid Charset given, falling back to default: " + charset + " Stacktrace: " + e);
            return Charset.defaultCharset();
        }
    }
    
}
