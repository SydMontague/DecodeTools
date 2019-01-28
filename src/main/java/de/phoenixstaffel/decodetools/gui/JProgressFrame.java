package de.phoenixstaffel.decodetools.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;

public class JProgressFrame extends JFrame {
    private static final long serialVersionUID = -2427180729086978760L;
    
    public static final String MESSAGE_PROPERTY = "message";
    public static final String PROGRESS_PROPERTY = "progress";
    public static final String TITLE_PROPERTY = "title";
    
    private JPanel contentPane;
    private final JProgressBar progressBar = new JProgressBar();
    
    transient PropertyChangeListener progressListener = b -> {
        if (PROGRESS_PROPERTY.equals(b.getPropertyName()))
            progressBar.setValue((int) b.getNewValue());
        if (MESSAGE_PROPERTY.equals(b.getPropertyName()))
            progressBar.setString((String) b.getNewValue());
        if (TITLE_PROPERTY.equals(b.getPropertyName()))
            setTitle((String) b.getNewValue());
    };

    public JProgressFrame() {
        this("");
    }
    
    public JProgressFrame(String title) {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 380, 70);
        setTitle(title);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        progressBar.setString("");
        progressBar.setStringPainted(true);
        contentPane.add(progressBar, BorderLayout.CENTER);
    }
    
    public PropertyChangeListener getProgressListener() {
        return progressListener;
    }
}
