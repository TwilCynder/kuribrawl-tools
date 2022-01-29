package gamedata;

import gamedata.exceptions.FrameOutOfBoundsException;
import java.awt.Image;

public class EntityAnimation extends Animation {
    private EntityFrame[] entity_frames;

    public EntityAnimation(int nbFrames, String name, Image source){
        super(nbFrames, name, source);
        this.entity_frames = new EntityFrame[nbFrames];
        for (int i = 0; i < nbFrames; i++){
            this.entity_frames[i] = new EntityFrame();
        }
    }

    public EntityFrame getEntityFrame(int i) throws FrameOutOfBoundsException {
        if (i < 0 || i >= entity_frames.length) throw new FrameOutOfBoundsException(this, i);

        return entity_frames[i];
    }
}
