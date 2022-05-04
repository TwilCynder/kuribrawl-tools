package UI;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class RealNumberDocumentFilter extends TwilDocumentFilter {
    /**
     * Returns whether a string can be considered numeric (empty strings are considered numeric)
     * @param str must not be null
     * @return
     */
    private static boolean isNumberString(String str){
        if (str.isEmpty()) return true;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a)throws BadLocationException {
        
        String predicted_result = getCurrentString(fb);
        predicted_result = replace(predicted_result, str, offset, length);
        if (isNumberString(predicted_result)){
            super.replace(fb, offset, length, str, a);
        }

    }

    public static RealNumberDocumentFilter staticInstance = new RealNumberDocumentFilter();
}
