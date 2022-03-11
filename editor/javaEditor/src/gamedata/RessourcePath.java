package gamedata;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.awt.Image;

import javax.imageio.ImageIO;

import java.awt.Point;
import KBUtil.Rectangle;
import KBUtil.Vec2;
import gamedata.exceptions.FrameOutOfBoundsException;
import gamedata.exceptions.InvalidRessourcePathException;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.WhatTheHellException;

public class RessourcePath {
    private Path path;
    private LinkOption symlinks_behavior;

    public RessourcePath(Path path)throws InvalidRessourcePathException{
        this.path = path;
        this.symlinks_behavior = LinkOption.NOFOLLOW_LINKS;
        if (!Files.exists(this.path, symlinks_behavior)){
            throw new InvalidRessourcePathException("Path does not exist");
        }

        if (!Files.isDirectory(this.path, symlinks_behavior)){
            throw new InvalidRessourcePathException("Path is not a directory");
        }
    }

    @Deprecated
    public RessourcePath(String pathname) throws InvalidRessourcePathException{
        this(stringToPath(pathname));
    }

    public Path getPath(){
        return path;
    }

    public static Path stringToPath(String pathname) throws InvalidRessourcePathException {
        try {
            return Paths.get(pathname);
        } catch (InvalidPathException e){
            throw new InvalidRessourcePathException("Invalid path", e);
        }
    }

    @Deprecated
    private InputStream fileStream(String name) throws IOException, InvalidPathException{
        return Files.newInputStream(path.resolve(name), symlinks_behavior);
    }

    private InputStream fileStream(Path path) throws IOException, InvalidPathException{
        return Files.newInputStream(this.path.resolve(path), symlinks_behavior);
    }

    private File getFile(String name) {
        return path.resolve(name).toFile();
    }

    static private Path toPath(String name){
        return name == null ? null : Paths.get(name);
    }

    private BufferedReader fileReader(String name) throws IOException, InvalidPathException, NoSuchFileException {
        Path filepath = path.resolve(name);
        filepath = filepath.toAbsolutePath();
        return Files.newBufferedReader(filepath);
    } 

