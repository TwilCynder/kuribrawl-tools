package UI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import UI.OpenPathButton.SelectionListener;
import gamedata.Champion;
import gamedata.EntityAnimation;
import gamedata.GameData;
import gamedata.RessourcePath;
import gamedata.exceptions.RessourceException;

class NewAnimationForm extends Form {
    private class ChampionCellRenderer extends JLabel implements ListCellRenderer<Champion> {

        @Override
        public Component getListCellRendererComponent(JList<? extends Champion> list, Champion value, int index,
                boolean isSelected, boolean cellHasFocus) {
            setText("  " + value.getDislayName());
            setHorizontalAlignment(SwingConstants.LEADING);
            return this;
        }
    }

    private JTextField animationName;
    private JTextField sourceImageFile;
    private JTextField descriptorFile;
    private JComboBox<Champion> championList;
    private IntegerSpinner nbFramesSpinner;

    private Action openExplorerSourceImageFileAction;
    private Action openExplorerDescriptorFileAction;
    private SelectionListener sourceImageSelectionListener;

    private RessourcePath currentRessourcePath;
    private GameData currentData;
    private Path currentPath;
    private Window editor;

    public NewAnimationForm(Window editor) throws IllegalStateException {
        super(editor, "New animation");
        
        currentRessourcePath = editor.getCurrentRessourcePath();
        if (editor.getCurrentRessourcePath() == null) throw new IllegalStateException("A new animation form was opened with no current ressource path");
        currentPath = currentRessourcePath.getPath();

        currentData = editor.getCurrentData();
        if (editor.getCurrentRessourcePath() == null) throw new IllegalStateException("A new animation form was opened with no current ressource path");

        this.editor = editor;

        init();
    }

