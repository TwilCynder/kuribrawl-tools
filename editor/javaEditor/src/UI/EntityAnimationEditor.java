package UI;

import gamedata.CollisionBox;
import gamedata.DamageHitbox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.exceptions.FrameOutOfBoundsException;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.WhatTheHellException;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Color;

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
import KBUtil.ui.display.InteractableDisplayable;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.awt.event.ActionEvent;

public class EntityAnimationEditor extends EntityAnimationDisplayer implements InteractableDisplayable, ClipboardOwner {
    private EntityAnimationEditorWindow editorWindow;
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
                    pasteIntoFrame(displayer);
                }
            });
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
            add(item);

            item = new JMenuItem("Move origin here");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    moveOrigin(displayer, pos);
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
                    deleteSelectedCbox(displayer);
                }
            });
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            add(item);

            item = new JMenuItem("Copy");
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    copyCollisionBox();
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
                    Frame frame = getCurrentFrame();
                    EntityFrame entity_frame = getCurrentEntityFrame();
                    frame.makeRelativeToOrigin(selection);
                    setSelectedCBox(entity_frame.addHurtbox(selection.x, selection.y, selection.w, selection.h));
                    cancelSelection(displayer);

                    editorWindow.notifyDataModified();
                }
            });
            add(item);

            item = new JMenuItem("Create hitbox");
            item.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Frame frame = getCurrentFrame();
                    EntityFrame entity_frame = getCurrentEntityFrame();
                    frame.makeRelativeToOrigin(selection);
                    DamageHitbox hb = new DamageHitbox(selection);
                    entity_frame.addHitbox(hb);
                    setSelectedCBox(hb);
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

    public EntityAnimationEditor(EntityAnimation anim, EntityAnimationEditorWindow win){
        super(anim);
        this.editorWindow = win;
        onAnimationChanged();
    }

    @Override
    public void draw(Graphics g, int x, int y, int w, int h, double zoom) throws IllegalStateException{
        super.draw(g, x, y, w, h, zoom);
        if (selection != null && selection.w + selection.h > 6){
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

    /**
     * Move origin to a certain anim pos and then updates whatever needs ot be updated following this change
     * @param displayer the Displayer to update
     * @param pos the position at which origin should be moved to, relative to the displayer
     */
    private void moveOrigin(Displayer displayer, Point pos){
        moveOriginToDisplayPos(pos);
        displayer.update();
        updateFrameControls();
    }

    private void deleteSelectedCbox(Displayer d) throws IllegalStateException{
        if (selected_cbox == null) return;
        if (selected_cbox instanceof Hitbox){
            if (!getCurrentEntityFrame().hitboxes.remove(selected_cbox)) 
                throw new IllegalStateException("Current selected hitbox is not part of the hitbox list of the current frame");
        } else if (selected_cbox instanceof Hurtbox){
            if (!getCurrentEntityFrame().hurtboxes.remove(selected_cbox)) 
                throw new IllegalStateException("Current selected hurtbox is not part of the hitbox list of the current frame");
        }

        selected_cbox = null;

        d.update();
        editorWindow.notifyDataModified();
        onSelectedCBoxChanged();
    }

    private void copyCollisionBox(CollisionBox cbox){
        if (selected_cbox == null) return;
        ClipboardManager.setClipboardText(cbox.generateDescriptor(false, 0), this);
    }

    private void copyCollisionBox(){
        copyCollisionBox(selected_cbox);
    }

    /**
     * Pastes into the current frame, i.e. parses the content of the clipboard and adds it to the current frame if it could be resolved to an element.
     */
    private void pasteIntoFrame(Displayer d){
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
                        getCurrentEntityFrame().addHurtbox(Hurtbox.parseDescriptorFields(fields, 1));
                        modif = true;
                        break;
                    case "h":
                        getCurrentEntityFrame().addHitbox(Hitbox.parseDescriptorFields(fields));
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
        
        Frame frame = getCurrentFrame();

        Point animpos = getAnimPosition(p);

        if (selected_cbox != null && selected_cbox.isInside(animpos, frame.getOrigin())) {
            popup_collisionbox_menu.show(d, p.x, p.y);
        } else {
            popup_menu.show(d, p.x, p.y);
        }
    }

    public void onKeyPressed(KeyEvent ev, Displayer d){
        switch (ev.getKeyCode()){
            case KeyEvent.VK_DELETE:
                deleteSelectedCbox(d);
        }

        if ((ev.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0){
            switch (ev.getKeyCode()){
                case KeyEvent.VK_C:
                    copyCollisionBox();
                    break;
                case KeyEvent.VK_V:
                    pasteIntoFrame(d);;
                    break;    
            }
        }

        if ((ev.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) > 0){
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
        updateElementControls();

    }

    private void moveOrigin(Point animpoint) throws IllegalStateException{
        try {
            //Point animpoint = getAnimPosition(display_point);
            //Size2D frame_size = current_anim.getFrameSize();
            //if (animpoint.x >= 0 && animpoint.x < frame_size.w && animpoint.y >= 0 && animpoint.y < frame_size.h){
                EntityAnimation.moveOrigin(
                    current_anim.getFrame(currentFrameIndex),
                    current_anim.getEntityFrame(currentFrameIndex),
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
                    current_anim.getFrame(currentFrameIndex),
                    current_anim.getEntityFrame(currentFrameIndex),
                    x
                ); 
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }   
    }

    public void moveOriginY(int y) throws IllegalStateException {
        try {
            EntityAnimation.moveOriginY(
                    current_anim.getFrame(currentFrameIndex),
                    current_anim.getEntityFrame(currentFrameIndex),
                    y
                ); 
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }   
    }

    private void moveOriginToDisplayPos(Point p){
        moveOrigin(getAnimPosition(p));
    }

    public EntityAnimationEditorWindow getWindow(){
        return editorWindow;
    }

    private void onAnimationChanged(){
        setFrameIndex(0);
        updateAnimationControls(true);
        onFrameChanged();
    }

    private void updateAnimationControls(boolean ignoreModifications){
        editorWindow.updateAnimControls(current_anim, ignoreModifications);
    }

    @SuppressWarnings("unused")
    private void updateAnimationControls(){
        updateAnimationControls(false);
    }

    private void onFrameChanged(){
        selected_cbox = null;
        updateFrameControls(true);
        onSelectedCBoxChanged();
    }

    private void updateFrameControls(boolean ignoreModifications){
        try {
            editorWindow.updateFrameControls(current_anim.getFrame(currentFrameIndex), current_anim.getEntityFrame(currentFrameIndex), ignoreModifications);
        } catch (FrameOutOfBoundsException e){
            throw new IllegalStateException(e);
        }
    }

    private void updateFrameControls(){
        updateFrameControls(false);
    }

    private void onSelectedCBoxChanged(){
        updateElementControls(true);
    }

    private void updateElementControls(boolean ignoreModifications){
        editorWindow.updateElementControls(selected_cbox, ignoreModifications);
    }

    private void updateElementControls(){
        updateElementControls(false);
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

    public void lostOwnership(Clipboard clipboard, Transferable contents){
        //menfou
    }

}
