package gamedata;

public class Animation {
    Frame[] frames;
    private int nbFrames;
    private String name;

    private Animation(int nbFrames){
        this.nbFrames = nbFrames;
        this.frames = new Frame[nbFrames];
    }
    public Animation(int nbFrames, String name) {
        this(nbFrames);
        this.name = name;
    }
}
