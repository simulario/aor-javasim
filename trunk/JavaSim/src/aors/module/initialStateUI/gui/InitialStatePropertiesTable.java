package aors.module.initialStateUI.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import aors.module.initialStateUI.controller.InitialStateUIController;
import aors.module.initialStateUI.controller.InitialStateUIProperty;
import aors.module.initialStateUI.controller.InitialStateUIType;
import aors.module.initialStateUI.controller.UpdateType;

public class InitialStatePropertiesTable implements ListSelectionListener {

	private InitialStateUI initialStateUI;

	private JTable initialStatePropertiesTable;
	private InitialStatePropertiesTableModel initialStatePropertiesTableModel;

	// private JScrollPane propertiesTypeTablescrollPane;

	// Add the scroll pane to this panel.

	public InitialStatePropertiesTable(InitialStateUI initialStateUI,
			String selectedTypeLanguage) {

		this.initialStateUI = initialStateUI;
		this.initialStatePropertiesTableModel = new InitialStatePropertiesTableModel(
				initialStateUI, selectedTypeLanguage);
		initialStatePropertiesTable = new propertiesTable(
				this.initialStatePropertiesTableModel, initialStateUI);

		initialStatePropertiesTable.getSelectionModel()
				.addListSelectionListener(this);

		initialStatePropertiesTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		initialStatePropertiesTable
				.setPreferredScrollableViewportSize(new Dimension(500, 70));
		initialStatePropertiesTable.setFillsViewportHeight(true);

	}

	public void setInitialStateUI(InitialStateUI initialStateUI) {
		this.initialStateUI = initialStateUI;
	}

	public InitialStateUI getInitialStateUI() {
		return initialStateUI;
	}

	/**
	 * @param initialStatePropertiesTable
	 *            the propertiesTypeTable to set
	 */
	public void setInitialStatePropertiesTable(
			JTable initialStatePropertiesTable) {
		this.initialStatePropertiesTable = initialStatePropertiesTable;
	}

	/**
	 * @return the propertiesTypeTable
	 */
	public JTable getInitialStatePropertiesTable() {
		return initialStatePropertiesTable;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		this.initialStateUI.enableCopyDeleteBottomPanel();

	}

	/**
	 * @param propertiesTypeTableModel
	 *            the propertiesTypeTableModel to set
	 */
	public void setPropertiesTypeTableModel(
			InitialStatePropertiesTableModel propertiesTypeTableModel) {
		this.initialStatePropertiesTableModel = propertiesTypeTableModel;
	}

	/**
	 * @return the propertiesTypeTableModel
	 */
	public InitialStatePropertiesTableModel getInitialStatePropertiesTableModel() {
		return initialStatePropertiesTableModel;
	}

}

class propertiesTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InitialStateUI initialStateUI;

	public propertiesTable(
			InitialStatePropertiesTableModel initialStatePropertiesTableModel,
			InitialStateUI initialStateUI)

	{
		this.initialStateUI = initialStateUI;
		this.setModel(initialStatePropertiesTableModel);

	}

	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);

				int hintIndex = index + 1;
				// hintList also contains instanceHAshMapKey which is not
				// displayed in table

				String hint = getInitialStateUI()
						.getInitialStatePropertiesHintsList().get(hintIndex);

				return hint;
			}
		};
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

}

class InitialStatePropertiesTableModel extends DefaultTableModel {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private Object[] propertiesLabels;

	private Object[][] propertiesValues;

	private ArrayList<InitialStateUIProperty> selectedTypePropertiesList;

	private InitialStateUI initialStateUI;

	private int propertiesValuesColumnCount;
	private int propertiesValuesRowsCount;

	public InitialStatePropertiesTableModel(InitialStateUI initialStateUI,
			String selectedLanguage) {

		this.initialStateUI = initialStateUI;

		this.selectedTypePropertiesList = initialStateUI
				.getSelectedTypePropertiesList();

		getPropertiesLabelsForInitialStatePropertiesTable(selectedLanguage);

		getPropertiesValuesForInitialStatePropertiesTable();

	}

	private void getPropertiesValuesForInitialStatePropertiesTable() {
		ArrayList<Object> propertiesTypeTableDataList = initialStateUI
				.getInitialStatePropertiesData();

		propertiesValues = new Object[InitialStateUIType.UNBOUNDED][propertiesLabels.length];
		propertiesValuesColumnCount = propertiesLabels.length;
		int propertiesValuesColumnCounter = 0;
		int propertiesValuesRowCounter = 0;
		int propertiesValuesOffset = 0;

		propertiesValues[propertiesValuesRowCounter] = new Object[propertiesValuesColumnCount];

		for (int counter = 0; counter < propertiesTypeTableDataList.size(); counter++) {
			Object propertiesTypeTableDataListElement = propertiesTypeTableDataList
					.get(counter);

			if (counter != (PropertyIndexConstants.INSTANCE_HASH_MAP_KEY + propertiesValuesOffset)) {
				propertiesValues[propertiesValuesRowCounter][propertiesValuesColumnCounter] = propertiesTypeTableDataListElement;

				if (propertiesValuesColumnCounter == (propertiesValuesColumnCount - 1)) {
					propertiesValuesRowCounter++;
					propertiesValuesColumnCounter = 0;
					propertiesValues[propertiesValuesRowCounter] = new Object[propertiesValuesColumnCount];
				} else {
					propertiesValuesColumnCounter++;
				}
			}

			propertiesValuesOffset = propertiesValuesRowCounter
					* (this.initialStateUI.getInitialStatePropertiesNamesList()
							.size());

		}

		propertiesValuesRowsCount = propertiesValuesRowCounter;

	}

