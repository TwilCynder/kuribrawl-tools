package KBUtil;

import java.awt.Point;

public class Rectangle extends Point implements Shape{
    public int w;
    public int h;
    public Rectangle() {};
    public Rectangle (int x_, int y_, int w_, int h_){
        x = x_; y = y_; w = w_; h = h_; //Using horrible formatting to piss off stackoverflow : Day 1
    }
    public Rectangle (Point2D p, Size2D s){
        x = p.x;
        y = p.y;
        w = s.w;
        h = s.h;
    }

    public Rectangle(Point p, int w_, int h_){
        x = p.x;
        y = p.y;
        w = w_;
        h = h_;
    }

    public boolean equals(Rectangle rect){
        return x == rect.x && y == rect.y && w == rect.w && h == rect.h;
    }

    public boolean equals(Object other){
        if (!(other instanceof Rectangle)) return false;

        return equals((Rectangle)other);
    }

    @Override
    public boolean isInside(int px, int py){
        return px >= x && px < x + w && py >= y && py < y + h;
    }

    public boolean isInside(Point p) {
        return isInside(p.x, p.y);
    }
}
