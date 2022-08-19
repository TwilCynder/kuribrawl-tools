package KBUtil.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.UIManager;

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

    private List<SelectionListener> listeners = new LinkedList<>();
    protected Component parent;
    private ChooserOpener opener;

    public OpenPathButton(Component parent, ChooserOpener opener){
        this.parent = parent;
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

    private Path showChooser(){
        PathChooser chooser = initChooser();
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
}
