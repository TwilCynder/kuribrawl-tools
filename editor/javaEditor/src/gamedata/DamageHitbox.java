package gamedata;

import KBUtil.Rectangle;

public class DamageHitbox extends Hitbox {
    public double damage = 0;
    public int angle = 0;
    public double base_knockback = 0;
    public double scaling_knockback = 0;
    public int hitID = 0;
    public int priority = 0;
    public AngleMode angle_mode = AngleMode.NORMAL;

    public DamageHitbox(){};

    public DamageHitbox(Rectangle rect){
        super (rect);
    }

    public char getTypeCode(){
        return DAMAGE_HITBOX_CODE;
    }

    public String stringifyTypeSpecificInfo(){
        return damage + " " + angle + " " + base_knockback + " " + scaling_knockback + " " + hitID + " " + priority;
    }
}
