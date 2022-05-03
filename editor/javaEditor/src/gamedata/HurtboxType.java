package gamedata;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public enum HurtboxType {
    NORMAL,
    PROTECTED,
    INVINCIBLE,
    INTANGIBLE;

    private static Map<Integer, HurtboxType> codes;

    static {
        codes = new TreeMap<>();
        HurtboxType[] vals = values();
        for (int i = 0; i < vals.length; i++){
            codes.put(i, vals[i]);
        }
    } 

    public static HurtboxType valueOf(int i){
        return codes.get(i);
    }

    public static HurtboxType valueOfSafe(int i) throws NoSuchElementException{
        HurtboxType res = valueOf(i);
        if (res == null){
            throw new NoSuchElementException();
        }
        return res;
    }
}
