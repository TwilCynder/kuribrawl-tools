package KBUtil;

import java.util.Arrays;

public abstract class StringHelper {
    /**
     * Splits a string around occurences of a given separator into an array of strings.
     * Does not return empty fields.
     * @param str string to split
     * @param sep separator
     * @return String[] array of all fields separated by one or more occurences of the separator, i.e. of all the longest possible substrings without any occurence.
     */
    public static String[] split(String str, String sep){
        return Arrays.stream(str.split(sep)).filter(e -> e.trim().length() > 0).toArray(String[]::new);
    }
}
