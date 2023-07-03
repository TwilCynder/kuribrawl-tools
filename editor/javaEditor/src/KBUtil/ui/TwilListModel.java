package KBUtil.ui;

import javax.swing.DefaultListModel;

public class TwilListModel<T> extends DefaultListModel<T> {
    public TwilListModel(){
        super();
    }

    public void add(Iterable<T> s){
        for (T elt : s){
            addElement(elt);
        }
    }

    public TwilListModel(Iterable<T> it){
        super();
        add(it);
    }
}
