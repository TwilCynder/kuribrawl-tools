package UI.forms;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPanel;

import KBUtil.functional.Transform;
import KBUtil.ui.TwilList;
import KBUtil.ui.TwilListModel;
import UI.Window;

public class SelectBackupForm extends EditorForm {
    private static String title = "Select a backup";
    
    private Path directory;

    /**
     * Creates a Select Backup Form. Assumes path points to a directory.
     * @param frame
     * @param directory
     */
    public SelectBackupForm(Window frame, Path directory) {
        super(frame, title);
        this.directory = directory;

        init();
    }

    private static class BackupFile implements Comparable <BackupFile>{
        private Path p;
        private Date date;
        boolean latest;

        private BackupFile(Path path, long time_point){
            p = path;
            latest = true;
            date = new Date(time_point);
        }

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

        public void setLatest(){
            latest = true;
        }

        private static DateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");

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
            return date.compareTo(o.date);
        }

    }

    @Override
    protected Component initForm() {
        
        JPanel form = new JPanel();

        TwilListModel<BackupFile> list_model = new TwilListModel<>();
        try { 
            SortedSet<BackupFile> files_list = new TreeSet<>();
            for (Path p : Files.newDirectoryStream(directory)){
                files_list.add(BackupFile.newInstance(p));
            }
            files_list.last().setLatest();
            list_model.addAll(files_list);
        } catch (IOException ex){
            ex.printStackTrace();
        }

        TwilList<BackupFile> list = new TwilList<>(list_model);
        form.add(list);

        return form;

    }

    @Override
    protected boolean confirm() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'confirm'");
    }
    
}
