package UI.forms;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;

import KBUtil.ui.OpenPathButton;
import UI.Window;
import gamedata.EntityAnimation;

public class RenameSourceImageForm extends SourceImageFilenameForm{
    protected String oldSourceImageFilename;
    
    private static String title = "Change the source image";

    public RenameSourceImageForm(Window frame, EntityAnimation anim) {
        super(frame, anim, title);
        this.oldSourceImageFilename = anim.getSourceFilename();
    }
    
    @Override
    protected boolean confirm() {

        try {
            String newPathName = tfFilename.getText();
            int res;

            if (newPathName.isEmpty()){
                JOptionPane.showMessageDialog(this, "You must select a source image.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Path oldPath = Paths.get(oldSourceImageFilename);
            Path newPath = Paths.get(newPathName);

            if (ressourcePath.exists(newPath)){
                res = JOptionPane.showOptionDialog(this, 
                newPath.toString() + " already exists. Overwrite it ?",
                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

                if (res != JOptionPane.YES_OPTION){
                    return false;
                }
            }

            Files.move(ressourcePath.resolvePath(oldPath), ressourcePath.resolvePath(newPath), StandardCopyOption.REPLACE_EXISTING);

            anim.setSourceFilename(newPathName);

            if (JOptionPane.showOptionDialog(this, """
            A file has been moved or deleted. If you close the editor without saving, file information and your actual filesystem will be incoherent.
            Do you want to ensure it doesn't happen by saving now ?
            """, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION){
                editor.saveData();
            }

        } catch (InvalidPathException ex) {
            JOptionPane.showMessageDialog(editor, "Given path is not a valid file path", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex){
            JOptionPane.showMessageDialog(editor, "IO Error : could not rename the file. Reason : " + ex.getLocalizedMessage(), "Inane error", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }

    @Override
    protected Component initForm() {
        return super.initForm(OpenPathButton.Save);
    }
}
