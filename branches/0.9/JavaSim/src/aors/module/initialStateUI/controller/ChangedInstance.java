package aors.module.initialStateUI.controller;

import java.util.HashMap;

public class ChangedInstance {
	
	private Long changedInstanceId;
	private HashMap<String, Object>changedPropertiesHashMap;

	public ChangedInstance(Long changedInstanceId)
	{
		this.changedInstanceId = changedInstanceId;
		changedPropertiesHashMap = new HashMap<String, Object>();
	}

	/**
	 * @return the changedInstanceId
	 */
	public Long getChangedInstanceId() {
		return changedInstanceId;
	}

	/**
	 * @param changedPropertiesHashMap the changedPropertiesHashMap to set
	 */
	public void setChangedPropertiesHashMap(HashMap<String, Object> changedPropertiesHashMap) {
		this.changedPropertiesHashMap = changedPropertiesHashMap;
	}

	/**
	 * @return the changedPropertiesHashMap
	 */
	public HashMap<String, Object> getChangedPropertiesHashMap() {
		return changedPropertiesHashMap;
	}
	
	
	public void addChangedPropertiesHashMapEntry(String changedPropertyName, Object changedPropertyValue)
	{
		this.changedPropertiesHashMap.put(changedPropertyName, changedPropertyValue);
	}

	public Object getChangedPropertiesHashMapValue(String key)
	{
		return this.changedPropertiesHashMap.get(key);
	}

}

