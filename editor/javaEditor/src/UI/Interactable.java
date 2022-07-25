package UI;

import java.awt.Point;
import java.awt.event.KeyEvent;

//TODO passer au système setDisplayer

public interface Interactable extends Displayable{

    /**
     * Called by a Displayer owning this Interactable when the user left-clicked on it
     * @param pos position relative to the up-left corner of the canvas
     * @param displayer the Displayer
     */
    default public void onLeftClick(Point pos, Displayer displayer){}

    /**
     * Called by a Displayer owning this Interactable when the user right-clicked on it
     * @param pos position relative to the up-left corner of the canvas
     * @param displayer the Displayer
     */
    default public void onRightClick(Point pos, Displayer displayer){}

    /**
     * Called by a Displayer owning this Interactable when the user performed an action that should bring up a popup-menu (generally right-click)
     * @param pos position relative to the up-left corner of the canvas
     * @param displayer the Displayer
     */
    default public void onPopupTrigger(Point pos, Displayer displayer){}

    /**
     * Called by a Displayer owning this Interactable when the user dragged the mouse (moved it with left click down) on it
     * @param pos position relative to the up-left corner of the canvas
     * @param displayer the Displayer
     */
    default public void mouseDragged(Point currentpos, Displayer displayer){}

    /**
     * Called by a Displayer owning this Interactable when the user pressed the left mouse button on it
     * @param pos position relative to the up-left corner of the canvas
     * @param displayer the Displayer
     */
    default public void mousePressed(Point pos, Displayer displayer){}

    /**
     * Called by a Displayer owning this Interactable when the user released the left mouse button on it
     * @param pos position relative to the up-left corner of the canvas
     * @param displayer the Displayer
     */
    default public void mouseReleased(Point pos, Displayer displayer){}

    /**
     * Called by a Displayer owning this Interactable lost keyboard focus
     * @param displayer the Displayer
     */
    default public void onLostFocus(Displayer displayer){}

    /**
     * Called by a Displayer owning this Interactable when the user pressed a keyboard key while it has keyboard focus
     * @param displayer the Displayer
     */
    default public void onKeyPressed(KeyEvent e, Displayer displayer){}
}