package gamedata;

import KBUtil.NullableResolvable;

/**
 * For elements in game data that refer to another gamedata element, which cannot be actually referenced until all the game data is initialized
 */
public class GameDataReference <T> extends NullableResolvable<T> {
    public void set(T v){
        val = v;
    }

    public GameDataReference(){}

    public GameDataReference(T v){
        resolve(v);
    }
}
