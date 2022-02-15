package gamedata;

import java.util.Map;
import java.util.TreeMap;

public class GameData {
    private Map<String, Champion> champions;

    public GameData(){
        champions = new TreeMap<>();
    }

    public Champion addChampion(String name){
        Champion c = new Champion(name);
        champions.put(name, c);
        return c;
    }

    public Champion getChampion(String name){
        return champions.get(name);
    }

    public Champion tryChampion(String name){
        Champion c = champions.get(name);
        if (c == null) c = addChampion(name);
        return c;
    }
}
