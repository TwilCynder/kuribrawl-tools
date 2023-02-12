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
import gamedata.exceptions.FrameOutOfBoundsException;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.WhatTheHellException;

public class AnimationDescriptorParser extends Parser {
    
    private static class CurrentFrame {
        public final int index;
        public final Frame frame;

        public CurrentFrame(Animation anim, int i) throws FrameOutOfBoundsException {
            index = i;
            frame = anim.getFrame(i);
        }

        public CurrentFrame(){
            index = -1;
            frame = null;
        };

        public boolean valid(){
            return index > -1;
        }

        public String toString(){
            return "Frame " + index;
        }

        public static CurrentFrame createCurrentFrame(Animation anim, int i) throws FrameOutOfBoundsException{
            return new CurrentFrame(anim, i);
        }
    }

    private static class CurrentEntityModeFrame extends CurrentFrame {
        public final EntityFrame entity_frame;

        public CurrentEntityModeFrame(EntityAnimation anim, int i) throws FrameOutOfBoundsException{
            super(anim, i);
            entity_frame = anim.getEntityFrame(i);
        }

        public CurrentEntityModeFrame(){
            super();
            entity_frame = null;
        }

        public static CurrentFrame createCurrentFrame(EntityAnimation anim, int i) throws FrameOutOfBoundsException{
            return new CurrentEntityModeFrame(anim, i);
        }
    }

    /*
    private static enum AnimationMode {
        ANIMATION,
        ENTITY_ANIMATION
    }
    */

    private static CurrentEntityModeFrame safeCastEntityAnimation(CurrentFrame cframe) throws NullPointerException {
        if (cframe == null) throw new NullPointerException("Attempt to cast a null object (CurrentFrame to CurrentEntityModeFrame)");
        if (cframe instanceof CurrentEntityModeFrame) throw new IllegalStateException(
            "Attemp to cast Animation CurrentFrame to EntityAnimation CurrentFrame while not in EntityAnimationMode"
        );
        return (CurrentEntityModeFrame)cframe;
    }

    private static EntityAnimation safeCastEntityAnimation(Animation cframe) throws NullPointerException {
        if (cframe == null) throw new NullPointerException("Attempt to cast a null object (Animation to EnttyAnimtion)");
        if (cframe instanceof EntityAnimation) throw new IllegalStateException(
            "Attemp to cast Animation to EntityAnimation while not in EntityAnimation Mode"
        );
        return (EntityAnimation)cframe;
    }

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

    public static <A extends Animation> A parseAnimationdescriptorModal(A anim, String descriptor_filename, BufferedReader buff_reader) throws RessourceException, WhatTheHellException, IOException {
        try (DescriptorReader reader = new DescriptorReader(buff_reader)){
            return parseAnimationDescriptorModal(anim, descriptor_filename, reader);
        }
    }

    private static void checkEntityAnimation(Animation anim, String element_description) throws RessourceException{
        if (!(anim instanceof EntityAnimation)) throw new RessourceException("EntityAnimation-specific element found in non-Entity Animation descriptor : " + element_description);
    }

