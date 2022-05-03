package gamedata;

import java.util.ArrayList;
import java.util.List;

import KBUtil.Rectangle;
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

    private final Vec2<FrameMovementAxis> movement;

    public EntityFrame(){
        movement = new Vec2<>(new FrameMovementAxis(), new FrameMovementAxis());
    }

    public void addHurtbox(Hurtbox hb){
        hurtboxes.add(hb);
    }

    public Hurtbox addHurtbox(int x, int y, int w, int h){
        Hurtbox hb = new Hurtbox(x, y, w, h);
        hurtboxes.add(hb);
        return hb;
    }

    public void addHitbox(Hitbox hb){
        hitboxes.add(hb);
    }

    public DamageHitbox addDamageHitbox(Rectangle rect){
        DamageHitbox hb = new DamageHitbox(rect);
        hitboxes.add(hb);
        return hb;
    }

    public Vec2<FrameMovementAxis> getMovement(){
        return movement;
    }
}

