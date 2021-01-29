package de.phoenixstaffel.decodetools.gui;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.core.Utils;
import de.phoenixstaffel.decodetools.gui.util.FunctionAction;
import de.phoenixstaffel.decodetools.gui.util.ResPayloadTreeNode;
import de.phoenixstaffel.decodetools.res.ResData;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;

public class KCAPPanel extends EditorPanel {
    private static final long serialVersionUID = -8718473237761608043L;
    
    private JScrollPane scrollPane = new JScrollPane();
    private JTree tree = new JTree((TreeModel) null);
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem exportItem = new JMenuItem("Export");
    private JMenuItem refeshItem = new JMenuItem("Refresh");
    
    private Map<Enum<?>, PayloadPanel> panels = PayloadPanel.generatePayloadPanels();
    private final JPanel panel = new JPanel();
    private CardLayout cardLayout = new CardLayout(0, 0);
    
    public KCAPPanel(EditorModel model) {
        super(model);
        
        popupMenu.add(exportItem);
        popupMenu.add(refeshItem);
        
        refeshItem.setAction(new FunctionAction("Refresh", e -> getModel().update()));
        
        exportItem.setAction(new FunctionAction("Export", a -> {
            JFileChooser inputFileDialogue = new JFileChooser("./");
            inputFileDialogue.setDialogTitle("Where to save the exported file?");
            inputFileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            inputFileDialogue.showOpenDialog(null);
            
            Object selected = ((ResPayloadTreeNode) tree.getSelectionPath().getLastPathComponent()).getPayload();
            File file = inputFileDialogue.getSelectedFile();
            if(!(selected instanceof ResPayload) || file == null)
                return;

            if(file.exists() && !file.delete())
                Main.LOGGER.severe("Could not delete already existing " + file.getName() + ". Aborting.");
            
            try (Access dest = new FileAccess(file); ResData data = new ResData()) {
                ((ResPayload) selected).writeKCAP(dest, data);
                
                if(data.getSize() != 0) {
                    dest.setPosition(Utils.align(((ResPayload) selected).getSize(), 0x80));
                    dest.writeByteArray(data.getStream().toByteArray());
                }
            }
            catch(IOException ex) {
                Main.LOGGER.severe("Exception while exporting file: " + ex.getMessage());
            }
        }));
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(a -> {
            Object selected = ((ResPayloadTreeNode) a.getPath().getLastPathComponent()).getPayload();
            Enum<?> type = null;
            
            if (selected instanceof ResPayload && panels.containsKey(((ResPayload) selected).getType()))
                type = ((ResPayload) selected).getType();
            else if (selected instanceof AbstractKCAP && panels.containsKey(((AbstractKCAP) selected).getKCAPType()))
                type = ((AbstractKCAP) selected).getKCAPType();
            
            if (type != null) {
                cardLayout.show(panel, type.name());
                panels.get(type).setSelectedFile(selected);
            }
            else
                cardLayout.show(panel, "NULL");
        });
        
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    if(row != -1)
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        scrollPane.setViewportView(tree);
        
        //@formatter:off
        panel.setLayout(cardLayout);

        panel.add(PayloadPanel.NULL_PANEL, "NULL");
        panels.forEach((a, b) -> panel.add(b, a.name()));
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addContainerGap(621, Short.MAX_VALUE))
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
}
