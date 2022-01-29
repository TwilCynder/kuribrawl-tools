package gamedata.exceptions;

import gamedata.Animation;

public class FrameOutOfBoundsException extends Exception {
    static private String makeErrorMessage(Animation anim, int index){
        return "Attempt to access frame " + index + " of " + anim.getClass() + anim.getName() + " which only has " + anim.getNbFrames() + ".";
    }

    public FrameOutOfBoundsException(Animation anim, int index){
        super(makeErrorMessage(anim, index));
    }

}
