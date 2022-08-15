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

        Object[] options = {"oui", "non"};

        form = initForm();
        optionPane = new JOptionPane(form, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null);
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
        optionPane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, 
            new PropertyChangeListener() {
                boolean resetting = false;

                public void propertyChange(PropertyChangeEvent evt) {
                    if (resetting) return;
                    String prop = evt.getPropertyName();

                    if (isVisible() 
                            && (evt.getSource() == optionPane)) {
                        
                        Integer res = (Integer)evt.getNewValue();
                        if (res.intValue() != JOptionPane.OK_OPTION || confirm()){
                            setVisible(false); //close if ok was not selected OR if it was but confirm returns false
                        } else {
                            resetting = true;
                            optionPane.setValue(-1);
                            resetting = false;
                        }

                    }
                }
            }
        );
    }

    protected abstract Component initForm();

    protected abstract boolean confirm();
}
