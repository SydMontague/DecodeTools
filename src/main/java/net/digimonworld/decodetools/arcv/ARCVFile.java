package net.digimonworld.decodetools.arcv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.Deflater;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.FileAccess;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.res.DummyResData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.ResPayload.Payload;

public class ARCVFile {
    private File inputDir;
    private boolean compressed;
    
    public ARCVFile(File inputDir, boolean compressed) {
        if (!inputDir.isDirectory())
            throw new IllegalArgumentException("Given File has to be an directory!");
        
        this.inputDir = inputDir;
        this.compressed = compressed;
    }
    
    public ARCVFile(File inputDir) {
        this(inputDir, true);
    }
    
    public void saveFiles(File outputDir) throws IOException {
        File destFile = new File(outputDir, "ARCV0.BIN");
        
        if (destFile.exists() && !destFile.delete())
            return;
        if (!destFile.exists() && !destFile.createNewFile())
            return;
        
        Access destination = new FileAccess(destFile);
        VCRAFile arcvinfo = new VCRAFile();
        RunValues run = new RunValues();
        
        List<File> list = Utils.listFiles(inputDir);
        
        list.parallelStream().filter(a -> !a.getName().endsWith(".bak") && a.isFile()).map(t -> {
            Path filePath = inputDir.toPath().relativize(t.toPath());
            Main.LOGGER.info("Adding " + filePath);
            try {
                return addFile(t.toPath(), filePath.toString().replace("\\", "/")); // fuck windows
            }
            catch (Exception e) {
                Main.LOGGER.log(Level.WARNING, e, () -> "Exception while adding file to ARCVFile: " + filePath);
            }
            
            return null;
        }).forEachOrdered(a -> {
            arcvinfo.addEntry(new VCRAEntry(a.compressedSize, a.uncompressedSize, a.name, run.sectorCount, a.marv));
            destination.writeByteArray(a.output.toByteArray());
            run.sectorCount += (a.output.size() + 0x800 - 1) / 0x800;
            
            run.zipTime += a.zipTime;
            run.resLoadTime += a.loadTime;
            run.resDataTime += a.dataTime;
        });
        
        destination.close();
        arcvinfo.repack(new File(outputDir, "ARCVINFO.BIN"));
        
        Main.LOGGER.info(() -> String.format("ZIP: %d | Res Load: %d | Res Data: %d",
                                             (run.zipTime / 1000000),
                                             (run.resLoadTime / 1000000),
                                             (run.resDataTime / 1000000)));
    }
    
    private AddFileResult addFile(Path a, String name) throws IOException {
        AddFileResult result = new AddFileResult();
        result.name = name;
        
        long timer = System.nanoTime();
        
        // ZIP
        byte[] input = Files.readAllBytes(a);
        boolean shouldCompress = input.length > 0x1000 && compressed;
        int compressedSize = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        if (shouldCompress) {
            Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
            compresser.setInput(input);
            compresser.finish();
            
            byte[] output = new byte[0x800];
            while (!compresser.finished()) {
                Arrays.fill(output, (byte) 0);
                compressedSize += compresser.deflate(output);
                outputStream.write(output);
            }
        }
        else {
            byte[] output = Arrays.copyOf(input, Utils.align(input.length, 0x800));
            compressedSize = input.length;
            outputStream.write(output);
        }
        
        result.output = outputStream;
        result.compressedSize = compressedSize;
        result.uncompressedSize = input.length;
        
        result.zipTime = System.nanoTime() - timer;
        timer = System.nanoTime();
        
        // Res Load
        Access access = new FileAccess(a.toFile(), true);
        ResPayload res = ResPayload.craft(access);
        int kcapSize = res.getType() == Payload.KCAP ? access.readInteger(0x08) : 0;
        access.close();
        
        result.loadTime = System.nanoTime() - timer;
        timer = System.nanoTime();
        
        // Res Data
        int structureSize = res.getSizeOfRoot();
        DummyResData resData = new DummyResData();
        res.fillDummyResData(resData);
        int dataSize = resData.getSize();
        int dataEntries = resData.getDataEntries();
        
        result.dataTime = System.nanoTime() - timer;
        
        if(res.getType() == Payload.KCAP && structureSize != kcapSize) {
            Main.LOGGER.log(Level.WARNING, () -> name + " claimed and calculated KCAP size differs.");
            structureSize = kcapSize;
        }
        
        // sanity checks, MARV generation
        if (input.length - Utils.align(structureSize, 0x80) != dataSize && dataSize != 0) {
            Main.LOGGER.log(Level.WARNING, () -> name + " calculated size and actual size differs. It's format might be invalid, please check and re-export if necessary.");
            dataSize = input.length - Utils.align(structureSize, 0x80);
        }
            
        MARVEntry marv = new MARVEntry(structureSize, dataSize, dataEntries, name.endsWith(".img"));
        if (res.getType() == Payload.GENERIC || res.getType() == Payload.BTX)
            marv = null;
        
        result.marv = marv;
        
        return result;
    }
    
    static class AddFileResult {
        ByteArrayOutputStream output;
        MARVEntry marv;
        String name;
        int uncompressedSize;
        int compressedSize;
        
        long zipTime;
        long loadTime;
        long dataTime;
    }
    
    static class RunValues {
        int sectorCount = 0;
        
        long zipTime = 0;
        long resLoadTime = 0;
        long resDataTime = 0;
    }
}
