package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

import javax.swing.JPanel;

import java.awt.Cursor;

public class Canvas extends JPanel{
    Displayable current_object;

    public Canvas(){
        super();
        setBackground(Color.WHITE);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public Canvas(Displayable obj){
        this();
        current_object = obj;
    }

    public void setDisplayable(Displayable obj){
        current_object = obj;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        Dimension dim = getSize();
        g2d.setColor(new Color((dim.width / 4) % 256, (dim.height / 4) % 256, ((dim.width + dim.height) / 4) % 256));
        g2d.fillRect(dim.width/4, dim.height/4, dim.width/2, dim.height/2);
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
