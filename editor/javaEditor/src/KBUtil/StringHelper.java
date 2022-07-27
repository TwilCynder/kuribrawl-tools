package KBUtil;

import java.util.Arrays;

public abstract class StringHelper {
    public static String[] split(String str, String sep){
        return Arrays.stream(str.split(sep)).filter(e -> e.trim().length() > 0).toArray(String[]::new);
    }
}
