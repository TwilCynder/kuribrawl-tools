package UI;

import java.awt.Component;
import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JPanel;

import KBUtil.ui.OpenPathButton;
import KBUtil.ui.OpenPathRestrictedButton;
import KBUtil.ui.TwilTextField;
import gamedata.EntityAnimation;
import gamedata.RessourcePath;

public class ChangeDescriptorFilenameForm extends EditorForm {
    EntityAnimation anim;
    private TwilTextField tfFilename;
    private RessourcePath currentRessourcePath;
    private String oldDescriptorFilename;

    public ChangeDescriptorFilenameForm(Window frame, String title, EntityAnimation anim) {
        super(frame, title);
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

        currentRessourcePath = editor.getCurrentRessourcePath();
        if (editor.getCurrentRessourcePath() == null) throw new IllegalStateException("A new animation form was opened with no current ressource path");
        Path currentPath = currentRessourcePath.getPath();

        OpenPathRestrictedButton button = new OpenPathRestrictedButton(this, OpenPathButton.Save, currentPath);
        button.setPreferredSize(new Dimension(25, 22));
        button.addSelectionListener(new TextFieldRelativePathSelectionListener(tfFilename, currentPath));
        button.addChoosableFileFilters(CommonFileFilters.datFilter);
        form.add(button);

        return form;
    }

    @Override
    protected boolean confirm() {
        if (oldDescriptorFilename != null){
            Path oldPath = currentRessourcePath.resolvePath(oldDescriptorFilename);
            //if (Files.exists(path, options))
        }

        return true;
    }
    
}
