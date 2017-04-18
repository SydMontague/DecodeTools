package de.phoenixstaffel.decodetools.gui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class JLogWindow extends JFrame {
    private static final long serialVersionUID = 166721964687279818L;

    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    
    private Logger log;
    private LogHandler handler;
    private final JScrollPane scrollPane = new JScrollPane();
    private final JTextArea textPane = new JTextArea();
    
    public JLogWindow(Logger logger) {
        this.log = logger;
        this.handler = new LogHandler();
        log.addHandler(handler);
        setBounds(0, 0, 500, 300);
        
        setTitle("Log Window");
        textPane.setEnabled(false);
        textPane.setEditable(false);
        ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        setContentPane(scrollPane);
        
        scrollPane.setViewportView(textPane);
    }
    
    protected JTextArea getTextArea() {
        return textPane;
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
            if(!isLoggable(record))
                return;

            String msg = getFormatter().format(record);
            getTextArea().append(msg);
        }

        @Override
        public void flush() {
            //nothing to do
        }

        @Override
        public void close() {
            //nothing to do
        }
        
    }
}
