package gamedata;

import java.util.Map;
import java.util.TreeMap;
import java.awt.Image;

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

public class Champion {
    public ChampionVals vals;
    private String name;
    private String displayName;

    private Map<String, EntityAnimation> animations = new TreeMap<>();
    private Map<String, Move> moves = new TreeMap<>();
    
    public Champion(String name){
        this.name = name;
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

    public Animation getAnimation(String name){
        return animations.get(name);
    }

    public Animation addAnimation(String name, int nbFrames, Image source){
        return animations.put(name, new EntityAnimation(nbFrames, name, source));
    }
}
