package de.phoenixstaffel.decodetools.gui;

import java.util.Observable;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.res.KCAPPayload.Payload;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.payload.GMIOFile;

public class EditorModel extends Observable {
    private ResFile selectedFile;
    private TreeModel treeModel;
    private DefaultListModel<GMIOFile> imageListModel;
    
    public ResFile getSelectedFile() {
        return selectedFile;
    }
    
    public void setSelectedFile(ResFile selectedFile) {
        this.selectedFile = selectedFile;
        this.treeModel = new DefaultTreeModel(selectedFile.getRoot().getTreeNode());
        this.imageListModel = new DefaultListModel<>();
        selectedFile.getRoot().getElementsWithType(Payload.GMIO).forEach(a -> imageListModel.addElement((GMIOFile) a));
        
        setChanged();
        notifyObservers();
    }
    
    public TreeModel getTreeModel() {
        return treeModel;
    }

    public ListModel<GMIOFile> getImageListModel() {
        return imageListModel;
    }
    
}
