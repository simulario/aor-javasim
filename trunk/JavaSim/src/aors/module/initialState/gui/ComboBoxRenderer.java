package aors.module.initialState.gui;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ComboBoxRenderer extends JComboBox implements TableCellRenderer {

  
	private static final long serialVersionUID = -3591615661408706646L;

   public ComboBoxRenderer(String[] items) {
    super(items);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }

    setSelectedItem(value);
    return this;
  }

}
