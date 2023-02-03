package UI;

import java.awt.Graphics;

import KBUtil.ui.display.Displayable;

public abstract class ZoomingDisplayer implements Displayable{
    protected double currentZoom = 1;
    
    public double getZoom(){
        return currentZoom;
    }

    public void setZoom(double zoom){
        currentZoom = zoom;
    }

    abstract public void draw(Graphics g, int x, int y, int w, int h, double zoom);

    public void draw(Graphics g, int x, int y, int w, int h){
        draw(g, x, y, w, h, currentZoom);
    }
}
