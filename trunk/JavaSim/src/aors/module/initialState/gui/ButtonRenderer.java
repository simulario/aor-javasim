package aors.module.initialState.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
 
/*this class is used to render the unit with ValueExpr or RandomVariable
and show them with JButton*/
public class ButtonRenderer extends JButton implements TableCellRenderer {
 
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  public ButtonRenderer() {
    setOpaque(true);
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      setBackground(table.getSelectionBackground());
    } else{
      setForeground(table.getForeground());
      setBackground(UIManager.getColor("Button.background"));
    }
    setText( (value ==null) ? "Show" : value.toString() );
    return this;
  }
}