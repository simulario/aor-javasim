package aors.module.visopengl.shape;

import aors.module.visopengl.space.view.MapType;

/**
 * This class is describing a mapping between an objects property and a shape
 * property.
 * 
 * @author Sebastian Mucha
 * @since March 11th, 2010
 * 
 */
public class ShapePropertyVisualizationMap {

  // String constant for a "ShapePropertyVisualizationMap" node
  public static final String SHAPE_PROPERTY_MAP = "ShapePropertyVisualizationMap";

  // String constants for attributes of a "ShapePropertyVisualizationMap" node
  public static final String SHAPE_PROPERTY = "shapeProperty";
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

  // Reference to the property class
  private Object propertyClass;

  // Shape property
  private String shapeProperty;

  // Property
  private String property;

  // Map type
  private MapType mapType;

  // Decision parameters
  private Object a0, a1, a2, a3;

  // Value parameters
  private String v0, v1, v2, v3, v4;

  public String getShapeProperty() {
    return shapeProperty;
  }

  public void setShapeProperty(String shapeProperty) {
    this.shapeProperty = shapeProperty;
  }

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
  }

  public MapType getMapType() {
    return mapType;
  }

  public void setMapType(MapType mapType) {
    this.mapType = mapType;
  }

  public Object getA0() {
    return a0;
  }

  public void setA0(Object a0) {
    this.a0 = a0;
  }

  public Object getA1() {
    return a1;
  }

  public void setA1(Object a1) {
    this.a1 = a1;
  }

  public Object getA2() {
    return a2;
  }

  public void setA2(Object a2) {
    this.a2 = a2;
  }

  public Object getA3() {
    return a3;
  }

  public void setA3(Object a3) {
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

  public Object getPropertyClass() {
    return propertyClass;
  }

  public void setPropertyClass(Object propertyClass) {
    this.propertyClass = propertyClass;
  }

}
