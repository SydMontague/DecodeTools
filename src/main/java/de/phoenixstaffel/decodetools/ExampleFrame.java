package de.phoenixstaffel.decodetools;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
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
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.arcv.ARCVFile;
import de.phoenixstaffel.decodetools.dataminer.Access;
import de.phoenixstaffel.decodetools.dataminer.FileAccess;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.payload.GMIOFile;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.border.LineBorder;

public class ExampleFrame extends JFrame {
    static final Logger log = Logger.getLogger("DataMiner");
    
    private JPanel contentPane;
    private JTree tree = new JTree((TreeModel) null);
    
    private ResFile res = null;
    
    /**
     * Create the frame.
     */
    public ExampleFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmLoadFile = new JMenuItem("Load File");
        mntmLoadFile.setAction(new AbstractAction("Load File") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileDialogue = new JFileChooser("./Input");
                fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileDialogue.showOpenDialog(null);
                
                if(fileDialogue.getSelectedFile() == null)
                    return;
                
                try (Access access = new FileAccess(fileDialogue.getSelectedFile())) {
                    setTitle(fileDialogue.getSelectedFile().getName());
                    setCurrentFile(new ResFile(access));
                }
                catch (IOException e1) {
                    log.log(Level.WARNING, "Error while loading file!", e1);
                }                
            }
        });

        
        JMenuItem mntmSaveFile = new JMenuItem("Save File");
        mntmSaveFile.setAction(new AbstractAction("Save File") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileDialogue = new JFileChooser("./Output");
                fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileDialogue.showSaveDialog(null);
                
                if(fileDialogue.getSelectedFile() == null)
                    return;
                
                res.repack(fileDialogue.getSelectedFile());
            }
        });
        
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.setAction(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        
        mnFile.add(mntmLoadFile);
        mnFile.add(mntmSaveFile);
        mnFile.add(mntmExit);
        
        JMenu mnArcv = new JMenu("ARCV");
        menuBar.add(mnArcv);
        
        JMenuItem mntmRebuildArcv = new JMenuItem("Rebuild ARCV");
        mntmRebuildArcv.setAction(new AbstractAction("Redbuild ARCV") {
            
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser inputFileDialogue = new JFileChooser("./");
                inputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                inputFileDialogue.showOpenDialog(null);

                JFileChooser outputFileDialogue = new JFileChooser("./");
                outputFileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                outputFileDialogue.showSaveDialog(null);
                
                if(inputFileDialogue.getSelectedFile() == null)
                    return;

                if(outputFileDialogue.getSelectedFile() == null)
                    return;
                
                try {
                    new ARCVFile(inputFileDialogue.getSelectedFile()).saveFiles(outputFileDialogue.getSelectedFile());
                }
                catch (IOException e1) {
                    log.log(Level.WARNING, "Error while rebuilding ARCV files!", e1);
                }
                
            }
        });
        
        mnArcv.add(mntmRebuildArcv);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        tree.setShowsRootHandles(true);
        scrollPane.setViewportView(tree);
        
        JPanel panel = new JPanel();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup().addComponent(scrollPane,
                                                                              GroupLayout.PREFERRED_SIZE,
                                                                              277,
                                                                              GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, 226, Short.MAX_VALUE).addContainerGap()));
        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE).addGap(12))
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE));
        
        JImage image = new JImage();
        image.setBackground(Color.LIGHT_GRAY);
        image.setMinimumSize(new Dimension(100, 100));
        image.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JButton btnExport = new JButton("Export");
        btnExport.setAction(new AbstractAction("Export") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected = ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
                
                if (!(selected instanceof GMIOFile)) 
                    return;

                JFileChooser fileDialogue = new JFileChooser("./Output");
                fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileDialogue.showSaveDialog(null);
                
                if(fileDialogue.getSelectedFile() == null)
                    return;
                
                try {
                    ImageIO.write(((GMIOFile) selected).getImage(), "PNG", fileDialogue.getSelectedFile());
                }
                catch(IOException ex) {
                    log.log(Level.WARNING, "Could not read image file, not an image?", ex);
                }
            }
        });
        
        
        JButton btnImport = new JButton("Import");
        btnImport.setAction(new AbstractAction("Import") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected = ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
                
                if (!(selected instanceof GMIOFile)) 
                    return;

                JFileChooser fileDialogue = new JFileChooser("./Input");
                fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileDialogue.showOpenDialog(null);
                
                if(fileDialogue.getSelectedFile() == null)
                    return;
                
                try {
                    BufferedImage image = ImageIO.read(fileDialogue.getSelectedFile());
                    ((GMIOFile) selected).setImage(image);
                }
                catch(IOException ex) {
                    log.log(Level.WARNING, "Could not read image file, not an image?", ex);
                }
            }
        });
        
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addGap(6)
                    .addComponent(btnExport)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(btnImport)
                    .addContainerGap(535, Short.MAX_VALUE))
                .addComponent(image, GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(btnImport)
                        .addComponent(btnExport))
                    .addGap(18)
                    .addComponent(image, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
        );
        panel.setLayout(gl_panel);
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent arg0) {
                Object selected = ((DefaultMutableTreeNode) arg0.getPath().getLastPathComponent()).getUserObject();
                
                if (selected instanceof GMIOFile) {
                    image.setImage(((GMIOFile) selected).getImage());
                }
            }
        });
        
        contentPane.setLayout(gl_contentPane);
        
    }
    
    public void setCurrentFile(ResFile res) {
        this.res = res;
        tree.setModel(new DefaultTreeModel(res.getRoot().getTreeNode()));
    }
}
