package gamedata;

import java.awt.Image;

import gamedata.exceptions.FrameOutOfBoundsException;

public class Animation {
    private Frame[] frames;
    private String name;
    private double speed;
    Image source;

    private Animation(int nbFrames, Image source){
        this.frames = new Frame[nbFrames];
        for (int i = 0; i < nbFrames; i++){
            this.frames[i] = new Frame();
        }
        this.source = source;
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

}
