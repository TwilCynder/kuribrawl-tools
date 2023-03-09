package UI.displayers;

import java.awt.Point;
import java.awt.event.KeyEvent;

import KBUtil.ui.display.Displayer;
import UI.AnimationEditorWindow;
import gamedata.Animation;

public class AnimationEditor extends AnimationDisplayer implements EditorFrontend {
    AbstractAnimationEditorBackend editor_backend;

    public AnimationEditor(Animation anim, AnimationEditorWindow win){
        super(anim);
        System.out.println("EAE constructed "+ anim);
        editor_backend = new AnimationEditorBackend(this, win);
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
        editor_backend.onLeftClick(p, d);
    }

    @Override
    public void onRightClick(Point p, Displayer d){
        editor_backend.onRightClick(p, d);
    };

    @Override
    public void onPopupTrigger(Point p, Displayer d){
        editor_backend.onPopupTrigger(p, d);
    }

    @Override
    public void onKeyPressed(KeyEvent ev, Displayer d){
       editor_backend.onKeyPressed(ev, d);
    }

    public AnimationEditorWindow getWindow(){
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
    public void setAnimation(Animation anim){
        super.setAnimation(anim);
        onAnimationChanged();
    }

    private void onAnimationChanged(){
        editor_backend.onAnimationChanged();
    }

    private void onFrameChanged(){
        editor_backend.onFrameChanged();
    }

    @Override
    public AbstractAnimationEditorBackend getBackend() {
        return editor_backend;
    }
}
