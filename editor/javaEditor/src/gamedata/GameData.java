package gamedata;

import java.util.Map;

public class GameData {
    private Map<String, Champion> champions;

    public Champion addChampion(String name){
        return champions.put(name, new Champion(name));
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
