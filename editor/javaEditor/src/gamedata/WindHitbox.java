package gamedata;

import KBUtil.Rectangle;

public class WindHitbox extends Hitbox{
    double direction_x;
    double direction_y;
    double strength;

    public WindHitbox(Rectangle rect){
        super(rect);
    }

    public char getTypeCode(){
        return WIND_HITBOX_CODE;
    }

    public String stringifyTypeSpecificInfo(){
        return "";
    }
}
