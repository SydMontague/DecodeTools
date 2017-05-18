package de.phoenixstaffel.decodetools.gui;

import java.awt.CardLayout;
import java.util.Map;
import java.util.Observable;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

public class KCAPPanel extends EditorPanel {
    private static final long serialVersionUID = -8718473237761608043L;
    
    private JScrollPane scrollPane = new JScrollPane();
    private JTree tree = new JTree((TreeModel) null);
    
    private Map<Enum<?>, PayloadPanel> panels = PayloadPanel.generatePayloadPanels();
    private final JPanel panel = new JPanel();
    private CardLayout cardLayout = new CardLayout(0, 0);
    
    public KCAPPanel(EditorModel model) {
        super(model);
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(a -> {
            Object selected = ((DefaultMutableTreeNode) a.getPath().getLastPathComponent()).getUserObject();
            Enum<?> type = null;
            
            if (selected instanceof ResPayload && panels.containsKey(((ResPayload) selected).getType()))
                type = ((ResPayload) selected).getType();
            else if (selected instanceof KCAPPayload && panels.containsKey(((KCAPPayload) selected).getExtension().getType()))
                type = ((KCAPPayload) selected).getExtension().getType();
            
            if (type != null) {
                cardLayout.show(panel, type.name());
                panels.get(type).setSelectedFile(selected);
            }
            else
                cardLayout.show(panel, "NULL");
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
        //panel_1.setLayout(new CardLayout(0, 0));
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
