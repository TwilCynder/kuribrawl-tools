package KBUtil;

import java.util.Iterator;

public abstract class MappedIterator<S, D> implements Iterator<D> {

    private Iterator<S> it;

    public MappedIterator(Iterator<S> it){
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public D next() {
        S val = it.next();
        return map(val);
    }

    protected abstract D map(S elt);

    
}
