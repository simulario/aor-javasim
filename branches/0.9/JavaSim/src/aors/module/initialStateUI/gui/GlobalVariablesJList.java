package aors.module.initialStateUI.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListModel;

public class GlobalVariablesJList extends JList {

	private InitialStateUI initialStateUI;

	public GlobalVariablesJList(InitialStateUI initialStateUI,
			ListModel listModel) {
		this.initialStateUI = initialStateUI;
		this.setModel(listModel);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getToolTipText(MouseEvent event) {
		Point point = event.getPoint();

		int index = this.locationToIndex(point);

		String propertyHint = this.initialStateUI.getGlobalVariableHints().get(
				index);

		return propertyHint;
	}

}
