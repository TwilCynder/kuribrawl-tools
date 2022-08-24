package KBUtil.ui;

import java.nio.file.Path;
import java.awt.Component;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JFileChooser;

/**
 * JFileChooser working with the Path API instead of the old File one.
 */
public class PathChooser extends JFileChooser {
    public enum Mode{
        FILE,
        DIRECTORY;

        public int toInt(){
            return (this == DIRECTORY) ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY;
        }
    }

    /**
     * Returns the specified Path as a File, or null if the argument is null
     */
    protected static File pathToFile(Path path){
        return path == null ? null : path.toFile();
    }

    /**
     * Constructs a new PathChooser starting at the specified path (in the form of a pathname string)
     * @param mode
     * @param directory the pathname of the starting directory
     */
    public PathChooser(Mode mode, String directory){
        super(directory);
        setFileSelectionMode(mode.toInt());
    }

    /**
     * Constructs a new PathChooser starting at the specified path
     * @param mode
     * @param path starting directory (can be null)
     */
    public PathChooser(Mode mode, Path path){
        super (pathToFile(path));
        setFileSelectionMode(mode.toInt());
    }

    /**
     * Constructs a new PathChooser starting at the specified path and using a specified FileSystemView.  
     * A custom FileSystemView allow to override the way the chooser interacts with the file system
     * @param mode
     * @param path starting directory (can be null)
     * @param fsv the FileSystemView used by this chooser
     */
    public PathChooser(Mode mode, Path path, FileSystemView fsv){
        super (pathToFile(path), fsv);
        setFileSelectionMode(mode.toInt());
    }

    public PathChooser(Mode mode){
        this();
        setFileSelectionMode(mode.toInt()); 
    }

    public PathChooser(){
        super();
    }

    /**
     * Adds multiple FileFilter's to the chooser
     * @param filters
     */
    public void addChoosableFileFilters(FileFilter... filters){
        for (FileFilter filter : filters){
            addChoosableFileFilter(filter);
        }
    }

    /**
     * Return the selected path OR NULL depending on the given integer result code
     */
    public Path returnAsPath(int result){
        if (result != JFileChooser.APPROVE_OPTION) return null;

        File selected = getSelectedFile();
        if (selected == null) return null;

        return selected.toPath();
    }

    /**
     * Shows this dialog in open mode (select an existing file or directory) and return the selected Path (if any, null otherwise)
     * @param parent component the chooser will be centered on
     * @return
     */
    public Path openPath(Component parent){
        int result = showOpenDialog(parent);
        return returnAsPath(result);
    }

    /**
     * Shows this dialog in save mode (select any file or directory, existing or not) and return the selected Path (if any, null otherwise)
     * @param parent component the chooser will be centered on
     * @return
     */
    public Path savePath(Component parent){
        int result = showSaveDialog(parent);
        return returnAsPath(result);
    }

    /**
     * Creates a chooser and initializes it based on the arguments
     * @param mode 
     * @param initPath path of the starting directory (can be null)
     * @param filters filters to be added to the chooser
     * @return
     */
    protected static PathChooser createChooser(Mode mode, Path initPath, FileFilter... filters){
        PathChooser chooser = new PathChooser(mode, initPath);
        chooser.addChoosableFileFilters(filters);
        return chooser;
    }

    /**
     * Shows a new PathChooser in open mode
     * @param parent
     * @param mode
     * @param initPath path of the starting directory (can be null)
     * @param filters filters to be added to the chooser
     * @return Selected Path or null
     */
    public static Path showOpenDialog(Component parent, Mode mode, Path initPath, FileFilter... filters){
        return createChooser(mode, initPath, filters).openPath(parent);
    }

    /**
     * Shows a new PathChooser in save mode
     * @param parent
     * @param mode
     * @param initPath path of the starting directory (can be null)
     * @param filters filters to be added to the chooser
     * @return Selected Path or null
     */
    public static Path showSaveDialog(Component parent, Mode mode, Path initPath, FileFilter... filters){
        return createChooser(mode, initPath, filters).savePath(parent);
    }

}
