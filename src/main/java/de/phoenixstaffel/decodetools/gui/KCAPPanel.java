package de.phoenixstaffel.decodetools.gui;

import java.awt.CardLayout;
import java.util.Map;
import java.util.Observable;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.res.KCAPPayload;

public class KCAPPanel extends EditorPanel {
    private static final long serialVersionUID = -8718473237761608043L;
    
    private JScrollPane scrollPane = new JScrollPane();
    private JTree tree = new JTree((TreeModel) null);
    
    private JLayeredPane panel = new JLayeredPane();
    private PayloadPanel activePanel = PayloadPanel.NULL_PANEL;
    
    private Map<KCAPPayload.Payload, PayloadPanel> panels = PayloadPanel.generatePayloadPanels();
    
    public KCAPPanel(EditorModel model) {
        super(model);
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(a -> {
            Object selected = ((DefaultMutableTreeNode) a.getPath().getLastPathComponent()).getUserObject();

            if (selected instanceof KCAPPayload) {
                setPanel(panels.getOrDefault(((KCAPPayload) selected).getType(), PayloadPanel.NULL_PANEL));
                getPanel().setSelectedFile(selected);
            }
            else
                setPanel(PayloadPanel.NULL_PANEL);
        });
        
        scrollPane.setViewportView(tree);

        panel.add(PayloadPanel.NULL_PANEL);
        panels.forEach((a, b) -> panel.add(b));
        
        //@formatter:off
        panel.setLayout(new CardLayout(0, 0));
        
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
    
    private PayloadPanel getPanel() {
        return activePanel;
    }
    
    private void setPanel(PayloadPanel panel) {
        this.panel.moveToBack(this.activePanel);
        this.activePanel = panel;
        this.panel.moveToFront(this.activePanel);
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
