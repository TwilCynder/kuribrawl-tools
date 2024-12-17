package gamedata.parsers;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;

import KBUtil.StringHelper;
import KBUtil.Vec2;
import gamedata.Animation;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.GameData;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.RessourcePath;
import gamedata.exceptions.FrameOutOfBoundsException;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.WhatTheHellException;

public class AnimationParser extends Parser {
    private static void checkEntityAnimation(Animation anim, String msgIfFail) throws IllegalStateException{
        if (!(anim instanceof EntityAnimation)) throw new IllegalStateException(msgIfFail);
    }

    private static EntityAnimationParsingState safeCastEntityAnimation(AnimationParsingState cframe, String msgIfFail) throws NullPointerException {
        if (cframe == null) throw new NullPointerException("Attempt to cast a null object (AnimationParsingState to EntityAnimationParsingState)");
        if (!(cframe instanceof EntityAnimationParsingState)) throw new IllegalStateException(
            msgIfFail != null ? msgIfFail : "Attemp to cast Animation to EntityAnimation while not in EntityAnimation Mode"
        );
        return (EntityAnimationParsingState)cframe;
    }

    private static EntityAnimation safeCastEntityAnimation(Animation cframe, String msgIfFail) throws NullPointerException, IllegalStateException {
        if (cframe == null) throw new NullPointerException("Attempt to cast a null object (Animation to EnttyAnimtion)");
        checkEntityAnimation(cframe, 
            msgIfFail != null ? msgIfFail : "Attemp to cast Animation to EntityAnimation while not in EntityAnimation Mode"
        );
        return (EntityAnimation)cframe;
    }

    private static String wem = "EntityAnimation-specific element found in non-Entity Animation descriptor : ";
    private static String wem(String element_description){
        return wem + element_description;
    }

    private static RessourceException toRessourceException(Throwable ex){
        RessourceException nex = new RessourceException(ex.getMessage());
        nex.setStackTrace(ex.getStackTrace());
        return nex;
    }

    public static void parseAnimationShortDescriptor(GameData gd, String[] fields, RessourcePath rp, String file) throws RessourceException{
        Animation anim;
        try {
            anim = rp.addAnimation(gd, fields[0], Integer.parseInt(fields[1]), file, null);
        } catch (NumberFormatException e){
            throw new RessourceException("File info second field is neither a descriptor filename of a valid number", e);
        }

        if (fields.length > 2){
            anim.setSpeed(Double.parseDouble(fields[2]));
            if (fields.length > 3){
                try {
                    EntityAnimation entity_anim = safeCastEntityAnimation(anim, wem("All-frame-full-hurtbox indicator"));
                    if (fields[3].equals("c")){
                        for (int i = 0; i < anim.getNbFrames(); i++){
                            try {
                                Frame frame; EntityFrame entity_frame;
                                frame = anim.getFrame(i);
                                entity_frame = entity_anim.getEntityFrame(i);
                                entity_frame.fullFrameHurtbox(frame);
                            } catch (FrameOutOfBoundsException e){
                                throw new WhatTheHellException("Supposedly safe array iteration went out of bounds", e);
                            }
    
                        }
                    }
                } catch (IllegalStateException ex){
                    throw toRessourceException(ex);
                }

            }
        }
    }

    public static class AnimationParsingState {
        protected final Animation anim;
        protected Frame frame;
        protected int frame_index;

        protected void checkAnimation(Object anim) throws NullPointerException {
            if (anim == null){
                throw new NullPointerException("Attempt to create state with null animation");
            }
        }

        public AnimationParsingState(Animation anim, int i) throws FrameOutOfBoundsException {
            checkAnimation(anim);
            this.anim = anim;
            this.frame = anim.getFrame(i);
            this.frame_index= i;
        }

        public AnimationParsingState(Animation anim) {
            checkAnimation(anim);
            this.anim = anim;
            this.frame = null;
            this.frame_index = -1;
        }

        public boolean isValid(){
            return this.frame_index > -1;
        }

        public Animation getAnimation(){
            return anim;
        }

        public void setFrame(int i) throws FrameOutOfBoundsException {
            this.frame = anim.getFrame(i);
            this.frame_index = i;
        }

        public Frame getFrame(){
            return frame;
        }
    }

