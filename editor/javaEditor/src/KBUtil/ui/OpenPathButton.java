package KBUtil.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public abstract class OpenPathButton extends JButton {
    public static interface ChooserOpener {
        public Path openChooser(PathChooser chooser, Component parent);
    }

    public static final ChooserOpener Open = new ChooserOpener() {
        @Override
        public Path openChooser(PathChooser chooser, Component parent) {
            return chooser.openPath(parent);
        }
    };

    public static final ChooserOpener Save = new ChooserOpener() {
        @Override
        public Path openChooser(PathChooser chooser, Component parent) {
            return chooser.savePath(parent);
        }
    };

    public static interface SelectionListener {
        public void pathSelected(Path selected);
    }

    public static interface ChooserPreparator {
        public void prepareChooser(PathChooser chooser);
    }

    private List<SelectionListener> listeners = new LinkedList<>();
    private List<ChooserPreparator> preparators = new LinkedList<>();
    protected Component parent;
    private ChooserOpener opener;

    public OpenPathButton(Component parent, ChooserOpener opener){
        this.parent = parent;
        this.opener = opener;
        setIcon(UIManager.getIcon("FileView.directoryIcon"));

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path p = showChooser();
                if (p != null){
                    callListeners(p);
                }
            }
        });
    }

    public OpenPathButton(Component parent){
        this(parent, Open);
    }

    public void setChooserParent(Component parent) {
        this.parent = parent;
    }

    protected abstract PathChooser initChooser();

    private void prepareChooser(PathChooser chooser){
        for (ChooserPreparator preparator : preparators){
            preparator.prepareChooser(chooser);
        }
    }

    private Path showChooser(){
        PathChooser chooser = initChooser();
        prepareChooser(chooser);
        return opener.openChooser(chooser, parent);
    }

    private void callListeners(Path path){
        for (var listener : listeners){
            listener.pathSelected(path);
        }
    }
    public void addSelectionListener(SelectionListener listener){
        listeners.add(listener);
    }

    public void addChooserPreparator(ChooserPreparator prep){
        preparators.add(prep);
    }

    public static class FiltersPreparator implements ChooserPreparator {
        FileFilter[] filters;

        public FiltersPreparator(FileFilter... filters) {
            this.filters = filters;
        }

        @Override
        public void prepareChooser(PathChooser chooser) {
               chooser.addChoosableFileFilters(filters);
        }
    }

    public void addChoosableFileFilters(FileFilter... filters){
        addChooserPreparator(new FiltersPreparator(filters));
    }

    public void setAcceptAllFileFilterUsed(boolean b){
        addChooserPreparator(new ChooserPreparator() {
            @Override
            public void prepareChooser(PathChooser chooser) {
                chooser.setAcceptAllFileFilterUsed(b);
            }
        });
    }
}
