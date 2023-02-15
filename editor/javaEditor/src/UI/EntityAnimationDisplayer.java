package UI;

import gamedata.Animation;
import gamedata.CollisionBox;
import gamedata.DamageHitbox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.WindHitbox;
import gamedata.exceptions.FrameOutOfBoundsException;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;


public class EntityAnimationDisplayer extends AnimationDisplayer {
    protected EntityAnimation current_anim = null;
    private EntityFrame currentEntityFrame = null;

    protected CollisionBox selected_cbox = null;

    public EntityAnimationDisplayer(EntityAnimation anim, int frame_){
        super(anim, frame_);
        this.current_anim = anim;
        updateCurrentEntityFrame();
    }

    public EntityAnimationDisplayer(EntityAnimation anim){
        this (anim, 0);
    }

    /**
     * SHOULD NEVER RETURN FALSE but if it ever happens any method call will result in a NullPointer or sum
     * @return whether it is safe to use this EAD
     */
    public boolean isValid(){
        return current_anim != null && super.isValid();
    }

    private static final Color hitbox_color = new Color(255, 0, 0, 255);
    private static final Color hurtbox_color = new Color(0, 255, 0, 255);
    private static final Color selected_color = new Color(255, 0, 255);

    /**
     * Returns the current entity frame of the current animation
     * @return an EntityFrame, never null
     * @throws IllegalStateException if the current frame index is out of the frame array bounds ; this should never be the case as the index is checked to avoid that
     */
    public EntityFrame getCurrentEntityFrame() {
        return currentEntityFrame;
    }

    public void draw(Graphics g, int x, int y, int w, int h, double zoom) throws IllegalStateException{
        super.draw(g, x, y, w, h, zoom);
        
        Point origin = getCurrentDisplayOrigin();
        g.setColor(hurtbox_color);

        if (currentEntityFrame == null) throw new IllegalStateException("No current entity frame (null)");

        System.out.println("Drawing " + current_anim.getName() + " " + currentFrameIndex);
        System.out.println(currentEntityFrame.hurtboxes.size());

        for (Hurtbox hb : currentEntityFrame.hurtboxes){
            if (hb == selected_cbox){
                g.setColor(selected_color);
                g.drawRect(origin.x + (int)(hb.x * zoom), origin.y - (int)(hb.y * zoom), (int)(hb.w * zoom), (int)(hb.h * zoom));
                g.setColor(hurtbox_color);
            } else {
                g.drawRect(origin.x + (int)(hb.x * zoom), origin.y - (int)(hb.y * zoom), (int)(hb.w * zoom), (int)(hb.h * zoom));
            }
        }
        g.setColor(hitbox_color);
        System.out.println(currentEntityFrame.hitboxes.size());
        for (Hitbox hb : currentEntityFrame.hitboxes){
            if (hb == selected_cbox){
                g.setColor(selected_color);
                g.drawRect(origin.x + (int)(hb.x * zoom), origin.y - (int)(hb.y * zoom), (int)(hb.w * zoom), (int)(hb.h * zoom));
                g.setColor(hitbox_color); 
            } else {
                g.drawRect(origin.x + (int)(hb.x * zoom), origin.y - (int)(hb.y * zoom), (int)(hb.w * zoom), (int)(hb.h * zoom));
            }            
        }
    }

    protected CollisionBox getCboxAt(Point animpos) throws IllegalStateException{
        CollisionBox selected = null; 
        Frame frame = getCurrentFrame();
        EntityFrame entity_frame = getCurrentEntityFrame();
        Point origin = frame.getOrigin();

        for (Hitbox h : entity_frame.hitboxes){
            if (h == selected_cbox) {
                selected = h;
                continue;
            }
            if (h.isInside(animpos, origin)){
                return h;
            }
        }

        for (Hurtbox h : entity_frame.hurtboxes){
            if (h == selected_cbox) {
                selected = h;
                continue;
            }
            if (h.isInside(animpos, origin)){
                return h;
            }
        }

        return selected;
    

    }

    public CollisionBox getSelectedCBox(){
        return selected_cbox;
    }

    protected CollisionBox resetSelectedCBox(){
        CollisionBox cbox = selected_cbox;
        selected_cbox = null;
        return cbox;
    }

    public Hurtbox getSelectedHurtbox() throws IllegalStateException{
        if (selected_cbox == null || !(selected_cbox instanceof Hurtbox)) throw new IllegalStateException("Selected Cbox is " + selected_cbox + "(should be a Hurtbox)");
        return (Hurtbox)selected_cbox;
    }

    public Hitbox getSelectedHitbox() throws IllegalStateException{
        if (selected_cbox == null || !(selected_cbox instanceof Hitbox)) throw new IllegalStateException("Selected Cbox is " + selected_cbox + "(should be a Hitbox)");
        return (Hitbox)selected_cbox;
    }

    public DamageHitbox getSelectedDamageHitbox() throws IllegalStateException{
        if (selected_cbox == null || !(selected_cbox instanceof Hitbox)) throw new IllegalStateException("Selected Cbox is " + selected_cbox + "(should be a DamageHitbox)");
        return (DamageHitbox)selected_cbox;
    }

    public WindHitbox getSelectedWindHitbox() throws IllegalStateException{
        if (selected_cbox == null || !(selected_cbox instanceof Hitbox)) throw new IllegalStateException("Selected Cbox is " + selected_cbox + "(should be a Windbox)");
        return (WindHitbox)selected_cbox;
    }

    protected void updateCurrentEntityFrame() throws IllegalStateException {
        try {
            currentEntityFrame = current_anim.getEntityFrame(currentFrameIndex);
        } catch (FrameOutOfBoundsException ex){
            throw new IllegalStateException("Current frame of EADisplayer was out of bounds", ex);
        }
    }

    @Override
    public EntityAnimation getAnimation(){
        return current_anim;
    }

    public void setAnimation(EntityAnimation anim){
        current_anim = anim;
        super.setAnimation(anim);
    }

    @Override
    public void setAnimation(Animation anim) throws IllegalArgumentException{
        System.out.println("Set animation : " + anim.getClass());
        throw new IllegalArgumentException("Attempt to set the current animation of an Entity Animation Displayer as an  Animation");
    }

    @Override
    protected void setFrameIndex(int index){
        super.setFrameIndex(index);
        updateCurrentEntityFrame();
    } 

    @Override 
    public void incrFrame(){
        super.incrFrame();
        updateCurrentEntityFrame();
    }

    @Override 
    public void decrFrame(){
        super.decrFrame();
        updateCurrentEntityFrame();
    }
}
