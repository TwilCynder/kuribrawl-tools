package UI.forms;

import java.awt.Dimension;
import java.nio.file.Path;

import javax.swing.JPanel;

import KBUtil.ui.OpenPathRestrictedButton;
import KBUtil.ui.TwilTextField;
import KBUtil.ui.OpenPathButton.ChooserOpener;
import UI.CommonFileFilters;
import UI.Window;
import UI.listeners.TextFieldRelativePathSelectionListener;
import gamedata.RessourcePath;

public abstract class RelativePathInputForm extends EditorForm {
    protected TwilTextField tfFilename;
    protected RessourcePath ressourcePath;

    public RelativePathInputForm(Window frame, String title){
        super(frame, title, true);
    }

    protected JPanel initForm(ChooserOpener opener){
        JPanel form = new JPanel();

        tfFilename = new TwilTextField();
        tfFilename.setColumns(30);
        form.add(tfFilename);

        ressourcePath = editor.getCurrentRessourcePath();
        if (editor.getCurrentRessourcePath() == null) throw new IllegalStateException("A new animation form was opened with no current ressource path");
        Path currentPath = ressourcePath.getPath();

        OpenPathRestrictedButton button = new OpenPathRestrictedButton(this, opener, currentPath);
        button.setPreferredSize(new Dimension(25, 22));
        button.addSelectionListener(new TextFieldRelativePathSelectionListener(tfFilename, currentPath));
        button.addChoosableFileFilters(CommonFileFilters.datFilter);
        form.add(button);

        return form;
    }
}
