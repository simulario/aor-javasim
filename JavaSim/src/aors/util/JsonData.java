package aors.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * 
 * JsonData
 * 
 * @author Christian Noack
 * @since 07.04.2009
 * @version $Revision: 1.0 $
 */
public class JsonData {

  private String jsonString;
  private JSONObject objTree;
  private JSONArray objArray;

  public JsonData(String json) throws Exception {
    this.jsonString = json;
  }

  public void process() {
    // this.parser = new JSONParser();
    Object obj;
    obj = JSONValue.parse(this.jsonString);
    // obj = parser.parse(this.jsonString);
    if (obj instanceof JSONObject)
      this.objTree = (JSONObject) obj;
    if (obj instanceof JSONArray)
      this.objArray = (JSONArray) obj;
  }

  public synchronized Object get(String key) {
    if (this.objTree != null) {
      return this.objTree.get(key);
    } else {
      return null;
    }
  }

  public synchronized Object get(int key) {
    if (this.objArray != null) {
      return this.objArray.get(key);
    } else {
      return null;
    }
  }

  public String getJson() {
    return this.jsonString;
  }

}
