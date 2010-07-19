package aors.module.initialStateUI.controller;

public class Unit {

	private UnitQuantityType unitQuantityType;

	Area area;
	Currency currency;
	Length length;
	Math math;
	Physics physics;
	Time time;
	Volume volume;
	Weight weight;
private boolean unitPresent; 

	/**
	 * @param unitQuantityType
	 *            the unitQuantityType to set
	 */
	public void setUnitQuantityType(UnitQuantityType unitQuantityType) {
		this.unitQuantityType = unitQuantityType;
	}

	/**
	 * @return the unitQuantityType
	 */
	public UnitQuantityType getUnitQuantityType() {
		return unitQuantityType;
	}

	/**
	 * @param unitPresent the unitPresent to set
	 */
	public void setUnitPresent(boolean unitPresent) {
		this.unitPresent = unitPresent;
	}

	/**
	 * @return the unitPresent
	 */
	public boolean isUnitPresent() {
		return unitPresent;
	}

	/**
	 * @param unitType
	 *            the unitType to set
	 */


}