	private void getPropertiesLabelsForInitialStatePropertiesTable(
			String selectedLanguage) {
		ArrayList<String> initialStatePropertiesNamesList = initialStateUI
				.getInitialStatePropertiesNamesList();
		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = initialStateUI
				.getSelectedTypePropertiesList();
		propertiesLabels = new Object[initialStatePropertiesNamesList.size() - 1];
		String propertyUnitLabel;
		InitialStateUIProperty initialStateUIProperty;
		// At 0th Index InstanceHashMApKey is present .That should not be
		// displayed
		for (int i = 1; i < initialStatePropertiesNamesList.size(); i++) {
			initialStateUIProperty = selectedTypePropertiesList.get(i);

			propertiesLabels[i - 1] = initialStatePropertiesNamesList.get(i);
			propertyUnitLabel = this.initialStateUI
					.getinitialStateUIController().getPropertyUnitLabel(
							initialStateUIProperty);
			if (propertyUnitLabel != null) {
				propertiesLabels[i - 1] = propertiesLabels[i - 1] + "("
						+ propertyUnitLabel + ")";
			}

		}

	}

	public int getColumnCount() {
		return propertiesValuesColumnCount;
	}

	public int getRowCount() {
		return propertiesValuesRowsCount;
	}

	public String getColumnName(int col) {
		return (String) propertiesLabels[col];
	}

	public Object getValueAt(int row, int col) {

		return propertiesValues[row][col];

	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {

		// 0 & 1 Index are for TypeName and Instance ID ,These are always
		// read-only
		if (col < 2) {
			return false;
		} else {
			InitialStateUIProperty initialStateUIProperty = this.selectedTypePropertiesList
					.get(col);

			if (initialStateUIProperty.getReadonly() != InitialStateUIProperty.No_Read_Only_Provided) {
				boolean readOnly = initialStateUIProperty.getReadonly();
				if (readOnly == true) {
					return false;
				} else {
					return true;
				}

			} else {

				return true;
			}
		}

	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {

		String typeName = (String) propertiesValues[row][0];

		// instanceIDOffset - Offset for Instance Id in PropertiesData ArrayList
		int instanceIDOffset = (row * (this.initialStateUI
				.getInitialStatePropertiesNamesList().size()))
				+ PropertyIndexConstants.INSTANCE_HASH_MAP_KEY;

		Long instanceID = (Long) this.initialStateUI
				.getInitialStatePropertiesData().get(instanceIDOffset);

		int propertyIndex = col + 1;

		InitialStateUIProperty initialStateUIProperty = this.selectedTypePropertiesList
				.get(propertyIndex);
		// Since PropertiesStructure Contains one more property than Fields
		// Displayed (InstancesKashMap key)
		String changedPropertyName = initialStateUIProperty.getPropertyName();
		Object changedPropertyValue = value;
		String propertyLabel = this.getColumnName(col);

		value = InitialStatePropertiesPanel.checkForInputValidity(value,
				initialStateUIProperty, propertyLabel);

		if (value != null) {

			propertiesValues[row][col] = value;
			InitialStateUIController initialStateUIController = this
					.getInitialStateUI().getinitialStateUIController();

			initialStateUIController.updateInitialStateUIHashMap(typeName,
					instanceID, changedPropertyName, changedPropertyValue);

			initialStateUIController.addInitialStateUIEditedInformation(
					UpdateType.EDIT, typeName, instanceID, changedPropertyName);

			fireTableCellUpdated(row, col);
		}

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
	 * @return the propertiesNames
	 */
	public Object[] getPropertiesLabels() {
		return propertiesLabels;
	}

	/**
	 * @param propertiesLabels
	 *            the propertiesNames to set
	 */
	public void setPropertiesLabels(Object[] propertiesLabels) {
		this.propertiesLabels = propertiesLabels;
	}

	/**
	 * @return the propertiesValues
	 */
	public Object[][] getPropertiesValues() {
		return propertiesValues;
	}

	/**
	 * @param propertiesValues
	 *            the propertiesValues to set
	 */
	public void setPropertiesValues(Object[][] propertiesValues) {
		this.propertiesValues = propertiesValues;
	}

	/**
	 * @return the selectedTypePropertiesList
	 */
	public ArrayList<InitialStateUIProperty> getSelectedTypePropertiesList() {
		return selectedTypePropertiesList;
	}

	/**
	 * @param selectedTypePropertiesList
	 *            the selectedTypePropertiesList to set
	 */
	public void setSelectedTypePropertiesList(
			ArrayList<InitialStateUIProperty> selectedTypePropertiesList) {
		this.selectedTypePropertiesList = selectedTypePropertiesList;
	}

}
