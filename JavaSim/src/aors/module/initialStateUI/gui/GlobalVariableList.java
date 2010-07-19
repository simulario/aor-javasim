package aors.module.initialStateUI.gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GlobalVariableList implements ListSelectionListener {
	
private InitialStateUI initialStateUI;
	

	private JList globalVariableList;
	
	private DefaultListModel globalVariableListModel;
	
	private JScrollPane globalVariableListScrollPane;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setInitialStateUI(InitialStateUI initialStateUI) {
		this.initialStateUI = initialStateUI;
	}

	public InitialStateUI getInitialStateUI() {
		return initialStateUI;
	}

	public void setGlobalVariableList(JList globalVariableList) {
		this.globalVariableList = globalVariableList;
	}

	public JList getGlobalVariableList() {
		return globalVariableList;
	}

	public void setGlobalVariableListModel(DefaultListModel globalVariableListModel) {
		this.globalVariableListModel = globalVariableListModel;
	}

	public DefaultListModel getGlobalVariableListModel() {
		return globalVariableListModel;
	}

	public void setGlobalVariableListScrollPane(
			JScrollPane globalVariableListScrollPane) {
		this.globalVariableListScrollPane = globalVariableListScrollPane;
	}

	public JScrollPane getGlobalVariableListScrollPane() {
		return globalVariableListScrollPane;
	}

}
