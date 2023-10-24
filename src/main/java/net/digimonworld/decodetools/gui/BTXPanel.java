package net.digimonworld.decodetools.gui;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import net.digimonworld.decodetools.core.Tuple;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.gui.util.FunctionAction;
import net.digimonworld.decodetools.res.payload.BTXPayload;
import net.digimonworld.decodetools.res.payload.BTXPayload.BTXEntry;
import net.digimonworld.decodetools.res.payload.BTXPayload.BTXMeta;

public class BTXPanel extends PayloadPanel {
    private BTXPayload payload;
    private final JButton importCSVButton = new JButton("Import CSV");
    private final JButton exportCSVButton = new JButton("Export CSV");
    private final JTable table = new JTable();
    private final JScrollPane scrollPane = new JScrollPane();
    
    public BTXPanel(Object object) {
        setSelectedFile(object);
        
        exportCSVButton.setAction(new FunctionAction("Export CSV", this::exportCSV));
        importCSVButton.setAction(new FunctionAction("Import CSV", this::importCSV));
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                  .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                                       .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                                                            .addComponent(scrollPane,
                                                                                                          GroupLayout.DEFAULT_SIZE,
                                                                                                          430,
                                                                                                          Short.MAX_VALUE)
                                                                                            .addGroup(groupLayout.createSequentialGroup()
                                                                                                                 .addComponent(importCSVButton)
                                                                                                                 .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                 .addComponent(exportCSVButton,
                                                                                                                               GroupLayout.PREFERRED_SIZE,
                                                                                                                               99,
                                                                                                                               GroupLayout.PREFERRED_SIZE)))
                                                                       .addContainerGap()));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                                                          .addComponent(importCSVButton)
                                                                                          .addComponent(exportCSVButton))
                                                                     .addGap(9)
                                                                     .addComponent(scrollPane,
                                                                                   GroupLayout.DEFAULT_SIZE,
                                                                                   246,
                                                                                   Short.MAX_VALUE)
                                                                     .addContainerGap()));
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
            model.addColumn("Voice Line");
            
            payload.getEntries().forEach(a -> {
                int id = a.getKey();
                String string = a.getValue().getString();
                Integer speaker = a.getValue().getMeta().map(BTXMeta::getSpeaker).orElseGet(() -> null);
                Short unk1 = a.getValue().getMeta().map(BTXMeta::getUnk1).orElseGet(() -> null);
                Short unk2 = a.getValue().getMeta().map(BTXMeta::getUnk2).orElseGet(() -> null);
                Short unk3 = a.getValue().getMeta().map(BTXMeta::getUnk3).orElseGet(() -> null);
                Short unk4 = a.getValue().getMeta().map(BTXMeta::getUnk4).orElseGet(() -> null);
                String voiceLine = a.getValue().getMeta().map(BTXMeta::getVoiceLine).orElseGet(() -> null);
                model.addRow(new Object[] { id, string, speaker, unk1, unk2, unk3, unk4, voiceLine });
            });
            
            table.setModel(model);
        }
    }
    
    private void importCSV(ActionEvent event) {
        JFileChooser inputFileDialogue = new JFileChooser("./");
        inputFileDialogue.setDialogTitle("Which file to import?");
        inputFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
        inputFileDialogue.showOpenDialog(null);
        
        File file = inputFileDialogue.getSelectedFile();
        
        // TODO switch to Utils.csvToBTX
        List<Tuple<Integer, BTXEntry>> list = payload.getEntries();
        list.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines().forEach(b -> {
                String[] split = b.split(";");
                
                if (split[0].contains("id"))
                    return;
                
                int id = Integer.parseInt(split[0]);
                String string = split[1];
                if (string.startsWith("\"") && string.endsWith("\""))
                    string = string.substring(1, string.length() - 1);
                string = string.replace("\\n", "\n");
                string = string.replace("\"\"", "\"");
                string = string.replace("\\1", ";");
                
                BTXMeta meta = null;
                
                if (split.length >= 7) {
                    int speaker = Integer.parseInt(split[2]);
                    short unk1 = Short.parseShort(split[3]);
                    short unk2 = Short.parseShort(split[4]);
                    short unk3 = Short.parseShort(split[5]);
                    short unk4 = Short.parseShort(split[6]);
                    
                    String voiceLine = split.length == 8 ? split[7] : "";
                    if (voiceLine.startsWith("\"") && voiceLine.endsWith("\""))
                        voiceLine = voiceLine.substring(1, voiceLine.length() - 1);
                    meta = new BTXMeta(id, speaker, unk1, unk2, unk3, unk4, voiceLine);
                }
                list.add(new Tuple<>(id, new BTXEntry(string, meta)));
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void exportCSV(ActionEvent event) {
        JFileChooser exportFileDialogue = new JFileChooser("./");
        exportFileDialogue.setDialogTitle("Which file to import?");
        exportFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
        exportFileDialogue.showSaveDialog(null);
        
        File file = exportFileDialogue.getSelectedFile();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id;string;speaker;unk1;unk2;unk3;unk4;voiceLine\n");
            String string = payload.getEntries().stream().map(Utils::btxToCSV).collect(Collectors.joining("\n"));
            
            writer.write(string);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
