package de.phoenixstaffel.decodetools.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import de.phoenixstaffel.decodetools.gui.util.FunctionAction;
import de.phoenixstaffel.decodetools.res.payload.GenericPayload;

public class GenericPanel extends PayloadPanel {
    private static final long serialVersionUID = -6205445177831125596L;
    private GenericPayload payload;
    private final JButton btnImportRaw = new JButton("Import Raw");
    
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
}
