package de.phoenixstaffel.decodetools.gui;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.res.payload.HSEMPayload;
import de.phoenixstaffel.decodetools.res.payload.hsem.HSEMEntry;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

//TODO quick 'n' dirty, make proper
public class HSEMPanel extends PayloadPanel {
    
    private static final long serialVersionUID = -4369075808768544826L;
    
    private transient HSEMPayload selected;
    private final JButton btnNewButton = new JButton("Export as .obj");
    private final JList<HSEMEntry> list = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane();
    
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
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNewButton))
                    .addContainerGap(56, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnNewButton)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                    .addContainerGap())
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
        scrollPane.setViewportView(list);
        list.setListData(selected.getEntries().toArray(new HSEMEntry[0]));
    }
    
    public HSEMPayload getSelectedFile() {
        return selected;
    }
}
