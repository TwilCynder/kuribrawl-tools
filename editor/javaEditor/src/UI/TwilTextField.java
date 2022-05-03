package UI;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import java.awt.Color;

import KBUtil.functional.DoubleToString;
import KBUtil.functional.LongToString;
import KBUtil.functional.TextTransform;

public class TwilTextField extends JTextField {
    TextTransform text_transform = null;
    LongToString long_transform = null;
    DoubleToString double_transform = null;

    private Color normal_background_color = UIManager.getColor("TextField.background");
    private boolean is_using_normal_backround = true;

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

    {
        //normal_background_color = super.getBackground();
    }

    /*
    @Override
    public void setBackground(Color bg){
        normal_background_color = bg;
        if (is_using_normal_backround){
            super.setBackground(bg);
        }
    }*/

    
    public void resetCurrentBackground(){
        super.setBackground(normal_background_color);
        is_using_normal_backround = true;
    }

    
    public void setCurrentBackground(Color bg){
        if (normal_background_color.equals(bg)){
            resetCurrentBackground();
        } else {
            super.setBackground(bg);
            is_using_normal_backround = false;
        }
    }

    public Color getNormalBackground(){
        return normal_background_color;
    }

    
    public Color getCurrentBackground(){
        return super.getBackground();
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

    private static final Color error_color = new Color(255, 0, 0);
    public int getInt() throws NumberFormatException {
        try {
            int res = Integer.parseInt(getText());
            resetCurrentBackground();
            return res;
        } catch (NumberFormatException ex){
            setCurrentBackground(error_color);
            throw ex;
        }
    }

    public void setDocumentFilter(DocumentFilter filter){
        Document doc = getDocument();
        if (doc instanceof AbstractDocument){
            ((AbstractDocument)doc).setDocumentFilter(filter);
        }
    }

}
