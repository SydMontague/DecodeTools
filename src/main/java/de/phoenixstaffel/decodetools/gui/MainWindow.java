package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.arcv.ARCVFile;
import de.phoenixstaffel.decodetools.arcv.VCRAFile;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.gui.util.FunctionAction;
import de.phoenixstaffel.decodetools.gui.util.JProgressFrame;
import de.phoenixstaffel.decodetools.res.DummyResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload;

public class MainWindow extends JFrame implements Observer {
    private static final long serialVersionUID = -8269477952146086450L;
    
    private EditorModel model = new EditorModel();
    
    private JPanel contentPane;
    private JMenu mnStyle = new JMenu("Style");
    private JMenuBar menuBar = new JMenuBar();
    private JMenu mnFile = new JMenu("File");
    private JMenuItem mntmLoadFile = new JMenuItem("Load File");
    private JMenuItem mntmSaveFile = new JMenuItem("Save File");
    private JMenuItem mntmExit = new JMenuItem("Exit");
    private JMenuItem mntmSave = new JMenuItem("Save");
    private JMenu mnArcv = new JMenu("ARCV");
    private JMenuItem mntmRebuildArcv = new JMenuItem("Rebuild ARCV");
    private JMenuItem mntmRebuildUncompressedArcv = new JMenuItem("Rebuild Uncompressed ARCV");
    private JMenu mnTools = new JMenu("Tools");
    private JMenuItem mntmReexportMipmaps = new JMenuItem("Re-Export Malformatted Files");
    private JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
    private final JMenuItem mntmMassStringReplacer = new JMenuItem("Mass String Replacer");
    private final JMenuItem mntmMergeBTX = new JMenuItem("Merge BTX folder to file");
    private final JMenuItem mntmUnpackARCV = new JMenuItem("New menu item");
    
