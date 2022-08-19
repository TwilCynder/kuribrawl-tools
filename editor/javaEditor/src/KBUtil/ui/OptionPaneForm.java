package KBUtil.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

public abstract class OptionPaneForm {
    Component form;
    String title = "";

    int optionType = JOptionPane.OK_CANCEL_OPTION;

    public OptionPaneForm(){
        this(null);
    }

    public OptionPaneForm(String title_){
        title = title_;
        initContent();
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOptionType() {
        return optionType;
    }

    public void setOptionType(int optionType) {
        this.optionType = optionType;
    }

    protected abstract void initContent();

    public int show(Component parentComponent, String title){
        int result = JOptionPane.showConfirmDialog(parentComponent, form, title, optionType);
        if (result == JOptionPane.OK_OPTION){
            confirm();
        }

        return result;
    }

    public int show(Component parentComponent){
        return show(parentComponent, title);
    }

    protected abstract void confirm();

    public Component getForm() {
        return form;
    }

    public void setForm(Component form) {
        this.form = form;
    }
}
