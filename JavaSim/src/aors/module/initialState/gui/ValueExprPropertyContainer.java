package aors.module.initialState.gui;
import java.util.HashMap;
import java.util.HashSet;

/*This class will be used as ValueExpr container
it will contain the propertyType(property+type) and  
and two mappings. one maps between a property  and its language
set, the other maps between ValueExpr property and its value
*/

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


