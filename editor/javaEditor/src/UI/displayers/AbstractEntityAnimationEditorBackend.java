//TODO LIST
//- faire la séparation entre EAEB et sa sous classe qui possède le editorwindow
//- compléter le squelette du AEB
//- compléter le AE
//- compléter le AEB

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

/**
 * Class exposing EntityAnimation editing functionalities. These functionalities will be called by en EntityAnimationEditor.
 */
public abstract class AbstractEntityAnimationEditorBackend extends AbstractAnimationEditorBackend implements ClipboardOwner{
    //TODO avoir l'EntityAnimationEditor en propriété (ou pas ?)
    //private EntityAnimationEditorWindow editorWindow;
    private Point drag_start_pos;
    private Rectangle selection;

    private static final Color selection_color = new Color(127, 127, 127, 255);

    @Override
    protected abstract EntityAnimationEditorWindow getEditorWindow();

    /**
     * Popup menu displayed inside an EntityAnimation editor, specifically. 
     */
    private static abstract class EntityAnimationPopupMenu extends InternalMenu {
        protected EntityAnimationEditor editor;

        public void show(EntityAnimationEditor editor_, Displayer invoker, int x, int y) {
            super.show(invoker, x, y);
            editor = editor_;
        }
    }
    
    /**
     * The basic popup menu displayed when right-clicking in an empty area
     */
    private class PopupMenu extends EntityAnimationPopupMenu implements OriginMovingMenu {
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
            item.addActionListener(createMoveOriginActionListener(this));
            add(item);
        }

