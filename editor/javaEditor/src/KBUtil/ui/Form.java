package KBUtil.ui;

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

    private int result = -1;

    public Form(Window frame, String title){
        super(frame, title);
    }

    public Form(Window frame, String title, boolean modal){
        this(frame, title);
        setModal(modal);
    }

    protected void init(){
        form = initForm();
        optionPane = new JOptionPane(form, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null);
        setContentPane(optionPane);

        __initListener();

        pack();
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

                    if (isVisible()
                            && (evt.getSource() == optionPane)) {

                        Integer res = (Integer)evt.getNewValue();
                        if (res.intValue() != JOptionPane.OK_OPTION || confirm()){
                            result = res;
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

    /**
     * Makes the form visible. 
     * @return the choosen result if the form is modal, or always -1 if it's not (just don't use the result value of a non-modal form)
     */
    public int showForm(){
        setVisible(true);
        return result;
    }

    protected abstract Component initForm();

    protected abstract boolean confirm();
}
