package aors.module.initialStateUI.controller;

import java.util.HashMap;

public class InitialStateUIInstance implements Cloneable {

	private Long id;
	private String name;
	private HashMap<String,InitialStateUIPropertyValue>instancePropertiesValuesHashMap;
	

	public InitialStateUIInstance(
			InitialStateUIInstance initialStateUIInstance, Long id) {
		this.id = id;
		this.name = initialStateUIInstance.getName();
		this.instancePropertiesValuesHashMap = new HashMap<String, InitialStateUIPropertyValue>();

		copyInstancePropertiesHashMapValues(initialStateUIInstance);

	}

	@SuppressWarnings("unchecked")
	private void copyInstancePropertiesHashMapValues(
			InitialStateUIInstance initialStateUIInstance) {
		this.instancePropertiesValuesHashMap = (HashMap<String, InitialStateUIPropertyValue>) initialStateUIInstance
				.getInstancePropertiesValuesHashMap().clone();
		String clonedProperty;
		for (String propertyName : this.instancePropertiesValuesHashMap.keySet()) {
			InitialStateUIPropertyValue initialStateUIPropertyValue = (InitialStateUIPropertyValue) this.instancePropertiesValuesHashMap
					.get(propertyName).clone();
			clonedProperty = new String(propertyName);

			Object propertyValue = initialStateUIPropertyValue.getPropertyValue();
			Object clonedPropertyValue = null;
			if (propertyValue.getClass().equals(Boolean.class)) {

				clonedPropertyValue = new Boolean(((Boolean) propertyValue)
						.booleanValue());
			} else if (propertyValue.getClass().equals(Long.class)) {

				clonedPropertyValue = new Long(((Long) propertyValue)
						.longValue());
			} else if (propertyValue.getClass().equals(Double.class)) {
				clonedPropertyValue = new Double(((Double) propertyValue)
						.doubleValue());
			} else if (propertyValue.getClass().equals(String.class)) {
				clonedPropertyValue = new String(propertyValue.toString());
			}

			initialStateUIPropertyValue.setPropertyValue(clonedPropertyValue);
			this.instancePropertiesValuesHashMap.put(clonedProperty,
					initialStateUIPropertyValue);

		}

	}

	public InitialStateUIInstance(Long id) {
		this.id = id;
		this.name = new String();
		this.instancePropertiesValuesHashMap = new HashMap<String, InitialStateUIPropertyValue>();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param instancePropertiesValuesHashMap the instancePropertiesValuesHashMap to set
	 */
	public void setInstancePropertiesValuesHashMap(
			HashMap<String,InitialStateUIPropertyValue> instancePropertiesValuesHashMap) {
		this.instancePropertiesValuesHashMap = instancePropertiesValuesHashMap;
	}

	/**
	 * @return the instancePropertiesValuesHashMap
	 */
	public HashMap<String,InitialStateUIPropertyValue> getInstancePropertiesValuesHashMap() {
		return instancePropertiesValuesHashMap;
	}

}
