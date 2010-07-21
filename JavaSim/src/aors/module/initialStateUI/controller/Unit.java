package aors.module.initialStateUI.controller;

public class Unit {

	private UnitQuantityType unitQuantityType;

	private Area area;
	private Currency currency;
	private Length length;
	private Math math;
	private Physics physics;
	private Time time;
	private Volume volume;
	private Weight weight;
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
	 * @param unitPresent
	 *            the unitPresent to set
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
	 * @return the area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	/**
	 * @return the weight
	 */
	public Weight getWeight() {
		return weight;
	}

	/**
	 * @param volume
	 *            the volume to set
	 */
	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	/**
	 * @return the volume
	 */
	public Volume getVolume() {
		return volume;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(Time time) {
		this.time = time;
	}

	/**
	 * @return the time
	 */
	public Time getTime() {
		return time;
	}

	/**
	 * @param physics
	 *            the physics to set
	 */
	public void setPhysics(Physics physics) {
		this.physics = physics;
	}

	/**
	 * @return the physics
	 */
	public Physics getPhysics() {
		return physics;
	}

	/**
	 * @param math
	 *            the math to set
	 */
	public void setMath(Math math) {
		this.math = math;
	}

	/**
	 * @return the math
	 */
	public Math getMath() {
		return math;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(Length length) {
		this.length = length;
	}

	/**
	 * @return the length
	 */
	public Length getLength() {
		return length;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param unitType
	 *            the unitType to set
	 */

}
