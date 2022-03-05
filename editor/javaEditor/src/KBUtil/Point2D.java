package KBUtil;

public class Point2D {
    public int x;
    public int y;

    public Point2D(){
        this.x = 0;
        this.y = 0;
    }

    public Point2D(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D p){
        this.x = p.x;
        this.y = p.y;
    }
}
