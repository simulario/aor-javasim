package aors.module.initialState.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorColumnRenderer extends DefaultTableCellRenderer {

  Color bgColor, fgColor;

  public ColorColumnRenderer(Color bgColor, Color fgColor) {

    super();
    this.bgColor = bgColor;
    this.fgColor = fgColor;

  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    Component cell = super.getTableCellRendererComponent(table, value,
        isSelected, hasFocus, row, column);

    cell.setBackground(bgColor);
    cell.setForeground(fgColor);

    return cell;

  }

}
