package net.digimonworld.decodetools.gui;

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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.digimonworld.decodetools.core.Tuple;
import net.digimonworld.decodetools.gui.util.FunctionAction;
import net.digimonworld.decodetools.res.payload.BTXPayload;
import net.digimonworld.decodetools.res.payload.BTXPayload.BTXEntry;

import javax.swing.JScrollPane;

public class BTXPanel extends PayloadPanel {
    private BTXPayload payload;
    private final JButton btnNewButton = new JButton("New button");
    private final JTable table = new JTable();
    private final JScrollPane scrollPane = new JScrollPane();
    
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
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                        .addComponent(btnNewButton))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnNewButton)
                    .addGap(9)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .addContainerGap())
        );
        scrollPane.setViewportView(table);
        setLayout(groupLayout);
    }
    
    @Override
    public void setSelectedFile(Object file) {
        this.payload = null;
        
        if (file instanceof BTXPayload) {
            this.payload = (BTXPayload) file;        
        
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("String");
            model.addColumn("Speaker");
            model.addColumn("Unk1");
            model.addColumn("Unk2");
            model.addColumn("Unk3");
            
            payload.getEntries().forEach(a -> {
                int id = a.getKey();
                String string = a.getValue().getString();
                Integer speaker = a.getValue().getMeta().isPresent() ? a.getValue().getMeta().get().getSpeaker() : null;
                Short unk1 = a.getValue().getMeta().isPresent() ? a.getValue().getMeta().get().getUnk1() : null;
                Byte unk2 = a.getValue().getMeta().isPresent() ? a.getValue().getMeta().get().getUnk2() : null;
                Byte unk3 = a.getValue().getMeta().isPresent() ? a.getValue().getMeta().get().getUnk3() : null;
                
                model.addRow(new Object[] { id, string, speaker, unk1, unk2, unk3 });
            });
            
            table.setModel(model);
        }
    }
}
