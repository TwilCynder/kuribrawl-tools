package KBUtil.ui.documentFilters;

public class RealNumberDocumentFilter extends TwilDocumentFilter {
    /**
     * Returns whether a string can be considered numeric (empty strings are considered numeric)
     * @param str must not be null
     * @return
     */
    protected boolean checkString(String str){
        if (str.isEmpty()) return true;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static RealNumberDocumentFilter staticInstance = new RealNumberDocumentFilter();
}
