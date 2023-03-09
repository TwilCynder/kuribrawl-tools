package UI.listeners;

import java.awt.event.ActionListener;

import UI.EntityAnimationMenuItem;
import gamedata.EntityAnimation;

import java.awt.event.ActionEvent;

public class EntityAnimationMenuItemListener implements ActionListener {
    public void actionPerformed(ActionEvent e){
        Object src_ = e.getSource();
        if (src_ instanceof EntityAnimationMenuItem){
            EntityAnimationMenuItem src = (EntityAnimationMenuItem)src_;
            EntityAnimation anim = src.getAnimation();
            src.getWindow().setDisplayedObject(anim);
        }
    }
}
