package gamedata;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GameData implements Iterable<Champion> {
    private Map<String, Champion> champions;
    RessourcePath originPath;

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

    public Iterator<Champion> getChampions(){
        return champions.values().iterator();
    }

    public Iterator<Champion> iterator(){
        return getChampions();
    }

    public List<Path> getUsedFilenames(){
        List<Path> res = new LinkedList<>();

        for (Champion c : this){
            res.add(c.getDescriptorFilename());
            for (EntityAnimation anim : c){
                res.add(anim.getSourceFilename());
                res.add(anim.getDescriptorFilename());
            }
        }
        return res;
    }
}
