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

    public void generateDescriptor(String base, boolean writeIndex, int index){
        generateDescriptor(base, writeIndex, index, null, null);
    }

    public void generateDescriptor(String base, boolean writeIndex, int index, Frame frame, Size2D frame_size){
        base += 'c';
        if (writeIndex){
            base+= index;
        }
        base += ' ';

        if (frame != null && isDefault(frame.getOrigin(), frame_size)){
            base += "whole";
        } else {
            base += x + " " + y + " " + w + " " + h;
        }
    }
}
