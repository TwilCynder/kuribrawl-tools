package KBUtil;

public class NullableResolvable <T> extends Resolvable<T> {
    private boolean resolved = false;

    @Override
    public void resolve(T val){
        resolved = true;
        super.resolve(val);
    }

    @Override
    public boolean isResolved(){
        return resolved;
    }
}
