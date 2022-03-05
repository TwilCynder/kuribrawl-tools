package KBUtil;

import java.awt.Point;

public class Rectangle extends Point {
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
}
