package KBUtil.ui;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;

public class OpenPathRestrictedButton extends OpenPathButton {

    private Path initPath;
    private File[] rootPaths;

    private File[] convertPathArray(Path[] paths){
        File[] res = new File[paths.length];
        for (int i = 0; i < paths.length; i++){
            res[i] = paths[i].toFile();
        }
        return res;
    }

    public OpenPathRestrictedButton(Component parent, ChooserOpener opener, Path initDir, Path... roots) {
        super(parent, opener);
        this.initPath = initDir;
        this.rootPaths = convertPathArray(roots);
    }
    
    public OpenPathRestrictedButton(Component parent, Path initDir, Path... roots){
        super(parent);
        this.initPath = initDir;
        this.rootPaths = convertPathArray(roots);
    }

    public OpenPathRestrictedButton(Component parent, ChooserOpener opener, Path root) {
        this(parent, opener, root, root);
    }
    
    public OpenPathRestrictedButton(Component parent, Path root){
        this(parent, root, root);
    }

    @Override
    protected PathChooser initChooser() {
        return new RestrictedRootPathChooser(PathChooser.Mode.FILE, initPath, rootPaths);
    }
}
