package UI;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import UI.listeners.AnimationMenuItemListener;
import gamedata.Animation;

public abstract class AnimationMenuItem extends JMenuItem {
    protected Window window;

    private static final AnimationMenuItemListener listener = new AnimationMenuItemListener(); // take this java developers

    abstract public Animation getAnimation();

    protected ActionListener getListener(){
        return listener;
    }

    public AnimationMenuItem(Animation anim, Window win)throws NullPointerException{
        super (anim.getName());
        this.window = win;

        if (win == null){
            throw new NullPointerException("AnimationMenuItem : window (second parameter) should not be null");
        }

        addActionListener(getListener());
    }

    public Window getWindow(){
        return window;
    }

    public static AnimationMenuItem create(Animation anim, Window win){
        return new ConcreteAnimationMenuItem(anim, win);
    }

}
