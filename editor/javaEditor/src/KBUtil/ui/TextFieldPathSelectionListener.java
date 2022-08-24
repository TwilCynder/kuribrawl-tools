package KBUtil.ui;

import java.nio.file.Path;

import javax.swing.JTextField;

import KBUtil.ui.OpenPathButton.PathSelectionListener;

public class TextFieldPathSelectionListener implements PathSelectionListener {

    protected JTextField field;

    public TextFieldPathSelectionListener(JTextField field){
        this.field = field;
    }

    @Override
    public void pathSelected(Path selected) {
        field.setText(selected.toString());
    }
    
}
