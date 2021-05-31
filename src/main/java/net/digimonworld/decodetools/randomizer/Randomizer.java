package net.digimonworld.decodetools.randomizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import net.digimonworld.decodetools.arcv.ARCVFile;
import net.digimonworld.decodetools.arcv.VCRAFile;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.DeleteDirectoryFileVisitor;
import net.digimonworld.decodetools.core.FileAccess;
import net.digimonworld.decodetools.core.Utils;

// TODO name and package temporary
public class Randomizer {
    
    private Randomizer() {
    }
    
    /*-
     *  working/
     *  part0/
     *  exefs/
     *      banner.bnr
     *      code.bin
     *      icon.icn
     *      logo.darc.lz
     *  romfs/
     *      library/
     *      movie/
     *      sound/
     *  arcv/
     *      <extracted ARCV>
     *  exfsheader.bin
     *  exheader.bin
     *  ncchheader.bin
     *  plain.bin
     *  part1.bin
     *  part7.bin
     */
    
    public static void downloadDependencies() {
        // 3dstool
        // ctrtool
        // makerom
    }
    
    private static final String _3DSTOOL = "3dstool";
    private static final String CRTTOOL = "ctrtool";
    private static final String MAKEROM = "makerom";
    private static final String ARMIPS = "armips";
    
