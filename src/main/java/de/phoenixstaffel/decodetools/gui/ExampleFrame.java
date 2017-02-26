package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import de.phoenixstaffel.decodetools.arcv.ARCVFile;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.ResFile;

public class ExampleFrame extends JFrame implements Observer {
    private static final long serialVersionUID = -8269477952146086450L;
    
    static final Logger log = Logger.getLogger("DataMiner");
    
    private EditorModel model = new EditorModel();
    
    private JPanel contentPane;
    
    public ExampleFrame() {
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
        mntmSaveFile.setAction(new SaveAction("Save File"));
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.setAction(new ExitAction("Exit"));
        
        mnFile.add(mntmLoadFile);
        mnFile.add(mntmSaveFile);
        mnFile.add(mntmExit);
        
        JMenu mnArcv = new JMenu("ARCV");
        menuBar.add(mnArcv);
        
        JMenuItem mntmRebuildArcv = new JMenuItem("Rebuild ARCV");
        mntmRebuildArcv.setAction(new RebuildAction());
        mnArcv.add(mntmRebuildArcv);
        
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
    }
    
    @Override
    public void update(Observable o, Object arg) {
        //nothing to implement yet
    }
    
    public EditorModel getModel() {
        return model;
    }
    
    class ExitAction extends AbstractAction {
        private static final long serialVersionUID = -3954749987113215617L;
        
        public ExitAction(String name) {
            super(name);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
    
    class SaveAction extends AbstractAction {
        private static final long serialVersionUID = -6551617661779370568L;
        
        public SaveAction(String name) {
            super(name);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileDialogue = new JFileChooser("./Output");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showSaveDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            getModel().getSelectedFile().repack(fileDialogue.getSelectedFile());
        }
    }
    
    class RebuildAction extends AbstractAction {
        private static final long serialVersionUID = -5886136864566743305L;
        
        public RebuildAction() {
            super("Rebuild ARCV");
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
            
            try {
                new ARCVFile(inputFileDialogue.getSelectedFile()).saveFiles(outputFileDialogue.getSelectedFile());
            }
            catch (IOException e1) {
                log.log(Level.WARNING, "Error while rebuilding ARCV files!", e1);
            }
            
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
            
            try (Access access = new FileAccess(fileDialogue.getSelectedFile())) {
                setTitle(fileDialogue.getSelectedFile().getName());
                ResFile file = new ResFile(access);
                getModel().setSelectedFile(file);
            }
            catch (IOException e1) {
                log.log(Level.WARNING, "Error while loading file!", e1);
            }
        }
    }
}