    public static class EntityAnimationParsingState extends AnimationParsingState {
        protected EntityFrame entity_frame;

        private EntityAnimation anim(){
            return (EntityAnimation)anim;
        }

        public EntityAnimationParsingState(EntityAnimation anim, int i) throws FrameOutOfBoundsException {
            super(anim, i);
            this.entity_frame = anim.getEntityFrame(i);
        }

        public EntityAnimationParsingState(EntityAnimation anim) {
            super(anim);
            this.entity_frame = null;
        }
        
        public EntityAnimation getAnimation(){
            return anim();
        }

        public void setFrame(int i) throws FrameOutOfBoundsException {
            super.setFrame(i);
            this.entity_frame = anim().getEntityFrame(i);
        }

        public EntityFrame getEntityFrame(){
            return entity_frame;
        }
    }

    /*
    private static enum AnimationMode {
        ANIMATION,
        ENTITY_ANIMATION
    }
    */

    private static void parseFrameMovementAxis(EntityFrame.FrameMovementAxis axis, String info) throws RessourceException{
        String[] fields;
        axis.enabled = true;
        fields = info.split(":");
        if (fields.length < 2) throw new RessourceException("Frame movement info should be of form \"m[<x mode>:<x value>]:[<y mode:y value>]\"");

        if (fields[0].contains("s")){
            axis.set_speed = true;
        }
        if (fields[0].contains("w")){
            axis.whole_frame = true;
        }

        try {
            axis.value = Double.parseDouble(fields[1]);
        } catch (NumberFormatException e) {
            throw new RessourceException("Movement value could not be parsed", e);
        }
    }

    public static void parseAnimationDescriptor(GameData gd, String tag, RessourcePath rp, String source_filename, String descriptor_filename, BufferedReader buff_reader) throws RessourceException, IOException {
        try (DescriptorReader reader = new DescriptorReader(buff_reader)) {
            parseAnimationdescriptor(gd, tag, rp, source_filename, descriptor_filename, reader);
        }
    }

    public static void parseAnimationdescriptor(GameData gd, String tag, RessourcePath rp, String source_filename, String descriptor_filename, DescriptorReader reader) throws IOException, RessourceException{
        
        String line = reader.readLine();
        if (line == null){
            throw new RessourceException("Descriptor doesn't contain shit", descriptor_filename, 1);
        }

        Animation anim = rp.addAnimation(gd, tag,
            parseInt(line, "Descriptor's first line is not a number", descriptor_filename, 1),
            source_filename, descriptor_filename);

        parseAnimationDescriptor(anim, descriptor_filename, reader);
    }

    private static AnimationParsingState createParsingState(Animation anim){
        return anim.getParsingState();
    }

