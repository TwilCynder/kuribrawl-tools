package UI;

import gamedata.CollisionBox;
import gamedata.DamageHitbox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.exceptions.FrameOutOfBoundsException;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import KBUtil.Rectangle;
import KBUtil.Size2D;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EntityAnimationEditor extends EntityAnimationDisplayer implements Interactable {
    private Window window;
    private Point drag_start_pos;
    private Rectangle selection;

    private static final Color selection_color = new Color(127, 127, 127, 255);

    private abstract class InternalMenu extends JPopupMenu {
        protected Point pos = new Point(0, 0);
        protected Displayer displayer;

        public void show(Displayer invoker, int x, int y) {
            displayer = invoker;
            JComponent component = invoker.getComponent();
            if (component == null) return;
            pos = new Point(x, y);
            super.show(invoker.getComponent(), x, y);
        }
    }

    private class PopupMenu extends InternalMenu {
        public PopupMenu() {
            JMenuItem item = new JMenuItem("Move origin here");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    moveOriginToDisplayPos(pos);
                    displayer.update();
                    updateFrameControls();
                }
            });
            add(item);
        }

        
    };

    private PopupMenu popup_menu = new PopupMenu();

    private class SelectionPupupMenu extends InternalMenu {
        public SelectionPupupMenu() {
            JMenuItem item = new JMenuItem("Create hurtbox");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Frame frame = getCurrentFrame();
                    EntityFrame entity_frame = getCurrentEntityFrame();
                    frame.relativeToOrigin(selection);
                    entity_frame.addHurtbox(selection.x, selection.y, selection.w, selection.h);
                    cancelSelection(displayer);
                }
            });
            add(item);

            item = new JMenuItem("Create hitbox");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Frame frame = getCurrentFrame();
                    EntityFrame entity_frame = getCurrentEntityFrame();
                    frame.relativeToOrigin(selection);
                    entity_frame.addHitbox(new DamageHitbox(selection));
                    cancelSelection(displayer);
                }
            });
            add(item);

            addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuCanceled(PopupMenuEvent e){
                    cancelSelection(displayer);
                }
                @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
                @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e){}
            });
        }
    };

    private SelectionPupupMenu selection_popup_menu = new SelectionPupupMenu();

    public EntityAnimationEditor(EntityAnimation anim, Window win){
        super(anim);
        this.window = win;
        onAnimationChanged();
    }

    @Override
    public void draw(Graphics g, int x, int y, int w, int h, double zoom) throws IllegalStateException{
        super.draw(g, x, y, w, h, zoom);
        if (selection != null){
            Rectangle display_selection = getDisplayRectangle(selection);
            g.setColor(selection_color);
            g.drawRect(display_selection.x, display_selection.y, display_selection.w, display_selection.h);
        }
    }

    public void setSelectedCBox(CollisionBox cbox){
        selected_cbox = cbox;
        onSelectedCBoxChanged();
    }

    private void cancelSelection(Displayer d){
        selection = null;
        d.update();
    }

    @Override
    public void mousePressed(Point pos, Displayer displayer){
        pos = getAnimPosition(pos);
        drag_start_pos = pos;
        selection = new Rectangle(pos.x, pos.y, 1, 1);
        displayer.update();
    }

    @Override
    public void mouseDragged(Point currentpos, Displayer displayer){
        currentpos = getAnimPosition(currentpos);
        int w = currentpos.x - drag_start_pos.x;
        if (w < 0){
            selection.x = currentpos.x;
            selection.w = -w;
        } else {
            selection.x = drag_start_pos.x;
            selection.w = w;
        }
        

        int h = currentpos.y - drag_start_pos.y;
        if (h < 0){
            selection.y = currentpos.y;
            selection.h = -h;
        } else {
            selection.y = drag_start_pos.y;
            selection.h = h;
        }

        displayer.update();
    }

    @Override
    public void mouseReleased(Point pos, Displayer displayer){
        if (selection != null){
            if (selection.w + selection.h < 10){
                cancelSelection(displayer);
            } else {
                selection_popup_menu.show(displayer, pos.x, pos.y);
            }        
        }
    }

    public void onLeftClick(Point p, Displayer d) throws IllegalStateException{
        setSelectedCBox(getCboxAt(getAnimPosition(p)));
        d.update();
    }

    public void onRightClick(Point p, Displayer d){
        System.out.println("Right click !");
    };

    public void onPopupTrigger(Point p, Displayer d){
        JComponent component = d.getComponent();
        if (component == null) return;
        popup_menu.show(d, p.x, p.y);
    }

    private void moveOrigin(Point display_point) throws IllegalStateException{
        try {
            Point animpoint = getAnimPosition(display_point);
            //Size2D frame_size = current_anim.getFrameSize();
            //if (animpoint.x >= 0 && animpoint.x < frame_size.w && animpoint.y >= 0 && animpoint.y < frame_size.h){
                EntityAnimation.moveOrigin(
                    current_anim.getFrame(currentFrame),
                    current_anim.getEntityFrame(currentFrame),
                    animpoint
                );           
            //}
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }   
    }

    public void moveOriginX(int x) throws IllegalStateException {
        try {
            EntityAnimation.moveOriginX(
                    current_anim.getFrame(currentFrame),
                    current_anim.getEntityFrame(currentFrame),
                    x
                ); 
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }   
    }

    public void moveOriginY(int y) throws IllegalStateException {
        try {
            EntityAnimation.moveOriginY(
                    current_anim.getFrame(currentFrame),
                    current_anim.getEntityFrame(currentFrame),
                    y
                ); 
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }   
    }

    private void moveOriginToDisplayPos(Point p){
        moveOrigin(getAnimPosition(p));
    }

    public Window getWindow(){
        return window;
    }

    private void onAnimationChanged(){
        updateAnimationControls();
        onFrameChanged();
    }

    private void updateAnimationControls(){
        window.updateAnimControls(current_anim);
    }

    private void onFrameChanged(){
        selected_cbox = null;
        updateFrameControls();
        onSelectedCBoxChanged();
    }

    private void updateFrameControls(){
        try {
            window.updateFrameControls(current_anim.getFrame(currentFrame), current_anim.getEntityFrame(currentFrame));
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }
    }

    private void onSelectedCBoxChanged(){
        window.updateElementControls(selected_cbox);
    }

    @Override
    public void incrFrame(){
        super.incrFrame();
        onFrameChanged();
    }

    @Override
    public void decrFrame(){
        super.decrFrame();
        onFrameChanged();
    }

    @Override
    public void setAnimation(EntityAnimation anim){
        super.setAnimation(anim);
        onAnimationChanged();
    }

}
