package UI;

import java.awt.Component;
import java.awt.Dimension;
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

    public ChangeDescriptorFilenameForm(Window frame, String title, EntityAnimation anim) {
        super(frame, title);
        this.anim = anim;

        init();
    }

    @Override
    protected Component initForm() {
        JPanel form = new JPanel();

        tfFilename = new TwilTextField(anim.getDescriptorFilename());
        form.add(tfFilename);

        RessourcePath currentRessourcePath = editor.getCurrentRessourcePath();
        if (editor.getCurrentRessourcePath() == null) throw new IllegalStateException("A new animation form was opened with no current ressource path");
        Path currentPath = currentRessourcePath.getPath();

        OpenPathRestrictedButton button = new OpenPathRestrictedButton(this, OpenPathButton.Save, currentPath);
        button.setSize(new Dimension(22, 25));
        button.addSelectionListener(new TextFieldRelativePathSelectionListener(tfFilename, currentPath));
        button.addChoosableFileFilters(CommonFileFilters.datFilter);
        form.add(button);

        return null;
    }

    @Override
    protected boolean confirm() {
        return false;
    }
    
}
