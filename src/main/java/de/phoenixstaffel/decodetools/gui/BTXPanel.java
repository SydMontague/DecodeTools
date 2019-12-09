package de.phoenixstaffel.decodetools.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import de.phoenixstaffel.decodetools.core.Tuple;
import de.phoenixstaffel.decodetools.gui.util.FunctionAction;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload.BTXEntry;

public class BTXPanel extends PayloadPanel {
    private BTXPayload payload;
    private final JButton btnNewButton = new JButton("New button");
    
    public BTXPanel(Object object) {
        setSelectedFile(object);
        
        btnNewButton.setAction(new FunctionAction("Import CSV", a -> {
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Which file to import?");
            inputFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            inputFileDialogue.showSaveDialog(null);
            
            File file = inputFileDialogue.getSelectedFile();
            
            List<Tuple<Integer, BTXEntry>> list = payload.getEntries();
            list.clear();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.lines().forEach(b -> {
                    String[] split = b.split(";");
                    int id = Integer.parseInt(split[0]);
                    String string = split[1];
                    
                    list.add(new Tuple<>(id, new BTXEntry(string, null)));
                });
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }));
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnNewButton)
                    .addContainerGap(351, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnNewButton)
                    .addContainerGap(266, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
    }
    
    @Override
    public void setSelectedFile(Object file) {
        this.payload = null;
        
        if (file instanceof BTXPayload)
            this.payload = (BTXPayload) file;        
    }
    
}
