package KBUtil.ui.documentFilters;


public class AlphanumericDocumentFilter extends TwilDocumentFilter {
    private AlphanumericDocumentFilter(){};
    
    /**
     * Returns whether a string can be considered numeric (empty strings are considered numeric)
     * @param str must not be null
     * @return
     */
    protected boolean checkString(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            System.out.println(c+0);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60 && c != 0x5f) || c > 0x7a)
                return false;
        }
        return true;
    }

    public static AlphanumericDocumentFilter staticInstance = new AlphanumericDocumentFilter();
}
