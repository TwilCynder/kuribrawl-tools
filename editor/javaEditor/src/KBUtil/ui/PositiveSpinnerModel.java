package KBUtil.ui;

import javax.swing.SpinnerNumberModel;

public class PositiveSpinnerModel extends SpinnerNumberModel {
    public PositiveSpinnerModel(){
        super(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1));
    }
}
