package KBUtil.ui;

import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
public class TwilDocumentFilter extends DocumentFilter {
    protected static String getCurrentString(FilterBypass fb) {
        try {
            return fb.getDocument().getText(0, fb.getDocument().getLength());
        } catch (BadLocationException ex){
            throw new IllegalStateException("Document " + fb.getDocument() + " threw a BadLocationException when trying to obtain text in range {offset : 0; length: getLength()}", ex);
        }
        
    }

    protected static String replace(String original, String added, int offset, int len){
        return original.substring(0, offset) + added + original.substring(offset + len);
    }
}
