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

    private static abstract class AnimationPopupMenu extends InternalMenu {
        protected AnimationDisplayer editor;

        public void show(AnimationDisplayer editor_, Displayer invoker, int x, int y) {
            super.show(invoker, x, y);
            editor = editor_;
        }
    }

    private class PopupMenu extends AnimationPopupMenu implements OriginMovingMenu {
        public void moveOrigin(){
            AbstractAnimationEditorBackend.this.moveOrigin(pos, editor, displayer);
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

    protected AbstractAnimationEditorBackend(AnimationEditor editor){

    }

    protected void onCreated(AnimationEditor editor){
        onAnimationChanged(editor);
    }
    
    protected void moveOrigin(Point displaypoint, AnimationDisplayer editor, Displayer displayer){
        Frame frame = editor.getCurrentFrame();
        frame.setOrigin(editor.getAnimPosition(displaypoint));
        displayer.update();
        getEditorWindow().notifyDataModified();
    }

    /**
     * Changes the x coordinate of the origin of the current frame
     * @param x new coordinate
     * @param editor
     * @throws IllegalStateException
     */
    public void moveOriginX(int x, AnimationDisplayer editor) throws IllegalStateException {
        Frame f = editor.getCurrentFrame();
        f.setOriginX(x);
    }

    /**
     * Changes the y coordinate of the origin of the current frame
     * @param y new coordinate
     * @param editor
     * @throws IllegalStateException
     */
    public void moveOriginY(int y, AnimationDisplayer editor) throws IllegalStateException {
        Frame f = editor.getCurrentFrame();
        f.setOriginX(y);
    }

    public void mousePressed(Point pos, AnimationDisplayer editor, Displayer displayer){
        
    }

    public void mouseDragged(Point pos, AnimationDisplayer editor, Displayer displayer){

    }

    public void mouseReleased(Point pos, AnimationDisplayer editor, Displayer displayer){

    }

    public void onLeftClick(Point p, Displayer d){

    }

    public void onRightClick(Point p, Displayer d){

    };

    public void onPopupTrigger(AnimationDisplayer editor, Point p, Displayer d){
        JComponent component = d.getComponent();
        if (component == null) return;

        popup_menu.show(editor, d, p.x, p.y);

    }

    public void onKeyPressed(AnimationDisplayer editor, KeyEvent ev, Displayer d){
        switch (ev.getKeyCode()){
            
        }
    }

    /**
     * Handles operations that must be run whenever the current animation of an editor is changed. 
     * @param editor
     */
    protected void onAnimationChanged(AnimationDisplayer editor){
        editor.setFrameIndex(0);
        updateAnimationControls(true, editor.current_animation);
        onFrameChanged(editor);
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
    protected void onFrameChanged(AnimationDisplayer editor){
        updateFrameControls(editor.getCurrentFrame(), true);
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
