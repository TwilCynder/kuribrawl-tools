package gamedata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTML.Tag;

import gamedata.exceptions.InvalidRessourcePathException;
import gamedata.exceptions.RessourceException;

public class RessourcePath {
    private Path path;
    private LinkOption symlinks_behavior;

    public RessourcePath(String path) throws InvalidRessourcePathException{
        try {
            this.path = Paths.get(path);
        } catch (InvalidPathException e){
            this.path = null;
            throw new InvalidRessourcePathException("Invalid path", e);
        }
        this.symlinks_behavior = LinkOption.NOFOLLOW_LINKS;
        if (!Files.exists(this.path, symlinks_behavior)){
            throw new InvalidRessourcePathException("Path does not exist");
        }

        if (!Files.isDirectory(this.path, symlinks_behavior)){
            throw new InvalidRessourcePathException("Path is not a directory");
        }
    }

    @Deprecated
    private InputStream fileStream(String name) throws IOException, InvalidPathException{
        return Files.newInputStream(path.resolve(name), symlinks_behavior);
    }

    private File getFile(String name) {
        return path.resolve(name).toFile();
    }

    private BufferedReader fileReader(String name) throws IOException, InvalidPathException {
        Path filepath = path.resolve(name);
        filepath = filepath.toAbsolutePath();

        return Files.newBufferedReader(filepath);
    }

    /**
     * Adds a new animation to the specified GameData based on basic information from the text files.  
     * Handles the interpretation of these informations (loading the given image file, parsing the tag, etc)
     */
    private EntityAnimation addAnimation(GameData gd, String tag, int nbFrames, String source_filename, String descriptor_filename) throws RessourceException{
        String[] tagSplit = tag.split("/");

        if (tagSplit.length != 2 && tagSplit[0] != ""){
            throw new RessourceException("Ill-formed animation file info : tag should contain 2 non-empty fields separated by \\ : (" + tag + ")");
        }

        try {
            Image source = ImageIO.read(getFile(source_filename));
            //System.out.println("RessourcePath.addAnimation" + source);

            return gd
            .tryChampion(tagSplit[0])
            .addAnimation(tagSplit[1], source, nbFrames, source_filename, descriptor_filename);
        } catch (IOException e){
            throw new RessourceException("Couldn't read source image file " + source_filename, e);
        }

    }

    private void parseAnimation(GameData gd, String file, String info) throws RessourceException{
        String[] fields;
        String val;

        fields = info.split(" ");

        if (fields.length < 2){
            throw new RessourceException("Ill-formed animation file info, should contain a tag and infos separated by spaces : " + info);
        }
        
        if (fields[1].endsWith(".dat")){

        } else {
            EntityAnimation anim = addAnimation(gd, fields[0], Integer.parseInt(fields[1]), file, null);
        }

    }

    private static final String listFilename = "project_db.txt";

    public GameData parseGameData() throws IOException, RessourceException {
        BufferedReader reader = fileReader(listFilename);


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

        

        return gd;
    }
}
