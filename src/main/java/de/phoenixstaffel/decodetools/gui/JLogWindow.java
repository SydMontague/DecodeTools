package de.phoenixstaffel.decodetools.gui;

import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class JLogWindow extends JFrame {
    private static final long serialVersionUID = 166721964687279818L;
    
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    
    private transient Logger log;
    private transient LogHandler handler;
    private final JScrollPane scrollPane = new JScrollPane();
    private final JTextArea textPane = new JTextArea();
    private final JMenuBar menu = new JMenuBar();
    private final JMenu mnLogLevel = new JMenu("Log Level");
    
    private final JMenuItem mntmAll = new JMenuItem("All");
    private final JMenuItem mntmInfo = new JMenuItem("Info");
    private final JMenuItem mntmWarning = new JMenuItem("Warning");
    private final JMenuItem mntmSevere = new JMenuItem("Severe");
    private final JMenuItem mntmOff = new JMenuItem("Off");
    
    public JLogWindow(Logger logger) {
        this.log = logger;
        this.handler = new LogHandler();
        log.addHandler(handler);
        setBounds(0, 0, 500, 300);
        
        setTitle("Log Window");
        textPane.setEditable(false);
        ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        setJMenuBar(menu);
        menu.add(mnLogLevel);
        mnLogLevel.add(mntmAll);
        mnLogLevel.add(mntmInfo);
        mnLogLevel.add(mntmWarning);
        mnLogLevel.add(mntmSevere);
        mnLogLevel.add(mntmOff);
        
        mntmAll.setAction(new SetLevelAction(Level.ALL, "All"));
        mntmInfo.setAction(new SetLevelAction(Level.INFO, "Info"));
        mntmWarning.setAction(new SetLevelAction(Level.WARNING, "Warning"));
        mntmSevere.setAction(new SetLevelAction(Level.SEVERE, "Severe"));
        mntmOff.setAction(new SetLevelAction(Level.OFF, "Off"));
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        setContentPane(scrollPane);
        
        scrollPane.setViewportView(textPane);
    }
    
    protected JTextArea getTextArea() {
        return textPane;
    }
    
    protected Handler getHandler() {
        return handler;
    }
    
    class LogHandler extends Handler {
        public LogHandler() {
            setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format("[%s] [%s] %s%n", LocalTime.now().format(DATE_FORMAT), record.getLevel(), record.getMessage());
                }
            });
            setLevel(Level.ALL);
        }
        
        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record))
                return;
            
            String msg = getFormatter().format(record);
            getTextArea().append(msg);
        }
        
        @Override
        public void flush() {
            // nothing to do
        }
        
        @Override
        public void close() {
            // nothing to do
        }
        
    }
    
    class SetLevelAction extends AbstractAction {
        private static final long serialVersionUID = -4172527610978198123L;
        
        private Level level;
        
        public SetLevelAction(Level level) {
            super();
            this.level = level;
        }
        
        public SetLevelAction(Level level, String name) {
            super(name);
            this.level = level;
        }
        
        public SetLevelAction(Level level, String name, Icon icon) {
            super(name, icon);
            this.level = level;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            getHandler().setLevel(level);
        }
    }
}
