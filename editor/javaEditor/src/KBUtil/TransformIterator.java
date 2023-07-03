package KBUtil;


import java.util.Iterator;

import KBUtil.functional.Transform;

public class TransformIterator<S, D> extends MappedIterator<S, D> {
    private Transform<S, D> transform;

    public TransformIterator(Iterator<S> it, Transform<S, D> transform){
        super(it);
        this.transform = transform;
    }

    @Override
    protected D map(S elt) {
        return transform.transform(elt);
    }

}
