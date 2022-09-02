package UI.forms;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;

import KBUtil.PathHelper;
import KBUtil.Result;
import UI.Window;

public abstract class RenameFileForm extends RelativePathInputForm {

    public RenameFileForm(Window frame, String title) {
        super(frame, title);
    }
    
    public Result<String> confirm(String oldPathName){
        
        try {
            String newPathName = getCurrentPathname();
            int res;

            if (newPathName.isEmpty()){
                JOptionPane.showMessageDialog(this, "You must select a source image.", "Error", JOptionPane.ERROR_MESSAGE);
                return new Result<String>(false);
            }

            Path oldPath = Paths.get(oldPathName);
            Path newPath = Paths.get(newPathName);

            if (oldPath.equals(newPath)){
                return new Result<>(true);
            }

            if (ressourcePath.exists(newPath)){
                res = JOptionPane.showOptionDialog(this, 
                newPath.toString() + " already exists. Overwrite it ?",
                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

                if (res != JOptionPane.YES_OPTION){
                    return new Result<String>(false);
                }
            }

            PathHelper.move(ressourcePath.resolvePath(oldPath), ressourcePath.resolvePath(newPath), StandardCopyOption.REPLACE_EXISTING);

            return new Result<>(true, newPathName);
        } catch (InvalidPathException ex) {
            JOptionPane.showMessageDialog(editor, "Given path is not a valid file path", "Inane error", JOptionPane.ERROR_MESSAGE);
            return new Result<String>(false);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(editor, "IO Error : could not rename the file. Reason : " + ex.getLocalizedMessage(), "Inane error", JOptionPane.ERROR_MESSAGE);
            return new Result<>(true);
        }
    }

    protected void showSaveMessage(){
        if (JOptionPane.showOptionDialog(this, """
            A file has been moved or deleted. If you close the editor without saving, file information and your actual filesystem will be incoherent.
            Do you want to ensure it doesn't happen by saving now ?
            """, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION){
                editor.saveData();
        }
    }

}
