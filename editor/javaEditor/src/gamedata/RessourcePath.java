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

    private BufferedReader fileReader(String name) throws IOException, InvalidPathException, RessourceException {
        Path filepath = path.resolve(name);
        filepath = path.toAbsolutePath();
        System.out.println(filepath);
        if (!Files.exists(filepath, symlinks_behavior) || !Files.isRegularFile(path, symlinks_behavior)){
            throw new RessourceException("File " + name + " is a directory or does not exist.");
        }
        return new BufferedReader(new FileReader(filepath.toString()));
    }

    public GameData parseGameData() throws IOException, RessourceException{
        BufferedReader reader = fileReader("project_db.txt");

        String file, info, field;

        while (reader.ready()){
            file = reader.readLine();
            info = reader.readLine();

            
        }



        GameData gd = new GameData();



        return gd;
    }
}
