package gamedata;

import KBUtil.Rectangle;

public abstract class Hitbox extends Rectangle{
    public Hitbox(){
        super();
    }
    public Hitbox(int x, int y, int w, int h){
        super(x, y, w, h);
    }
}