    private static <A extends Animation> A parseAnimationDescriptorModal(A anim, String descriptor_filename, DescriptorReader reader) throws RessourceException, WhatTheHellException, IOException{
        if (anim == null) throw new NullPointerException("Attemp to parse descriptor for null animation");

        String line;
        String[] fields;
        int valInt;

        if (descriptor_filename == null) throw new RessourceException("null descriptor filename");

        line = reader.readLine();
        if (line == null){
            throw new RessourceException("Descriptor doesn't contain shit", descriptor_filename, 1);
        }

        /*
        Animation anim = addAnimation(gd, tag,
            parseInt(line, "Descriptor's first line is not a number", descriptor_filename, 1),
            source_filename, descriptor_filename);
        */

        CurrentFrame current_frame = new CurrentFrame();

        int line_index = 1;

        while (reader.ready()){
            line_index += reader.getLinesRead();
            line = reader.readLine();
            //System.out.println(line);
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

                try {
                    current_frame = new CurrentEntityModeFrame(null, line_index);
                } catch (FrameOutOfBoundsException e){
                    throw new RessourceException("Frame index out of bounds", descriptor_filename, line_index, e);
                }

                for (int i = 1; i < fields.length; i ++){
                    switch(fields[i].substring(0, 1)){
                        case "d":
                        valInt = parseInt(fields[i].substring(1), "Frame duration is not a valid integer", descriptor_filename, line_index);
                        if (valInt < 1) throw new RessourceException("Duration should be strictly positive", descriptor_filename, line_index);
                        //System.out.println("duration : " + valInt);
                        current_frame.frame.setDuration(valInt);
                        break;
                        case "o":
                        valInt = parseInt(fields[i].substring(1), "Frame origin indicator not followed by a valid integer", descriptor_filename, line_index);
                        i++;
                        if (i >= fields.length) throw new RessourceException("Frame origin info should of form o<x> <y> but line stops after the first field", descriptor_filename, line_index);
                        {
                            int valInt2 =  parseInt(fields[i], "Frame origin 2nd field is not a valid integer", descriptor_filename, line_index);
                            current_frame.frame.setOrigin(new Point(valInt, valInt2));
                        }
                        break;
                        case "m":
                        {
                            checkEntityAnimation(anim, "Frame movement");

                            String[] subFields = fields[i].substring(1).split(",");
                            Vec2<EntityFrame.FrameMovementAxis> movement = safeCastEntityAnimation(current_frame).entity_frame.getMovement();

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
                checkEntityAnimation(anim, "Hurtbox");

                fields = StringHelper.split(line, " ");
                if (fields.length > 1 && fields[1].equals("all")){
                    for (int i = 0; i < anim.getNbFrames(); i++){
                        Frame frame; EntityFrame entity_frame;
                        try {
                            frame = anim.getFrame(i);
                            entity_frame = safeCastEntityAnimation(anim).getEntityFrame(i);
                        } catch (FrameOutOfBoundsException e){
                            throw new WhatTheHellException("Supposedly safe array iteration went out of bounds", e);
                        }

                        entity_frame.fullFrameHurtbox(frame);
                    }
                } else {

                    if (fields.length < 2){
                        throw new RessourceException("Hurtbox info line does not contain any information", descriptor_filename, line_index);
                    }

                    if (fields[0].length() > 1){ //we have a "c<frame number>" at the beginning
                        valInt = parseInt(fields[0].substring(1), "Frame index is not a valid integer", descriptor_filename, line_index);

                        try {
                            current_frame = new CurrentFrame(anim, valInt);
                        } catch (FrameOutOfBoundsException e){
                            throw new RessourceException("Frame index out of bounds", descriptor_filename, line_index, e);
                        }
                    }

                    if (!current_frame.valid()){
                        throw new RessourceException("Hurtbox info with no frame index found before any frame info", descriptor_filename, line_index);
                    }

                    try {
                        safeCastEntityAnimation(current_frame).entity_frame.addHurtbox(Hurtbox.parseDescriptorFields(fields, 1, current_frame.frame));
                    } catch (RessourceException ex){
                        throw new RessourceException(ex.getMessage(), descriptor_filename, line_index, ex.getCause());
                    }

                }

                break;
                case "h":

                checkEntityAnimation(anim, "Hurtbox");

                fields = StringHelper.split(line, " ");

                if (fields[0].length() > 1){ //we have a "c<frame number>" at the beginning
                    valInt = parseInt(fields[0].substring(1), "Frame index is not a valid integer", descriptor_filename, line_index);

                    try {
                        current_frame = new CurrentFrame(anim, valInt);
                    } catch (FrameOutOfBoundsException e){
                        throw new RessourceException("Frame index out of bounds", descriptor_filename, line_index, e);
                    }
                }

                try {
                    Hitbox h = Hitbox.parseDescriptorFields(fields);
                    if (h != null) safeCastEntityAnimation(current_frame).entity_frame.addHitbox(h);
                } catch (RessourceException ex){
                    throw new RessourceException(ex.getMessage(), descriptor_filename, line_index, ex.getCause());
                }

                break;
            }
        }

        return anim;

    }

    public static Animation parseAnimationDescriptor(Animation anim, String descriptor_filename, BufferedReader buff_reader) throws RessourceException, WhatTheHellException, IOException {
        return parseAnimationdescriptorModal(anim, descriptor_filename, buff_reader);
    }

    public static EntityAnimation parseEntityAnimationDescriptor(EntityAnimation anim, String descriptor_filename, BufferedReader buff_reader) throws RessourceException, WhatTheHellException, IOException {
        return parseAnimationdescriptorModal(anim, descriptor_filename, buff_reader);
    }

}
