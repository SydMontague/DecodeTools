package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

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

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.core.Tuple;
import de.phoenixstaffel.decodetools.gui.util.LinebreakUtil;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload.BTXEntry;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MassStringReplacer extends JFrame {
    private static final long serialVersionUID = -5343568132395772145L;
    private static final String MESSAGE_PROPERTY = "message";
    private static final String PROGRESS_PROPERTY = "progress";
    
    private transient Map<String, ResFile> files = new HashMap<>();
    private transient Set<String> changedFiles = new HashSet<>();
    
    private TNFOPayload linebreakFont = null;
    
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
    private final JLabel fontSizeLabel = new JLabel("Font Size:");
    private final JSpinner lineLimitSpinner = new JSpinner();
    private final JButton linebreakButton = new JButton("Redo Linebreaks");
    private final JButton prefixButton = new JButton("Prefix Stuff");
    private final JButton digitterLinebreakButton = new JButton("New button");
    private final JButton loadFontButton = new JButton("Load Font");
    private final JSpinner maxWidthSpinner = new JSpinner();
    private final JLabel maxWidthLabel = new JLabel("Max Width");
    private final JButton cleanupButton = new JButton("Cleanup Strings");
    
    public MassStringReplacer() {
        maxWidthSpinner.setModel(new SpinnerNumberModel(252, 100, 480, 1));
        lineLimitSpinner.setModel(new SpinnerNumberModel(10.0, 1.0, 999.0, 0.1));
        setTitle("Mass Text Replacer Tool");
        setSize(600, 500);
        
        openButton.setAction(new OpenAction());
        findButton.setAction(new FindAction());
        replaceButton.setAction(new ReplaceAction());
        saveButton.setAction(new SaveAction());
        linebreakButton.setAction(new LinebreakFixerAction());
        prefixButton.setAction(new PrefixStuffAction());
        digitterLinebreakButton.setAction(new DigitterLinebreakFixerAction());
        loadFontButton.setAction(new LoadFontAction());
        cleanupButton.setAction(new CleanupAction());
        
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
                        .addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(folderLabel)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(pathLabel))
                                .addComponent(originalLabel)
                                .addComponent(originalInput, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                                .addComponent(replacementInput, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                                .addComponent(messageLabel))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                .addComponent(saveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(openButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(findButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(replaceButton)))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(fontSizeLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lineLimitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(maxWidthLabel)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(maxWidthSpinner, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(loadFontButton)
                            .addGap(76)
                            .addComponent(prefixButton))
                        .addComponent(replacementLabel)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(linebreakButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(digitterLinebreakButton)
                            .addPreferredGap(ComponentPlacement.RELATED, 251, Short.MAX_VALUE)
                            .addComponent(cleanupButton)))
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
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(replacementLabel)
                    .addGap(11)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(replaceButton)
                        .addComponent(replacementInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(saveButton)
                        .addComponent(messageLabel))
                    .addGap(11)
                    .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(fontSizeLabel)
                        .addComponent(lineLimitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(prefixButton)
                        .addComponent(loadFontButton)
                        .addComponent(maxWidthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(maxWidthLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(linebreakButton)
                        .addComponent(digitterLinebreakButton)
                        .addComponent(cleanupButton))
                    .addContainerGap(82, Short.MAX_VALUE))
        );
        findButton.setEnabled(false);
        replaceButton.setEnabled(false);
        saveButton.setEnabled(false);
        progressBar.setString("");
        progressBar.setStringPainted(true);
        getContentPane().setLayout(groupLayout);
        //@formatter:on
    }
    
    private class CleanupAction extends AbstractAction {
        private static final long serialVersionUID = 561757485888416842L;

        public CleanupAction() {
            super("Cleanup Strings");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            long count = 0;
            int sizeDiff = 0;
            long fCount = 0;

            for (Entry<String, ResFile> file : files.entrySet()) {
                boolean replaced = false;
                
                for (ResPayload payload : file.getValue().getRoot().getElementsWithType(Payload.BTX)) {
                    BTXPayload btx = (BTXPayload) payload;
                    
                    for(Tuple<Integer, BTXEntry> entry : btx.getEntries()) {
                        String s = entry.getValue().getString();
                        String newString = s.replaceAll("(?m)(^ +)|( +$)", "").replaceAll("( {2,})", " ");
                        
                        if(!s.equals(newString)) {
                            
                            Main.LOGGER.info(() -> "Old String: " + s);
                            Main.LOGGER.info(() -> "New String: " + newString);
                            
                            replaced = true;
                            count++;
                            sizeDiff = s.length() - newString.length();
                            
                            entry.getValue().setString(newString);
                        }
                    }
                }
                
                if (replaced) {
                    changedFiles.add(file.getKey());
                    fCount++;
                }
            }
            
            messageLabel.setText("Cleaned up: " + count + " strings in " + fCount + " files. Total: " + sizeDiff);
        }
    }
    
    private class LoadFontAction extends AbstractAction {
        private static final long serialVersionUID = -2044589566004599107L;

        public LoadFontAction() {
            super("Load Font");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setDialogTitle("Please select GlobalKeepRes with the font you want to use");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showOpenDialog(null);
            
            File f = fileDialogue.getSelectedFile();
            if (f == null)
                return;

            messageLabel.setText("Font loaded!");
            
            try(FileAccess access = new FileAccess(f)) {
                ResFile res = new ResFile(access);
                linebreakFont = (TNFOPayload) res.getRoot().getElementsWithType(Payload.TNFO).get(0);
            }
            catch (Exception e1) {
                Main.LOGGER.warning(() -> "Error while loading font, did you enter the GlobalKeepRes.res?");
                messageLabel.setText("Error while loading font!");
            }
        }
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
                
                List<String> btxIds = new ArrayList<>();
                int id = 0;
                
                for (ResPayload payload : file.getValue().getRoot().getElementsWithType(Payload.BTX)) {
                    BTXPayload btx = (BTXPayload) payload;
                    
                    long lCount = btx.getEntries().stream().map(c -> c.getValue().getString()).filter(c -> c.contains(input)).count();
                    count += lCount;
                    
                    if (lCount > 0) {
                        replaced = true;
                        btxIds.add(Integer.toString(id));
                    }
                    id++;
                }
                
                if (replaced) {
                    filesFound.append(file.getKey()).append(" | ").append(String.join(", ", btxIds)).append("\n");
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
                    btx.getEntries().stream().map(Tuple::getValue).filter(c -> c.getString().contains(input))
                       .forEach(c -> c.setString(c.getString().replace(input, replacement)));
                    if (lCount > 0)
                        replaced = true;
                }
                
                if (replaced) {
                    fCount++;
                    changedFiles.add(file.getKey());
                }
            }
            
            messageLabel.setText("Replaced " + count + " entries in " + fCount + " files.");
        }
    }
    
    private class LinebreakFixerAction extends AbstractAction {
        private static final long serialVersionUID = 2568092425475577644L;
        
        public LinebreakFixerAction() {
            super("Fix Linebreaks");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            double fontSize = (double) lineLimitSpinner.getValue();
            int maxWidth = (int) maxWidthSpinner.getValue();
            int fCount = 0;
            
            if(linebreakFont == null)
                messageLabel.setText("No font loaded!");
            
            for (Entry<String, ResFile> file : files.entrySet()) {
                boolean changed = false;
                
                for (ResPayload p : file.getValue().getRoot().getElementsWithType(Payload.BTX)) {
                    BTXPayload btx = (BTXPayload) p;
                    
                    for (Tuple<Integer, BTXEntry> str : btx.getEntries()) {
                        BTXEntry entry = str.getValue();
                        
                        try {
                            String output = LinebreakUtil.calculateLinebreaks(entry.getString(), fontSize, maxWidth, linebreakFont, false);
                        
                            if (!output.equals(entry.getString())) {
                                entry.setString(output);
                                changed = true;
                            }
                        }
                        catch(Exception ex) {
                            Main.LOGGER.warning(String.format("Error while calcualting linebreaks. File: %s | String: %s", file.getKey(), entry.getString()));
                            Main.LOGGER.log(Level.WARNING, "Exception: ", ex);
                        }
                    }
                }
                
                if (changed) {
                    changedFiles.add(file.getKey());
                    fCount++;
                }
            }
            
            messageLabel.setText("Changed linebreaks in " + fCount + " files.");
        }
    }
    
    private class DigitterLinebreakFixerAction extends AbstractAction {
        private static final long serialVersionUID = 2568092425475577644L;
        
        public DigitterLinebreakFixerAction() {
            super("Fix Digitterbreaks");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            double fontSize = (double) lineLimitSpinner.getValue();
            int maxWidth = (int) maxWidthSpinner.getValue();
            int fCount = 0;
            
            if(linebreakFont == null)
                messageLabel.setText("No font loaded!");
            
            for (Entry<String, ResFile> file : files.entrySet()) {
                boolean changed = false;
                
                for (ResPayload p : file.getValue().getRoot().getElementsWithType(Payload.BTX)) {
                    BTXPayload btx = (BTXPayload) p;
                    
                    for (Tuple<Integer, BTXEntry> str : btx.getEntries()) {
                        BTXEntry entry = str.getValue();
                        
                        try {
                            String output = LinebreakUtil.calculateLinebreaks(entry.getString(), fontSize, maxWidth, linebreakFont, true);
                        
                            if (!output.equals(entry.getString())) {
                                entry.setString(output);
                                changed = true;
                            }
                        }
                        catch(Exception ex) {
                            Main.LOGGER.warning(String.format("Error while calcualting linebreaks. File: %s | String: %s", file.getKey(), entry.getString()));
                            Main.LOGGER.log(Level.WARNING, "Exception: ", ex);
                        }
                    }
                }
                
                if (changed) {
                    changedFiles.add(file.getKey());
                    fCount++;
                }
            }
            
            messageLabel.setText("Changed linebreaks in " + fCount + " files.");
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
            for (String name : changedFiles) {
                files.get(name).repack(new File(input, name));
                setProgress((++count * 100) / changedFiles.size());
                firePropertyChange(MESSAGE_PROPERTY, "", count + " of " + changedFiles.size() + " files saved.");
            }
            
            return null;
        }
        
        @Override
        protected void done() {
            changedFiles.clear();
            
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
                    catch (Exception e) {
                        // do nothing
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
    
    private static class BlaEntry {
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + fileId;
            result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
            result = prime * result + stringId;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof BlaEntry))
                return false;
            BlaEntry other = (BlaEntry) obj;
            if (fileId != other.fileId)
                return false;
            if (fileName == null) {
                if (other.fileName != null)
                    return false;
            }
            else if (!fileName.equals(other.fileName))
                return false;
            if (stringId != other.stringId)
                return false;
            return true;
        }

        String fileName;
        int fileId;
        int stringId;
        
        public BlaEntry(String fileName, int fileId, int stringId) {
            this.fileName = fileName;
            this.fileId = fileId;
            this.stringId = stringId;
        }
    }
    
    private class PrefixStuffAction extends AbstractAction {

        public PrefixStuffAction() {
            super("Prefix Stuff");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser prefixFileDialogue = new JFileChooser("./Input");
            prefixFileDialogue.setDialogTitle("Please select the prefix file.");
            prefixFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            prefixFileDialogue.showOpenDialog(null);

            JFileChooser japaneseFileDialogue = new JFileChooser("./Input");
            japaneseFileDialogue.setDialogTitle("Please select the folder with the Japanese files.");
            japaneseFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            japaneseFileDialogue.showOpenDialog(null);

            JFileChooser translatedFileDialogue = new JFileChooser("./Input");
            translatedFileDialogue.setDialogTitle("Please select the folder with the translated files.");
            translatedFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            translatedFileDialogue.showOpenDialog(null);
            
            JFileChooser outputFileDialogue = new JFileChooser("./Output");
            outputFileDialogue.setDialogTitle("Please select output prefix file.");
            outputFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            outputFileDialogue.showSaveDialog(null);
            
            File prefix = prefixFileDialogue.getSelectedFile();
            File japanese = japaneseFileDialogue.getSelectedFile();
            File translated = translatedFileDialogue.getSelectedFile();
            File output = outputFileDialogue.getSelectedFile();
            
            try(FileAccess bbb = new FileAccess(prefix)) {
                ResFile pref = new ResFile(bbb);

                Map<String, BlaEntry> jpMap = new HashMap<>();
                Map<BlaEntry, String> enMap = new HashMap<>();
                
                try(Stream<Path> lFiles = Files.walk(japanese.toPath())) {
                    lFiles.forEach(a -> {
                        if(!a.toFile().isFile())
                            return;
                        
                        try (FileAccess acc = new FileAccess(a.toFile())){
                            ResFile file = new ResFile(acc);
                            String fileName = a.getFileName().toString();
                            
                            file.getRoot().getElementsWithType(Payload.BTX).forEach(b -> {
                                BTXPayload btx = (BTXPayload) b;
                                btx.getEntries().forEach(c -> jpMap.put(c.getValue().getString(), new BlaEntry(fileName, btx.getFileId(), c.getKey())));
                            });
                        }
                        catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    });
                }
                try(Stream<Path> lFiles = Files.walk(translated.toPath())) {
                    lFiles.forEach(a -> {
                        if(!a.toFile().isFile())
                            return;
                        try (FileAccess acc = new FileAccess(a.toFile())){
                            ResFile file = new ResFile(acc);
                            String fileName = a.getFileName().toString();
                            
                            file.getRoot().getElementsWithType(Payload.BTX).forEach(b -> {
                                BTXPayload btx = (BTXPayload) b;
                                btx.getEntries().forEach(c -> enMap.put(new BlaEntry(fileName, btx.getFileId(), c.getKey()), c.getValue().getString()));
                            });
                        }
                        catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    });
                }
                
                pref.getRoot().getElementsWithType(Payload.BTX).forEach(a -> {
                    BTXPayload btx = (BTXPayload) a;
                    btx.getEntries().forEach(str -> {
                        String oldString = str.getValue().getString();
                        
                        if(!jpMap.containsKey(oldString)) {
                            Main.LOGGER.warning("String not found: " + str.getValue().getString());
                            return;
                        }
                        
                        String newString = enMap.get(jpMap.get(oldString));
                        
                        if(newString == null) {
                            Main.LOGGER.warning("New string is null, original: " + str.getValue().getString());
                            return;
                        }
                        
                        str.getValue().setString(newString);
                    });
                });
                
                pref.repack(output);
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        
        
    }
}
