package UI.listeners;

import java.nio.file.Path;

import javax.swing.JTextField;

import KBUtil.ui.TextFieldPathSelectionListener;

public class TextFieldRelativePathSelectionListener extends TextFieldPathSelectionListener {
    Path basePath;

    public TextFieldRelativePathSelectionListener(JTextField field, Path basePath) {
        super(field);
        if (basePath == null) throw new IllegalArgumentException("Cannot create a TFRPSL with a null Path");
        this.basePath = basePath;
    }

    @Override public void pathSelected(Path path){
        Path relativePath = basePath.relativize(path);
        field.setText(relativePath.toString());
    }
    
}
