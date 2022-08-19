package KBUtil.ui;

import java.nio.file.Path;

public class RestrictedRootPathChooser  extends PathChooser {
    public RestrictedRootPathChooser(PathChooser.Mode mode, Path rootDirectory){
        super(mode, rootDirectory, new RestrictedRootFileSystemView(rootDirectory.toFile()));
    }
}
