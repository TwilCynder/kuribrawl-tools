package KBUtil.ui.documentFilters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
public abstract class TwilDocumentFilter extends DocumentFilter {
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

    protected static String getPredictedString(FilterBypass fb, int offset, int length, String str){
        return replace(getCurrentString(fb), str, offset, length);
    }

    protected abstract boolean checkString(String str);

    @Override
    public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a)throws BadLocationException {
        
        String predicted_result = getPredictedString(fb, offset, length, str);
        if (checkString (predicted_result)){
            super.replace(fb, offset, length, str, a);
        }

    }
}
