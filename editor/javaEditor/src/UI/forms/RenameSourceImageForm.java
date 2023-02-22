package UI.forms;

import java.awt.Component;

import javax.swing.JPanel;

import KBUtil.Result;
import KBUtil.ui.OpenPathButton;
import UI.Window;
import gamedata.Animation;

public class RenameSourceImageForm extends RenameFileForm {
    protected String oldSourceImageFilename;

    Animation anim;

    private static String title = "Change the source image";

    public RenameSourceImageForm(Window frame, Animation anim) {
        super(frame, title);
        this.oldSourceImageFilename = anim.getSourceFilename();
        this.anim = anim;
        init();
    }
    
    @Override
    protected Component initForm() {
        JPanel form = super.initForm(OpenPathButton.Save);
        tfFilename.setText(oldSourceImageFilename);
        return form;
    }

    @Override
    protected boolean confirm() {
        Result<String> res = super.confirm(oldSourceImageFilename);

        System.out.println(res.data);

        if (res.data != null){
            anim.setSourceFilename(res.data);
            showSaveMessage();
        }
        
        return res.success;
    }
}
