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
    private final JList<HSEMEntry> list = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane();
    
    public HSEMPanel(Object obj) {
        setSelectedFile(obj);
        
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(40, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
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
