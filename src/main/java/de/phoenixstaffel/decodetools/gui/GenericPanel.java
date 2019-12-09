package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import de.phoenixstaffel.decodetools.gui.util.FunctionAction;
import de.phoenixstaffel.decodetools.res.payload.GenericPayload;

public class GenericPanel extends PayloadPanel {
    
    private GenericPayload payload;
    private final JButton btnImportRaw = new JButton("Import Raw");
    private final Action action = new SwingAction();
    
    public GenericPanel(Object selected) {
        setSelectedFile(selected);
        
        btnImportRaw.setAction(new FunctionAction("Import Raw", (a) -> {
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Which file to import?");
            inputFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            inputFileDialogue.showSaveDialog(null);
            
            File file = inputFileDialogue.getSelectedFile();
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                payload.setData(bytes);
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }));
        
        add(btnImportRaw);
    }
    
    @Override
    public void setSelectedFile(Object file) {
        this.payload = null;
        
        if (file instanceof GenericPayload)
            this.payload = (GenericPayload) file;
    }
    
    private class SwingAction extends AbstractAction {
        public SwingAction() {
            putValue(NAME, "SwingAction");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        
        public void actionPerformed(ActionEvent e) {
        }
    }
}
