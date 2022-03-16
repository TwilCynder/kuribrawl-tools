package gamedata;

import KBUtil.Rectangle;
import KBUtil.Size2D;

import java.awt.Point;

public abstract class CollisionBox extends Rectangle{
    public CollisionBox(){
        super();
    }
    public CollisionBox(int x, int y, int w, int h) {
        super(x, y, w, h);
    }
        
    public boolean isDefault(Point origin, Size2D frame_size){
        return x == -origin.x && y == origin.y && w == frame_size.w && h == frame_size.h;
    }
    
    //this class inherits equals(Rectangle), so a cbox can be compared to a recangle    
}
