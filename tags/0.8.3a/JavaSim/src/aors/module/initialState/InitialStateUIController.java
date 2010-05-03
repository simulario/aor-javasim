package aors.module.initialState;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.data.java.SimulationEvent;
import aors.data.java.SimulationStepEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.module.Module;
import aors.module.initialState.gui.InitialStateUITab;

public class InitialStateUIController implements Module, ActionListener {

  public InitialStateUIController() {
    System.out.println("Here is InitialStateUIController constructor!");
    this.tabScroll = new InitialStateUITab(this);

  }

  public Object getGUIComponent() {
    return this.tabScroll;
  }

  public InitialStateUIController(JTable table, DefaultTableModel model,
      String type, Node node, HashMap<String, Vector<String>> userInterfaceMap) {

    this.type = type;
    this.table = table;
    this.model = model;
    this.node = node;
    this.userInterfaceMap = userInterfaceMap;

  }

  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {

    System.out.println("Here is InitialStateUIController SimDomOnly!");

    this.sd = simulationDescription;
    this.dom = sd.getDom();

    if (dom == null) {
      return;
    }

    ((InitialStateUITab) getGUIComponent()).initial(dom, sd, tabScroll,
        scenario);
  }

  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationStepStart(long stepNumber) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationEnded() {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationInfosEvent(SimulationEvent simulationEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationInitialize(InitialState initialState) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationPaused(boolean pauseState) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationProjectDirectoryChanged(File projectDirectory) {
    // TODO Auto-generated method stub
    this.scenario = new File(projectDirectory, "scenario.xml");

  }

  @Override
  public void simulationStarted() {
    // TODO Auto-generated method stub

  }

  /*
   * @Override public void objektDestroyEvent(ObjektDestroyEvent
   * objektDestroyEvent) { // TODO Auto-generated method stub
   * 
   * }
   * 
   * 
   * @Override public void objektInitEvent(ObjektInitEvent objInitEvent) { //
   * TODO Auto-generated method stub
   * 
   * }
   */

  public void actionPerformed(ActionEvent e) {

    JButton selectButton = (JButton) e.getSource();

    if (selectButton.getText().equalsIgnoreCase("Copy")) {
      copyRow(table, model);
    } else if (selectButton.getText().equalsIgnoreCase("Del")) {
      delRow(table, model);
    } else if (selectButton.getText().equalsIgnoreCase("New")) {
      createNewRow(table, model, selectButton);
    } else if (selectButton.getText().equalsIgnoreCase("Edit")) {
      createEditRow(table, model, selectButton);
    }
  }

  public void copyRow(JTable table, DefaultTableModel model) {

    int cRow = table.getSelectedRow();
    Vector<String> header = new Vector<String>();
    Vector<Vector<String>> tempData = (Vector<Vector<String>>) model
        .getDataVector();

    if (cRow < 0) {
      cRow = 0;
    }

    if (cRow > tempData.size()) {
      cRow = tempData.size();
    }

    if (!(tempData.isEmpty())) {

      Vector<String> tempRow = tempData.elementAt(cRow);
      tempRow = (Vector<String>) tempRow.clone();
      model.insertRow(cRow + 1, tempRow);

    }
  }

  public void delRow(JTable table, DefaultTableModel model) {

    int cRow = table.getSelectedRow();
    Vector<Vector<String>> tempData = (Vector<Vector<String>>) model
        .getDataVector();
    boolean flag;
    if (cRow < 0 || cRow > tempData.size()) {
      flag = false;
    } else {
      flag = true;
    }

    if (flag & (tempData.size() > 1)) {

      model.removeRow(cRow);
    }

  }

  public void createNewRow(JTable table, DefaultTableModel model, JButton button) {

    HashMap<String, HashSet<Integer>> constrainMap = new HashMap<String, HashSet<Integer>>();
    HashSet<Integer> booleanPosition = new HashSet<Integer>();
    HashSet<Integer> integerPosition = new HashSet<Integer>();

    int cRow = table.getSelectedRow();
    Vector<Vector> tempData = (Vector<Vector>) model.getDataVector();

    Vector tempRow;
    if (cRow < 0) {
      cRow = 0;
    }

    if (cRow > tempData.size()) {
      cRow = tempData.size();
    }

    if (!(tempData.isEmpty())) {

      tempRow = tempData.elementAt(cRow);
      tempRow = (Vector) tempRow.clone();

      for (int i = 0; i < tempRow.size(); i++) {

        if ((tempRow.get(i).getClass().getName()).equals("java.lang.Boolean")) {

          booleanPosition.add(i);

        } else if ((tempRow.get(i).getClass().getName())
            .equals("java.lang.Integer")) {

          integerPosition.add(i);
        }

        tempRow.set(i, "");
      }

      constrainMap.put("boolean", booleanPosition);
      constrainMap.put("integer", integerPosition);

      Frame frame = (Frame) SwingUtilities.getRoot(button);
      JDialog editJDialog = new EditJDialog(frame, true, table, tempRow,
          tempData, model, cRow, button, constrainMap, tabScroll, type);
      editJDialog.pack();
      editJDialog.setVisible(true);
    }

  }

  public void createEditRow(JTable table, DefaultTableModel model,
      JButton button) {
    HashMap<String, HashSet<Integer>> constrainMap = new HashMap<String, HashSet<Integer>>();
    HashSet<Integer> booleanPosition = new HashSet<Integer>();
    HashSet<Integer> integerPosition = new HashSet<Integer>();

    int cRow = table.getSelectedRow();
    Vector<Vector> tempData = (Vector<Vector>) model.getDataVector();
    if (cRow < 0) {
      cRow = 0;
    }
    if (cRow > tempData.size()) {
      cRow = tempData.size();
    }
    Vector tempRow = tempData.elementAt(cRow);

    for (int i = 0; i < tempRow.size(); i++) {
      if ((tempRow.get(i).getClass().getName()).equals("java.lang.Boolean")) {
        booleanPosition.add(i);
      } else if ((tempRow.get(i).getClass().getName())
          .equals("java.lang.Integer")) {
        integerPosition.add(i);
      }
      tempRow.set(i, String.valueOf(tempRow.get(i)));
    }

    constrainMap.put("boolean", booleanPosition);
    constrainMap.put("integer", integerPosition);
    Frame frame = (Frame) SwingUtilities.getRoot(button);
    JDialog editJDialog = new EditJDialog(frame, true, table, tempRow,
        tempData, model, cRow, button, constrainMap, tabScroll, type);
    editJDialog.pack();
    editJDialog.setVisible(true);
  }

  private Node node;
  private Document dom;
  private JTable table;
  private DefaultTableModel model;
  private File scenario;
  private String type;
  private HashMap<String, Vector<String>> userInterfaceMap;
  private SimulationDescription sd;
  private static InitialStateUITab tabScroll;

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    // TODO Auto-generated method stub

  }

}

class EditJDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  public EditJDialog(Frame owner, boolean modal, JTable table,
      Vector<String> row, Vector<Vector> rowData, DefaultTableModel model,
      int cRow, JButton button, HashMap<String, HashSet<Integer>> constrainMap,
      InitialStateUITab initialStateUITab, String type) {

    super(owner, modal);
    JDialog.setDefaultLookAndFeelDecorated(true);
    if (owner != null) {
      Dimension parentSize = owner.getSize();
      Point p = owner.getLocation();
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }

    this.type = type;
    this.table = table;
    this.row = row;
    this.model = model;
    this.cRow = cRow;
    this.rowData = rowData;
    this.button = button;
    this.constrainMap = constrainMap;
    this.initialStateUITab = initialStateUITab;
    System.out.println("The initialStateUITab:=> " + this.initialStateUITab);
    JScrollPane editPane = new JScrollPane();
    editPane.getViewport().add(createContentPanel());
    add(editPane, BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);

  }

