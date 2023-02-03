package gamedata;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GameData implements Iterable<Champion> {
    private Map<String, Champion> champions = new TreeMap<>();
    private Map<String, Stage> stages = new TreeMap<>();
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

    public Champion tryChampion(String name, String descriptor_filename){
        Champion c = champions.get(name);
        if (c == null)  {
            c = addChampion(name, descriptor_filename);
        } else {
            c.setDescriptorFilename(descriptor_filename);
        }
        return c;
    }

    public Collection<Champion> getChampions(){
        return champions.values();
    }

    
    public Stage addStage(String name){
        Stage c = new Stage(name);
        stages.put(name, c);
        return c;
    }

    public Stage addStage(String name, String filename){
        Stage c = new Stage(name, filename);
        stages.put(name, c);
        return c;
    }

    public Stage getStage(String name){
        return stages.get(name);
    }

    public Stage tryStage(String name){
        Stage c = stages.get(name);
        if (c == null) c = addStage(name);
        return c;
    }

    public Collection<Stage> getChampgetStages(){
        return stages.values();
    }

    public void addOtherFile(String filename, String info){
        otherFiles.put(filename, info);
    }

    public Collection<Map.Entry<String, String>> getOtherFiles(){
        return otherFiles.entrySet();
    }

    public Iterator<Champion> iterator(){
        return getChampions().iterator();
    }

    public Champion getEntityAnimationOwner(EntityAnimation anim){
        for (Champion c : getChampions()){
            for (EntityAnimation a : c.getAnimations()){
                if (a == anim) return c;
            }
        }   
        return null;
    }

    /**
     * Returns a list of files that are not saved to (images, etc).
     * @return the list (see above)
     */
    public List<String> getUnmodifiedFilenames(){
        List<String> res = new LinkedList<>();

        for (Champion c : this){
            res.add(c.getDescriptorFilename());
            for (EntityAnimation anim : c){
                res.add(anim.getSourceFilename());
            }
        }

        for (var file : otherFiles.entrySet()){
            res.add(file.getKey());
        }

        return res;
    }

    /**
     * Returns a list of the files used by this GameData.  
     * If it was just loaded, matches exactly the files mentioned in project_db.txt.
     * @return
     */
    public List<String> getUsedFilenames(){
        List<String> res = new LinkedList<>();

        for (Champion c : this){
            res.add(c.getDescriptorFilename());
            for (EntityAnimation anim : c){
                res.add(anim.getSourceFilename());
                res.add(anim.getDescriptorFilename());
            }
        }

        for (var file : otherFiles.entrySet()){
            res.add(file.getKey());
        }

        return res;
    }

    @Override
    public String toString() {
        return "GameData (originPath :" + originPath + ", " + champions.size() + " champions)";
    }

    public void printAnimations(){
        System.out.println("Using this GameData : ");
		for (Champion c : this){
			System.out.println("==" + c.getDislayName() + "==");
            for (EntityAnimation anim : c){
                System.out.println(anim.getName());
            }
        }
    }

    public static boolean isValidIdentifier(String s){
        return s.matches("\\w+");
    }
}
