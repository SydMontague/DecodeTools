package de.phoenixstaffel.decodetools.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.res.payload.GMIOFile;

public class KCAPPanel extends EditorPanel {
    private static final long serialVersionUID = -8718473237761608043L;
    
    private JScrollPane scrollPane = new JScrollPane();
    private JTree tree = new JTree((TreeModel) null);
    
    private JPanel panel = new JPanel();
    private JImage image = new JImage();
    private JButton exportButton = new JButton("Export");
    private JButton importButton = new JButton("Import");
    
    public KCAPPanel(EditorModel model) {
        super(model);
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        exportButton.setAction(new ExportAction());
        importButton.setAction(new ImportAction());
        
        image.setMinimumSize(new Dimension(100, 100));
        image.setBackground(Color.LIGHT_GRAY);
        
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(a -> {
            // TODO modularise the file viewer
            Object selected = ((DefaultMutableTreeNode) a.getPath().getLastPathComponent()).getUserObject();
            
            if (selected instanceof GMIOFile) {
                image.setImage(((GMIOFile) selected).getImage());
            }
        });
        
        scrollPane.setViewportView(tree);
        
        //@formatter:off
        GroupLayout panelLayout = new GroupLayout(panel);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(Alignment.LEADING)
                .addGap(0, 888, Short.MAX_VALUE)
                .addGroup(panelLayout.createSequentialGroup()
                    .addGap(6)
                    .addComponent(exportButton)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(importButton)
                    .addContainerGap(728, Short.MAX_VALUE))
                .addComponent(image, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(Alignment.LEADING)
                .addGap(0, 702, Short.MAX_VALUE)
                .addGroup(panelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelLayout.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(importButton)
                        .addComponent(exportButton))
                    .addGap(18)
                    .addComponent(image, GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE))
        );
        panel.setLayout(panelLayout);
        
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel, GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
        );
        //@formatter:on
        
        setLayout(groupLayout);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (tree.getModel() != getModel().getTreeModel())
            tree.setModel(getModel().getTreeModel());
    }
    
    public JTree getTree() {
        return tree;
    }
    
    public JImage getImagePanel() {
        return image;
    }
    
    class ExportAction extends AbstractAction {
        private static final long serialVersionUID = 7773708520423706131L;
        
        public ExportAction() {
            super("Export");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (getTree().getModel() == null)
                return;
            
            Object selected = ((DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent()).getUserObject();
            
            if (!(selected instanceof GMIOFile))
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Output");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.setFileFilter(new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isDirectory();
                }
                
                @Override
                public String getDescription() {
                    return "PNG Image File (.png)";
                }
            });
            fileDialogue.showSaveDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try {
                File file;
                if (fileDialogue.getSelectedFile().getName().endsWith(".png"))
                    file = fileDialogue.getSelectedFile();
                else
                    file = new File(fileDialogue.getSelectedFile().getPath() + ".png");
                
                ImageIO.write(((GMIOFile) selected).getImage(), "PNG", file);
            }
            catch (IOException ex) {
                Main.LOGGER.log(Level.WARNING, "Could not read image file, not an image?", ex);
            }
        }
    }
    
    class ImportAction extends AbstractAction {
        private static final long serialVersionUID = 7376798961551919059L;
        
        public ImportAction() {
            super("Import");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (getTree().getModel() == null)
                return;
            
            Object selected = ((DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent()).getUserObject();
            
            if (!(selected instanceof GMIOFile))
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Input");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showOpenDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try {
                BufferedImage localImage = ImageIO.read(fileDialogue.getSelectedFile());
                ((GMIOFile) selected).setImage(localImage);
                getImagePanel().setImage(((GMIOFile) selected).getImage());
            }
            catch (IOException ex) {
                Main.LOGGER.log(Level.WARNING, "Could not read image file, not an image?", ex);
            }
        }
    }
}
