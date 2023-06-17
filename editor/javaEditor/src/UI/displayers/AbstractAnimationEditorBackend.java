package UI.displayers;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import KBUtil.ui.display.Displayer;
import UI.AnimationEditorWindow;
import gamedata.Animation;
import gamedata.Frame;

public abstract class AbstractAnimationEditorBackend {

    protected abstract AnimationEditorWindow getEditorWindow();

    protected abstract AnimationDisplayer getEditor();

    /**
     * Popup menu displayed inside an editor.
     */
    protected interface OriginMovingMenu {
        public void moveOrigin();
    }

    protected static class MoveOriginActionListener implements ActionListener {
        OriginMovingMenu menu;

        public MoveOriginActionListener(OriginMovingMenu menu) {
            this.menu = menu;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            menu.moveOrigin();
        }
    }

    protected static MoveOriginActionListener createMoveOriginActionListener(OriginMovingMenu menu){
        return new MoveOriginActionListener(menu);
    }

    protected static abstract class InternalMenu extends JPopupMenu {
        protected Point pos = new Point(0, 0);
        protected Displayer displayer;

        public void show(Displayer invoker, int x, int y) {
            displayer = invoker;
            JComponent component = invoker.getComponent();
            if (component == null) return;
            pos = new Point(x, y);
            super.show(invoker.getComponent(), x, y);
        }

        /**
         * Initializes the items in this menu.
         */
        public abstract void initItems();

        public InternalMenu(){
            initItems();
        }
    }

    /*
    private static abstract class AnimationPopupMenu extends InternalMenu {

        public void show(Displayer invoker, int x, int y) {
            super.show(invoker, x, y);
        }
    }
    */

    private class PopupMenu extends InternalMenu implements OriginMovingMenu {
        public void moveOrigin(){
            AbstractAnimationEditorBackend.this.moveOrigin(pos, displayer);
        }

        @Override
        public void initItems(){
            JMenuItem item;

            item = new JMenuItem("Move origin here");
            item.addActionListener(createMoveOriginActionListener(this));
            add(item);
        }
    }

    private PopupMenu popup_menu = new PopupMenu();

    protected AbstractAnimationEditorBackend(){
    }

    protected void onCreated(){
        onAnimationChanged();
    }

    protected void notifyDataModified(){
        getEditorWindow().notifyDataModified();
    }

    protected void notifyAndUpdate(Displayer d){
        notifyDataModified();
        d.update();
    }
    
    protected void moveOrigin(Point displaypoint, Displayer displayer){
        AnimationDisplayer editor = getEditor();
        Frame frame = editor.getCurrentFrame();
        frame.setOrigin(editor.getAnimPosition(displaypoint));
        notifyAndUpdate(displayer);
        updateFrameControls(frame, false);
    }

    /**
     * Changes the x coordinate of the origin of the current frame
     * @param x new coordinate
     * @param editor
     * @throws IllegalStateException
     */
    public void moveOriginX(int x) throws IllegalStateException {
        Frame f = getEditor().getCurrentFrame();
        f.setOriginX(x);
    }

    /**
     * Changes the y coordinate of the origin of the current frame
     * @param y new coordinate
     * @param editor
     * @throws IllegalStateException
     */
    public void moveOriginY(int y) throws IllegalStateException {
        Frame f = getEditor().getCurrentFrame();
        f.setOriginY(y);
    }

    public void mousePressed(Point pos, Displayer displayer){
        
    }

    public void mouseDragged(Point pos, Displayer displayer){

    }

    public void mouseReleased(Point pos, Displayer displayer){

    }

    public void onLeftClick(Point p, Displayer d){

    }

    public void onRightClick(Point p, Displayer d){

    };

    public void onPopupTrigger(Point p, Displayer d){
        JComponent component = d.getComponent();
        if (component == null) return;

        popup_menu.show(d, p.x, p.y);

    }

    protected boolean handleKeyPress(KeyEvent ev, Displayer d, Frame frame){
        Point origin = frame.getOrigin();
        if (ev.getModifiersEx() == 0)
            switch (ev.getKeyCode()){
                case KeyEvent.VK_UP:
                    this.moveOriginY(origin.y - 1);
                    break;
                case KeyEvent.VK_DOWN:
                    this.moveOriginY(origin.y + 1);
                    break;
                case KeyEvent.VK_LEFT:
                    moveOriginX(origin.x - 1);
                    break;
                case KeyEvent.VK_RIGHT:
                    moveOriginX(origin.x + 1);
                    break;
                default:
                    return false;
            }
        
        notifyAndUpdate(d);
        updateFrameControls(frame, true);
        return true;
    }

    public void onKeyPressed(KeyEvent ev, Displayer d){
        Frame frame = getEditor().getCurrentFrame();
        handleKeyPress(ev, d, frame);
    }

    /**
     * Handles operations that must be run whenever the current animation of an editor is changed. 
     * @param editor
     */
    protected void onAnimationChanged(){
        AnimationDisplayer editor = getEditor();
        editor.setFrameIndex(0);
        updateAnimationControls(true, editor.current_animation);
        onFrameChanged();
    }

    /**
     * Updates the editor controls (in the UI) related to the animation.
     * @param ignoreModifications whether the change should NOT mark data as modified.  
     * @param current_anim
     */
    protected void updateAnimationControls(boolean ignoreModifications, Animation current_anim){
        getEditorWindow().updateAnimControls(current_anim, ignoreModifications);
    }

    /**
     * Handles operations that must be run whenever the current frame of an editor is changed
     * @param editor
     */
    protected void onFrameChanged(){
        updateFrameControls(getEditor().getCurrentFrame(), true);
    }

    /**
     * Updates the editor controls (in the UI) related to the current frame.
     * @param editor
     */
    protected void updateFrameControls(Frame frame){
        updateFrameControls(frame, false);
    }

    /**
     * Updates the editor controls (in the UI) related to the current frame.
     * @param editor
     * @param ignoreModifications whether the change should NOT mark data as modified.
     */
    protected void updateFrameControls(Frame frame, boolean ignoreModifications){
        getEditorWindow().updateFrameControls(frame, ignoreModifications);
    }

}
