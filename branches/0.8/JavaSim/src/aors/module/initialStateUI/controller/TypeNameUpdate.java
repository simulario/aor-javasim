package aors.module.initialStateUI.controller;

import java.util.ArrayList;
import java.util.HashMap;

public class TypeNameUpdate {

	private HashMap<String, ArrayList<Long>> typeNameUpdatesHashMap;
	
	
	public TypeNameUpdate()
	{
		typeNameUpdatesHashMap = new HashMap<String, ArrayList<Long>>();
	}
	

	public void addtypeNameUpdatesHashMapEntry(String updatedTypeName,
			Long updatedInstanceId) {
		ArrayList<Long> updatedInstancesList;
		if (typeNameUpdatesHashMap.containsKey(updatedTypeName)) {
			updatedInstancesList = typeNameUpdatesHashMap.get(updatedTypeName);
			updatedInstancesList.add(updatedInstanceId);
		} else {
			updatedInstancesList = new ArrayList<Long>();
			updatedInstancesList.add(updatedInstanceId);
			this.typeNameUpdatesHashMap.put(updatedTypeName,
					updatedInstancesList);

		}

	}

	public ArrayList<Long> getUpdatesInstancesList(String updatedTypeName) {
		return this.typeNameUpdatesHashMap.get(updatedTypeName);
	}

	/**
	 * @param typeNameUpdatesHashMap
	 *            the typeNameUpdatesHashMap to set
	 */
	public void setTypeNameUpdatesHashMap(
			HashMap<String, ArrayList<Long>> typeNameUpdatesHashMap) {
		this.typeNameUpdatesHashMap = typeNameUpdatesHashMap;
	}

	/**
	 * @return the typeNameUpdatesHashMap
	 */
	public HashMap<String, ArrayList<Long>> getTypeNameUpdatesHashMap() {
		return typeNameUpdatesHashMap;
	}

}
