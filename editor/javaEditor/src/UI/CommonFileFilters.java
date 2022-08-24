package UI;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public abstract class CommonFileFilters {
    public static final FileFilter datFilter = new FileNameExtensionFilter("Descriptor files", "dat");
    public static final FileFilter pngFilter = new FileNameExtensionFilter("PNG Image files", "png");
}
