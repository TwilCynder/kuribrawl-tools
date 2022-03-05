package UI;

import java.awt.Point;

public interface Interactable extends Displayable{
    default public void onLeftClick(Point pos, Displayer displayer){}

    default public void onRightClick(Point pos, Displayer displayer){}

    default public void onPopupTrigger(Point pos, Displayer displayer){}

    default public void mouseDragged(Point currentpos, Point startpos, Displayer displayer){}
}