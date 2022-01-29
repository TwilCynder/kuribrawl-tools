package gamedata;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import gamedata.exceptions.InvalidDatafileException;
import javafx.util.Pair;

public class DataFile extends DataInputStream {
    private String origin;

    public DataFile(String filename) throws FileNotFoundException {
        this (new FileInputStream(filename));
        this.origin = filename;
    }

    public DataFile(InputStream in){
        super (in);
    }

    public void checkSignature() throws InvalidDatafileException, IOException{
        for (int i = 0; i < 4; i++){
            if (readByte() != 0x54){
                throw new InvalidDatafileException(origin, "Invalid signature, most likely not a Kuribrawl data file");
            };
        }
    }

    public void readVersion() throws IOException{
        System.out.println("Version : " + readByte() + "." + readByte() + "." + readByte());
    }

    public void readAnimation(String name) throws IOException{
        
    }


    public GameData read(GameData gamedata) throws IOException, InvalidDatafileException{
        String tag, entity, element;

        checkSignature();
        readVersion();

        while (available() > 0){
            byte type = readByte();
            tag = readLine();
            switch (type){
                case DatafileMarkers.TYPE_ANIMATION:
                String[] res = tag.split("\\");
                if (res.length < 2) throw new InvalidDatafileException(origin, "Animations tag does not contain a \\");
                entity = res[0];
                element = res[1];
                readAnimation(gamedata.tryChampion(entity).addAnimation(element, readByte()));
                return null;
            }

        }

        return gamedata;
    }
}
