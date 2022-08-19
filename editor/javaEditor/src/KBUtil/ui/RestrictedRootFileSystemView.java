package KBUtil.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

public class RestrictedRootFileSystemView extends FileSystemView{

    private final File[] rootDirectories;

    RestrictedRootFileSystemView(File rootDirectory)
    {
        this.rootDirectories = new File[] {rootDirectory};
    }

    RestrictedRootFileSystemView(File[] rootDirectories)
    {
        this.rootDirectories = rootDirectories;
    }

    @Override
    public File[] getRoots()
    {
        return rootDirectories;
    }

    @Override
    public boolean isRoot(File file)
    {
        System.out.println(file);
        for (File root : rootDirectories) {
            System.out.println(root);
            if (root.equals(file)) {
                return true;
            }
        }
        return super.isRoot(file);
    }
    
    @Override
    public File createNewFolder(File containingDir) throws IOException {
		File folder = new File(containingDir, "New Folder");
		folder.mkdir();
		return folder;
    }
    
}
