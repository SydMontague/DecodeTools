package de.phoenixstaffel.decodetools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.ResFile;

public class Main {
    private static final Logger log = Logger.getLogger("DataMiner");
    
    private Main() {
        // no implementation
    }
    
    public static void main(String[] args) throws IOException {
        //try (Access access = new FileAccess(fileDialogue.getSelectedFile())) {
        //    ResFile file = new ResFile(access);
        //    TreeModel model = new DefaultTreeModel(file.getRoot().getTreeNode());
            new ExampleFrame().setVisible(true);
        //}
    }
}
