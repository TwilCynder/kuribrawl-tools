package gamedata;

import KBUtil.Rectangle;
import KBUtil.Size2D;

import java.awt.Point;

public abstract class CollisionBox extends Rectangle {
    public CollisionBox(){
        super();
    }
    public CollisionBox(int x, int y, int w, int h) {
        super(x, y, w, h);
    }
        
    public boolean isDefault(Point origin, Size2D frame_size){
        return x == -origin.x && y == origin.y && w == frame_size.w && h == frame_size.h;
    }

    /**
     * Returns whether a point is inside the box
     * @param p anim position, relative to the frame
     * @param origin the origin point of the frame
     */
    public boolean isInside(Point p, Point origin){
        return p.x >= origin.x + x && p.x < origin.x + x + w && p.y >= origin.y - y && p.y < origin.y - y + h;
    }


    @Override
    /**
     * Returns whether a point is inside the box
     * @param p orogin position, relative to the origin (upwards y)
     */
    public boolean isInside(Point p){
        return p.x >= x && p.x < x + w && p.y <= y && p.y > y - h;
    }

    /**
     * Returns the descriptor line of this Collision Box
     * @param writeIndex whether the given (frame) index should be written in the line
     * @param index the index to write if writeIndex is true
     */
    public abstract String generateDescriptor(boolean writeIndex, int index);

    //this class inherits equals(Rectangle), so a cbox can be compared to a recangle    
}
