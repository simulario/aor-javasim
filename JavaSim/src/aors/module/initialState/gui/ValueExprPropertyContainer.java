package aors.module.initialState.gui;
import java.util.HashMap;
import java.util.HashSet;



public class ValueExprPropertyContainer {
  
  
  public ValueExprPropertyContainer(
      String propertyType,
      HashMap<String, HashSet<String>> valueExprPropertyMap,
      HashMap<String, String> valueExprValueMap) {
  
    
      this.propertyType = propertyType;
      this.valueExprPropertyMap = valueExprPropertyMap;
      this.valueExprValueMap = valueExprValueMap;
  }
  
  
  public String getPropertyType() {
    return propertyType;
  }


  public void setPropertyType(String propertyType) {
    this.propertyType = propertyType;
  }
  
  public HashMap<String, HashSet<String>> getValueExprPropertyMap() {
    return valueExprPropertyMap;
  }
  
  
  public void setValueExprPropertyMap(
      HashMap<String, HashSet<String>> valueExprPropertyMap) {
      this.valueExprPropertyMap = valueExprPropertyMap;
  }
  
  
  public HashMap<String, String> getValueExprValueMap() {
    return valueExprValueMap;
  }
  
  
  public void setValueExprValueMap(HashMap<String, String> valueExprValueMap) {
    this.valueExprValueMap = valueExprValueMap;
  }
  
  
 


  private String propertyType;
  private HashMap<String,HashSet<String>> valueExprPropertyMap = new HashMap<String,HashSet<String>>();
  private HashMap<String,String> valueExprValueMap = new HashMap<String,String>();

}


