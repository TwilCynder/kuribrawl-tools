package KBUtil;

import java.util.Iterator;
import java.util.List;

public class ConstList <T> implements Iterable<T> {
    private List<T> list;

    public ConstList(List<T> l){
        list = l;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
