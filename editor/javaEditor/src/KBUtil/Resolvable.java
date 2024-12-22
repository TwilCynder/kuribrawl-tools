package KBUtil;

public class Resolvable <T> {
    protected T val;

    public T get(){
        return val;
    }

    public void resolve(T val){
        this.val = val;
    }

    public boolean isResolved(){
        return val != null;
    }
}
