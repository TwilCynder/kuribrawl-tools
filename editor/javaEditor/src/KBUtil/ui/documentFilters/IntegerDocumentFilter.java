package KBUtil.ui.documentFilters;

public class IntegerDocumentFilter extends TwilDocumentFilter {
    /**
     * Hide default constructor : you don't need to instanciate this class, as the static instance if enough.
     */
    private IntegerDocumentFilter(){};
    
    /**
     * Returns whether a string can be considered numeric (empty strings are considered numeric)
     * @param str must not be null
     * @return
     */
    protected boolean checkString(String str){
        if (str.isEmpty()) return true;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static IntegerDocumentFilter staticInstance = new IntegerDocumentFilter();
}
