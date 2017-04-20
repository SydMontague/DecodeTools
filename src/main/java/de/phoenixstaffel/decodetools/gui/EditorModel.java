package de.phoenixstaffel.decodetools.gui;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;

public class EditorModel extends Observable {
    private ResFile selectedRes;
    private File selectedFile;
    
    private TreeModel treeModel;
    private DefaultListModel<GMIOPayload> imageListModel;
    
    public ResFile getSelectedResource() {
        return selectedRes;
    }
    
    public void setSelectedFile(File selectedFile) {
        try (Access access = new FileAccess(selectedFile)) {
            ResFile file = new ResFile(access);
            
            this.selectedFile = selectedFile;
            this.selectedRes = file;
            this.treeModel = new DefaultTreeModel(selectedRes.getRoot().getTreeNode());
            this.imageListModel = new DefaultListModel<>();
            selectedRes.getRoot().getElementsWithType(Payload.GMIO).forEach(a -> imageListModel.addElement((GMIOPayload) a));
            
            setChanged();
            notifyObservers();
        }
        catch (IOException e1) {
            Main.LOGGER.log(Level.WARNING, "Error while loading file!", e1);
            return;
        }
    }
    
    public TreeModel getTreeModel() {
        return treeModel;
    }
    
    public ListModel<GMIOPayload> getImageListModel() {
        return imageListModel;
    }
    
    public File getSelectedFile() {
        return selectedFile;
    }
    
}