  public JPanel createContentPanel() {

    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new GridLayout(row.size(), 2, 10, 0));
    label = new JLabel[row.size()];
    field = new JTextField[row.size()];

    for (int i = 0; i < row.size(); i++) {
      String columnName = (String) table.getColumnModel().getColumn(i)
          .getHeaderValue(); // table.getModel().getColumnName(i);
      label[i] = new JLabel(columnName);
      field[i] = new JTextField(row.get(i));
      contentPanel.add(label[i]);
      contentPanel.add(field[i]);

    }

    return contentPanel;

  }

  JPanel createButtonPanel() {

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    JButton ok = new JButton("OK");
    ActionListener okListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        Vector insertEditRow = new Vector();

        for (int i = 0; i < field.length; i++) {

          if ((field[i].getText().equals("")) || (field[i].getText() == null)) {

            // System.out.println("field "+i+" is blank!");
            JOptionPane.showMessageDialog(null,
                "Please fill in the field with the content,now it is empty!");
            field[i].setText("!empty!");
            return;
          } else if (constrainMap.get("boolean").contains(i)) {

            booleanPosition = constrainMap.get("boolean");
            for (Iterator<Integer> it = booleanPosition.iterator(); it
                .hasNext();) {
              int position = it.next();

              if (position == i) {

                if ((!field[i].getText().equals("true"))
                    & (!field[i].getText().equals("false"))) {
                  JOptionPane.showMessageDialog(null,
                      "The content of the field " + label[i].getText()
                          + " is only true or false");
                  return;
                } else {

                  insertEditRow.addElement(Boolean.valueOf(field[i].getText()));
                }
              }
            }

          } else if (constrainMap.get("integer").contains(i)) {

            integerPosition = constrainMap.get("integer");
            for (Iterator<Integer> it = integerPosition.iterator(); it
                .hasNext();) {
              int position = it.next();

              if (position == i) {

                HashSet<String> tempPropertySet = initialStateUITab
                    .getConstrainMap().get(type);

                for (Iterator<String> propertys = tempPropertySet.iterator(); propertys
                    .hasNext();) {

                  String property = propertys.next();
                  String propertyType = property + type;

                  HashSet<String> tempLanSet = initialStateUITab.getLanType()
                      .get(type);
                  for (Iterator<String> lans = tempLanSet.iterator(); lans
                      .hasNext();) {

                    String lan = lans.next();
                    String labelKey = propertyType + lan;

                    if (initialStateUITab.getLabelMap().get(labelKey).equals(
                        label[i].getText())) {

                      Vector<Integer> tempValueRange = initialStateUITab
                          .getConstrainNameMapRange().get(property);
                      int minValue = tempValueRange.get(0);
                      int maxValue = tempValueRange.get(1);

                      int currentValue = Integer.valueOf(field[i].getText());

                      if ((currentValue > maxValue)
                          || (currentValue < minValue)) {

                        JOptionPane.showMessageDialog(null,
                            "The content of the field " + label[i].getText()
                                + " must be in the range" + "(" + minValue
                                + "," + maxValue + ")");
                        return;
                      } else {

                        insertEditRow.addElement(Integer.valueOf(field[i]
                            .getText()));
                      }
                    }
                  }
                }
              }
            }
          } else {

            boolean enumProperty = false;
            String property = "";
            tempStop: {

              for (Iterator<String> it = initialStateUITab.getEnumMap()
                  .keySet().iterator(); it.hasNext();) {

                property = it.next();
                String propertyType = property + type;

                HashSet<String> tempLanSet = initialStateUITab.getLanType()
                    .get(type);
                for (Iterator<String> lans = tempLanSet.iterator(); lans
                    .hasNext();) {

                  String lan = lans.next();
                  String labelKey = propertyType + lan;

                  Collection<String> labels = initialStateUITab.getLabelMap()
                      .values();

                  if (labels.contains(initialStateUITab.getLabelMap().get(
                      labelKey))) {

                    if (initialStateUITab.getLabelMap().get(labelKey).equals(
                        label[i].getText())) {

                      enumProperty = true;
                      break tempStop;
                    }
                  }
                }
              }

            }

            if (!enumProperty) {

              insertEditRow.addElement(field[i].getText());

            } else {

              HashSet<String> tempEnumContent = initialStateUITab.getEnumMap()
                  .get(property);

              for (String s : tempEnumContent) {
                // System.out.println("The individual content in Enum:===> " +
                // s);
              }

              if (tempEnumContent.contains(field[i].getText().trim())) {

                insertEditRow.addElement(field[i].getText());

              } else {

                JOptionPane.showMessageDialog(null, "The content of the field "
                    + label[i].getText() + " must be in the enum range");
                return;

              }

            }

          }
        }
        if (cRow < 0) {
          cRow = 0;
        }
        if (cRow > rowData.size()) {
          cRow = rowData.size();
        }

        if (button.getText().equals("Edit")) {
          model.insertRow(cRow, insertEditRow);

          boolean flag;
          if (cRow < 0 || cRow > rowData.size()) {
            flag = false;
          } else {
            flag = true;
          }

          if (flag) {
            model.removeRow(cRow + 1);
          }
        }

        if (button.getText().equals("New")) {
          model.insertRow(cRow + 1, insertEditRow);
        }

        setVisible(false);
        dispose();

      }
    };
    ok.addActionListener(okListener);

    JButton cancel = new JButton("CANCEL");
    ActionListener cancelListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    };
    cancel.addActionListener(cancelListener);

    buttonPanel.add(ok);
    buttonPanel.add(cancel);

    return buttonPanel;

  }

  private JTable table;
  private Vector<String> row;
  private Vector<Vector> rowData;
  private JLabel[] label;
  private JTextField[] field;
  private DefaultTableModel model;
  private int cRow;
  private String type;
  private JButton button;
  private HashMap<String, HashSet<Integer>> constrainMap;
  private HashSet<Integer> booleanPosition;
  private HashSet<Integer> integerPosition;
  private InitialStateUITab initialStateUITab;

}
