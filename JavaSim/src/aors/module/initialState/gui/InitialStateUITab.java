package aors.module.initialState.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import aors.controller.SimulationDescription;
import aors.module.GUIModule;
import aors.module.Module;
import aors.module.initialState.ChooseLanguageFieldsHandler;
import aors.module.initialState.ChooseLanguageHandler;
import aors.module.initialState.FieldsLanguageBoxHandler;
import aors.module.initialState.InitialStateUIController;
import aors.module.initialState.LanguageBoxHandler;

public class InitialStateUITab extends JScrollPane implements GUIModule {

  public InitialStateUITab(InitialStateUIController controller) {

    this.controller = controller;

  }

  public void initial(Document dom, SimulationDescription sd,
      InitialStateUITab initialStateUITab, File file) {

    System.out.println("Here is InitialStateUITab initial!");

    /* instance of SimulationDescription to be used get node information */
    this.sd = sd;

    /* original input file */
    this.file = file;

    /* original dom */
    this.dom = dom;

    /* JScrollPane contains all information of this module */
    this.initialStateUITab = initialStateUITab;

    /* the type of table displayed in the scrollPane */
    typeUITableHead = new HashSet<String>();

    /* map the property to corresponding label */
    labelMap = new HashMap<String, String>();

    /* give the property corresponding hint */
    hintMap = new HashMap<String, String>();

    /* the language type of each type */
    lanType = new HashMap<String, HashSet<String>>();

    /* map each type to corresponding properties */
    userInterfaceMap = new HashMap<String, Vector<String>>();

    /* map between the type and corresponding table */
    tableType = new HashMap<String, JTable>();

    /* in the field style map between the type and corresponding labels */
    labelTypeMap = new HashMap<String, Vector<String>>();

    /* in the field style map between the type and corresponding fields values */
    fieldTypeMap = new HashMap<String, Vector<String>>();

    /* in the field style map between the type and corresponding objects */
    fieldsTypeMap = new HashMap<String, Vector<FieldsEdit>>();

    /* contain all property for each type */
    userInterfaceVariableVector = new Vector<String>();

    /* contain all language information for each type */
    lanVariableSet = new HashSet<String>();

    /*
     * used to judge to create a new one or add the content to the exist
     * container
     */
    typeSet = new HashSet<String>();

    // objectEventTypeSet = new HashSet<String>();

    /* map the events that exist in the object or agent */
    objectObjectEventMap = new HashMap<String, HashSet<String>>();

    /* contain all types that will be shown using Field style */
    fieldStyleSet = new HashSet<String>();

    /* map between a type with its contentPanel using field style */
    contentPanelTypeMap = new HashMap<String, JPanel>();

    /* map between a type and the selfbliefproperty */
    selfPropertyMap = new HashMap<String, Vector<String>>();

    /* map between a type and the enumeration property */
    enumMap = new HashMap<String, HashSet<String>>();

    /* constrain mapping between a type and the corresponding properties */
    constrainMap = new HashMap<String, HashSet<String>>();

    /* map between the property and value range */
    constrainNameMapRange = new HashMap<String, Vector<Integer>>();

    /* map between a type and valueExpr slot property */
    valueExprLanMap = new HashMap<String, HashSet<String>>();

    /* map between the property and corresponding value */
    valueExprMap = new HashMap<String, Vector<String>>();

    /* map between the type and corresponding JComboxBox */
    pLanTypeMap = new HashMap<String, JComboBox>();

    /* contain all field types that have valueExpr */
    pLanFieldTypeSet = new HashSet<String>();

    initialStatePanel = new JPanel();

    /* process value constrain e.g. enumeration value, maxValue, minValue */
    processValueConstrain();

    /* use to distinguisch the show style between the table and field */
    processNmrOfInstance();

    /* getInformationFromUserInterface() */
    getInformationFromUserInterface();

    /* get Information from InitialState section */
    getInformationFromInitialState();

    /* process table render e.g. JComboBox, JSlider */
    processTableRenderer();
    initialStatePanel.setLayout(new BoxLayout(initialStatePanel,
        BoxLayout.Y_AXIS));
    initialStatePanel.add(createSaveButtonPanel()); // getSaveButtonPanel
    initialStateUITab.getViewport().add(initialStatePanel);

  }

  @Override
  public Module getBaseComponent() {
    // TODO Auto-generated method stub
    return this.controller;
  }

  public void processValueConstrain() {

    enumValueConstrain();// process enumeration property constraint rendered by
                         // JComboBox
    minMaxValueConstrain();// process minimun max value property constraint
                           // rendered by JSlider
    selfPropertyConstrain();// process selfBeliefProperty rendered by background
                            // color

  }

  public void enumValueConstrain() {

    NodeList EnumerationPropertys = sd.getNodeList(ENUMPROPERTY);// get NodeList
                                                                 // Enumeration
                                                                 // property
                                                                 // List
    for (int j = 0; j < EnumerationPropertys.getLength(); j++) {

      String name = ((Element) EnumerationPropertys.item(j))
          .getAttribute("name");
      String type = ((Element) EnumerationPropertys.item(j))
          .getAttribute("type");

      NodeList enumNodes = null;

      enumNodes = sd.getNodeList("/" + PX + "SimulationScenario/" + PX
          + "SimulationModel/" + PX + "DataTypes/" + PX + "Enumeration[@name='"
          + type + "']/" + PX + "EnumerationLiteral");

      HashSet<String> enumNameSet = new HashSet<String>();
      for (int i = 0; i < enumNodes.getLength(); i++) {

        // value of Enumeration property
        String enumContent = type + "."
            + ((Element) enumNodes.item(i)).getFirstChild().getNodeValue();
        enumNameSet.add(enumContent);

      }

      enumMap.put(name, enumNameSet);

    }
  }

  //
  public void minMaxValueConstrain() {

    // get all the node, which contains the min and max value that belong to the
    // Entity type
    NodeList constrainLists = sd.getNodeList(
        "//" + PX + "Attribute[@maxValue]", sd.getNode(CONSTRAINNODE));
    for (int i = 0; i < constrainLists.getLength(); i++) {

      String constrainType = "";
      Element parentNode = (Element) (constrainLists.item(i).getParentNode());
      // if the min max value is the property of GlobalVariable,
      // then we will assume the type of them is globalVariable

      if (parentNode.getNodeName().contains("Global")) {
        constrainType = "globalVariable";
      } else {
        constrainType = parentNode.getAttribute("name");
      }

      // if the type has sub type, we will also to process them
      NodeList subConstrainLists = sd.getNodeList("//*[@superType='"
          + constrainType + "']", sd.getNode(CONSTRAINNODE));

      HashSet<String> subConstrainTypeSet = new HashSet<String>();
      for (int j = 0; j < subConstrainLists.getLength(); j++) {
        String subConstrainType = ((Element) subConstrainLists.item(j))
            .getAttribute("name");
        subConstrainTypeSet.add(subConstrainType);
      }

      Element constrainElement = (Element) constrainLists.item(i);
      String constrainName = constrainElement.getAttribute("name");
      int Min = Integer.parseInt(constrainElement.getAttribute("minValue"));
      int Max = Integer.parseInt(constrainElement.getAttribute("maxValue"));

      // put the key(constrainName) and the value to the constranMap
      if (!constrainMap.keySet().contains(constrainType)) {

        HashSet<String> constrainSet = new HashSet<String>();
        constrainSet.add(constrainName);
        constrainMap.put(constrainType, constrainSet);
        for (Iterator<String> it = subConstrainTypeSet.iterator(); it.hasNext();) {
          String tempConstrainType = it.next();
          constrainMap.put(tempConstrainType, constrainSet);
        }
      } else {
        HashSet<String> tempSet = constrainMap.get(constrainType);
        tempSet.add(constrainName);
        constrainMap.put(constrainType, tempSet);

        for (Iterator<String> it = subConstrainTypeSet.iterator(); it.hasNext();) {
          String tempConstrainType = it.next();
          HashSet<String> subTempSet = constrainMap.get(tempConstrainType);
          subTempSet.add(constrainName);
          constrainMap.put(tempConstrainType, subTempSet);
        }

      }

      Vector<Integer> valueRange = new Vector<Integer>();
      valueRange.add(Min);
      valueRange.add(Max);

      constrainNameMapRange.put(constrainName, valueRange);

    }

  }

  public void selfPropertyConstrain() {

    // only the AgentUI can contain SelfBeliefProperty
    NodeList nodes = sd.getNodeList("//" + PX + "AgentUI");

    for (int i = 0; i < nodes.getLength(); i++) {
      NodeList selfPropertyNodes = sd.getNodeList(PX + "SelfBeliefPropertyUI",
          nodes.item(i));

      // Then we will test of the number of selefBeliefProperty is great than
      // zero or not.
      // If so, we will save the selefBeliefProperty and its value in the
      // selfPropertyMap
      if (selfPropertyNodes.getLength() > 0) {

        String type = ((Element) nodes.item(i)).getAttribute("agentType");
        Vector<String> selfProperties = new Vector<String>();

        for (int j = 0; j < selfPropertyNodes.getLength(); j++) {
          String property = ((Element) selfPropertyNodes.item(j))
              .getAttribute("property");
          selfProperties.add(property);

        }

        selfPropertyMap.put(type, selfProperties);

      }

    }

  }

  /*
   * we use this method to choose the display style, if a type with the
   * nmrOfInstance property, it means that this type will be displayed with
   * field style. Normally the number of entry is between 1 and 3
   */
  public void processNmrOfInstance() {

    Node UIInitialStateNode = sd.getNode(INITIALSTATEUI);
    NodeList fieldNodes = sd.getNodeList("//*[@nmrOfInstances]",
        UIInitialStateNode);

    for (int i = 0; i < fieldNodes.getLength(); i++) {

      Integer nrmOfInstance = Integer.valueOf(((Element) fieldNodes.item(i))
          .getAttribute("nmrOfInstances"));
      if (nrmOfInstance.intValue() < 4) {

        String type = processInitialStateUIType(fieldNodes.item(i));
        fieldStyleSet.add(type);

      }
    }

    fieldStyleSet.add("globalVariable");

  }

  public String processInitialStateUIType(Node node) {

    String type = "";

    if (node.getNodeName().equalsIgnoreCase("EventUI")) {
      type = ((Element) node).getAttribute("eventType");
    } else if (node.getNodeName().equalsIgnoreCase("ObjectUI")) {
      type = ((Element) node).getAttribute("objectType");
    } else if (node.getNodeName().equalsIgnoreCase("AgentUI")) {
      type = ((Element) node).getAttribute("agentType");
    } else if (node.getNodeName().equalsIgnoreCase("GlobalVariableUI")) {
      type = "globalVariable";
    }

    return type;

  }

