package de.phoenixstaffel.decodetools.gui.util;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * An implementation of {@link AbstractAction} that allows passing a {@link Consumer}<ActionEvent>
 * during construction that gets executed when actionPerformed is called.
 * 
 * This allows Lambdas and Method references to be used and makes separate classes for each Action unnecessary.
 */
public class FunctionAction extends AbstractAction {
    private static final long serialVersionUID = 6115002314325923497L;

    private Consumer<ActionEvent> function;
    
    /**
     * Creates a new instances with just the function passed.
     * 
     * @param function the {@link Consumer}<ActionEvent> to be executed on actionPerformed
     */
    public FunctionAction(Consumer<ActionEvent> function) {
        super();
        this.function = function;
    }

    /**
     * Creates a new instance with a name and a function passed.
     * The name gets used for Button labels and other things by Swing.
     * 
     * @param name the name for this action
     * @param function the {@link Consumer}<ActionEvent> to be executed on actionPerformed
     */
    public FunctionAction(String name, Consumer<ActionEvent> function) {
        super(name);
        this.function = function;
    }

    /**
     * Creates a new instance with a name, an icon and a function passed.
     * The name and the icon get by Swing, depending on where this action is attached to.
     * 
     * @param name the name for this action
     * @param icon the icon for this action
     * @param function the {@link Consumer}<ActionEvent> to be executed on actionPerformed
     */
    public FunctionAction(String name, Icon icon, Consumer<ActionEvent> function) {
        super(name, icon);
        this.function = function;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        function.accept(e);
    }
    
}
