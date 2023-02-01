package gamedata;

import java.awt.Point;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import KBUtil.PathHelper;

public class Stage {
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
