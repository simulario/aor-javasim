package aors.module.initialState.gui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

class JSliderTableEditor extends AbstractCellEditor implements TableCellEditor {

 
	private static final long serialVersionUID = -6172697509994071914L;
    protected JSlider slider = null;

  public JSliderTableEditor(int min, int max) {
    slider = new JSlider(min, max);
    // slider.setPaintLabels(true);
    // slider.setPaintTicks(true);
    // slider.setMajorTickSpacing((max-min)/100);
    // slider.setSize(115, 35);
  }

  public Component getTableCellEditorComponent(JTable table, Object value,
      boolean isSelected, int row, int column) {
    Integer val = (Integer) value;
    slider.setValue(val.intValue());
    return slider;
  }

  public Object getCellEditorValue() {
    return new Integer(slider.getValue());
  }
}