    /**
     * Reads an animation descriptor starting from after the frame number. At this point
     * the animation should have already been constructed.
     * @param anim animation to parse descriptor for
     * @param descriptor_filename
     * @param reader 
     * @throws RessourceException
     * @throws WhatTheHellException
     * @throws IOException
     */
    private static void parseAnimationDescriptor(Animation anim, String descriptor_filename, DescriptorReader reader) throws RessourceException, WhatTheHellException, IOException{
        if (anim == null) throw new NullPointerException("Attemp to parse descriptor for null animation");

        String line;
        String[] fields;
        int valInt;

        if (descriptor_filename == null) throw new RessourceException("null descriptor filename");

        //Check if null before use : if null, that's a "no frame index founds before ..." type of error
        AnimationParsingState state = createParsingState(anim);

        int line_index = 1;

        try {
            while (reader.ready()){
                line_index += reader.getLinesRead();
                line = reader.readLine();
                switch(line.substring(0, 1)){
                    case "s":
                    line = line.substring(1);
                    anim.setSpeed(parseDouble(line, "Speed info is not a valid number", descriptor_filename, line_index));
                    break;
    
                    case "f":
                    fields = StringHelper.split(line.substring(1), " ");
                    if (fields.length < 1){
                        throw new RessourceException("Frame info line doesn't even contain a frame index", descriptor_filename, line_index);
                    }
    
                    valInt = parseInt(fields[0], "Frame index is not a valid integer", descriptor_filename, line_index);
    
                    state.setFrame(valInt);
    
                    for (int i = 1; i < fields.length; i ++){
                        switch(fields[i].substring(0, 1)){
                            case "d":
                            valInt = parseInt(fields[i].substring(1), "Frame duration is not a valid integer", descriptor_filename, line_index);
                            if (valInt < 1) throw new RessourceException("Duration should be strictly positive", descriptor_filename, line_index);
                            state.getFrame().setDuration(valInt);
                            break;
                            case "o":
                            valInt = parseInt(fields[i].substring(1), "Frame origin indicator not followed by a valid integer", descriptor_filename, line_index);
                            i++;
                            if (i >= fields.length) throw new RessourceException("Frame origin info should of form o<x> <y> but line stops after the first field", descriptor_filename, line_index);
                            {
                                int valInt2 =  parseInt(fields[i], "Frame origin 2nd field is not a valid integer", descriptor_filename, line_index);
                                state.getFrame().setOrigin(new Point(valInt, valInt2));
                            }
                            break;
                            case "m":
                            {
                                String[] subFields = fields[i].substring(1).split(",");
                                Vec2<EntityFrame.FrameMovementAxis> movement = 
                                    safeCastEntityAnimation(state, wem("Frame movement")).
                                    entity_frame.getMovement();
    
                                try {
                                    if (subFields[0] != ""){ //x movement
                                        parseFrameMovementAxis(movement.x, subFields[0]);
                                        if (subFields.length > 1){
                                            parseFrameMovementAxis(movement.y, subFields[0]);
                                        }
                                    }
                                } catch (RessourceException e){
                                    throw new RessourceException(e.getMessage(), descriptor_filename, line_index, e.getCause());
                                }
    
                            }
                            break;
                        }
                    }
                    break;
                    case "c":
                    {

                        EntityAnimationParsingState estate = safeCastEntityAnimation(state, wem("Hurtbox"));
                    
                        fields = StringHelper.split(line, " ");
                        if (fields.length > 1 && fields[1].equals("all")){
                            EntityAnimation eanim = estate.getAnimation();
                            eanim.fullFramehurtboxes();
                        } else {
        
                            if (fields.length < 2){
                                throw new RessourceException("Hurtbox info line does not contain any information", descriptor_filename, line_index);
                            }
        
                            if (fields[0].length() > 1){ //we have a "c<frame number>" at the beginning
                                valInt = parseInt(fields[0].substring(1), "Frame index is not a valid integer", descriptor_filename, line_index);
                                
                                state.setFrame(valInt);
                            }

                            if (!estate.isValid()){
                                throw new RessourceException("Hurtbox info with no frame index found before any frame info", descriptor_filename, line_index);
                            }
        
                            try {
                                estate.entity_frame.addHurtbox(Hurtbox.parseDescriptorFields(fields, 1, estate.frame));
                            } catch (RessourceException ex){
                                throw new RessourceException(ex.getMessage(), descriptor_filename, line_index, ex.getCause());
                            }        
                        }
                    }
                    break;
                    case "h":
                    {
                        EntityAnimationParsingState estate = safeCastEntityAnimation(state, "Hitbox");
    
                        fields = StringHelper.split(line, " ");
        
                        if (fields[0].length() > 1){ //we have a "c<frame number>" at the beginning
                            valInt = parseInt(fields[0].substring(1), "Frame index is not a valid integer", descriptor_filename, line_index);
        
                            state.setFrame(valInt);
                        }
        
                        try {
                            Hitbox h = Hitbox.parseDescriptorFields(fields);
                            if (h != null) estate.entity_frame.addHitbox(h);
                        } catch (RessourceException ex){
                            throw new RessourceException(ex.getMessage(), descriptor_filename, line_index, ex.getCause());
                        }
        
                    }
                    
                    break;
                    case "l": {
                        EntityAnimation eanim = (EntityAnimation)anim;
                        eanim.gab.;
                    }
                    break;
                    default:
                    System.err.println("WARNING : unknown marker at beginning of line " + line_index + " in descriptor " + descriptor_filename);
                }
            }
        } catch (IllegalStateException ex){
            throw toRessourceException(ex);
        } catch (FrameOutOfBoundsException e){
            throw new RessourceException("Frame index out of bounds", descriptor_filename, line_index, e);
        }
    }

}
