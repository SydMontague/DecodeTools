package de.phoenixstaffel.decodetools.gui;

import java.util.Observable;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import de.phoenixstaffel.decodetools.res.payload.GMIOPayload;

public class ImageViewerPanel extends EditorPanel {
    private static final long serialVersionUID = 4301317831427884206L;
    
    private final JScrollPane scrollPane = new JScrollPane();
    private final JList<GMIOPayload> list = new JList<>();
    private PayloadPanel payload = new GMIOPanel(null);
    
    public ImageViewerPanel(EditorModel model) {
        super(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(a -> {
            GMIOPayload selected = list.getSelectedValue();
            payload.setSelectedFile(selected);
        });
        
        //@formatter:off
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(2)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(payload, GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addComponent(payload, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //@formatter:on
        
        scrollPane.setViewportView(list);
        setLayout(groupLayout);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (list.getModel() != getModel().getImageListModel())
            list.setModel(getModel().getImageListModel());
    }
}
