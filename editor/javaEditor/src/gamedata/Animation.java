package gamedata;

import java.awt.Image;
import java.awt.Point;
import java.nio.file.Path;
import java.awt.Graphics;

import KBUtil.PathHelper;
import KBUtil.Size2D;
import gamedata.exceptions.FrameOutOfBoundsException;
import gamedata.exceptions.GameDataException;
import gamedata.exceptions.WhatTheHellException;
import gamedata.parsers.AnimationParser;

public class Animation {
    protected Frame[] frames;
    protected String name;
    protected double speed;
    protected Image source;
    protected Path descriptor_filename;
    protected Size2D frame_size;

    protected Path source_filename;

    public Animation(String name, Image source, int nbFrames, String filename, String descriptor_filename) throws NullPointerException {
        this(name, source, nbFrames, filename);
        setDescriptorFilename(descriptor_filename);
    }

    public Animation(String name, Image source, int nbFrames, String filename) throws NullPointerException {
        this(nbFrames, name, source);
        setSourceFilename(filename);
    }

    protected Animation(int nbFrames, Image source) throws NullPointerException{
        setSourceImage(source);
        makeFrames(nbFrames);
    }

    public void makeFrames(int nbFrames){
        if (nbFrames == 0) {
            this.frames = null;
            return;
        }

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
    
    public Path getDescriptorPath(){
        return descriptor_filename;
    }

    public String getDescriptorFilename(){
        return descriptor_filename == null ? null : descriptor_filename.toString();
    }

    public void setDescriptorFilename(String filename){
        descriptor_filename = PathHelper.stringToPathOrNull(filename);
    }

    public void setDescriptorFilename(Path path){
        descriptor_filename = path;
    }

    public void draw(Graphics g, int frameIndex, int x, int y, int w, int h) throws FrameOutOfBoundsException{
        if (frames.length < 1) throw new FrameOutOfBoundsException(this, frameIndex);
        if (frameIndex >= frames.length) throw new FrameOutOfBoundsException(this, frameIndex);
        int sx = frame_size.w * frameIndex;
        //no sy it's always 0

        g.drawImage(source, x, y, x + w, y + h, sx, 0, sx + frame_size.w, frame_size.h, null);
    }

    public AnimationType getType(){
        return AnimationType.ANIMATION;
    }

    public AnimationParser.AnimationParsingState getParsingState(){
        return new AnimationParser.AnimationParsingState(this);
    }

    public interface Defaultness {
        public default boolean needDescriptor(){
            return this == AnimationDefaultness.NONDEFAULT;
        }
    }

    public enum AnimationDefaultness implements Defaultness {
        NONDEFAULT,
        DEFAULT;

    }

    protected boolean isSpeedDefault(){
        return speed == 0 || speed == 1;
    }

    /**
     * Returns whether a frame is default, meaning that it has a default origin (at (w/2, h)) and a default duration (0 or 1)  
     * @param frame the frame to test
     * @return boolean
     */
    protected boolean isFrameDefault(Frame frame){
        return frame.hasDefaultOrigin() && frame.hasDefaultDuration();
    }

    protected String generateFrameInfoDescriptorLine(int index, Frame frame) throws GameDataException {
        String res = "";  
        res = "f" + index + " ";
        
        if (!frame.hasDefaultOrigin()){
            Point origin = frame.getOrigin();
            res += "o" + origin.x + " " + origin.y + " ";
        }
        int duration = frame.getDuration();
        if (duration != 0 && duration != 1){
            res += "d" + duration + " ";
        }  

        return res;
    }

    public String generateDescriptorHead() {
        String res = "" + frames.length + System.lineSeparator();
        if (!isSpeedDefault()){
            res += speed + System.lineSeparator();
        }
        return res;
    }

    protected String generateFrameDescriptor(int index, Frame frame) throws GameDataException{
        String res = "";
        //boolean indexWritten = false;
        if (!isFrameDefault(frame)){
            //indexWritten = true;
            res += generateFrameInfoDescriptorLine(index, frame);
            res += System.lineSeparator();
        }

        return res; 
    }

    protected String generateFrameDescriptor(int index) throws FrameOutOfBoundsException, GameDataException{
        return generateFrameDescriptor(index, getFrame(index));
    }

    public String generateDescriptor() throws GameDataException, WhatTheHellException{
        String res = generateDescriptorHead();

        try {
            for (int i = 0; i < getNbFrames(); i++){
                res += generateFrameDescriptor(i);
            } 
        } catch (FrameOutOfBoundsException e){
            throw new WhatTheHellException("Supposedly safe array iteration went out of bounds", e); 
        }
  
        return res;
    }

    protected boolean areFramesDefault(){
        boolean res = false;
        for (Frame f : frames){
            res |= isFrameDefault(f);
        }
        return res;
    }

    public boolean needDescriptor(){
        return !areFramesDefault();
    }
    
}
