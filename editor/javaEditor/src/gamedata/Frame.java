package gamedata;

import KBUtil.Rectangle;
import java.awt.Point;

public class Frame {
    private int duration = 0;
    private final Rectangle display;
    private Point origin;

    public Frame (int x, int y, int w, int h){
        this(new Rectangle(x, y, w, h));
    }

    public Frame (Rectangle display){
        this.display = display;
        origin = new Point(display.w / 2, display.h);
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
}
