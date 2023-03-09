package UI;

import UI.listeners.EntityAnimationMenuItemListener;
import gamedata.EntityAnimation;


public abstract class EntityAnimationMenuItem extends AnimationMenuItem{
    private static final EntityAnimationMenuItemListener listener = new EntityAnimationMenuItemListener(); // take this java developers

    public EntityAnimationMenuItem(EntityAnimation anim, Window win)throws NullPointerException{
        super (anim, win);

        addActionListener(listener);
    }

    abstract public EntityAnimation getAnimation();

    public static EntityAnimationMenuItem create(EntityAnimation anim, Window win){
        return new ConcreteEntityAnimationMenuItem(anim, win);
    }
}