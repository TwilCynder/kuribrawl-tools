package gamedata;

import KBUtil.Rectangle;

public abstract class Hitbox extends Rectangle{
    public Hitbox(){
        super();
    }
    public Hitbox(int x, int y, int w, int h){
        super(x, y, w, h);
    }

    public Hitbox(Rectangle rect){
        super(rect.x, rect.y, rect.w, rect.h);
    }

    public static final char DAMAGE_HITBOX_CODE = 0;
    public static final char GRAB_HITBOX_CODE = 1;
    public static final char WIND_HITBOX_CODE = 2;
    public static final char SPECIAL_HITBOX_CODE = 3;
}
