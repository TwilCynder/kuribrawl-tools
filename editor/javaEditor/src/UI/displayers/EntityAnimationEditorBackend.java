package UI.displayers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import KBUtil.Rectangle;
import KBUtil.StringHelper;
import KBUtil.ui.ClipboardManager;
import KBUtil.ui.display.Displayer;
import UI.EntityAnimationEditorWindow;
import gamedata.CollisionBox;
import gamedata.DamageHitbox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.WhatTheHellException;

public class EntityAnimationEditorBackend implements ClipboardOwner{
    private EntityAnimationEditorWindow editorWindow;
    private Point drag_start_pos;
    private Rectangle selection;

    private static final Color selection_color = new Color(127, 127, 127, 255);

    
    private static abstract class InternalMenu extends JPopupMenu {
        protected Point pos = new Point(0, 0);
        protected Displayer displayer;
        protected EntityAnimationEditor editor;

        public void show(EntityAnimationEditor editor_, Displayer invoker, int x, int y) {
            displayer = invoker;
            editor = editor_;
            JComponent component = invoker.getComponent();
            if (component == null) return;
            pos = new Point(x, y);
            super.show(invoker.getComponent(), x, y);
        }

        public abstract void initItems();

        public InternalMenu(){
            initItems();
        }
    }

    
    private class PopupMenu extends InternalMenu {
        @Override
        public void initItems(){
            JMenuItem item;

            item = new JMenuItem("Paste");
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    pasteIntoFrame(editor, displayer);
                }
            });
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
            add(item);

            item = new JMenuItem("Move origin here");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    moveOrigin(editor, displayer, pos);
                }
            });
            add(item);
        }
        
    };

    private class PopupCollisionboxMenu extends PopupMenu {
        public void initItems() {
            JMenuItem item;

            item = new JMenuItem("Delete");
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    deleteSelectedCbox(editor, displayer);
                }
            });
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            add(item);

            item = new JMenuItem("Copy");
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    copySelectedCollisionBox(editor);
                }
            });
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
            add(item);

            addSeparator();

            super.initItems();
        }
        
    };

    private PopupMenu popup_menu = new PopupMenu();
    private PopupCollisionboxMenu popup_collisionbox_menu = new PopupCollisionboxMenu();

    
    private class SelectionPopupMenu extends InternalMenu {
     
        public void initItems() {
            JMenuItem item = new JMenuItem("Create hurtbox");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Frame frame = editor.getCurrentFrame();
                    EntityFrame entity_frame = editor.getCurrentEntityFrame();
                    frame.makeRelativeToOrigin(selection);
                    editor.setSelectedCBox(entity_frame.addHurtbox(selection.x, selection.y, selection.w, selection.h));
                    cancelSelection(displayer);
                    editorWindow.notifyDataModified();
                }
            });
            add(item);

            item = new JMenuItem("Create hitbox");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Frame frame = editor.getCurrentFrame();
                    EntityFrame entity_frame = editor.getCurrentEntityFrame();
                    frame.makeRelativeToOrigin(selection);
                    DamageHitbox hb = new DamageHitbox(selection);
                    entity_frame.addHitbox(hb);
                    editor.setSelectedCBox(hb);
                    cancelSelection(displayer);
                    editorWindow.notifyDataModified();
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

    private SelectionPopupMenu selection_popup_menu = new SelectionPopupMenu();
    
    public EntityAnimationEditorBackend(EntityAnimationEditorWindow win, EntityAnimationEditor editor){
        this.editorWindow = win;
        onAnimationChanged(editor);
    }

    public void draw(Graphics g, int x, int y, int w, int h, double zoom, EntityAnimationEditor editor) throws IllegalStateException{
        if (selection != null && selection.w + selection.h > 6){
            Rectangle display_selection = editor.getDisplayRectangle(selection);
            g.setColor(selection_color);
            g.drawRect(display_selection.x, display_selection.y, display_selection.w, display_selection.h);
        }
    }

    /**
     * Move origin to a certain anim pos and then updates whatever needs ot be updated following this change
     * @param displayer the Displayer to update
     * @param pos the position at which origin should be moved to, relative to the displayer
     */
    private void moveOrigin(EntityAnimationEditor editor, Displayer displayer, Point pos){
        moveOriginToDisplayPos(pos, editor);
        displayer.update();
        updateFrameControls(editor);
    }

    private void deleteCbox(Displayer d, CollisionBox cbox, EntityFrame entity_frame) throws IllegalStateException{
        if (cbox == null) return;
        if (cbox instanceof Hitbox){
            if (!entity_frame.hitboxes.remove(cbox)) 
                throw new IllegalStateException("Current selected hitbox is not part of the current frame's hitboxes");
        } else if (cbox instanceof Hurtbox){
            if (!entity_frame.hurtboxes.remove(cbox)) 
                throw new IllegalStateException("Current selected hurtbox is not part of the current frame's hurtboxes");
        }

        d.update();
        editorWindow.notifyDataModified();
    }

    private void deleteSelectedCbox(EntityAnimationEditor editor, Displayer d){
        deleteCbox(d, editor.resetSelectedCBox(), editor.getCurrentEntityFrame());
        onSelectedCBoxChanged(editor);
    }

    private void copyCollisionBox(CollisionBox cbox){
        if (cbox == null) return;
        ClipboardManager.setClipboardText(cbox.generateDescriptor(false, 0), this);
    }

    private void copySelectedCollisionBox(EntityAnimationEditor editor){
        copyCollisionBox(editor.getSelectedCBox());
    }

    protected void pasteIntoFrame(EntityAnimationEditor editor, Displayer d){
        pasteIntoFrame(editor.getCurrentEntityFrame(), d);
    }

    /**
     * Pastes into the current frame, i.e. parses the content of the clipboard and adds it to the current frame if it could be resolved to an element.
     */
    private void pasteIntoFrame(EntityFrame eframe, Displayer d){
        //TODO : this looks like code duplication, fix it
        try {
            String descriptor = ClipboardManager.getClipboardText();
                    
            if (descriptor == null || descriptor.length() < 1) return; //there was no text in the clipboard

            String[] fields = StringHelper.split(descriptor, " ");

            System.out.println(fields);
            System.out.println(descriptor.substring(0, 1));

            boolean modif = false;

            try {
                switch (descriptor.substring(0, 1)){
                    case "c":
                        eframe.addHurtbox(Hurtbox.parseDescriptorFields(fields, 1));
                        modif = true;
                        break;
                    case "h":
                        eframe.addHitbox(Hitbox.parseDescriptorFields(fields));
                        modif = true;
                        break;
                }
            } catch (RessourceException ex){
                System.out.println("Clipboard contains invalid collisionbox data");
                ex.printStackTrace();
            }

            if (modif){
                d.update();
                editorWindow.notifyDataModified();
            }
        }  catch (UnsupportedFlavorException ex) {
            throw new WhatTheHellException("So apparently the string flavor is not supported ?", ex);
        }

    }
    
    private void moveOrigin(Point animpoint, EntityAnimationEditor editor) throws IllegalStateException{
        //Point animpoint = getAnimPosition(display_point);
        //Size2D frame_size = current_anim.getFrameSize();
        //if (animpoint.x >= 0 && animpoint.x < frame_size.w && animpoint.y >= 0 && animpoint.y < frame_size.h){
        EntityAnimation.moveOrigin(
            editor.getCurrentFrame(),
            editor.getCurrentEntityFrame(),
            animpoint
        );           
        //}   
    }

    public void moveOriginX(int x, EntityAnimationEditor editor) throws IllegalStateException {
        EntityAnimation.moveOriginX(
                editor.getCurrentFrame(),
                editor.getCurrentEntityFrame(),
                x
            ); 
    }

    public void moveOriginY(int y, EntityAnimationEditor editor) throws IllegalStateException {
        EntityAnimation.moveOriginY(
                editor.getCurrentFrame(),
                editor.getCurrentEntityFrame(),
                y
            ); 
    }

    private void moveOriginToDisplayPos(Point p, EntityAnimationEditor editor){
        moveOrigin(editor.getAnimPosition(p), editor);
    }

    public EntityAnimationEditorWindow getWindow(){
        return editorWindow;
    }

    protected void onAnimationChanged(EntityAnimationEditor editor){
        editor.setFrameIndex(0);
        updateAnimationControls(true, editor.current_anim);
        onFrameChanged(editor);
    }

    private void updateAnimationControls(boolean ignoreModifications, EntityAnimation current_anim){
        editorWindow.updateAnimControls(current_anim, ignoreModifications);
    }

    protected void onFrameChanged(EntityAnimationEditor editor){
        editor.selected_cbox = null;
        updateFrameControls(editor, true);
        onSelectedCBoxChanged(editor);
    }

    private void updateFrameControls(EntityAnimationEditor editor){
        updateFrameControls(editor, false);
    }

    private void updateFrameControls(EntityAnimationEditor editor, boolean ignoreModifications){
        editorWindow.updateFrameControls(editor.getCurrentFrame(), editor.getCurrentEntityFrame(), ignoreModifications);
    }

    protected void onSelectedCBoxChanged(EntityAnimationEditor editor){
        updateElementControls(editor, true);
    }

    private void updateElementControls(EntityAnimationEditor editor, boolean ignoreModifications){
        editorWindow.updateElementControls(editor.selected_cbox, ignoreModifications);
    }

    @SuppressWarnings("unused")
    private void updateElementControls(EntityAnimationEditor editor){
        updateElementControls(editor, false);
    }
    
    public void lostOwnership(Clipboard clipboard, Transferable contents){
        //menfou
    }

    protected void cancelSelection(Displayer d){
        selection = null;
        d.update();
    }

    public void mousePressed(Point animpos, Displayer displayer){
        drag_start_pos = animpos;
        selection = new Rectangle(animpos.x, animpos.y, 1, 1);
    }

    public void mouseDragged(Point animpos, Displayer displayer){
        int w = animpos.x - drag_start_pos.x;
        if (w < 0){
            selection.x = animpos.x;
            selection.w = -w;
        } else {
            selection.x = drag_start_pos.x;
            selection.w = w;
        }    

        int h = animpos.y - drag_start_pos.y;
        if (h < 0){
            selection.y = animpos.y;
            selection.h = -h;
        } else {
            selection.y = drag_start_pos.y;
            selection.h = h;
        }

        displayer.update();
    }

    public void mouseReleased(Point pos, EntityAnimationEditor editor, Displayer displayer){
        if (selection != null){
            if (selection.w + selection.h < 10){
                cancelSelection(displayer);
            } else {
                selection_popup_menu.show(editor, displayer, pos.x, pos.y);
            }   
        }
    }

    public void onRightClick(Point p, Displayer d){
        System.out.println("Right click !");
    };

    public void onPopupTrigger(EntityAnimationEditor editor, Point p, Displayer d){
        JComponent component = d.getComponent();
        if (component == null) return;
        
        Frame frame = editor.getCurrentFrame();
        Point animpos = editor.getAnimPosition(p);

        if (editor.selected_cbox != null && editor.selected_cbox.isInside(animpos, frame.getOrigin())) {
            popup_collisionbox_menu.show(editor, d, p.x, p.y);
        } else {
            popup_menu.show(editor, d, p.x, p.y);
        }
    }

    public void onKeyPressed(EntityAnimationEditor editor, KeyEvent ev, Displayer d){
        switch (ev.getKeyCode()){
            case KeyEvent.VK_DELETE:
                deleteSelectedCbox(editor, d);
        }

        if ((ev.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0){
            switch (ev.getKeyCode()){
                case KeyEvent.VK_C:
                    copySelectedCollisionBox(editor);
                    break;
                case KeyEvent.VK_V:
                    pasteIntoFrame(editor, d);
                    break;
            }
        }

        if ((ev.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) > 0){
            CollisionBox selected_cbox = editor.selected_cbox;
            switch (ev.getKeyCode()){
                case KeyEvent.VK_UP:
                if (selected_cbox != null) selected_cbox.h -= 1;
                break;
            case KeyEvent.VK_DOWN:
                if (selected_cbox != null) selected_cbox.h += 1;
                break;
            case KeyEvent.VK_LEFT:
                if (selected_cbox != null) selected_cbox.w -= 1;
                break;
            case KeyEvent.VK_RIGHT:
                if (selected_cbox != null) selected_cbox.w += 1;
                break;
            default:
                return;
            }
        } else {
            CollisionBox selected_cbox = editor.selected_cbox;
            switch (ev.getKeyCode()){
                case KeyEvent.VK_UP:
                    if (selected_cbox != null) selected_cbox.y += 1;
                    break;
                case KeyEvent.VK_DOWN:
                    if (selected_cbox != null) selected_cbox.y -= 1;
                    break;
                case KeyEvent.VK_LEFT:
                    if (selected_cbox != null) selected_cbox.x -= 1;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (selected_cbox != null) selected_cbox.x += 1;
                    break;
                default:
                    return;
            }
        }

        d.update();
        updateElementControls(editor);

    }
}
