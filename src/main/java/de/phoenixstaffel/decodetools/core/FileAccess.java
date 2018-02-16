package de.phoenixstaffel.decodetools.core;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of {@link Access} that reads and writes from a File.
 * 
 * As such it can grow in size.
 */
public class FileAccess extends BufferedAccess {
    private static final Logger LOGGER = Logger.getLogger(Access.class.getName());
    
    private static final String ERROR_READ = "FileAccess: failed to read from FileChannel";
    private static final String ERROR_WRITE = "FileAccess: failed to write into FileChannel";
    
    private static final StandardOpenOption[] OPEN_OPTIONS = { StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE };
    private static final StandardOpenOption[] READ_OPTIONS = { StandardOpenOption.READ };
    
    private final String name;
    private final FileChannel chan;
    
    private boolean readOnly = false;
    
    /**
     * Initialises a new instances of this class.
     * 
     * @param chan the FileChannel to read and write from
     * @param name the name to give this instance
     * @param byteOrder the ByteOrder to use when reading/writing, i.e. Big/Little Endian
     */
    public FileAccess(FileChannel chan, String name, ByteOrder byteOrder) {
        super(byteOrder);
        
        this.chan = chan;
        this.name = name;
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
        this(FileChannel.open(file.toPath(), OPEN_OPTIONS), name);
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
        this(FileChannel.open(file.toPath(), OPEN_OPTIONS), name, byteOrder);
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
    public FileAccess(File file, boolean readOnly) throws IOException {
        this(file, file.getPath(), readOnly);
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
    public FileAccess(File file, ByteOrder byteOrder, boolean readOnly) throws IOException {
        this(file, file.getPath(), byteOrder, readOnly);
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
    public FileAccess(File file, String name, boolean readOnly) throws IOException {
        this(FileChannel.open(file.toPath(), readOnly ? READ_OPTIONS : OPEN_OPTIONS), name);
        this.readOnly = readOnly;
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
    public FileAccess(File file, String name, ByteOrder byteOrder, boolean readOnly) throws IOException {
        this(FileChannel.open(file.toPath(), readOnly ? READ_OPTIONS : OPEN_OPTIONS), name, byteOrder);
        this.readOnly = readOnly;
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
            if(address > getSize() && !readOnly) {
                chan.position(getSize());
                chan.write(ByteBuffer.allocate((int) (address - getSize())));
            }
            else
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
    
    @Override
    void readBuffer(ByteBuffer buff) {
        buff.clear();
        try {
            chan.read(buff);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_READ, e);
        }
        buff.flip();
    }
    
    @Override
    void readBuffer(ByteBuffer buff, long address) {
        buff.clear();
        try {
            chan.read(buff, address);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_READ, e);
        }
        buff.flip();
    }
    
    @Override
    void writeBuffer(ByteBuffer buff) {
        buff.flip();
        try {
            chan.write(buff);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_WRITE, e);
        }
    }
    
    @Override
    void writeBuffer(ByteBuffer buff, long address) {
        buff.flip();
        try {
            chan.write(buff, address);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_WRITE, e);
        }
    }
    
}
