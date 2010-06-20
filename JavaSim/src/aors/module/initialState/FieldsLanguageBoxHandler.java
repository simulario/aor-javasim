package aors.module.initialState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import aors.module.initialState.gui.InitialStateUITab;

public class FieldsLanguageBoxHandler implements ActionListener {

  public FieldsLanguageBoxHandler(
      HashMap<String, Vector<InitialStateUITab.FieldsEdit>> fieldsTypeMap,
      HashMap<String, String> labelMap, HashMap<String, String> hintMap,
      HashMap<String, HashSet<String>> userInterfaceMap,
      HashMap<String, HashSet<String>> objectObjectEventMap,
      Vector<JButton> buttons) {

    this.fieldsTypeMap = fieldsTypeMap;
    this.labelMap = labelMap;
    this.hintMap = hintMap;
    this.userInterfaceMap = userInterfaceMap;
    this.objectObjectEventMap = objectObjectEventMap;

    this.buttons = buttons;
    initialButtonLanMap();

  }

  public void initialButtonLanMap() {

    Vector<String> enButton = new Vector<String>();
    enButton.add("Save");
    enButton.add("Save As...");

    Vector<String> deButton = new Vector<String>();
    deButton.add("Speichern");
    deButton.add("Speichern Als...");

    buttonLanMap.put("en", enButton);
    buttonLanMap.put("de", deButton);

  }

  public void actionPerformed(ActionEvent ie) {

    JComboBox source = (JComboBox) ie.getSource();
    String lan = (String) source.getSelectedItem();
    System.out.println("The select language is: ===> " + lan);

    processButton(lan);
    processFields(lan);

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

  public void processFields(String lan) {

    Collection<HashSet<String>> objectObjectEventSets = objectObjectEventMap
        .values();

    for (Iterator<String> fieldsTypes = fieldsTypeMap.keySet().iterator(); fieldsTypes
        .hasNext();) {

      String tempFieldsType = fieldsTypes.next();
      String userInterfaceType = tempFieldsType;

      for (Iterator<HashSet<String>> eventSets = objectObjectEventSets
          .iterator(); eventSets.hasNext();) {

        HashSet<String> tempEventSet = eventSets.next();

        if (tempEventSet.contains(tempFieldsType)) {

          for (Iterator<String> objectTypes = objectObjectEventMap.keySet()
              .iterator(); objectTypes.hasNext();) {

            String tempObjectType = objectTypes.next();
            if (objectObjectEventMap.get(tempObjectType).equals(tempEventSet)) {

              userInterfaceType = tempFieldsType.substring(tempObjectType
                  .length());
            }
          }
        }
      }

      HashSet<String> tempPropertyVector = userInterfaceMap
          .get(userInterfaceType);
      Vector<InitialStateUITab.FieldsEdit> fieldsContainer = fieldsTypeMap
          .get(tempFieldsType);
      for (int i = 0; i < fieldsContainer.size(); i++) {
        Vector<String> tempLabels = fieldsContainer.get(i).getLabelsContainer();
        Vector<JLabel> tempEditLabelsContainer = fieldsContainer.get(i)
            .getEditLabelsContainer();

        for (int j = 0; j < tempLabels.size(); j++) {

          String tempFieldLabel = tempLabels.get(j);

          if (tempFieldLabel.equalsIgnoreCase("id")
              | tempFieldLabel.equalsIgnoreCase("idRef")
              | tempFieldLabel.equalsIgnoreCase("rangeStartID")
              | tempFieldLabel.equalsIgnoreCase("rangeEndID")

          ) {

            continue;
          } else {

            Iterator<String> it = tempPropertyVector.iterator();
            while (it.hasNext()) {
              String tempLabel = it.next();
              String tempTableHeadName = labelMap.get(tempLabel);
              if (tempTableHeadName.equals(tempFieldLabel)) {

                String newLabel = tempLabel.substring(0,
                    (tempLabel.length() - 2))
                    + lan;
                String newTableHeadName = labelMap.get(newLabel);
                if (newTableHeadName == null) {
                  newTableHeadName = tempFieldLabel;
                  tempEditLabelsContainer.get(j).setText(newTableHeadName);
                  tempEditLabelsContainer.get(j).setToolTipText(
                      "Please add corresponding label and hint");

                } else {

                  tempEditLabelsContainer.get(j).setText(newTableHeadName);
                  String newLabelHeadHint = hintMap.get(newLabel);

                  if (newLabelHeadHint == null) {

                    newLabelHeadHint = "Please add corresponding hint";
                    tempEditLabelsContainer.get(j).setToolTipText(
                        newLabelHeadHint);

                  } else {

                    tempEditLabelsContainer.get(j).setToolTipText(
                        newLabelHeadHint);

                  }

                }

              }
            }
          }
        }
      }

    }

  }

  private HashMap<String, Vector<InitialStateUITab.FieldsEdit>> fieldsTypeMap;
  private Vector<JButton> buttons;
  private HashMap<String, Vector<String>> buttonLanMap = new HashMap<String, Vector<String>>();
  private HashMap<String, String> labelMap;
  private HashMap<String, String> hintMap;
  private HashMap<String, HashSet<String>> userInterfaceMap;
  private HashMap<String, HashSet<String>> objectObjectEventMap;

}