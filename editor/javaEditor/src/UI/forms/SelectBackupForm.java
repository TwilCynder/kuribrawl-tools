package UI.forms;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import KBUtil.ui.TwilList;
import KBUtil.ui.TwilListModel;
import UI.Window;
import gamedata.RessourcePath;

/**
 * Form displaying all backups in a resouce path and restoring the one that is selected.
 */
public class SelectBackupForm extends EditorForm {
    private static String title = "Select a backup";
    private static int list_padding = 5;

    private TwilList<BackupFile> list;
    private RessourcePath rPath;

    public enum State {
        OK,
        NO_BACKUPS,
        COULDNT_ACESS_BACKUPS
    }

    private State state = State.OK;

    /**
     * Creates a Select Backup Form. Assumes path points to a directory.
     * @param frame the window this form belongs to. Is it a UI.Window or a AWT window ? I don't care !
     * @param rPath the resource path (see the doc of the class)
     */
    public SelectBackupForm(Window frame, RessourcePath rPath) {
        super(frame, title);
        this.rPath = rPath;

        init();
    }

    /**
     * Returns the state of the form, which can be interpreted as the result of its initialization.
     * Anything other than OK means something went wrong and the form shouldn't be displayed.
     * @return see above
     */
    public State getState(){
        return state;
    }

    /**
     * Represents a Backup. Each backup is just a file really, but this class stores various useful calculated info, and allows for a custom toString.
     */
    private static class BackupFile implements Comparable <BackupFile>{
        private Path p;
        private Date date;
        boolean latest;

        private BackupFile(Path path, long time_point){
            p = path;
            latest = false;
            date = new Date(time_point);
        }

        /**
         * Creates a new BackupFile, calculating the time_point from the name og the file.
         * @param p the actual NIO file.
         * @return the new instance/
         */
        public static BackupFile newInstance(Path p){
            try {
                String time_string = p.getFileName().toString().split("\\.")[0];
                long time_point = Long.parseLong(time_string);
                return new BackupFile(p, time_point);
            } catch (NumberFormatException ex){
                return null;
            }

        }

        public Path getPath(){
            return p;
        }

        /**
         * Sets this BackupFile as the lastest one (which shows in its list entry)
         */
        public void setLatest(){
            latest = true;
        }

        private static DateFormat date_format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        @Override
        public String toString() {
            String res = date_format.format(date);
            if (latest){
                res += " (latest)";
            }
            return res;
        }

        @Override
        public int compareTo(BackupFile o) {
            return -date.compareTo(o.date);
        }

    }

    /**
     * Creates a list model with all the backup files contained in this.rPath.
     * @return see above.
     */
    private TwilListModel<BackupFile> makeListModel(){
        TwilListModel<BackupFile> list_model = new TwilListModel<>();
        try { 
            SortedSet<BackupFile> files_list = new TreeSet<>();

            Path directory = rPath.resolvePath("backup");

            if (!Files.exists(directory)){
                state = State.NO_BACKUPS;
                return list_model;
            }

            for (Path p : Files.newDirectoryStream(directory)){
                files_list.add(BackupFile.newInstance(p));
            }

            if (files_list.isEmpty()){
                state = State.NO_BACKUPS;
                return null;
            }

            files_list.first().setLatest();
            list_model.addAll(files_list);
        } catch (IOException ex){
            state = State.COULDNT_ACESS_BACKUPS;
            ex.printStackTrace();
        }

        return list_model;
    }

    @Override
    protected Component initForm() {
        
        JPanel form = new JPanel();

        list = new TwilList<>(makeListModel());
        list.setBorder(new EmptyBorder(list_padding, list_padding, list_padding, list_padding));
        form.add(list);
        System.out.println("Widht : " + form.getSize().width);

        return form;

    }

    @Override
    protected boolean confirm() {
        BackupFile p = list.getSelectedValue();
        
        if (p == null){
            return false;
        } else {
            rPath.restoreArchive(p.getPath());
            return true;
        }

    }
}