    private static int parseInt(String str, String msgIfFail, String filename, int line) throws RessourceException{
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e){
            throw new RessourceException(msgIfFail, filename, line, e);
        }
    }

    private static double parseDouble(String str, String msgIfFail, String filename, int line) throws RessourceException{
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e){
            throw new RessourceException(msgIfFail, filename, line, e);
        }
    }

    private static String[] split(String str, String sep){
        return Arrays.stream(str.split(sep)).filter(e -> e.trim().length() > 0).toArray(String[]::new);
    }

    private static void fullFrameHurtbox(Frame frame, EntityFrame entity_frame){
        Point origin = frame.getOrigin();
        Rectangle display = frame.getDisplay();        
        
        entity_frame.addHurtbox(- origin.x, origin.y, display.w, display.h);
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

    /**
     * Adds a new animation to the specified GameData based on basic information from the text files.  
     * Handles the interpretation of these informations (loading the given image file, parsing the tag, etc)
     */
    private EntityAnimation addAnimation(GameData gd, String tag, int nbFrames, String source_filename, String descriptor_filename) throws RessourceException{
        String[] tagSplit = split(tag, "/");

        if (tagSplit.length != 2 && tagSplit[0] != ""){
            throw new RessourceException("Ill-formed animation file info : tag should contain 2 non-empty fields separated by \\ : (" + tag + ")");
        }

        try {
            Image source = ImageIO.read(getFile(source_filename));
            //System.out.println("RessourcePath.addAnimation" + source);

            return gd
            .tryChampion(tagSplit[0])
            .addAnimation(tagSplit[1], source, nbFrames, toPath(source_filename), toPath(descriptor_filename));
        } catch (IOException e){
            throw new RessourceException("Couldn't read source image file " + source_filename, e);
        }

    }

    private class CurrentFrame {
        public final int index;
        public final Frame frame;
        public final EntityFrame entity_frame;

        public CurrentFrame(EntityAnimation anim, int i) throws FrameOutOfBoundsException{
            index = i;
            frame = anim.getFrame(i);
            entity_frame = anim.getEntityFrame(i);
        }

        public CurrentFrame(){
            index = -1;
            frame = null;
            entity_frame = null;
        }

        public boolean valid(){
            return index > -1;
        }

        public String toString(){
            return "" + index;
        }
    }

    private EntityAnimation parseAnimationDescriptor(GameData gd, String tag, String source_filename, String descriptor_filename) throws RessourceException, WhatTheHellException{
        String line;
        String[] fields;
        int valInt;

        if (descriptor_filename == null) throw new RessourceException("null descriptor filename");

        try (BufferedReader reader = fileReader(descriptor_filename)){

            line = reader.readLine();
            if (line == null){
                throw new RessourceException("Descriptor contains no shit", descriptor_filename, 1);
            }

            EntityAnimation anim = addAnimation(gd, tag, 
                parseInt(line, "Descriptor's first line is not a number", descriptor_filename, 1), 
                source_filename, descriptor_filename);

            CurrentFrame current_frame = new CurrentFrame();

            int line_index = 1;
            while (reader.ready()){
                line_index++;
                line = reader.readLine();
                //System.out.println(line);
                switch(line.substring(0, 1)){
                    case "s":
                    line = line.substring(1);
                    anim.setSpeed(parseDouble(line, "Speed info is not a valid number", descriptor_filename, line_index));
                    break;

                    case "f":
                    fields = split(line.substring(1), " ");
                    if (fields.length < 1){
                        throw new RessourceException("Frame info line doesn't even contain a frame index", descriptor_filename, line_index);
                    }

                    valInt = parseInt(fields[0], "Frame index is not a valid integer", descriptor_filename, line_index);
                    
                    try {
                        current_frame = new CurrentFrame(anim, valInt);
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
                                String[] subFields = fields[i].substring(1).split(",");

                                Vec2<EntityFrame.FrameMovementAxis> movement = current_frame.entity_frame.getMovement();

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
                    if (line.trim().equals("c all")){
                        for (int i = 0; i < anim.getNbFrames(); i++){
                            Frame frame; EntityFrame entity_frame;
                            try {
                                frame = anim.getFrame(i);
                                entity_frame = anim.getEntityFrame(i);
                            } catch (FrameOutOfBoundsException e){
                                throw new WhatTheHellException("Supposedly safe array iteration went out of bounds", e);
                            }
                            
                            fullFrameHurtbox(frame, entity_frame);
                        }
                    } else {

                        fields = split(line, " ");

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
    
                        if (fields[1].equals("whole")){
                            fullFrameHurtbox(current_frame.frame, current_frame.entity_frame);
                        } else {
                            if (fields.length != 5){
                                throw new RessourceException("Hurtbox info should contain either 4 coordinates or \"all\"", descriptor_filename, line_index);
                            }
    
                            try {
                                current_frame.entity_frame.addHurtbox(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));
                            } catch (NumberFormatException e){
                                throw new RessourceException("Hurtbox coordinate is not a valid number", descriptor_filename, line_index);
                            }
    
                        }
                    }


                    break;
                    case "h":
                    fields = split(line, " ");

                    if (fields.length < 6){
                        throw new RessourceException("Hurtbox info line does not contain enough information (must be at least 4 coordinates and a type code)", descriptor_filename, line_index);
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
                        //coordinates
                        Rectangle rect = new Rectangle(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));
                        switch (Integer.parseInt(fields[5])){
                            case Hitbox.DAMAGE_HITBOX_CODE:
                            {
                                DamageHitbox hitbox = new DamageHitbox(rect);
                                if (fields.length < 10) throw new RessourceException("Hitbox info line should contain at least 9 fields (x y w h type dmg angle bkb skb [hit[prio]]", descriptor_filename, line_index);
                                hitbox.damage = Double.parseDouble(fields[6]);
                                hitbox.angle = Integer.parseInt(fields[7]);
                                hitbox.base_knockback= Double.parseDouble(fields[8]);
                                hitbox.scaling_knockback = Double.parseDouble(fields[9]);
                                if (fields.length > 10){
                                    hitbox.hitID = Integer.parseInt(fields[10]);
                                    if (fields.length > 11) hitbox.priority = Integer.parseInt(fields[11]);
                                }
                                current_frame.entity_frame.addHitbox(hitbox);
                            }
                            break;
                            case Hitbox.GRAB_HITBOX_CODE:

                            break;
                            case Hitbox.WIND_HITBOX_CODE:
                            break;
                            case Hitbox.SPECIAL_HITBOX_CODE:
                            break;
                            default:
                            throw new RessourceException("Unsupported hitbox type : " + fields[5], descriptor_filename, line_index);
                        }
                    } catch (NumberFormatException e){
                        throw new RessourceException("One of the numeric values could not be parsed", descriptor_filename, line_index);
                    }

                    break;
                }
            }

            return anim;

        } catch (IOException e){
            throw new RessourceException("Couldn't descriptor file " + descriptor_filename, e);
        }
    }

    private void parseAnimation(GameData gd, String file, String info) throws RessourceException, WhatTheHellException{
        String[] fields;

        fields = split(info, " ");

        if (fields.length < 2){
            throw new RessourceException("Ill-formed animation file info, should contain a tag and infos separated by spaces : " + info);
        }
        
        if (fields[1].endsWith(".dat")){ //animation has a descriptor
            parseAnimationDescriptor(gd, fields[0], file, fields[1]);
        } else { //no
            EntityAnimation anim;
            try {
                anim = addAnimation(gd, fields[0], Integer.parseInt(fields[1]), file, null);
            } catch (NumberFormatException e){
                throw new RessourceException("File info second field is neither a descriptor filename of a valid number", e);
            }
            
            if (fields.length > 2){
                anim.setSpeed(Double.parseDouble(fields[2]));
                if (fields.length > 3){
                    if (fields[3] == "c" || fields[3] == "cbox all"){
                        for (int i = 0; i < anim.getNbFrames(); i++){
                            Frame frame; EntityFrame entity_frame;
                            try {
                                frame = anim.getFrame(i);
                                entity_frame = anim.getEntityFrame(i);
                            } catch (FrameOutOfBoundsException e){
                                throw new WhatTheHellException("Supposedly safe array iteration went out of bounds", e);
                            }
                            
                            fullFrameHurtbox(frame, entity_frame);
                        }
                    }
                }
            }
        }

    }

    private static final String listFilename = "project_db.txt";

    public GameData parseGameData() throws IOException, RessourceException, WhatTheHellException, InvalidRessourcePathException {
        BufferedReader reader;
        try {
            reader = fileReader(listFilename);
        } catch (NoSuchFileException e){
            throw new RessourceException("The specified directory is not a valid ressource path : does not contain a project_db.txt file.", e);
        }

        String file, info;
        String[] split;
        int line = 1;

        GameData gd = new GameData();

        while (reader.ready()){
            file = reader.readLine();
            info = reader.readLine();   

            if (info == null) throw new RessourceException("filename without file info in files list file (" + listFilename + ")");

            split = info.split(":");
            
            if (split.length != 2) throw new RessourceException("File info line should contain exactly 1 : separator", listFilename, line + 1);
            line += 2;

            switch (split[0]){
                case "L":
                case "I":
                case "X":
                    break;
                case "A":
                    parseAnimation(gd, file.trim(), split[1]);
            }
        }

        /*
        for (Champion c : gd){
            for (EntityAnimation anim : c){
                System.out.println(anim.getName());
                System.out.println(anim.getNbFrames());
                System.out.println(anim.getSpeed());
            }
        }
        */

        return gd;
    }

    public void saveAsArchive(List<Path> files, Path dest) throws IOException {

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(dest, symlinks_behavior, StandardOpenOption.CREATE))){
            for (Path file : files){
                if (file == null) continue;
                zipFile(file, zos);
            }
        }
    }

    private static final int READ_BUFFER = 4096;
    public void zipFile(Path path, ZipOutputStream zos) throws IOException {
        System.out.println(path);
        zos.putNextEntry(new ZipEntry(path.toString()));

        BufferedInputStream bis = new BufferedInputStream(fileStream(path));
        
        byte[] buffer = new byte[READ_BUFFER];
        int read;

        while ((read = bis.read(buffer)) != -1){
            zos.write(buffer, 0, read);
        }
        zos.closeEntry();
    }
}
