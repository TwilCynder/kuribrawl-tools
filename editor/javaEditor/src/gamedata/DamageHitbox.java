package gamedata;

import KBUtil.Rectangle;

public class DamageHitbox extends Hitbox {
    public double damage;
    public int angle;
    public double base_knockback;
    public double scaling_knockback;
    public int hitID = 0;
    public int priority = 0;

    public DamageHitbox(){};

    public DamageHitbox(Rectangle rect){
        super (rect);
    }
}
