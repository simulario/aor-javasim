package aors.module.initialState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
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
      HashMap<String, HashSet<String>> userInterfaceMap, Vector<JButton> buttons) {

    this.type = type;//the entity type
    this.table = table;//selected table
    this.labelMap = labelMap;//mapping between labelKey and correspondent label
    this.hintMap = hintMap;//mapping between hintKey and correspondent hint
    this.userInterfaceMap = userInterfaceMap;//mapping between an entity type and labelKey set
    this.buttons = buttons;//buttons container for copy, edit, delete and create
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

    processTable(lan);//process table column name language and hint conversion
    processButton(lan);//process button label language conversion

  }

  public void processTable(String lan) {

    tempPropertyVector = userInterfaceMap.get(type);
    Vector<String> tableHeadHintVector = new Vector<String>();
    for (int i = 0; i < table.getColumnCount(); i++) {
      String tableHeadName = (String) table.getColumnModel().getColumn(i)
          .getHeaderValue();
      //id, idRef, rangeStartID and rangeEndID are special table column
      //the table column name of them will be preserved
      if (tableHeadName.equalsIgnoreCase("id")
          | tableHeadName.equalsIgnoreCase("idRef")
          | tableHeadName.equalsIgnoreCase("rangeStartID")
          | tableHeadName.equalsIgnoreCase("rangeEndID")

      ) {

        continue;
      } else {

        Iterator<String> it = tempPropertyVector.iterator();
        while (it.hasNext()) {
          String tempLabel = it.next();
          String tempTableHeadName = labelMap.get(tempLabel);
          //match correspondent table column name
          if (tempTableHeadName.equals(tableHeadName)) {

            String newLabel = tempLabel.substring(0, (tempLabel.length() - 2)) + lan;
            String newTableHeadName = labelMap.get(newLabel);
            
            //if the user does not set correspondent column name label 
            //then we will not change the column name
            if (newTableHeadName == null) {
              
            	newTableHeadName = tableHeadName;
                table.getColumnModel().getColumn(i).setHeaderValue(
                  newTableHeadName);

            } else {

                table.getColumnModel().getColumn(i).setHeaderValue(
                  newTableHeadName);
                
            }

            String newTableHeadHint = hintMap.get(newLabel);
            //if the user does not provide correspondent hint
            //then we will advise him to add correspondent into it 
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
  private HashSet<String> tempPropertyVector;
  private HashMap<String, String> labelMap;
  private HashMap<String, String> hintMap;
  private HashMap<String, HashSet<String>> userInterfaceMap;
  private HashMap<String, Vector<String>> buttonLanMap = new HashMap<String, Vector<String>>();
  private Vector<JButton> buttons;

}
