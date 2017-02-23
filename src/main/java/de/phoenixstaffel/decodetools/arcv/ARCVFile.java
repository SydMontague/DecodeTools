package de.phoenixstaffel.decodetools.arcv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.IResData;
import de.phoenixstaffel.decodetools.res.KCAPPayload;
import de.phoenixstaffel.decodetools.res.KCAPPayload.Payload;
import de.phoenixstaffel.decodetools.res.ResFile;

//this is crap, but it works...
//FIXME it's slow as fuck and needs optimisation + multi-threading
public class ARCVFile {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private File inputDir;
    
    private Access destination;
    private int sectorCount = 0;
    private VCRAFile arcvinfo;
    
    public ARCVFile(File inputDir) {
        if (!inputDir.isDirectory())
            throw new IllegalArgumentException("Given File has to be an directory!");
        
        this.inputDir = inputDir;
        this.arcvinfo = new VCRAFile();
    }
    
    public void saveFiles(File outputDir) throws IOException {
        File destFile = new File(outputDir, "ARCV0.BIN");
        
        if (destFile.exists() && !destFile.delete())
            return;
        if (!destFile.exists() && !destFile.createNewFile())
            return;
        
        destination = new FileAccess(destFile);
        
        List<File> list = Utils.fileOrder(inputDir);
        
        list.forEach(t -> {
            if (t.isDirectory())
                return;
            
            try {
                log.info("Adding " + inputDir.toPath().relativize(t.toPath()));
                addFile(t.toPath(), inputDir.toPath().relativize(t.toPath()).toString().replaceAll("\\\\", "/")); // fuck windows
            }
            catch (IOException e) {
                log.log(Level.WARNING, "Exception while adding file to ARCVFile.", e);
            }
        });
        
        destination.close();
        arcvinfo.repack(new File(outputDir, "/ARCVINFO.BIN"));
    }
    
    private void addFile(Path a, String name) throws IOException {
        if (Files.isDirectory(a))
            return;
        
        int startSector = sectorCount;
        int compressedSize = 0;
        
        byte[] input = Files.readAllBytes(a);
        
        if (input.length > 0x1000) {
            Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
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
        
        Access access = new FileAccess(a.toFile());
        KCAPPayload res = new ResFile(access).getRoot();
        access.close();
        
        int structureSize = res.getSizeOfRoot();
        IResData resData = new DummyResData();
        res.fillResData(resData);
        int dataSize = resData.getSize();
        int dataEntries = resData.getDataEntries();
        resData.close();
        
        MARVEntry marv = new MARVEntry(structureSize, dataSize, dataEntries);
        if (res.getType() == Payload.GENERIC)
            marv = null;
        
        arcvinfo.addEntry(new VCRAEntry(compressedSize, input.length, name, startSector, marv));
    }
}
