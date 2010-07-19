package aors.module.initialStateUI.controller;

import java.util.LinkedList;
import java.util.ListIterator;

public class InitialStateUIEditedInformation {

	private InitialStateUIController initialStateUIController;

	private LinkedList<ObjektUpdate> initialStateUIUpdatesList;

	public InitialStateUIEditedInformation(
			InitialStateUIController initialStateUIController) {
		this.initialStateUIController = initialStateUIController;

		this.initialStateUIUpdatesList = new LinkedList<ObjektUpdate>();

	}

	/**
	 * @param initialStateUIController
	 *            the initialStateUIController to set
	 */
	public void setInitialStateUIController(
			InitialStateUIController initialStateUIController) {
		this.initialStateUIController = initialStateUIController;
	}

	/**
	 * @return the initialStateUIController
	 */
	public InitialStateUIController getInitialStateUIController() {
		return initialStateUIController;
	}

	public void addObjektUpdate(UpdateType updateType, String typeName,
			Long instanceID) {
		ObjektUpdate objektUpdate = new ObjektUpdate(updateType, typeName,
				instanceID);
		this.initialStateUIUpdatesList.add(objektUpdate);

	}

	public void addObjektUpdate(UpdateType updateType, String typeName,
			Long instanceID, String updatedPropertyName) {
		ObjektUpdate objektUpdate = new ObjektUpdate(updateType, typeName,
				instanceID, updatedPropertyName);
		this.initialStateUIUpdatesList.add(objektUpdate);

	}

	public void processInitialStateUIEditedInformation() {
		ListIterator<ObjektUpdate> initialStateUIUpdatesListIterator = this.initialStateUIUpdatesList
				.listIterator();
		ObjektUpdate objektUpdate;

		while (initialStateUIUpdatesListIterator.hasNext()) {
			objektUpdate = initialStateUIUpdatesListIterator.next();

			this.initialStateUIController.updateObjekt(objektUpdate);

		}
	}

	/**
	 * @param initialStateUIUpdatesList
	 *            the initialStateUIUpdatesList to set
	 */
	public void setInitialStateUIUpdatesList(
			LinkedList<ObjektUpdate> initialStateUIUpdatesList) {
		this.initialStateUIUpdatesList = initialStateUIUpdatesList;
	}

	/**
	 * @return the initialStateUIUpdatesList
	 */
	public LinkedList<ObjektUpdate> getInitialStateUIUpdatesList() {
		return initialStateUIUpdatesList;
	}

}
