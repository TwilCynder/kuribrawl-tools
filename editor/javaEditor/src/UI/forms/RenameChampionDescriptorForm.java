package UI.forms;

import java.awt.Component;

import java.nio.file.Path;

import javax.swing.JPanel;

import KBUtil.Result;
import KBUtil.ui.OpenPathButton;
import UI.Window;
import gamedata.Champion;

public class RenameChampionDescriptorForm extends RenameFileForm {

    private static String title = "Rename champion descriptor file";
    private Champion champion;
    private Path oldDescriptor;

    public RenameChampionDescriptorForm(Window frame, Champion champion) {
        super(frame, title);
        this .champion = champion;
        this .oldDescriptor = champion.getDescriptorPath();
        init();
    }

    @Override
    protected Component initForm() {
        JPanel form = super.initForm(OpenPathButton.Save);
        tfFilename.setText(champion.getDescriptorFilename());
        return form;
    }

    @Override
    protected boolean confirm() {        
        Result<String> res = super.confirm(oldDescriptor.toString());

        if (res.data != null){
            champion.setDescriptorFilename(res.data);
            showSaveMessage();
        }
        
        return res.success;
    }
}
