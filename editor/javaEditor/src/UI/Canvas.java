package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.FocusAdapter;

public class Canvas extends JPanel implements Displayer{
    private Interactable current_object;

    public Canvas(){
        super();
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if (current_object == null) return;
                switch (e.getButton()){
                    case MouseEvent.BUTTON1:
                    current_object.onLeftClick(e.getPoint(), Canvas.this);
                    break;
                    case MouseEvent.BUTTON3:
                    current_object.onRightClick(e.getPoint(), Canvas.this);
                    break;
                }


            }

            public void mousePressed(MouseEvent e){
                if (current_object != null){
                    current_object.mousePressed(e.getPoint(), Canvas.this);
                    if ( e.isPopupTrigger()){
                        System.out.println("popup Trigger");
                        current_object.onPopupTrigger(e.getPoint(), Canvas.this);
                    }
                }
            }

            public void mouseReleased(MouseEvent e){
                if (current_object != null){
                    current_object.mouseReleased(e.getPoint(), Canvas.this);
                    if ( e.isPopupTrigger()){
                        System.out.println("popup Trigger");
                        current_object.onPopupTrigger(e.getPoint(), Canvas.this);
                    }
                }
            }
            
        });

        addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent evt){
                if (current_object == null) return;
                current_object.mouseDragged(evt.getPoint(), Canvas.this);
            }
        });

        addFocusListener(new FocusAdapter(){
            public void focusLost(FocusEvent e){
                if (current_object == null) return;
                current_object.onLostFocus(Canvas.this);
			}
        });

        setBackground(Color.WHITE);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public Canvas(Interactable obj){
        this();
        current_object = obj;
    }

    public void update(){
        repaint();
    }

    public JComponent getComponent(){
        return this;
    }

    public void setDisplayable(Interactable obj){
        current_object = obj;
    }

    public Interactable getDisplayable(){
        return current_object;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Graphics2D g2d = (Graphics2D)g;
        Dimension dim = getSize();
        //g2d.setColor(new Color((dim.width / 4) % 256, (dim.height / 4) % 256, ((dim.width + dim.height) / 4) % 256));
        //g2d.fillRect(dim.width/4, dim.height/4, dim.width/2, dim.height/2);
        if (current_object != null){
            current_object.draw(g, 0, 0, dim.width, dim.height);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Figure out what the layout manager needs and
        // then add 100 to the largest of the dimensions
        // in order to enforce a 'round' bullseye
        Dimension layoutSize = super.getPreferredSize();
        int max = Math.max(layoutSize.width, layoutSize.height);
        return new Dimension(max + 200, max + 200);
    }
}
