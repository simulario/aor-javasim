package aors.module.initialState;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ColumnHeaderToolTips extends MouseMotionAdapter {

  public void setToolTip(TableColumn col, String tooltip) {
    if (tooltip == null) {
      tips.remove(col);
    } else {
      tips.put(col, tooltip);
    }
  }

  public void mouseMoved(MouseEvent evt) {
    TableColumn col = null;
    JTableHeader header = (JTableHeader) evt.getSource();
    JTable table = header.getTable();
    TableColumnModel colModel = table.getColumnModel();
    int vColIndex = colModel.getColumnIndexAtX(evt.getX());

    if (vColIndex >= 0) {
      col = colModel.getColumn(vColIndex);
    }

    if (col != curCol) {
      header.setToolTipText((String) tips.get(col));
      curCol = col;
    }
  }

  private TableColumn curCol;
  private Map<TableColumn, String> tips = new HashMap<TableColumn, String>();

}
