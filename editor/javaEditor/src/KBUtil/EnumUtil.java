package KBUtil;

import java.util.Map;
import java.util.NoSuchElementException;

public abstract class EnumUtil {
    public static <E extends Enum<E>, K> E valueOf(Map<K, E> codes, K key){
        return codes.get(key);
    }

    public static <E extends Enum<E>> E valueOfSafe(Map<Integer, E> codes, int i) throws NoSuchElementException {
        E res = valueOf(codes, i);
        if (res == null){
            throw new NoSuchElementException("Provided code was " + i + " ; the maximum value is " + (codes.size() - 1));
        }
        return res;
    }

    public static <E extends Enum<E>, K> E valueOfSafe(Map<K, E> codes, K key) throws NoSuchElementException {
        E res = valueOf(codes, key);
        if (res == null){
            throw new NoSuchElementException("Provided key " + key + " doesn't match a value ; valid keys are " + codes.keySet());
        }
        return res;
    }
  
}
