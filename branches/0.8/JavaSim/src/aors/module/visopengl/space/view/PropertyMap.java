package aors.module.visopengl.space.view;

/**
 * Mapping between a visual property of an object/grid and some other property.
 * 
 * @author Sebastian Mucha
 * @since March 17th, 2010
 * 
 */
public class PropertyMap {

  // Property map nodes
  public static final String GRID_PROPERTY_MAP = "GridCellPropertyVisualizationMap";

  // Attributes of property map nodes
  public static final String SHAPE_PROPERTY = "shapeProperty";
  public static final String CELL_PROPERTY = "cellViewProperty";
  public static final String PROPERTY = "property";
  public static final String MAP_TYPE = "mapType";
  public static final String A0 = "a0";
  public static final String A1 = "a1";
  public static final String A2 = "a2";
  public static final String A3 = "a3";
  public static final String V0 = "v0";
  public static final String V1 = "v1";
  public static final String V2 = "v2";
  public static final String V3 = "v3";
  public static final String V4 = "v4";

  // Property type
  private Object propertyType;

  // Property name
  private String propertyName;

  // Visual property name
  private String visualPropertyName;

  // Mapping type
  private MapType mapping;

  // Decision parameters
  private String a0, a1, a2, a3;

  // Value parameters
  private String v0, v1, v2, v3, v4;

  /**
   * Performs the correct mapping.
   * 
   * @param propertyValue
   */
  public String performMapping(Object propertyValue) {
    if (propertyType == null) {
      propertyType = propertyValue;
    }

    if (mapping.equals(MapType.caseWise)) {
      return caseWise(propertyValue);
    } else if (mapping.equals(MapType.equalityCaseWise)) {
      return equalityCaseWise(propertyValue);
    } else if (mapping.equals(MapType.enumerationMap)) {
      return enumerationMap(propertyValue);
    } else if (mapping.equals(MapType.polynomial)) {
      return polynomial(propertyValue);
    } else {
      return null;
    }
  }

  /**
   * Performs a case wise mapping.
   * 
   * @param propertyValue
   */
  private String caseWise(Object propertyValue) {
    if (propertyType instanceof Long || propertyType instanceof Double) {
      double x = 0;

      if (propertyValue instanceof String) {
        x = Double.valueOf((String) propertyValue);
      } else if (propertyValue instanceof Long) {
        x = (Long) propertyValue;
      } else if (propertyValue instanceof Double) {
        x = (Double) propertyValue;
      } else {
        return null;
      }

      if (a0 != null) {
        if (x < Double.valueOf(a0)) {
          return v0;
        } else if (a1 != null) {
          if (x >= Double.valueOf(a0) && x < Double.valueOf(a1)) {
            return v1;
          } else if (a2 != null) {
            if (x >= Double.valueOf(a1) && x < Double.valueOf(a2)) {
              return v2;
            } else if (a3 != null) {
              if (x >= Double.valueOf(a2) && x < Double.valueOf(a3)) {
                return v3;
              } else {
                return v4;
              }
            } else {
              return v3;
            }
          } else {
            return v2;
          }
        } else {
          return v1;
        }
      }
    }

    return null;
  }

  /**
   * Performs a equality case wise mapping.
   * 
   * @param propertyValue
   */
  private String equalityCaseWise(Object propertyValue) {
    if (propertyType instanceof String) {
      if (propertyValue instanceof String) {
        if (a0 != null) {
          if (((String) propertyValue).equals(a0)) {
            return v0;
          } else if (a1 != null) {
            if (((String) propertyValue).equals(a1)) {
              return v1;
            } else if (a2 != null) {
              if (((String) propertyValue).equals(a2)) {
                return v2;
              } else if (a3 != null) {
                if (((String) propertyValue).equals(a3)) {
                  return v3;
                }
              }
            }
          }
        }
      }
    }

    else if (propertyType instanceof Boolean) {
      boolean x = false;

      if (propertyValue instanceof String) {
        x = Boolean.valueOf((String) propertyValue);
      } else if (propertyValue instanceof Boolean) {
        x = (Boolean) propertyValue;
      } else {
        return null;
      }

      if (a0 != null) {
        if (x == Boolean.valueOf(a0)) {
          return v0;
        } else if (a1 != null) {
          if (x == Boolean.valueOf(a1)) {
            return v1;
          }
        }
      }
    }

    else if (propertyType instanceof Long || propertyType instanceof Double) {
      double x = 0;

      if (propertyValue instanceof String) {
        x = Double.valueOf((String) propertyValue);
      } else if (propertyValue instanceof Long) {
        x = (Long) propertyValue;
      } else if (propertyValue instanceof Double) {
        x = (Double) propertyValue;
      } else {
        return null;
      }

      if (a0 != null) {
        if (x == Double.valueOf(a0)) {
          return v0;
        } else if (a1 != null) {
          if (x == Double.valueOf(a1)) {
            return v1;
          } else if (a2 != null) {
            if (x == Double.valueOf(a2)) {
              return v2;
            } else if (a3 != null) {
              if (x == Double.valueOf(a3)) {
                return v3;
              }
            }
          }
        }
      }
    }

    return null;
  }

