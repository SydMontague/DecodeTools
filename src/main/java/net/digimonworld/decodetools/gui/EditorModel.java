package net.digimonworld.decodetools.gui;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Access;
import net.digimonworld.decodetools.core.FileAccess;
import net.digimonworld.decodetools.gui.util.ResPayloadTreeNodeFactory;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.ResPayload.Payload;
import net.digimonworld.decodetools.res.payload.GMIOPayload;

public class EditorModel extends Observable {
    private ResPayload selectedRes;
    private File selectedFile;
    
    private TreeModel treeModel;
    private DefaultListModel<GMIOPayload> imageListModel;
    
    public ResPayload getSelectedResource() {
        return selectedRes;
    }
    
    public void setSelectedFile(File selectedFile) {
        try (Access access = new FileAccess(selectedFile, true)) {
            ResPayload file = ResPayload.craft(access);
            
            this.selectedFile = selectedFile;
            setSelectedResource(file);
        }
        catch (IOException e1) {
            Main.LOGGER.log(Level.WARNING, "Error while loading file!", e1);
            return;
        }
    }
    
    public void setSelectedResource(ResPayload res) {
        this.selectedRes = res;
        update();
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

    /**
     * Update the models in this class and notify the listeners.
     */
    public void update() {
        this.treeModel = new DefaultTreeModel(ResPayloadTreeNodeFactory.craft(selectedRes));
        this.imageListModel = new DefaultListModel<>();
        selectedRes.getElementsWithType(Payload.GMIO).forEach(a -> imageListModel.addElement((GMIOPayload) a));
        
        setChanged();
        notifyObservers();        
    }
}
