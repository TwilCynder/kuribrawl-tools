package UI;

import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * A form
 * fuck this
 */
public abstract class Form extends JDialog {
    protected Component form;
    private JOptionPane optionPane;

    public Form(Window frame, String title){
        super(frame, title);

        form = initForm();
        optionPane = new JOptionPane(form, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        setContentPane(optionPane);

        __initListener();

        pack();
        setVisible(true);
        setLocationRelativeTo(getOwner());
    }

    public Component getForm() {
        return form;
    }

    private void __initListener(){
        optionPane.addPropertyChangeListener(
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    String prop = evt.getPropertyName();

                    if (isVisible() 
                            && (evt.getSource() == optionPane)
                            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                        
                        Integer res = (Integer)evt.getNewValue();
                        if (res.intValue() == JOptionPane.OK_OPTION){
                            confirm();
                        }

                        setVisible(false);
                    }
                }
            }
        );
    }

    protected abstract Component initForm();

    protected abstract void confirm();
}
