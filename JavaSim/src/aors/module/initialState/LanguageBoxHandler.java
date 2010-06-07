package aors.module.initialState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class LanguageBoxHandler implements ActionListener {

  public LanguageBoxHandler(String type, JTable table,
      HashMap<String, String> labelMap, HashMap<String, String> hintMap,
      HashMap<String, Vector<String>> userInterfaceMap, Vector<JButton> buttons) {

    this.type = type;
    this.table = table;
    this.labelMap = labelMap;
    this.hintMap = hintMap;
    this.userInterfaceMap = userInterfaceMap;
    this.buttons = buttons;
    initialButtonLanMap();

  }

  public void initialButtonLanMap() {

    Vector<String> enButton = new Vector<String>();
    enButton.add("Copy");
    enButton.add("Del");
    enButton.add("New");
    enButton.add("Edit");

    Vector<String> deButton = new Vector<String>();
    deButton.add("Kopieren");
    deButton.add("Loeschen");
    deButton.add("Neu");
    deButton.add("Bearbeiten");

    buttonLanMap.put("en", enButton);
    buttonLanMap.put("de", deButton);

  }

  public void actionPerformed(ActionEvent ie) {

    JComboBox source = (JComboBox) ie.getSource();
    String lan = (String) source.getSelectedItem();
    System.out.println("The select language is: ===> " + lan);

    processTable(lan);
    processButton(lan);

  }

  public void processTable(String lan) {

    tempPropertyVector = userInterfaceMap.get(type);
    Vector<String> tableHeadHintVector = new Vector<String>();
    for (int i = 0; i < table.getColumnCount(); i++) {
      String tableHeadName = (String) table.getColumnModel().getColumn(i)
          .getHeaderValue();

      if (tableHeadName.equalsIgnoreCase("id")
          | tableHeadName.equalsIgnoreCase("idRef")
          | tableHeadName.equalsIgnoreCase("rangeStartID")
          | tableHeadName.equalsIgnoreCase("rangeEndID")

      ) {

        continue;
      } else {

        Iterator<String> it = tempPropertyVector.iterator();
        while (it.hasNext()) {
          String tempLabel = (String) it.next();
          String tempTableHeadName = labelMap.get(tempLabel);

          if (tempTableHeadName.equals(tableHeadName)) {

            String newLabel = tempLabel.substring(0, (tempLabel.length() - 2))
                + lan;
            String newTableHeadName = labelMap.get(newLabel);
            if (newTableHeadName == null) {
              newTableHeadName = tableHeadName;
              table.getColumnModel().getColumn(i).setHeaderValue(
                  newTableHeadName);

            } else {

              table.getColumnModel().getColumn(i).setHeaderValue(
                  newTableHeadName);
            }

            String newTableHeadHint = hintMap.get(newLabel);

            if (newTableHeadHint == null) {

              newTableHeadHint = "Please add corresponding label and hint";
              tableHeadHintVector.addElement(newTableHeadHint);

            } else {

              tableHeadHintVector.addElement(newTableHeadHint);
            }
          }
        }
      }
    }
    table.getTableHeader().resizeAndRepaint();

    JTableHeader header = table.getTableHeader();
    ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
    int k = 0;
    for (int c = 0; c < table.getColumnCount(); c++) {
      TableColumn col = table.getColumnModel().getColumn(c);
      if (((String) col.getHeaderValue()).equals("id")
          | ((String) col.getHeaderValue()).equals("idRef")
          | ((String) col.getHeaderValue()).equals("rangeStartID")
          | ((String) col.getHeaderValue()).equals("rangeEndID")) {
        continue;
      } else {
        tips.setToolTip(col, tableHeadHintVector.get(k));
        k++;

      }
    }
    header.addMouseMotionListener(tips);

  }

  public void processButton(String lan) {

    Vector<String> tempLan = buttonLanMap.get(lan);

    if (tempLan != null) {

      for (int i = 0; i < buttons.size(); i++) {

        buttons.get(i).setText(tempLan.get(i));
      }
    } else {

      for (int i = 0; i < buttons.size(); i++) {

        buttons.get(i).setToolTipText(
            "Please add corresponding content to the Button!");
      }

    }

  }

  private String type;
  private JTable table;
  private Vector<String> tempPropertyVector;
  private HashMap<String, String> labelMap;
  private HashMap<String, String> hintMap;
  private HashMap<String, Vector<String>> userInterfaceMap;
  private HashMap<String, Vector<String>> buttonLanMap = new HashMap<String, Vector<String>>();
  private Vector<JButton> buttons;

}
