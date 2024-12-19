package gamedata;

import java.awt.Point;
import java.util.NoSuchElementException;

import KBUtil.Rectangle;
import KBUtil.Size2D;
import gamedata.exceptions.RessourceException;

public class Hurtbox extends CollisionBox {
    public HurtboxType type = HurtboxType.NORMAL;
    public Hurtbox(){
        super();
    }
    
    public Hurtbox(int x, int y, int w, int h){
        super(x, y, w, h);
    }

    public Hurtbox(Frame frame){
        super();
        Point origin = frame.getOrigin();
        Rectangle display = frame.getDisplay();        
        
        set(- origin.x, origin.y, display.w, display.h);
    }

    public String generateDescriptor(boolean writeIndex, int index){
        return generateDescriptor(writeIndex, index, null, null);
    }

    public String generateDescriptor(boolean writeIndex, int index, Frame frame, Size2D frame_size){
        String res = "c";
        if (writeIndex){
            res+= index;
        }
        res += ' ';

        if (frame != null && isDefault(frame.getOrigin(), frame_size)){
            res += "whole";
        } else {
            res += x + " " + y + " " + w + " " + h;
        }

        return res;
    }

    public static Hurtbox parseDescriptorFields(String[] fields, int firstField, Frame frame) throws RessourceException{
        Hurtbox h;
        int currentField = firstField;
        if (fields[currentField].equals("whole")){
            h = new Hurtbox(frame);
            currentField++;
        } else {
            if (fields.length < 5){
                throw new RessourceException("Hurtbox info should contain either 4 coordinates or \"whole\"");
            }

            try {
                h = new Hurtbox(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));
            } catch (NumberFormatException e){
                throw new RessourceException("Hurtbox coordinate is not a valid number");
            }
            currentField = 5;
        }

        if (fields.length > currentField){
            try {
                h.type = HurtboxType.valueOfSafe(Integer.parseInt(fields[currentField]));
            } catch (NumberFormatException | NoSuchElementException e){
                throw new RessourceException("Hurtbox type is not a valid hurtbox type code" + (!e.getMessage().isEmpty() ? " : " + e.getMessage() : ""));
            }
        }

        return h;
    }

    public static Hurtbox parseDescriptorFields(String[] fields, int firstField) throws RessourceException{
        return parseDescriptorFields(fields, firstField, null);
    }
}
