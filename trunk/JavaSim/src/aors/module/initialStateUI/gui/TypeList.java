package aors.module.initialStateUI.gui;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TypeList implements ListSelectionListener {

	private InitialStateUI initialStateUI;

	// List containing all the Object Types
	private JList typesList;

	// List Model for the List
	private DefaultListModel typesListModel;

	private JScrollPane typesListScrollPane;

	private String selectedType;

	private ListType listType;
	private InitialStateUIStatus initialStateUIStatus;

	/*
	 * Constructor for the TypeList Class .Populating the typesListModel with
	 * the parameter array. Instantiating typesList with the typesListModel as
	 * parameter
	 * 
	 * @param typeNamesList String Array containing all the Object Types
	 */
	public TypeList(ArrayList<String> typeNamesList,
			InitialStateUI initialStateUI, ListType listType,
			InitialStateUIStatus initialStateUIStatus) {

		this.initialStateUIStatus = initialStateUIStatus;
		this.initialStateUI = initialStateUI;
		this.listType = listType;
		typesListModel = new DefaultListModel();

		Iterator<String> typesListIterator = typeNamesList.iterator();

		while (typesListIterator.hasNext()) {
			typesListModel.addElement(typesListIterator.next());
		}

		setTypesList(new JList(typesListModel));

		this.typesList.setVisibleRowCount(4);

		this.typesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.typesList.addListSelectionListener(this);

		setTypesListScrollPane(new JScrollPane(this.typesList));

		if (!typesList.isSelectionEmpty()) {
			this.selectedType = new String(((String) typesList
					.getSelectedValue()));
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		this.selectedType = new String(((String) typesList.getSelectedValue()));
		if (!initialStateUIStatus.equals(InitialStateUIStatus.LANGUAGE_CHANGED)) {
			this.getInitialStateUI()
					.initializeInitializeStateUIForSelectedType(selectedType,
							this.listType);

		} else {
			initialStateUIStatus = InitialStateUIStatus.CREATED;
		}
	}

	public void setTypesList(JList typesList) {
		this.typesList = typesList;
	}

	public JList getTypesList() {
		return typesList;
	}

	public void setTypesListScrollPane(JScrollPane typesListScrollPane) {
		this.typesListScrollPane = typesListScrollPane;
	}

	public JScrollPane getTypesListScrollPane() {
		return typesListScrollPane;
	}

	public void setInitialStateUI(InitialStateUI initialStateUI) {
		this.initialStateUI = initialStateUI;
	}

	public InitialStateUI getInitialStateUI() {
		return initialStateUI;
	}

	/**
	 * @return the selectedObjectType
	 */
	public String getSelectedType() {
		return selectedType;
	}

	/**
	 * @param selectedType
	 *            the selectedObjectType to set
	 */
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	/**
	 * @param listType
	 *            the listType to set
	 */
	public void setListType(ListType listType) {
		this.listType = listType;
	}

	/**
	 * @return the listType
	 */
	public ListType getListType() {
		return listType;
	}

	/**
	 * @param initialStateUIStatus
	 *            the initialStateUIStatus to set
	 */
	public void setInitialStateUIStatus(
			InitialStateUIStatus initialStateUIStatus) {
		this.initialStateUIStatus = initialStateUIStatus;
	}

	/**
	 * @return the initialStateUIStatus
	 */
	public InitialStateUIStatus getInitialStateUIStatus() {
		return initialStateUIStatus;
	}

}
