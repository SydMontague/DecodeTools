package de.phoenixstaffel.decodetools.res;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.ByteChannel;
import java.util.logging.Logger;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.ExampleFrame;
import de.phoenixstaffel.decodetools.Utils;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.payload.KCAPFile;

//TODO padding is a mess, either try to reproduce the padding from the original files, or screw it and do it without
public class ResFile {
    private static final Logger log = Logger.getLogger("DataMiner");
    private Access source;
    
    private KCAPFile root;
    
    public ResFile(Access source) {
        this.source = source;
        
        int dataStart = Utils.getPadded(source.readInteger(0x8), 0x80);
        System.out.println(dataStart);
        root = new KCAPFile(source, dataStart, null);
        
        System.out.println(root.getSize());
        
        //TreeModel model = new DefaultTreeModel(root.getTreeNode());
        //new ExampleFrame(model).setVisible(true);
        
        repack(new File("newRes.res"));
    }
    
    public void repack(File file) {
        file.delete();
        if(!file.exists())
            try {
                file.createNewFile();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        
        try (Access dest = new FileAccess(file); ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            root.writeKCAP(dest, data);
            dest.setPosition(Utils.getPadded(root.getSizeOfRoot(), 0x80));
            dest.writeByteArray(data.toByteArray());
        }
        catch (IOException e) {
            log.warning("Exception while writing new .res file.");
            e.printStackTrace();
        }
    }
}
