package net.digimonworld.decodetools.gui;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.gui.util.FunctionAction;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;
import net.digimonworld.decodetools.res.kcap.GMIPKCAP;
import net.digimonworld.decodetools.res.payload.GMIOPayload;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;

public class GMIPPanel extends PayloadPanel {
    private static final long serialVersionUID = -6616813521331311399L;

    private GMIPKCAP gmip;
    private final JList<GMIOPayload> list = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JPanel panel = new JPanel();
    private final JButton buttonAdd = new JButton("+");
    private final JButton buttonRemove = new JButton("-");
    private final JButton buttonUp = new JButton("↑");
    private final JButton buttonDown = new JButton("↓");
    
    private DefaultListModel<GMIOPayload> model = new DefaultListModel<>();
    
    public GMIPPanel(Object selected) {
        setSelectedFile(selected);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(247, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                    .addContainerGap())
        );

        list.setModel(model);
        
        scrollPane.setViewportView(list);
        scrollPane.setColumnHeaderView(panel);
        
        buttonAdd.setAction(new FunctionAction("+", e -> {
            gmip.add(new GMIOPayload(gmip));
            updateListModel();
        }));
        
        buttonRemove.setAction(new FunctionAction("-", e -> {
            int selectedIndex = list.getSelectedIndex();
            
            if (selectedIndex == -1)
                return;
            
            gmip.remove(selectedIndex);
            updateListModel();
        }));
        
        buttonUp.setAction(new FunctionAction("↑", e -> {
            int selIndex = list.getSelectedIndex();
            if(selIndex == -1)
                return;
            
            if(selIndex <= 0)
                return;
            
            gmip.swap(selIndex, selIndex - 1);
            updateListModel();
            list.setSelectedIndex(selIndex - 1);
            list.requestFocus();
        }));
        
        buttonDown.setAction(new FunctionAction("↓", e -> {
            int selIndex = list.getSelectedIndex();
            if(selIndex == -1)
                return;
            
            if(selIndex + 1 >= gmip.getEntryCount())
                return;
            
            gmip.swap(selIndex, selIndex + 1);
            updateListModel();
            list.setSelectedIndex(selIndex + 1);
            list.requestFocus();
        }));
        
        panel.add(buttonAdd);
        panel.add(buttonUp);
        panel.add(buttonDown);
        panel.add(buttonRemove);
        setLayout(groupLayout);
    }
    
    @Override
    public void setSelectedFile(Object file) {
        if (file == null)
            return;
        
        if (!(file instanceof AbstractKCAP)) {
            Main.LOGGER.warning("Tried to select non-KCAP File in GMIPPanel.");
            return;
        }
        
        if (((AbstractKCAP) file).getKCAPType() != AbstractKCAP.KCAPType.GMIP) {
            Main.LOGGER.warning("Tried to select non-GMIP KCAP File in GMIPPanel.");
            return;
        }
        
        this.gmip = (GMIPKCAP) file;
        updateListModel();
    }
    
    private void updateListModel() {
        model.clear();
        model.addAll(gmip.getGMIOEntries());
    }
}