  /**
   * Performs an enumeration mapping.
   * 
   * @param propertyValue
   */
  @SuppressWarnings("unchecked")
  private String enumerationMap(Object propertyValue) {
    if (propertyType instanceof Enum) {
      Enum x = null;

      if (propertyValue instanceof String) {
        int index = ((String) propertyValue).lastIndexOf(".");
        String value = ((String) propertyValue).substring(index + 1);
        x = Enum.valueOf((Class) propertyType.getClass(), value);
      }

      else if (propertyValue instanceof Enum) {
        x = (Enum) propertyValue;
      }

      else {
        return null;
      }

      if (x.ordinal() == 0) {
        return v0;
      } else if (x.ordinal() == 1) {
        return v1;
      } else if (x.ordinal() == 2) {
        return v2;
      } else if (x.ordinal() == 3) {
        return v3;
      } else if (x.ordinal() == 4) {
        return v4;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Performs a polynomial mapping.
   * 
   * @param propertyValue
   */
  private String polynomial(Object propertyValue) {

    if (propertyType instanceof Long || propertyType instanceof Double) {
      double x = 0;

      if (propertyValue instanceof String) {
        x = Double.valueOf((String) propertyValue);
      } else if (propertyValue instanceof Long) {
        x = (Long) propertyValue;
      } else if (propertyValue instanceof Double) {
        x = (Double) propertyValue;
      } else {
        return null;
      }

      double result = 0;
      boolean computed = false;

      if (a0 != null) {
        result += Double.valueOf(a0);
        computed = true;
      }

      if (a1 != null) {
        result += Double.valueOf(a1) * x;
        computed = true;
      }

      if (a2 != null) {
        result += Double.valueOf(a2) * Math.pow(x, 2);
        computed = true;
      }

      if (a3 != null) {
        result += Double.valueOf(a3) * Math.pow(x, 3);
        computed = true;
      }

      if (computed) {
        return Double.toString(result);
      }
    }

    return null;
  }

  public Object getPropertyType() {
    return propertyType;
  }

  public void setPropertyType(Object propertyType) {
    this.propertyType = propertyType;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getVisualPropertyName() {
    return visualPropertyName;
  }

  public void setVisualPropertyName(String visualPropertyName) {
    this.visualPropertyName = visualPropertyName;
  }

  public MapType getMapping() {
    return mapping;
  }

  public void setMapping(MapType mapping) {
    this.mapping = mapping;
  }

  public String getA0() {
    return a0;
  }

  public void setA0(String a0) {
    this.a0 = a0;
  }

  public String getA1() {
    return a1;
  }

  public void setA1(String a1) {
    this.a1 = a1;
  }

  public String getA2() {
    return a2;
  }

  public void setA2(String a2) {
    this.a2 = a2;
  }

  public String getA3() {
    return a3;
  }

  public void setA3(String a3) {
    this.a3 = a3;
  }

  public String getV0() {
    return v0;
  }

  public void setV0(String v0) {
    this.v0 = v0;
  }

  public String getV1() {
    return v1;
  }

  public void setV1(String v1) {
    this.v1 = v1;
  }

  public String getV2() {
    return v2;
  }

  public void setV2(String v2) {
    this.v2 = v2;
  }

  public String getV3() {
    return v3;
  }

  public void setV3(String v3) {
    this.v3 = v3;
  }

  public String getV4() {
    return v4;
  }

  public void setV4(String v4) {
    this.v4 = v4;
  }
}
