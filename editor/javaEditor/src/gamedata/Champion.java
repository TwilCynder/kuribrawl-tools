package gamedata;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import KBUtil.PathHelper;

import java.awt.Image;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

class ChampionVals  {
    public double walk_speed;
    public double dash_speed;
    public double dash_start_speed;
    public double dash_turn_accel;
    public double dash_stop_deceleration;
    public double traction; //grounded horizontal deceleration
    public double max_air_speed;
    public double air_acceleration;
    public double air_friction;
    public double jump_speed;
    public double short_hop_speed;
    double air_jump_speed;
    double ground_forward_jump_speed;
    double ground_backward_jump_speed;
    double air_forward_jump_speed;
    double air_backward_jump_speed;
    double gravity;
    double max_fall_speed;
    double fast_fall_speed;
    double weight;
    int jump_squat_duration;
    int dash_start_duration;
    int dash_stop_duration;
    int dash_turn_duration;
    int landing_duration;
    int guard_start_duration;
    int guard_stop_duration;
    int shield_x;
    int shield_y;
    int shield_size;
    int air_jumps;
}

public class Champion implements AnimationPool<EntityAnimation>{
    public ChampionVals vals;
    private String name;
    private String displayName;

    private Path descriptor_file;

    private Map<String, EntityAnimation> animations = new TreeMap<>();
    private Map<String, Move> moves = new TreeMap<>();

    public Champion(String name, String filename) throws IllegalArgumentException {
        this(name);
        setDescriptorFilename(filename); 
    }

    public Champion(String name){
        this.name = name;
        this.displayName = name;
    }

    public String getName(){
        return name;
    }

    public void setDisplayName(String name){
        displayName = name;
    }

    public String getDislayName(){
        return displayName;
    }

    public Move getMove(String name){
        return moves.get(name);
    }

    public Move addMoveInfo(String name){
        return moves.put(name, new Move());
    }

    public EntityAnimation getAnimation(String name){
        return animations.get(name);
    }

    public Collection<EntityAnimation> getAnimations(){
        return animations.values();
    }

    public Iterator<EntityAnimation> iterator(){
        return getAnimations().iterator();
    }

    public EntityAnimation addAnimation(String name, Image source, int nbFrames, String source_filename, String descriptor_filename) throws InvalidPathException {
        EntityAnimation anim = new EntityAnimation(name, source, nbFrames, source_filename, descriptor_filename);
        animations.put(name, anim);
        return anim;
    }

    @Deprecated
    public EntityAnimation addAnimation(String name, int nbFrames, Image source){
        EntityAnimation anim = new EntityAnimation(nbFrames, name, source);
        animations.put(name, anim);
        return anim;
    }

    public String getDescriptorFilename(){
        return PathHelper.pathToStringOrNull(descriptor_file);
    }

    public Path getDescriptorPath(){
        return descriptor_file;
    }

    public void setDescriptorFilename(String filename) throws NullPointerException{
        setDescriptorFilename(PathHelper.stringToPathOrNull(filename));
    }

    public void setDescriptorFilename(Path path) throws NullPointerException{
        if (path == null) throw new NullPointerException("Descriptor path cannot be null");
        descriptor_file = path;
    }

    @Override
    public String toString() {
        return "Champion [displayName=" + displayName + ", name=" + name + "]";
    }


}
