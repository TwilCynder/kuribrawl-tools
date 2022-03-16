package gamedata;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GameData implements Iterable<Champion> {
    private Map<String, Champion> champions = new TreeMap<>();
    private Map<String, String> otherFiles = new TreeMap<>();
    RessourcePath originPath;

    public GameData(){
    }

    public Champion addChampion(String name){
        Champion c = new Champion(name);
        champions.put(name, c);
        return c;
    }

    public Champion addChampion(String name, String filename){
        Champion c = new Champion(name, filename);
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

    public void addOtherFile(String filename, String info){
        otherFiles.put(filename, info);
    }

    public Collection<Champion> getChampions(){
        return champions.values();
    }

    public Collection<Map.Entry<String, String>> getOtherFiles(){
        return otherFiles.entrySet();
    }

    public Iterator<Champion> iterator(){
        return getChampions().iterator();
    }

    public List<String> getUsedFilenames(){
        List<String> res = new LinkedList<>();

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
