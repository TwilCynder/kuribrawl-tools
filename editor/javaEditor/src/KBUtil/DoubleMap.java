package KBUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DoubleMap <K, V> implements Map<K, V> {
    protected static <K, V> void putDest(K key, V val, Map<K, V> dest){
        if (val == null){
            throw new IllegalArgumentException("Cannot insert null value into DoubleMap (key was " + key.toString() + ")");
        }
        dest.put(key, val);
    }

    protected static <K, V> void putReverse(Map<? extends K, ? extends V> source, Map<V, K> dest){
        for (var entry : source.entrySet()){
            putDest(entry.getValue(), entry.getKey(), dest);
        }
    }

    public static <K, V> Map<V, K> makeReverseMap(Map<K, V> map){
        var reverseMap = new TreeMap<V, K>();
        putReverse(map, reverseMap);
        return reverseMap;
    }

    private Map<K, V> left;
    private Map<V, K> right;

    //public Map<K, V> left(){ return this.left;}
    //public Map<V, K> right(){ return this.right;}


    public DoubleMap(){
        this.left = new TreeMap<K, V>();
        this.right = new TreeMap<V, K>();
    }

    public DoubleMap(Map<K, V> left){
        this.left = left;
        this.right = makeReverseMap(left);
    }

    @Override
    public void clear() {
        left.clear();
        right.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return left.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return right.containsKey(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return left.entrySet();
    }

    @Override
    public V get(Object key) {
        return left.get(key);
    }

    public V getLeft(K key){
        return left.get(key);
    }

    public K getRight(V key){
        return right.get(key);
    }

    @Override
    public boolean isEmpty() {
        return left.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return left.keySet();
    }

    public Set<V> valueSet(){
        return right.keySet();
    }

    @Override
    public V put(K key, V value) {
        if (value == null){
            throw new IllegalArgumentException("Cannot insert null value into DoubleMap (key was " + key.toString() + ")");
        }
        right.put(value, key);
        left .put(key, value);
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        left.putAll(m);
        putReverse(m, right);
    }

    @Override
    public V remove(Object key) {
        V val = left.get(key);
        if (val == null) return null;
        left.remove(key);
        right.remove(val);

        return val;
    }

    @Override
    public int size() {
        return left.size();
    }

    @Override
    public Collection<V> values() {
        return left.values();
    }
}
