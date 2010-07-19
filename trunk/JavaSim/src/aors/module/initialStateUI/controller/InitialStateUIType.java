package aors.module.initialStateUI.controller;

import java.util.HashMap;

public class InitialStateUIType {
	
	public final static int UNBOUNDED = 10000;
	public final static int NO_INSTANCES_EXIST = 1;
	private String typeName;
	private int nmrOfInstances;
  
	private  CategoryType categoryType;	
	
	
	
	private HashMap<String,InitialStateUIProperty>propertiesInfoHashMap;
	private HashMap<Long, InitialStateUIInstance> instancesHashMap;
	
	private Object[] languageSet;

	
	
	
	
	public InitialStateUIInstance getInitialStateUIInstanceStructure(Long key) {
		return this.instancesHashMap.get(key);
	}

	public void addInitialStateUIInstance(Long key,
			InitialStateUIInstance initialStateUIInstance) {
		this.getInstancesHashMap().put(key, initialStateUIInstance);
	}

	
	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return the typeName
	 */
	
	
	
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param nmrOfInstances
	 *            the nmrOfInstances to set
	 */
	public void setNmrOfInstances(int nmrOfInstances) {
		this.nmrOfInstances = nmrOfInstances;
	}

	/**
	 * @return the nmrOfInstances
	 */
	public int getNmrOfInstances() {
		return nmrOfInstances;
	}

	
	/**
	 * @param instancesHashMap
	 *            the instancesHashMap to set
	 */
	public void setInstancesHashMap(
			HashMap<Long, InitialStateUIInstance> instancesHashMap) {
		this.instancesHashMap = instancesHashMap;
	}

	/**
	 * @return the instancesHashMap
	 */
	public HashMap<Long, InitialStateUIInstance> getInstancesHashMap() {
		return instancesHashMap;
	}

	

	/**
	 * @param categoryType the superType to set
	 */
	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
	}

	/**
	 * @return the superType
	 */
	public CategoryType getCategoryType() {
		return categoryType;
	}

	/**
	 * @param languageSet the languageSet to set
	 */
	public void setLanguageSet(Object[] languageSet) {
		this.languageSet = languageSet;
	}

	/**
	 * @return the languageSet
	 */
	public Object[] getLanguageSet() {
		return languageSet;
	}

	/**
	 * @param propertiesInfoHashMap the propertiesInfoHashMap to set
	 */
	public void setPropertiesInfoHashMap(HashMap<String,InitialStateUIProperty> propertiesInfoHashMap) {
		this.propertiesInfoHashMap = propertiesInfoHashMap;
	}

	/**
	 * @return the propertiesInfoHashMap
	 */
	public HashMap<String,InitialStateUIProperty> getPropertiesInfoHashMap() {
		return propertiesInfoHashMap;
	}

	
}
