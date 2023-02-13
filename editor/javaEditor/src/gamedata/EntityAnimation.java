package gamedata;

import gamedata.EntityFrame.FrameMovementAxis;
import gamedata.exceptions.FrameOutOfBoundsException;
import gamedata.exceptions.GameDataException;
import gamedata.parsers.AnimationParser;

import java.awt.Image;
import java.awt.Point;
import java.nio.file.InvalidPathException;
import java.util.Iterator;
import java.util.List;

import KBUtil.Pair;
import KBUtil.Vec2;

public class EntityAnimation extends Animation implements Iterable<Pair<Frame, EntityFrame>>{
    private EntityFrame[] entity_frames;

    public EntityAnimation(String name, Image source, int nbFrames, String source_filename, String descriptor_filename) throws InvalidPathException{
        super (name, source, nbFrames, source_filename, descriptor_filename);

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

    /**
     * Return the entity frame at the given index
     * @param i index in the frames list
     * @return an EntityFrame, never null
     * @throws FrameOutOfBoundsException if the index if out of the array bounds.
     */
    public EntityFrame getEntityFrame(int i) throws FrameOutOfBoundsException {
        if (i < 0 || i >= entity_frames.length) throw new FrameOutOfBoundsException(this, i);

        return entity_frames[i];
    }
    
    public class FrameIterator implements Iterator<Pair<Frame, EntityFrame>>{
        int i;

        FrameIterator (){
            i = -1;
        }

        public boolean hasNext(){
            return i < entity_frames.length - 1;
        }

        public Pair<Frame, EntityFrame> next(){
            i++; 
            try {
                return new Pair<>(getFrame(i), entity_frames[i]);
            } catch (FrameOutOfBoundsException ex){
                //it just aint happening 
                ex.printStackTrace();
                return null;
            }
            
        }
    }

    public FrameIterator iterator(){ 
        return new FrameIterator();
    }

    public List<Hitbox> getHitboxes(int i){
        if (i < 0 || i >= entity_frames.length) return null;
        EntityFrame frame = entity_frames[i];
        return frame.hitboxes;
    }

    public List<Hurtbox> getHurtboxes(int i){
        if (i < 0 || i >= entity_frames.length) return null;
        EntityFrame frame = entity_frames[i];
        return frame.hurtboxes;
    }

    public enum EntityAnimationDefaultness implements Defaultness {
        DEFAULT_CBOX;
    } 

    private Defaultness getFrameDefaultness(Frame frame, EntityFrame entityFrame){
        /*System.out.println("origin : " + frame.hasDefaultOrigin() + "; duration : " + 
        frame.hasDefaultDuration() + "; hitboxes : " + entityFrame.hitboxes.isEmpty() +
        "; hurtboxes : " + frameHasDefaultHurtboxes(frame, entityFrame));*/
        if (isFrameDefault(frame) && entityFrame.hitboxes.isEmpty()){
            if (entityFrame.hurtboxes.isEmpty()) {
                return AnimationDefaultness.DEFAULT;
            } else if (frameHasDefaultHurtboxes(frame, entityFrame)){
                return EntityAnimationDefaultness.DEFAULT_CBOX;
            }
            return AnimationDefaultness.NONDEFAULT;
        } 
        return AnimationDefaultness.NONDEFAULT;
    }

    private boolean frameHasDefaultHurtboxes(Frame frame, EntityFrame entityFrame){
        return entityFrame.hurtboxes.size() == 1 && entityFrame.hurtboxes.get(0).isDefault(frame.getOrigin(), frame_size);
    }

    private Defaultness getFrameDefaultness(int index){
        try {
            return getFrameDefaultness(getFrame(index), getEntityFrame(index));
        } catch (FrameOutOfBoundsException ex){
            throw new IllegalStateException(ex);
        }
    }

    public Defaultness getFramesDefaultness(){
        //premier passage : on teste si toutes les frames sont default
        Defaultness last_defaultness = getFrameDefaultness(0);
        for (int i = 1; i < getNbFrames(); i++){
            Defaultness d = getFrameDefaultness(i);
            
            if (d != last_defaultness || d == AnimationDefaultness.NONDEFAULT) return AnimationDefaultness.NONDEFAULT;
            last_defaultness = d;
        }
        return last_defaultness;
    }

    protected static String generateFrameMovementAxisDescriptor(FrameMovementAxis axis){
        String res = "";
        if (axis.enabled){
            if (axis.set_speed) res += "s";
            if (axis.whole_frame) res += "w";
            res+= ":" + axis.value;
        }
        return res;
    }

    protected String generateFrameInfoDescriptorLine(int index, Frame frame, EntityFrame eframe) throws GameDataException {
        String res = generateFrameDescriptor(index, frame);
        if (eframe.hasMovement()){
            res+= " " + "m";
            Vec2<FrameMovementAxis> movement = eframe.getMovement();
            res += 
                generateFrameMovementAxisDescriptor(movement.x) +
                generateFrameMovementAxisDescriptor(movement.y);
        }
        return res;
    }

    protected boolean needFrameInfoLineInDescriptor(Frame frame, EntityFrame eframe){
        return !super.isFrameDefault(frame) || eframe.hasMovement();
    }

    @Override
    public boolean needDescriptor() {
        return getFramesDefaultness().needDescriptor();
    }

    private String generateFrameDescriptor(int index, Frame frame, EntityFrame entityFrame) throws GameDataException{
        String res = "";
        boolean indexWritten = false;
        if (!isFrameDefault(frame)){

            if (needFrameInfoLineInDescriptor(frame, entityFrame)){
                res += generateFrameInfoDescriptorLine(index, frame, entityFrame);
                indexWritten = true;
            }
            res += System.lineSeparator();
        }

        for (Hurtbox h : entityFrame.hurtboxes){
            res += h.generateDescriptor(indexWritten |= !indexWritten, index, frame, frame_size);
            res += System.lineSeparator();
        }

        for (Hitbox h : entityFrame.hitboxes){
            res += h.generateDescriptor(indexWritten |= !indexWritten, index);
            res += System.lineSeparator();
        }

        return res; 
    }

    @Override
    protected String generateFrameDescriptor(int index) throws FrameOutOfBoundsException, GameDataException {
        return generateFrameDescriptor(index, getFrame(index), getEntityFrame(index));
    }

    public static void shiftElements(EntityFrame eFrame, Point diff){
        for (Hurtbox h : eFrame.hurtboxes){
            h.translate(diff.x, diff.y);
        }
        for (Hitbox h : eFrame.hitboxes){
            h.translate(diff.x, diff.y);
        }
    }

    public static void moveOrigin(Frame frame, EntityFrame entity_frame, Point new_pos){
        Point old_origin = new Point(frame.getOrigin());
        Point diff = new Point(
            old_origin.x - new_pos.x,
            new_pos.y - old_origin.y
        );
        frame.setOrigin(new_pos);
        shiftElements(entity_frame, diff);
    }

    public static void moveOriginX(Frame frame, EntityFrame entity_frame, int new_x){
        Point diff = new Point(
            frame.getOrigin().x - new_x, 0
        );
        frame.setOriginX(new_x);
        shiftElements(entity_frame, diff);
    }

    public static void moveOriginY(Frame frame, EntityFrame entity_frame, int new_y){
        Point diff = new Point(
            0,  new_y - frame.getOrigin().y
        );
        frame.setOriginY(new_y);
        shiftElements(entity_frame, diff);
    }

    @Override
    public AnimationType getType(){
        return AnimationType.ENTITY;
    }

    @Override
    public AnimationParser.EntityAnimationParsingState getParsingState(){
        return new AnimationParser.EntityAnimationParsingState(this);
    }

}
