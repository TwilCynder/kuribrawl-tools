package gamedata;

import java.util.Collection;
import java.awt.Image;
import java.nio.file.InvalidPathException;

public interface AnimationPool <A extends Animation> extends Iterable<A>{

    public A getAnimation(String name);
    public Collection<A> getAnimations();

    public A addAnimation(String name, Image source, int nbFrames, String source_filename, String descriptor_filename) throws InvalidPathException;
}
