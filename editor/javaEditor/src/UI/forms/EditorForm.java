package UI.forms;

import KBUtil.ui.Form;
import UI.Window;

public abstract class EditorForm extends Form {
    protected Window editor;

    public EditorForm(Window frame, String title){
        super(frame, title);
        editor = frame;
    }

    public EditorForm(Window frame, String title, boolean modal){
        super(frame, title, modal);
        editor = frame;
    }
}
