package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.arcv.ARCVFile;

public class MainWindow extends JFrame implements Observer {
    private static final long serialVersionUID = -8269477952146086450L;
    
    private EditorModel model = new EditorModel();
    
    private JPanel contentPane;
    private JMenu mnStyle = new JMenu("Style");
    
    public MainWindow() {
        model.addObserver(this);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1127, 791);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmLoadFile = new JMenuItem("Load File");
        mntmLoadFile.setAction(new LoadAction());
        
        JMenuItem mntmSaveFile = new JMenuItem("Save File");
        mntmSaveFile.setAction(new SaveAsAction());
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.setAction(new ExitAction());
        
        JMenuItem mntmSave = new JMenuItem("Save");
        mntmSave.setAction(new SaveAction());
        
        mnFile.add(mntmLoadFile);
        mnFile.add(mntmSaveFile);
        mnFile.add(mntmSave);
        mnFile.add(mntmExit);
        
        JMenu mnArcv = new JMenu("ARCV");
        menuBar.add(mnArcv);
        
        JMenuItem mntmRebuildArcv = new JMenuItem("Rebuild ARCV");
        mntmRebuildArcv.setAction(new RebuildAction("Rebuild ARCV", true));
        mnArcv.add(mntmRebuildArcv);
        
        JMenuItem mntmRebuildUncompressedArcv = new JMenuItem("Rebuild Uncompressed ARCV");
        mntmRebuildUncompressedArcv.setAction(new RebuildAction("Rebuild Uncompressed ARCV", false));
        mnArcv.add(mntmRebuildUncompressedArcv);
        
        menuBar.add(mnStyle);
        
        contentPane = new JPanel();
        contentPane.setBorder(null);
        setContentPane(contentPane);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
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
            mnStyle.add(new JMenuItem(new AbstractAction(style.getName()) {
                private static final long serialVersionUID = -7199990221476393001L;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        UIManager.setLookAndFeel(style.getClassName());
                        SwingUtilities.updateComponentTreeUI(MainWindow.this);
                    }
                    catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
                        Main.LOGGER.log(Level.SEVERE, "Error while setting Look & Feel!" , e1);
                    }
                }
            }));
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        // nothing to implement yet
    }
    
    public EditorModel getModel() {
        return model;
    }
    
    class ExitAction extends AbstractAction {
        private static final long serialVersionUID = -3954749987113215617L;
        
        public ExitAction() {
            super("Exit");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
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
            
            //TODO add work queue, to make sure only one task is executed at a time
            MainWindow.this.setEnabled(false);
            SwingWorker<Void, Object> worker = new SwingWorker<Void, Object>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        new ARCVFile(inputFileDialogue.getSelectedFile(), compressed).saveFiles(outputFileDialogue.getSelectedFile());
                        //TODO add progressbar
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
}
