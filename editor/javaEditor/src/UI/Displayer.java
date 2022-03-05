package UI;

import javax.swing.JComponent;

public interface Displayer {
    public void update();
    default public JComponent getComponent(){
        return null;
    }
}
