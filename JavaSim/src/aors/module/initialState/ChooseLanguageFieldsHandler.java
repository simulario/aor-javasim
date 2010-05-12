/*package aors.module.initialState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import aors.module.initialState.gui.InitialStateUITab;

public class ChooseLanguageFieldsHandler implements ActionListener {

  public ChooseLanguageFieldsHandler(
      HashMap<String, Vector<InitialStateUITab.FieldsEdit>> fieldsTypeMap,
      HashMap<String, String> labelMap,
      HashMap<String, HashSet<String>> lanTypeMap,
      HashMap<String, Vector<String>> valueExprMap,
      HashMap<String, HashSet<String>> valueExprLanMap,
      HashMap<String, HashSet<String>> objectObjectEventMap,
      HashMap<String, Vector<String>> userInterfaceMap) {

    this.fieldsTypeMap = fieldsTypeMap;
    this.labelMap = labelMap;
    this.lanTypeMap = lanTypeMap;
    this.valueExprLanMap = valueExprLanMap;
    this.valueExprMap = valueExprMap;
    this.objectObjectEventMap = objectObjectEventMap;
    this.userInterfaceMap = userInterfaceMap;

  }

  public void actionPerformed(ActionEvent ae) {

    JComboBox source = (JComboBox) ae.getSource();
    String lan = (String) source.getSelectedItem();
    Collection<HashSet<String>> objectObjectEventSets = objectObjectEventMap
        .values();

    for (Iterator<String> fieldsTypes = fieldsTypeMap.keySet().iterator(); fieldsTypes
        .hasNext();) {

      String tempFieldsType = fieldsTypes.next();
      String typeLan = tempFieldsType + lan;
      String userInterfaceType = tempFieldsType;
      String objectType = null;

      if (valueExprLanMap.keySet().contains(tempFieldsType)) {

        for (Iterator<HashSet<String>> eventSets = objectObjectEventSets
            .iterator(); eventSets.hasNext();) {

          HashSet<String> tempEventSet = eventSets.next();
          if (tempEventSet.contains(tempFieldsType)) {
            System.out.println("Here is objectObjectEventType!");
            for (Iterator<String> objectTypes = objectObjectEventMap.keySet()
                .iterator(); objectTypes.hasNext();) {

              String tempObjectType = objectTypes.next();
              if (objectObjectEventMap.get(tempObjectType).equals(tempEventSet)) {

                userInterfaceType = tempFieldsType.substring(tempObjectType
                    .length());
                objectType = tempObjectType;

              }
            }
          }
        }

        Vector<InitialStateUITab.FieldsEdit> fieldsContainer = fieldsTypeMap
            .get(tempFieldsType);
        Vector<String> tempLabelKeys = userInterfaceMap.get(userInterfaceType);

        for (int i = 0; i < fieldsContainer.size(); i++) {

          Vector<String> tempLabels = fieldsContainer.get(i)
              .getLabelsContainer();
          Vector<JTextField> tempEditFieldsContainer = fieldsContainer.get(i)
              .getEditFieldsContainer();

          for (int j = 0; j < tempLabels.size(); j++) {

            String tempLabel = tempLabels.get(j);

            for (Iterator<String> valueKeys = valueExprMap.keySet().iterator(); valueKeys
                .hasNext();) {

              String valueKey = valueKeys.next();

              if (valueKey.contains(typeLan)) {

                String propertyType = null;

                if (objectType != null) {

                  String property = valueKey.substring(0, (valueKey.length()
                      - objectType.length() - userInterfaceType.length() - lan
                      .length()));

                  propertyType = property + userInterfaceType;

                } else {

                  propertyType = valueKey.substring(0, (valueKey.length() - lan
                      .length()));

                }

                HashSet<String> tempLans = lanTypeMap.get(userInterfaceType);

                for (Iterator<String> lans = tempLans.iterator(); lans
                    .hasNext();) {

                  String tempLan = lans.next();
                  String tempLabelKey = propertyType + tempLan;
                  if (labelMap.get(tempLabelKey) != null) {

                    if (tempLabelKeys.contains(tempLabelKey)) {

                      if (tempLabel.equals(labelMap.get(tempLabelKey))) {
                        System.out.println("tempLabel:=> " + tempLabel);
                        System.out.println("valueKey:=> " + valueKey);
                        Vector<String> columnValues = valueExprMap
                            .get(valueKey);
                        tempEditFieldsContainer.get(j).setText(
                            columnValues.get(i));
                      }
                    }
                  }
                }
              }

            }
          }
        }
      }

    }

  }

  private HashMap<String, Vector<InitialStateUITab.FieldsEdit>> fieldsTypeMap = null;
  private HashMap<String, String> labelMap = null;
  private HashMap<String, HashSet<String>> lanTypeMap = null;
  private HashMap<String, Vector<String>> valueExprMap = null;
  private HashMap<String, HashSet<String>> valueExprLanMap = null;
  private HashMap<String, HashSet<String>> objectObjectEventMap = null;
  private HashMap<String, Vector<String>> userInterfaceMap = null;
  private HashMap<String, JComboBox> pLanTypeMap;
  private JComboBox box;

}
*/