package de.phoenixstaffel.decodetools.arcv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.Deflater;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.KCAPPayload.Payload;
import de.phoenixstaffel.decodetools.res.ResFile;

//this is crap, but it works...
public class ARCVFile {
    private File inputDir;
    private boolean compressed;
    
    private Access destination;
    private int sectorCount = 0;
    private VCRAFile arcvinfo;
    
    private long zipTime = 0;
    private long resLoadTime = 0;
    private long resDataTime = 0;
    
    public ARCVFile(File inputDir, boolean compressed) {
        if (!inputDir.isDirectory())
            throw new IllegalArgumentException("Given File has to be an directory!");
        
        this.inputDir = inputDir;
        this.compressed = compressed;
        this.arcvinfo = new VCRAFile();
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
        
        destination = new FileAccess(destFile);
        
        List<File> list = Utils.fileOrder(inputDir);
        
        list.stream().filter(a -> !a.getName().endsWith(".bak")).forEach(t -> {
            if (t.isDirectory())
                return;
            
            try {
                Main.LOGGER.info("Adding " + inputDir.toPath().relativize(t.toPath()));
                addFile(t.toPath(), inputDir.toPath().relativize(t.toPath()).toString().replaceAll("\\\\", "/")); // fuck windows
            }
            catch (IOException e) {
                Main.LOGGER.log(Level.WARNING, "Exception while adding file to ARCVFile.", e);
            }
        });
        
        destination.close();
        arcvinfo.repack(new File(outputDir, "/ARCVINFO.BIN"));
        
        Main.LOGGER.info("ZIP: " + (zipTime / 1000000) + " | Res Load: " + (resLoadTime / 1000000) + " | Res Data: " + (resDataTime / 1000000));
    }
    
    private void addFile(Path a, String name) throws IOException {
        if (Files.isDirectory(a))
            return;
        
        int startSector = sectorCount;
        int compressedSize = 0;
        
        long t = System.nanoTime();
        
        byte[] input = Files.readAllBytes(a);
        
        if (input.length > 0x1000 && compressed) {
            Deflater compresser = new Deflater(Deflater.DEFAULT_COMPRESSION);
            compresser.setInput(input);
            compresser.finish();
            
            byte[] output = new byte[0x800];
            while (!compresser.finished()) {
                compressedSize += compresser.deflate(output);
                destination.writeByteArray(output);
                sectorCount++;
            }
            compresser.end();
        }
        else {
            byte[] output = Arrays.copyOf(input, Utils.getPadded(input.length, 0x800));
            compressedSize = input.length;
            destination.writeByteArray(output);
            sectorCount += output.length / 0x800;
        }
        
        zipTime += System.nanoTime() - t;
        t = System.nanoTime();
        
        Access access = new FileAccess(a.toFile());
        KCAPPayload res = new ResFile(access).getRoot();
        access.close();
        
        resLoadTime += System.nanoTime() - t;
        t = System.nanoTime();
        
        int structureSize = res.getSizeOfRoot();
        DummyResData resData = new DummyResData();
        res.fillDummyResData(resData);
        int dataSize = resData.getSize();
        int dataEntries = resData.getDataEntries();
        resData.close();
        
        resDataTime += System.nanoTime() - t;
        
        MARVEntry marv = new MARVEntry(structureSize, dataSize, dataEntries);
        if (res.getType() == Payload.GENERIC)
            marv = null;
        
        arcvinfo.addEntry(new VCRAEntry(compressedSize, input.length, name, startSector, marv));
    }
}
