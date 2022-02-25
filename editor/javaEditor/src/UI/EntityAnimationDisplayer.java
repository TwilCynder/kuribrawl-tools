package UI;

import gamedata.EntityAnimation;
import java.awt.Graphics;

public class EntityAnimationDisplayer extends ZoomingDisplayer{
    private EntityAnimation current_anim;
    private int frame;

    public EntityAnimationDisplayer(EntityAnimation anim, int frame_){
        this.current_anim = anim;
        this.frame = frame_;
    }

    public void draw(Graphics g, int x, int y, int w, int h, double zoom){
        current_anim.draw(g, frame, x, y, w, h, zoom);
    }

    public int getFrameIndex(){
        return frame;
    }

    public void incrFrame(){
        if (frame < current_anim.getNbFrames() -1)
        frame++;
    }

    public void decrFrame(){
        if (frame > 0)
        frame--;
    }
}
