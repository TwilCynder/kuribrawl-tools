package UI.displayers;

import gamedata.CollisionBox;
import gamedata.EntityAnimation;
import java.awt.Graphics;
import java.awt.Point;
import KBUtil.ui.display.Displayer;
import UI.EntityAnimationEditorWindow;

import java.awt.event.KeyEvent;

public class EntityAnimationEditor extends EntityAnimationDisplayer implements EditorFrontend {
    AbstractEntityAnimationEditorBackend editor_backend;

    public EntityAnimationEditor(EntityAnimation anim, EntityAnimationEditorWindow win){
        super(anim);
        System.out.println("EAE constructed "+ anim);
        editor_backend = new EntityAnimationEditorBackend(this, win);
    }

    @Override
    public void draw(Graphics g, int x, int y, int w, int h, double zoom) throws IllegalStateException{
        super.draw(g, x, y, w, h, zoom);
        editor_backend.draw(g, x, y, w, h);
    }

    @Override
    public void setSelectedCBox(CollisionBox cbox){
        super.setSelectedCBox(cbox);
        onSelectedCBoxChanged();
    }

    @Override
    public void mousePressed(Point pos, Displayer displayer){
        editor_backend.mousePressed(pos, displayer);
    }

    @Override
    public void mouseDragged(Point currentpos, Displayer displayer){
        editor_backend.mouseDragged(currentpos, displayer);
    }

    @Override
    public void mouseReleased(Point pos, Displayer displayer){
        editor_backend.mouseReleased(pos, displayer);
    }

    @Override
    public void onLeftClick(Point p, Displayer d) throws IllegalStateException{
        CollisionBox cbox = getCboxAt(getAnimPosition(p));
        System.out.println(p);
        System.out.println(getAnimPosition(p));
        System.out.println(cbox);
        if (cbox == null){
            resetSelectedCBox();
        } else if (cbox != selected_cbox) {
            setSelectedCBox(cbox);
            d.update();
        }
        editor_backend.onLeftClick(p, d);
    }

    @Override
    public void onRightClick(Point p, Displayer d){
        System.out.println("Right click !");
        editor_backend.onRightClick(p, d);
    };

    @Override
    public void onPopupTrigger(Point p, Displayer d){
        editor_backend.onPopupTrigger(p, d);
    }

    @Override
    public void onKeyPressed(KeyEvent ev, Displayer d){
    System.out.println();
       editor_backend.onKeyPressed(ev, d);
    }

    public EntityAnimationEditorWindow getWindow(){
        return editor_backend.getEditorWindow();
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
        editor_backend.onAnimationChanged();
    }

    private void onFrameChanged(){
        editor_backend.onFrameChanged();
    }

    private void onSelectedCBoxChanged() {
        editor_backend.onSelectedCBoxChanged();
    }

    public void moveOriginX(int x){
        editor_backend.moveOriginX(x);
    }

    public void moveOriginY(int y){
        editor_backend.moveOriginY(y);
    }

    @Override
    public AbstractEntityAnimationEditorBackend getBackend(){
        return editor_backend;
    };

}
