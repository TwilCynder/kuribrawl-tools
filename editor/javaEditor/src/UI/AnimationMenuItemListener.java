package UI;

import java.awt.event.ActionListener;

import gamedata.EntityAnimation;

import java.awt.event.ActionEvent;

public class AnimationMenuItemListener implements ActionListener {
    public void actionPerformed(ActionEvent e){
        Object src_ = e.getSource();
        if (src_ instanceof AnimationMenuItem){
            AnimationMenuItem src = (AnimationMenuItem)src_;
            EntityAnimation anim = src.getAnimation();
            src.getWindow().setDisplayedObject(anim);
        }
    }
}
