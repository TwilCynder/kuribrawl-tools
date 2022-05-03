package UI;

import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument {
    @Override
    public void insertString(int offset, String str, AttributeSet a){
        System.out.println("Insert");
    }
}
