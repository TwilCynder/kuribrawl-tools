package UI;

import java.nio.file.Path;
import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

public class PathChooser extends JFileChooser {
    public enum Mode{
        FILE,
        DIRECTORY;

        public int toInt(){
            return (this == DIRECTORY) ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY;
        }
    }

    public PathChooser(Mode mode, String directory){
        super(directory);
        setFileSelectionMode(mode.toInt());
    }

    public PathChooser(Mode mode){
        super();
        setFileSelectionMode(mode.toInt()); 
    }

    public Path showDialog(Component parent){
        int result = showOpenDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return null;

        File selected = getSelectedFile();
        if (selected == null) return null;

        return selected.toPath();
    }
}
