package KBUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * technically not a Collection<T>
 * fuck you
 */
public class ConstCollection <T> implements Iterable<T> {
    private Collection<T> collection;

    public ConstCollection(Collection<T> l){
        collection = l;
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
        return new ConstIterator<>(collection.iterator());
    }

    public int size(){
        return collection.size();
    }

    public boolean contains(T o){
        return collection.contains(o);
    }

    public boolean containsAll(Collection<T> c){
        return collection.containsAll(c);
    }

    public boolean equals(Object o){
        return collection.equals(o);   
    }

    public int hashCode(){
        return collection.hashCode();
    }

    public boolean isEmpty(){
        return collection.isEmpty();
    }
}
