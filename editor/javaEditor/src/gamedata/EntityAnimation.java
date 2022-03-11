package gamedata;

import gamedata.exceptions.FrameOutOfBoundsException;
import java.awt.Image;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public class EntityAnimation extends Animation implements Iterable<EntityFrame>{
    private EntityFrame[] entity_frames;
    protected Path descriptor_filename;

    public EntityAnimation(String name, Image source, int nbFrames, Path source_filename, Path descriptor_filename){
        super (name, source, nbFrames, source_filename);
        this.descriptor_filename = descriptor_filename;
        initEntityFrames(nbFrames);
    }

    @Deprecated
    public EntityAnimation(int nbFrames, String name, Image source){
        super(nbFrames, name, source);
        initEntityFrames(nbFrames);
    }

    private void initEntityFrames(int nbFrames){
        this.entity_frames = new EntityFrame[nbFrames];
        for (int i = 0; i < nbFrames; i++){
            this.entity_frames[i] = new EntityFrame();
        }
    }

    public EntityFrame getEntityFrame(int i) throws FrameOutOfBoundsException {
        if (i < 0 || i >= entity_frames.length) throw new FrameOutOfBoundsException(this, i);

        return entity_frames[i];
    }

    private class FrameIterator implements Iterator<EntityFrame>{
        int i;

        FrameIterator (){
            i = -1;
        }

        public boolean hasNext(){
            return i < entity_frames.length - 1;
        }

        public EntityFrame next(){
            i++;
            return entity_frames[i];
        }
    }

    public Iterator<EntityFrame> iterator(){ 
        return new FrameIterator();
    }

    public Path getDescriptorFilename(){
        return descriptor_filename;
    }

    public List<Hitbox> getHitboxes(int i){
        if (i < 0 || i >= entity_frames.length) return null;
        EntityFrame frame = entity_frames[i];
        return frame.hitboxes;
    }

    public List<Hurtbox> getHurtboxes(int i){
        if (i < 0 || i >= entity_frames.length) return null;
        EntityFrame frame = entity_frames[i];
        return frame.hurtboxes;
    }
}
