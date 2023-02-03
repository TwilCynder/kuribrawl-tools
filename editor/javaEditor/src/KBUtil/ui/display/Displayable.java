package KBUtil.ui.display;

import java.awt.Graphics;

public interface Displayable {
    default public void draw(Graphics g, int x, int y, int w, int h, double zoom){
        draw(g, x, y, w, h);
    }
    public void draw(Graphics g, int x, int y, int w, int h);
}
