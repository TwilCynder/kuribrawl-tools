package KBUtil.ui;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;

import javax.swing.filechooser.FileFilter;

public class RestrictedRootPathChooser  extends PathChooser {
    public RestrictedRootPathChooser(PathChooser.Mode mode, Path rootDirectory){
        super(mode, rootDirectory, new RestrictedRootFileSystemView(pathToFile(rootDirectory)));
    }

    public RestrictedRootPathChooser(PathChooser.Mode mode, Path initDirectory, File rootDirectory){
        super(mode, initDirectory, new RestrictedRootFileSystemView(rootDirectory));
    }

    public RestrictedRootPathChooser(PathChooser.Mode mode, Path initDirectory, File... rootDirectories){
        super(mode, initDirectory, new RestrictedRootFileSystemView(rootDirectories));
    }

    protected static PathChooser createChooser(Mode mode, Path initPath, FileFilter... filters){
        System.out.println("override");
        PathChooser chooser = new RestrictedRootPathChooser(mode, initPath);
        chooser.addChoosableFileFilters(filters);
        return chooser;
    }

    public static Path showOpenDialog(Component parent, Mode mode, Path initPath, FileFilter... filters){
        return createChooser(mode, initPath, filters).openPath(parent);
    }

    public static Path showSaveDialog(Component parent, Mode mode, Path initPath, FileFilter... filters){
        return createChooser(mode, initPath, filters).savePath(parent);
    }
}
