package UI.forms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import KBUtil.ui.OpenPathButton;
import UI.Window;
import gamedata.EntityAnimation;

public class ChangeDescriptorFilenameForm extends RelativePathInputForm {
    EntityAnimation anim;
    private String oldDescriptorFilename;

    private static String title = "Change the descriptor file name";

    public ChangeDescriptorFilenameForm(Window frame, EntityAnimation anim) {
        super(frame, title);
        this.anim = anim;
        this.oldDescriptorFilename = anim.getDescriptorFilename();
        init();
    }

    @Override
    protected JPanel initForm() {
        JPanel form = super.initForm(OpenPathButton.Save);

        tfFilename.setText(oldDescriptorFilename);

        return form;
    }

    @Override
    protected boolean confirm() {
        try {
            String newPathName = tfFilename.getText();
            int res;

            if (newPathName.isEmpty()){
                if (anim.areFramesDefault().needDescriptor()){
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

            if (oldDescriptorFilename != null && !oldDescriptorFilename.isEmpty()){
                Path oldPath = ressourcePath.resolvePath(oldDescriptorFilename);
                if (ressourcePath.exists(oldPath)){
                    if (newPathName == null){
                        if (JOptionPane.showOptionDialog(this, 
                            "The former descriptor file was an existing file, do you want to delete it ?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION){

                            Files.delete(oldPath);
                            fileMoved = true;
                        }
                    } else {
                        Path newPath = (newPathName.isEmpty()) ? null :  Paths.get(newPathName);
        
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

                            Files.move(ressourcePath.resolvePath(oldPath), ressourcePath.resolvePath(newPath), StandardCopyOption.REPLACE_EXISTING);
                            fileMoved = true;
                        }
                    }
                }
            }

            anim.setDescriptorFilename(newPathName);

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
