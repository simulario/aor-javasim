package aors;

import java.util.HashMap;
import java.util.List;

import aors.model.envsim.Objekt;

/**
 * 
 * InheritedPropertyAccess
 * 
 * This interface defines the methods to access properties in inherited classes
 * of PhysicalObject. We choose this approach in order to avoid the use of Java
 * Reflections. Implementing classes should use HashMaps to achieve this goal.
 * 
 * @author Marco Pehla
 * @since 07.08.2008
 * @version $Revision$
 */
public class InheritedProperty {

  public enum PropertyType {
    String, Long, Double, Boolean, Objekt
  };

  // these HashMaps holds all properties of inherited classes;
  // we use this approach to AVOID Java Reflections, which are
  // known to be very slow in comparison:
  // http://www.ibm.com/developerworks/library/j-dyn0603/
  private HashMap<String, String> stringProperties;
  private HashMap<String, Boolean> booleanProperties;
  private HashMap<String, Double> doubleProperties;
  private HashMap<String, Long> longProperties;
  private HashMap<String, Objekt> objektProperties;
  private HashMap<String, List<?>> listProperties;

  public InheritedProperty() {
    this.stringProperties = new HashMap<String, String>();
    this.booleanProperties = new HashMap<String, Boolean>();
    this.doubleProperties = new HashMap<String, Double>();
    this.longProperties = new HashMap<String, Long>();
    this.objektProperties = new HashMap<String, Objekt>();
    this.listProperties = new HashMap<String, List<?>>();
  }

  public double getDoubleProperty(String propertyName) {
    return this.doubleProperties.get(propertyName);
  }

  public long getLongProperty(String propertyName) {
    return this.longProperties.get(propertyName);
  }

  public String getStringProperty(String propertyName) {
    return this.stringProperties.get(propertyName);
  }

  public boolean isBooleanProperty(String propertyName) {
    return this.booleanProperties.get(propertyName);
  }

  public Objekt getObjektProperty(String propertyName) {
    return this.objektProperties.get(propertyName);
  }

  public void setBooleanProperty(String propertyName, boolean propertyValue) {
    this.booleanProperties.put(propertyName, propertyValue);
  }

  public void setDoubleProperty(String propertyName, double propertyValue) {
    this.doubleProperties.put(propertyName, propertyValue);
  }

  public void setLongProperty(String propertyName, long propertyValue) {
    this.longProperties.put(propertyName, propertyValue);
  }

  public void setStringProperty(String propertyName, String propertyValue) {
    this.stringProperties.put(propertyName, propertyValue);
  }

  public void setObjektProperty(String propertyName, Objekt objekt) {
    this.objektProperties.put(propertyName, objekt);
  }

  public void setListProperty(String propertyName, List<?> list) {
    this.listProperties.put(propertyName, list);
  }

  public HashMap<String, Boolean> getBooleanProperties() {
    return this.booleanProperties;
  }

  public HashMap<String, Double> getDoubleProperties() {
    return this.doubleProperties;
  }

  public HashMap<String, Long> getLongProperties() {
    return this.longProperties;
  }

  public HashMap<String, String> getStringProperties() {
    return this.stringProperties;
  }

  public HashMap<String, Objekt> getObjektProperties() {
    return this.objektProperties;
  }

}