    private void initActions(){
        FileFilter datFilter = new FileNameExtensionFilter("Descriptor files", "dat");
        FileFilter pngFilter = new FileNameExtensionFilter("PNG Image files", "png");

        sourceImageSelectionListener = new SelectionListener() {
            @Override
            public void pathSelected(Path selected) {
                Path relativePath = currentPath.relativize(selected);
                sourceImageFile.setText(relativePath.toString());
            }
        };

        openExplorerSourceImageFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RestrictedRootPathChooser chooser = new RestrictedRootPathChooser(PathChooser.Mode.FILE, currentPath);
                chooser.setFileFilter(pngFilter);
                Path selected = chooser.openPath(NewAnimationForm.this);

                if (selected == null) return;

                Path relativePath = currentPath.relativize(selected);
                sourceImageFile.setText(relativePath.toString());
            }
        };

        openExplorerDescriptorFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RestrictedRootPathChooser chooser = new RestrictedRootPathChooser(PathChooser.Mode.FILE, currentPath);
                chooser.setFileFilter(datFilter);
                Path selected = chooser.savePath(NewAnimationForm.this);

                if (selected == null) return;

                Path relativePath = currentPath.relativize(selected);
                descriptorFile.setText(relativePath.toString());
            }
        };
    }

    private void fillFields(){
        Interactable i = editor.getCurrentEditor();
        if (i != null){
            if (i instanceof EntityAnimationEditor){
                EntityAnimationEditor editor = (EntityAnimationEditor)i;
                EntityAnimation anim = editor.getAnimation();
                Champion current_champion = currentData.getEntityAnimationOwner(anim);

                championList.setSelectedItem(current_champion);
            }
        }

        nbFramesSpinner.setValue(1);
    }

    private void createLayout(JPanel panel){
        initActions();

        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("default:grow"),
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
        }));

        JLabel label = new JLabel("Champion");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, "2, 2");

        label = new JLabel("Animation Name");
        panel.add(label, "4, 2");

        label = new JLabel("/");
        panel.add(label, "3, 2");

        Collection<Champion> champions = currentData.getChampions();
        championList = new JComboBox<Champion>(champions.toArray(new Champion[champions.size()]));
        championList.setRenderer(new ChampionCellRenderer());

        panel.add(championList, "2, 4, fill, default");

        label = new JLabel("/");
        panel.add(label, "3, 4, right, default");

        animationName = new JTextField();
        panel.add(animationName, "4, 4, fill, default");
        animationName.setColumns(40);

        label = new JLabel("Source Image ");
        panel.add(label, "2, 6, right, default");

        sourceImageFile = new JTextField();
        panel.add(sourceImageFile, "4, 6, fill, default");
        sourceImageFile.setColumns(10);

        JButton openExplorerSourceImageFile = new JButton(openExplorerSourceImageFileAction);
        openExplorerSourceImageFile.setPreferredSize(new Dimension(25, 22));
        openExplorerSourceImageFile.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        panel.add(openExplorerSourceImageFile, "5, 6");

        label = new JLabel("Descriptor file");
        panel.add(label, "2, 8, right, default");

        descriptorFile = new JTextField();
        panel.add(descriptorFile, "4, 8, fill, default");
        descriptorFile.setColumns(10);

        JButton openExplorerDescriptorFile = new JButton(openExplorerDescriptorFileAction);
        openExplorerDescriptorFile.setPreferredSize(new Dimension(25, 22));
        openExplorerDescriptorFile.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        panel.add(openExplorerDescriptorFile, "5, 8");

        label = new JLabel("Number of frames");
        panel.add(label, "2, 10, right, default");

        nbFramesSpinner = new IntegerSpinner();
        nbFramesSpinner.setColumns(3);
        panel.add(nbFramesSpinner, "4, 10, left, default");

        panel.add(new JPanel(), "2, 12, left, default");
    }

    @Override
    protected Component initForm(){
        JPanel panel = new JPanel();

        createLayout(panel);
        fillFields();

        return panel;
    }

    @Override
    protected boolean confirm(){
        //checking if the content of the fields are correct, if not, returning false will cancel the confirmation.
        Champion champion = (Champion)championList.getSelectedItem();
        if (champion == null) {
            JOptionPane.showMessageDialog(editor, "No champion selected", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String animName = animationName.getText();
        if (animName.isEmpty()){
            JOptionPane.showMessageDialog(editor, "Animation name cannot be empty", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //TODO : do better than just a on-confirm check for this
        if (!animName.matches("\\w+")){
            JOptionPane.showMessageDialog(editor, "Animation name can only contain alphanumeric characters and underscores", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        String sourceImageFilename = sourceImageFile.getText();
        if (sourceImageFilename.isEmpty()){
            JOptionPane.showMessageDialog(editor, "You must select a source image", "Inane error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String descriptorFilename = descriptorFile.getText();
        if (descriptorFilename.isEmpty()){
            int res = JOptionPane.showOptionDialog(editor, 
            "If you don't specify a descriptor file, you will have to do it later if the animation does \nnot follow the default elements configuration. Proceed ?",
            "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

            if (res == JOptionPane.CANCEL_OPTION) return false;
        }
        int nbFrames = nbFramesSpinner.getValueInt();

        System.out.println("Confirm : animation name : " + animationName.getText());
        System.out.println("Confirm : source image file : " + sourceImageFile.getText());
        System.out.println("Confirm : descriptor file : " + descriptorFile.getText());
        System.out.println("Confirm : champion " + championList.getSelectedItem());
        System.out.println("Confirm : nb frames " + nbFrames);

        if (currentRessourcePath == null) throw new IllegalStateException("Cannot create a new animation with no current ressource path");

        try {
            currentRessourcePath.addAnimation(champion, animName, nbFrames, sourceImageFilename, descriptorFilename);
            editor.updateAnimations();
            currentData.printAnimations();
        } catch (RessourceException ex){
            JOptionPane.showMessageDialog(editor, "Error while creating the animation : \n" + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }
}