    public MainWindow() {
        model.addObserver(this);
        
        setBounds(100, 100, 1127, 791);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(null,
                                                           "Are you sure you want to close this Application?",
                                                           "Confirm Exit",
                                                           JOptionPane.YES_NO_OPTION,
                                                           JOptionPane.QUESTION_MESSAGE,
                                                           null,
                                                           null,
                                                           null);
                
                if (confirm == JOptionPane.YES_OPTION)
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                else
                    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                
                super.windowClosing(e);
            }
        });
        
        setJMenuBar(menuBar);
        
        menuBar.add(mnFile);
        mntmLoadFile.setAction(new LoadAction());
        mntmSaveFile.setAction(new SaveAsAction());
        mntmExit.setAction(new FunctionAction("Exit", a -> dispose()));
        mntmSave.setAction(new SaveAction());
        
        mnFile.add(mntmLoadFile);
        mnFile.add(mntmSaveFile);
        mnFile.add(mntmSave);
        mnFile.add(mntmExit);
        
        menuBar.add(mnArcv);
        
        mntmRebuildArcv.setAction(new RebuildAction("Rebuild ARCV", true));
        mnArcv.add(mntmRebuildArcv);
        
        mntmRebuildUncompressedArcv.setAction(new RebuildAction("Rebuild Uncompressed ARCV", false));
        mnArcv.add(mntmRebuildUncompressedArcv);
        
        mntmUnpackARCV.setAction(new ExtractAction());
        mnArcv.add(mntmUnpackARCV);
        
        menuBar.add(mnStyle);
        menuBar.add(mnTools);
        
        mntmMassStringReplacer.setAction(new FunctionAction("Mass String Replacer", a -> new MassStringReplacer().setVisible(true)));
        mnTools.add(mntmMassStringReplacer);
        
        mntmReexportMipmaps.setAction(new ReExportAction());
        mnTools.add(mntmReexportMipmaps);
        
        mntmMergeBTX.setAction(new MergeBTXAction());
        mnTools.add(mntmMergeBTX);
        
        contentPane = new JPanel();
        contentPane.setBorder(null);
        setContentPane(contentPane);
        
        KCAPPanel kcapViewer = new KCAPPanel(model);
        tabbedPane.addTab("KCAP Viewer", null, kcapViewer, null);
        
        ImageViewerPanel imageViewer = new ImageViewerPanel(model);
        tabbedPane.addTab("Image Viewer", null, imageViewer, null);
        
        //@formatter:off
        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 1111, Short.MAX_VALUE));
        
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE));
        //@formatter:on
        
        contentPane.setLayout(contentPaneLayout);
        
        addLookAndFeelOptions();
    }
    
    private void addLookAndFeelOptions() {
        LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        
        for (LookAndFeelInfo style : info) {
            mnStyle.add(new JMenuItem(new FunctionAction(style.getName(), a -> {
                try {
                    UIManager.setLookAndFeel(style.getClassName());
                    SwingUtilities.updateComponentTreeUI(MainWindow.this);
                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
                    Main.LOGGER.log(Level.SEVERE, "Error while setting Look & Feel!", e1);
                }
            })));
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        // nothing to implement yet
    }
    
    public EditorModel getModel() {
        return model;
    }
    
    class SaveAsAction extends AbstractAction {
        private static final long serialVersionUID = -6551617661779370568L;
        
        public SaveAsAction() {
            super("Save As");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileDialogue = new JFileChooser("./Output");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showSaveDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            getModel().getSelectedResource().repack(fileDialogue.getSelectedFile());
        }
    }
    
    class SaveAction extends AbstractAction {
        private static final long serialVersionUID = -6551617661779370568L;
        
        public SaveAction() {
            super("Save");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            getModel().getSelectedResource().repack(getModel().getSelectedFile());
        }
    }
    
    class RebuildAction extends AbstractAction {
        private static final long serialVersionUID = -5886136864566743305L;
        boolean compressed;
        
        public RebuildAction(String name, boolean compressed) {
            super(name);
            this.compressed = compressed;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Please select the directory with the extracted ARCV contents.");
            inputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            inputFileDialogue.showOpenDialog(null);
            
            JFileChooser outputFileDialogue = new JFileChooser("./");
            outputFileDialogue.setDialogTitle("Please select the directory in which the ARCV0 and ARCVINFO will be saved.");
            outputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            outputFileDialogue.showSaveDialog(null);
            
            if (inputFileDialogue.getSelectedFile() == null)
                return;
            
            if (outputFileDialogue.getSelectedFile() == null)
                return;
            
            // TODO add work queue, to make sure only one task is executed at a time
            MainWindow.this.setEnabled(false);
            SwingWorker<Void, Object> worker = new SwingWorker<Void, Object>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        new ARCVFile(inputFileDialogue.getSelectedFile(), compressed).saveFiles(outputFileDialogue.getSelectedFile());
                        // TODO add progressbar
                    }
                    catch (IOException e1) {
                        Main.LOGGER.log(Level.WARNING, "Error while rebuilding ARCV files!", e1);
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    MainWindow.this.setEnabled(true);
                }
                
            };
            worker.execute();
            
        }
    }
    
    class ExtractAction extends AbstractAction {
        private static final long serialVersionUID = 1596235982551552437L;

        public ExtractAction() {
            super("Extract ARCV");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Please select the directory containing ARCVINFO.BIN and ARCV0.BIN");
            inputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            inputFileDialogue.showOpenDialog(null);
            
            JFileChooser outputFileDialogue = new JFileChooser("./");
            outputFileDialogue.setDialogTitle("Please select the directory in which the files will be extracted.");
            outputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            outputFileDialogue.showSaveDialog(null);
            
            if (inputFileDialogue.getSelectedFile() == null)
                return;
            
            if (outputFileDialogue.getSelectedFile() == null)
                return;
            
            // TODO add work queue, to make sure only one task is executed at a time
            MainWindow.this.setEnabled(false);
            SwingWorker<Void, Object> worker = new SwingWorker<Void, Object>() {
                @Override
                protected Void doInBackground() throws Exception {
                    File input = inputFileDialogue.getSelectedFile();
                    File output = outputFileDialogue.getSelectedFile();
                    
                    try (Access access = new FileAccess(new File(input, "ARCVINFO.BIN"))){
                        new VCRAFile(access).extractARCV(new File(input, "ARCV0.BIN").toPath(), output.toPath());
                    }
                    catch (IOException e1) {
                        Main.LOGGER.log(Level.WARNING, "Error while extracting ARCV files!", e1);
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    MainWindow.this.setEnabled(true);
                }
                
            };
            worker.execute();
        }
    }
    
    class LoadAction extends AbstractAction {
        private static final long serialVersionUID = 423960702402170030L;
        
        public LoadAction() {
            super("Load File");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showOpenDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            File file = fileDialogue.getSelectedFile();
            setTitle(file.getName());
            getModel().setSelectedFile(file);
        }
    }
    
    class ReExportAction extends AbstractAction {
        private static final long serialVersionUID = -7894935184753933528L;
        
        public ReExportAction() {
            super("Re-Export Malformatted Files");
        }
        
        @Override
        public void actionPerformed(ActionEvent ee) {
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Please select the directory with the input files");
            inputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            inputFileDialogue.showOpenDialog(null);
            
            JFileChooser outputFileDialogue = new JFileChooser("./");
            outputFileDialogue.setDialogTitle("Please select the directory in which the exported files will be saved.");
            outputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            outputFileDialogue.showSaveDialog(null);
            
            String path = inputFileDialogue.getSelectedFile().getPath();
            List<File> files = Utils.listFiles(inputFileDialogue.getSelectedFile());
            
            MainWindow.this.setEnabled(false);
            JProgressFrame progressFrame = new JProgressFrame("Re-Exporting in progress");
            progressFrame.setVisible(true);
            
            SwingWorker<Void, Object> worker = new SwingWorker<Void, Object>() {
                private int filesHandled = 0;
                private int filesTotal = files.size();
                
                @Override
                protected Void doInBackground() throws Exception {
                    files.parallelStream().forEach(f -> {
                        try {
                            String local = f.getPath().replace(path, "");
                            
                            byte[] input = Files.readAllBytes(f.toPath());
                            
                            Access access = new FileAccess(f, true);
                            ResPayload res = ResPayload.craft(access);
                            access.close();
                            
                            int structureSize = res.getSizeOfRoot();
                            DummyResData resData = new DummyResData();
                            res.fillDummyResData(resData);
                            int dataSize = resData.getSize();
                            
                            if (input.length - Utils.align(structureSize, 0x80) != dataSize && dataSize != 0) {
                                Main.LOGGER.info(() -> "Re-Exporting " + local);
                                File ff = new File(outputFileDialogue.getSelectedFile(), local);
                                ff.getParentFile().mkdirs();
                                res.repack(ff);
                            }
                            
                            publish(0);
                        }
                        catch (IOException e) {
                            Main.LOGGER.log(Level.SEVERE, "IOException while trying to re-export " + f, e);
                        }
                    });
                    return null;
                }
                
                @Override
                protected void process(List<Object> chunks) {
                    filesHandled += chunks.size();
                    setProgress((filesHandled * 100) / filesTotal);
                    firePropertyChange(JProgressFrame.MESSAGE_PROPERTY, "", filesHandled + " of " + filesTotal);
                }
                
                @Override
                protected void done() {
                    MainWindow.this.setEnabled(true);
                    progressFrame.dispose();
                }
            };
            
            worker.addPropertyChangeListener(progressFrame.getProgressListener());
            worker.execute();
        }
        
    }
    
    class MergeBTXAction extends AbstractAction {
        private static final long serialVersionUID = -7894935184753933528L;
        
        public MergeBTXAction() {
            super("Merge BTX to single file");
        }
        
        @Override
        public void actionPerformed(ActionEvent ee) {
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Please select the directory with the input files");
            inputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            inputFileDialogue.showOpenDialog(null);
            
            JFileChooser outputFileDialogue = new JFileChooser("./");
            outputFileDialogue.setDialogTitle("Please select the directory in which the exported files will be saved.");
            outputFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            outputFileDialogue.setMultiSelectionEnabled(false);
            outputFileDialogue.showSaveDialog(null);
            
            String path = inputFileDialogue.getSelectedFile().getPath();
            
            try (PrintStream fout = new PrintStream(outputFileDialogue.getSelectedFile()); Stream<Path> files = Files.walk(Paths.get(path))) {
                files.forEach(a -> {
                    if (!a.toFile().isFile())
                        return;
                    
                    try (Access b = new FileAccess(a.toFile())) {
                        ResPayload f = ResPayload.craft(b);
                        String name = a.subpath(a.getNameCount() - 1, a.getNameCount()).toString();
                        
                        fout.println(name);
                        f.getElementsWithType(Payload.BTX).forEach(c -> {
                            ((BTXPayload) c).getEntries().stream().filter(d -> !d.getValue().getString().isEmpty()).forEach(d -> fout.println(d.getValue().getString().replace("\n", "\\n")));
                            fout.println();
                        });
                        
                    }
                    catch (Exception e) {
                        Main.LOGGER.log(Level.SEVERE, String.format("Error while handling %s.", a.toString()), e);
                    }
                });
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        
    }
}
