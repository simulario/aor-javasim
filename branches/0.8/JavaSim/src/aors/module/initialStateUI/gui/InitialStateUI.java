package aors.module.initialStateUI.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import aors.module.GUIModule;
import aors.module.Module;
import aors.module.initialStateUI.controller.CategoryType;
import aors.module.initialStateUI.controller.InitialStateUIController;
import aors.module.initialStateUI.controller.InitialStateUIHashMap;
import aors.module.initialStateUI.controller.InitialStateUIInstance;
import aors.module.initialStateUI.controller.InitialStateUIProperty;
import aors.module.initialStateUI.controller.InitialStateUIType;

public class InitialStateUI extends JScrollPane implements GUIModule,
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1741619527577023644L;

	public static final Integer PANEL_GUI_LIMIT = 3;
	private InitialStateUIController initialstateUIcontroller;

	private InitialStatePropertiesTable initialStatePropertiesTableview;
	private InitialStatePropertiesPanel initialStatePropertiesPanel;
	private CreateObjektPanel createObjektPanel;

	private HashMap<String, HashMap<String, String>> propertiesTableColumnTextHashMap;

	private ArrayList<InitialStateUIProperty> selectedTypePropertiesList;
	private ArrayList<String> initialStatePropertiesNamesList;
	private ArrayList<String> initialStatePropertiesHintsList;

	private ArrayList<Object> initialStatePropertiesData;

	private ArrayList<TypeList> typeLists;
	private ArrayList<String> globalVariableHints;
	private ListType selectedListType;
	private InitialStatePropertiesGUIType initialStatePropertiesGUIType;

	private JScrollPane initialStatePropertiesJScrollPane;

	/**
	 * The content panel
	 */

	private JPanel contentPanel;

	/**
	 * The top panel from the InitialStateUI
	 */
	private JPanel topPanel;

	/**
	 * The center panel from the InitialStateUI
	 */
	private JPanel centerPanel;

	private JPanel listsPanel;
	private JPanel initialStatePropertiesMainPanel;

	private InitialStateUIBottomPanel initialStateUIBottomPanel;

	private JScrollPane bottomPanelScrollPanel;

	public InitialStateUI(InitialStateUIController initialstateUIcontroller) {

		initializeGUI(initialstateUIcontroller);

	}

	public void initializeGUI(InitialStateUIController initialstateUIcontroller) {

		// set the core component
		this.setinitialStateUIController(initialstateUIcontroller);

		initialStatePropertiesJScrollPane = new JScrollPane();

		// create the content panel
		this.contentPanel = new JPanel();
		this.contentPanel.setLayout(new BorderLayout());

		// create the center panel

		this.centerPanel = new JPanel();

		this.centerPanel.setBorder(new EtchedBorder());
		this.centerPanel.setLayout(new BorderLayout());

		// create the lists panel

		initializeListsPanel();

		// create the propertiesTable Panel
		this.initialStatePropertiesMainPanel = new JPanel();

		this.initialStatePropertiesMainPanel.setLayout(new BorderLayout());

		// adding to propertiesTable Panel
		this.initialStatePropertiesMainPanel.add(BorderLayout.CENTER,
				initialStatePropertiesJScrollPane);

		// adding to center panel
		this.centerPanel.add(BorderLayout.NORTH,
				this.initialStatePropertiesMainPanel);

		// create the topPanel
		this.topPanel = new JPanel();

		this.topPanel.setBorder(new EtchedBorder());
		this.topPanel.setLayout(new BorderLayout());
		this.topPanel.add(this.listsPanel, BorderLayout.CENTER);

		// Create the bottom Panel

		this.initialStateUIBottomPanel = new InitialStateUIBottomPanel();
		bottomPanelScrollPanel = new JScrollPane(this.initialStateUIBottomPanel
				.getBottomPanel());

		// add panels to InitialStateUI
		this.setViewportView(this.contentPanel);
		this.contentPanel.add(BorderLayout.NORTH, topPanel);
		this.contentPanel.add(BorderLayout.CENTER, centerPanel);
		this.contentPanel.add(BorderLayout.SOUTH, this.bottomPanelScrollPanel);

	}

	public void initializeListsPanel() {
		this.listsPanel = new JPanel();

		this.listsPanel.setLayout(new GridLayout(1, 4));

	}

	@Override
	public Module getBaseComponent() {
		// TODO Auto-generated method stub
		return this.initialstateUIcontroller;
	}

	public void setinitialStateUIController(
			InitialStateUIController initialstateUIcontroller) {
		this.initialstateUIcontroller = initialstateUIcontroller;
	}

	public InitialStateUIController getinitialStateUIController() {
		return initialstateUIcontroller;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	public void populateObjectList() {

		InitialStateUIController initialStateUIController = this
				.getinitialStateUIController();

		typeLists.add(ListType.OBJECT_LIST.ordinal(), new TypeList(
				initialStateUIController.getObjectTypes(), this,
				ListType.OBJECT_LIST, InitialStateUIStatus.NOT_CREATED));
		TypeList objectTypeList = typeLists.get(ListType.OBJECT_LIST.ordinal());

		this.listsPanel.add(objectTypeList.getTypesListScrollPane());

	}

	public void populateEventList() {
		InitialStateUIController initialStateUIController = this
				.getinitialStateUIController();

		typeLists.add(ListType.EVENT_LIST.ordinal(), new TypeList(
				initialStateUIController.getEventTypes(), this,
				ListType.EVENT_LIST, InitialStateUIStatus.NOT_CREATED));
		TypeList eventTypeList = typeLists.get(ListType.EVENT_LIST.ordinal());
		this.listsPanel.add(eventTypeList.getTypesListScrollPane());

	}

	public void populateAgentList() {
		InitialStateUIController initialStateUIController = this
				.getinitialStateUIController();

		typeLists.add(ListType.AGENT_LIST.ordinal(), new TypeList(
				initialStateUIController.getAgentTypes(), this,
				ListType.AGENT_LIST, InitialStateUIStatus.NOT_CREATED));
		TypeList agentTypeList = typeLists.get(ListType.AGENT_LIST.ordinal());
		this.listsPanel.add(agentTypeList.getTypesListScrollPane());

	}

	/*
	 * For Global Variables
	 */
	public void populateGlobalVariablesList() {
		int index = ListType.GLOBAL_VARIABLE_LIST.ordinal();
		TypeList globalVariableList;
		InitialStateUIStatus initialStateUIStatus = InitialStateUIStatus.NOT_CREATED;
		if (typeLists.size() == 4) {

			globalVariableList = typeLists.get(ListType.GLOBAL_VARIABLE_LIST
					.ordinal());
			this.listsPanel.remove(globalVariableList.getTypesListScrollPane());

			typeLists.remove(index);
			initialStateUIStatus = InitialStateUIStatus.LANGUAGE_CHANGED;

		}

		typeLists.add(ListType.GLOBAL_VARIABLE_LIST.ordinal(), new TypeList(
				initialstateUIcontroller.getGlobalVariables(), this,
				ListType.GLOBAL_VARIABLE_LIST, initialStateUIStatus));

		globalVariableList = typeLists.get(ListType.GLOBAL_VARIABLE_LIST
				.ordinal());
		this.listsPanel.add(globalVariableList.getTypesListScrollPane(), 3);

	}

	public void initializeInitializeStateUIForSelectedType(String selectedType,
			ListType selectedListType) {

		this.setSelectedListType(selectedListType);
		deSelectRestLists();

		initializeBottomPanel(selectedType);

		getPropertiesData();

	}

	private void initializeBottomPanel(String selectedType) {

		switch (this.getSelectedListType()) {
		case AGENT_LIST:
		case EVENT_LIST:
		case OBJECT_LIST: {

			this.initialStateUIBottomPanel = new InitialStateUIBottomPanel(
					this, selectedType);

			break;
		}
		case GLOBAL_VARIABLE_LIST: {
			if (this.initialStateUIBottomPanel.getLanguageComboBox() != null)
				;
			else {
				this.initialStateUIBottomPanel = new InitialStateUIBottomPanel(
						this, CategoryType.Global.name());
			}
			break;

		}

		}

		bottomPanelScrollPanel.setViewportView(this.initialStateUIBottomPanel
				.getBottomPanel());

	}

	private String findSelectedType() {

		String selectedType = null;
		switch (this.selectedListType) {
		case OBJECT_LIST: {

			TypeList objectTypeList = this.typeLists.get(ListType.OBJECT_LIST
					.ordinal());
			selectedType = objectTypeList.getSelectedType();
			break;
		}
		case AGENT_LIST: {
			TypeList agentTypeList = typeLists.get(ListType.AGENT_LIST
					.ordinal());
			selectedType = agentTypeList.getSelectedType();
			break;
		}
		case EVENT_LIST: {
			TypeList eventTypeList = typeLists.get(ListType.EVENT_LIST
					.ordinal());
			selectedType = eventTypeList.getSelectedType();
			break;
		}
		case GLOBAL_VARIABLE_LIST: {
			TypeList globalVariableList = typeLists
					.get(ListType.GLOBAL_VARIABLE_LIST.ordinal());
			selectedType = globalVariableList.getSelectedType();

			break;
		}
		}

		return selectedType;

	}

	public void getPropertiesData() {
		String selectedType;

		selectedType = findSelectedType();

		// Eg.
		// objecttype,agenttype,environmenttype,Global
		// variable labels in case
		// of Global variables

		InitialStateUIController initialStateUIController = this
				.getinitialStateUIController();

		if (!selectedListType.equals(ListType.GLOBAL_VARIABLE_LIST)) {
			initialStateUIController
					.getPropertiesValuesForSelectedType(selectedType);

			initialStateUIController
					.getPropertiesListForSelectedType(selectedType);
			this.getinitialStateUIController().editPropertiesNamesHints(
					this.getInitialStateUIBottomPanel()
							.getSelectedLanguageType());
			selectGUIType();
		} else {
			this.initialStatePropertiesNamesList = new ArrayList<String>();
			initialStatePropertiesNamesList.add(selectedType);

			InitialStateUIProperty initialStateUIProperty = getPropertyFromGlobalVariableLabel(selectedType);
			this.selectedTypePropertiesList = new ArrayList<InitialStateUIProperty>();
			selectedTypePropertiesList.add(initialStateUIProperty);
			initialStatePropertiesData = new ArrayList<Object>();
			initialStatePropertiesData.add(initialStateUIProperty
					.getPropertyValue());

			this.getinitialStateUIController().editPropertiesNamesHints(
					this.getInitialStateUIBottomPanel()
							.getSelectedLanguageType());
			populateInitialStatePropertiesPanel(InitialStateUIType.NO_INSTANCES_EXIST);
			this.initialStatePropertiesGUIType = InitialStatePropertiesGUIType.PANEL;
		}

	}

	private InitialStateUIProperty getPropertyFromGlobalVariableLabel(
			String selectedType) {
		InitialStateUIHashMap initialStateUIHashMap = this
				.getinitialStateUIController().getInitialStateHashMap();
		InitialStateUIType initialStateUIType = initialStateUIHashMap
				.getTypeStructure(CategoryType.Global.name());
		HashMap<String, InitialStateUIProperty> propertiesInfoHashMap = initialStateUIType
				.getPropertiesInfoHashMap();
		InitialStateUIProperty initialStateUIProperty;
		for (String propertyName : propertiesInfoHashMap.keySet()) {
			initialStateUIProperty = propertiesInfoHashMap.get(propertyName);

			if (propertyName.equals(selectedType)) {
				return initialStateUIProperty;

			} else {
				HashMap<String, String> languagePropertyLabelTextHashMap = initialStateUIProperty
						.getLanguagePropertyLabelTextHashMap();
				for (String propertyLabel : languagePropertyLabelTextHashMap
						.values()) {
					if (propertyLabel.equals(selectedType)) {
						return initialStateUIProperty;
					}
				}
			}
		}

		return null;
	}

	public void selectGUIType() {

		String selectedType = findSelectedType(); // Eg. Menuboard ,Car
		InitialStateUIType initialStateUIType = this.initialstateUIcontroller
				.getInitialStateHashMap().getTypeStructure(selectedType);

		int noOfInstances;
		HashMap<Long, InitialStateUIInstance> instancesHashMap = initialStateUIType
				.getInstancesHashMap();
		if (instancesHashMap != null) {
			noOfInstances = instancesHashMap.values().size();
		} else {
			noOfInstances = InitialStateUIType.NO_INSTANCES_EXIST;
		}

		if (noOfInstances > InitialStateUI.PANEL_GUI_LIMIT) {

			populateInitialStatePropertiesTable();
			this.initialStatePropertiesGUIType = InitialStatePropertiesGUIType.TABLE;
		} else {
			populateInitialStatePropertiesPanel(noOfInstances);
			this.initialStatePropertiesGUIType = InitialStatePropertiesGUIType.PANEL;

		}
	}

	private void populateInitialStatePropertiesPanel(int noOfInstances) {
		String selectedLanguage = this.initialStateUIBottomPanel
				.getSelectedLanguageType();

		this.setInitialStatePropertiesPanel(new InitialStatePropertiesPanel(
				this, noOfInstances, selectedLanguage));
		initialStatePropertiesJScrollPane.setViewportView(this
				.getInitialStatePropertiesPanel());
		initialStatePropertiesJScrollPane.updateUI();

		this.centerPanel.updateUI();

		this.contentPanel.updateUI();

		this.updateUI();

	}

	public void populateInitialStatePropertiesTable() {
		String selectedLanguage = this.initialStateUIBottomPanel
				.getSelectedLanguageType();

		this
				.setInitialStatePropertiesTableView(new InitialStatePropertiesTable(
						this, selectedLanguage));

		initialStatePropertiesJScrollPane.setViewportView(this
				.getInitialStatePropertiesTableView()
				.getInitialStatePropertiesTable());

		initialStatePropertiesJScrollPane.updateUI();

		this.centerPanel.updateUI();

		this.contentPanel.updateUI();

		this.updateUI();

	}

	public void setInitialStatePropertiesTableView(
			InitialStatePropertiesTable propertiesTypeTableClass) {
		this.initialStatePropertiesTableview = propertiesTypeTableClass;
	}

	public InitialStatePropertiesTable getInitialStatePropertiesTableView() {
		return initialStatePropertiesTableview;
	}

	public void setListsPanel(JPanel listsPanel) {
		this.listsPanel = listsPanel;
	}

	public JPanel getListsPanel() {
		return listsPanel;
	}

	/**
	 * @param initialStatePropertiesNamesList
	 *            the propertiesTypeTableColumnList to set
	 */
	public void setInitialStatePropertiesNamesList(
			ArrayList<String> initialStatePropertiesNamesList) {
		this.initialStatePropertiesNamesList = initialStatePropertiesNamesList;
	}

	/**
	 * @return the propertiesTypeTableColumnList
	 */
	public ArrayList<String> getInitialStatePropertiesNamesList() {
		return initialStatePropertiesNamesList;
	}

	/**
	 * @param initialStatePropertiesData
	 *            the propertiesTypeTableDataList to set
	 */
	public void setInitialStatePropertiesData(
			ArrayList<Object> initialStatePropertiesData) {
		this.initialStatePropertiesData = initialStatePropertiesData;
	}

	/**
	 * @return the propertiesTypeTableDataList
	 */
	public ArrayList<Object> getInitialStatePropertiesData() {
		return initialStatePropertiesData;
	}

	/**
	 * @param propertiesTypeTableColumnHashMap
	 *            the propertiesTypeTableColumnHashMap to set
	 */
	public void setPropertiesTableColumnTextHashMap(
			HashMap<String, HashMap<String, String>> propertiesTypeTableColumnHashMap) {
		this.propertiesTableColumnTextHashMap = propertiesTypeTableColumnHashMap;
	}

	/**
	 * @return the propertiesTypeTableColumnHashMap
	 */
	public HashMap<String, HashMap<String, String>> getPropertiesTableColumnTextHashMap() {
		return propertiesTableColumnTextHashMap;
	}

	/**
	 * @param selectedTypePropertiesList
	 *            the selectedTypePropertiesList to set
	 */
	public void setSelectedTypePropertiesList(
			ArrayList<InitialStateUIProperty> selectedTypePropertiesList) {
		this.selectedTypePropertiesList = selectedTypePropertiesList;
	}

	/**
	 * @return the selectedTypePropertiesList
	 */
	public ArrayList<InitialStateUIProperty> getSelectedTypePropertiesList() {
		return selectedTypePropertiesList;
	}

	private void copyInstanceInPanelView() {
		InitialStatePropertiesPanel initialStatePropertiesPanel = this
				.getInitialStatePropertiesPanel();

		int selectedPanelIndex = initialStatePropertiesPanel
				.getSelectedPanelIndex();
		InstancePanel selectedPanel = initialStatePropertiesPanel
				.getInstancesPanelList()[selectedPanelIndex];

		int noOfProperties = this.getInitialStatePropertiesNamesList().size();

		int typeNameIndex = selectedPanelIndex * noOfProperties
				+ PropertyIndexConstants.TYPE_NAME_INPUT_FIELD_INDEX;

		Long selectedInstanceID = selectedPanel.getInstancePanelKey();

		String selectedType = (String) this.getInitialStatePropertiesData()
				.get(typeNameIndex);
		validateInstanceCopy(selectedType, selectedInstanceID);

	}

	public void copyInstanceInTableView() {

		InitialStatePropertiesTable initialStatePropertiesTable = this
				.getInitialStatePropertiesTableView();

		int propertiesTableSelectedIndex = initialStatePropertiesTable
				.getInitialStatePropertiesTable().getSelectionModel()
				.getLeadSelectionIndex();
		int noOfProperties = this.initialStatePropertiesNamesList.size();

		// Offset for Instance HashMap Key in the PropertiesData ArrayList
		int instanceKeyOffset = (propertiesTableSelectedIndex * noOfProperties)
				+ PropertyIndexConstants.INSTANCE_HASH_MAP_KEY;

		Long selectedInstanceID = (Long) this.initialStatePropertiesData
				.get(instanceKeyOffset);

		// At 0th position in the JTable typeNAme is present
		String selectedType = (String) initialStatePropertiesTable
				.getInitialStatePropertiesTable().getValueAt(
						propertiesTableSelectedIndex, 0);

		validateInstanceCopy(selectedType, selectedInstanceID);

	}

	private void validateInstanceCopy(String selectedType,
			Long selectedInstanceID) {

		if (!this.initialstateUIcontroller.isObjektCreationValid(selectedType)) {
			JOptionPane.showMessageDialog(null,
					MessageBoxConstants.ERROR_EXCEED_INSTANCES_LIMIT,
					MessageBoxConstants.TITLE_COPY, JOptionPane.ERROR_MESSAGE);
		} else {
			Long newInstanceID = Long.parseLong(JOptionPane.showInputDialog(
					null, MessageBoxConstants.INPUT_ID,
					MessageBoxConstants.TITLE_COPY, JOptionPane.PLAIN_MESSAGE));

			switch (this.getSelectedListType()) {
			case AGENT_LIST:
			case OBJECT_LIST: {
				while (this.initialstateUIcontroller.isPresent(newInstanceID))

				{

					JOptionPane.showMessageDialog(null,
							MessageBoxConstants.ERROR_ID_PRESENT,
							MessageBoxConstants.TITLE_COPY,
							JOptionPane.ERROR_MESSAGE);
					newInstanceID = Long.parseLong(JOptionPane.showInputDialog(
							null, MessageBoxConstants.INPUT_ID,
							MessageBoxConstants.TITLE_COPY,
							JOptionPane.PLAIN_MESSAGE));
				}
				break;
			}
			case EVENT_LIST: {

				break;
			}
			case GLOBAL_VARIABLE_LIST:

			}

			this.initialstateUIcontroller.copyInitialStateUIInstance(
					newInstanceID, selectedInstanceID, selectedType);

			getPropertiesData();

		}
	}

	public void deleteInstance() {
		switch (this.initialStatePropertiesGUIType) {
		case PANEL: {
			deleteInstanceInPanel();

			break;

		}
		case TABLE: {
			deleteInstanceInTable();
			break;

		}
		}

	}

	private void deleteInstanceInPanel() {
		InitialStatePropertiesPanel initialStatePropertiesPanel = this
				.getInitialStatePropertiesPanel();
		int selectedPanelIndex = initialStatePropertiesPanel
				.getSelectedPanelIndex();

		InstancePanel selectedPanel = initialStatePropertiesPanel
				.getInstancesPanelList()[selectedPanelIndex];

		int noOfProperties = this.getInitialStatePropertiesNamesList().size();

		int typeNameIndex = selectedPanelIndex * noOfProperties
				+ PropertyIndexConstants.TYPE_NAME_INPUT_FIELD_INDEX;
		Long selectedInstanceID = selectedPanel.getInstancePanelKey();
		String selectedType = (String) this.getInitialStatePropertiesData()
				.get(typeNameIndex);
		this.initialstateUIcontroller.deleteInitialStateUIInstance(
				selectedInstanceID, selectedType);

		getPropertiesData();

	}

	private void deleteInstanceInTable() {
		int propertiesTableSelectedIndex = initialStatePropertiesTableview
				.getInitialStatePropertiesTable().getSelectionModel()
				.getLeadSelectionIndex();
		int noOfProperties = this.initialStatePropertiesNamesList.size();

		// Offset for Instance HashMap Key in the PropertiesData ArrayList
		int instanceKeyOffset = (propertiesTableSelectedIndex * noOfProperties)
				+ PropertyIndexConstants.INSTANCE_HASH_MAP_KEY;

		Long selectedInstanceID = (Long) this.initialStatePropertiesData
				.get(instanceKeyOffset);

		// At 0th position in the JTable typeNAme is present
		String selectedType = (String) initialStatePropertiesTableview
				.getInitialStatePropertiesTable().getValueAt(
						propertiesTableSelectedIndex, 0);

		this.initialstateUIcontroller.deleteInitialStateUIInstance(
				selectedInstanceID, selectedType);

		getPropertiesData();

	}

	/**
	 * @return the initialStateUIBottomPanel
	 */
	public InitialStateUIBottomPanel getInitialStateUIBottomPanel() {
		return initialStateUIBottomPanel;
	}

	/**
	 * @param initialStateUIBottomPanel
	 *            the initialStateUIBottomPanel to set
	 */
	public void setInitialStateUIBottomPanel(
			InitialStateUIBottomPanel initialStateUIBottomPanel) {
		this.initialStateUIBottomPanel = initialStateUIBottomPanel;
	}

	public void enableCopyDeleteBottomPanel() {
		InitialStateUIBottomPanel initialStateUIBottomPanel = this
				.getInitialStateUIBottomPanel();
		initialStateUIBottomPanel.enableCopyDelete();

	}

	public void initializeCreateObjektPanel() {

		if (validateObjektCreation()) {

			CreateObjektPanel createObjektPanel = new CreateObjektPanel(this);
			this.setCreateObjektPanel(createObjektPanel);
		}

	}

	public boolean initializeObjektCreation() {

		String selectedType = null;

		selectedType = findSelectedType();

		boolean flagerror = this.initialstateUIcontroller
				.updateInitialStateUIHashMap(selectedType);
		if (!flagerror) {
			getPropertiesData();
		}
		return flagerror;

	}

	/**
	 * @param selectedListType
	 *            the selectedListType to set
	 */
	public void setSelectedListType(ListType selectedListType) {
		this.selectedListType = selectedListType;
	}

	/**
	 * @return the selectedListType
	 */
	public ListType getSelectedListType() {
		return selectedListType;
	}

	/**
	 * @param createObjektPanel
	 *            the createObjektPanel to set
	 */
	public void setCreateObjektPanel(CreateObjektPanel createObjektPanel) {
		this.createObjektPanel = createObjektPanel;
	}

	/**
	 * @return the createObjektPanel
	 */
	public CreateObjektPanel getCreateObjektPanel() {
		return createObjektPanel;
	}

	/**
	 * @param initialStatePropertiesPanel
	 *            the initialStatePropertiesPanel to set
	 */
	public void setInitialStatePropertiesPanel(
			InitialStatePropertiesPanel initialStatePropertiesPanel) {
		this.initialStatePropertiesPanel = initialStatePropertiesPanel;
	}

	/**
	 * @return the initialStatePropertiesPanel
	 */
	public InitialStatePropertiesPanel getInitialStatePropertiesPanel() {
		return initialStatePropertiesPanel;
	}

	/**
	 * @param initialStatePropertiesGUIType
	 *            the initialStatePropertiesGUIType to set
	 */
	public void setInitialStatePropertiesGUIType(
			InitialStatePropertiesGUIType initialStatePropertiesGUIType) {
		this.initialStatePropertiesGUIType = initialStatePropertiesGUIType;
	}

	/**
	 * @return the initialStatePropertiesGUIType
	 */
	public InitialStatePropertiesGUIType getInitialStatePropertiesGUIType() {
		return initialStatePropertiesGUIType;
	}

	public void copyInstance() {
		switch (this.initialStatePropertiesGUIType) {
		case PANEL: {
			copyInstanceInPanelView();
			break;
		}
		case TABLE: {
			copyInstanceInTableView();
			break;
		}
		}

	}

	public boolean validateObjektCreation() {
		String selectedType = findSelectedType();
		if (!this.initialstateUIcontroller.isObjektCreationValid(selectedType)) {
			JOptionPane.showMessageDialog(null,
					MessageBoxConstants.ERROR_EXCEED_INSTANCES_LIMIT,
					MessageBoxConstants.TITLE_COPY, JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			return true;

		}
	}

	public void deSelectRestLists() {

		for (TypeList typeList : typeLists) {
			if (!typeList.getListType().equals(getSelectedListType())) {
				JList typesList = typeList.getTypesList();
				if (!typesList.isSelectionEmpty()) {

					try {
						typesList.clearSelection();
					} catch (Exception e) {

					}
				}
			}
		}

	}

	/**
	 * @param typeLists
	 *            the typeLists to set
	 */
	public void setTypeLists(ArrayList<TypeList> typeLists) {
		this.typeLists = typeLists;
	}

	/**
	 * @return the typeLists
	 */
	public ArrayList<TypeList> getTypeLists() {
		return typeLists;
	}

	public void updateUIForLanguageChosen() {
		String selectedLanguage = this.getInitialStateUIBottomPanel()
				.getSelectedLanguageType();
		switch (getSelectedListType()) {
		case AGENT_LIST:
		case EVENT_LIST:
		case OBJECT_LIST: {

			this.getinitialStateUIController().editPropertiesNamesHints(
					selectedLanguage);
			selectGUIType();
			break;

		}
		case GLOBAL_VARIABLE_LIST: {
			this.initialstateUIcontroller
					.updateGlobalVariableLabels(selectedLanguage);
			int globalVariablesListIndex = ListType.GLOBAL_VARIABLE_LIST
					.ordinal();

			int selectedIndex = this.typeLists.get(globalVariablesListIndex)
					.getTypesList().getSelectedIndex();

			this.populateGlobalVariablesList();
			this.typeLists.get(globalVariablesListIndex).getTypesList()
					.setSelectedIndex(selectedIndex);

			this.getPropertiesData();

		}

		}
	}

	public void updateListsPanel() {

		if (this.getTypeLists() != null) {
			for (int i = 0; i < this.getTypeLists().size(); i++) {
				this.getListsPanel().remove(
						this.getTypeLists().get(i).getTypesListScrollPane());

			}
			initialStatePropertiesJScrollPane.updateUI();

			this.centerPanel.updateUI();

			this.contentPanel.updateUI();

			this.updateUI();

		}

	}

	/**
	 * @param initialStatePropertiesHintsList
	 *            the initialStatePropertiesHintsList to set
	 */
	public void setInitialStatePropertiesHintsList(
			ArrayList<String> initialStatePropertiesHintsList) {
		this.initialStatePropertiesHintsList = initialStatePropertiesHintsList;
	}

	/**
	 * @return the initialStatePropertiesHintsList
	 */
	public ArrayList<String> getInitialStatePropertiesHintsList() {
		return initialStatePropertiesHintsList;
	}

	/**
	 * @param globalVariableHints
	 *            the globalVariableHints to set
	 */
	public void setGlobalVariableHints(ArrayList<String> globalVariableHints) {
		this.globalVariableHints = globalVariableHints;
	}

	/**
	 * @return the globalVariableHints
	 */
	public ArrayList<String> getGlobalVariableHints() {
		return globalVariableHints;
	}

	public void reinitializeUI() {
		this.contentPanel.removeAll();

		this.contentPanel.updateUI();
		initializeGUI(this.initialstateUIcontroller);
		
		this.initialstateUIcontroller.reinitialize();
		
	}

}
