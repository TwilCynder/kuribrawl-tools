package KBUtil.ui.documentFilters;

import KBUtil.PathHelper;

public class PathDocumentFilter extends TwilDocumentFilter {
    private PathDocumentFilter(){}

    protected boolean checkString(String str){
        return PathHelper.isValidPathName(str);
    }

    public static PathDocumentFilter staticInstance = new PathDocumentFilter();
}
