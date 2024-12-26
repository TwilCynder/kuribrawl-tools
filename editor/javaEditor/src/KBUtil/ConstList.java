package KBUtil;

import java.util.Iterator;
import java.util.List;

public class ConstList <T> implements Iterable<T> {
    private Iterable<T> list;

    public ConstList(Iterable<T> l){
        list = l;
    }

    public static class ConstIterator<T> implements Iterator<T> {
        private Iterator<T> it;

        public ConstIterator(Iterator<T> it){
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public T next() {
            return it.next();
        }
    };

    @Override
    public Iterator<T> iterator() {
        return new ConstIterator<>(list.iterator());
    }
}
