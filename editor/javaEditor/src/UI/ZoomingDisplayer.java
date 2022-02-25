package UI;

import java.awt.Graphics;

public abstract class ZoomingDisplayer implements Displayable{
    private double currentZoom = 1;

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
