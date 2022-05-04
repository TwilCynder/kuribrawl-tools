package UI;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument {
    @Override
    public void insertString(int offset, String str, AttributeSet a)throws BadLocationException{
        System.out.println("Insert");
        String current_str = getText(0, getLength());
        try {
            StringBuffer buf = new StringBuffer(current_str); //fuck you for making me do this java :)
            Integer.parseInt(buf.toString());
        } catch (NumberFormatException e){
            System.out.println("Excepción");
        }
        super.insertString(offset, str, a);
    }
}
