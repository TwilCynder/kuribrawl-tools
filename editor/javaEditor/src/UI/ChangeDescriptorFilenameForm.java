package UI;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import KBUtil.ui.OpenPathButton;
import KBUtil.ui.OpenPathRestrictedButton;
import KBUtil.ui.TwilTextField;
import gamedata.EntityAnimation;
import gamedata.RessourcePath;

public class ChangeDescriptorFilenameForm extends EditorForm {
    EntityAnimation anim;
    private TwilTextField tfFilename;
    private RessourcePath ressourcePath;
    private String oldDescriptorFilename;

    public ChangeDescriptorFilenameForm(Window frame, String title, EntityAnimation anim) {
        super(frame, title, true);
        this.anim = anim;
        this.oldDescriptorFilename = anim.getDescriptorFilename();
        init();
    }

    @Override
    protected Component initForm() {
        JPanel form = new JPanel();

        tfFilename = new TwilTextField(anim.getDescriptorFilename());
        tfFilename.setColumns(30);
        form.add(tfFilename);

        ressourcePath = editor.getCurrentRessourcePath();
        if (editor.getCurrentRessourcePath() == null) throw new IllegalStateException("A new animation form was opened with no current ressource path");
        Path currentPath = ressourcePath.getPath();

        OpenPathRestrictedButton button = new OpenPathRestrictedButton(this, OpenPathButton.Save, currentPath);
        button.setPreferredSize(new Dimension(25, 22));
        button.addSelectionListener(new TextFieldRelativePathSelectionListener(tfFilename, currentPath));
        button.addChoosableFileFilters(CommonFileFilters.datFilter);
        form.add(button);

        return form;
    }

    @Override
    protected boolean confirm() {
        //TODO utiliser le Remove Descriptor Filename
        try {
            String newPathName = tfFilename.getText();
            int res;

            if (!newPathName.isEmpty()){
                Path newPath = Paths.get(newPathName);
                if (oldDescriptorFilename != null && !oldDescriptorFilename.equals(newPath.toString())){
                    Path oldPath = ressourcePath.resolvePath(oldDescriptorFilename);
                    if (ressourcePath.exists(oldPath)){
                        res = JOptionPane.showOptionDialog(editor, 
                        "The former descriptor file was an existing file, do you want to \nrename this file instead of just changing the descriptor pathname ?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
        
                        if (res == JOptionPane.YES_OPTION){
                            if (ressourcePath.exists(newPath)){
                                res = JOptionPane.showOptionDialog(editor, 
                                newPath.toString() + " already exists. Overwrite it ?",
                         "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
    
                                if (res == JOptionPane.NO_OPTION){
                                    return false;
                                }
                            }
    
                            System.out.println("Move " + ressourcePath.resolvePath(oldPath) + " to " + ressourcePath.resolvePath(newPath) );
                            Files.move(ressourcePath.resolvePath(oldPath), ressourcePath.resolvePath(newPath), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } else {
                    if (ressourcePath.exists(newPath)){
                        res = JOptionPane.showOptionDialog(editor, 
                        newPath.toString() + " already exists. When saving the Game Data, it will be overwritten. Proceed ?",
                 "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
    
                        if (res == JOptionPane.NO_OPTION){
                            return false;
                        }
                    }
                }
            }

            anim.setDescriptorFilename(newPathName);

        } catch (InvalidPathException ex) {
            JOptionPane.showMessageDialog(editor, "Given path is not a valid file path", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex){
            JOptionPane.showMessageDialog(editor, "IO Error : could not rename the file. Aborting.", "Inane error", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }
    
}
