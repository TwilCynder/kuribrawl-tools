package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import KBUtil.Rectangle;
import KBUtil.StringHelper;
import KBUtil.ui.ClipboardManager;
import KBUtil.ui.display.Displayer;
import KBUtil.ui.display.Interactable;
import gamedata.CollisionBox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.WhatTheHellException;

public class EntityAnimationEditorBackend implements Interactable, ClipboardOwner{
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
    
    public EntityAnimationEditorBackend(EntityAnimation anim, EntityAnimationEditorWindow win){
        this.editorWindow = win;
        //onAnimationChanged();
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
    private void moveOrigin(Displayer displayer, Point pos){
        moveOriginToDisplayPos(pos);
        displayer.update();
        updateFrameControls();
    }

    private void deleteCbox(CollisionBox cbox, EntityFrame current_entity_frame) throws IllegalStateException{
        if (cbox == null) return;
        if (cbox instanceof Hitbox){
            if (!current_entity_frame.hitboxes.remove(cbox)) 
                throw new IllegalStateException("Current selected hitbox is not part of the current frame's hitboxes");
        } else if (cbox instanceof Hurtbox){
            if (!current_entity_frame.hurtboxes.remove(cbox)) 
                throw new IllegalStateException("Current selected hurtbox is not part of the current frame's hurtboxes");
        }

        editorWindow.notifyDataModified();
        onSelectedCBoxChanged();
    }

    private void deleteSelectedCbox(EntityAnimationEditor editor){
        deleteCbox(editor.getSelectedCBox(), editor.getCurrentEntityFrame());
    }

    private void copyCollisionBox(CollisionBox cbox){
        if (cbox == null) return;
        ClipboardManager.setClipboardText(cbox.generateDescriptor(false, 0), this);
    }

    private void copySelectedCollisionBox(EntityAnimationEditor editor){
        copyCollisionBox(editor.getSelectedCBox());
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
}
