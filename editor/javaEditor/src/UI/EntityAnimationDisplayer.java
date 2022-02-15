package UI;

import gamedata.EntityAnimation;
import java.awt.Graphics;

public class EntityAnimationDisplayer implements Displayable{
    private EntityAnimation current_anim;
    private int frame;

    public EntityAnimationDisplayer(EntityAnimation anim, int frame_){
        this.current_anim = anim;
        this.frame = frame_;
    }

    public void draw(Graphics g, int x, int y, int w, int h){
        current_anim.draw(g, frame, x, y, w, h);
    }
}
