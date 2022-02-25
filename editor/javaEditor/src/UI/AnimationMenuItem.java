package UI;

import javax.swing.JMenuItem;
import gamedata.EntityAnimation;


public class AnimationMenuItem extends JMenuItem{
    private EntityAnimation anim;
    private Window window;

    private static final AnimationMenuItemListener listener = new AnimationMenuItemListener(); // take this java developers

    public AnimationMenuItem(EntityAnimation anim, Window win)throws NullPointerException{
        super (anim.getName());
        this.anim = anim;
        this.window = win;

        if (win == null){
            throw new NullPointerException("AnimationMenuItem : window (second parameter) should not be null");
        }

        addActionListener(listener);
    }

    public EntityAnimation getAnimation(){
        return anim;
    }

    public Window getWindow(){
        return window;
    }
}
