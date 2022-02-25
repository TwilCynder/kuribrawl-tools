package gamedata;

import gamedata.exceptions.FrameOutOfBoundsException;
import java.awt.Image;
import java.util.Iterator;
import java.awt.Graphics;

public class EntityAnimation extends Animation implements Iterable<EntityFrame>{
    private EntityFrame[] entity_frames;
    protected String descriptor_filename;

    public EntityAnimation(String name, Image source, int nbFrames, String source_filename, String descriptor_filename){
        super (name, source, nbFrames, source_filename);
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

    public void draw(Graphics g, int frame, int dx, int dy, int tw, int th, double zoom){
        if (frames.length < 1) return;
        if (frame >= frames.length) return;
        int sw = source.getWidth(null);
        int sh = source.getHeight(null);
        sw /= frames.length;
        int x = frame * sw;

        int dw = (int)Math.round(sw * zoom);
        int dh = (int)Math.round(sh * zoom);


        dx = dx + (tw / 2) - (dw / 2);
        dy = dy + (th / 2) - (dh / 2);

        g.drawImage(source, dx, dy, dx + dw, dy + dh, x, 0, x + sw, sh, null);
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
