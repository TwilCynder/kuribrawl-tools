package gamedata;

import java.awt.Image;
import java.awt.Point;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import KBUtil.PathHelper;

public class Stage implements AnimationPool<Animation> {
    public static class Platform {
        Point position;
        int w;
        Animation anim = null;
        public Platform(Point position, int w) {
            this.position = position;
            this.w = w;
        }
        public Platform(Point position, int w, Animation anim) {
            this(position, w);
            this.anim = anim;
        }
    }

    public static class BackgroundElement {
        Point position;
        Animation anim;
        public BackgroundElement(Point position, Animation anim) {
            this.position = position;
            this.anim = anim;
        }
        
    }
    
    private String display_name;
    private Path descriptor_file;
    private List<BackgroundElement> background_elements = new LinkedList<>();
    private List<Platform> platforms = new LinkedList<>();
    private Map<String, Animation> animations = new TreeMap<>();
    
    public Stage(String name) {
        this.display_name = name;
    }

    public Stage(String name, String filename) {
        this(name);
        setDisplayName(filename);
    }

    public void setDisplayName(String name){
        display_name = name;
    }

    public String getDisplayName(){
        return display_name;
    }

    public String getDescriptorFilename(){
        return descriptor_file.toString();
    }

    public Path getDescriptorPath(){
        return descriptor_file;
    }

    public void setDescriptorFilename(String filename) throws NullPointerException{
        setDescriptorFilename(PathHelper.stringToPathOrNull(filename));
    }

    public void setDescriptorFilename(Path path) throws NullPointerException{
        if (path == null) throw new NullPointerException("Descriptor path cannot be null");
        descriptor_file = path;
    }

    
    public Animation getAnimation(String name){
        return animations.get(name);
    }

    public Collection<Animation> getAnimations(){
        return animations.values();
    }

    public Iterator<Animation> iterator(){
        return getAnimations().iterator();
    }

    public Animation addAnimation(String name, Image source, int nbFrames, String source_filename, String descriptor_filename) throws InvalidPathException {
        Animation anim = new Animation(name, source, nbFrames, source_filename, descriptor_filename);
        animations.put(name, anim);
        return anim;
    }


    public void addBackgroundElement(int x, int y, Animation anim){
        background_elements.add(new BackgroundElement(new Point(x, y), anim));
    }

    public void addPlatform(int x, int y, int w){
        platforms.add(new Platform(new Point(x, y), w));
    }

    public void addPlatform(int x, int y, int w, Animation anim){
        platforms.add(new Platform(new Point(x, y), w, anim));
    }

}
