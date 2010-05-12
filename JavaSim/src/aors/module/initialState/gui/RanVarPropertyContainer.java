package aors.module.initialState.gui;
import java.util.HashMap;
import java.util.HashSet;



public class RanVarPropertyContainer {
	
	
	public RanVarPropertyContainer(
			String selectedType,
			HashMap<String, HashSet<String>> ranVarPropertyMap,
			HashMap<String, String> ranVarValueMap) {
	
		this.selectedType = selectedType;
		this.ranVarPropertyMap = ranVarPropertyMap;
		this.ranVarValueMap = ranVarValueMap;
	}
	
	public RanVarPropertyContainer(
			String selectedType,
			HashMap<String, HashSet<String>> ranVarPropertyMap,
			HashMap<String, HashSet<String>> ranVarLanMap,
			HashMap<String, String> ranVarValueMap) {
	
		this.selectedType = selectedType;
		this.ranVarPropertyMap = ranVarPropertyMap;
		this.ranVarLanMap = ranVarLanMap;
		this.ranVarValueMap = ranVarValueMap;
		
	}
	
	
	public String getSelectedType() {
		return selectedType;
	}


	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public HashMap<String, HashSet<String>> getRanVarLanMap() {
		return ranVarLanMap;
	}

	public void setRanVarLanMap(HashMap<String, HashSet<String>> ranVarLanMap) {
		this.ranVarLanMap = ranVarLanMap;
	}

	public HashMap<String, HashSet<String>> getRanVarPropertyMap() {
		return ranVarPropertyMap;
	}
	public void setRanVarPropertyMap(
			HashMap<String, HashSet<String>> ranVarPropertyMap) {
		this.ranVarPropertyMap = ranVarPropertyMap;
	}
	public HashMap<String, String> getRanVarValueMap() {
		return ranVarValueMap;
	}
	public void setRanVarValueMap(HashMap<String, String> ranVarValueMap) {
		this.ranVarValueMap = ranVarValueMap;
	}
	
	



    private String selectedType = null;
    private HashMap<String,HashSet<String>> ranVarPropertyMap = null;
	private HashMap<String,String> ranVarValueMap = null;
	private HashMap<String,HashSet<String>> ranVarLanMap = null;
	
	
	

}
