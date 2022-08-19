package UI;

import java.awt.Component;

public abstract class OpenPathRestrictedButton extends OpenPathButton {

    public OpenPathRestrictedButton(Component parent, ChooserOpener opener) {
        super(parent, opener);
    }
    
    public OpenPathRestrictedButton(Component parent){
        super(parent);
    }

}
