package gamedata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private BufferedReader fileReader(String name) throws IOException, InvalidPathException {
        Path filepath = path.resolve(name);
        filepath = path.toAbsolutePath();
        System.out.println(filepath);

        return Files.newBufferedReader(filepath);
    }

    private void parseAnimation(GameData gd, String file, String info){
        
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
                    parseAnimation(gd, file, info);
            }
        }

        return gd;
    }
}
