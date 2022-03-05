package UI;

import gamedata.CollisionBox;
import gamedata.EntityAnimation;
import gamedata.Frame;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.exceptions.FrameOutOfBoundsException;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;

import KBUtil.Rectangle;
import KBUtil.Size2D;

public class EntityAnimationDisplayer extends ZoomingDisplayer{
    protected EntityAnimation current_anim = null;
    protected int currentFrame = 0;
    protected CollisionBox selected_cbox = null;

    protected Rectangle last_display_area = null;

    public EntityAnimationDisplayer(EntityAnimation anim, int frame_){
        this.current_anim = anim;
        this.currentFrame = frame_;
    }

    public EntityAnimationDisplayer(EntityAnimation anim){
        this (anim, 0);
    }

    private Rectangle getActualDisplayArea(int x, int y, int totalW, int totalH, double zoom){
        Size2D source_size = current_anim.getFrameSize();
        int dw = (int)(source_size.w * zoom);
        int dh = (int)(source_size.h * zoom);
        return new Rectangle(
            x + (totalW / 2) - ((dw / 2)),
            y + (totalH / 2) - ((dw / 2)),
            dw,
            dh
        );
    }

    private Rectangle getActualDisplayArea(int x, int y, int totalW, int totalH){
        return getActualDisplayArea(x, y, totalW, totalH, currentZoom);
    }

    private static final Color origin_color = new Color(0, 0, 255, 255);
    private static final Color hitbox_color = new Color(255, 0, 0, 255);
    private static final Color hurtbox_color = new Color(0, 255, 0, 255);

    public void draw(Graphics g, int x, int y, int w, int h, double zoom) throws IllegalStateException{
        try {
            last_display_area = getActualDisplayArea(x, y, w, h, zoom);
            int dw = last_display_area.w;
            int dh = last_display_area.h;
            current_anim.draw(g, currentFrame, last_display_area.x, last_display_area.y, dw, dh);

            Frame frame = current_anim.getFrame(currentFrame);
            Point origin = frame.getOrigin();
            origin = getDisplayPosition(origin);

            g.setColor(origin_color);
            g.drawLine(0, origin.y, x + w, origin.y);
            g.drawLine(origin.x, 0, origin.x, 0 + h);
            g.setColor(hurtbox_color);
            for (Hurtbox hb : current_anim.getHurtboxes(currentFrame)){
                g.drawRect(origin.x + (int)(hb.x * zoom), origin.y - (int)(hb.y * zoom), (int)(hb.w * zoom), (int)(hb.h * zoom));
            }
            g.setColor(hitbox_color);
            for (Hitbox hb : current_anim.getHitboxes(currentFrame)){
                g.drawRect(origin.x + (int)(hb.x * zoom), origin.y - (int)(hb.y * zoom), (int)(hb.w * zoom), (int)(hb.h * zoom));
            }
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }
    }

    protected Point getDisplayPosition(Point animpos, double zoom){
        if (last_display_area == null) return new Point(0, 0);
        return new Point(
            last_display_area.x + (int)(animpos.x * zoom),
            last_display_area.y + (int)(animpos.y * zoom)
        );
    }

    protected Point getAnimPosition(Point displaypos, double zoom){
        if (last_display_area == null) return new Point(0, 0);
        return new Point(
            (int)((displaypos.x - last_display_area.x) / zoom),
            (int)((displaypos.y - last_display_area.y) / zoom)
        );
    }

    protected Point getDisplayPosition(Point pos){
        return getDisplayPosition(pos, currentZoom);
    }

    protected Point getAnimPosition(Point displaypos){
        return getAnimPosition(displaypos, currentZoom);
    }

    public int getFrameIndex(){
        return currentFrame;
    }

    public void incrFrame(){
        if (currentFrame < current_anim.getNbFrames() -1)
        currentFrame++;
    }

    public void decrFrame(){
        if (currentFrame > 0)
        currentFrame--;
    }
}
