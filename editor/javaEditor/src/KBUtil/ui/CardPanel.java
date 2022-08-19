package KBUtil.ui;

import java.awt.CardLayout;
import javax.swing.JPanel;

public class CardPanel extends JPanel{
    private CardLayout layout;
    public CardPanel(){
        super();
        layout = new CardLayout();
        setLayout(layout);
    }

    public CardPanel(boolean buffered){
        super(buffered);
        layout = new CardLayout();
        setLayout(layout);
    }

    public void show(String name){
        layout.show(this, name);
    }
}
