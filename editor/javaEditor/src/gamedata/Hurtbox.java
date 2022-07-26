package gamedata;

import KBUtil.Size2D;

public class Hurtbox extends CollisionBox {
    public HurtboxType type = HurtboxType.NORMAL;
    public Hurtbox(){
        super();
    }
    
    public Hurtbox(int x, int y, int w, int h){
        super(x, y, w, h);
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
}
