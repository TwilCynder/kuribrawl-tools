package KBUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class PathHelper {
   public static String getExtenstion(Path path){
        String fileName = path.toString();
        int index = fileName.lastIndexOf('.');
        if(index > 0) {
            String extension = fileName.substring(index + 1);
            return extension;
        }
        return null;
    } 

    public static String getExtenstion(File path){
        String fileName = path.toString();
        int index = fileName.lastIndexOf('.');
        if(index > 0) {
            String extension = fileName.substring(index + 1);
            return extension;
        }
        return null;
    } 

    public static Path stringToPathOrNull(String pathname) throws InvalidPathException {
        return (pathname == null) ? null : Paths.get(pathname);
    }

    public static String pathToStringOrNull(Path path){
        return (path == null) ? null : path.toString();
    }

    
    /**
     * Returns whether the given string is a valid pathname.
     * 
     * Note that this method consistently returns false if Paths.get would throw an exception, and vice versa. 
     * @param name
     * @return
     */
    public static boolean isValidPathName(String name){
        try {
            Paths.get(name);
        } catch (InvalidPathException ex){
            return false;
        }
        return true;
    }

    public static void createDirsForFile(Path path) throws IOException {
        Files.createDirectories(path.getParent());
    }
    public static void move(Path source, Path target, CopyOption... options) throws IOException {
        createDirsForFile(target);
        Files.move(source, target, options);
    }

    /**
     * NO WINDOWS I DONT WANT YOUR FUCKASS \ IN FILEPATHS BE NORMAL
     * @param path
     * @return
     */
    public static String normalizePath(String path) {
        return path.replace('\\', '/');
    }
}