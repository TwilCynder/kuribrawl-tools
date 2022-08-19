package KBUtil.ui;

import javax.swing.JSpinner;

public class TwilSpinner extends JSpinner{
    public TwilSpinner(){
        super();
    }

    public TwilSpinner(int col){
        setColumns(col);
    }

    public void setColumns(int col){
        ((JSpinner.DefaultEditor)getEditor()).getTextField().setColumns(col); //WHAT THE HELL IS THIS JAVA
    }
}
