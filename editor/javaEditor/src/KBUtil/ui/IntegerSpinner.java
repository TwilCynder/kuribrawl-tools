package KBUtil.ui;

public class IntegerSpinner extends TwilSpinner {
    public IntegerSpinner(){
        super();
    }

    public IntegerSpinner(int col){
        super(col);
    }

    public void setValue(int val){
        setValue(Integer.valueOf(val));
    }

    public int getValueInt(){
        return ((Integer)getValue()).intValue();
    }

    public void validateInt(){
    }
}
