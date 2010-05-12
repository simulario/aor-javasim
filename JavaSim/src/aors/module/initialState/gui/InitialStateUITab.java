package aors.module.initialState.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
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
import javax.swing.JCheckBox;
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

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aors.controller.SimulationDescription;
import aors.module.GUIModule;
import aors.module.Module;
import aors.module.initialState.EditRandomVariableDialog;
import aors.module.initialState.EditValueExprDialog;
import aors.module.initialState.FieldsLanguageBoxHandler;
import aors.module.initialState.InitialStateUIController;
import aors.module.initialState.LanguageBoxHandler;

public class InitialStateUITab extends JScrollPane implements GUIModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InitialStateUITab(InitialStateUIController controller) {

		this.controller = controller;
		theKit = Toolkit.getDefaultToolkit();
		screenResolution = theKit.getScreenResolution();

	}

	public void initial(Document dom, SimulationDescription sd,
			InitialStateUITab initialStateUITab, File file) {

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

		/*
		 * in the field style map between the type and corresponding fields
		 * values
		 */
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

		valueExprTypePropertyMap = new HashMap<String, HashSet<String>>();

		valueExprPropertyContainerMap = new HashMap<String, Vector<ValueExprPropertyContainer>>();

		ranTypePropertyMap = new HashMap<String, HashSet<String>>();

		ranVarPropertyContainerMap = new HashMap<String, Vector<RanVarPropertyContainer>>();

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

		enumValueConstrain();// process enumeration property constraint rendered
		// by
		// JComboBox
		minMaxValueConstrain();// process minimun max value property constraint
		// rendered by JSlider
		selfPropertyConstrain();// process selfBeliefProperty rendered by
		// background
		// color

	}

	public void enumValueConstrain() {

		NodeList EnumerationPropertys = sd.getNodeList(ENUMPROPERTY);// get
		// NodeList
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
					+ "SimulationModel/" + PX + "DataTypes/" + PX
					+ "Enumeration[@name='" + type + "']/" + PX
					+ "EnumerationLiteral");

			HashSet<String> enumNameSet = new HashSet<String>();
			for (int i = 0; i < enumNodes.getLength(); i++) {

				// value of Enumeration property
				String enumContent = type
						+ "."
						+ ((Element) enumNodes.item(i)).getFirstChild()
								.getNodeValue();
				enumNameSet.add(enumContent);

			}

			enumMap.put(name, enumNameSet);

		}
	}

	//
	public void minMaxValueConstrain() {

		// get all the node, which contains the min and max value that belong to
		// the
		// Entity type
		NodeList constrainLists = sd.getNodeList("//" + PX
				+ "Attribute[@maxValue]", sd.getNode(CONSTRAINNODE));
		for (int i = 0; i < constrainLists.getLength(); i++) {

			String constrainType = "";
			Element parentNode = (Element) (constrainLists.item(i)
					.getParentNode());
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
			int Min = Integer.parseInt(constrainElement
					.getAttribute("minValue"));
			int Max = Integer.parseInt(constrainElement
					.getAttribute("maxValue"));

			// put the key(constrainName) and the value to the constranMap
			if (!constrainMap.keySet().contains(constrainType)) {

				HashSet<String> constrainSet = new HashSet<String>();
				constrainSet.add(constrainName);
				constrainMap.put(constrainType, constrainSet);
				for (Iterator<String> it = subConstrainTypeSet.iterator(); it
						.hasNext();) {
					String tempConstrainType = it.next();
					constrainMap.put(tempConstrainType, constrainSet);
				}
			} else {
				HashSet<String> tempSet = constrainMap.get(constrainType);
				tempSet.add(constrainName);
				constrainMap.put(constrainType, tempSet);

				for (Iterator<String> it = subConstrainTypeSet.iterator(); it
						.hasNext();) {
					String tempConstrainType = it.next();
					HashSet<String> subTempSet = constrainMap
							.get(tempConstrainType);
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
			NodeList selfPropertyNodes = sd.getNodeList(PX
					+ "SelfBeliefPropertyUI", nodes.item(i));

			// Then we will test of the number of selefBeliefProperty is great
			// than
			// zero or not.
			// If so, we will save the selefBeliefProperty and its value in the
			// selfPropertyMap
			if (selfPropertyNodes.getLength() > 0) {

				String type = ((Element) nodes.item(i))
						.getAttribute("agentType");
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

			Integer nrmOfInstance = Integer.valueOf(((Element) fieldNodes
					.item(i)).getAttribute("nmrOfInstances"));
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
				nodeNameSet.add(nodeName);
			}
		}

		Iterator<String> it = nodeNameSet.iterator();
		while (it.hasNext()) {
			String content = (String) it.next();
			NodeList isSubNodes = sd.getNodeList(INITIALSTATEUI + "/" + PX
					+ content);
			createInitialStateUITableHeader(isSubNodes);
		}

		createBeliefEntityUI();
	}

	public void processInputFieldLength(String type, String propertyType,
			String inputFieldLength) {

		if ((inputFieldLength != null) & (!inputFieldLength.isEmpty())) {

			Double length = Double.valueOf(inputFieldLength);
			fieldLengthTypeSet.add(type);
			propertyLengthMap.put(propertyType, length);

		}
	}

	/*
	 * create the typeUITableHead to save every Type. The type of GlobalVariable
	 * is globalVariable and the BeliefEntityUI is a special one that we will
	 * deal with separately
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

						String property = sd.getNodeContent("@property",
								propertyUIs.item(k));
						String propertyType = property + type;

						String inputFieldLength = sd.getNodeContent(
								"@inputFieldLength", propertyUIs.item(k));
						processInputFieldLength(type, propertyType,
								inputFieldLength);

						NodeList labelnodes = sd.getNodeList(LT, propertyUIs
								.item(k));
						NodeList hintnodes = sd.getNodeList(HT, propertyUIs
								.item(k));

						for (int j = 0; j < labelnodes.getLength(); j++) {
							String lan = sd.getNodeContent(XLAN, labelnodes
									.item(j));
							lanSet.add(lan);
							String labelKey = propertyType + lan;// use
							// property+type+lan
							// to
							// create the label key
							String lContent = sd.getNodeContent("text()",
									labelnodes.item(j));

							labelMap.put(labelKey, lContent);

							if (hintnodes.item(j) != null) {
								String hContent = sd.getNodeContent("text()",
										hintnodes.item(j));
								hintMap.put(labelKey, hContent);
							} else {
								hintMap.put(labelKey, null);// If the value of
								// label or hint
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

				String inputFieldLength = ((Element) child)
						.getAttribute("@inputFieldLength");
				processInputFieldLength(type, propertyType, inputFieldLength);

				NodeList labelnodes = sd.getNodeList(LT, child);
				NodeList hintnodes = sd.getNodeList(HT, child);

				for (int j = 0; j < labelnodes.getLength(); j++) {
					String lan = sd.getNodeContent(XLAN, labelnodes.item(j));
					String lContent = sd.getNodeContent("text()", labelnodes
							.item(j));
					String hContent = sd.getNodeContent("text()", hintnodes
							.item(j));
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

			String type = sd.getNodeContent("@beliefEntityType",
					beliefEntityUIs.item(i));
			typeUITableHead.add(type);
			NodeList beliefPropertyUIs = beliefEntityUIs.item(i)
					.getChildNodes();

			HashSet<String> lanSet = new HashSet<String>();
			Vector<String> userInterfacePropertyVector = new Vector<String>();

			for (int k = 0; k < beliefPropertyUIs.getLength(); k++) {
				if ((beliefPropertyUIs.item(k) instanceof Element)) {
					String property = sd.getNodeContent("@property",
							beliefPropertyUIs.item(k));
					String propertyType = property + type;

					String inputFieldLength = sd.getNodeContent(
							"@inputFieldLength", beliefPropertyUIs.item(k));
					processInputFieldLength(type, propertyType,
							inputFieldLength);

					NodeList labelnodes = sd.getNodeList(LT, beliefPropertyUIs
							.item(k));
					NodeList hintnodes = sd.getNodeList(HT, beliefPropertyUIs
							.item(k));

					for (int j = 0; j < labelnodes.getLength(); j++) {
						String lan = sd
								.getNodeContent(XLAN, labelnodes.item(j));
						lanSet.add(lan);
						String labelKey = propertyType + lan;
						String lContent = sd.getNodeContent("text()",
								labelnodes.item(j));
						labelMap.put(labelKey, lContent);
						if (hintnodes.item(j) != null) {
							String hContent = sd.getNodeContent("text()",
									hintnodes.item(j));

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
	 * first time to be added into initialPanel or not if it is first time we
	 * will create and not we will add the entry to the content
	 */
	public void getInformationFromInitialState() {

		Node InitialState = sd.getNode(IS);
		NodeList children = InitialState.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);
			if (child instanceof Element) {
				String nodeName = children.item(i).getNodeName();

				if (nodeName.equals("PhysicalObject")
						|| nodeName.equals("PhysicalAgent")
						|| nodeName.equals("Object")
						|| nodeName.equals("PhysicalAgents")
						|| nodeName.equals("Agents")
						|| nodeName.equals("Agent")
						|| nodeName.equals("PhysicalObjects")) {

					String type = sd.getNodeContent("@type", child);

					if (typeUITableHead.contains(type)) {
						if (!typeSet.contains(type)) {
							typeSet.add(type);

							initialStatePanel.add(createObjectContent(type,
									children.item(i), nodeName));
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

							initialStatePanel.add(createEventContent(type,
									child));
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

							initialStatePanel
									.add(createGlobalVariableContent(globalVariables));
							initialStatePanel.add(Box.createVerticalStrut(5));
						}
					}
				}

			}

		}

	}

	public void processTableFieldLength(String type, JTable table) {

		if (fieldLengthTypeSet.contains(type)) {

			for (Iterator<String> it = propertyLengthMap.keySet().iterator(); it
					.hasNext();) {

				String tempKey = it.next();
				String tempLabel = tempKey + "en";

				for (int i = 0; i < table.getColumnCount(); i++) {

					String tempTableHeader = (String) table.getModel()
							.getColumnName(i);

					if (labelMap.get(tempLabel).equals(tempTableHeader)
							& (tempKey.contains(type))) {

						Double length = propertyLengthMap.get(tempKey);

						table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						table.setPreferredScrollableViewportSize(table
								.getPreferredSize());
						table.getColumnModel().getColumn(i).setPreferredWidth(
								(int) (screenResolution * length / 2.45));

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

					Vector<String> tempContent = userInterfaceMap.get(type);
					for (Iterator<String> it = tempContent.iterator(); it
							.hasNext();) {

						String tempString = (String) it.next();
						String label = labelMap.get(tempString);
						if (label.equals(colHeadValue)) {

							String tempPropertyName = tempString.substring(0,
									(tempString.length() - type.length() - 2));

							Vector<String> tempVector = selfPropertyMap
									.get(type);

							if (tempVector.contains(tempPropertyName)) {

								TableColumn selfColumn = table.getColumnModel()
										.getColumn(j);
								selfColumn
										.setCellRenderer(new ColorColumnRenderer(
												new Color(168, 64, 89),
												Color.WHITE));

							}
						}
					}
				}
			}

			processTableFieldLength(type, table);
			JScrollPane tablePane = createScrollPane(table);
			objectCenterPanel.add(tablePane);
			objectCenterPanel.add(createButtonPanel(model, table, type, node,
					null));
			objectSubPanel.add(objectCenterPanel, BorderLayout.CENTER);
			tableType.put(type, table);

		} else {

			createFieldModel(type, node, null);
			objectContentSubNodes(type, node, model, objectCenterPanel,
					objectBottomPanel);
			Vector<String> tempLabels = labelTypeMap.get(type);
			Vector<String> tempFields = fieldTypeMap.get(type);
			FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
					initialStateUITab, type, null);
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
				Vector<Object> columnValue = new Vector<Object>();
				String subNodeName = children.item(i).getNodeName();
				if (subNodeName.equals("Slot")
						| (subNodeName.equals("SelfBeliefSlot"))
						| subNodeName.equals("BeliefSlot")) {

					processCreateSlot(model, children.item(i), tempContent,
							type, null);
				}

				if (subNodeName.contains("Event")) {
					JPanel objectEventPanel = new JPanel();
					String eventType = ((Element) children.item(i))
							.getAttribute("type");
					String objectEventType = type + eventType;
					if (typeUITableHead.contains(eventType)) {
						if (!typeSet.contains(objectEventType)) {
							typeSet.add(objectEventType);
							objectEventPanel = createObjectEventPanel(children
									.item(i), eventType, type);

							if (!fieldStyleSet.contains(eventType)) {

								centerPanel.add(objectEventPanel);
								centerPanel.add(Box.createVerticalStrut(5));
							} else {

								bottomPanel.add(objectEventPanel);
								bottomPanel.add(Box.createVerticalStrut(5));

							}
						} else {
							addObjectEventContent(eventType, objectEventType,
									children.item(i));

						}
					}
				}

				if (subNodeName.contains("BeliefEntity")) {
					JPanel agentEntityPanel = new JPanel();
					String entityType = ((Element) children.item(i))
							.getAttribute("type");
					String agentEntityType = type + entityType;
					if (typeUITableHead.contains(entityType)) {
						if (!typeSet.contains(agentEntityType)) {
							typeSet.add(agentEntityType);
							agentEntityPanel = createObjectEventPanel(children
									.item(i), entityType, type);

							if (!fieldStyleSet.contains(entityType)) {

								centerPanel.add(agentEntityPanel);
								centerPanel.add(Box.createVerticalStrut(5));
							} else {

								bottomPanel.add(agentEntityPanel);
								bottomPanel.add(Box.createVerticalStrut(5));

							}

						} else {
							addObjectEventContent(entityType, agentEntityType,
									children.item(i));

						}
					}
				}

				if (subNodeName.contains("Range")) {
					String property = subNodeName;
					String value = children.item(i).getFirstChild()
							.getNodeValue();
					Iterator<String> it = tempContent.iterator();
					while (it.hasNext()) {
						String tempString = (String) it.next();
						if (tempString.endsWith("en")) {
							if ((tempString.substring(0, (tempString.length()
									- type.length() - 2)))
									.equalsIgnoreCase(property)) {

								if (!fieldStyleSet.contains(type)) {
									if (constrainNameMapRange.keySet()
											.contains(property)) {

										Integer intValue = Integer
												.valueOf(value);
										columnValue.addElement(intValue);

									} else {

										columnValue.addElement(value);
									}
									String label = labelMap.get(tempString);
									model.addColumn(label, columnValue);
								} else {

									String fieldLabel = labelMap
											.get(tempString);
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
		Vector<Object> rowDatumTemp = new Vector<Object>();
		DefaultTableModel tempModel = null;

		if (!fieldStyleSet.contains(type)) {

			JTable tempTable = tableType.get(type);

			for (int j = 0; j < tempTable.getColumnCount(); j++) {
				String colHeadValue = (String) tempTable.getColumnModel()
						.getColumn(j).getHeaderValue();
				header.addElement(colHeadValue);
			}

			tempModel = (DefaultTableModel) tempTable.getModel();
			NamedNodeMap attrs = node.getAttributes();
			rowDatumTemp = createRowDatum(attrs, type, header);

		} else {

			createFieldModel(type, node, null);
		}

		if (node.hasChildNodes()) {

			NodeList children = node.getChildNodes();
			Vector<String> tempContent = userInterfaceMap.get(type);
			for (int i = 0; i < children.getLength(); i++) {
				String subNodeName = children.item(i).getNodeName();
				if (subNodeName.equals("Slot")
						| (subNodeName.equals("SelfBeliefSlot"))
						| subNodeName.equals("BeliefSlot")) {

					processAddSlot(header, children.item(i), tempContent, type,
							rowDatumTemp, null);
				}

				if (subNodeName.contains("Event")) {
					String eventType = ((Element) children.item(i))
							.getAttribute("type");
					String objectEventType = type + eventType;

					addObjectEventContent(eventType, objectEventType, children
							.item(i));
				}

				if (subNodeName.contains("BeliefEntity")) {
					String entityType = ((Element) children.item(i))
							.getAttribute("type");
					String agentEntityType = type + entityType;

					addObjectEventContent(entityType, agentEntityType, children
							.item(i));
				}

				if (subNodeName.contains("Range")) {
					String property = subNodeName;
					String value = children.item(i).getFirstChild()
							.getNodeValue();
					Iterator<String> it = tempContent.iterator();
					while (it.hasNext()) {
						String tempString = (String) it.next();
						if (tempString.endsWith("en")) {
							if ((tempString.substring(0, (tempString.length()
									- type.length() - 2)))
									.equalsIgnoreCase(property)) {

								if (!fieldStyleSet.contains(type)) {

									if (constrainNameMapRange.keySet()
											.contains(property)) {

										Integer intValue = Integer
												.valueOf(value);
										rowDatumTemp.addElement(intValue);

									} else {
										rowDatumTemp.addElement(value);
									}

								} else {

									String fieldLabel = labelMap
											.get(tempString);
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
					initialStateUITab, type, null);
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

			createFieldModel(type, node, null);

		}

		if (node.hasChildNodes()) {

			Vector<String> tempContent = userInterfaceMap.get(type);

			NodeList subNodes = node.getChildNodes();
			for (int i = 0; i < subNodes.getLength(); i++) {
				if (subNodes.item(i).getNodeName().equalsIgnoreCase("Slot")) {
					// Vector<String> columnValue = new Vector<String>();
					processCreateSlot(model, subNodes.item(i), tempContent,
							type, null);

				}

			}

		}

		if (!fieldStyleSet.contains(type)) {

			JTable table = new JTable(model);
			processTableFieldLength(type, table);
			JScrollPane tablePane = createScrollPane(table);
			eventCenterPanel.add(tablePane);
			eventCenterPanel.add(createButtonPanel(model, table, type, node,
					null));
			eventSubPanel.add(eventCenterPanel, BorderLayout.CENTER);
			tableType.put(type, table);
		} else {

			Vector<String> tempLabels = labelTypeMap.get(type);
			Vector<String> tempFields = fieldTypeMap.get(type);
			FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
					initialStateUITab, type, null);
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
					String subNodeName = languages.item(j).getNodeName();

					if (subNodeName.equals("ValueExpr")) {

						value = "ValueExpr";
						processValueExprTypePropertyMap(type, name);
						NodeList valueExprNodes = sd.getNodeList(PX
								+ "ValueExpr", nodes.item(i));
						processSlotValueExpr(type, valueExprNodes, name, null);
						break;
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
					if ((tempString.substring(0, (tempString.length()
							- type.length() - 2))).equals(name)) {

						String label = labelMap.get(tempString);
						tempLabels.add(label);
					}
				}
			}

		}

		labelTypeMap.put(type, tempLabels);
		fieldTypeMap.put(type, tempFields);
		FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
				initialStateUITab, type, null);
		Vector<FieldsEdit> fieldsContainer = new Vector<FieldsEdit>();
		fieldsContainer.add(fieldsEdit);
		JPanel entry = fieldsEdit.createGridLayoutPanel();
		globalVariablePanel.add(entry);
		contentPanelTypeMap.put(type, globalVariablePanel);
		fieldsTypeMap.put(type, fieldsContainer);

		return globalVariablePanel;

	}

	public void processValueExprTypePropertyMap(String type, String property) {

		if (!valueExprTypePropertyMap.containsKey(type)) {

			HashSet<String> ValueExprPropertySet = new HashSet<String>();
			ValueExprPropertySet.add(property);
			valueExprTypePropertyMap.put(type, ValueExprPropertySet);

		} else {

			HashSet<String> tempSet = valueExprTypePropertyMap.get(type);
			tempSet.add(property);
			valueExprTypePropertyMap.put(type, tempSet);

		}
	}

	public void processRanTypePropertyMap(String type, String property) {

		if (!ranTypePropertyMap.containsKey(type)) {

			HashSet<String> ranPropertySet = new HashSet<String>();
			ranPropertySet.add(property);
			ranTypePropertyMap.put(type, ranPropertySet);

		} else {

			HashSet<String> tempSet = ranTypePropertyMap.get(type);
			tempSet.add(property);
			ranTypePropertyMap.put(type, tempSet);

		}

	}

	public void processSlotValueExpr(String type, NodeList nodes,
			String property, String objectType) {

		HashMap<String, HashSet<String>> valueExprLanMap = new HashMap<String, HashSet<String>>();
		HashMap<String, String> valueExprValueMap = new HashMap<String, String>();

		String valueExprLanKey;

		if (objectType != null) {

			valueExprLanKey = property + objectType + type;
		} else {

			valueExprLanKey = property + type;
		}

		HashSet<String> tempLanSet = new HashSet<String>();

		for (int i = 0; i < nodes.getLength(); i++) {

			Node node = nodes.item(i);
			String tempLan = sd.getNodeContent("@language", node);
			String tempValue = sd.getNodeContent("text()", node);

			tempLanSet.add(tempLan);
			valueExprValueMap.put(valueExprLanKey + tempLan, tempValue);
		}

		valueExprLanMap.put(valueExprLanKey, tempLanSet);
		ValueExprPropertyContainer vContainer = new ValueExprPropertyContainer(
				valueExprLanKey, valueExprLanMap, valueExprValueMap);

		if (!valueExprPropertyContainerMap.containsKey(valueExprLanKey)) {

			Vector<ValueExprPropertyContainer> newContainers = new Vector<ValueExprPropertyContainer>();
			newContainers.add(vContainer);
			valueExprPropertyContainerMap.put(valueExprLanKey, newContainers);

		} else {

			Vector<ValueExprPropertyContainer> tempContainers = valueExprPropertyContainerMap
					.get(valueExprLanKey);
			tempContainers.add(vContainer);
			valueExprPropertyContainerMap.put(valueExprLanKey, tempContainers);

		}

	}

	public JPanel createObjectEventPanel(Node node, String type,
			String objectType) {

		String objectEventType = objectType + type;
		String title = type + "<<" + node.getNodeName() + ">>";

		DefaultTableModel model = null;

		if (!fieldStyleSet.contains(type)) {
			model = createTableModel(type, node);

		} else {
			createFieldModel(type, node, objectType);

		}

		if (node.hasChildNodes()) {

			Vector<String> tempContent = userInterfaceMap.get(type);
			NodeList subNodes = node.getChildNodes();
			for (int i = 0; i < subNodes.getLength(); i++) {
				if (subNodes.item(i).getNodeName().equals("Slot")
						| subNodes.item(i).getNodeName().equals("BeliefSlot")) {
					// Vector<String> columnValue = new Vector<String>();
					processCreateSlot(model, subNodes.item(i), tempContent,
							type, objectType);
				}
			}
		}

		JPanel objectEventPanel = createContentPanel(title);

		if (!fieldStyleSet.contains(type)) {

			JTable table = new JTable(model);
			processTableFieldLength(type, table);
			JScrollPane tablePane = createScrollPane(table);
			objectEventPanel.add(tablePane);
			objectEventPanel.add(createButtonPanel(model, table, type, node,
					objectType));
			tableType.put(objectEventType, table);

		} else {

			Vector<String> tempLabels = labelTypeMap.get(objectType + type);
			Vector<String> tempFields = fieldTypeMap.get(objectType + type);
			FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
					initialStateUITab, type, objectType);
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
		Vector<Object> rowDatumTemp = new Vector<Object>();
		DefaultTableModel tempModel = null;
		String objectType = objectEventType.substring(0, (objectEventType
				.length() - type.length()));

		if (!fieldStyleSet.contains(type)) {

			JTable tempTable = tableType.get(objectEventType);

			for (int j = 0; j < tempTable.getColumnCount(); j++) {
				String colHeadValue = (String) tempTable.getColumnModel()
						.getColumn(j).getHeaderValue();
				header.addElement(colHeadValue);
			}

			tempModel = (DefaultTableModel) tempTable.getModel();
			NamedNodeMap attrs = node.getAttributes();

			rowDatumTemp = createRowDatum(attrs, type, header);

		} else {

			createFieldModel(type, node, objectType);

		}

		if (node.hasChildNodes()) {
			NodeList children = node.getChildNodes();
			Vector<String> tempContent = userInterfaceMap.get(type);
			for (int i = 0; i < children.getLength(); i++) {

				String subNodeName = children.item(i).getNodeName();
				if (subNodeName.equals("Slot")
						| subNodeName.equals("BeliefSlot")) {

					processAddSlot(header, children.item(i), tempContent, type,
							rowDatumTemp, objectType);

				}
			}
		}

		if (!fieldStyleSet.contains(type)) {

			tempModel.addRow(rowDatumTemp);

		} else {

			Vector<String> tempLabels = labelTypeMap.get(objectEventType);
			Vector<String> tempFields = fieldTypeMap.get(objectEventType);
			FieldsEdit fieldsEdit = new FieldsEdit(tempLabels, tempFields,
					initialStateUITab, type, objectType);
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
		buttonContainer.add(copy);

		JButton del = new JButton("Del");
		buttonContainer.add(del);

		JButton createNew = new JButton("New");
		buttonContainer.add(createNew);

		JButton edit = new JButton("Edit");
		buttonContainer.add(edit);

		copy.addActionListener(new InitialStateUIController(table, model, type,
				buttonContainer, objectType));

		del.addActionListener(new InitialStateUIController(table, model, type,
				buttonContainer, objectType));

		createNew.addActionListener(new InitialStateUIController(table, model,
				type, buttonContainer, objectType));

		edit.addActionListener(new InitialStateUIController(table, model, type,
				buttonContainer, objectType));

		JComboBox lanBox = new JComboBox(tempVector);
		lanBox.setSelectedItem("en");
		lanBox.addActionListener(new LanguageBoxHandler(type, table, labelMap,
				hintMap, userInterfaceMap, buttonContainer));

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

			}
		}

		Iterator<String> it = tempContent.iterator();
		while (it.hasNext()) {
			String tempString = (String) it.next();
			if (tempString.endsWith("en")) {

				String label = labelMap.get(tempString);
				rowHeaderTemp.addElement(label);
			}
		}

		int idPosition = rowHeaderTemp.indexOf("id");

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

	public Vector<Vector<Object>> createRowData(NamedNodeMap rattrs,
			String type, Vector<String> rowHeader) {

		Vector<Vector<Object>> rowDataTemp = new Vector<Vector<Object>>();
		rowDataTemp.add(createRowDatum(rattrs, type, rowHeader));
		return rowDataTemp;

	}

	public Vector<Object> createRowDatum(NamedNodeMap rattrs, String type,
			Vector<String> rowHeader) {

		Vector<Object> rowDatumTemp = new Vector<Object>();
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

						if ((!(attribute.getName()).equals("type") & (tempString
								.substring(0, (tempString.length()
										- type.length() - 2))).equals(attribute
								.getName())))

							if (!fieldStyleSet.contains(type)) {

								if (attribute.getValue().equals("true")
										|| attribute.getValue().equals("false")) {

									Boolean flag = Boolean.valueOf(attribute
											.getValue());
									rowDatumTemp.set(rowHeader.indexOf(labelMap
											.get(tempString)), flag);

								} else if (constrainNameMapRange.keySet()
										.contains(attribute.getName())) {

									Integer intValue = Integer
											.valueOf(attribute.getValue());
									rowDatumTemp.set(rowHeader.indexOf(labelMap
											.get(tempString)), intValue);

								}

								else {

									rowDatumTemp.set(rowHeader.indexOf(labelMap
											.get(tempString)), attribute
											.getValue());
								}
							} else {

								rowDatumTemp
										.set(rowHeader.indexOf(labelMap
												.get(tempString)), attribute
												.getValue());
							}

					}
				}
			}
		}
		return rowDatumTemp;
	}

	public Vector<String> createFieldRowDatum(NamedNodeMap rattrs, String type,
			Vector<String> rowHeader) {

		Vector<String> rowDatumTemp = new Vector<String>();
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

						if ((!(attribute.getName()).equals("type") & (tempString
								.substring(0, (tempString.length()
										- type.length() - 2))).equals(attribute
								.getName()))) {

							rowDatumTemp.set(rowHeader.indexOf(labelMap
									.get(tempString)), attribute.getValue());
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
		saveButtonPanel.setBorder(BorderFactory
				.createTitledBorder("Save To File"));
		saveButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		save = new JButton("Save");
		save.setToolTipText("Save the table content to the original File");
		saveAs = new JButton("Save As...");
		saveAs.setToolTipText("Save the table content to another File");

		buttonContainer.add(save);
		buttonContainer.add(saveAs);

		HashSet<String> tempLanSetAll = new HashSet<String>();
		for (Iterator<String> it = lanType.keySet().iterator(); it.hasNext();) {

			String tempType = it.next();
			HashSet<String> tempLanSet = lanType.get(tempType);
			tempLanSetAll.addAll(tempLanSet);

		}

		Vector<String> tempVector = new Vector<String>();
		tempVector.addAll(tempLanSetAll);

		JComboBox lanBox = new JComboBox(tempVector);
		lanBox.setSelectedItem("en");
		lanBox.addActionListener(new FieldsLanguageBoxHandler(fieldsTypeMap,
				labelMap, hintMap, userInterfaceMap, objectObjectEventMap,
				buttonContainer));

		saveButtonPanel.add(lanBox);
		saveButtonPanel.add(save);
		saveButtonPanel.add(saveAs);
		ActionListener listenSave = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				processDom();
				if (fieldValidateTypeMap != null) {
					for (Iterator<String> it = fieldValidateTypeMap.keySet()
							.iterator(); it.hasNext();) {

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
					for (Iterator<String> it = fieldValidateTypeMap.keySet()
							.iterator(); it.hasNext();) {

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
			if (fileChoose.showSaveDialog((Frame) SwingUtilities
					.getRoot(button)) != JFileChooser.APPROVE_OPTION) {
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

	@SuppressWarnings("unchecked")
	public void processDom() {

		HashSet<String> savedTypeContainer = new HashSet<String>();
		Node InitialState = sd.getNode(IS);
		NodeList children = InitialState.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);
			if (child instanceof Element) {
				String nodeName = child.getNodeName();

				if (nodeName.equals("PhysicalObject")
						|| nodeName.equals("PhysicalAgent")
						|| nodeName.equals("Object")
						|| nodeName.equals("PhysicalAgents")
						|| nodeName.equals("Agents")
						|| nodeName.equals("Agent")
						|| nodeName.equals("PhysicalObjects")) {

					String type = sd.getNodeContent("@type", child);
					if (typeUITableHead.contains(type)) {
						if (!savedTypeContainer.contains(type)) {
							savedTypeContainer.add(type);

							if (!fieldStyleSet.contains(type)) {

								JTable table = tableType.get(type);
								DefaultTableModel model = (DefaultTableModel) table
										.getModel();
								Vector<Vector<Object>> dataRows = model
										.getDataVector();

								NodeList objectsAfterDel = prepareProcess(type,
										InitialState, dataRows);

								for (int l = 0; l < objectsAfterDel.getLength(); l++) {

									Vector<String> attrNames = createAttributeContainer(objectsAfterDel
											.item(l));

									processPropertyEdit(model, dataRows, type,
											attrNames, l, objectsAfterDel
													.item(l), null);

									NodeList objectEventNodes = sd.getNodeList(
											"*[contains(name(),'Event')]",
											objectsAfterDel.item(l));
									if (objectEventNodes.getLength() > 0) {
										processSubNodes(objectEventNodes,
												objectsAfterDel.item(l), l,
												type);
									}

									NodeList beliefEntityNodes = sd
											.getNodeList(
													"*[contains(name(),'BeliefEntity')]",
													objectsAfterDel.item(l));
									if (beliefEntityNodes.getLength() > 0) {
										processSubNodes(beliefEntityNodes,
												objectsAfterDel.item(l), l,
												type);
									}

								}
							} else {

								NodeList nodes = (NodeList) sd
										.getNodeList("*[@type='" + type + "']",
												InitialState);
								Vector<FieldsEdit> fieldsContainer = fieldsTypeMap
										.get(type);

								for (int f = 0; f < fieldsContainer.size(); f++) {

									Vector<String> attrNames = createAttributeContainer(nodes
											.item(f));
									Vector<String> tempLabels = labelTypeMap
											.get(type);
									FieldsEdit tempFieldEdit = fieldsContainer
											.get(f);
									Vector<String> savedFields = tempFieldEdit
											.saveProcess(tempFieldEdit
													.getFieldsContainer(),
													type, null);

									processFieldProperty(tempLabels,
											savedFields, nodes.item(f), type,
											attrNames, null, f);

									NodeList objectEventNodes = sd.getNodeList(
											"*[contains(name(),'Event')]",
											nodes.item(f));
									if (objectEventNodes.getLength() > 0) {
										processSubNodes(objectEventNodes, nodes
												.item(f), f, type);
									}

									NodeList beliefEntityNodes = sd
											.getNodeList(
													"*[contains(name(),'BeliefEntity')]",
													nodes.item(f));
									if (beliefEntityNodes.getLength() > 0) {
										processSubNodes(beliefEntityNodes,
												nodes.item(f), f, type);
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
								DefaultTableModel model = (DefaultTableModel) table
										.getModel();
								Vector<Vector<Object>> dataRows = model
										.getDataVector();

								NodeList eventsAfterDel = prepareProcess(type,
										InitialState, dataRows);
								for (int l = 0; l < eventsAfterDel.getLength(); l++) {

									Vector<String> attrNames = createAttributeContainer(eventsAfterDel
											.item(l));
									processPropertyEdit(model, dataRows, type,
											attrNames, l, eventsAfterDel
													.item(l), null);

								}
							} else {

								NodeList nodes = sd.getNodeList("*[@type='"
										+ type + "']", InitialState);
								Vector<FieldsEdit> fieldsContainer = fieldsTypeMap
										.get(type);

								for (int f = 0; f < fieldsContainer.size(); f++) {

									Vector<String> attrNames = createAttributeContainer(nodes
											.item(f));
									Vector<String> tempLabels = labelTypeMap
											.get(type);
									FieldsEdit tempFieldEdit = fieldsContainer
											.get(f);
									Vector<String> savedFields = tempFieldEdit
											.saveProcess(tempFieldEdit
													.getFieldsContainer(),
													type, null);
									processFieldProperty(tempLabels,
											savedFields, nodes.item(f), type,
											attrNames, null, f);

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
							NodeList globalVariables = sd.getNodeList(PX
									+ "GlobalVariable", InitialState);

							Vector<String> tempLabels = labelTypeMap
									.get("globalVariable");
							FieldsEdit tempFieldsEdit = tempFieldsContainer
									.get(0);
							Vector<String> savedFields = tempFieldsEdit
									.saveProcess(tempFieldsEdit
											.getFieldsContainer(),
											"globalVariable", null);
							for (int j = 0; j < tempLabels.size(); j++) {

								String tableHeadElement = tempLabels.get(j);
								String value = savedFields.get(j);
								;

								Vector<String> tempContent = userInterfaceMap
										.get("globalVariable");
								for (Iterator<String> it = tempContent
										.iterator(); it.hasNext();) {

									String tempString = it.next();
									String label = labelMap.get(tempString);

									if (label.equals(tableHeadElement)) {
										String tempPropertyName = tempString
												.substring(0, (tempString
														.length() - 16));

										for (int g = 0; g < globalVariables
												.getLength(); g++) {

											String name = ((Element) globalVariables
													.item(g))
													.getAttribute("name");

											if (name.equals(tempPropertyName)) {
												if (!(globalVariables.item(g))
														.hasChildNodes()) {
													setAttribute(
															(Element) globalVariables
																	.item(g),
															"value", value);
												} else {

													NodeList subNodes = globalVariables
															.item(g)
															.getChildNodes();

													for (int s = 0; s < subNodes
															.getLength(); s++) {

														Node gChild = subNodes
																.item(s);

														if (gChild instanceof Element) {
															// RandomVariable
															String gNodeName = child
																	.getNodeName();

															if (gNodeName
																	.equals("ValueExpr")) {
																

																Vector<ValueExprPropertyContainer> tempContainers = valueExprPropertyContainerMap
																		.get(name
																				+ "globalVariable");

																ValueExprPropertyContainer tempContainer = tempContainers
																		.get(0);

																

																processValueExpr(
																		globalVariables
																				.item(g),
																		tempContainer,
																		name,
																		"globalVariable",
																		null);
																break;

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
		}
	}

	public void processPropertyEdit(DefaultTableModel model,
			Vector<Vector<Object>> dataRows, String type,
			Vector<String> attrNames, int l, Node editNode, String objectType) {

		String tempPropertyValue = null;
		for (int j = 0; j < model.getColumnCount(); j++) {

			String tableHeadElement = model.getColumnName(j);

			if (tableHeadElement.equals("id")
					| tableHeadElement.equals("idRef")
					| tableHeadElement.equals("rangeStartID")
					| tableHeadElement.equals("rangeEndID")) {
				tempPropertyValue = (String) dataRows.get(l).get(j);
				setAttribute((Element) editNode, tableHeadElement,
						tempPropertyValue);
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

				processNormalProperty(type, tableHeadElement, attrNames,
						editNode, tempPropertyValue, objectType, l);

			}
		}
	}

	public void processFieldProperty(Vector<String> tempLabels,
			Vector<String> savedFields, Node node, String type,
			Vector<String> attrNames, String objectType, int r) {

		for (int l = 0; l < tempLabels.size(); l++) {

			String tempLabel = tempLabels.get(l);
			String tempPropertyValue = savedFields.get(l);

			if (tempLabel.equals("id") | tempLabel.equals("idRef")
					| tempLabel.equals("rangeStartID")
					| tempLabel.equals("rangeEndID")

			) {

				setAttribute((Element) node, tempLabel, tempPropertyValue);
			} else {

				processNormalProperty(type, tempLabel, attrNames, node,
						tempPropertyValue, objectType, r);

			}
		}
	}

	public String typeTransfer(String objectType, String type) {

		String returnType = null;

		if (objectType != null) {

			returnType = objectType + type;

		} else {

			returnType = type;

		}

		return returnType;

	}

	public void processNormalProperty(String type, String tableHeadElement,
			Vector<String> attrNames, Node editNode, String tempPropertyValue,
			String objectType, int l) {

		String pLanType = typeTransfer(objectType, type);

		Vector<String> tempContent = userInterfaceMap.get(type);
		Iterator<String> it = tempContent.iterator();
		while (it.hasNext()) {

			String tempString = (String) it.next();
			String label = labelMap.get(tempString);
			if (label.equals(tableHeadElement)) {
				String tempPropertyName = tempString.substring(0, (tempString
						.length()
						- type.length() - 2));

				if (attrNames.contains(tempPropertyName)) {

					setAttribute((Element) editNode, tempPropertyName,
							tempPropertyValue);

				} else if (editNode.hasChildNodes()) {

					NodeList subChildren = editNode.getChildNodes();
					for (int h = 0; h < subChildren.getLength(); h++) {
						String subNodeName = subChildren.item(h).getNodeName();

						if (subNodeName.equals("Slot")
								|| subNodeName.equals("SelfBeliefSlot")
								|| subNodeName.equals("BeliefSlot")) {
							String property = sd.getNodeContent("@property",
									subChildren.item(h));

							if (tempPropertyName.equals(property)) {

								if (subChildren.item(h).hasChildNodes()) {

									NodeList subNodes = subChildren.item(h)
											.getChildNodes();

									for (int s = 0; s < subNodes.getLength(); s++) {

										Node child = subNodes.item(s);

										if (child instanceof Element) {
											// RandomVariable
											String nodeName = child
													.getNodeName();

											if (nodeName
													.equals("RandomVariable")) {

												Vector<RanVarPropertyContainer> tempContainers = ranVarPropertyContainerMap
														.get(property
																+ pLanType);

												RanVarPropertyContainer tempContainer = tempContainers
														.get(l);

												processRandomVariable(child,
														tempContainer,
														property, type,
														objectType);

											} else if (nodeName
													.equals("ValueExpr")) {
												
												Vector<ValueExprPropertyContainer> tempContainers = valueExprPropertyContainerMap
														.get(property
																+ pLanType);

												ValueExprPropertyContainer tempContainer = tempContainers
														.get(l);

												processValueExpr(subChildren
														.item(h),
														tempContainer,
														property, type,
														objectType);
												break;

											}
										}
									}

								} else {
									setAttribute((Element) subChildren.item(h),
											"value", tempPropertyValue);
								}
							}
						} else if (subNodeName.contains("Range")) {
							if (subNodeName.equalsIgnoreCase(tempPropertyName)) {
								subChildren.item(h).setTextContent(
										tempPropertyValue);
							}
						}
					}
				}
			}
		}
	}

	public void processValueExpr(Node slot,
			ValueExprPropertyContainer tempContainer, String property,
			String type, String objectType) {

		String nodeName = slot.getNodeName();
		String tempVProperty = null;

		if (nodeName.equals("Slot")) {

			tempVProperty = "property";

		} else if (nodeName.equals("GlobalVariable")) {

			tempVProperty = "name";

		}

		Node parentSlot = slot.getParentNode();
		parentSlot.removeChild(slot);

		Element newSlot = dom.createElement(nodeName);
		Attr attrSlot = dom.createAttribute(tempVProperty);
		attrSlot.setValue(property);
		newSlot.setAttributeNode(attrSlot);

		String tempPropertyType = tempContainer.getPropertyType();
		HashSet<String> tempLanSet = tempContainer.getValueExprPropertyMap()
				.get(tempPropertyType);
		HashMap<String, String> tempValueMap = tempContainer
				.getValueExprValueMap();
		// Document newDom = builder.newDocument();

		for (Iterator<String> it = tempLanSet.iterator(); it.hasNext();) {

			Element newTypeElement = dom.createElement("ValueExpr");

			String tempProperty = it.next();
			Attr attr = dom.createAttribute("language");//
			attr.setValue(tempProperty);
			newTypeElement.setAttributeNode(attr);
			String tempValue = tempValueMap.get(tempPropertyType + tempProperty);
			newTypeElement.appendChild(dom.createTextNode(tempValue));//
			newSlot.appendChild(newTypeElement);

		}

		parentSlot.appendChild(newSlot);

	}

	public void processRandomVariable(Node node,
			RanVarPropertyContainer tempContainer, String property,
			String type, String objectType) {

		String pLanType = typeTransfer(objectType, type);
		;

		NodeList ranSubNodes = node.getChildNodes();

		for (int s = 0; s < ranSubNodes.getLength(); s++) {

			Node child = ranSubNodes.item(s);
			if (child instanceof Element) {

				// if(ranTypeSet.contains(child.getNodeName())){

				node.removeChild(child);
				break;

				// }

			}
		}

		String ranType = tempContainer.getSelectedType();
		Element newTypeElement = dom.createElement(ranType);// Uniform
		HashMap<String, HashSet<String>> tempRanPropertyMap = tempContainer
				.getRanVarPropertyMap();
		HashMap<String, HashSet<String>> tempRanLanMap = tempContainer
				.getRanVarLanMap();
		HashMap<String, String> tempRanValueMap = tempContainer
				.getRanVarValueMap();

		HashSet<String> tempRanPropertySet = tempRanPropertyMap.get(property
				+ pLanType + ranType);

		if (tempRanLanMap == null) {

			for (Iterator<String> it = tempRanPropertySet.iterator(); it
					.hasNext();) {

				String tempProperty = it.next();
				Attr attr = dom.createAttribute(tempProperty);
				String tempValue = tempRanValueMap.get(property + pLanType
						+ ranType + tempProperty);
				attr.setValue(tempValue);
				newTypeElement.setAttributeNode(attr);

			}

			node.appendChild(newTypeElement);

		} else {

			HashSet<String> tempRanLanSet = tempRanLanMap.get(property
					+ pLanType + ranType);

			for (Iterator<String> it = tempRanPropertySet.iterator(); it
					.hasNext();) {

				String tempProperty = it.next();
				

				for (Iterator<String> lans = tempRanLanSet.iterator(); lans
						.hasNext();) {
					Element newSubElement = dom.createElement(tempProperty);
					String tempLan = lans.next();
					
					Attr attr = dom.createAttribute("language");
					attr.setValue(tempLan);
					newSubElement.setAttributeNode(attr);
					String tempValue = tempRanValueMap.get(property + pLanType
							+ ranType + tempProperty + tempLan);
					newSubElement.setTextContent(tempValue);
					newTypeElement.appendChild(newSubElement);

				}
			}

			node.appendChild(newTypeElement);

		}

	}

	public NodeList prepareProcess(String type, Node node,
			Vector<Vector<Object>> dataRows) {

		NodeList nodes = sd.getNodeList("*[@type='" + type + "']", node);
		Element parent = (Element) nodes.item(0).getParentNode();
		for (int r = 1; r < nodes.getLength(); r++) {
			parent.removeChild(nodes.item(r));
		}

		for (int d = 1; d < dataRows.size(); d++) {
			duplicateNode(nodes.item(0));
		}

		NodeList nodesAfterDel = sd
				.getNodeList("*[@type='" + type + "']", node);

		return nodesAfterDel;

	}

	@SuppressWarnings("unchecked")
	public void processSubNodes(NodeList nodes, Node node, int l, String type) {

		HashSet<String> objectEventTypeSet = null;

		objectEventTypeSet = new HashSet<String>();
		for (int e = 0; e < nodes.getLength(); e++) {

			Element event = (Element) nodes.item(e);
			String eventType = event.getAttribute("type");
			objectEventTypeSet.add(eventType);

		}

		for (Iterator<String> it = objectEventTypeSet.iterator(); it.hasNext();) {

			String tempType = it.next();
			if (typeUITableHead.contains(tempType)) {
				// System.out.println("the next type is: ===> " + tempType);
				NodeList eventNodes = (NodeList) sd.getNodeList("*[@type='"
						+ tempType + "']", node);
				String objectEventType = type + tempType;

				Vector<Vector<Object>> objectEventDataRows = null;
				DefaultTableModel objectEventModel = null;

				Vector<FieldsEdit> objectEventFieldsContainer = null;

				if (!fieldStyleSet.contains(tempType)) {
					JTable objectEventTable = tableType.get(objectEventType);
					objectEventModel = (DefaultTableModel) objectEventTable
							.getModel();
					objectEventDataRows = objectEventModel.getDataVector();
				} else {

					objectEventFieldsContainer = fieldsTypeMap
							.get(objectEventType);
				}
				int oen = 0;
				for (int r = (l * eventNodes.getLength()); r < (l
						* eventNodes.getLength() + eventNodes.getLength()); r++) {

					Node eventNode = eventNodes.item(oen);
					Vector<String> objectEventAttrNames = createAttributeContainer(eventNode);

					if (!fieldStyleSet.contains(tempType)) {
						processPropertyEdit(objectEventModel,
								objectEventDataRows, tempType,
								objectEventAttrNames, r, eventNode, type);
					} else {

						for (int e = (l * eventNodes.getLength()); e < (l
								* eventNodes.getLength() + eventNodes
								.getLength()); e++) {
							Vector<String> tempLabels = labelTypeMap
									.get(objectEventType);
							FieldsEdit tempFieldEdit = objectEventFieldsContainer
									.get(e);
							Vector<String> savedFields = tempFieldEdit
									.saveProcess(tempFieldEdit
											.getFieldsContainer(), tempType,
											objectEventType);
							processFieldProperty(tempLabels, savedFields,
									eventNode, tempType, objectEventAttrNames,
									type, oen);
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

	private static void callWriteXmlFile(Document doc, File fOut,
			String encoding) {

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

			FileWriter fileWriter = new FileWriter(fOut);
			fileWriter.write(stream.toString());

		} catch (IOException e) {
			
		}

		/*
		 * DOMImplementation domImplementation = doc.getImplementation();
		 * DOMImplementationLS domImplementationLS = (DOMImplementationLS)
		 * domImplementation .getFeature("LS", "3.0"); LSSerializer lsSerializer
		 * = domImplementationLS.createLSSerializer();
		 * lsSerializer.getDomConfig().setParameter("format-pretty-print",
		 * Boolean.TRUE);// LSOutput lsOutput =
		 * domImplementationLS.createLSOutput(); lsOutput.setEncoding("UTF-8");
		 * StringWriter stringWriter = new StringWriter();
		 * lsOutput.setCharacterStream(stringWriter); lsSerializer.write(doc,
		 * lsOutput);
		 * 
		 * FileOutputStream out;
		 * 
		 * try { out = new FileOutputStream(fOut, false); DataOutputStream
		 * dataOut = new DataOutputStream(out); byte[] data =
		 * stringWriter.toString().getBytes(); dataOut.write(data, 0,
		 * data.length); out.close(); dataOut.close(); } catch
		 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); }
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

	@SuppressWarnings("serial")
	public DefaultTableModel createTableModel(String type, Node node) {

		NamedNodeMap attrs = node.getAttributes();
		Vector<String> rowHeader = createRowHeader(attrs, type);
		Vector<Vector<Object>> rowData = createRowData(attrs, type, rowHeader);

		for (int i = 0; i < rowData.size(); i++) {

			for (int j = 0; j < rowData.get(i).size(); j++) {

				if (rowData.get(i).get(j) == null) {

					rowData.get(i).set(j, "");
				}
			}
		}

		DefaultTableModel model = new DefaultTableModel(rowData, rowHeader) {

			public Class<?> getColumnClass(int c) {

				return getValueAt(0, c).getClass();
			}
		};
		return model;
	}

	public void createFieldModel(String type, Node node, String objectType) {

		String modelType = null;
		modelType = typeTransfer(objectType, type);
		NamedNodeMap attrs = node.getAttributes();
		Vector<String> labels = createRowHeader(attrs, type);
		Vector<String> fields = createFieldRowDatum(attrs, type, labels);

		labelTypeMap.put(modelType, labels);
		fieldTypeMap.put(modelType, fields);
	}

	@SuppressWarnings("unchecked")
	private void processCreateSlot(DefaultTableModel model, Node node,
			Vector<String> tempContent, String type, String objectType) {

		String property = sd.getNodeContent("@property", node);

		String value = null;
		String valueExprType = null;
		String ranRavType = null;

		HashSet<String> tempRanPropertySet = new HashSet<String>();
		HashSet<String> tempValueExprPropertySet = new HashSet<String>();

		Iterator<String> it = tempContent.iterator();
		while (it.hasNext()) {

			String tempString = (String) it.next();
			if (tempString.endsWith("en")) {

				if ((tempString.substring(0, (tempString.length()
						- type.length() - 2))).equals(property)) {

					if (!node.hasChildNodes()) {

						value = sd.getNodeContent("@value", node);

					} else {

						NodeList slotSubNodes = node.getChildNodes();

						for (int i = 0; i < slotSubNodes.getLength(); i++) {

							String subNodeName = slotSubNodes.item(i)
									.getNodeName();

							if (subNodeName.equals("ValueExpr")) {

								value = "ValueExpr";
								valueExprType = typeTransfer(objectType, type);

								processValueExprTypePropertyMap(valueExprType,
										property);

								NodeList ValueExprNodes = sd.getNodeList(PX
										+ "ValueExpr", node);
								processSlotValueExpr(type, ValueExprNodes,
										property, objectType);
								break;

							} else if (subNodeName.equals("RandomVariable")) {

								value = "RandomVariable";
								ranRavType = typeTransfer(objectType, type);

								processRanTypePropertyMap(ranRavType, property);

								Node ranNode = sd.getNode(
										PX + "RandomVariable", node);
								processSlotRandomVariable(type, ranNode,
										property, objectType);
								break;

							}
						}
					}

					if (valueExprType != null) {

						tempValueExprPropertySet
								.addAll(valueExprTypePropertyMap
										.get(valueExprType));

					}

					if (ranRavType != null) {

						tempRanPropertySet.addAll(ranTypePropertyMap
								.get(ranRavType));

					}

					if (!fieldStyleSet.contains(type)) {

						Vector<Vector<Object>> tempData = (Vector<Vector<Object>>) model
								.getDataVector();
						Vector<Object> tempRow = tempData.elementAt(0);

						Vector<String> header = new Vector<String>();

						for (int j = 0; j < model.getColumnCount(); j++) {
							String colHeadValue = (String) model
									.getColumnName(j);
							header.addElement(colHeadValue);
						}

						if (value.equals("true") || value.equals("false")) {
							Boolean flag = Boolean.valueOf(value);
							tempRow.set(header
									.indexOf(labelMap.get(tempString)), flag);

						}

						else if (constrainMap.containsKey(type)
								& constrainNameMapRange.containsKey(property)
								& (!ranTypePropertyMap.containsKey(ranRavType) | !tempRanPropertySet
										.contains(property))
								& (!valueExprTypePropertyMap
										.containsKey(valueExprType) | !tempValueExprPropertySet
										.contains(property)))

						{

							Integer intValue = Integer.valueOf(value);
							tempRow.set(header
									.indexOf(labelMap.get(tempString)),
									intValue);
						}

						else {
							tempRow.set(header
									.indexOf(labelMap.get(tempString)), value);
						}

					} else {

						String tempSlotFieldType = typeTransfer(objectType,
								type);
						fieldTypeMap.get(tempSlotFieldType).set(
								labelTypeMap.get(tempSlotFieldType).indexOf(
										labelMap.get(tempString)), value);

					}
				}
			}
		}

	}

	public void processSlotRandomVariable(String type, Node node,
			String property, String objectType) {

		HashMap<String, HashSet<String>> ranVarPropertyMap = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> ranVarLanMap = new HashMap<String, HashSet<String>>();
		HashMap<String, String> ranVarValueMap = new HashMap<String, String>();

		String ranType = null;

		NodeList ranSubNodes = node.getChildNodes();

		for (int s = 0; s < ranSubNodes.getLength(); s++) {

			Node ranSubNode = ranSubNodes.item(s);
			if (ranSubNode instanceof Element) {

				ranType = ranSubNode.getNodeName();
				// ranTypeSet.add(ranType);

				String tempPartKey;

				if (objectType != null) {

					tempPartKey = property + objectType + type;
				} else {

					tempPartKey = property + type;
				}

				if (!ranSubNode.hasChildNodes()) {

					NamedNodeMap attrs = ranSubNode.getAttributes();

					for (int a = 0; a < attrs.getLength(); a++) {

						Attr attribute = (Attr) attrs.item(a);
						String tempKey = attribute.getName();
						String tempValue = attribute.getValue();

						if (!ranVarPropertyMap.containsKey(tempPartKey
								+ ranType)) {

							HashSet<String> ranVarPropertySet = new HashSet<String>();
							ranVarPropertySet.add(tempKey);// tempKey =
							// attribute.getName();
							ranVarPropertyMap.put((tempPartKey + ranType),
									ranVarPropertySet);

						} else {

							HashSet<String> tempSet = ranVarPropertyMap
									.get(tempPartKey + ranType);
							tempSet.add(tempKey);
							ranVarPropertyMap.put(tempPartKey + ranType,
									tempSet);

						}

						ranVarValueMap.put((tempPartKey + ranType + tempKey),
								tempValue);
					}

					RanVarPropertyContainer rContaienr = new RanVarPropertyContainer(
							ranType, ranVarPropertyMap, ranVarValueMap);

					
					addRanVarInstance(tempPartKey, rContaienr);

				} else {

					HashSet<String> ranVarLanSet = new HashSet<String>();
					HashSet<String> ranVarPropertySet = new HashSet<String>();

					NodeList ranExprNodes = ranSubNode.getChildNodes();
					// String tempKey = type+property+ranType;
					String tempValueKey = null;
					for (int r = 0; r < ranExprNodes.getLength(); r++) {

						Node ranExprNode = ranExprNodes.item(r);

						if (ranExprNode instanceof Element) {

							String exprName = ranExprNode.getNodeName();
							String tempLan = sd.getNodeContent("@language",
									ranExprNode);
							ranVarPropertySet.add(exprName);
							ranVarLanSet.add(tempLan);
							tempValueKey = tempPartKey + ranType + exprName
									+ tempLan;
							String tempValue = sd.getNodeContent("text()",
									ranExprNode).trim();// !!
							ranVarValueMap.put(tempValueKey, tempValue);

						}
					}

					ranVarPropertyMap.put(tempPartKey + ranType,
							ranVarPropertySet);
					ranVarLanMap.put(tempPartKey + ranType, ranVarLanSet);

					RanVarPropertyContainer rContaienr = new RanVarPropertyContainer(
							ranType, ranVarPropertyMap, ranVarLanMap,
							ranVarValueMap);

					addRanVarInstance(tempPartKey, rContaienr);

				}
			}

		}

	}

	public void addRanVarInstance(String key,
			RanVarPropertyContainer newInstance) {

		if (!ranVarPropertyContainerMap.containsKey(key)) {

			Vector<RanVarPropertyContainer> newContainers = new Vector<RanVarPropertyContainer>();
			newContainers.add(newInstance);
			ranVarPropertyContainerMap.put(key, newContainers);

		} else {

			Vector<RanVarPropertyContainer> tempContainers = ranVarPropertyContainerMap
					.get(key);
			tempContainers.add(newInstance);
			ranVarPropertyContainerMap.put(key, tempContainers);

		}

	}

	private void processAddSlot(Vector<String> header, Node node,
			Vector<String> tempContent, String type,
			Vector<Object> rowDatumTemp, String objectType) {

		String property = sd.getNodeContent("@property", node);

		String value = "";

		Iterator<String> it = tempContent.iterator();
		while (it.hasNext()) {
			String tempString = (String) it.next();
			if (tempString.endsWith("en")) {
				if ((tempString.substring(0, (tempString.length()
						- type.length() - 2))).equals(property)) {

					if (!node.hasChildNodes()) {

						value = sd.getNodeContent("@value", node);

					} else {

						NodeList slotSubNodes = node.getChildNodes();
						for (int i = 0; i < slotSubNodes.getLength(); i++) {

							String subNodeName = slotSubNodes.item(i)
									.getNodeName();
							if (subNodeName.equals("ValueExpr")) {

								value = "ValueExpr";
								NodeList valueExprNodes = sd.getNodeList(PX
										+ "ValueExpr", node);
								processSlotValueExpr(type, valueExprNodes,
										property, objectType);
								break;

							} else if (subNodeName.equals("RandomVariable")) {

								value = "RandomVariable";
								Node ranNode = sd.getNode(
										PX + "RandomVariable", node);
								processSlotRandomVariable(type, ranNode,
										property, objectType);
								break;

							}

						}

					}

					if (!fieldStyleSet.contains(type)) {
						if (value.equals("true") || value.equals("false")) {

							Boolean flag = Boolean.valueOf(value);
							rowDatumTemp.set(header.indexOf(labelMap
									.get(tempString)), flag);
						} else if (constrainNameMapRange.keySet().contains(
								property)) {

							Integer intValue = Integer.valueOf(value);
							rowDatumTemp.set(header.indexOf(labelMap
									.get(tempString)), intValue);
						} else {

							rowDatumTemp.set(header.indexOf(labelMap
									.get(tempString)), value);
						}
					} else {

						String tempSlotFieldType = typeTransfer(objectType,
								type);
						fieldTypeMap.get(tempSlotFieldType).set(
								labelTypeMap.get(tempSlotFieldType).indexOf(
										labelMap.get(tempString)), value);

					}
				}
			}
		}

	}

	public void processTableRenderer() {

		prepareProcessJSliderRender();
		prepareProcessJComboBoxRender();
		processRanVarTableRender();
		processValueExprTableRender();

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
					String eventType = objectEventType.substring(objectType
							.length());

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

		for (Iterator<HashSet<String>> it = objectObjectEventValueSet
				.iterator(); it.hasNext();) {
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
					if (objectObjectEventMap.get(tempObjectType).equals(
							tableTypeKey)) {

						String userInterfaceType = tableTypeKey
								.substring(tempObjectType.length());
						JTable table = tableType.get(tableTypeKey);
						processJComboBoxRender(table, userInterfaceType);

					}
				}
			} else {

				JTable table = tableType.get(tableTypeKey);
				processJComboBoxRender(table, tableTypeKey);

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
						TableColumn rendererComboBoxColumn = table
								.getColumnModel().getColumn(i);
						rendererComboBoxColumn
								.setCellRenderer(new ComboBoxRenderer(
										valuesComboBox));
						rendererComboBoxColumn
								.setCellEditor(new ComboBoxEditor(
										valuesComboBox));
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
						TableColumn rendererSliderColumn = table
								.getColumnModel().getColumn(i);
						rendererSliderColumn
								.setCellEditor(new JSliderTableEditor(minVlaue,
										maxValue));

					}
				}
			}
		}
	}

	public void processValueExprTableRender() {

		for (Iterator<String> it = tableType.keySet().iterator(); it.hasNext();) {

			String tempType = it.next();
			if (valueExprTypePropertyMap.containsKey(tempType)) {

				String selectedObjectType = null;
				String selectedEventType = null;
				String selectedLanType = null;

				JTable vTable = tableType.get(tempType);
				HashSet<String> tempPropertySet = valueExprTypePropertyMap
						.get(tempType);
				Collection<HashSet<String>> tempTypeCollection = objectObjectEventMap
						.values();

				for (Iterator<HashSet<String>> tempTypeSets = tempTypeCollection
						.iterator(); tempTypeSets.hasNext();) {

					HashSet<String> tempTypeSet = tempTypeSets.next();

					if (tempTypeSet.contains(tempType)) {

						for (Iterator<String> keys = objectObjectEventMap
								.keySet().iterator(); keys.hasNext();) {

							String objectType = keys.next();

							if (objectObjectEventMap.get(objectType).equals(
									tempTypeSet)) {

								selectedObjectType = objectType;
								selectedEventType = tempType
										.substring(objectType.length());

							}
						}
					}

				}

				ExprValueButtonEditor buttonEditor = null;

				if (selectedObjectType == null) {

					buttonEditor = new ExprValueButtonEditor(new JCheckBox(),
							vTable, tempType, valueExprTypePropertyMap,
							valueExprPropertyContainerMap, lanType, labelMap,
							null);

					selectedLanType = tempType;

				} else {

					buttonEditor = new ExprValueButtonEditor(new JCheckBox(),
							vTable, selectedEventType,
							valueExprTypePropertyMap,
							valueExprPropertyContainerMap, lanType, labelMap,
							selectedObjectType);

					selectedLanType = selectedEventType;

				}

				HashSet<String> tempLanSet = lanType.get(selectedLanType);
				String tempLabel = null;

				for (int j = 0; j < vTable.getColumnCount(); j++) {

					String colHeadValue = (String) vTable.getColumnModel()
							.getColumn(j).getHeaderValue();

					for (Iterator<String> tempProperties = tempPropertySet
							.iterator(); tempProperties.hasNext();) {

						String tempProperty = tempProperties.next();

						for (Iterator<String> lans = tempLanSet.iterator(); lans
								.hasNext();) {

							String tempLan = lans.next();

							if (selectedObjectType != null) {

								tempLabel = tempProperty + selectedEventType
										+ tempLan;

							} else {

								tempLabel = tempProperty + tempType + tempLan;
							}

							if (labelMap.get(tempLabel).equals(colHeadValue)) {

								vTable.getColumn(colHeadValue).setCellRenderer(
										new ButtonRenderer());
								vTable.getColumn(colHeadValue).setCellEditor(
										buttonEditor);

							}
						}
					}
				}

			}

		}

	}

	public void processRanVarTableRender() {

		for (Iterator<String> it = tableType.keySet().iterator(); it.hasNext();) {

			String tempType = it.next();
			if (ranTypePropertyMap.containsKey(tempType)) {

				String selectedObjectType = null;
				String selectedEventType = null;
				String selectedLanType = null;

				JTable rTable = tableType.get(tempType);
				HashSet<String> tempPropertySet = ranTypePropertyMap
						.get(tempType);
				Collection<HashSet<String>> tempTypeCollection = objectObjectEventMap
						.values();

				for (Iterator<HashSet<String>> tempTypeSets = tempTypeCollection
						.iterator(); tempTypeSets.hasNext();) {

					HashSet<String> tempTypeSet = tempTypeSets.next();

					if (tempTypeSet.contains(tempType)) {

						for (Iterator<String> keys = objectObjectEventMap
								.keySet().iterator(); keys.hasNext();) {

							String objectType = keys.next();

							if (objectObjectEventMap.get(objectType).equals(
									tempTypeSet)) {

								selectedObjectType = objectType;
								selectedEventType = tempType
										.substring(objectType.length());

							}
						}
					}
				}

				ButtonEditor buttonEditor = null;

				if (selectedObjectType == null) {

					buttonEditor = new ButtonEditor(new JCheckBox(), rTable,
							tempType, ranTypePropertyMap,
							ranVarPropertyContainerMap, lanType, labelMap, null);

					selectedLanType = tempType;

				} else {

					buttonEditor = new ButtonEditor(new JCheckBox(), rTable,
							selectedEventType, ranTypePropertyMap,
							ranVarPropertyContainerMap, lanType, labelMap,
							selectedObjectType);

					selectedLanType = selectedEventType;
				}

				HashSet<String> tempLanSet = lanType.get(selectedLanType);
				String tempLabel = null;

				for (int j = 0; j < rTable.getColumnCount(); j++) {

					String colHeadValue = (String) rTable.getColumnModel()
							.getColumn(j).getHeaderValue();

					for (Iterator<String> tempProperties = tempPropertySet
							.iterator(); tempProperties.hasNext();) {

						String tempProperty = tempProperties.next();

						for (Iterator<String> lans = tempLanSet.iterator(); lans
								.hasNext();) {

							String tempLan = lans.next();

							if (selectedObjectType != null) {

								tempLabel = tempProperty + selectedEventType
										+ tempLan;

							} else {

								tempLabel = tempProperty + tempType + tempLan;
							}

							if (labelMap.get(tempLabel).equals(colHeadValue)) {

								rTable.getColumn(colHeadValue).setCellRenderer(
										new ButtonRenderer());
								rTable.getColumn(colHeadValue).setCellEditor(
										buttonEditor);

							}
						}
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

	public HashMap<String, HashSet<String>> getRanTypePropertyMap() {

		return this.ranTypePropertyMap;
	}

	public HashMap<String, Vector<RanVarPropertyContainer>> getRanVarPropertyContainerMap() {

		return this.ranVarPropertyContainerMap;
	}

	public HashMap<String, HashSet<String>> getValueExprTypePropertyMap() {

		return this.valueExprTypePropertyMap;
	}

	public HashMap<String, Vector<ValueExprPropertyContainer>> getValueExprPropertyContainerMap() {

		return this.valueExprPropertyContainerMap;
	}

	private JPanel initialStatePanel;
	private Document dom;
	private InitialStateUIController controller;
	private SimulationDescription sd;
	private HashSet<String> typeUITableHead;
	private HashMap<String, String> labelMap;
	private HashMap<String, String> hintMap;
	private HashMap<String, HashSet<String>> lanType;
	private HashMap<String, Vector<String>> userInterfaceMap;
	private HashMap<String, JTable> tableType;
	private HashMap<String, Vector<String>> labelTypeMap;
	private HashMap<String, Vector<String>> fieldTypeMap;
	private HashMap<String, Vector<FieldsEdit>> fieldsTypeMap;
	private Vector<String> userInterfaceVariableVector;
	private HashSet<String> lanVariableSet;
	private HashSet<String> typeSet;
	private JFileChooser fileChoose;
	private JButton save, saveAs;
	private File file;
	private HashMap<String, HashSet<String>> objectObjectEventMap;
	private HashSet<String> fieldStyleSet;
	private HashMap<String, JPanel> contentPanelTypeMap;
	private HashMap<String, Vector<String>> selfPropertyMap;
	private HashMap<String, HashSet<String>> constrainMap;
	private HashMap<String, Vector<Integer>> constrainNameMapRange;
	private InitialStateUITab initialStateUITab;
	private Vector<Boolean> fieldValidateContainer;
	private HashMap<String, Vector<Vector<Boolean>>> fieldValidateTypeMap;
	private HashMap<String, HashSet<String>> enumMap;
	private HashMap<String, Double> propertyLengthMap;
	private HashSet<String> fieldLengthTypeSet;
	private int screenResolution;
	private Toolkit theKit;
	private HashMap<String, HashSet<String>> valueExprTypePropertyMap;
	private HashMap<String, Vector<ValueExprPropertyContainer>> valueExprPropertyContainerMap;
	private HashMap<String, HashSet<String>> ranTypePropertyMap;
	private HashMap<String, Vector<RanVarPropertyContainer>> ranVarPropertyContainerMap;

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
	private final String ENUMPROPERTY = "//" + PX + "EnumerationProperty";

	public class FieldsEdit {

		public FieldsEdit(Vector<String> labelsContainer,
				Vector<String> fieldsContainer,
				InitialStateUITab initialStateUITab, String type,
				String objectType) {

			this.labelsContainer = labelsContainer;
			this.fieldsContainer = fieldsContainer;
			this.initialStateUITab = initialStateUITab;
			labels = new JLabel[labelsContainer.size()];
			fields = new Object[fieldsContainer.size()];
			editLabelsContainer = new Vector<JLabel>();
			editFieldsContainer = new Vector<Object>();

			this.type = type;
			this.objectType = objectType;

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

		public Vector<Object> getEditFieldsContainer() {

			return editFieldsContainer;
		}

		public JPanel createGridLayoutPanel() {

			String modelType = typeTransfer(objectType, type);

			JPanel entry = new JPanel();
			entry.setLayout(new GridLayout(0, 4, 0, 0));

			if (!ranTypePropertyMap.containsKey(modelType)
					& !valueExprTypePropertyMap.containsKey(modelType)) {

				for (int i = 0; i < labelsContainer.size(); i++) {

					labels[i] = new JLabel();
					labels[i].setText(labelsContainer.get(i));
					editLabelsContainer.add(labels[i]);
					// fields[i] = new JTextField();

					fields[i] = processField(fields[i], i);

					if (fieldsContainer.get(i).equals("true")
							|| fieldsContainer.get(i).equals("false")) {
						((JTextField) fields[i])
								.setToolTipText("Please pay attention to the value in this field only true or false");
					}
					((JTextField) fields[i]).setText(fieldsContainer.get(i));
					editFieldsContainer.add(fields[i]);
					JPanel innerPnl = new JPanel(
							new FlowLayout(FlowLayout.LEFT));
					innerPnl.add(labels[i]);
					innerPnl.add((JTextField) fields[i]);
					entry.add(innerPnl);

				}
			} else {

				String tempLabel = null;

				out: for (int i = 0; i < labelsContainer.size(); i++) {

					labels[i] = new JLabel();
					labels[i].setText(labelsContainer.get(i));
					editLabelsContainer.add(labels[i]);
					JPanel innerPnl = new JPanel(
							new FlowLayout(FlowLayout.LEFT));
					innerPnl.add(labels[i]);

					if (ranTypePropertyMap.containsKey(modelType)) {

						HashSet<String> tempPropertySet = ranTypePropertyMap
								.get(modelType);

						for (Iterator<String> it = tempPropertySet.iterator(); it
								.hasNext();) {

							final String tempProperty = it.next();
							tempLabel = tempProperty + type + "en";

							if (labelMap.get(tempLabel).equals(
									labelsContainer.get(i))) {

								cRow = ranVarPropertyContainerMap.get(
										tempProperty + modelType).size() - 1;
								fields[i] = new JButton("RandomVariable");

								editFieldsContainer.add(fields[i]);
								innerPnl.add((JButton) fields[i]);
								entry.add(innerPnl);

								((JButton) fields[i])
										.setPreferredSize(new Dimension(
												(int) (screenResolution * 3.5 / 2.45),
												20));
								Frame frame = (Frame) SwingUtilities
										.getRoot(initialStateUITab);

								final EditRandomVariableDialog sbd = new EditRandomVariableDialog(
										frame, "Edit RandomVariable Dialog",
										true, cRow, type, tempProperty,
										initialStateUITab, false, objectType);

								((JButton) fields[i])
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent ae) {

												sbd.setVisible(true);

											}

										});

								continue out;
							}
						}
					}

					if (valueExprTypePropertyMap.containsKey(modelType)) {

						HashSet<String> tempPropertySet = valueExprTypePropertyMap
								.get(modelType);

						for (Iterator<String> it = tempPropertySet.iterator(); it
								.hasNext();) {

							final String tempProperty = it.next();
							tempLabel = tempProperty + type + "en";

							// System.out.println("tempLabel: "+ tempLabel);

							if (labelMap.get(tempLabel).equals(
									labelsContainer.get(i))) {

								cRow = valueExprPropertyContainerMap.get(
										tempProperty + modelType).size() - 1;
								fields[i] = new JButton("ValueExpr");

								editFieldsContainer.add(fields[i]);
								innerPnl.add((JButton) fields[i]);
								entry.add(innerPnl);

								((JButton) fields[i])
										.setPreferredSize(new Dimension(
												(int) (screenResolution * 2.5 / 2.45),
												20));
								Frame frame = (Frame) SwingUtilities
										.getRoot(initialStateUITab);

								final EditValueExprDialog ved = new EditValueExprDialog(
										frame, "Edit ValueExpr Dialog", true,
										cRow, type, tempProperty,
										initialStateUITab, false, objectType);

								((JButton) fields[i])
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent ae) {

												// ranVarPropertyContainerMap =
												// initialStateUITab.getRanVarPropertyContainerMap();

												ved.setVisible(true);

											}

										});

								continue out;

							}
						}
					}

					fields[i] = processField(fields[i], i);

					if (fieldsContainer.get(i).equals("true")
							|| fieldsContainer.get(i).equals("false")) {
						((JTextField) fields[i])
								.setToolTipText("Please pay attention to the value in this field only true or false");
					}
					((JTextField) fields[i]).setText(fieldsContainer.get(i));
					editFieldsContainer.add(fields[i]);
					innerPnl.add((JTextField) fields[i]);
					entry.add(innerPnl);

				}

			}
			return entry;
		}

		public JTextField processField(Object field, int i) {

			out2: {

				if (fieldLengthTypeSet.contains(type)) {

					for (Iterator<String> it = propertyLengthMap.keySet()
							.iterator(); it.hasNext();) {

						String tempKey = it.next();
						String restLabel = tempKey + "en";

						if (labelMap.get(restLabel).equals(
								labelsContainer.get(i))
								& (tempKey.contains(type))) {

							field = new JTextField();
							Double length = propertyLengthMap.get(tempKey);
							((JTextField) field).setPreferredSize(new Dimension(
											(int) (screenResolution * length / 2.45),
											20));
							break out2;

						}
					}
					field = new JTextField(10);

				} else {

					field = new JTextField(10);

				}
			}

			return (JTextField) field;

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

				if ((fields[i].getClass().getName())
						.equals("javax.swing.JButton")) {

					this.fieldsContainer
							.set(i, ((JButton) fields[i]).getText());
					fieldValidate = true;
					fieldValidateContainer.set(i, fieldValidate);

				} else {

					if (((JTextField) fields[i]).getText().equals("")
							|| ((JTextField) fields[i]).getText() == null) {

						JOptionPane
								.showMessageDialog(null,
										"Please fill in the field with the content,now it is empty!");
						((JTextField) fields[i]).setText("!empty!");
						((JTextField) fields[i]).setBackground(Color.YELLOW);
						fieldValidate = false;
						fieldValidateContainer.set(i, fieldValidate);

					}

					else if (constrainMap.keySet().contains(type)) {

						HashSet<String> tempPropertySet = constrainMap
								.get(type);

						for (Iterator<String> propertys = tempPropertySet
								.iterator(); propertys.hasNext();) {

							String property = propertys.next();
							String propertyType = property + type;

							HashSet<String> tempLanSet = getLanType().get(type);
							for (Iterator<String> lans = tempLanSet.iterator(); lans
									.hasNext();) {

								String lan = lans.next();
								String labelKey = propertyType + lan;
								
								if (labelMap.get(labelKey).equals(
										labels[i].getText())) {

									Vector<Integer> tempValueRange = constrainNameMapRange
											.get(property);
									int minValue = tempValueRange.get(0);
									int maxValue = tempValueRange.get(1);

									int currentValue = Integer
											.valueOf(((JTextField) fields[i])
													.getText());
									
									if ((currentValue > maxValue)
											|| (currentValue < minValue)) {

										JOptionPane
												.showMessageDialog(
														null,
														"The content of the field "
																+ labels[i]
																		.getText()
																+ " must be in the range"
																+ "("
																+ minValue
																+ ","
																+ maxValue
																+ ")");

										((JTextField) fields[i])
												.setBackground(Color.YELLOW);
										fieldValidate = false;
										fieldValidateContainer.set(i,
												fieldValidate);

										
									} else {

										this.fieldsContainer.set(i,
												((JTextField) fields[i])
														.getText());
										((JTextField) fields[i])
												.setBackground(Color.WHITE);
										fieldValidate = true;
										fieldValidateContainer.set(i,
												fieldValidate);
										// System.out.println("The value of fieldValidate in Constrained Field :==> "
										// + fieldValidate);
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

			}

			if (objectEventType != null) {

				if (!fieldValidateTypeMap.keySet().contains(objectEventType)) {

					Vector<Vector<Boolean>> booleanObjectEventData = new Vector<Vector<Boolean>>();
					booleanObjectEventData.add(fieldValidateContainer);

					fieldValidateTypeMap.put(objectEventType,
							booleanObjectEventData);

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

			return this.fieldsContainer;

		}

		public void processRest(String type, int i) {

			// System.out.println("in the processRest:=> " + i +
			// " with type:=> " +type );

			boolean enumProperty = false;
			String property = "";
			subTempStop: {
				for (Iterator<String> it = enumMap.keySet().iterator(); it
						.hasNext();) {

					property = it.next();
					String propertyType = property + type;

					HashSet<String> tempLanSet = lanType.get(type);
					for (Iterator<String> lans = tempLanSet.iterator(); lans
							.hasNext();) {

						String lan = lans.next();
						String labelKey = propertyType + lan;

						Collection<String> labels = labelMap.values();

						if (labels.contains(labelMap.get(labelKey))) {

							if (labelMap.get(labelKey).equals(
									this.labels[i].getText())) {

								// System.out.println("The label of enumProperty is :=> "
								// + this.labels[i].getText());
								enumProperty = true;
								break subTempStop;
							}
						}
					}
				}
			}
			// System.out.println("The value of enumProperty:=> " +
			// enumProperty);

			if (!enumProperty) {

				fieldsContainer.set(i, ((JTextField) fields[i]).getText());
				((JTextField) fields[i]).setBackground(Color.WHITE);
				fieldValidate = true;
				fieldValidateContainer.set(i, fieldValidate);

			} else {

				HashSet<String> tempEnumContent = enumMap.get(property);

				if (tempEnumContent.contains(((JTextField) fields[i]).getText()
						.trim())) {

					fieldsContainer.set(i, ((JTextField) fields[i]).getText());
					((JTextField) fields[i]).setBackground(Color.WHITE);
					fieldValidate = true;
					fieldValidateContainer.set(i, fieldValidate);

				} else {

					JOptionPane.showMessageDialog(null,
							"The content of the field "
									+ this.labels[i].getText()
									+ " must be in the enum range");
					((JTextField) fields[i]).setBackground(Color.YELLOW);
					fieldValidate = false;
					fieldValidateContainer.set(i, fieldValidate);

				}
			}

		}

		private Vector<String> labelsContainer;
		private Vector<String> fieldsContainer;
		private JLabel labels[];
		private Object fields[];
		private InitialStateUITab initialStateUITab;
		private boolean fieldValidate;
		private Vector<JLabel> editLabelsContainer;
		private Vector<Object> editFieldsContainer;
		private String objectType;
		private String type;
		private int cRow;
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
