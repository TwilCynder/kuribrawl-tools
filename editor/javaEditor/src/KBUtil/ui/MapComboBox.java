package KBUtil.ui;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;

public class MapComboBox <Val, Disp> extends JComboBox<MapComboBoxItem<Val, Disp>> {
    private Map<Val, MapComboBoxItem<Val,Disp>> itemsMap = new TreeMap<>();
    public MapComboBox(Map<Val, Disp> map){
        for (var entry : map.entrySet()){
            MapComboBoxItem<Val, Disp> item = new MapComboBoxItem<>(entry.getKey(), entry.getValue());
            itemsMap.put(entry.getKey(), item);
            addItem(item);
        }
    }

    public Object getSelectedValue(){
        Object selectedItem = getSelectedItem();
        if (selectedItem == null) return null;
        if (selectedItem instanceof MapComboBoxItem){
            MapComboBoxItem<?, ?> mcbi = (MapComboBoxItem<?, ?>)selectedItem;
            return mcbi.getValue();
        } else {
            throw new IllegalStateException("MapComboBox has an item that is not a MapComboBoxItem");
        }
    }

    public void setSelectedValue(Object value){
        setSelectedItem(itemsMap.get(value));
    } 
}
