package gamedata;

import KBUtil.Rectangle;
import gamedata.exceptions.RessourceException;

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

    public String generateDescriptor(boolean writeIndex, int index){
        String res = "h";

        if (writeIndex){
            res += index;
        }
        res += ' ';

        res += x + " " + y + " " + w + " " + h + " " + Integer.toString(getTypeCode()) + " " + stringifyTypeSpecificInfo();
        return res;
    }

    public static Hitbox parseDescriptorFields(String[] fields) throws RessourceException{
        Hitbox h = null;

        if (fields.length < 6){
            throw new RessourceException("Hurtbox info line does not contain enough information (must be at least 4 coordinates and a type code)");
        }

        try {
            //coordinates
            Rectangle rect = new Rectangle(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));

            switch (Integer.parseInt(fields[5])){
                case Hitbox.DAMAGE_HITBOX_CODE:
                {
                    DamageHitbox hitbox = new DamageHitbox(rect);
                    if (fields.length < 10) throw new RessourceException("Damage Hitbox info line should contain at least 9 fields (x y w h type dmg angle bkb skb [hit[prio]]");
                    hitbox.damage = java.lang.Double.parseDouble(fields[6]);
                    hitbox.angle = Integer.parseInt(fields[7]);
                    hitbox.base_knockback= java.lang.Double.parseDouble(fields[8]);
                    hitbox.scaling_knockback = java.lang.Double.parseDouble(fields[9]);
                    if (fields.length > 10){
                        hitbox.hitID = Integer.parseInt(fields[10]);
                        if (fields.length > 11) hitbox.priority = Integer.parseInt(fields[11]);
                    }
                    h = hitbox;
                }
                break;
                case Hitbox.GRAB_HITBOX_CODE:

                break;
                case Hitbox.WIND_HITBOX_CODE:
                break;
                case Hitbox.SPECIAL_HITBOX_CODE:
                break;
                default:
                throw new RessourceException("Unsupported hitbox type : " + fields[5]);
            }
        } catch (NumberFormatException e){
            throw new RessourceException("One of the numeric values could not be parsed", e);
        }

        return h;
    }

    abstract public char getTypeCode();

    public abstract String stringifyTypeSpecificInfo();

    public static final char DAMAGE_HITBOX_CODE = 0;
    public static final char GRAB_HITBOX_CODE = 1;
    public static final char WIND_HITBOX_CODE = 2;
    public static final char SPECIAL_HITBOX_CODE = 3;
}
