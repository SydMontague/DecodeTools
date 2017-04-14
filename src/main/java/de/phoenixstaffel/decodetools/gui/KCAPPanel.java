package de.phoenixstaffel.decodetools.gui;

import java.util.Observable;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

public class KCAPPanel extends EditorPanel {
    private static final long serialVersionUID = -8718473237761608043L;
    
    private JScrollPane scrollPane = new JScrollPane();
    private JTree tree = new JTree((TreeModel) null);
    
    private PayloadPanel panel = new GMIOPanel(null);
    
    public KCAPPanel(EditorModel model) {
        super(model);
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(a -> {
            // TODO modularise the file viewer
            Object selected = ((DefaultMutableTreeNode) a.getPath().getLastPathComponent()).getUserObject();
            
            panel.setSelectedFile(selected);
        });
        
        scrollPane.setViewportView(tree);
        
        //@formatter:off       
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
}
