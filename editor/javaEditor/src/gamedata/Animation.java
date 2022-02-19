package gamedata;

import java.awt.Image;

import gamedata.exceptions.FrameOutOfBoundsException;

public class Animation {
    protected Frame[] frames;
    protected String name;
    protected double speed;
    protected Image source;

    protected String source_filname;

    public Animation(String name, Image source, int nbFrames, String filename){
        this(nbFrames, name, source);
        this.source_filname = filename;
    }

    private Animation(int nbFrames, Image source){
        this.source = source;
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
    }

    public Animation(int nbFrames, String name, Image source) {
        this(nbFrames, source);
        this.name = name;
    }

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
        return source_filname;
    }

    public void setSourceFilename(String filename){
        source_filname = filename;
    }

}
