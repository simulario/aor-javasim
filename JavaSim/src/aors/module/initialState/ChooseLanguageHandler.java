package aors.module.initialState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ChooseLanguageHandler implements ActionListener {

  public ChooseLanguageHandler(JTable table,
      HashMap<String, Vector<String>> valueExprMap, String type,
      HashMap<String, HashSet<String>> lanTypeMap,
      HashMap<String, String> labelMap, String objectType) {

    this.table = table;
    this.valueExprMap = valueExprMap;
    this.type = type;
    this.lanTypeMap = lanTypeMap;
    this.labelMap = labelMap;
    this.model = (DefaultTableModel) table.getModel();
    this.objectType = objectType;

  }

  public void actionPerformed(ActionEvent ae) {

    JComboBox source = (JComboBox) ae.getSource();
    String lan = (String) source.getSelectedItem();

    for (int i = 0; i < table.getColumnCount(); i++) {

      String tableLabel = (String) table.getColumnModel().getColumn(i)
          .getHeaderValue();

      for (Iterator<String> it = valueExprMap.keySet().iterator(); it.hasNext();) {

        String valueKey = it.next();

        if (valueKey.contains(lan)) {

          String propertyType = null;

          if (objectType != null) {
            String property = valueKey.substring(0, (valueKey.length()
                - objectType.length() - type.length() - lan.length()));

            propertyType = property + type;

          } else {
            propertyType = valueKey.substring(0, (valueKey.length() - lan
                .length()));

          }
          HashSet<String> tempLans = lanTypeMap.get(type);

          for (Iterator<String> itLans = tempLans.iterator(); itLans.hasNext();) {

            String tempLan = itLans.next();
            String tempLabelKey = propertyType + tempLan;
            String tempLabel = labelMap.get(tempLabelKey);
            System.out.println("the tempLabel is:=> " + tempLabel);
            if (tempLabel != null) {

              if (tempLabel.equals(tableLabel)) {

                Vector<String> columnValues = valueExprMap.get(valueKey);

                for (int j = 0; j < model.getDataVector().size(); j++) {
                  System.out.println("Now the i:=> " + i + " j:=>" + j
                      + " and the value is:=> " + columnValues.get(j));
                  model.setValueAt(columnValues.get(j), j, i);

                }

              }

            }

          }

        }

      }

    }

  }

  private JTable table;
  private HashMap<String, Vector<String>> valueExprMap;
  private HashMap<String, HashSet<String>> lanTypeMap;
  private String type;
  private HashMap<String, String> labelMap;
  private DefaultTableModel model;
  private String objectType;

}
