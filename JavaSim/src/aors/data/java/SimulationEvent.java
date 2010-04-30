package aors.data.java;

import java.util.EventObject;

/**
 * 
 * Event for the management of simulations (and not for simulation itself); e.g.
 * SimStart or SimEnd or SimSteps ...
 * 
 * @author Jens Werner
 * 
 */
public class SimulationEvent extends EventObject {

  /**
   * 
   */
  private static final long serialVersionUID = -3528190005679590754L;

  private String propertyName;

  private Object value;

  public SimulationEvent(Object source, String propertyName, Object value) {
    super(source);
    this.propertyName = propertyName;
    this.value = value;
  }

  /**
   * @return the propertyName
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }

}
