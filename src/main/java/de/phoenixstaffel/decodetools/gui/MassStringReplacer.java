package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.core.Tuple;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload;

public class MassStringReplacer extends JFrame {
    
    private static final long serialVersionUID = -5343568132395772145L;
    private static final String MESSAGE_PROPERTY = "message";
    private static final String PROGRESS_PROPERTY = "progress";
    
    transient Map<String, ResFile> files = new HashMap<>();
    
    private final JLabel folderLabel = new JLabel("Folder:");
    private final JLabel originalLabel = new JLabel("Original String");
    private final JLabel replacementLabel = new JLabel("Replacement String");
    final JButton saveButton = new JButton("Save");
    final JButton openButton = new JButton("Open");
    final JButton findButton = new JButton("Find");
    final JButton replaceButton = new JButton("Replace");
    final JTextArea originalInput = new JTextArea();
    final JTextArea replacementInput = new JTextArea();
    final JProgressBar progressBar = new JProgressBar();
    final JLabel messageLabel = new JLabel("");
    final JLabel pathLabel = new JLabel("(none)");
    
    transient PropertyChangeListener progressListener = b -> {
        if (PROGRESS_PROPERTY.equals(b.getPropertyName())) {
            progressBar.setValue((int) b.getNewValue());
        }
        if (MESSAGE_PROPERTY.equals(b.getPropertyName())) {
            progressBar.setString((String) b.getNewValue());
        }
    };
    
