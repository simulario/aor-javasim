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
  public static final String A4 = "a4";
  public static final String A5 = "a5";
  public static final String A6 = "a6";
  public static final String A7 = "a7";
  public static final String V0 = "v0";
  public static final String V1 = "v1";
  public static final String V2 = "v2";
  public static final String V3 = "v3";
  public static final String V4 = "v4";
  public static final String V5 = "v5";
  public static final String V6 = "v6";
  public static final String V7 = "v7";
  public static final String V8 = "v8";

  // Property type
  private Object propertyType;

  // Property name
  private String propertyName;

  // Visual property name
  private String visualPropertyName;

  // Mapping type
  private MapType mapping;

  // Decision parameters
  private String a0, a1, a2, a3, a4, a5, a6, a7;

  // Value parameters
  private String v0, v1, v2, v3, v4, v5, v6, v7, v8;

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

      if (a0 != null && x < Double.valueOf(a0)) {
        return v0;
      }
      if (a0 != null && a1 != null && x >= Double.valueOf(a0) && x < Double.valueOf(a1)) {
        return v1;
      }
      if (a1 != null && a2 != null && x >= Double.valueOf(a1) && x < Double.valueOf(a2)) {
        return v2;
      }
      if (a2 != null && a3 != null && x >= Double.valueOf(a2) && x < Double.valueOf(a3)) {
        return v3;
      }
      if (a3 != null && a4 != null && x >= Double.valueOf(a3) && x < Double.valueOf(a4)) {
        return v4;
      }
      if (a4 != null && a5 != null && x >= Double.valueOf(a4) && x < Double.valueOf(a5)) {
        return v5;
      }
      if (a5 != null && a6 != null && x >= Double.valueOf(a5) && x < Double.valueOf(a6)) {
        return v6;
      }
      if (a6 != null && a7 != null && x >= Double.valueOf(a6) && x < Double.valueOf(a7)) {
        return v7;
      }
      
      return v8;
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
        String pValue = ((String) propertyValue);
        if (a0 != null && pValue.equals(a0)) {
          return v0;
        }
        if (a1 != null && pValue.equals(a1)) {
          return v1;
        }
        if (a2 != null && pValue.equals(a2)) {
          return v2;
        }
        if (a3 != null && pValue.equals(a3)) {
          return v3;
        }
        if (a4 != null && pValue.equals(a4)) {
          return v4;
        }
        if (a5 != null && pValue.equals(a5)) {
          return v5;
        }
        if (a6 != null && pValue.equals(a6)) {
          return v6;
        }
        if (a7 != null && pValue.equals(a7)) {
          return v7;
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

      if (a0 != null && x == Boolean.valueOf(a0)) {
        return v0;
      }
      if (a1 != null && x == Boolean.valueOf(a1)) {
        return v1;
      }
      if (a2 != null && x == Boolean.valueOf(a2)) {
        return v2;
      }
      if (a3 != null && x == Boolean.valueOf(a3)) {
        return v3;
      }
      if (a4 != null && x == Boolean.valueOf(a4)) {
        return v4;
      }
      if (a5 != null && x == Boolean.valueOf(a5)) {
        return v5;
      }
      if (a6 != null && x == Boolean.valueOf(a6)) {
        return v6;
      }
      if (a7 != null && x == Boolean.valueOf(a7)) {
        return v7;
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

      if (a0 != null && x == Double.valueOf(a0)) {
        return v0;
      }
      if (a1 != null && x == Double.valueOf(a1)) {
        return v1;
      }
      if (a2 != null && x == Double.valueOf(a2)) {
        return v2;
      }
      if (a3 != null && x == Double.valueOf(a3)) {
        return v3;
      }
      if (a4 != null && x == Double.valueOf(a4)) {
        return v4;
      }
      if (a5 != null && x == Double.valueOf(a5)) {
        return v5;
      }
      if (a6 != null && x == Double.valueOf(a6)) {
        return v6;
      }
      if (a7 != null && x == Double.valueOf(a7)) {
        return v7;
      }
    }

    return null;
  }

  /**
   * Performs an enumeration mapping.
   * 
   * @param propertyValue
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
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
      } else if (x.ordinal() == 5) {
        return v5;
      } else if (x.ordinal() == 6) {
        return v6;
      } else if (x.ordinal() == 7) {
        return v7;
      } else if (x.ordinal() == 8) {
        return v8;
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

      if (a4 != null) {
        result += Double.valueOf(a4) * Math.pow(x, 4);
        computed = true;
      }

      if (a5 != null) {
        result += Double.valueOf(a5) * Math.pow(x, 5);
        computed = true;
      }

      if (a6 != null) {
        result += Double.valueOf(a6) * Math.pow(x, 6);
        computed = true;
      }

      if (a7 != null) {
        result += Double.valueOf(a7) * Math.pow(x, 7);
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

  public String getA4() {
    return a4;
  }

  public void setA4(String a4) {
    this.a4 = a4;
  }

  public String getA5() {
    return a5;
  }

  public void setA5(String a5) {
    this.a5 = a5;
  }

  public String getA6() {
    return a6;
  }

  public void setA6(String a6) {
    this.a6 = a6;
  }

  public String getA7() {
    return a7;
  }

  public void setA7(String a7) {
    this.a7 = a7;
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

  public String getV5() {
    return v5;
  }

  public void setV5(String v5) {
    this.v5 = v5;
  }

  public String getV6() {
    return v6;
  }

  public void setV6(String v6) {
    this.v6 = v6;
  }

  public String getV7() {
    return v7;
  }

  public void setV7(String v7) {
    this.v7 = v7;
  }

  public String getV8() {
    return v8;
  }

  public void setV8(String v8) {
    this.v8 = v8;
  }
}
