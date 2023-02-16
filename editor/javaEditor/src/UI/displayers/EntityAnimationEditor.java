package UI.displayers;

import gamedata.CollisionBox;
import gamedata.EntityAnimation;
import java.awt.Graphics;
import java.awt.Point;
import KBUtil.ui.display.Displayer;
import KBUtil.ui.display.InteractableDisplayable;
import UI.EntityAnimationEditorWindow;

import java.awt.event.KeyEvent;

public class EntityAnimationEditor extends EntityAnimationDisplayer implements InteractableDisplayable {
    EntityAnimationEditorBackend editor_backend;

    public EntityAnimationEditor(EntityAnimation anim, EntityAnimationEditorWindow win){
        super(anim);
        System.out.println("EAE constructed "+ anim);
        editor_backend = new EntityAnimationEditorBackend(win, this);
        onAnimationChanged();
    }

    @Override
    public void draw(Graphics g, int x, int y, int w, int h, double zoom) throws IllegalStateException{
        super.draw(g, x, y, w, h, zoom);
        editor_backend.draw(g, x, y, w, h, this);
    }

    public void setSelectedCBox(CollisionBox cbox){
        selected_cbox = cbox;
        onSelectedCBoxChanged();
    }

    @Override
    public void mousePressed(Point pos, Displayer displayer){
        editor_backend.mousePressed(pos, this, displayer);
    }

    @Override
    public void mouseDragged(Point currentpos, Displayer displayer){
        editor_backend.mouseDragged(currentpos, this, displayer);
    }

    @Override
    public void mouseReleased(Point pos, Displayer displayer){
        editor_backend.mouseReleased(pos, this, displayer);
    }

    @Override
    public void onLeftClick(Point p, Displayer d) throws IllegalStateException{
        CollisionBox cbox = getCboxAt(getAnimPosition(p));
        if (cbox != selected_cbox) {
            setSelectedCBox(cbox);
            d.update();
        }
    }

    @Override
    public void onRightClick(Point p, Displayer d){
        System.out.println("Right click !");
    };

    @Override
    public void onPopupTrigger(Point p, Displayer d){
        editor_backend.onPopupTrigger(this, p, d);
    }

    @Override
    public void onKeyPressed(KeyEvent ev, Displayer d){
       editor_backend.onKeyPressed(this, ev, d);
    }

    public EntityAnimationEditorWindow getWindow(){
        return editor_backend.getWindow();
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

    private void onAnimationChanged(){
        editor_backend.onAnimationChanged(this);
    }

    private void onFrameChanged(){
        editor_backend.onFrameChanged(this);
    }

    private void onSelectedCBoxChanged() {
        editor_backend.onSelectedCBoxChanged(this);
    }

    public void moveOriginX(int x){
        editor_backend.moveOriginX(x, this);
    }

    public void moveOriginY(int y){
        editor_backend.moveOriginY(y, this);
    }

    public EntityAnimationEditorBackend getBackend(){
        return editor_backend;
    };
}
