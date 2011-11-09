/**
 * 
 */
package aors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jens Werner, Christian Noack
 * 
 */
public class GeneralSimulationModel {

  private Map<String, String> paramMap;

  public GeneralSimulationModel() {
    paramMap = new HashMap<String, String>();
    Field[] declaredFields = this.getClass().getDeclaredFields();
    for (Field f : declaredFields) {
      int modifiers = f.getModifiers();
      if (Modifier.isStatic(modifiers)) {
        try {
          Object o = f.get(this);
          // System.out.println(f.getName() + " "+o.getClass().getSimpleName());
          if (o.getClass().getSimpleName().equals("String")) {
            paramMap.put(f.getName(), (String) o);
          } else if (o.getClass().getSimpleName().equals("Boolean")) {
            paramMap.put(f.getName(), ((Boolean) o) ? "true" : "false");
          } else {
            paramMap.put(f.getName(), (String) o);
          }
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Map<String, String> getModelParamMap() {
    return paramMap;
  }

  public String getModelParameter(String key) {
    String res = paramMap.get(key);
    if (res == null) {
      System.out.println("Non ModelParameter " + key + "exists!");
      return "";
    }
    return res;
  }

  // it is used in the logger too
  // if you change here someone, please change it in the custom.xsl too
  public enum ModelParameter {

    MODEL_NAME, MODEL_TITLE, BASE_URI;

    static final Map<String, ModelParameter> modelParameterMap = new HashMap<String, ModelParameter>();

    static {
      for (ModelParameter mp : ModelParameter.values())
        modelParameterMap.put(mp.toString(), mp);
    }
  }

}
