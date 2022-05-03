package gamedata;

public class Hurtbox extends CollisionBox {
    public HurtboxType type = HurtboxType.NORMAL;
    public Hurtbox(){
        super();
    }
    
    public Hurtbox(int x, int y, int w, int h){
        super(x, y, w, h);
    }
}
