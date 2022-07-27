package UI;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardManager {

    private static Clipboard current_clipboard = null;

    private static void fetchClipboard(){
        if (current_clipboard == null) current_clipboard = 
            Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public static void setClipboardText(String text, ClipboardOwner owner){
        fetchClipboard();
        current_clipboard.setContents(new StringSelection(text), owner);
    }

    public static String getClipboardText(){
        fetchClipboard();
        Transferable data =  current_clipboard.getContents(null);

        if (data != null && data instanceof StringSelection){
            return ((StringSelection)data).toString();
        }

        return "";
    }
}
