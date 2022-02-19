package gamedata;

import java.util.ArrayList;
import java.util.List;

import KBUtil.Vec2;


public class EntityFrame {
    public List<Hurtbox> hurtboxes = new ArrayList<>();
    public List<Hitbox>  hitboxes  = new ArrayList<>();

    public class FrameMovementAxis {
        public boolean enabled = false;
        public boolean set_speed = false;
        public boolean whole_frame = false;
        public double value = 0.0;
    }

    public Vec2<FrameMovementAxis> movement;

    public EntityFrame(){
        movement = new Vec2<>(new FrameMovementAxis(), new FrameMovementAxis());
    }

    public void addHurtbox(int x, int y, int w, int h){
        hurtboxes.add(new Hurtbox(x, y, w, h));
    }

    public void addDamageHurtbox(){
        
    }

}

