package KBUtil.ui;

import java.io.File;
import java.nio.file.Path;

public class RestrictedRootPathChooser  extends PathChooser {
    public RestrictedRootPathChooser(PathChooser.Mode mode, Path rootDirectory){
        super(mode, rootDirectory, new RestrictedRootFileSystemView(rootDirectory.toFile()));
    }

    public RestrictedRootPathChooser(PathChooser.Mode mode, Path initDirectory, File rootDirectory){
        super(mode, initDirectory, new RestrictedRootFileSystemView(rootDirectory));
    }

    public RestrictedRootPathChooser(PathChooser.Mode mode, Path initDirectory, File[] rootDirectories){
        super(mode, initDirectory, new RestrictedRootFileSystemView(rootDirectories));
    }
}
