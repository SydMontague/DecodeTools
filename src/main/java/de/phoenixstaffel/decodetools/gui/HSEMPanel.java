package de.phoenixstaffel.decodetools.gui;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.res.payload.HSEMPayload;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class HSEMPanel extends PayloadPanel {
    
    private static final long serialVersionUID = -4369075808768544826L;
    
    private transient HSEMPayload selected;
    private final JButton btnNewButton = new JButton("Export as .obj");
    
    public HSEMPanel(Object obj) {
        setSelectedFile(obj);
        
        btnNewButton.addActionListener(a -> {
            if (getSelectedFile() == null)
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Output");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showSaveDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try (PrintStream out = new PrintStream(fileDialogue.getSelectedFile())) {
                selected.toObj(out);
            }
            catch (FileNotFoundException e) {
                //
            }
        });
        
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnNewButton)
                    .addContainerGap(375, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnNewButton)
                    .addContainerGap(266, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
        //@formatter:on
    }
    
    @Override
    public void setSelectedFile(Object file) {
        if (file == null)
            return;
        
        if (!(file instanceof HSEMPayload)) {
            Main.LOGGER.warning("Tried to select non-HSEM File in HSEMPanel.");
            return;
        }
        
        selected = (HSEMPayload) file;
    }
    
    public HSEMPayload getSelectedFile() {
        return selected;
    }
    
}
