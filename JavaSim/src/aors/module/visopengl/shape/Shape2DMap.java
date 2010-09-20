package aors.module.visopengl.shape;

import java.lang.reflect.Field;
import java.util.HashMap;

import aors.logger.model.AgtType;
import aors.logger.model.ObjType;
import aors.logger.model.ObjectType;
import aors.logger.model.SlotType;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;

public class Shape2DMap {

  // String constants describing nodes of a shape map
  public static final String PHYSICAL_SHAPE2D_MAP = "PhysicalShape2dMap";
  public static final String SHAPE2D_MAP = "Shape2dVisualizationMap";
  public static final String CASE = "Case";

  // String constants describing attributes of a shape map
  public static final String PROPERTY = "property";
  public static final String VALUE = "value";

  // Property
  private String propertyName;
  private Object propertyType;

  // Map of property values and associated shapes
  private HashMap<String, Shape2D> map = new HashMap<String, Shape2D>();
  
  /**
   * Determines the value of the property through reflection and returns the
   * associated shape. This method only deals with non-physical attributes.
   * 
   * @param obj
   */
  public void determineShape(Objekt obj, View view) {
    if (propertyName != null) {
      try {
        // Get the property
        Field field = obj.getClass().getDeclaredField(propertyName);

        // Make it accessible
        field.setAccessible(true);

        // Get the property's type
        propertyType = field.get(obj);

        if (propertyType instanceof Long || propertyType instanceof Double) {
          // Get the property's value
          String propertyValue = String.valueOf(Double.valueOf(String.valueOf(field.get(obj))));
          
          if (map.get(propertyValue) != null) {
            view.setShape2D(map.get(propertyValue));
          }
        }

        if (propertyType instanceof Boolean) {
          // Get the property's value
          String propertyValue = field.get(obj).toString();
          
          if (map.get(propertyValue) != null) {
            view.setShape2D(map.get(propertyValue));
          }
        }
        
        if (propertyType instanceof Enum<?>) {
          // Get the property's value
          String propertyValue = field.get(obj).toString();
          
          if (map.get(propertyType.getClass().getSimpleName() + "." + propertyValue) != null) {
            view.setShape2D(map.get(propertyType.getClass().getSimpleName() + "." + propertyValue));
          }
        }
        
        if (propertyType instanceof String) {
          // Get the property's value
          String propertyValue = field.get(obj).toString();
          
          if (map.get(propertyValue) != null) {
            view.setShape2D(map.get(propertyValue));
          }
        }
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        // Do nothing if no such property was found
        return;
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Determines the value of the property from changes of a simulation step and
   * returns the associated shape. This method only deals with non-physical
   * attributes.
   * 
   * @param objType
   */
  public void determinePropertyValue(ObjectType objType, View view) {
    if (propertyName != null) {
      for (SlotType slot : objType.getSlot()) {
        if (slot.getProperty().equals(propertyName)) {
          if (propertyType instanceof Long || propertyType instanceof Double) {
            // Get the property's value
            String propertyValue = String.valueOf(Double.valueOf(slot.getValue()));
            
            if (map.get(propertyValue) != null) {
              view.setShape2D(map.get(propertyValue));
            }
          }

          if (propertyType instanceof Boolean) {
            if (map.get(slot.getValue()) != null) {
              view.setShape2D(map.get(slot.getValue()));
            }
          }
          
          if (propertyType instanceof Enum<?>) {
            if (map.get(propertyType.getClass().getSimpleName() + "." +slot.getValue()) != null) {
              view.setShape2D(map.get(propertyType.getClass().getSimpleName() + "." +slot.getValue()));
            }
          }
          
          if (propertyType instanceof String) {
            if (map.get(slot.getValue()) != null) {
              view.setShape2D(map.get(slot.getValue()));
            }
          }
        }
      }
    }
  }

  /**
   * Determines the value of the property from changes of a simulation step and
   * returns the associated shape. This method only deals with non-physical
   * attributes.
   * 
   * @param objType
   */
  public void determinePropertyValue(ObjType objType, View view) {
    if (propertyName != null) {
      for (SlotType slot : objType.getSlot()) {
        if (slot.getProperty().equals(propertyName)) {
          if (propertyType instanceof Long || propertyType instanceof Double) {
            // Get the property's value
            String propertyValue = String.valueOf(Double.valueOf(slot.getValue()));
            
            if (map.get(propertyValue) != null) {
              view.setShape2D(map.get(propertyValue));
            }
          }

          if (propertyType instanceof Boolean) {
            if (map.get(slot.getValue()) != null) {
              view.setShape2D(map.get(slot.getValue()));
            }
          }
          
          if (propertyType instanceof Enum<?>) {
            if (map.get(propertyType.getClass().getSimpleName() + "." +slot.getValue()) != null) {
              view.setShape2D(map.get(propertyType.getClass().getSimpleName() + "." +slot.getValue()));
            }
          }
          
          if (propertyType instanceof String) {
            if (map.get(slot.getValue()) != null) {
              view.setShape2D(map.get(slot.getValue()));
            }
          }
        }
      }
    }
  }

  /**
   * Determines the value of the property from changes of a simulation step and
   * returns the associated shape. This method only deals with non-physical
   * attributes.
   * 
   * @param agtType
   */
  public void determinePropertyValue(AgtType agtType, View view) {
    if (propertyName != null) {
      for (SlotType slot : agtType.getSlot()) {
        if (slot.getProperty().equals(propertyName)) {
          if (propertyType instanceof Long || propertyType instanceof Double) {
            // Get the property's value
            String propertyValue = String.valueOf(Double.valueOf(slot.getValue()));
            
            if (map.get(propertyValue) != null) {
              view.setShape2D(map.get(propertyValue));
            }
          }

          if (propertyType instanceof Boolean) {
            if (map.get(slot.getValue()) != null) {
              view.setShape2D(map.get(slot.getValue()));
            }
          }
          
          if (propertyType instanceof Enum<?>) {
            if (map.get(propertyType.getClass().getSimpleName() + "." +slot.getValue()) != null) {
              view.setShape2D(map.get(propertyType.getClass().getSimpleName() + "." +slot.getValue()));
            }
          }
          
          if (propertyType instanceof String) {
            if (map.get(slot.getValue()) != null) {
              view.setShape2D(map.get(slot.getValue()));
            }
          }
        }
      }
    }
  }

  /**
   * Determines the value of the property from the members of physical object
   * and returns the associated shape. This method should be used if a physical
   * property is used to select a shape.
   * 
   * @param phy
   */
  public void determineShape(Physical phy, View view) {
    if (propertyName != null) {
      String propertyValue = null;

      if (propertyName.equals("x"))
        propertyValue = String.valueOf(phy.getX());

      else if (propertyName.equals("y"))
        propertyValue = String.valueOf((phy.getY()));

      else if (propertyName.equals("z"))
        propertyValue = String.valueOf((phy.getZ()));

      else if (propertyName.equals("rotationAngleX"))
        propertyValue = String.valueOf((phy.getRotX()));

      else if (propertyName.equals("rotationAngleY"))
        propertyValue = String.valueOf((phy.getRotY()));

      else if (propertyName.equals("rotationAngleZ"))
        propertyValue = String.valueOf((phy.getRotZ()));

      else if (propertyName.equals("vx"))
        propertyValue = String.valueOf((phy.getVx()));

      else if (propertyName.equals("vy"))
        propertyValue = String.valueOf((phy.getVy()));

      else if (propertyName.equals("vz"))
        propertyValue = String.valueOf((phy.getVz()));

      else if (propertyName.equals("ax"))
        propertyValue = String.valueOf((phy.getAx()));

      else if (propertyName.equals("ay"))
        propertyValue = String.valueOf((phy.getAy()));

      else if (propertyName.equals("az"))
        propertyValue = String.valueOf((phy.getAz()));

      else if (propertyName.equals("omegaX"))
        propertyValue = String.valueOf((phy.getOmegaX()));

      else if (propertyName.equals("omegaY"))
        propertyValue = String.valueOf((phy.getOmegaY()));

      else if (propertyName.equals("omegaZ"))
        propertyValue = String.valueOf((phy.getOmegaZ()));

      else if (propertyName.equals("alphaX"))
        propertyValue = String.valueOf((phy.getAlphaX()));

      else if (propertyName.equals("alphaY"))
        propertyValue = String.valueOf((phy.getAlphaY()));

      else if (propertyName.equals("alphaZ"))
        propertyValue = String.valueOf((phy.getAlphaZ()));

      else if (propertyName.equals("m"))
        propertyValue = String.valueOf((phy.getM()));

      else if (propertyName.equals("width"))
        propertyValue = String.valueOf((phy.getWidth()));

      else if (propertyName.equals("height"))
        propertyValue = String.valueOf((phy.getHeight()));

      else if (propertyName.equals("depth"))
        propertyValue = String.valueOf((phy.getDepth()));

      // Return the associated shape
      if (propertyValue != null) {
        if (map.get(Double.valueOf(propertyValue)) != null) {
          view.setShape2D(map.get(Double.valueOf(propertyValue)));
        }
      }
    }
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public HashMap<String, Shape2D> getMap() {
    return map;
  }

  public void setMap(HashMap<String, Shape2D> map) {
    this.map = map;
  }
}
