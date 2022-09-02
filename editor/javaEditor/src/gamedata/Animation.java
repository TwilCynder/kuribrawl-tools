package gamedata;

import java.awt.Image;
import java.nio.file.Path;
import java.awt.Graphics;

import KBUtil.PathHelper;
import KBUtil.Size2D;
import gamedata.exceptions.FrameOutOfBoundsException;

public class Animation {
    protected Frame[] frames;
    protected String name;
    protected double speed;
    protected Image source;
    protected Size2D frame_size;

    protected Path source_filename;

    public Animation(String name, Image source, int nbFrames, String filename) throws NullPointerException {
        this(nbFrames, name, source);
        setSourceFilename(filename);
    }

    private Animation(int nbFrames, Image source) throws NullPointerException{
        setSourceImage(source);
        makeFrames(nbFrames);
    }

    private void makeFrames(int nbFrames){
        this.frames = new Frame[nbFrames];
        int w = source.getWidth(null);
        int h = source.getHeight(null);
        int frameW = w / nbFrames;
        for (int i = 0; i < nbFrames; i++){
            this.frames[i] = new Frame(frameW * i, 0, frameW, h);
        }
        frame_size = new Size2D(frameW, h);
    }

    public Animation(int nbFrames, String name, Image source) {
        this(nbFrames, source);
        this.name = name;
    }

    public Size2D getFrameSize(){
        return frame_size;
    }

    /**
     * Return the frame at the given index
     * @param i index in the frames list
     * @return a frame, never null
     * @throws FrameOutOfBoundsException if the index if out of the array bounds.
     */
    public Frame getFrame(int i) throws FrameOutOfBoundsException {
        if (i < 0 || i >= frames.length) throw new FrameOutOfBoundsException(this, i);

        return frames[i];
    }

    public int getNbFrames() {
        return this.frames.length;
    }

    public String getName() {
        return this.name;
    }

    public double getSpeed(){
        return this.speed;
    }

    public void setSpeed(double s){
        this.speed = s;
    }

    public String getSourceFilename(){
        return source_filename.toString();
    }

    public void setSourceFilename(String filename){
        source_filename = PathHelper.stringToPathOrNull(filename);
    }

    public void setSourceFilename(Path path){
        if (path == null) throw new NullPointerException("Source image filename cannot be null");
        source_filename = path;
    }

    private void setSourceImage(Image source) throws NullPointerException{
        if (source == null) throw new NullPointerException("Source image cannot be null");
        this.source = source;
    }

    public void setSourceImage(Image source, String filename){
        setSourceFilename(filename);
        setSourceImage(source);
    }

    public void setSourceImage(Image source, Path filename){
        setSourceFilename(filename);
        setSourceImage(source);
    }
    
    public void draw(Graphics g, int frameIndex, int x, int y, int w, int h) throws FrameOutOfBoundsException{
        if (frames.length < 1) throw new FrameOutOfBoundsException(this, frameIndex);
        if (frameIndex >= frames.length) throw new FrameOutOfBoundsException(this, frameIndex);
        int sx = frame_size.w * frameIndex;
        //no sy it's always 0

        g.drawImage(source, x, y, x + w, y + h, sx, 0, sx + frame_size.w, frame_size.h, null);
    }

}
