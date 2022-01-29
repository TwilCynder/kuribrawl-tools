package gamedata;

import KBUtil.Rectangle;

enum HurtboxType {
    NORMAL,
    PROTECTED,
    INVINCIBLE,
    INTANGIBLE
}

public class Hurtbox extends Rectangle{
    public HurtboxType type;
    public Hurtbox(){
        super();
    }
    public Hurtbox(int x, int y, int w, int h){
        super(x, y, w, h);
    }
}
