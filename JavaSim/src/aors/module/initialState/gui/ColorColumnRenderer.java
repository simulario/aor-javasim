package aors.module.initialState.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//We will use this class to render SelfBeliefSlot
public class ColorColumnRenderer extends DefaultTableCellRenderer {

  
	private static final long serialVersionUID = -8337524591365893098L;
    Color bgColor, fgColor;

  public ColorColumnRenderer(Color bgColor, Color fgColor) {

    super();
    this.bgColor = bgColor;//initialize the background color for a cell
    this.fgColor = fgColor;//initialize the foreground color for a cell

  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    Component cell = super.getTableCellRendererComponent(table, value,
        isSelected, hasFocus, row, column);
    
    //always set the back and foreground color 
    //we don't care isSelected or hasFocus
    cell.setBackground(bgColor);//set background color as red
    cell.setForeground(fgColor);//set foreground color as white

    return cell;

  }

}
