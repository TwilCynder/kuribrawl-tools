package UI;

import java.nio.file.Path;
import java.awt.Component;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
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

    public PathChooser(Mode mode, Path path){
        super (path == null ? null : path.toFile());
        setFileSelectionMode(mode.toInt());
    }

    public PathChooser(Mode mode, Path path, FileSystemView fsv){
        super (path == null ? null : path.toFile(), fsv);
        setFileSelectionMode(mode.toInt());
    }

    public PathChooser(Mode mode){
        this();
        setFileSelectionMode(mode.toInt()); 
    }

    public PathChooser(){
        super();
    }

    public void addFileFilters(FileFilter... filters){
        for (FileFilter filter : filters){
            addChoosableFileFilter(filter);
        }
    }

    public Path returnAsPath(int result){
        if (result != JFileChooser.APPROVE_OPTION) return null;

        File selected = getSelectedFile();
        if (selected == null) return null;

        return selected.toPath();
    }

    public Path openPath(Component parent){
        int result = showOpenDialog(parent);
        return returnAsPath(result);
    }

    public Path savePath(Component parent){
        int result = showSaveDialog(parent);
        return returnAsPath(result);
    }
}
