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

public abstract class AbstractAnimationEditorBackend {

    protected abstract AnimationEditorWindow getEditorWindow();
        /**
     * Popup menu displayed inside an editor.
     */
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
        protected AnimationEditor editor;

        public void show(AnimationEditor editor_, Displayer invoker, int x, int y) {
            super.show(invoker, x, y);
            editor = editor_;
        }
    }

    private static abstract class PopupMenu extends AnimationPopupMenu {
        @Override
        public void initItems(){
            JMenuItem item;

            item = new JMenuItem("Move origin here");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    //moveOrigin(pos, editor, displayer);
                }
            });
            add(item);
        }
    }

    protected AbstractAnimationEditorBackend(){

    }

    public AbstractAnimationEditorBackend(AnimationEditor editor){
        onAnimationChanged(editor);
    }
    

    public void mousePressed(Point pos, AnimationEditor editor, Displayer displayer){
        
    }

    public void mouseDragged(Point pos, AnimationEditor editor, Displayer displayer){

    }

    public void mouseReleased(Point pos, AnimationEditor editor, Displayer displayer){

    }

    public void onLeftClick(Point p, Displayer d){

    }

    public void onRightClick(Point p, Displayer d){

    };

    public void onPopupTrigger(AnimationEditor editor, Point p, Displayer d){

    }

    public void onKeyPressed(AnimationEditor editor, KeyEvent ev, Displayer d){
        
    }

    protected void onAnimationChanged(AnimationEditor editor){

    }

    protected void updateAnimationControls(AnimationEditor editor){

    }

    protected void onFrameChanged(AnimationEditor editor){

    }

    protected void updateFrameControls(AnimationEditor editor){

    }

}
