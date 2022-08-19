package KBUtil.ui;

public class MapComboBoxItem <Val, Disp> {
    private Val value;
    private Disp display;

    public MapComboBoxItem(Val v, Disp d){
        value = v;
        display = d;
    }

    public Val getValue(){
        return value;
    }

    @Override
    public String toString(){
        return display.toString();
    }
}
