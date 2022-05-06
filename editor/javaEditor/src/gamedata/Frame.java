package gamedata;

import KBUtil.Rectangle;
import java.awt.Point;

public class Frame {
    private int duration = 0;
    private final Rectangle display;
    private Point origin;
    private Point default_origin;

    public Frame (int x, int y, int w, int h){
        this(new Rectangle(x, y, w, h));
    }

    public Frame (Rectangle display){
        this.display = display;
        default_origin = new Point(display.w / 2, display.h);
        origin = default_origin;
    }

    public int getDuration(){
        return duration;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public Rectangle getDisplay(){
        return display;
    }

    public Point getOrigin(){
        return origin;
    }

    public void setOrigin(Point p){
        origin = p;
    }

    public void setOriginX(int x){
        origin.x = x;
    }

    public void setOriginY(int y){
        origin.y = y;
    }

    public boolean hasDefaultOrigin(){
        return origin.equals(default_origin);
    }

    public boolean hasDefaultDuration(){
        return duration == 0 || duration == 1;
    }
}
