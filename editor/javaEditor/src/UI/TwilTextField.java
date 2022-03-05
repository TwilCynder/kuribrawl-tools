package UI;

import javax.swing.JTextField;

import KBUtil.functional.DoubleToString;
import KBUtil.functional.LongToString;
import KBUtil.functional.TextTransform;

public class TwilTextField extends JTextField {
    TextTransform text_transform = null;
    LongToString long_transform = null;
    DoubleToString double_transform = null;

    public TwilTextField(){
        super();
    }    

    public TwilTextField(String s){
        super(s);
    }

    public TwilTextField(int col){
        super(col);
    }

    public TwilTextField(String text, int columns){
        super(text, columns);
    }

    public TextTransform getTextTransform() {
        return this.text_transform;
    }

    public void setTextTransform(TextTransform text_transform) {
        this.text_transform = text_transform;
    }

    public LongToString getLongTransform() {
        return this.long_transform;
    }

    public void setLongTransform(LongToString long_transform) {
        this.long_transform = long_transform;
    }

    public DoubleToString getDoubleTransform() {
        return this.double_transform;
    }

    public void setDoubleTransform(DoubleToString double_transform) {
        this.double_transform = double_transform;
    }

    @Override
    public void setText(String s){
        if (text_transform == null) super.setText(s);
        else super.setText(text_transform.transform(s));
    }

    public void setText(long val){
        if (long_transform == null) super.setText(Long.toString(val));
        else super.setText(long_transform.transform(val));
    }

    public void setText(double val){
        if (double_transform == null) super.setText(Double.toString(val));
        else super.setText(double_transform.transform(val));
    }
}
