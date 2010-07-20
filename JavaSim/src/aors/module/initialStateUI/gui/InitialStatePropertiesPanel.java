package aors.module.initialStateUI.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import aors.module.initialStateUI.controller.CategoryType;
import aors.module.initialStateUI.controller.InitialStateUIController;
import aors.module.initialStateUI.controller.InitialStateUIProperty;
import aors.module.initialStateUI.controller.PropertyNameConstants;
import aors.module.initialStateUI.controller.UpdateType;

public class InitialStatePropertiesPanel extends JPanel implements
		MouseListener, ActionListener, KeyListener {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	public static final Integer GLOBALS_PANEL_INDEX = 0;

	private JLabel[][] propertyLabels;
	private JComponent[][] inputFields;
	private JPanel[][] cellPanels;
	private InstancePanel[] instancesPanels;
	private InitialStateUI initialStateUI;

	private int selectedPanelIndex;

	public InitialStatePropertiesPanel(InitialStateUI initialStateUI,
			int noOfInstancePanels, String selectedLanguage) {

		this.initialStateUI = initialStateUI;

		this.setLayout(new GridLayout(noOfInstancePanels, 1));
		propertyLabels = new JLabel[noOfInstancePanels][];
		inputFields = new JComponent[noOfInstancePanels][];

		cellPanels = new JPanel[noOfInstancePanels][];

		this.instancesPanels = new InstancePanel[noOfInstancePanels];
		InstancePanel instancePanel;
		for (int i = 0; i < noOfInstancePanels; i++) {
			instancePanel = new InstancePanel();

			instancePanel.setBorder(new EtchedBorder());
			this.add(instancePanel);
			instancesPanels[i] = instancePanel;
			instancesPanels[i].addMouseListener(this);

			initializePanelControls(i);

		}

	}

	private void initializePanelControls(int instancePanelNo) {

		if (this.initialStateUI.getSelectedListType() == ListType.GLOBAL_VARIABLE_LIST) {
			initializePanelControlsGlobals(instancePanelNo);
		} else {
			initializePanelControlsEntity(instancePanelNo);
		}

	}

	private void initializePanelControlsEntity(int instancePanelNo) {
		InstancePanel instancePanel = instancesPanels[instancePanelNo];

		instancePanel.setLayout(new FlowLayout());
		ArrayList<String> initialStatePropertiesNamesList = this.initialStateUI
				.getInitialStatePropertiesNamesList();
		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = this.initialStateUI
				.getSelectedTypePropertiesList();
		ArrayList<Object> initialStatePropertiesData = this.initialStateUI
				.getInitialStatePropertiesData();
		ArrayList<String> initialStatePropertiesHintsList = this.initialStateUI
				.getInitialStatePropertiesHintsList();
		int noOfProperties = initialStatePropertiesNamesList.size();

		JLabel[] instanceLabels;
		JComponent[] instanceInputFields;
		JPanel[] instanceCellPanels;

		InitialStateUIProperty initialStateUIProperty;
		Object propertyValue;
		String propertyHint;

		int noOfFields = noOfProperties - 1; // Instance HashMap key
		// should not be
		// displayed
		instanceLabels = new JLabel[noOfFields];
		instanceInputFields = new JComponent[noOfFields];
		instanceCellPanels = new JPanel[noOfFields];
		this.propertyLabels[instancePanelNo] = instanceLabels;
		this.inputFields[instancePanelNo] = instanceInputFields;
		this.cellPanels[instancePanelNo] = instanceCellPanels;
		JLabel jlabel;

		JComponent jComponent = null;

		int propertiesValuesOffest;

		// This offset is for PropertiesValues ArrayList which
		// also conatins InstanceHashMApKey ,Therefore noOfProperties is
		// used instead of noOfFields.

		if (instancePanelNo != 0) {
			propertiesValuesOffest = noOfProperties * instancePanelNo;
		} else {
			propertiesValuesOffest = 0;
		}

		int fieldIndex;
		for (int i = 0; i < noOfProperties; i++) {
			if (i != (PropertyIndexConstants.INSTANCE_HASH_MAP_KEY)) {

				initialStateUIProperty = selectedTypePropertiesList.get(i);

				propertyHint = initialStatePropertiesHintsList.get(i);
				jlabel = new JLabel(initializePropertyLabel(i));

				jlabel.setToolTipText(propertyHint);

				// Since fields displayed is one less than
				// noofProperties(InstancesHashMapKey is not displayed)
				// Therfore fieldIndex is one less than i(Counter for
				// properties)
				fieldIndex = i - 1;
				instanceLabels[fieldIndex] = jlabel;
				instanceCellPanels[fieldIndex] = new JPanel();

				instanceCellPanels[fieldIndex].add(jlabel);

				propertyValue = initialStatePropertiesData.get(i
						+ propertiesValuesOffest);

				jComponent = initializeInputField(jComponent, propertyValue,
						initialStateUIProperty, propertyHint);

				instanceInputFields[fieldIndex] = jComponent;

				instanceCellPanels[fieldIndex].add(jComponent);

				instancePanel.add(instanceCellPanels[fieldIndex]);

				/*
				 * TypeName , Instance ID and read Only fields are not editable
				 */
				checkInputFieldEditablity(initialStateUIProperty, jComponent);
			} else {

				Long instanceHashMapKey = (Long) initialStatePropertiesData
						.get(i + propertiesValuesOffest);
				instancePanel.setInstancePanelKey(instanceHashMapKey);

			}
		}

	}

	private String initializePropertyLabel(int index) {
		String propertyLabel = null;

		ArrayList<String> initialStatePropertiesNamesList = this.initialStateUI
				.getInitialStatePropertiesNamesList();

		propertyLabel = initialStatePropertiesNamesList.get(index);

		return propertyLabel;
	}

	private void initializePanelControlsGlobals(int instancePanelNo) {

		ArrayList<String> initialStatePropertiesNamesList = this.initialStateUI
				.getInitialStatePropertiesNamesList();
		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = this.initialStateUI
				.getSelectedTypePropertiesList();
		ArrayList<Object> initialStatePropertiesData = this.initialStateUI
				.getInitialStatePropertiesData();
		ArrayList<String> initialStatePropertiesHintsList = this.initialStateUI
				.getInitialStatePropertiesHintsList();

		InstancePanel instancePanel = instancesPanels[instancePanelNo];

		instancePanel.setLayout(new FlowLayout());

		JLabel[] instanceLabels;
		JComponent[] instanceInputFields;
		JPanel[] instanceCellPanels;

		InitialStateUIProperty initialStateUIProperty;
		Object propertyValue;
		String propertyHint;

		instanceLabels = new JLabel[1];
		instanceInputFields = new JComponent[1];
		instanceCellPanels = new JPanel[1];
		this.propertyLabels[instancePanelNo] = instanceLabels;
		this.inputFields[instancePanelNo] = instanceInputFields;
		this.cellPanels[instancePanelNo] = instanceCellPanels;
		JLabel jlabel;

		JComponent jComponent = null;
		int globalVariableIndex = InitialStatePropertiesPanel.GLOBALS_PANEL_INDEX;
		propertyHint = initialStatePropertiesHintsList.get(globalVariableIndex);
		jlabel = new JLabel(initialStatePropertiesNamesList
				.get(globalVariableIndex));
		jlabel.setToolTipText(propertyHint);
		instanceLabels[globalVariableIndex] = jlabel;
		instanceCellPanels[globalVariableIndex] = new JPanel();

		instanceCellPanels[globalVariableIndex].add(jlabel);

		initialStateUIProperty = selectedTypePropertiesList
				.get(globalVariableIndex);

		propertyValue = initialStatePropertiesData.get(globalVariableIndex);

		jComponent = initializeInputField(jComponent, propertyValue,
				initialStateUIProperty, propertyHint);

		instanceInputFields[globalVariableIndex] = jComponent;

		instanceCellPanels[globalVariableIndex].add(jComponent);

		instancePanel.add(instanceCellPanels[globalVariableIndex]);

	}

	private void checkInputFieldEditablity(

	/*
	 * TypeName , Instance ID and read Only fields are not editable
	 */

	InitialStateUIProperty initialStateUIProperty, JComponent jComponent) {
		if (initialStateUIProperty.getReadonly() != InitialStateUIProperty.No_Read_Only_Provided) {
			boolean readOnly = initialStateUIProperty.getReadonly();

			if (readOnly == true) {
				if (jComponent instanceof JTextComponent) {
					((JTextComponent) jComponent).setEditable(false);
				} else {
					jComponent.setEnabled(false);
				}
			}
		}

		else {

			String propertyName = initialStateUIProperty.getPropertyName();
			if ((propertyName
					.equalsIgnoreCase(PropertyNameConstants.INSTANCE_ID))
					|| (propertyName
							.equalsIgnoreCase(PropertyNameConstants.TYPE_NAME))) {
				((JTextComponent) jComponent).setEditable(false);
			}

		}

	}

	private JComponent initializeInputField(JComponent jComponent,
			Object propertyValue,
			InitialStateUIProperty initialStateUIProperty, String propertyHint) {

		Long inputFieldLength;

		if (propertyValue != null) {
			if (propertyValue.getClass().equals(Boolean.class)) {

				jComponent = new JCheckBox();

				((JCheckBox) jComponent).setSelected((Boolean) propertyValue);
				((AbstractButton) jComponent).addActionListener(this);

			} else {

				inputFieldLength = initialStateUIProperty.getInputFieldLength();
				if (inputFieldLength != null
						&& inputFieldLength != InitialStateUIProperty.Unbounded_Field_Length) {
					jComponent = new JTextField();

					// ((JTextField) jComponent).setColumns(inputFieldLength);

				} else {
					jComponent = new JTextField();
					// ((JTextField) jComponent).setColumns(20);
				}

				((JTextField) jComponent).addActionListener(this);

				// ((JTextField) jComponent).addKeyListener(this);
				// inputFieldLength = ((JTextField) jComponent).getColumns();

				if (!propertyValue.toString().equalsIgnoreCase("")) {
					((JTextField) jComponent).setText(propertyValue.toString());
				} else {
					((JTextField) jComponent).setColumns(10);
				}

				/*
				 * if (propertyValue.toString().length() > inputFieldLength) {
				 * 
				 * ((JTextField) jComponent).setText(propertyValue.toString()
				 * .substring(0, inputFieldLength)); } else { ((JTextField)
				 * jComponent).setText(propertyValue.toString()); }
				 */
			}

		} else {
			jComponent = new JTextField();
			// ((JTextField) jComponent).setColumns(20);

			((JTextField) jComponent).addActionListener(this);
			// ((JTextField) jComponent).addKeyListener(this);

		}
		jComponent.setToolTipText(propertyHint);
		return jComponent;

	}

	/**
	 * @param instancesPanelList
	 *            the instancesPanelList to set
	 */
	public void setInstancesPanelList(InstancePanel[] instancesPanelList) {
		this.instancesPanels = instancesPanelList;
	}

	/**
	 * @return the instancesPanelList
	 */
	public InstancePanel[] getInstancesPanelList() {
		return instancesPanels;
	}

	/**
	 * @param initialStateUI
	 *            the initialStateUI to set
	 */
	public void setInitialStateUI(InitialStateUI initialStateUI) {
		this.initialStateUI = initialStateUI;
	}

	/**
	 * @return the initialStateUI
	 */
	public InitialStateUI getInitialStateUI() {
		return initialStateUI;
	}

	/**
	 * @param labelList
	 *            the labelList to set
	 */
	public void setLabelList(JLabel[][] labelList) {
		this.propertyLabels = labelList;
	}

	/**
	 * @return the labelList
	 */
	public JLabel[][] getLabelList() {
		return propertyLabels;
	}

	/**
	 * @param inputFieldList
	 *            the inputFieldList to set
	 */
	public void setInputFields(JComponent[][] inputFieldList) {
		this.inputFields = inputFieldList;
	}

	/**
	 * @return the inputFieldList
	 */
	public JComponent[][] getInputFields() {
		return inputFields;
	}

	/**
	 * @param cellPanels
	 *            the cellPanels to set
	 */
	public void setCellPanels(JPanel[][] cellPanels) {
		this.cellPanels = cellPanels;
	}

	/**
	 * @return the cellPanels
	 */
	public JPanel[][] getCellPanels() {
		return cellPanels;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		for (int i = 0; i < this.instancesPanels.length; i++) {
			if (e.getSource().equals(instancesPanels[i])) {
				instancesPanels[i].setBorder(new EtchedBorder(Color.BLUE,
						Color.BLUE));

				this.selectedPanelIndex = i;
				this.initialStateUI.enableCopyDeleteBottomPanel();

			} else {
				instancesPanels[i].setBorder(null);
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param selectedPanelIndex
	 *            the selectedPanelIndex to set
	 */
	public void setSelectedPanelIndex(int selectedPanelIndex) {
		this.selectedPanelIndex = selectedPanelIndex;
	}

	/**
	 * @return the selectedPanelIndex
	 */
	public int getSelectedPanelIndex() {
		return selectedPanelIndex;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int instancePanelIndex = 0; instancePanelIndex < instancesPanels.length; instancePanelIndex++) {

			for (int inputFieldIndex = 0; inputFieldIndex < propertyLabels[instancePanelIndex].length; inputFieldIndex++) {
				if (e.getSource().equals(
						inputFields[instancePanelIndex][inputFieldIndex])) {

					if (!initializeValueUpdate(instancePanelIndex,
							inputFieldIndex))
						;
					else {
						this.inputFields[instancePanelIndex][inputFieldIndex]
								.setBorder(new EtchedBorder(Color.RED,
										Color.RED));
					}

					break;

				}
			}
		}

	}

	private boolean initializeValueUpdate(int instancePanelIndex,
			int inputFieldIndex) {
		InstancePanel instancePanel = instancesPanels[instancePanelIndex];

		ArrayList<String> initialStatePropertiesNamesList = this.initialStateUI
				.getInitialStatePropertiesNamesList();
		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = this.initialStateUI
				.getSelectedTypePropertiesList();
		ArrayList<Object> initialStatePropertiesData = this.initialStateUI
				.getInitialStatePropertiesData();

		int noOfProperties = initialStatePropertiesNamesList.size();

		String typeName = null;
		Long instanceID = null;
		int propertyIndex = 0;
		int initialStatePropertiesDataIndex = 0;

		switch (this.initialStateUI.getSelectedListType()) {
		case AGENT_LIST:
		case EVENT_LIST:
		case OBJECT_LIST: {
			// 0th Column for TypeNAme in the Fields Displayed in the Panel
			typeName = ((JTextField) inputFields[instancePanelIndex][0])
					.getText();
			instanceID = instancePanel.getInstancePanelKey();
			// Since PropertiesStructure Contains one more property than Fields
			// Displayed (InstancesKashMap key)
			propertyIndex = inputFieldIndex + 1;

			break;

		}
		case GLOBAL_VARIABLE_LIST: {
			// TypeNAme is always Global for GLobal variables
			typeName = CategoryType.Global.name();
			instancePanel.setInstancePanelKey(InstancePanel.KEY_FOR_GLOBALS);
			instanceID = instancePanel.getInstancePanelKey();
			// PropertiesStructure Contains same no as no. of Fields
			// Displayed
			propertyIndex = inputFieldIndex;
			break;

		}
		}

		initialStatePropertiesDataIndex = (instancePanelIndex * noOfProperties)
				+ propertyIndex;
		String changedPropertyName = selectedTypePropertiesList.get(
				propertyIndex).getPropertyName();
		String changedPropertyLabel = initialStatePropertiesNamesList
				.get(propertyIndex);

		JComponent jComponent = inputFields[instancePanelIndex][inputFieldIndex];
		Object changedPropertyValue;

		boolean flagError = false;
		Object oldValue;
		oldValue = initialStatePropertiesData
				.get(initialStatePropertiesDataIndex);

		flagError = checkForEmptyInput(jComponent, oldValue);

		if (!flagError) {
			Class<? extends Object> propertyValueClass = initialStatePropertiesData
					.get(initialStatePropertiesDataIndex).getClass();

			changedPropertyValue = getChangedPropertyValue(jComponent,
					propertyValueClass, changedPropertyLabel);

			if (changedPropertyValue != null) {
				initialStatePropertiesData.set(initialStatePropertiesDataIndex,
						changedPropertyValue);

				InitialStateUIController initialStateUIController = this
						.getInitialStateUI().getinitialStateUIController();

				initialStateUIController.updateInitialStateUIHashMap(typeName,
						instanceID, changedPropertyName, changedPropertyValue);

				initialStateUIController.addInitialStateUIEditedInformation(
						UpdateType.EDIT, typeName, instanceID,
						changedPropertyName);
				flagError = false;

			} else {
				flagError = true;
			}

		}
		return flagError;

	}

	private boolean checkForEmptyInput(JComponent jComponent, Object oldValue) {
		boolean flagError = false;
		if (jComponent.getClass().equals(JTextField.class)) {
			String input = ((JTextComponent) jComponent).getText();
			if (input.equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null,
						MessageBoxConstants.ERROR_FIELDS_EMPTY_EDIT,
						MessageBoxConstants.TITLE_EDIT,
						JOptionPane.INFORMATION_MESSAGE);
				((JTextComponent) jComponent).setText(oldValue.toString());

				flagError = true;

			}
		}
		return flagError;
	}

	private Object getChangedPropertyValue(JComponent jComponent,
			Class<? extends Object> propertyValueClass,
			String changedPropertyLabel) {

		String errMsg;
		Object propertyValue = null;

		try {
			if (propertyValueClass.equals(Long.class)) {
				propertyValue = Long.parseLong(((JTextComponent) jComponent)
						.getText());
			} else if (propertyValueClass.equals(Double.class)) {
				propertyValue = Double
						.parseDouble(((JTextComponent) jComponent).getText());
			} else if (propertyValueClass.equals(Boolean.class)) {
				propertyValue = ((JCheckBox) jComponent).isSelected();

			} else {
				propertyValue = ((JTextComponent) jComponent).getText();
			}
		} catch (NumberFormatException e) {
			errMsg = "Property : " + changedPropertyLabel + " is of "
					+ propertyValueClass.getSimpleName()
					+ " type. Please enter the correct type";

			JOptionPane.showMessageDialog(null, errMsg,
					MessageBoxConstants.TITLE_NEW, JOptionPane.ERROR_MESSAGE);

		}
		return propertyValue;

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (int instancePanelIndex = 0; instancePanelIndex < instancesPanels.length; instancePanelIndex++) {

			for (int inputFieldIndex = 0; inputFieldIndex < propertyLabels[instancePanelIndex].length; inputFieldIndex++) {
				if (e.getSource().equals(
						inputFields[instancePanelIndex][inputFieldIndex])) {

					JComponent jComponent = inputFields[instancePanelIndex][inputFieldIndex];
					int inputFieldLength = ((JTextField) jComponent)
							.getColumns();

					if (inputFieldLength < ((JTextField) jComponent).getText()
							.length()) {
						JOptionPane.showMessageDialog(null,
								MessageBoxConstants.INPUT_FIELD_LENGTH_EXCEED
										+ inputFieldLength,
								MessageBoxConstants.TITLE_EDIT,
								JOptionPane.INFORMATION_MESSAGE);
					}
					break;
				}
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
