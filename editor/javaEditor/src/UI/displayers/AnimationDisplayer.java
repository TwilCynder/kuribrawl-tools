package UI.displayers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import KBUtil.Rectangle;
import KBUtil.Size2D;
import UI.ZoomingDisplayer;
import gamedata.Animation;
import gamedata.Frame;
import gamedata.exceptions.FrameOutOfBoundsException;

public class AnimationDisplayer extends ZoomingDisplayer{
    protected Animation current_animation = null;
    protected int currentFrameIndex = 0;

    protected Frame currentFrame = null;

    protected Rectangle last_display_area = null;

    public AnimationDisplayer(Animation anim, int frame_){
        this.current_animation = anim;
        this.currentFrameIndex = frame_;
        updateCurrentFrame();
    }

    public AnimationDisplayer(Animation anim){
        this (anim, 0);
    }

    protected void updateCurrentFrame() throws IllegalStateException {
        try {
            currentFrame = current_animation.getFrame(currentFrameIndex);
            //currentEntityFrame = current_anim.getEntityFrame(currentFrameIndex);
        } catch (FrameOutOfBoundsException ex){
            throw new IllegalStateException("Current frame of EADisplayer was out of bounds", ex);
        }
    }
        
    public int getFrameIndex(){
        return currentFrameIndex;
    }


    protected void setFrameIndex(int index){
        currentFrameIndex = index;
        updateCurrentFrame();
    }   

    public void incrFrame(){
        if (currentFrameIndex < current_animation.getNbFrames() -1)
            currentFrameIndex++;
        updateCurrentFrame();
    }

    public void decrFrame(){
        if (currentFrameIndex > 0)
            currentFrameIndex--;
        updateCurrentFrame();
    }

    /**
     * SHOULD NEVER RETURN FALSE but if it ever happens any method call will result in a NullPointer or sum
     * @return whether it is safe to use this EAD
     */
    public boolean isValid(){
        return current_animation != null;
    }

    protected Rectangle getActualDisplayArea(int x, int y, int totalW, int totalH, double zoom){
        Size2D source_size = current_animation.getFrameSize();
        int dw = (int)(source_size.w * zoom);
        int dh = (int)(source_size.h * zoom);
        return new Rectangle(
            x + (totalW / 2) - ((dw / 2)),
            y + (totalH / 2) - ((dw / 2)),
            dw,
            dh
        );
    }

    @SuppressWarnings("unused")
    private Rectangle getActualDisplayArea(int x, int y, int totalW, int totalH){
        return getActualDisplayArea(x, y, totalW, totalH, currentZoom);
    }

    /**
     * Returns the current frame of the current animation
     * @return a Frame, never null
     * @throws IllegalStateException if the current frame index is out of the frame array bounds ; this should never be the case as the index is checked to avoid that
     */
    public Frame getCurrentFrame() throws IllegalStateException {
        //TODO make it actually throw an exception (if current_frame is null ?)
        return currentFrame; 
    }

    protected Point getDisplayPosition(Point animpos, double zoom){
        if (last_display_area == null) return new Point(0, 0);
        return new Point(
            last_display_area.x + (int)(animpos.x * zoom),
            last_display_area.y + (int)(animpos.y * zoom)
        );
    }

    protected Point getDisplayPosition(Point pos){
        return getDisplayPosition(pos, currentZoom);
    }

    private static final Color origin_color = new Color(0, 0, 255, 255);

    protected Point getDisplayOrigin(Frame f){
        return getDisplayPosition(f.getOrigin());
    }

    protected Point getCurrentDisplayOrigin(){
        return getDisplayOrigin(currentFrame);
    }

    public void draw(Graphics g, int x, int y, int w, int h, double zoom) throws IllegalStateException {
        try {
            last_display_area = getActualDisplayArea(x, y, w, h, zoom);
            int dw = last_display_area.w;
            int dh = last_display_area.h;
            current_animation.draw(g, currentFrameIndex, last_display_area.x, last_display_area.y, dw, dh);

            Frame frame = getCurrentFrame();
            Point origin = getDisplayOrigin(frame);

            g.setColor(origin_color);
            g.drawLine(0, origin.y, x + w, origin.y);
            g.drawLine(origin.x, 0, origin.x, 0 + h);
        } catch (FrameOutOfBoundsException ex){
            throw new IllegalStateException(ex);
        }
    }

    protected Rectangle getDisplayRectangle(Rectangle animrect, double zoom){
        if (last_display_area == null) return animrect;
        return new Rectangle(
            last_display_area.x + (int)(animrect.x * zoom),
            last_display_area.y + (int)(animrect.y * zoom),
            (int)(animrect.w * zoom),
            (int)(animrect.h * zoom)
        );
    }

    protected Point getAnimPosition(Point displaypos, double zoom){
        if (last_display_area == null) return new Point(0, 0);
        return new Point(
            (int)((displaypos.x - last_display_area.x) / zoom),
            (int)((displaypos.y - last_display_area.y) / zoom)
        );
    }

    protected Rectangle getDisplayRectangle(Rectangle rect){
        return getDisplayRectangle(rect, currentZoom);
    }

    protected Point getAnimPosition(Point displaypos){
        return getAnimPosition(displaypos, currentZoom);
    }

    public Animation getAnimation(){
        return current_animation;
    }

    public void setAnimation(Animation anim){
        current_animation = anim;
        setFrameIndex(0);
    }
}
