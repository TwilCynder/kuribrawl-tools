package gamedata;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import java.awt.Image;

import gamedata.exceptions.FrameOutOfBoundsException;
import gamedata.exceptions.InvalidDatafileException;

public class DataFile implements AutoCloseable {
    private String origin = "Unknown origin";
    private ImageInputStream input;

    public DataFile(String filename) throws FileNotFoundException, IOException {
        this (new FileInputStream(filename));
        this.origin = filename;
    }

    public DataFile(InputStream obj) throws IOException {
        input = ImageIO.createImageInputStream(obj);
        input.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }

    public void error(String message) throws InvalidDatafileException {
        throw new InvalidDatafileException(origin, message);
    }

    public void errorAt(String message, long pos) throws InvalidDatafileException {
        throw new InvalidDatafileException(origin, pos, message);
    }

    public void error(String message, long offset) throws InvalidDatafileException, IOException {
        throw new InvalidDatafileException(origin, input.getStreamPosition() - offset, message);
    }

    public static void printHex(int val){
        System.out.println(String.format("%x", val));
    }

    public static void printHex(byte val){
        System.out.println(String.format("%x", val));
    }

    public static boolean getBitBool(byte val, int shift){
        return ((val & (1 << shift) )  >> shift ) == 1;
    }

    public void checkSignature() throws InvalidDatafileException, IOException{
        for (int i = 0; i < 4; i++){
            if (input.readByte() != 0x54){
                throw new InvalidDatafileException(origin, "Invalid signature, most likely not a Kuribrawl data file");
            };
        }
    }

    public void readVersion() throws IOException{
        System.out.println("Version : " + input.readByte() + "." + input.readByte() + "." + input.readByte());
    }

    public Image readImage(int length) throws IOException{
        //I WILL NEVER FORGIVE YOU FOR THIS JAVA
        //NEVER
        //FUCK YOU
        
        byte[] raw = new byte[length];
        input.read(raw, 0, length);
        input.setByteOrder(ByteOrder.BIG_ENDIAN);
        Image img = ImageIO.read(new ByteArrayInputStream(raw));
        input.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        //java is a fucking joke
        //System.out.println(img);
        return img;
    }

    public EntityAnimation readAnimation(GameData data, String tag) throws IOException, InvalidDatafileException{
        byte byteVal; short shortVal; int intVal; double doubleVal;
        Frame current_frame = null; EntityFrame current_entity_frame = null; int current_frame_id = 0;
        String[] res = tag.split("/");
        if (res.length < 2) throw new InvalidDatafileException(origin, "Animations tag does not contain a \\");
        String entity = res[0];
        String element = res[1];

        int length = input.readInt();
        Image source = readImage(length);

        byteVal = input.readByte();
        if (byteVal != DatafileMarkers.DESCRIPTORSTART){
            error("Animation source image data should always be followed by a DESCRIPTORSTART marker (0x" + String.format("%x", DatafileMarkers.DESCRIPTORSTART) + ")", 
            input.getStreamPosition() - 1);
        }

        EntityAnimation anim = data.tryChampion(entity).addAnimation(element, input.readByte(), source);
        
        System.out.println(anim.getNbFrames());

        boolean quit = false;
        while (!quit){
            switch (input.readByte()){
                case DatafileMarkers.ANIMSPEED:
                    doubleVal = input.readDouble(); 
                    System.out.println(doubleVal);
                    quit = true;
                    break;
                case DatafileMarkers.FRAMEINFO:
                    current_frame_id = input.readByte();
                    try {
                        current_frame = anim.getFrame(current_frame_id);
                        current_entity_frame = anim.getEntityFrame(current_frame_id);
                    } catch (FrameOutOfBoundsException e){
                        error("Frame Info data refers to out of bounds frame", -1);
                    }
                case DatafileMarkers.FRAMEDURATION:
                    if (current_frame == null) error("Frame duration data found before any Frame info", -1);
                    shortVal = input.readShort();
                    if (shortVal < 1) error("Frame duration is negative or null", -2);
                    current_frame.duration = shortVal;
                case DatafileMarkers.FRAMEMOVEMENT:
                    if (current_entity_frame == null) error("Frame movement data found before any Frame info", 0);
                    byteVal = input.readByte();
                    current_entity_frame.movement.x.enabled = getBitBool(byteVal, 0);
                    current_entity_frame.movement.x.set_speed = getBitBool(byteVal, 1);
                    current_entity_frame.movement.x.whole_frame = getBitBool(byteVal, 2);
                    current_entity_frame.movement.y.enabled = getBitBool(byteVal, 3);
                    current_entity_frame.movement.y.set_speed = getBitBool(byteVal, 4);
                    current_entity_frame.movement.y.whole_frame = getBitBool(byteVal, 5);
                    
                    current_entity_frame.movement.x.value = input.readDouble();
                    current_entity_frame.movement.y.value = input.readDouble();
                    break;
                case DatafileMarkers.HURTBOXINFO:
                    if (current_entity_frame == null) error("Hurtbox data found before any Frame info", 0);
                    if (input.readShort() == DatafileMarkers.MAX_VAL_SHORT){
                        current_entity_frame.hurtboxes.add(new Hurtbox(
                            -(current_frame.origin.x), current_frame.origin.y, current_frame.display.w, current_frame.display.h
                        ));
                    }
                    
                    
                case DatafileMarkers.INTERFILE:
                    quit = true;
            }
        }
        
        return anim;
        
    }


    public GameData read(GameData gamedata) throws IOException, InvalidDatafileException{
        String tag;

        checkSignature();
        readVersion();

        while (true){
            byte type = input.readByte();
            if (type == -1) break;
            tag = input.readLine();
            switch (type){
                case DatafileMarkers.TYPE_ANIMATION:
                readAnimation(gamedata, tag);
                return null;
            }

        }

        return gamedata;
    }

    public void close() throws IOException{
        if (input != null){
            input.close();
        }
    }
}
