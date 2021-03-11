package net.digimonworld.decodetools.gui;

import java.util.Observer;

import javax.swing.JPanel;

public abstract class EditorPanel extends JPanel implements Observer {
    private static final long serialVersionUID = -4112706371340135417L;
    
    private EditorModel model;
    
    public EditorPanel(EditorModel model) {
        setModel(model);
    }
    
    public void setModel(EditorModel model) {
        if (this.model != null)
            this.model.deleteObserver(this);
        this.model = model;
        if (this.model != null)
            this.model.addObserver(this);
    }
    
    public EditorModel getModel() {
        return model;
    }
}