  // create the nodeNameSet to contain all kinds of type in the InitialStateUI
  // section
  public void getInformationFromUserInterface() {

    Node initialStateUI = sd.getNode(INITIALSTATEUI);
    NodeList children = initialStateUI.getChildNodes();
    HashSet<String> nodeNameSet = new HashSet<String>();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        String nodeName = children.item(i).getNodeName();
        System.out.println("The nodeName in the UserInterface:=> " + nodeName);
        nodeNameSet.add(nodeName);
      }
    }

    Iterator<String> it = nodeNameSet.iterator();
    while (it.hasNext()) {
      String content = (String) it.next();
      NodeList isSubNodes = sd.getNodeList(INITIALSTATEUI + "/" + PX + content);
      createInitialStateUITableHeader(isSubNodes);
    }

    createBeliefEntityUI();
  }

  /*
   * create the typeUITableHead to save every Type. The type of GlobalVariable
   * is globalVariable and the BeliefEntityUI is a special one that we will deal
   * with separately
   */
  public void createInitialStateUITableHeader(NodeList list) {

    for (int i = 0; i < list.getLength(); i++) {
      Node child = list.item(i);
      String type = processInitialStateUIType(child);
      System.out.println("The type of content:===> " + type);
      typeUITableHead.add(type);
      Vector<String> userInterfacePropertyVector = new Vector<String>();
      HashSet<String> lanSet = new HashSet<String>();
      if (!type.equals("globalVariable")) {

        NodeList propertyUIs = child.getChildNodes();
        for (int k = 0; k < propertyUIs.getLength(); k++) {
          if ((propertyUIs.item(k) instanceof Element)
              & (!(propertyUIs.item(k).getNodeName()
                  .equalsIgnoreCase("BeliefEntityUI")))) {

            String property = sd.getNodeContent("@property", propertyUIs
                .item(k));
            String propertyType = property + type;

            NodeList labelnodes = sd.getNodeList(LT, propertyUIs.item(k));
            NodeList hintnodes = sd.getNodeList(HT, propertyUIs.item(k));

            for (int j = 0; j < labelnodes.getLength(); j++) {
              String lan = sd.getNodeContent(XLAN, labelnodes.item(j));
              lanSet.add(lan);
              String labelKey = propertyType + lan;// use property+type+lan to
                                                   // create the label key
              String lContent = sd.getNodeContent("text()", labelnodes.item(j));

              labelMap.put(labelKey, lContent);

              if (hintnodes.item(j) != null) {
                String hContent = sd
                    .getNodeContent("text()", hintnodes.item(j));
                hintMap.put(labelKey, hContent);
              } else {
                hintMap.put(labelKey, null);// If the value of label or hint
                                            // does not provided
                // the null will be set as default value
              }
              userInterfacePropertyVector.add(labelKey);

            }
          }
        }

        userInterfaceMap.put(type, userInterfacePropertyVector);
        lanType.put(type, lanSet);
      } else {

        String variable = ((Element) child).getAttribute("variable");
        String propertyType = variable + type;
        NodeList labelnodes = sd.getNodeList(LT, child);
        NodeList hintnodes = sd.getNodeList(HT, child);

        for (int j = 0; j < labelnodes.getLength(); j++) {
          String lan = sd.getNodeContent(XLAN, labelnodes.item(j));
          String lContent = sd.getNodeContent("text()", labelnodes.item(j));
          String hContent = sd.getNodeContent("text()", hintnodes.item(j));
          lanVariableSet.add(lan);
          String labelKey = propertyType + lan;// variable+type+lan
          userInterfaceVariableVector.add(labelKey);
          labelMap.put(labelKey, lContent);
          hintMap.put(labelKey, hContent);
        }
      }

    }

    userInterfaceMap.put("globalVariable", userInterfaceVariableVector);
    lanType.put("globalVariable", lanVariableSet);

  }

  public void createBeliefEntityUI() {

    // System.out.println("Here is createBeliefEntityUI()!");

    NodeList beliefEntityUIs = sd.getNodeList("//" + PX + "BeliefEntityUI");

    for (int i = 0; i < beliefEntityUIs.getLength(); i++) {

      String type = sd.getNodeContent("@beliefEntityType", beliefEntityUIs
          .item(i));
      typeUITableHead.add(type);
      NodeList beliefPropertyUIs = beliefEntityUIs.item(i).getChildNodes();

      HashSet<String> lanSet = new HashSet<String>();
      Vector<String> userInterfacePropertyVector = new Vector<String>();

      for (int k = 0; k < beliefPropertyUIs.getLength(); k++) {
        if ((beliefPropertyUIs.item(k) instanceof Element)) {
          String property = sd.getNodeContent("@property", beliefPropertyUIs
              .item(k));
          String propertyType = property + type;

          NodeList labelnodes = sd.getNodeList(LT, beliefPropertyUIs.item(k));
          NodeList hintnodes = sd.getNodeList(HT, beliefPropertyUIs.item(k));

          for (int j = 0; j < labelnodes.getLength(); j++) {
            String lan = sd.getNodeContent(XLAN, labelnodes.item(j));
            lanSet.add(lan);
            String labelKey = propertyType + lan;
            String lContent = sd.getNodeContent("text()", labelnodes.item(j));
            labelMap.put(labelKey, lContent);
            if (hintnodes.item(j) != null) {
              String hContent = sd.getNodeContent("text()", hintnodes.item(j));

              hintMap.put(labelKey, hContent);
            } else {
              hintMap.put(labelKey, null);
            }
            userInterfacePropertyVector.add(labelKey);

          }
        }
      }

      userInterfaceMap.put(type, userInterfacePropertyVector);
      lanType.put(type, lanSet);
    }

  }

  /*
   * get the childNodes from InitialState and we divide the nodes into three
   * category. They are object, event, and Globalvariable. The attribute
   * typeUITableHead to be used to decide whether this type will be display in
   * the panel. The attribute typeSet to distinguish whether the type is the
   * first time to be added into initialPanel or not if it is first time we will
   * create and not we will add the entry to the content
   */
  public void getInformationFromInitialState() {

    Node InitialState = sd.getNode(IS);
    NodeList children = InitialState.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {

      Node child = children.item(i);
      if (child instanceof Element) {
        String nodeName = children.item(i).getNodeName();

        if (nodeName.equals("PhysicalObject")
            || nodeName.equals("PhysicalAgent") || nodeName.equals("Object")
            || nodeName.equals("PhysicalAgents") || nodeName.equals("Agents")
            || nodeName.equals("Agent") || nodeName.equals("PhysicalObjects")) {

          String type = sd.getNodeContent("@type", child);

          if (typeUITableHead.contains(type)) {
            if (!typeSet.contains(type)) {
              typeSet.add(type);
              System.out.println("The type of object:===>" + type);
              initialStatePanel.add(createObjectContent(type, children.item(i),
                  nodeName));
              initialStatePanel.add(Box.createVerticalStrut(5));
            } else {
              addObjectContent(type, children.item(i));
            }
          }
        }

        if (nodeName.contains("Event")) {
          String type = sd.getNodeContent("@type", child);
          if (typeUITableHead.contains(type)) {
            if (!typeSet.contains(type)) {
              typeSet.add(type);
              System.out.println("The type of event:===>" + type);
              initialStatePanel.add(createEventContent(type, child));
              initialStatePanel.add(Box.createVerticalStrut(5));
            } else {

              addObjectContent(type, children.item(i));
            }
          }
        }

        if (nodeName.equals("GlobalVariable")) {
          NodeList globalVariables = sd.getNodeList(GV);
          if (typeUITableHead.contains("globalVariable")) {
            if (!typeSet.contains("globalVariable")) {
              typeSet.add("globalVariable");
              System.out.println("Here is globalvariable!");
              initialStatePanel
                  .add(createGlobalVariableContent(globalVariables));
              initialStatePanel.add(Box.createVerticalStrut(5));
            }
          }
        }

      }

    }

  }

  public JPanel createObjectContent(String type, Node node, String nodeName) {

    String title = type + "<<" + nodeName + ">>";
    JPanel objectSubPanel = createSubPanel(title);
    JPanel objectCenterPanel = createCenterPanel();
    JPanel objectBottomPanel = createBottomPanel();

    DefaultTableModel model = null;

    if (!fieldStyleSet.contains(type)) {

      model = createTableModel(type, node);
      objectContentSubNodes(type, node, model, objectCenterPanel,
          objectBottomPanel);

      JTable table = new JTable();
      table.setModel(model);

      for (int j = 0; j < model.getColumnCount(); j++) {

        String colHeadValue = (String) model.getColumnName(j);

        if (selfPropertyMap.keySet().contains(type)) {

          System.out.println("The type of self Property :=> " + type);
          Vector<String> tempContent = userInterfaceMap.get(type);
          for (Iterator<String> it = tempContent.iterator(); it.hasNext();) {

            String tempString = (String) it.next();
            String label = labelMap.get(tempString);
            if (label.equals(colHeadValue)) {

              String tempPropertyName = tempString.substring(0, (tempString
                  .length()
                  - type.length() - 2));

              Vector<String> tempVector = selfPropertyMap.get(type);

              if (tempVector.contains(tempPropertyName)) {
                System.out.println("The tempPropertyName:=> "
                    + tempPropertyName);

                /*
                 * TableColumn selfColumn = table.getColumnModel().getColumn(j);
                 * selfColumn.setCellRenderer(new ColorColumnRenderer(new
                 * Color(168, 64, 89), Color.WHITE));
                 */

              }
            }
          }
        }
      }

      JScrollPane tablePane = createScrollPane(table);
      objectCenterPanel.add(tablePane);
      objectCenterPanel.add(createButtonPanel(model, table, type, node, null));
      objectSubPanel.add(objectCenterPanel, BorderLayout.CENTER);
      tableType.put(type, table);

    } else {

      createFieldModel(type, node);
      objectContentSubNodes(type, node, model, objectCenterPanel,
          objectBottomPanel);
      Vector<String> tempLabels = labelTypeMap.get(type);
      Vector<String> tempFields = fieldTypeMap.get(type);
      FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
          initialStateUITab);
      Vector<FieldsEdit> fieldsContainer = new Vector<FieldsEdit>();
      fieldsContainer.add(fieldsEdit);
      fieldsTypeMap.put(type, fieldsContainer);
      JPanel entry = fieldsEdit.createGridLayoutPanel();
      objectBottomPanel.add(entry);
      objectSubPanel.add(objectBottomPanel, BorderLayout.SOUTH);
      contentPanelTypeMap.put(type, objectBottomPanel);
    }

    return objectSubPanel;

  }

  public void objectContentSubNodes(String type, Node node,
      DefaultTableModel model, JPanel centerPanel, JPanel bottomPanel) {

    Vector<String> tempContent = userInterfaceMap.get(type);
    if (node.hasChildNodes()) {

      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Vector columnValue = new Vector();
        String subNodeName = children.item(i).getNodeName();
        if (subNodeName.equals("Slot") | (subNodeName.equals("SelfBeliefSlot"))) {

          processCreateSlot(model, children.item(i), tempContent, type,
              columnValue, null);
        }

        if (subNodeName.contains("Event")) {
          JPanel objectEventPanel = new JPanel();
          String eventType = ((Element) children.item(i)).getAttribute("type");
          String objectEventType = type + eventType;
          if (typeUITableHead.contains(eventType)) {
            if (!typeSet.contains(objectEventType)) {
              typeSet.add(objectEventType);
              objectEventPanel = createObjectEventPanel(children.item(i),
                  eventType, type);

              if (!fieldStyleSet.contains(eventType)) {

                centerPanel.add(objectEventPanel);
                centerPanel.add(Box.createVerticalStrut(5));
              } else {

                bottomPanel.add(objectEventPanel);
                bottomPanel.add(Box.createVerticalStrut(5));

              }
            } else {
              addObjectEventContent(eventType, objectEventType, children
                  .item(i));

            }
          }
        }

        if (subNodeName.contains("BeliefEntity")) {
          JPanel agentEntityPanel = new JPanel();
          String entityType = ((Element) children.item(i)).getAttribute("type");
          String agentEntityType = type + entityType;
          if (typeUITableHead.contains(entityType)) {
            if (!typeSet.contains(agentEntityType)) {
              typeSet.add(agentEntityType);
              agentEntityPanel = createObjectEventPanel(children.item(i),
                  entityType, type);

              if (!fieldStyleSet.contains(entityType)) {

                centerPanel.add(agentEntityPanel);
                centerPanel.add(Box.createVerticalStrut(5));
              } else {

                bottomPanel.add(agentEntityPanel);
                bottomPanel.add(Box.createVerticalStrut(5));

              }

            } else {
              addObjectEventContent(entityType, agentEntityType, children
                  .item(i));

            }
          }
        }

        if (subNodeName.contains("Range")) {
          String property = subNodeName;
          String value = children.item(i).getFirstChild().getNodeValue();
          Iterator<String> it = tempContent.iterator();
          while (it.hasNext()) {
            String tempString = (String) it.next();
            if (tempString.endsWith("en")) {
              if ((tempString.substring(0,
                  (tempString.length() - type.length() - 2)))
                  .equalsIgnoreCase(property)) {

                if (!fieldStyleSet.contains(type)) {
                  if (constrainNameMapRange.keySet().contains(property)) {

                    Integer intValue = Integer.valueOf(value);
                    columnValue.addElement(intValue);

                  } else {

                    columnValue.addElement(value);
                  }
                  String label = labelMap.get(tempString);
                  model.addColumn(label, columnValue);
                } else {

                  String fieldLabel = labelMap.get(tempString);
                  labelTypeMap.get(type).add(fieldLabel);
                  fieldTypeMap.get(type).add(value);
                }
              }
            }
          }
        }

      }

    }

  }

  public void addObjectContent(String type, Node node) {

    Vector<String> header = new Vector<String>();
    Vector rowDatumTemp = new Vector();
    DefaultTableModel tempModel = null;

    if (!fieldStyleSet.contains(type)) {

      JTable tempTable = tableType.get(type);

      for (int j = 0; j < tempTable.getColumnCount(); j++) {
        String colHeadValue = (String) tempTable.getColumnModel().getColumn(j)
            .getHeaderValue();
        header.addElement(colHeadValue);
      }

      tempModel = (DefaultTableModel) tempTable.getModel();
      NamedNodeMap attrs = node.getAttributes();
      rowDatumTemp = createRowDatum(attrs, type, header);

    } else {

      createFieldModel(type, node);
    }

    if (node.hasChildNodes()) {

      NodeList children = node.getChildNodes();
      Vector<String> tempContent = userInterfaceMap.get(type);
      for (int i = 0; i < children.getLength(); i++) {
        String subNodeName = children.item(i).getNodeName();
        if (subNodeName.equals("Slot") | (subNodeName.equals("SelfBeliefSlot"))) {

          processAddSlot(header, children.item(i), tempContent, type,
              rowDatumTemp, null);
        }

        if (subNodeName.contains("Event")) {
          String eventType = ((Element) children.item(i)).getAttribute("type");
          String objectEventType = type + eventType;

          addObjectEventContent(eventType, objectEventType, children.item(i));
        }

        if (subNodeName.contains("BeliefEntity")) {
          String entityType = ((Element) children.item(i)).getAttribute("type");
          String agentEntityType = type + entityType;

          addObjectEventContent(entityType, agentEntityType, children.item(i));
        }

        if (subNodeName.contains("Range")) {
          String property = subNodeName;
          String value = children.item(i).getFirstChild().getNodeValue();
          Iterator<String> it = tempContent.iterator();
          while (it.hasNext()) {
            String tempString = (String) it.next();
            if (tempString.endsWith("en")) {
              if ((tempString.substring(0,
                  (tempString.length() - type.length() - 2)))
                  .equalsIgnoreCase(property)) {

                if (!fieldStyleSet.contains(type)) {

                  if (constrainNameMapRange.keySet().contains(property)) {

                    Integer intValue = Integer.valueOf(value);
                    rowDatumTemp.addElement(intValue);

                  } else {
                    rowDatumTemp.addElement(value);
                  }

                } else {

                  String fieldLabel = labelMap.get(tempString);
                  labelTypeMap.get(type).add(fieldLabel);
                  fieldTypeMap.get(type).add(value);

                }

              }
            }
          }
        }
      }
    }

    if (!fieldStyleSet.contains(type)) {

      tempModel.addRow(rowDatumTemp);

    } else {

      Vector<String> tempLabels = labelTypeMap.get(type);
      Vector<String> tempFields = fieldTypeMap.get(type);
      FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
          initialStateUITab);
      JPanel entry = fieldsEdit.createGridLayoutPanel();
      JPanel tempPanel = contentPanelTypeMap.get(type);
      tempPanel.add(entry);
      fieldsTypeMap.get(type).add(fieldsEdit);

    }
  }

  public JPanel createEventContent(String type, Node node) {

    String title = type + "<<" + node.getNodeName() + ">>";
    JPanel eventSubPanel = createSubPanel(title);
    JPanel eventCenterPanel = createCenterPanel();
    JPanel eventBottomPanel = createBottomPanel();

    DefaultTableModel model = null;

    if (!fieldStyleSet.contains(type)) {
      model = createTableModel(type, node);

    } else {

      createFieldModel(type, node);

    }

    if (node.hasChildNodes()) {

      Vector<String> tempContent = userInterfaceMap.get(type);

      NodeList subNodes = node.getChildNodes();
      for (int i = 0; i < subNodes.getLength(); i++) {
        if (subNodes.item(i).getNodeName().equalsIgnoreCase("Slot")) {
          Vector<String> columnValue = new Vector<String>();
          processCreateSlot(model, subNodes.item(i), tempContent, type,
              columnValue, null);

        }

      }

    }

    if (!fieldStyleSet.contains(type)) {
      JTable table = new JTable(model);
      JScrollPane tablePane = createScrollPane(table);
      eventCenterPanel.add(tablePane);
      eventCenterPanel.add(createButtonPanel(model, table, type, node, null));
      eventSubPanel.add(eventCenterPanel, BorderLayout.CENTER);
      tableType.put(type, table);
    } else {

      Vector<String> tempLabels = labelTypeMap.get(type);
      Vector<String> tempFields = fieldTypeMap.get(type);
      FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
          initialStateUITab);
      Vector<FieldsEdit> fieldsContainer = new Vector<FieldsEdit>();
      fieldsContainer.add(fieldsEdit);
      fieldsTypeMap.put(type, fieldsContainer);
      JPanel entry = fieldsEdit.createGridLayoutPanel();
      eventBottomPanel.add(entry);
      eventSubPanel.add(eventBottomPanel, BorderLayout.SOUTH);
      contentPanelTypeMap.put(type, eventBottomPanel);

    }
    return eventSubPanel;

  }

  public JPanel createGlobalVariableContent(NodeList nodes) {

    String type = "globalVariable";
    JPanel globalVariablePanel = createContentPanel("GlobalVariables");
    Vector<String> tempLabels = new Vector<String>();
    Vector<String> tempFields = new Vector<String>();
    Vector<String> tempContent = userInterfaceMap.get("globalVariable");

    for (int i = 0; i < nodes.getLength(); i++) {

      String name = sd.getNodeContent("@name", nodes.item(i));
      String value = "";
      if (nodes.item(i).hasChildNodes()) {
        NodeList languages = nodes.item(i).getChildNodes();
        for (int j = 0; j < languages.getLength(); j++) {
          Node child = languages.item(j);
          if (child instanceof Element) {
            String language = sd.getNodeContent("@language", languages.item(j));
            if (language.equalsIgnoreCase("java")) {
              value = sd.getNodeContent("text()", languages.item(j));
              processSlotValueExpr("globalVariable", nodes.item(i), name, null);
            }
          }
        }
      } else {
        value = sd.getNodeContent("@value", nodes.item(i));
      }

      tempFields.add(value);
      Iterator<String> it = tempContent.iterator();
      while (it.hasNext()) {
        String tempString = (String) it.next();
        if (tempString.endsWith("en")) {
          if ((tempString.substring(0,
              (tempString.length() - type.length() - 2))).equals(name)) {

            String label = labelMap.get(tempString);
            tempLabels.add(label);
          }
        }
      }

    }

    labelTypeMap.put(type, tempLabels);
    fieldTypeMap.put(type, tempFields);
    FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
        initialStateUITab);
    Vector<FieldsEdit> fieldsContainer = new Vector<FieldsEdit>();
    fieldsContainer.add(fieldsEdit);
    JPanel entry = fieldsEdit.createGridLayoutPanel();
    globalVariablePanel.add(entry);
    contentPanelTypeMap.put(type, globalVariablePanel);
    fieldsTypeMap.put(type, fieldsContainer);

    return globalVariablePanel;

  }

  public JPanel createObjectEventPanel(Node node, String type, String objectType) {

    String objectEventType = objectType + type;
    String title = type + "<<" + node.getNodeName() + ">>";

    DefaultTableModel model = null;

    if (!fieldStyleSet.contains(type)) {
      model = createTableModel(type, node);

    } else {
      createFieldModel(type, node);

    }

    if (node.hasChildNodes()) {

      Vector<String> tempContent = userInterfaceMap.get(type);
      NodeList subNodes = node.getChildNodes();
      for (int i = 0; i < subNodes.getLength(); i++) {
        if (subNodes.item(i).getNodeName().equals("Slot")
            | subNodes.item(i).getNodeName().equals("BeliefSlot")) {
          Vector<String> columnValue = new Vector<String>();
          processCreateSlot(model, subNodes.item(i), tempContent, type,
              columnValue, objectType);
        }
      }
    }

    JPanel objectEventPanel = createContentPanel(title);

    if (!fieldStyleSet.contains(type)) {

      JTable table = new JTable(model);
      JScrollPane tablePane = createScrollPane(table);
      objectEventPanel.add(tablePane);
      objectEventPanel.add(createButtonPanel(model, table, type, node,
          objectType));
      tableType.put(objectEventType, table);

    } else {

      Vector<String> tempLabels = labelTypeMap.get(type);
      Vector<String> tempFields = fieldTypeMap.get(type);
      FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
          initialStateUITab);
      Vector<FieldsEdit> fieldsContainer = new Vector<FieldsEdit>();
      fieldsContainer.add(fieldsEdit);
      JPanel entry = fieldsEdit.createGridLayoutPanel();
      objectEventPanel.add(entry);
      contentPanelTypeMap.put(objectEventType, objectEventPanel);
      fieldsTypeMap.put(objectEventType, fieldsContainer);

    }

    if ((objectObjectEventMap.keySet()).contains(objectType)) {

      HashSet<String> tempSet = objectObjectEventMap.get(objectType);
      tempSet.add(objectEventType);
      objectObjectEventMap.put(objectType, tempSet);

    } else {

      HashSet<String> newObjectEventTypeSet = new HashSet<String>();
      newObjectEventTypeSet.add(objectEventType);
      objectObjectEventMap.put(objectType, newObjectEventTypeSet);

    }

    return objectEventPanel;

  }

  public void addObjectEventContent(String type, String objectEventType,
      Node node) {

    Vector<String> header = new Vector<String>();
    Vector<String> rowDatumTemp = new Vector<String>();
    DefaultTableModel tempModel = null;

    if (!fieldStyleSet.contains(type)) {

      JTable tempTable = tableType.get(objectEventType);

      for (int j = 0; j < tempTable.getColumnCount(); j++) {
        String colHeadValue = (String) tempTable.getColumnModel().getColumn(j)
            .getHeaderValue();
        header.addElement(colHeadValue);
      }

      tempModel = (DefaultTableModel) tempTable.getModel();
      NamedNodeMap attrs = node.getAttributes();

      rowDatumTemp = createRowDatum(attrs, type, header);

    } else {

      createFieldModel(type, node);

    }

    if (node.hasChildNodes()) {
      NodeList children = node.getChildNodes();
      Vector<String> tempContent = userInterfaceMap.get(type);
      for (int i = 0; i < children.getLength(); i++) {

        String subNodeName = children.item(i).getNodeName();
        if (subNodeName.equals("Slot") | subNodeName.equals("BeliefSlot")) {

          String objectType = objectEventType.substring(0, (objectEventType
              .length() - type.length()));
          processAddSlot(header, children.item(i), tempContent, type,
              rowDatumTemp, objectType);

        }
      }
    }

    if (!fieldStyleSet.contains(type)) {

      tempModel.addRow(rowDatumTemp);

    } else {

      Vector<String> tempLabels = labelTypeMap.get(type);
      Vector<String> tempFields = fieldTypeMap.get(type);
      FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
          initialStateUITab);
      JPanel entry = fieldsEdit.createGridLayoutPanel();
      JPanel tempPanel = contentPanelTypeMap.get(objectEventType);
      tempPanel.add(entry);
      fieldsTypeMap.get(objectEventType).add(fieldsEdit);

    }

  }

  public JPanel createButtonPanel(DefaultTableModel model, JTable table,
      String type, Node node, String objectType) {

    Vector<JButton> buttonContainer = new Vector<JButton>();

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    HashSet<String> tempSet = lanType.get(type);
    Vector<String> tempVector = new Vector<String>();
    Iterator<String> it = tempSet.iterator();
    while (it.hasNext()) {
      String temp = (String) it.next();
      tempVector.addElement(temp);
    }

    JButton copy = new JButton("Copy");
    copy.addActionListener(new InitialStateUIController(table, model, type,
        node, userInterfaceMap));
    buttonContainer.add(copy);

    JButton del = new JButton("Del");
    del.addActionListener(new InitialStateUIController(table, model, type,
        node, userInterfaceMap));
    buttonContainer.add(del);

    JButton createNew = new JButton("New");
    createNew.addActionListener(new InitialStateUIController(table, model,
        type, node, userInterfaceMap));
    buttonContainer.add(createNew);

    JButton edit = new JButton("Edit");
    edit.addActionListener(new InitialStateUIController(table, model, type,
        node, userInterfaceMap));
    buttonContainer.add(edit);

    JComboBox lanBox = new JComboBox(tempVector);
    lanBox.setSelectedItem("en");
    lanBox.addActionListener(new LanguageBoxHandler(type, table, labelMap,
        hintMap, userInterfaceMap, buttonContainer));

    String valueExprType = null;

    if (objectType != null) {

      valueExprType = objectType + type;
    } else {

      valueExprType = type;
    }

    if (valueExprLanMap.keySet().contains(valueExprType)) {

      HashSet<String> tempPLanSet = valueExprLanMap.get(valueExprType);
      Vector<String> tempPLanVector = new Vector<String>();
      Iterator<String> pIt = tempPLanSet.iterator();
      while (pIt.hasNext()) {
        String pLantemp = (String) pIt.next();
        tempPLanVector.addElement(pLantemp);
      }

      JComboBox pLanBox = new JComboBox(tempPLanVector);
      pLanBox.setSelectedItem("Java");
      pLanBox.addActionListener(new ChooseLanguageHandler(table, valueExprMap,
          type, lanType, labelMap, objectType));

      pLanTypeMap.put(valueExprType, pLanBox);
      buttonPanel.add(pLanBox);

    }

    buttonPanel.add(lanBox);
    buttonPanel.add(copy);
    buttonPanel.add(del);
    buttonPanel.add(createNew);
    buttonPanel.add(edit);

    return buttonPanel;

  }

  public Vector<String> createRowHeader(NamedNodeMap hattrs, String type) {

    Vector<String> rowHeaderTemp = new Vector<String>();
    Vector<String> tempContent = userInterfaceMap.get(type);

    for (int k = 0; k < hattrs.getLength(); k++) {
      Attr attribute = (Attr) hattrs.item(k);
      if ((attribute.getName().equals("id"))
          | (attribute.getName().equals("idRef"))
          | (attribute.getName().equals("rangeStartID"))
          | (attribute.getName().equals("rangeEndID"))) {
        rowHeaderTemp.addElement(attribute.getName());
      } else {
        Iterator<String> it = tempContent.iterator();
        while (it.hasNext()) {
          String tempString = (String) it.next();
          if (tempString.endsWith("en")) {

            if ((!(attribute.getName()).equals("type") && (tempString
                .substring(0, (tempString.length() - type.length() - 2)))
                .equals(attribute.getName()))) {

              String label = labelMap.get(tempString);
              rowHeaderTemp.addElement(label);
            }

          }
        }
      }
    }

    int idPosition = rowHeaderTemp.indexOf("id");
    // System.out.println("The id position is: ===> " + idPosition);
    if (idPosition > 0) {
      rowHeaderTemp.remove(idPosition);
      rowHeaderTemp.insertElementAt("id", 0);
    }

    int idRefPosition = rowHeaderTemp.indexOf("idRef");

    if (idRefPosition > 0) {
      rowHeaderTemp.remove(idRefPosition);
      rowHeaderTemp.insertElementAt("idRef", 0);
    }

    int rangeStartIDPosition = rowHeaderTemp.indexOf("rangeStartID");

    if (rangeStartIDPosition > 0) {
      rowHeaderTemp.remove(rangeStartIDPosition);
      rowHeaderTemp.insertElementAt("rangeStartID", 0);
    }

    int rangeEndIDPosition = rowHeaderTemp.indexOf("rangeEndID");

    if ((rangeEndIDPosition > 0) & (rangeEndIDPosition != 1)) {
      rowHeaderTemp.remove(rangeEndIDPosition);
      rowHeaderTemp.insertElementAt("rangeEndID", 1);
    }

    return rowHeaderTemp;
  }

  public Vector<Vector> createRowData(NamedNodeMap rattrs, String type,
      Vector<String> rowHeader) {

    Vector<Vector> rowDataTemp = new Vector<Vector>();
    rowDataTemp.add(createRowDatum(rattrs, type, rowHeader));
    return rowDataTemp;

  }

  public Vector createRowDatum(NamedNodeMap rattrs, String type,
      Vector<String> rowHeader) {

    Vector rowDatumTemp = new Vector();
    Vector<String> tempContent = userInterfaceMap.get(type);

    for (int i = 0; i < rowHeader.size(); i++) {
      rowDatumTemp.addElement(null);
    }

    for (int k = 0; k < rattrs.getLength(); k++) {
      Attr attribute = (Attr) rattrs.item(k);
      if ((attribute.getName().equals("id"))
          | (attribute.getName().equals("idRef"))
          | (attribute.getName().equals("rangeStartID"))) {

        rowDatumTemp.set(0, attribute.getValue());
      }

      if ((attribute.getName().equals("rangeEndID"))) {

        rowDatumTemp.set(1, attribute.getValue());
      } else {
        Iterator<String> it = tempContent.iterator();
        while (it.hasNext()) {

          String tempString = (String) it.next();
          if (tempString.endsWith("en")) {

            if ((!(attribute.getName()).equals("type") & (tempString.substring(
                0, (tempString.length() - type.length() - 2))).equals(attribute
                .getName())))

              if (!fieldStyleSet.contains(type)) {

                if (attribute.getValue().equals("true")
                    || attribute.getValue().equals("false")) {

                  Boolean flag = Boolean.valueOf(attribute.getValue());
                  rowDatumTemp.set(rowHeader.indexOf(labelMap.get(tempString)),
                      flag);

                } else if (constrainNameMapRange.keySet().contains(
                    attribute.getName())) {

                  Integer intValue = Integer.valueOf(attribute.getValue());
                  rowDatumTemp.set(rowHeader.indexOf(labelMap.get(tempString)),
                      intValue);

                }

                else {

                  rowDatumTemp.set(rowHeader.indexOf(labelMap.get(tempString)),
                      attribute.getValue());
                }
              } else {

                rowDatumTemp.set(rowHeader.indexOf(labelMap.get(tempString)),
                    attribute.getValue());
              }

          }
        }
      }
    }
    return rowDatumTemp;
  }

  public JPanel createSaveButtonPanel() {

    Vector<JButton> buttonContainer = new Vector<JButton>();

    fileChoose = new JFileChooser();
    fileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChoose.setFileFilter(new ChooseFilter("xml", "XML Files"));

    try {
      File dir = (new File(".")).getCanonicalFile();
      fileChoose.setCurrentDirectory(dir);
    } catch (IOException e) {
      e.printStackTrace();
    }

    JPanel saveButtonPanel = new JPanel();
    saveButtonPanel.setBorder(BorderFactory.createTitledBorder("Save To File"));
    saveButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

    save = new JButton("Save");
    save.setToolTipText("Save the table content to the original File");
    saveAs = new JButton("Save As...");
    saveAs.setToolTipText("Save the table content to another File");

    HashSet<String> tempLanSetAll = new HashSet<String>();
    for (Iterator<String> it = lanType.keySet().iterator(); it.hasNext();) {

      String tempType = it.next();
      HashSet<String> tempLanSet = lanType.get(tempType);
      tempLanSetAll.addAll(tempLanSet);

    }

    Vector<String> tempVector = new Vector<String>();
    Iterator<String> it = tempLanSetAll.iterator();
    while (it.hasNext()) {
      String temp = (String) it.next();
      tempVector.addElement(temp);
    }

    JComboBox lanBox = new JComboBox(tempVector);
    lanBox.setSelectedItem("en");
    lanBox.addActionListener(new FieldsLanguageBoxHandler(fieldsTypeMap,
        labelMap, hintMap, userInterfaceMap, objectObjectEventMap,
        buttonContainer));

    HashSet<String> tempPLanSetAll = new HashSet<String>();
    for (Iterator<String> pIt = valueExprLanMap.keySet().iterator(); pIt
        .hasNext();) {

      String tempPType = pIt.next();
      HashSet<String> tempPLanSet = valueExprLanMap.get(tempPType);
      tempPLanSetAll.addAll(tempPLanSet);

    }

    Vector<String> tempPLanVector = new Vector<String>();
    Iterator<String> pLanIt = tempPLanSetAll.iterator();
    while (pLanIt.hasNext()) {
      String tempLan = (String) pLanIt.next();
      tempPLanVector.addElement(tempLan);
    }

    JComboBox pLanBox = new JComboBox(tempPLanVector);
    pLanBox.setSelectedItem("Java");
    pLanBox.addActionListener(new ChooseLanguageFieldsHandler(fieldsTypeMap,
        labelMap, lanType, valueExprMap, valueExprLanMap, objectObjectEventMap,
        userInterfaceMap));

    for (Iterator<String> pLanFieldTypes = pLanFieldTypeSet.iterator(); pLanFieldTypes
        .hasNext();) {

      String tempType = pLanFieldTypes.next();
      pLanTypeMap.put(tempType, pLanBox);

    }

    saveButtonPanel.add(pLanBox);
    saveButtonPanel.add(lanBox);
    saveButtonPanel.add(save);
    saveButtonPanel.add(saveAs);
    ActionListener listenSave = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {

        processDom();
        if (fieldValidateTypeMap != null) {
          for (Iterator<String> it = fieldValidateTypeMap.keySet().iterator(); it
              .hasNext();) {

            String tempType = it.next();
            Vector<Vector<Boolean>> tempBooleanContainer = fieldValidateTypeMap
                .get(tempType);
            for (int b = 0; b < tempBooleanContainer.size(); b++) {

              if (tempBooleanContainer.get(b).contains(false)) {

                return;
              }
            }
          }
        }
        saveFile(false, save);

      }
    };

    ActionListener listenSaveAs = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {

        processDom();

        if (fieldValidateTypeMap != null) {
          for (Iterator<String> it = fieldValidateTypeMap.keySet().iterator(); it
              .hasNext();) {

            String tempType = it.next();
            Vector<Vector<Boolean>> tempBooleanContainer = fieldValidateTypeMap
                .get(tempType);
            for (int b = 0; b < tempBooleanContainer.size(); b++) {

              if (tempBooleanContainer.get(b).contains(false)) {

                return;
              }
            }
          }
        }
        saveFile(true, saveAs);

      }
    };

    save.addActionListener(listenSave);
    saveAs.addActionListener(listenSaveAs);

    return saveButtonPanel;
  }

  private boolean saveFile(boolean changeFile, JButton button) {

    if (dom == null)
      return false;

    if (changeFile || file == null) {
      if (fileChoose.showSaveDialog((Frame) SwingUtilities.getRoot(button)) != JFileChooser.APPROVE_OPTION) {
        return false;
      }

      File f = fileChoose.getSelectedFile();
      if (f == null) {
        return false;
      }
      this.file = f;
    }

    callWriteXmlFile(dom, this.file, "utf-8");

    return true;

  }

  public void processDom() {

    HashSet<String> savedTypeContainer = new HashSet<String>();
    Node InitialState = sd.getNode(IS);
    NodeList children = InitialState.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {

      Node child = children.item(i);
      if (child instanceof Element) {
        String nodeName = child.getNodeName();

        if (nodeName.equals("PhysicalObject")
            || nodeName.equals("PhysicalAgent") || nodeName.equals("Object")
            || nodeName.equals("PhysicalAgents") || nodeName.equals("Agents")
            || nodeName.equals("Agent") || nodeName.equals("PhysicalObjects")) {

          String type = sd.getNodeContent("@type", child);
          if (typeUITableHead.contains(type)) {
            if (!savedTypeContainer.contains(type)) {
              savedTypeContainer.add(type);

              if (!fieldStyleSet.contains(type)) {

                JTable table = tableType.get(type);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                Vector<Vector> dataRows = model.getDataVector();

                NodeList objectsAfterDel = prepareProcess(type, InitialState,
                    dataRows);

                for (int l = 0; l < objectsAfterDel.getLength(); l++) {

                  Vector<String> attrNames = createAttributeContainer(objectsAfterDel
                      .item(l));

                  processPropertyEdit(model, dataRows, type, attrNames, l,
                      objectsAfterDel.item(l), null);

                  NodeList objectEventNodes = sd.getNodeList(
                      "*[contains(name(),'Event')]", objectsAfterDel.item(l));
                  if (objectEventNodes.getLength() > 0) {
                    processSubNodes(objectEventNodes, objectsAfterDel.item(l),
                        l, type);
                  }

                  NodeList beliefEntityNodes = sd.getNodeList(
                      "*[contains(name(),'BeliefEntity')]", objectsAfterDel
                          .item(l));
                  if (beliefEntityNodes.getLength() > 0) {
                    processSubNodes(beliefEntityNodes, objectsAfterDel.item(l),
                        l, type);
                  }

                }
              } else {

                NodeList nodes = (NodeList) sd.getNodeList("*[@type='" + type
                    + "']", InitialState);
                Vector<FieldsEdit> fieldsContainer = fieldsTypeMap.get(type);

                for (int f = 0; f < fieldsContainer.size(); f++) {

                  Vector<String> attrNames = createAttributeContainer(nodes
                      .item(f));
                  Vector<String> tempLabels = labelTypeMap.get(type);
                  FieldsEdit tempFieldEdit = fieldsContainer.get(f);
                  Vector<String> savedFields = tempFieldEdit.saveProcess(
                      tempFieldEdit.getFieldsContainer(), type, null);

                  processFieldProperty(tempLabels, savedFields, nodes.item(f),
                      type, attrNames, null);

                  NodeList objectEventNodes = sd.getNodeList(
                      "*[contains(name(),'Event')]", nodes.item(f));
                  if (objectEventNodes.getLength() > 0) {
                    processSubNodes(objectEventNodes, nodes.item(f), f, type);
                  }

                  NodeList beliefEntityNodes = sd.getNodeList(
                      "*[contains(name(),'BeliefEntity')]", nodes.item(f));
                  if (beliefEntityNodes.getLength() > 0) {
                    processSubNodes(beliefEntityNodes, nodes.item(f), f, type);
                  }
                }
              }
            }
          }
        }

        if (nodeName.contains("Event")) {

          String type = sd.getNodeContent("@type", child);
          if (typeUITableHead.contains(type)) {
            if (!savedTypeContainer.contains(type)) {
              savedTypeContainer.add(type);

              if (!fieldStyleSet.contains(type)) {
                JTable table = tableType.get(type);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                Vector<Vector> dataRows = model.getDataVector();

                NodeList eventsAfterDel = prepareProcess(type, InitialState,
                    dataRows);
                for (int l = 0; l < eventsAfterDel.getLength(); l++) {

                  Vector<String> attrNames = createAttributeContainer(eventsAfterDel
                      .item(l));
                  processPropertyEdit(model, dataRows, type, attrNames, l,
                      eventsAfterDel.item(l), null);

                }
              } else {

                NodeList nodes = sd.getNodeList("*[@type='" + type + "']",
                    InitialState);
                Vector<FieldsEdit> fieldsContainer = fieldsTypeMap.get(type);

                for (int f = 0; f < fieldsContainer.size(); f++) {

                  Vector<String> attrNames = createAttributeContainer(nodes
                      .item(f));
                  Vector<String> tempLabels = labelTypeMap.get(type);
                  FieldsEdit tempFieldEdit = fieldsContainer.get(f);
                  Vector<String> savedFields = tempFieldEdit.saveProcess(
                      tempFieldEdit.getFieldsContainer(), type, null);
                  processFieldProperty(tempLabels, savedFields, nodes.item(f),
                      type, attrNames, null);

                }
              }
            }
          }
        }

        if (nodeName.contains("GlobalVariable")) {
          if (typeUITableHead.contains("globalVariable")) {
            if (!savedTypeContainer.contains("globalVariable")) {
              savedTypeContainer.add("globalVariable");

              Vector<FieldsEdit> tempFieldsContainer = fieldsTypeMap
                  .get("globalVariable");
              NodeList globalVariables = sd.getNodeList(PX + "GlobalVariable",
                  InitialState);

              Vector<String> tempLabels = labelTypeMap.get("globalVariable");
              FieldsEdit tempFieldsEdit = tempFieldsContainer.get(0);
              Vector<String> savedFields = tempFieldsEdit.saveProcess(
                  tempFieldsEdit.getFieldsContainer(), "globalVariable", null);
              for (int j = 0; j < tempLabels.size(); j++) {

                String tableHeadElement = tempLabels.get(j);
                String value = savedFields.get(j);
                ;

                Vector<String> tempContent = userInterfaceMap
                    .get("globalVariable");
                for (Iterator<String> it = tempContent.iterator(); it.hasNext();) {

                  String tempString = it.next();
                  String label = labelMap.get(tempString);
                  if (label.equals(tableHeadElement)) {
                    String tempPropertyName = tempString.substring(0,
                        (tempString.length() - 16));

                    for (int g = 0; g < globalVariables.getLength(); g++) {
                      String name = ((Element) globalVariables.item(g))
                          .getAttribute("name");

                      if (name.equals(tempPropertyName)) {
                        if (!(globalVariables.item(g)).hasChildNodes()) {
                          setAttribute((Element) globalVariables.item(g),
                              GlobalValue, value);
                        } else {

                          if (pLanTypeMap.keySet().contains("globalVariable")) {

                            JComboBox tempBox = pLanTypeMap
                                .get("globalVariable");
                            String tempLan = (String) tempBox.getSelectedItem();
                            Node node = sd.getNode(PX + "ValueExpr[@language='"
                                + tempLan + "']", globalVariables.item(g));
                            if (node != null) {
                              node.setTextContent(value);
                            }

                          } else {
                            Node currentNode = sd.getNode(GlobalSubNodeValue,
                                globalVariables.item(g));
                            currentNode.setTextContent(value);
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
    }
  }

  public void processPropertyEdit(DefaultTableModel model,
      Vector<Vector> dataRows, String type, Vector<String> attrNames, int l,
      Node editNode, String objectType) {

    String tempPropertyValue = null;
    for (int j = 0; j < model.getColumnCount(); j++) {

      String tableHeadElement = model.getColumnName(j);

      if (tableHeadElement.equals("id") | tableHeadElement.equals("idRef")
          | tableHeadElement.equals("rangeStartID")
          | tableHeadElement.equals("rangeEndID")) {
        tempPropertyValue = (String) dataRows.get(l).get(j);
        setAttribute((Element) editNode, tableHeadElement, tempPropertyValue);
      }

      else {

        if ((dataRows.get(l).get(j).getClass().getName())
            .equals("java.lang.Boolean")) {

          tempPropertyValue = String.valueOf(dataRows.get(l).get(j));

        } else if ((dataRows.get(l).get(j).getClass().getName())
            .equals("java.lang.Integer")) {

          tempPropertyValue = String.valueOf(dataRows.get(l).get(j));
        }

        else {
          tempPropertyValue = (String) dataRows.get(l).get(j);

        }

        processNormalProperty(type, tableHeadElement, attrNames, editNode,
            tempPropertyValue, objectType);

      }
    }
  }

  public void processFieldProperty(Vector<String> tempLabels,
      Vector<String> savedFields, Node node, String type,
      Vector<String> attrNames, String objectType) {

    for (int l = 0; l < tempLabels.size(); l++) {

      String tempLabel = tempLabels.get(l);
      String tempPropertyValue = savedFields.get(l);

      if (tempLabel.equals("id") | tempLabel.equals("idRef")
          | tempLabel.equals("rangeStartID") | tempLabel.equals("rangeEndID")

      ) {

        setAttribute((Element) node, tempLabel, tempPropertyValue);
      } else {

        processNormalProperty(type, tempLabel, attrNames, node,
            tempPropertyValue, objectType);

      }
    }
  }

  public void processNormalProperty(String type, String tableHeadElement,
      Vector<String> attrNames, Node editNode, String tempPropertyValue,
      String objectType) {

    String pLanType = null;

    if (objectType != null) {

      pLanType = objectType + type;
    } else {

      pLanType = type;
    }

    Vector<String> tempContent = userInterfaceMap.get(type);
    Iterator<String> it = tempContent.iterator();
    while (it.hasNext()) {

      String tempString = (String) it.next();
      String label = labelMap.get(tempString);
      if (label.equals(tableHeadElement)) {
        String tempPropertyName = tempString.substring(0, (tempString.length()
            - type.length() - 2));

        if (attrNames.contains(tempPropertyName)) {

          setAttribute((Element) editNode, tempPropertyName, tempPropertyValue);

        } else if (editNode.hasChildNodes()) {

          NodeList subChildren = editNode.getChildNodes();
          for (int h = 0; h < subChildren.getLength(); h++) {
            String subNodeName = subChildren.item(h).getNodeName();

            if (subNodeName.equals("Slot")
                || subNodeName.equals("SelfBeliefSlot")
                || subNodeName.equals("BeliefSlot")) {
              String property = sd.getNodeContent("@property", subChildren
                  .item(h));
              if (tempPropertyName.equals(property)) {
                if (subChildren.item(h).hasChildNodes()) {

                  JComboBox tempBox = pLanTypeMap.get(pLanType);
                  String tempLan = (String) tempBox.getSelectedItem();
                  Node node = sd.getNode(PX + "ValueExpr[@language='" + tempLan
                      + "']", subChildren.item(h));
                  if (node != null) {

                    node.setTextContent(tempPropertyValue);

                  }

                } else {
                  setAttribute((Element) subChildren.item(h), "value",
                      tempPropertyValue);
                }
              }
            } else if (subNodeName.contains("Range")) {
              if (subNodeName.equalsIgnoreCase(tempPropertyName)) {
                subChildren.item(h).setTextContent(tempPropertyValue);
              }
            }
          }
        }
      }
    }
  }

  public NodeList prepareProcess(String type, Node node, Vector<Vector> dataRows) {

    NodeList nodes = sd.getNodeList("*[@type='" + type + "']", node);
    Element parent = (Element) nodes.item(0).getParentNode();
    for (int r = 1; r < nodes.getLength(); r++) {
      parent.removeChild(nodes.item(r));
    }

    for (int d = 1; d < dataRows.size(); d++) {
      duplicateNode(nodes.item(0));
    }

    NodeList nodesAfterDel = sd.getNodeList("*[@type='" + type + "']", node);

    return nodesAfterDel;

  }

  public void processSubNodes(NodeList nodes, Node node, int l, String type) {

    // System.out.println("The type transfer into processSubNodes is: ===> " +
    // type);
    HashSet<String> objectEventTypeSet = null;

    objectEventTypeSet = new HashSet<String>();
    for (int e = 0; e < nodes.getLength(); e++) {

      Element event = (Element) nodes.item(e);
      String eventType = event.getAttribute("type");
      System.out.println("the content added into objectEventTypeSet: ===> "
          + eventType);
      objectEventTypeSet.add(eventType);

    }

    for (Iterator<String> it = objectEventTypeSet.iterator(); it.hasNext();) {

      String tempType = it.next();
      if (typeUITableHead.contains(tempType)) {
        // System.out.println("the next type is: ===> " + tempType);
        NodeList eventNodes = (NodeList) sd.getNodeList("*[@type='" + tempType
            + "']", node);
        String objectEventType = type + tempType;

        Vector<Vector> objectEventDataRows = null;
        DefaultTableModel objectEventModel = null;

        Vector<FieldsEdit> objectEventFieldsContainer = null;

        if (!fieldStyleSet.contains(tempType)) {
          JTable objectEventTable = tableType.get(objectEventType);
          objectEventModel = (DefaultTableModel) objectEventTable.getModel();
          objectEventDataRows = objectEventModel.getDataVector();
        } else {

          objectEventFieldsContainer = fieldsTypeMap.get(objectEventType);
        }
        int oen = 0;
        for (int r = (l * eventNodes.getLength()); r < (l
            * eventNodes.getLength() + eventNodes.getLength()); r++) {

          Node eventNode = eventNodes.item(oen);
          Vector<String> objectEventAttrNames = createAttributeContainer(eventNode);

          if (!fieldStyleSet.contains(tempType)) {
            processPropertyEdit(objectEventModel, objectEventDataRows,
                tempType, objectEventAttrNames, r, eventNode, type);
          } else {

            for (int e = 0; e < objectEventFieldsContainer.size(); e++) {
              Vector<String> tempLabels = labelTypeMap.get(tempType);
              FieldsEdit tempFieldEdit = objectEventFieldsContainer.get(e);
              Vector<String> savedFields = tempFieldEdit
                  .saveProcess(tempFieldEdit.getFieldsContainer(), tempType,
                      objectEventType);
              processFieldProperty(tempLabels, savedFields, eventNode,
                  tempType, objectEventAttrNames, type);
            }
          }
          oen++;
        }
      }
    }

  }

  public static void duplicateNode(Node node) {

    Element parentNode = (Element) node.getParentNode();
    Element newNode = (Element) node.cloneNode(true);
    parentNode.insertBefore(newNode, node);
  }

  public static void setAttribute(Element element, String name, String value) {

    element.setAttribute(name, value);
  }

  private static void callWriteXmlFile(Document doc, File fOut, String encoding) {

    StringBuilder stringBuilder = null;
    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    // only to format the XML - this can be dropped is no format is needed
    OutputFormat outputformat = new OutputFormat();
    outputformat.setIndent(4);
    outputformat.setIndenting(true);
    outputformat.setPreserveSpace(false);

    // create and use the XML serializer
    XMLSerializer serializer = new XMLSerializer();
    serializer.setOutputFormat(outputformat);
    serializer.setOutputByteStream(stream);
    try {
      serializer.asDOMSerializer();
      serializer.serialize(doc.getDocumentElement());
      stringBuilder = new StringBuilder(stream.toString());

      System.out.println(stream.toString());

      FileWriter fileWriter = new FileWriter(fOut);
      fileWriter.write(stream.toString());

    } catch (IOException e) {
      System.out.println("Errors on InitialStateUITab.callWriteXmlFile(): "
          + e.toString());
    }

    /*
     * DOMImplementation domImplementation = doc.getImplementation();
     * DOMImplementationLS domImplementationLS = (DOMImplementationLS)
     * domImplementation .getFeature("LS", "3.0"); LSSerializer lsSerializer =
     * domImplementationLS.createLSSerializer();
     * lsSerializer.getDomConfig().setParameter("format-pretty-print",
     * Boolean.TRUE);// LSOutput lsOutput =
     * domImplementationLS.createLSOutput(); lsOutput.setEncoding("UTF-8");
     * StringWriter stringWriter = new StringWriter();
     * lsOutput.setCharacterStream(stringWriter); lsSerializer.write(doc,
     * lsOutput);
     * 
     * FileOutputStream out;
     * 
     * try { out = new FileOutputStream(fOut, false); DataOutputStream dataOut =
     * new DataOutputStream(out); byte[] data =
     * stringWriter.toString().getBytes(); dataOut.write(data, 0, data.length);
     * out.close(); dataOut.close(); } catch (FileNotFoundException e) {
     * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
     */

  }

  public JPanel createSubPanel(String title) {

    JPanel subPanel = new JPanel();
    subPanel.setBorder(BorderFactory.createTitledBorder(title));
    subPanel.setLayout(new BorderLayout());
    return subPanel;

  }

  public JPanel createCenterPanel() {

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    return contentPanel;

  }

  public JScrollPane createScrollPane(JTable table) {

    JScrollPane tableScroll = new JScrollPane();
    tableScroll.getViewport().add(table);
    return tableScroll;

  }

  public JPanel createBottomPanel() {

    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
    return bottomPanel;

  }

  public JPanel createContentPanel(String title) {

    JPanel contentPanel = new JPanel();
    contentPanel.setBorder(BorderFactory.createTitledBorder(title));
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    return contentPanel;

  }

  public Vector<String> createAttributeContainer(Node node) {

    NamedNodeMap attrs = node.getAttributes();
    Vector<String> attrNames = new Vector<String>();
    for (int k = 0; k < attrs.getLength(); k++) {
      Attr attribute = (Attr) attrs.item(k);
      attrNames.add(attribute.getName());
    }

    return attrNames;
  }

  public DefaultTableModel createTableModel(String type, Node node) {

    NamedNodeMap attrs = node.getAttributes();
    Vector<String> rowHeader = createRowHeader(attrs, type);
    Vector<Vector> rowData = createRowData(attrs, type, rowHeader);
    DefaultTableModel model = new DefaultTableModel(rowData, rowHeader) {

      public Class<?> getColumnClass(int c) {

        return getValueAt(0, c).getClass();
      }
    };
    return model;
  }

  public void createFieldModel(String type, Node node) {

    NamedNodeMap attrs = node.getAttributes();
    Vector<String> labels = createRowHeader(attrs, type);
    Vector<String> fields = createRowDatum(attrs, type, labels);

    labelTypeMap.put(type, labels);
    fieldTypeMap.put(type, fields);
  }

  private void processCreateSlot(DefaultTableModel model, Node node,
      Vector<String> tempContent, String type, Vector columnValue,
      String objectType) {

    String property = sd.getNodeContent("@property", node);

    String value = "";
    if (node.hasChildNodes()) {
      value = sd.getNodeContent(PX + "ValueExpr[@language='Java']", node);
      processSlotValueExpr(type, node, property, objectType);
    } else {
      value = sd.getNodeContent("@value", node);
    }

    Iterator<String> it = tempContent.iterator();
    while (it.hasNext()) {
      String tempString = (String) it.next();
      if (tempString.endsWith("en")) {
        // System.out.println("The content of the tempString: ===> " +
        // tempString);
        // System.out.println("The content of the type: ===> " + type);

        if ((tempString.substring(0, (tempString.length() - type.length() - 2)))
            .equals(property)) {

          if (!fieldStyleSet.contains(type)) {
            if (value.equals("true") || value.equals("false")) {
              Boolean flag = Boolean.valueOf(value);
              columnValue.addElement(flag);
            } else if (constrainNameMapRange.keySet().contains(property)) {
              Integer intValue = Integer.valueOf(value);
              columnValue.addElement(intValue);
            }

            else {
              columnValue.addElement(value);
            }

            String tableLabel = labelMap.get(tempString);
            model.addColumn(tableLabel, columnValue);

          } else {

            String fieldLabel = labelMap.get(tempString);
            labelTypeMap.get(type).add(fieldLabel);
            fieldTypeMap.get(type).add(value);

          }
        }
      }
    }

  }

  public void processSlotValueExpr(String type, Node node, String property,
      String objectType) {

    NodeList valueExprs = sd.getNodeList(PX + "ValueExpr", node);

    String key = null;
    String valueExprType = null;

    for (int i = 0; i < valueExprs.getLength(); i++) {

      Node valueExprNode = valueExprs.item(i);
      String lan = sd.getNodeContent("@language", valueExprNode);

      if (objectType != null) {

        key = property + objectType + type + lan;
        valueExprType = objectType + type;

      } else {

        key = property + type + lan;
        valueExprType = type;
      }

      String value = valueExprNode.getFirstChild().getTextContent();
      // System.out.println("The value int valueExpr:=> " + value);

      if (!valueExprLanMap.keySet().contains(valueExprType)) {
        HashSet<String> lanSet = new HashSet<String>();
        lanSet.add(lan);
        valueExprLanMap.put(valueExprType, lanSet);

      } else {

        HashSet<String> tempLanSet = valueExprLanMap.get(valueExprType);
        tempLanSet.add(lan);
        valueExprLanMap.put(valueExprType, tempLanSet);
      }

      if (!valueExprMap.keySet().contains(key)) {

        Vector<String> values = new Vector<String>();
        values.add(value);
        valueExprMap.put(key, values);
      } else {

        Vector<String> tempValues = valueExprMap.get(key);
        tempValues.add(value);
        valueExprMap.put(key, tempValues);

      }

    }

    if (fieldStyleSet.contains(type)) {
      pLanFieldTypeSet.add(valueExprType);
    }
  }

  private void processAddSlot(Vector<String> header, Node node,
      Vector<String> tempContent, String type, Vector rowDatumTemp,
      String objectType) {

    String property = sd.getNodeContent("@property", node);
    String value = "";
    if (node.hasChildNodes()) {

      value = sd.getNodeContent(PX + "ValueExpr[@language='Java']", node);
      processSlotValueExpr(type, node, property, objectType);

    } else {
      value = sd.getNodeContent("@value", node);

    }

    Iterator<String> it = tempContent.iterator();
    while (it.hasNext()) {
      String tempString = (String) it.next();
      if (tempString.endsWith("en")) {
        if ((tempString.substring(0, (tempString.length() - type.length() - 2)))
            .equals(property)) {

          if (!fieldStyleSet.contains(type)) {
            if (value.equals("true") || value.equals("false")) {

              Boolean flag = Boolean.valueOf(value);
              rowDatumTemp.set(header.indexOf(labelMap.get(tempString)), flag);
            } else if (constrainNameMapRange.keySet().contains(property)) {
              Integer intValue = Integer.valueOf(value);
              rowDatumTemp.set(header.indexOf(labelMap.get(tempString)),
                  intValue);
            } else {

              rowDatumTemp.set(header.indexOf(labelMap.get(tempString)), value);
            }
          } else {

            String fieldLabel = labelMap.get(tempString);
            labelTypeMap.get(type).add(fieldLabel);
            fieldTypeMap.get(type).add(value);

          }
        }
      }
    }
  }

  public void processTableRenderer() {

    prepareProcessJSliderRender();
    prepareProcessJComboBoxRender();

  }

  public void prepareProcessJSliderRender() {

    Set<String> tableTypeKeySet = tableType.keySet();
    for (Iterator<String> it = tableTypeKeySet.iterator(); it.hasNext();) {

      String tempType = it.next();
      if (constrainMap.keySet().contains(tempType)) {
        JTable table = tableType.get(tempType);
        processJSliderRender(table, tempType);
      }
    }

    tableTypeKeySet = objectObjectEventMap.keySet();
    if (!tableTypeKeySet.isEmpty()) {
      for (Iterator<String> it = tableTypeKeySet.iterator(); it.hasNext();) {

        String objectType = it.next();
        HashSet<String> tempObjectEventTypeSet = objectObjectEventMap
            .get(objectType);

        for (Iterator<String> objectEventTypes = tempObjectEventTypeSet
            .iterator(); objectEventTypes.hasNext();) {

          String objectEventType = objectEventTypes.next();
          String eventType = objectEventType.substring(objectType.length());

          if (constrainMap.keySet().contains(eventType)) {
            JTable table = tableType.get(objectEventType);
            processJSliderRender(table, eventType);
          }
        }

      }

    }
  }

  public void prepareProcessJComboBoxRender() {

    Set<String> objectObjectEventKeySet = objectObjectEventMap.keySet();
    Collection<HashSet<String>> objectObjectEventValueSet = objectObjectEventMap
        .values();
    HashSet<String> allObjectEvent = new HashSet<String>();

    for (Iterator<HashSet<String>> it = objectObjectEventValueSet.iterator(); it
        .hasNext();) {
      HashSet<String> tempSet = it.next();
      allObjectEvent.addAll(tempSet);
    }

    Set<String> tableTypeKeySet = tableType.keySet();
    for (Iterator<String> tableTypeKeys = tableTypeKeySet.iterator(); tableTypeKeys
        .hasNext();) {

      String tableTypeKey = tableTypeKeys.next();
      if (allObjectEvent.contains(tableTypeKey)) {

        for (Iterator<String> keys = objectObjectEventKeySet.iterator(); keys
            .hasNext();) {

          String tempObjectType = keys.next();
          if (objectObjectEventMap.get(tempObjectType).equals(tableTypeKey)) {

            String userInterfaceType = tableTypeKey.substring(tempObjectType
                .length());
            JTable table = tableType.get(tableTypeKey);

          }
        }
      } else {

        JTable table = tableType.get(tableTypeKey);

      }

    }
  }

  public void processJComboBoxRender(JTable table, String type) {

    Set<String> enumPropertySet = enumMap.keySet();
    Vector<String> tempContent = userInterfaceMap.get(type);
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    Iterator<String> labelKeys = tempContent.iterator();
    while (labelKeys.hasNext()) {
      String labelKey = labelKeys.next();
      String property = labelKey.substring(0, (labelKey.length()
          - type.length() - 2));
      if (enumPropertySet.contains(property)) {
        HashSet<String> tempRange = enumMap.get(property);
        String[] valuesComboBox = new String[tempRange.size()];
        int k = 0;
        for (Iterator<String> it = tempRange.iterator(); it.hasNext();) {
          String tempString = it.next();
          valuesComboBox[k] = tempString;
          k++;
        }

        String constrainTableHeadName = labelMap.get(labelKey);
        for (int i = 0; i < model.getColumnCount(); i++) {
          String tableHeadName = model.getColumnName(i);
          if (constrainTableHeadName.equals(tableHeadName)) {
            TableColumn rendererComboBoxColumn = table.getColumnModel()
                .getColumn(i);
            /*
             * rendererComboBoxColumn.setCellRenderer(new ComboBoxRenderer(
             * valuesComboBox)); rendererComboBoxColumn.setCellEditor(new
             * ComboBoxEditor( valuesComboBox));
             */
          }
        }
      }
    }
  }

  public void processJSliderRender(JTable table, String type) {

    HashSet<String> tempConstrainSet = constrainMap.get(type);
    Vector<String> tempContent = userInterfaceMap.get(type);
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    Iterator<String> labelKeys = tempContent.iterator();
    while (labelKeys.hasNext()) {
      String labelKey = labelKeys.next();
      String property = labelKey.substring(0, (labelKey.length()
          - type.length() - 2));
      if (tempConstrainSet.contains(property)) {
        Vector<Integer> tempRange = constrainNameMapRange.get(property);

        int maxValue = tempRange.get(1);
        int minVlaue = tempRange.get(0);

        String constrainTableHeadName = labelMap.get(labelKey);
        for (int i = 0; i < model.getColumnCount(); i++) {
          String tableHeadName = model.getColumnName(i);
          if (constrainTableHeadName.equals(tableHeadName)) {
            /*
             * TableColumn rendererSliderColumn =
             * table.getColumnModel().getColumn(i);
             * rendererSliderColumn.setCellEditor(new
             * JSliderTableEditor(minVlaue,maxValue));
             */

          }
        }
      }
    }
  }

  public HashMap<String, Vector<String>> getUserInterfaceMap() {

    return userInterfaceMap;
  }

  public HashMap<String, HashSet<String>> getObjectObjectEventMap() {

    return objectObjectEventMap;
  }

  public HashMap<String, HashSet<String>> getLanType() {

    return lanType;
  }

  public HashMap<String, String> getLabelMap() {

    return labelMap;
  }

  public HashMap<String, Vector<String>> getSelfPropertyMap() {

    return selfPropertyMap;
  }

  public HashMap<String, HashSet<String>> getConstrainMap() {

    return constrainMap;
  }

  public HashMap<String, HashSet<String>> getEnumMap() {

    return enumMap;
  }

  public HashMap<String, Vector<Integer>> getConstrainNameMapRange() {

    return constrainNameMapRange;
  }

  private JPanel initialStatePanel;
  private Document dom;
  private InitialStateUIController controller;
  private SimulationDescription sd;
  private HashSet<String> typeUITableHead = null;
  private HashMap<String, String> labelMap = null;
  private HashMap<String, String> hintMap = null;
  private HashMap<String, HashSet<String>> lanType = null;
  private HashMap<String, Vector<String>> userInterfaceMap = null;
  private HashMap<String, JTable> tableType = null;
  private HashMap<String, Vector<String>> labelTypeMap = null;
  private HashMap<String, Vector<String>> fieldTypeMap = null;
  private HashMap<String, Vector<FieldsEdit>> fieldsTypeMap = null;
  private Vector<String> userInterfaceVariableVector = null;
  private HashSet<String> lanVariableSet = null;
  private HashSet<String> typeSet = null;
  private JFileChooser fileChoose;
  private JButton save, saveAs;
  private File file = null;
  private HashSet<String> objectEventTypeSet = null;
  private HashMap<String, HashSet<String>> objectObjectEventMap = null;
  private HashSet<String> fieldStyleSet = null;
  private HashMap<String, Integer> fieldNumberMap = null;
  private HashMap<String, JPanel> contentPanelTypeMap = null;
  private HashMap<String, Vector<String>> selfPropertyMap = null;
  private HashMap<String, HashSet<String>> constrainMap = null;
  private HashMap<String, Vector<Integer>> constrainNameMapRange = null;
  private InitialStateUITab initialStateUITab = null;
  private Vector<Boolean> fieldValidateContainer = null;
  private HashMap<String, Vector<Vector<Boolean>>> fieldValidateTypeMap = null;
  private HashMap<String, HashSet<String>> enumMap = null;
  private HashMap<String, HashSet<String>> valueExprLanMap = null;
  private HashMap<String, Vector<String>> valueExprMap = null;
  private HashMap<String, JComboBox> pLanTypeMap = null;
  private HashSet<String> pLanFieldTypeSet = null;
  private final String PX = SimulationDescription.ER_AOR_PREFIX + ":";
  private final String IS = "/" + PX + "SimulationScenario/" + PX
      + "InitialState";
  private final String INITIALSTATEUI = "/" + PX + "SimulationScenario/" + PX
      + "UserInterface/" + PX + "InitialStateUI";
  private final String GV = "/" + PX + "SimulationScenario/" + PX
      + "InitialState/" + PX + "GlobalVariable";
  private final String CONSTRAINNODE = "/" + PX + "SimulationScenario/" + PX
      + "SimulationModel/" + PX + "EntityTypes";
  private final String LT = PX + "Label/" + PX + "Text";
  private final String HT = PX + "Hint/" + PX + "Text";
  private final String XLAN = "@xml:lang";
  private final String GlobalValue = "value";
  private final String GlobalSubNodeValue = PX + "ValueExpr[@language='Java']";
  private final String ENUMPROPERTY = "//" + PX + "EnumerationProperty";

  public class FieldsEdit {

    public FieldsEdit(Vector<String> labelsContainer,
        Vector<String> fieldsContainer, InitialStateUITab initialStateUITab) {

      this.labelsContainer = labelsContainer;
      this.fieldsContainer = fieldsContainer;
      this.initialStateUITab = initialStateUITab;
      labels = new JLabel[labelsContainer.size()];
      fields = new JTextField[fieldsContainer.size()];
    }

    public Vector<String> getLabelsContainer() {
      return labelsContainer;
    }

    public Vector<String> getFieldsContainer() {
      return fieldsContainer;
    }

    public Vector<JLabel> getEditLabelsContainer() {
      return editLabelsContainer;
    }

    public Vector<JTextField> getEditFieldsContainer() {

      return editFieldsContainer;
    }

    public JPanel createGridLayoutPanel() {

      JPanel entry = new JPanel();
      entry.setLayout(new GridLayout((labelsContainer.size() / 4), 4, 10, 5));
      for (int i = 0; i < labelsContainer.size(); i++) {
        labels[i] = new JLabel();
        labels[i].setText(labelsContainer.get(i));
        editLabelsContainer.add(labels[i]);
        fields[i] = new JTextField();
        if (fieldsContainer.get(i).equals("true")
            || fieldsContainer.get(i).equals("false")) {
          fields[i]
              .setToolTipText("Please pay attention to the value in this field only true or false");
        }
        fields[i].setText(fieldsContainer.get(i));
        editFieldsContainer.add(fields[i]);
        JPanel innerPnl = new JPanel(new BorderLayout());
        innerPnl.add(labels[i], BorderLayout.WEST);
        innerPnl.add(fields[i], BorderLayout.CENTER);
        entry.add(innerPnl);
      }
      return entry;
    }

    public Vector<String> saveProcess(Vector<String> fieldsContainer,
        String type, String objectEventType) {

      fieldValidateContainer = new Vector<Boolean>();
      fieldValidateTypeMap = new HashMap<String, Vector<Vector<Boolean>>>();
      for (int k = 0; k < fieldsContainer.size(); k++) {

        fieldValidateContainer.add(false);
      }

      nextField:

      for (int i = 0; i < fieldsContainer.size(); i++) {

        if ((fields[i].getText().equals("")) || (fields[i].getText() == null)) {

          JOptionPane.showMessageDialog(null,
              "Please fill in the field with the content,now it is empty!");
          fields[i].setText("!empty!");
          fields[i].setBackground(Color.YELLOW);
          fieldValidate = false;
          fieldValidateContainer.set(i, fieldValidate);

        }

        else if (initialStateUITab.getConstrainMap().keySet().contains(type)) {

          HashSet<String> tempPropertySet = initialStateUITab.getConstrainMap()
              .get(type);

          for (Iterator<String> propertys = tempPropertySet.iterator(); propertys
              .hasNext();) {

            String property = propertys.next();
            String propertyType = property + type;

            HashSet<String> tempLanSet = initialStateUITab.getLanType().get(
                type);
            for (Iterator<String> lans = tempLanSet.iterator(); lans.hasNext();) {

              String lan = lans.next();
              String labelKey = propertyType + lan;

              if (initialStateUITab.getLabelMap().get(labelKey).equals(
                  labels[i].getText())) {

                Vector<Integer> tempValueRange = initialStateUITab
                    .getConstrainNameMapRange().get(property);
                int minValue = tempValueRange.get(0);
                int maxValue = tempValueRange.get(1);

                int currentValue = Integer.valueOf(fields[i].getText());
                if ((currentValue > maxValue) || (currentValue < minValue)) {

                  JOptionPane.showMessageDialog(null,
                      "The content of the field " + labels[i].getText()
                          + " must be in the range" + "(" + minValue + ","
                          + maxValue + ")");

                  fields[i].setBackground(Color.YELLOW);
                  fieldValidate = false;
                  fieldValidateContainer.set(i, fieldValidate);

                } else {

                  fieldsContainer.set(i, fields[i].getText());
                  fields[i].setBackground(Color.WHITE);
                  fieldValidate = true;
                  fieldValidateContainer.set(i, fieldValidate);

                }
                continue nextField;
              }
            }
          }
          processRest(type, i);
        } else {

          processRest(type, i);

        }

      }

      if (objectEventType != null) {

        if (!fieldValidateTypeMap.keySet().contains(objectEventType)) {

          Vector<Vector<Boolean>> booleanObjectEventData = new Vector<Vector<Boolean>>();
          booleanObjectEventData.add(fieldValidateContainer);

          fieldValidateTypeMap.put(objectEventType, booleanObjectEventData);

        } else {

          Vector<Vector<Boolean>> tempObjectEventBooleanContainer = fieldValidateTypeMap
              .get(objectEventType);
          tempObjectEventBooleanContainer.add(fieldValidateContainer);
        }
      } else {

        if (!fieldValidateTypeMap.keySet().contains(type)) {

          Vector<Vector<Boolean>> booleanData = new Vector<Vector<Boolean>>();
          booleanData.add(fieldValidateContainer);
          fieldValidateTypeMap.put(type, booleanData);

        } else {

          Vector<Vector<Boolean>> tempBooleanContainer = fieldValidateTypeMap
              .get(type);
          tempBooleanContainer.add(fieldValidateContainer);

        }

      }

      return fieldsContainer;
    }

    public void processRest(String type, int i) {

      boolean enumProperty = false;
      String property = "";
      subTempStop: {
        for (Iterator<String> it = initialStateUITab.getEnumMap().keySet()
            .iterator(); it.hasNext();) {

          property = it.next();
          String propertyType = property + type;

          HashSet<String> tempLanSet = initialStateUITab.getLanType().get(type);
          for (Iterator<String> lans = tempLanSet.iterator(); lans.hasNext();) {

            String lan = lans.next();
            String labelKey = propertyType + lan;

            Collection<String> labels = initialStateUITab.getLabelMap()
                .values();

            if (labels.contains(initialStateUITab.getLabelMap().get(labelKey))) {

              if (initialStateUITab.getLabelMap().get(labelKey).equals(
                  this.labels[i].getText())) {

                enumProperty = true;
                break subTempStop;
              }
            }
          }
        }
      }

      if (!enumProperty) {

        fieldsContainer.set(i, fields[i].getText());
        fields[i].setBackground(Color.WHITE);
        fieldValidate = true;
        fieldValidateContainer.set(i, fieldValidate);

      } else {

        HashSet<String> tempEnumContent = initialStateUITab.getEnumMap().get(
            property);

        if (tempEnumContent.contains(fields[i].getText().trim())) {

          fieldsContainer.set(i, fields[i].getText());
          fields[i].setBackground(Color.WHITE);
          fieldValidate = true;
          fieldValidateContainer.set(i, fieldValidate);

        } else {

          JOptionPane.showMessageDialog(null, "The content of the field "
              + this.labels[i].getText() + " must be in the enum range");
          fields[i].setBackground(Color.YELLOW);
          fieldValidate = false;
          fieldValidateContainer.set(i, fieldValidate);

        }
      }

    }

    private Vector<String> labelsContainer;
    private Vector<String> fieldsContainer;
    private JLabel labels[];
    private JTextField fields[];
    private InitialStateUITab initialStateUITab;
    private boolean fieldValidate;
    private Vector<JLabel> editLabelsContainer = new Vector<JLabel>();
    private Vector<JTextField> editFieldsContainer = new Vector<JTextField>();

  }

}

class ChooseFilter extends FileFilter {

  public ChooseFilter(String extension, String description) {
    this.description = description;
    this.extension = extension;
  }

  public String getDescription() {

    return description;
  }

  public boolean accept(File file) {

    if (file == null) {
      return false;
    }

    if (file.isDirectory()) {
      return true;
    }

    return file.getName().toLowerCase().endsWith(extension);

  }

  private String description;
  private String extension;

}
