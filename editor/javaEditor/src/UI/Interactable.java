package UI;

import java.awt.Point;

//TODO passer au système setDisplayer

public interface Interactable extends Displayable{
    default public void onLeftClick(Point pos, Displayer displayer){}

    default public void onRightClick(Point pos, Displayer displayer){}

    default public void onPopupTrigger(Point pos, Displayer displayer){}

    default public void mouseDragged(Point currentpos, Displayer displayer){}

    default public void mousePressed(Point pos, Displayer displayer){}

    default public void mouseReleased(Point pos, Displayer displayer){}

    default public void onLostFocus(Displayer displayer){}
}