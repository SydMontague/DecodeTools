package de.phoenixstaffel.decodetools.arcv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.FileAccess;

/*
 * VCRA Format – 0x20
 * int - magic value (VCRA)
 * int - unknown (version?)
 * int - number of entries
 * int - unknown (sector size?)
 * int - size of VCRA structure
 * int - unknown
 * int - unknown
 * int - unknown
 */
public class VCRAFile {
    private static final String MAGIC_VALUE = "VCRA";
    private static final int VERSION = 0x00000102; // ?
    
    private static final int SECTOR_SIZE = 0x800;
    
    private List<VCRAEntry> entries = new ArrayList<>();
    
    private int unknown1;
    private int unknown2;
    private int unknown3;
    
    public VCRAFile(Access source) {
        int numEntries = source.readInteger(0x8);
        source.setPosition(0x20);
        
        for (int i = 0; i < numEntries; i++)
            entries.add(new VCRAEntry(source));
        
        unknown1 = source.readInteger(0x14);
        unknown2 = source.readInteger(0x18);
        unknown3 = source.readInteger(0x1C);
    }
    
    public VCRAFile() {
        unknown1 = 1;
        unknown2 = 0;
        unknown3 = 0;
    }
    
    public void addEntry(VCRAEntry entry) {
        entries.add(entry);
    }
    
    public void extractARCV(Path inputARCV, Path outputDir) {
        try(Access arcv = new FileAccess(inputARCV.toFile())) {
            for(VCRAEntry entry : entries) {
                
                Path outputFile = outputDir.resolve(entry.getPath());
                byte[] raw = arcv.readByteArray(entry.getCompressedSize(), entry.getSector() * 0x800L);
                byte[] output;
                
                if(entry.getCompressedSize() == entry.getUnpackedSize())
                    output = raw;
                else {
                    output = new byte[entry.getUnpackedSize()];
                    Inflater inflater = new Inflater();
                    inflater.setInput(raw);
                    inflater.inflate(output);
                }
                
                Files.createDirectories(outputFile.getParent());
                Files.write(outputFile, output, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
        catch (IOException | DataFormatException e) {
            Main.LOGGER.log(Level.SEVERE, "Exception while extracting ARCV: ", e);
        }
    }
    
    public void repack(File file) {
        if (file.exists() && !file.delete())
            return;
        try {
            if (!file.createNewFile())
                return;
        }
        catch (IOException e1) {
            Main.LOGGER.log(Level.WARNING, "Exception while creating new ARCVINFO.BIN.", e1);
        }
        
        try (Access access = new FileAccess(file)) {
            int marvStart = 0x20 + entries.size() * 0x20;
            int pathStart = (int) (marvStart + entries.stream().filter(a -> a.getMARV() != null).count() * 0x20);
            int totalSize = pathStart + entries.stream().collect(Collectors.summingInt(a -> a.getPath().length() + 1));
            
            access.writeString(MAGIC_VALUE, "ASCII");
            access.writeInteger(VERSION);
            access.writeInteger(entries.size());
            access.writeInteger(SECTOR_SIZE);
            access.writeInteger(totalSize);
            access.writeInteger(unknown1);
            access.writeInteger(unknown2);
            access.writeInteger(unknown3);
            
            List<VCRAEntry> listCopy = new ArrayList<>(entries);
            listCopy.sort(new VCRAPathComperator());
            
            Map<VCRAEntry, Integer> map = new HashMap<>();
            
            for (VCRAEntry entry : entries) {
                if (entry.getMARV() != null) {
                    access.writeByteArray(entry.getMARV().getBytes(), marvStart);
                    map.put(entry, marvStart);
                    marvStart += 0x20;
                }
            }
            
            for (VCRAEntry entry : listCopy) {
                access.writeInteger(0x00000000);
                access.writeInteger(entry.getCompressedSize());
                access.writeInteger(entry.getUnpackedSize());
                access.writeInteger(pathStart);
                access.writeInteger(entry.getSector());
                access.writeInteger(map.containsKey(entry) ? map.get(entry) : 0);
                access.writeInteger(0x00000000);
                access.writeInteger(entry.isCompressed() ? 0x00000001 : 0x00000000);
                access.writeString(entry.getPath() + (char) 0, "ASCII", pathStart);
                pathStart += entry.getPath().length() + 1;
            }
        }
        catch (IOException e) {
            Main.LOGGER.log(Level.WARNING, "Exception while writing new ARCVINFO.BIN.", e);
        }
    }
    
}

class VCRAPathComperator implements Comparator<VCRAEntry> {
    @Override
    public int compare(VCRAEntry o1, VCRAEntry o2) {
        if (o1.getPath().equals(o2.getPath()))
            return 0;
        
        char[] carray1 = o1.getPath().toCharArray();
        char[] carray2 = o2.getPath().toCharArray();
        
        for (int i = 0; i < Math.min(carray1.length, carray2.length); i++) {
            char c1 = carray1[i];
            char c2 = carray2[i];
            
            if (c1 == c2)
                continue;
            
            int result = compareCharacter(c1, c2);
            if (result != 0)
                return result;
            
            return Character.compare(c1, c2);
        }
        
        return carray1.length > carray2.length ? -1 : 0;
    }
    
    private int compareCharacter(char c1, char c2) {
        if (Character.getType(c1) == Character.LOWERCASE_LETTER && Character.getType(c2) == Character.UPPERCASE_LETTER)
            return 1;
        
        if (Character.getType(c2) == Character.LOWERCASE_LETTER && Character.getType(c1) == Character.UPPERCASE_LETTER)
            return -1;
        
        if (c2 == File.separatorChar && c1 != File.separatorChar)
            return 1;
        
        if (c1 == File.separatorChar && c2 != File.separatorChar)
            return -1;
        
        return 0;
    }
}