        @Override
        public void moveOrigin() {
            AbstractEntityAnimationEditorBackend.this.moveOrigin(pos, editor, displayer);
        }
        
    };

    /**
     * Popup menu displayed when right cliking a collision box
     */
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

    /**
     * The popup menu displayed after performing a rectangle selection
     */
    private class SelectionPopupMenu extends EntityAnimationPopupMenu {
     
        public void initItems() {
            JMenuItem item = new JMenuItem("Create hurtbox");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    createHurtbox(editor, selection);
                    cancelSelection(displayer);
                }
            });
            add(item);

            item = new JMenuItem("Create hitbox");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    createHitbox(editor, selection);
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

    private SelectionPopupMenu selection_popup_menu = new SelectionPopupMenu();
    
    protected AbstractEntityAnimationEditorBackend(){

    }

    protected AbstractEntityAnimationEditorBackend(EntityAnimationEditor editor){

    }

    protected void onCreated(EntityAnimationEditor editor){
        onAnimationChanged(editor);
    }

    /**
     * Draws information related to the edition to the Canvas displaying the editor.
     * @throws IllegalStateException
     */
    public void draw(Graphics g, int x, int y, int w, int h, EntityAnimationEditor editor) throws IllegalStateException{
        if (selection != null && selection.w + selection.h > 6){
            Rectangle display_selection = editor.getDisplayRectangle(selection);
            g.setColor(selection_color);
            g.drawRect(display_selection.x, display_selection.y, display_selection.w, display_selection.h);
        }
    }

    private void createHurtbox(EntityAnimationEditor editor, Rectangle rect){
        Frame frame = editor.getCurrentFrame();
        EntityFrame entity_frame = editor.getCurrentEntityFrame();
        frame.makeRelativeToOrigin(rect);
        editor.setSelectedCBox(entity_frame.addHurtbox(rect.x, rect.y, rect.w, rect.h));
        //cancelSelection(d);
        notifyDataModified();
    }

    private void createHitbox(EntityAnimationEditor editor, Rectangle rect){
        Frame frame = editor.getCurrentFrame();
        EntityFrame entity_frame = editor.getCurrentEntityFrame();
        frame.makeRelativeToOrigin(rect);
        DamageHitbox hb = new DamageHitbox(rect);
        entity_frame.addHitbox(hb);
        editor.setSelectedCBox(hb);
        notifyDataModified();
    }

    /**
     * Deletes a collision box from a given entity frame.
     * @param d displayer that may need to be updated following this operation
     * @param cbox collision box to remove
     * @param entity_frame entity frame to remove the cbox from
     * @throws IllegalStateException
     */
    private void deleteCbox(Displayer d, CollisionBox cbox, EntityFrame entity_frame) throws IllegalStateException{
        if (cbox == null) return;
        if (cbox instanceof Hitbox){
            if (!entity_frame.hitboxes.remove(cbox)) 
                throw new IllegalStateException("Current selected hitbox is not part of the current frame's hitboxes");
        } else if (cbox instanceof Hurtbox){
            if (!entity_frame.hurtboxes.remove(cbox)) 
                throw new IllegalStateException("Current selected hurtbox is not part of the current frame's hurtboxes");
        }

        notifyAndUpdate(d);
    }

    /**
     * Deletes the selected cbox from the currently edited entity animation.
     * @param editor
     * @param d displayer that may need to be updated following this operation
     */
    private void deleteSelectedCbox(EntityAnimationDisplayer editor, Displayer d){
        deleteCbox(d, editor.resetSelectedCBox(), editor.getCurrentEntityFrame());
        onSelectedCBoxChanged(editor);
    }

    /**
     * Copy a collision box to the clipboard.
     * @param cbox
     */
    private void copyCollisionBox(CollisionBox cbox){
        if (cbox == null) return;
        ClipboardManager.setClipboardText(cbox.generateDescriptor(false, 0), this);
    }

    /**
     * Copy the currently selected collision box to the clipboard. 
     * @param editor
     */
    private void copySelectedCollisionBox(EntityAnimationDisplayer editor){
        copyCollisionBox(editor.getSelectedCBox());
    }

    /**
     * Attemps to paste the content of the clipboard to the currently editor frame, 
     * i.e. parses the content of the clipboard and adds it to the current frame if it could be resolved to an entity frame element.
     */
    protected boolean pasteIntoFrame(EntityAnimationDisplayer editor, Displayer d){
        return pasteIntoFrame(editor.getCurrentEntityFrame(), d);
    }

    /**
     * Attemps to paste the content of the clipboard to a given entity frame
     * @param eframe the entity frame where we'll try to put the resolved entity frame element.
     * @param d displayer that may need to be updated following this operation.
     */
    private boolean pasteIntoFrame(EntityFrame eframe, Displayer d){
        //TODO : this looks like code duplication, fix it
        try {
            String descriptor = ClipboardManager.getClipboardText();
                    
            if (descriptor == null || descriptor.length() < 1) return false; //there was no text in the clipboard

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
                notifyAndUpdate(d);
            }

            return modif;
        }  catch (UnsupportedFlavorException ex) {
            throw new WhatTheHellException("So apparently the string flavor is not supported ?", ex);
        }

    }

    //"hiding" the previous method
    protected void moveOrigin(Point displaypoint, AnimationEditor editor, Displayer displayer){
        throw new IllegalStateException("Called basic animation moveOrigin on an EntityAnimation editor");
    }

    /**
     * Move the origin of the current frame to a certain anim pos and then updates whatever needs ot be updated following this change.
     * @param displayer the Displayer to update
     * @param displaypoint the position at which origin should be moved to, *relative to the displayer*.
     */
    private void moveOrigin(Point displaypoint, EntityAnimationEditor editor, Displayer displayer){
        moveOriginToDisplayPos(displaypoint, editor);
        displayer.update();
        updateFrameControls(editor.getCurrentFrame());
    }

    /**
     * Moves the origin of the current frame to a certain display position, i.e. to the position in the animation that is
     * displayed at the specified position on the editor. 
     * @param displaypoint the on-screen point to which the origin should be moved.
     * @param editor
     */
    private void moveOriginToDisplayPos(Point displaypoint, EntityAnimationEditor editor){
        moveOrigin(editor.getAnimPosition(displaypoint), editor);
    }

    /**
     * Moves the origin of the current frame to the given point (relative to the origin). 
     * @param animpoint
     * @param editor
     * @throws IllegalStateException
     */
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

    public void moveOriginX(int x, AnimationDisplayer editor) throws IllegalStateException {
        if (editor instanceof EntityAnimationDisplayer){
            moveOriginX(x, (EntityAnimationDisplayer)editor);
        } else {
            super.moveOriginX(x, editor);
        }
    }

    public void moveOriginY(int y, AnimationDisplayer editor) throws IllegalStateException {
        if (editor instanceof EntityAnimationDisplayer){
            moveOriginY(y, (EntityAnimationDisplayer)editor);
        } else {
            super.moveOriginY(y, editor);
        }
    }

    /**
     * Changes the x coordinate of the origin of the current frame
     * @param x new coordinate
     * @param editor
     * @throws IllegalStateException
     */
    public void moveOriginX(int x, EntityAnimationDisplayer editor) throws IllegalStateException {
        EntityAnimation.moveOriginX(
                editor.getCurrentFrame(),
                editor.getCurrentEntityFrame(),
                x
            ); 
    }

    /**
     * Changes the y coordinate of the origin of the current frame
     * @param y new coordinate
     * @param editor
     * @throws IllegalStateException
     */
    public void moveOriginY(int y, EntityAnimationDisplayer editor) throws IllegalStateException {
        EntityAnimation.moveOriginY(
                editor.getCurrentFrame(),
                editor.getCurrentEntityFrame(),
                y
            ); 
    }


    /**
     * Handles operations that must be run whenever the current frame of an editor is changed
     * @param editor
     */
    protected void onFrameChanged(EntityAnimationDisplayer editor){
        editor.selected_cbox = null;
        super.onFrameChanged(editor);
        onSelectedCBoxChanged(editor);
    }

    /**
     * Handles operations that must be run whenever the current frame of an editor is changed
     * @param editor
     */
    protected void onSelectedCBoxChanged(EntityAnimationDisplayer editor){
        updateElementControls(editor, true);
    }

    /**
     * Updates the editor controls (in the UI) related to the current selected element.
     * @param editor
     * @param ignoreModifications whether the change should NOT mark data as modified.
     */
    private void updateElementControls(EntityAnimationDisplayer editor, boolean ignoreModifications){
        getEditorWindow().updateElementControls(editor.selected_cbox, ignoreModifications);
    }

    @SuppressWarnings("unused")
    private void updateElementControls(EntityAnimationEditor editor){
        updateElementControls(editor, false);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents){
        //menfou
    }

    /**
     * Cancels the rectangle selection and reflects it visually
     */
    protected void cancelSelection(Displayer d){
        selection = null;
        d.update();
    }

    /**
     * Called when the left button of the mouse has been pressed while on the editor.
     * @param pos position of the mouse in the editor
     * @param displayer
     */
    public void mousePressed(Point pos, EntityAnimationEditor editor, Displayer displayer){
        pos = editor.getAnimPosition(pos);
        drag_start_pos = pos;
        selection = new Rectangle(pos.x, pos.y, 1, 1);
    }

    /**
     * Called when the mouse has been moved with the left button down.
     * @param pos position of the mouse in the editor.
     * @param displayer
     */
    public void mouseDragged(Point pos, EntityAnimationEditor editor, Displayer displayer){
        pos = editor.getAnimPosition(pos);
        int w = pos.x - drag_start_pos.x;
        if (w < 0){
            selection.x = pos.x;
            selection.w = -w;
        } else {
            selection.x = drag_start_pos.x;
            selection.w = w;
        }    

        int h = pos.y - drag_start_pos.y;
        if (h < 0){
            selection.y = pos.y;
            selection.h = -h;
        } else {
            selection.y = drag_start_pos.y;
            selection.h = h;
        }

        displayer.update();
    }

    /**
     * Called when the left button of the mouse has been released. 
     * @param pos Position of the mouse, in pixels, relative to the the editor.
     * @param editor
     * @param displayer
     */
    public void mouseReleased(Point pos, EntityAnimationEditor editor, Displayer displayer){
        if (selection != null){
            if (selection.w + selection.h < 10){
                cancelSelection(displayer);
            } else {
                selection_popup_menu.show(editor, displayer, pos.x, pos.y);
            }   
        }
    }

    public void onLeftClick(Point p, Displayer d){

    }

    public void onRightClick(Point p, Displayer d){
    };

    /**
     * Called when an action that typically triggers actiation of a popup on the current system is performed. 
     * @param editor
     * @param p position of the mouse on the editor 
     * @param d
     */
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

    private boolean handleCollisionboxKeyPress(EntityAnimationDisplayer editor, KeyEvent ev, Displayer d, Frame frame, CollisionBox selected_cbox){
        if ((ev.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0){
            switch (ev.getKeyCode()){
                case KeyEvent.VK_C:
                    copySelectedCollisionBox(editor);
                    return true;
                case KeyEvent.VK_V:
                    return pasteIntoFrame(editor, d);
            }
        } else if ((ev.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) > 0){
            switch (ev.getKeyCode()){
                case KeyEvent.VK_UP:
                selected_cbox.h -= 1;
                return true;
            case KeyEvent.VK_DOWN:
                selected_cbox.h += 1;
                return true;
            case KeyEvent.VK_LEFT:
                selected_cbox.w -= 1;
                return true;
            case KeyEvent.VK_RIGHT:
                selected_cbox.w += 1;
                return true;
            default:
                break;
            }
        } else {
            switch (ev.getKeyCode()){
                case KeyEvent.VK_UP:
                    selected_cbox.y += 1;
                    return true;
                case KeyEvent.VK_DOWN:
                    selected_cbox.y -= 1;
                    return true;
                case KeyEvent.VK_LEFT:
                    selected_cbox.x -= 1;
                    return true;
                case KeyEvent.VK_RIGHT:
                    selected_cbox.x += 1;
                    return true;
                case KeyEvent.VK_DELETE:
                    deleteSelectedCbox(editor, d);
                    return true;
                case KeyEvent.VK_ESCAPE:
                    editor.resetSelectedCBox();
                    return true;
                default:
                    break;
            } 
        }
        return false;
    }

    protected boolean handleKeyPress(EntityAnimationDisplayer editor, KeyEvent ev, Displayer d, Frame frame, CollisionBox selected_cbox){
        boolean modifs;
        if (selected_cbox != null){
            modifs = handleCollisionboxKeyPress(editor, ev, d, frame, selected_cbox);
        } else {
            modifs = false;
        }

        if (!modifs){
            return super.handleKeyPress(editor, ev, d, frame);
        }

        return modifs;

    }

    public boolean onKeyPressed(EntityAnimationEditor editor, KeyEvent ev, Displayer d){
        CollisionBox selected_cbox = editor.selected_cbox;
        Frame frame = editor.getCurrentFrame();

        if (handleKeyPress(editor, ev, d, frame, selected_cbox)){
            notifyAndUpdate(d);
            updateElementControls(editor);
        }

        return true;

    }
}