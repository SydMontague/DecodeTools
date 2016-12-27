package de.phoenixstaffel.decodetools;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import de.phoenixstaffel.decodetools.res.payload.GMIOFile;
import java.awt.FlowLayout;
import java.awt.Dimension;

public class ExampleFrame extends JFrame {
    
    private JPanel contentPane;
    
    /**
     * Create the frame.
     */
    public ExampleFrame(TreeModel model) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 701, 500);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mnFile.add(mntmExit);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        JTree tree = new JTree();
        tree.setModel(model);
        
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
        image.setMinimumSize(new Dimension(100, 100));
        image.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel.createSequentialGroup()
                        .addComponent(image, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE).addContainerGap()));
        gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                .addComponent(image, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE));
        panel.setLayout(gl_panel);
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent arg0) {
                Object selected = ((DefaultMutableTreeNode) arg0.getPath().getLastPathComponent()).getUserObject();
                
                if (selected instanceof GMIOFile) {
                    image.setImage(((GMIOFile) selected).getImage());
                }
            }
        });
        
        contentPane.setLayout(gl_contentPane);
        
    }
}
