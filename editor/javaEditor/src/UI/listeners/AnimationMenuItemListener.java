package UI.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import UI.AnimationMenuItem;
import gamedata.Animation;

public class AnimationMenuItemListener implements ActionListener {
    public void actionPerformed(ActionEvent e){
        Object src_ = e.getSource();
        if (src_ instanceof AnimationMenuItem){
            AnimationMenuItem src = (AnimationMenuItem)src_;
            Animation anim = src.getAnimation();
            src.getWindow().setDisplayedObject(anim);
        }
    }
}
