package KBUtil.ui;

import java.util.Vector;
import javax.swing.JList;
import javax.swing.ListModel;

public class TwilList<T> extends JList<T> {
    public TwilList(){
        super();
    }

    public TwilList(Vector<T> arg){
        super(arg);
    }

    public TwilList(T[] arg){
        super(arg);
    }

    public TwilList(ListModel<T> model){
        super(model);
    }

    public TwilList(Iterable<T> s){
        super(new TwilListModel<>(s));
    }


}
