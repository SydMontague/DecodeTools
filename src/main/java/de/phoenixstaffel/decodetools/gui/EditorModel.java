package de.phoenixstaffel.decodetools.gui;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.res.KCAPPayload.Payload;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.payload.GMIOFile;

public class EditorModel extends Observable {
    static final Logger log = Logger.getLogger("Decode Tool");
    
    private ResFile selectedRes;
    private File selectedFile;
    
    private TreeModel treeModel;
    private DefaultListModel<GMIOFile> imageListModel;
    
    public ResFile getSelectedResource() {
        return selectedRes;
    }
    
    public void setSelectedFile(File selectedFile) {
        try (Access access = new FileAccess(selectedFile)) {
            ResFile file = new ResFile(access);
            
            this.selectedRes = file;
            this.treeModel = new DefaultTreeModel(selectedRes.getRoot().getTreeNode());
            this.imageListModel = new DefaultListModel<>();
            selectedRes.getRoot().getElementsWithType(Payload.GMIO).forEach(a -> imageListModel.addElement((GMIOFile) a));
            
            setChanged();
            notifyObservers();
            
        }
        catch (IOException e1) {
            log.log(Level.WARNING, "Error while loading file!", e1);
            return;
        }
    }
    
    public TreeModel getTreeModel() {
        return treeModel;
    }
    
    public ListModel<GMIOFile> getImageListModel() {
        return imageListModel;
    }
    
    public File getSelectedFile() {
        return selectedFile;
    }
    
}
