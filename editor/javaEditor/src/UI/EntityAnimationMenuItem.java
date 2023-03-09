package UI;

import java.awt.event.ActionListener;

import UI.listeners.EntityAnimationMenuItemListener;
import gamedata.EntityAnimation;


public abstract class EntityAnimationMenuItem extends AnimationMenuItem{
    private static final EntityAnimationMenuItemListener listener = new EntityAnimationMenuItemListener(); // take this java developers

    @Override
    protected ActionListener getListener(){
        return listener;
    }

    public EntityAnimationMenuItem(EntityAnimation anim, Window win)throws NullPointerException{
        super (anim, win);
    }

    abstract public EntityAnimation getAnimation();

    public static EntityAnimationMenuItem create(EntityAnimation anim, Window win){
        return new ConcreteEntityAnimationMenuItem(anim, win);
    }
}