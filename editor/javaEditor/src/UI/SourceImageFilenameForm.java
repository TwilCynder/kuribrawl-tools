package UI;

import javax.swing.JPanel;

import KBUtil.ui.OpenPathButton.ChooserOpener;
import gamedata.EntityAnimation;

public abstract class SourceImageFilenameForm extends RelativePathInputForm {
    EntityAnimation anim;

    public SourceImageFilenameForm(Window frame, EntityAnimation anim, String title) {
        super(frame, title);
        this.anim = anim;
        init();
    }

    @Override
    protected JPanel initForm(ChooserOpener opener) {
        JPanel form = super.initForm(opener);

        tfFilename.setText(anim.getSourceFilename());

        return form;
    }
    
}
