package KBUtil;

import java.util.Iterator;

import KBUtil.functional.Transform;

public class TransformIterable<S, D> implements Iterable<D>{
    private Iterable<S> iterable;
    private Transform<S, D> transform;

    public TransformIterable(Iterable<S> iterable, Transform<S, D> transform){
        this.iterable = iterable;
        this.transform = transform;
    }

    @Override
    public Iterator<D> iterator() {
        return new TransformIterator<S, D>(iterable.iterator(), transform);
    }

}
