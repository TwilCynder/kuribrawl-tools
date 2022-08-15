package KBUtil;

import java.io.File;
import java.nio.file.Path;

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
}