    public static boolean extractCIAFile(Path working, Path inputFile) {
        try {
            Files.createDirectories(working.resolve("part0/exefs"));
            Files.createDirectories(working.resolve("part0/romfs"));
            Files.createDirectories(working.resolve("part0/arcv"));
            
            Path data = Files.createTempFile("data", ".bin");
            Path exeFS = Files.createTempFile("exeFS", ".bin");
            Path romFS = Files.createTempFile("romFS", ".bin");
            
            Path tmd = Files.createTempFile("tmd", ".bin");
            Path tik = Files.createTempFile("tik", ".bin");
            Path meta = Files.createTempFile("meta", ".bin");
            Path certs = Files.createTempFile("certs", ".bin");

            ProcessBuilder extractROM = new ProcessBuilder().command(CRTTOOL, 
                                                                     "-v",
                                                                     inputFile.toString(),
                                                                     String.format("--tmd=%s", tmd.toString()),
                                                                     String.format("--contents=%s", data.toString()),
                                                                     String.format("--tik=%s", tik.toString()),
                                                                     String.format("--meta=%s", meta.toString()),
                                                                     String.format("--certs=%s", certs.toString()));
            
            ProcessBuilder extractPart0 = new ProcessBuilder().command(_3DSTOOL,
                                                                       "-xf",
                                                                       data.toString() + ".0000.00000000",
                                                                       "--header",
                                                                       working.resolve("part0/ncchheader.bin").toString(),
                                                                       "--exh",
                                                                       working.resolve("part0/exheader.bin").toString(),
                                                                       "--plain",
                                                                       working.resolve("part0/plain.bin").toString(),
                                                                       "--exefs",
                                                                       exeFS.toString(),
                                                                       "--romfs",
                                                                       romFS.toString());

            ProcessBuilder extractExeFS = new ProcessBuilder().command(_3DSTOOL,
                                                                       "-xfu",
                                                                       exeFS.toString(),
                                                                       "--exefs-dir",
                                                                       working.resolve("part0/exefs/").toString(),
                                                                       "--header",
                                                                       working.resolve("part0/exefsheader.bin").toString());
            
            ProcessBuilder extractRomFS = new ProcessBuilder().command(_3DSTOOL,
                                                                       "-xf",
                                                                       romFS.toString(),
                                                                       "--romfs-dir",
                                                                       working.resolve("part0/romfs/").toString());

            Path arcvinfo = working.resolve("part0/romfs/ARCVINFO.BIN");
            Path arcv0 = working.resolve("part0/romfs/ARCV0.BIN");

            
            boolean success = extractROM.start().waitFor() == 0 
                           && extractPart0.start().waitFor() == 0 
                           && extractExeFS.start().waitFor() == 0
                           && extractRomFS.start().waitFor() == 0;
            
            if (success) {
                Files.move(Path.of(data.toString() + ".0001.00000001"), working.resolve("part1.bin"));
                try (Access access = new FileAccess(arcvinfo.toFile())) {
                    new VCRAFile(access).extractARCV(arcv0, working.resolve("part0/arcv"));
                }
            }
            
            Files.deleteIfExists(arcv0);
            Files.deleteIfExists(arcvinfo);
            Files.deleteIfExists(data);
            Files.deleteIfExists(Path.of(data.toString() + ".0000.00000000"));
            Files.deleteIfExists(Path.of(data.toString() + ".0001.00000001"));
            Files.deleteIfExists(exeFS);
            Files.deleteIfExists(romFS);
            Files.deleteIfExists(tmd);
            Files.deleteIfExists(tik);
            Files.deleteIfExists(meta);
            Files.deleteIfExists(certs);
            
            return success;
        }

        catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean extract3DSFile(Path working, Path inputFile) {
        try {
            Files.createDirectories(working.resolve("part0/exefs"));
            Files.createDirectories(working.resolve("part0/romfs"));
            Files.createDirectories(working.resolve("part0/arcv"));
            
            Path part0 = Files.createTempFile("part0", ".bin");
            Path exeFS = Files.createTempFile("exeFS", ".bin");
            Path romFS = Files.createTempFile("romFS", ".bin");
            
            ProcessBuilder extractROM = new ProcessBuilder().command(_3DSTOOL,
                                                                     "-fx017",
                                                                     inputFile.toString(),
                                                                     part0.toString(),
                                                                     working.resolve("part1.bin").toString(),
                                                                     working.resolve("part7.bin").toString(),
                                                                     "--header",
                                                                     working.resolve("header.bin").toString());
            
            ProcessBuilder extractPart0 = new ProcessBuilder().command(_3DSTOOL,
                                                                       "-xf",
                                                                       part0.toString(),
                                                                       "--header",
                                                                       working.resolve("part0/ncchheader.bin").toString(),
                                                                       "--exh",
                                                                       working.resolve("part0/exheader.bin").toString(),
                                                                       "--plain",
                                                                       working.resolve("part0/plain.bin").toString(),
                                                                       "--exefs",
                                                                       exeFS.toString(),
                                                                       "--romfs",
                                                                       romFS.toString());
            
            ProcessBuilder extractExeFS = new ProcessBuilder().command(_3DSTOOL,
                                                                       "-xfu",
                                                                       exeFS.toString(),
                                                                       "--exefs-dir",
                                                                       working.resolve("part0/exefs/").toString(),
                                                                       "--header",
                                                                       working.resolve("part0/exefsheader.bin").toString());
            
            ProcessBuilder extractRomFS = new ProcessBuilder().command(_3DSTOOL,
                                                                       "-xf",
                                                                       romFS.toString(),
                                                                       "--romfs-dir",
                                                                       working.resolve("part0/romfs/").toString());
            
            Path arcvinfo = working.resolve("part0/romfs/ARCVINFO.BIN");
            Path arcv0 = working.resolve("part0/romfs/ARCV0.BIN");
            
            boolean success = extractROM.start().waitFor() == 0 
                           && extractPart0.start().waitFor() == 0 
                           && extractExeFS.start().waitFor() == 0
                           && extractRomFS.start().waitFor() == 0;
            
            if (success)
                try (Access access = new FileAccess(arcvinfo.toFile())) {
                    new VCRAFile(access).extractARCV(arcv0, working.resolve("part0/arcv"));
                }
            
            Files.deleteIfExists(arcv0);
            Files.deleteIfExists(arcvinfo);
            Files.deleteIfExists(part0);
            Files.deleteIfExists(exeFS);
            Files.deleteIfExists(romFS);
            
            return success;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static void handleCodeASMFile(Path path, Path tempDir) {
        try {
            Path codeBin = tempDir.resolve("part0/exefs/code.bin");
            Path codeBin2 = tempDir.resolve("part0/exefs/code2.bin");
            Files.copy(codeBin, codeBin2);
            Files.delete(codeBin);
            Files.copy(codeBin2, codeBin);
            Files.delete(codeBin2);
            ProcessBuilder applyASM = new ProcessBuilder().command(ARMIPS, path.toAbsolutePath().toString()).directory(tempDir.toFile());
            applyASM.start().waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean rebuildCIA(Path inputDir, Path outputROM, List<Path> modFolders) {
        try {
            Path tempDir = Files.createTempDirectory(Paths.get("."), "rebuild");
            Path buildDir = Files.createTempDirectory(Paths.get("."), "build");
            
            
            // TODO formalise rebuild dir, allowing more forms of modification such as xdelta
            Utils.listFiles(inputDir.toFile()).stream().map(File::toPath).forEach(a -> createTmpLink(tempDir, a, inputDir.relativize(a)));
            modFolders.forEach(modFolder -> Utils.listFiles(modFolder.toFile()).stream().map(File::toPath).forEach(a -> {
                if (a.toString().endsWith("code.bin.asm"))
                    handleCodeASMFile(a, tempDir);
                else
                    createTmpLink(tempDir, a, modFolder.relativize(a));
            }));
            
            new ARCVFile(tempDir.resolve("part0/arcv").toFile()).saveFiles(tempDir.resolve("part0/romfs").toFile());
            
            ProcessBuilder buildRomFS = new ProcessBuilder().inheritIO().command(_3DSTOOL,
                                                                     "-c",
                                                                     "--type",
                                                                     "romfs",
                                                                     "-f",
                                                                     buildDir.resolve("romfs.bin").toString(),
                                                                     "--romfs-dir",
                                                                     tempDir.resolve("part0/romfs").toString());
            
            ProcessBuilder buildExeFS = new ProcessBuilder().inheritIO().command(_3DSTOOL,
                                                                     "-cz",
                                                                     "--type",
                                                                     "exefs",
                                                                     "-f",
                                                                     buildDir.resolve("exefs.bin").toString(),
                                                                     "--exefs-dir",
                                                                     tempDir.resolve("part0/exefs").toString(),
                                                                     "--header",
                                                                     tempDir.resolve("part0/exefsheader.bin").toString());
            
            ProcessBuilder buildCXI = new ProcessBuilder().inheritIO().command(_3DSTOOL,
                                                                   "-ctf",
                                                                   "cxi",
                                                                   buildDir.resolve("part0.bin").toString(),
                                                                   "--header",
                                                                   tempDir.resolve("part0/ncchheader.bin").toString(),
                                                                   "--exh",
                                                                   tempDir.resolve("part0/exheader.bin").toString(),
                                                                   "--plain",
                                                                   tempDir.resolve("part0/plain.bin").toString(),
                                                                   "--exefs",
                                                                   buildDir.resolve("exefs.bin").toString(),
                                                                   "--romfs",
                                                                   buildDir.resolve("romfs.bin").toString(),
                                                                   "--not-encrypt");
            
            ProcessBuilder buildCIA = new ProcessBuilder().inheritIO().command(MAKEROM,
                                                                   "-f",
                                                                   "cia",
                                                                   "-content",
                                                                   buildDir.resolve("part0.bin").toString() + ":0:0",
                                                                   "-content",
                                                                   tempDir.resolve("part1.bin").toString() + ":1:1",
                                                                   "-o",
                                                                   outputROM.toString(),
                                                                   "-ignoresign");
            
            boolean success = buildRomFS.start().waitFor() == 0 
                    && buildExeFS.start().waitFor() == 0 
                    && buildCXI.start().waitFor() == 0
                    && buildCIA.start().waitFor() == 0;
            
            Files.walkFileTree(tempDir, new DeleteDirectoryFileVisitor());
            Files.walkFileTree(buildDir, new DeleteDirectoryFileVisitor());
            
            return success;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean rebuild3DS(Path inputDir, Path outputROM, List<Path> modFolders) {
        if(!Files.exists(inputDir.resolve("header.bin")))
            return false;
        
        try {
            Path tempDir = Files.createTempDirectory(Paths.get("."), "rebuild");
            Path buildDir = Files.createTempDirectory(Paths.get("."), "build");
            
            
            // TODO formalise rebuild dir, allowing more forms of modification such as xdelta
            Utils.listFiles(inputDir.toFile()).stream().map(File::toPath).forEach(a -> createTmpLink(tempDir, a, inputDir.relativize(a)));
            modFolders.forEach(modFolder -> Utils.listFiles(modFolder.toFile()).stream().map(File::toPath).forEach(a -> {
                if (a.toString().endsWith("code.bin.asm"))
                    handleCodeASMFile(a, tempDir);
                else
                    createTmpLink(tempDir, a, modFolder.relativize(a));
            }));
            
            new ARCVFile(tempDir.resolve("part0/arcv").toFile()).saveFiles(tempDir.resolve("part0/romfs").toFile());
            
            ProcessBuilder buildRomFS = new ProcessBuilder().inheritIO().command(_3DSTOOL,
                                                                     "-c",
                                                                     "--type",
                                                                     "romfs",
                                                                     "-f",
                                                                     buildDir.resolve("romfs.bin").toString(),
                                                                     "--romfs-dir",
                                                                     tempDir.resolve("part0/romfs").toString());
            
            ProcessBuilder buildExeFS = new ProcessBuilder().inheritIO().command(_3DSTOOL,
                                                                     "-cz",
                                                                     "--type",
                                                                     "exefs",
                                                                     "-f",
                                                                     buildDir.resolve("exefs.bin").toString(),
                                                                     "--exefs-dir",
                                                                     tempDir.resolve("part0/exefs").toString(),
                                                                     "--header",
                                                                     tempDir.resolve("part0/exefsheader.bin").toString());
            
            ProcessBuilder buildCXI = new ProcessBuilder().inheritIO().command(_3DSTOOL,
                                                                   "-ctf",
                                                                   "cxi",
                                                                   buildDir.resolve("part0.bin").toString(),
                                                                   "--header",
                                                                   tempDir.resolve("part0/ncchheader.bin").toString(),
                                                                   "--exh",
                                                                   tempDir.resolve("part0/exheader.bin").toString(),
                                                                   "--plain",
                                                                   tempDir.resolve("part0/plain.bin").toString(),
                                                                   "--exefs",
                                                                   buildDir.resolve("exefs.bin").toString(),
                                                                   "--romfs",
                                                                   buildDir.resolve("romfs.bin").toString(),
                                                                   "--not-encrypt");
            
            ProcessBuilder buildCCI = new ProcessBuilder().inheritIO().command(_3DSTOOL,
                                                                   "-ctf017",
                                                                   "cci",
                                                                   outputROM.toString(),
                                                                   buildDir.resolve("part0.bin").toString(),
                                                                   tempDir.resolve("part1.bin").toString(),
                                                                   tempDir.resolve("part7.bin").toString(),
                                                                   "--header",
                                                                   tempDir.resolve("header.bin").toString(),
                                                                   "--not-pad");
            
            boolean success = buildRomFS.start().waitFor() == 0 
                    && buildExeFS.start().waitFor() == 0 
                    && buildCXI.start().waitFor() == 0
                    && buildCCI.start().waitFor() == 0;
            
            Files.walkFileTree(tempDir, new DeleteDirectoryFileVisitor());
            Files.walkFileTree(buildDir, new DeleteDirectoryFileVisitor());
            
            return success;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static final void createTmpLink(Path tempDir, Path target, Path path) {
        try {
            Path link = tempDir.resolve(path);
            
            Files.createDirectories(link.getParent());
            Files.deleteIfExists(link);
            Files.createLink(link, target);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
