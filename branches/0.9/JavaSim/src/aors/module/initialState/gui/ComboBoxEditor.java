package aors.module.initialState.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/*We will use this class to render enumeration value
when user click the JComboBox*/
public class ComboBoxEditor extends DefaultCellEditor {

  
	private static final long serialVersionUID = 1771422301703392768L;

    public ComboBoxEditor(String[] items) {

    super(new JComboBox(items));
  }

}
