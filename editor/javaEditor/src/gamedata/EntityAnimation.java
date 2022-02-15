package gamedata;

import gamedata.exceptions.FrameOutOfBoundsException;
import java.awt.Image;

import java.awt.Graphics;

public class EntityAnimation extends Animation {
    private EntityFrame[] entity_frames;
    protected String descriptor_filename;

    public EntityAnimation(String name, Image source, int nbFrames, String source_filename, String descriptor_filename){
        super (name, source, nbFrames, source_filename);
    }

    @Deprecated
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

    public void draw(Graphics g, int frame, int dx, int dy, int dw, int dh){
        if (frames.length < 1) return;
        int w = source.getWidth(null);
        int h = source.getHeight(null);
        w /= frames.length;
        int x = frame * w;

        dx = dx + (dw / 2) - (w / 2);
        dy = dy + (dh / 2) - (h / 2);

        g.drawImage(source, dx, dy, dx + w, dy + h, x, 0, x + w, h, null);
    }

    public String getDescriptorFilename(){
        return descriptor_filename;
    }

    /*
    public void setDescriptorFilename(String filename){
        descriptor_filename = filename;
    }
    */
}
