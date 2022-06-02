package UI;

import gamedata.CollisionBox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.exceptions.FrameOutOfBoundsException;

import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

//import KBUtil.Size2D;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EntityAnimationEditor extends EntityAnimationDisplayer implements Interactable {
    private Window window;

    private class PopupMenu extends JPopupMenu {
        private Point pos = new Point(0, 0);
        Displayer displayer;
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

        public void show(Displayer invoker, int x, int y) {
            displayer = invoker;
            JComponent component = invoker.getComponent();
            if (component == null) return;
            pos = new Point(x, y);
            super.show(invoker.getComponent(), x, y);
        }
        
    };

    private PopupMenu popup_menu = new PopupMenu();

    public EntityAnimationEditor(EntityAnimation anim, Window win){
        super(anim);
        this.window = win;
        onAnimationChanged();
    }

    public void setSelectedCBox(CollisionBox cbox){
        selected_cbox = cbox;
        onSelectedCBoxChanged();
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

    private static void shiftElements(EntityFrame eFrame, Point diff){
        for (Hurtbox h : eFrame.hurtboxes){
            h.translate(diff.x, diff.y);
        }
        for (Hitbox h : eFrame.hitboxes){
            h.translate(diff.x, diff.y);
        }
    }

    private void shiftCurrentElements(Point diff){
        shiftElements(getCurrentEntityFrame(), diff);
    }

    public void moveOriginX(int x){
        Frame frame = getCurrentFrame();
        shiftCurrentElements(new Point(frame.getOrigin().x - x, 0));
        frame.setOriginX(x);
    }

    public void moveOriginY(int y){
        Frame frame = getCurrentFrame();
        shiftCurrentElements(new Point(0, y - frame.getOrigin().y));
        frame.setOriginY(y);
    }

    public void moveOrigin(Point p) throws IllegalStateException{
        //Size2D frame_size = current_anim.getFrameSize();
        //if (p.x >= 0 && p.x < frame_size.w && p.y >= 0 && p.y < frame_size.h){
        Frame frame = getCurrentFrame();
        Point old_origin = new Point(frame.getOrigin());
        Point diff = new Point(
            old_origin.x - p.x,
            p.y - old_origin.y
        );

        shiftElements(getCurrentEntityFrame(), diff); 
        frame.setOrigin(p);

        //}
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
