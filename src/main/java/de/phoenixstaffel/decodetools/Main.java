package de.phoenixstaffel.decodetools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIPropertyStore;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;

import de.phoenixstaffel.decodetools.arcv.ARCVFile;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.gui.JLogWindow;
import de.phoenixstaffel.decodetools.gui.MainWindow;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.kcap.HSEMKCAP;
import de.phoenixstaffel.decodetools.res.kcap.HSMPKCAP;
import de.phoenixstaffel.decodetools.res.kcap.NormalKCAP;
import de.phoenixstaffel.decodetools.res.kcap.TNOJKCAP;
import de.phoenixstaffel.decodetools.res.kcap.XDIPKCAP;
import de.phoenixstaffel.decodetools.res.kcap.XTVPKCAP;
import de.phoenixstaffel.decodetools.res.payload.HSEMPayload;
import de.phoenixstaffel.decodetools.res.payload.TNOJPayload;
import de.phoenixstaffel.decodetools.res.payload.XDIOPayload;
import de.phoenixstaffel.decodetools.res.payload.XTVOPayload;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEM07Entry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMDrawEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMJointEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMMaterialEntry;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMTextureEntry;
import de.phoenixstaffel.decodetools.res.payload.xdio.XDIOFace;
import de.phoenixstaffel.decodetools.res.payload.xtvo.XTVOAttribute;
import de.phoenixstaffel.decodetools.res.payload.xtvo.XTVORegisterType;
import de.phoenixstaffel.decodetools.res.payload.xtvo.XTVOValueType;
import de.phoenixstaffel.decodetools.res.payload.xtvo.XTVOVertex;

//TODO store settings and preferences
public class Main {
    public static final Logger LOGGER = Logger.getLogger("Decode Tool");
    
    private Main() {
        // no implementation
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 3 && "rebuild".equalsIgnoreCase(args[0])) {
            File input = new File(args[1]);
            File output = new File(args[2]);
            
            if (!input.isDirectory()) {
                LOGGER.severe("The input value must be a directory.");
                return;
            }
            
            if (!output.isDirectory())
                output.mkdirs();
            
            ARCVFile arcv = new ARCVFile(input, true);
            arcv.saveFiles(output);
        }
        else {
            new JLogWindow(LOGGER, Logger.getLogger(Access.class.getName())).setVisible(true);
            new MainWindow().setVisible(true);
        }
    }
    
    static class Bla {
        AINode node;
        int depth;
        
        public Bla(AINode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }
}