    public MassStringReplacer() {
        setTitle("Mass Text Replacer Tool");
        setResizable(false);
        setSize(500, 360);
        
        openButton.setAction(new OpenAction());
        findButton.setAction(new FindAction());
        replaceButton.setAction(new ReplaceAction());
        saveButton.setAction(new SaveAction());
        
        //@formatter:off
        replacementInput.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        replacementInput.setRows(4);
        originalInput.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        originalInput.setRows(4);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(progressBar, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(folderLabel)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(pathLabel))
                                .addComponent(originalLabel)
                                .addComponent(originalInput, GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                                .addComponent(replacementInput, GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                                .addComponent(messageLabel))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                .addComponent(saveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(openButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(findButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(replaceButton)))
                        .addComponent(replacementLabel))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(openButton)
                        .addComponent(folderLabel)
                        .addComponent(pathLabel))
                    .addGap(11)
                    .addComponent(originalLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(findButton)
                        .addComponent(originalInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(11)
                    .addComponent(replacementLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(replaceButton)
                        .addComponent(replacementInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(saveButton)
                        .addComponent(messageLabel))
                    .addGap(11)
                    .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(14, Short.MAX_VALUE))
        );
        findButton.setEnabled(false);
        replaceButton.setEnabled(false);
        saveButton.setEnabled(false);
        progressBar.setString("");
        progressBar.setStringPainted(true);
        getContentPane().setLayout(groupLayout);
        //@formatter:on
    }
    
    private class FindAction extends AbstractAction {
        private static final long serialVersionUID = 7461199792838793467L;
        
        public FindAction() {
            super("Find");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = originalInput.getText();
            long count = 0;
            long fCount = 0;
            StringBuilder filesFound = new StringBuilder();
            
            for (Entry<String, ResFile> file : files.entrySet()) {
                boolean replaced = false;
                
                for (ResPayload payload : file.getValue().getRoot().getElementsWithType(Payload.BTX)) {
                    BTXPayload btx = (BTXPayload) payload;
                    
                    long lCount = btx.getEntries().stream().map(c -> c.getValue().getString()).filter(c -> c.contains(input)).count();
                    count += lCount;
                    
                    if(lCount > 0)
                        replaced = true;
                }
                
                if(replaced) {
                    filesFound.append(file.getKey()).append("\n");
                    fCount++;
                }
            }
            
            messageLabel.setText("Found: " + count + " in " + fCount + " files.");
            JOptionPane.showMessageDialog(null, "Found: " + count + " in " + fCount + " files:\n" + filesFound.toString());
        }
    }
    
    private class ReplaceAction extends AbstractAction {
        private static final long serialVersionUID = 1673627024945977948L;
        
        public ReplaceAction() {
            super("Replace");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = originalInput.getText();
            String replacement = replacementInput.getText();
            long count = 0;
            long fCount = 0;
            
            for (Entry<String, ResFile> file : files.entrySet()) {
                boolean replaced = false;
                
                for (ResPayload payload : file.getValue().getRoot().getElementsWithType(Payload.BTX)) {
                    BTXPayload btx = (BTXPayload) payload;
                    long lCount = btx.getEntries().stream().map(Tuple::getValue).filter(c -> c.getString().contains(input)).count();
                    count += lCount;
                    btx.getEntries().stream().map(Tuple::getValue).filter(c -> c.getString().contains(input)).forEach(c -> c.setString(c.getString().replace(input, replacement)));
                    if (lCount > 0)
                        replaced = true;
                }
                
                if (replaced)
                    fCount++;
            }
            
            messageLabel.setText("Replaced " + count + " entries in " + fCount + " files.");
        }
    }
    
    private class OpenAction extends AbstractAction {
        private static final long serialVersionUID = 4622073913656406423L;
        
        public OpenAction() {
            super("Open");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setDialogTitle("Please select the directory with the input files");
            fileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileDialogue.showOpenDialog(null);
            
            File f = fileDialogue.getSelectedFile();
            if (f == null)
                return;
            
            LoadFilesTask task = new LoadFilesTask(f);
            
            task.addPropertyChangeListener(progressListener);
            
            pathLabel.setText(f.toString());
            openButton.setEnabled(false);
            findButton.setEnabled(false);
            replaceButton.setEnabled(false);
            saveButton.setEnabled(false);
            task.execute();
        }
        
    }
    
    private class SaveAction extends AbstractAction {
        private static final long serialVersionUID = -5795638388552317623L;
        
        public SaveAction() {
            super("Save");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setDialogTitle("Please select the directory where to store the changed files");
            fileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileDialogue.showSaveDialog(null);
            
            File f = fileDialogue.getSelectedFile();
            if (f == null)
                return;
            
            SaveFilesTask task = new SaveFilesTask(f);
            
            task.addPropertyChangeListener(progressListener);
            
            openButton.setEnabled(false);
            findButton.setEnabled(false);
            replaceButton.setEnabled(false);
            saveButton.setEnabled(false);
            task.execute();
        }
    }
    
    private class SaveFilesTask extends SwingWorker<Void, Void> {
        private File input;
        
        public SaveFilesTask(File dir) {
            this.input = dir;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            int count = 0;
            setProgress(0);
            for (Entry<String, ResFile> file : files.entrySet()) {
                file.getValue().repack(new File(input, file.getKey()));
                setProgress((++count * 100) / files.size());
                firePropertyChange(MESSAGE_PROPERTY, "", count + " of " + files.size() + " files saved.");
            }
            
            return null;
        }
        
        @Override
        protected void done() {
            openButton.setEnabled(true);
            findButton.setEnabled(true);
            replaceButton.setEnabled(true);
            saveButton.setEnabled(true);
        }
    }
    
    private class LoadFilesTask extends SwingWorker<Void, Void> {
        private File input;
        
        public LoadFilesTask(File dir) {
            this.input = dir;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            File[] dirContent = input.listFiles();
            
            int count = 0;
            setProgress(0);
            for (File ff : dirContent) {
                try (FileAccess access = new FileAccess(ff, true)) {
                    try {
                        files.put(ff.getName(), new ResFile(access));
                    }
                    catch(Exception e) {
                        //do nothing
                    }
                }
                setProgress((++count * 100) / dirContent.length);
                firePropertyChange(MESSAGE_PROPERTY, "", count + " of " + dirContent.length + " files loaded.");
            }
            
            return null;
        }
        
        @Override
        protected void done() {
            openButton.setEnabled(true);
            findButton.setEnabled(true);
            replaceButton.setEnabled(true);
            saveButton.setEnabled(true);
        }
    }
}
