package UI;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import KBUtil.ui.OpenPathButton;
import gamedata.EntityAnimation;

public class ChangeSourceImageForm extends SourceImageFilenameForm {
    private static String title = "Change source image";

    public ChangeSourceImageForm(Window frame, EntityAnimation anim) {
        super(frame, anim, title);
    }

    @Override
    protected Component initForm() {
        return super.initForm(OpenPathButton.Open);
    }

    @Override
    protected boolean confirm() {
        try {
            String newPathName = tfFilename.getText();

            if (newPathName.isEmpty()){
                JOptionPane.showMessageDialog(this, "You must select a source image.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Path newPath = Paths.get(newPathName);

            if (!ressourcePath.exists(newPath)){
                JOptionPane.showMessageDialog(this, "The selected image does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            anim.setSourceImage(ressourcePath.loadImage(newPath), newPathName);
            editor.updateVisualEditor();
            editor.notifyDataModified();

        } catch (InvalidPathException ex) {
            JOptionPane.showMessageDialog(editor, "Given path is not a valid file path", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(editor, "IO Error : could not load the file. Reason : " + ex.getLocalizedMessage(), "Inane error", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }
    
}
