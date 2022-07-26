package gamedata;

import KBUtil.Rectangle;

public abstract class Hitbox extends CollisionBox {
    public Hitbox(){
        super();
    }
    public Hitbox(int x, int y, int w, int h){
        super(x, y, w, h);
    }

    public Hitbox(Rectangle rect){
        super(rect.x, rect.y, rect.w, rect.h);
    }

    public void generateDescriptor(String base, boolean writeIndex, int index){
        base += 'h';
        if (writeIndex){
            base += index;
        }
        base += ' ';

        base += x + " " + y + " " + w + " " + h + " " + Integer.toString(getTypeCode()) + " " + stringifyTypeSpecificInfo();
    }

    abstract public char getTypeCode();

    public abstract String stringifyTypeSpecificInfo();

    public static final char DAMAGE_HITBOX_CODE = 0;
    public static final char GRAB_HITBOX_CODE = 1;
    public static final char WIND_HITBOX_CODE = 2;
    public static final char SPECIAL_HITBOX_CODE = 3;
}
