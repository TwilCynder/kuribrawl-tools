package UI;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import gamedata.exceptions.WhatTheHellException;

public abstract class ClipboardManager {

    private static Clipboard current_clipboard = null;

    private static void fetchClipboard(){
        if (current_clipboard == null) current_clipboard = 
            Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public static void setClipboardText(String text, ClipboardOwner owner){
        fetchClipboard();
        current_clipboard.setContents(new StringSelection(text), owner);
    }

    public static String getClipboardText() throws WhatTheHellException {
        fetchClipboard();

        try {
            Object data = current_clipboard.getData(DataFlavor.stringFlavor);

            if (data != null && data instanceof String){
                return (String)data;
            }

        } catch (UnsupportedFlavorException ex) {
            throw new WhatTheHellException("So apparently the string flavor is not supported ?", ex);
        } catch (IOException ex){
            return "";
        }


        return "";
    }
}
