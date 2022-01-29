package gamedata;

import KBUtil.Rectangle;

public abstract class CollisionBox extends Rectangle{
    public CollisionBox(){
        super();
    }
    public CollisionBox(int x, int y, int w, int h) {
        super(x, y, w, h);
    }
        
}
