package aors.module.initialStateUI.controller;

import java.util.HashMap;

public class InitialStateUIHashMap {

	private HashMap<String, InitialStateUIType> initialStateTypeshashMap;
	
	
	public InitialStateUIHashMap()
	{
		this.initialStateTypeshashMap = new HashMap<String, InitialStateUIType>();
	}
	
	
	

	public InitialStateUIType getTypeStructure(String key) {
		return this.initialStateTypeshashMap.get(key);
	}

	public void addTypeStructure(String key,
			InitialStateUIType initialStateUIType) {
		this.initialStateTypeshashMap.put(key, initialStateUIType);
	}

	/**
	 * @param initialStateTypeshashMap
	 *            the initialStateTypeshashMap to set
	 */
	public void setInitialStateTypeshashMap(
			HashMap<String, InitialStateUIType> initialStateTypeshashMap) {
		this.initialStateTypeshashMap = initialStateTypeshashMap;
	}

	/**
	 * @return the initialStateTypeshashMap
	 */
	public HashMap<String, InitialStateUIType> getInitialStateTypeshashMap() {
		return initialStateTypeshashMap;
	}

}
