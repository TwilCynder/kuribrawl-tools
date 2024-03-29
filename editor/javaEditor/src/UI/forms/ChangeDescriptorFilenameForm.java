package UI.forms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import KBUtil.PathHelper;
import KBUtil.ui.OpenPathButton;
import UI.Window;
import gamedata.Animation;

public class ChangeDescriptorFilenameForm extends RelativePathInputForm {
    Animation anim;
    private Path oldDescriptorPath;

    private static String title = "Change the descriptor file name";

    public ChangeDescriptorFilenameForm(Window frame, Animation anim) {
        super(frame, title);
        this.anim = anim;
        this.oldDescriptorPath = anim.getDescriptorPath();
        init();
    }

    @Override
    protected JPanel initForm() {
        JPanel form = super.initForm(OpenPathButton.Save);

        tfFilename.setText(oldDescriptorPath == null ? null : oldDescriptorPath.toString());

        return form;
    }

    @Override
    protected boolean confirm() {
        try {
            String newPathName = tfFilename.getText();
            Path newPath = (newPathName.isEmpty()) ? null :  Paths.get(newPathName);
            int res;

            if (newPathName.isEmpty()){
                if (anim.needDescriptor()){
                    if (JOptionPane.showOptionDialog(this, 
                    """
                        Entering an empty descriptor filename means no descriptor. \n
                        However, this animation can't be saved without a descriptor, so you will not be able to save unless the animation becomes default. Proceed ?
                    """,
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) != JOptionPane.YES_OPTION){
                        return false;
                    }
                }

                newPathName = null;
            }

            boolean fileMoved = false;

            if (oldDescriptorPath != null && ressourcePath.exists(oldDescriptorPath)){
                if (newPathName == null){
                    if (JOptionPane.showOptionDialog(this, 
                        "The former descriptor file was an existing file, do you want to delete it ?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION){

                        Files.delete(oldDescriptorPath);
                        fileMoved = true;
                    }
                } else if (!oldDescriptorPath.equals(newPath)){
    
                    if (JOptionPane.showOptionDialog(this, 
                        "The former descriptor file was an existing file, do you want to \nrename this file instead of just changing the descriptor pathname ?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION){
                        if (ressourcePath.exists(newPath)){
                            res = JOptionPane.showOptionDialog(this, 
                            newPath.toString() + " already exists. Overwrite it ?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

                            if (res != JOptionPane.YES_OPTION){
                                return false;
                            }
                        }

                        PathHelper.move(ressourcePath.resolvePath(oldDescriptorPath), ressourcePath.resolvePath(newPath), StandardCopyOption.REPLACE_EXISTING);
                        fileMoved = true;
                    }
                }
            }

            anim.setDescriptorFilename(newPath);

            if (fileMoved){
                if (JOptionPane.showOptionDialog(this, """
                A file has been moved or deleted. If you close the editor without saving, file information and your actual filesystem will be incoherent.
                Do you want to ensure it doesn't happen by saving now ?
                """, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION){
                    editor.saveData();
                }
            }

        } catch (InvalidPathException ex) {
            JOptionPane.showMessageDialog(editor, "Given path is not a valid file path", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex){
            JOptionPane.showMessageDialog(editor, "IO Error : could not rename the file. Reason : " + ex.getLocalizedMessage(), "Inane error", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }
    
}
