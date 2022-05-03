package UI;

import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;

public class IntegerDocumentFilter extends DocumentFilter {

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr){
        System.out.println(string);  
    }
}
