package net.digimonworld.decodetools.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
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
import net.digimonworld.decodetools.res.payload.BTXPayload.BTXMeta;

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
                    BTXMeta meta = null;
                    
                    if (split.length == 15) {
                        int speaker = Integer.parseInt(split[2]);
                        short unk1 = Short.parseShort(split[3]);
                        byte unk2 = Byte.parseByte(split[4]);
                        byte unk3 = Byte.parseByte(split[5]);
                        int unk4 = Integer.parseInt(split[6]);
                        int unk5 = Integer.parseInt(split[7]);
                        int unk6 = Integer.parseInt(split[8]);
                        int unk7 = Integer.parseInt(split[9]);
                        int unk8 = Integer.parseInt(split[10]);
                        int unk9 = Integer.parseInt(split[11]);
                        int unk10 = Integer.parseInt(split[12]);
                        int unk11 = Integer.parseInt(split[13]);
                        int unk12 = Integer.parseInt(split[14]);
                        meta = new BTXMeta(id, speaker, unk1, unk2, unk3, unk4, unk5, unk6, unk7, unk8, unk9, unk10, unk11, unk12);
                    }
                    list.add(new Tuple<>(id, new BTXEntry(string, meta)));
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
            model.addColumn("Unk4");  
            model.addColumn("Unk5");
            model.addColumn("Unk6");
            model.addColumn("Unk7");
            model.addColumn("Unk8");
            model.addColumn("Unk9");
            model.addColumn("Unk10");
            model.addColumn("Unk11");
            model.addColumn("Unk12");
            
            payload.getEntries().forEach(a -> {
                int id = a.getKey();
                String string = a.getValue().getString();
                Integer speaker = a.getValue().getMeta().map(BTXMeta::getSpeaker).orElseGet(() -> null);
                Short unk1 = a.getValue().getMeta().map(BTXMeta::getUnk1).orElseGet(() -> null);
                Byte unk2 = a.getValue().getMeta().map(BTXMeta::getUnk2).orElseGet(() -> null);
                Byte unk3 = a.getValue().getMeta().map(BTXMeta::getUnk3).orElseGet(() -> null);
                Integer unk4 = a.getValue().getMeta().map(BTXMeta::getUnk4).orElseGet(() -> null);
                Integer unk5 = a.getValue().getMeta().map(BTXMeta::getUnk5).orElseGet(() -> null);
                Integer unk6 = a.getValue().getMeta().map(BTXMeta::getUnk6).orElseGet(() -> null);
                Integer unk7 = a.getValue().getMeta().map(BTXMeta::getUnk7).orElseGet(() -> null);
                Integer unk8 = a.getValue().getMeta().map(BTXMeta::getUnk8).orElseGet(() -> null);
                Integer unk9 = a.getValue().getMeta().map(BTXMeta::getUnk9).orElseGet(() -> null);
                Integer unk10 = a.getValue().getMeta().map(BTXMeta::getUnk10).orElseGet(() -> null);
                Integer unk11 = a.getValue().getMeta().map(BTXMeta::getUnk11).orElseGet(() -> null);
                Integer unk12 = a.getValue().getMeta().map(BTXMeta::getUnk12).orElseGet(() -> null);
                model.addRow(new Object[] { id, string, speaker, unk1, unk2, unk3, unk4, unk5, unk6, unk7, unk8, unk9, unk10, unk11,
                                            unk12 });
            });
            
            table.setModel(model);
        }
    }
}
