/**
 * 
 */
package aors.data.java.helper;

/**
 * @author Jens Werner
 * 
 */
public class AbstractSimState {

  private long simStep;

  private long agentsCount;

  private long objectsCount;

  private long eventsCount;

  /**
   * @return the simStep
   */
  public long getSimStep() {
    return simStep;
  }

  /**
   * @param simStep
   *          the simStep to set
   */
  public void setSimStep(long simStep) {
    this.simStep = simStep;
  }

  /**
   * @return the agentsCount
   */
  public long getAgentsCount() {
    return agentsCount;
  }

  /**
   * @param agentsCount
   *          the agentsCount to set
   */
  public void setAgentsCount(long agentsCount) {
    this.agentsCount = agentsCount;
  }

  /**
   * @return the objectsCount
   */
  public long getObjectsCount() {
    return objectsCount;
  }

  /**
   * @param objectsCount
   *          the objectsCount to set
   */
  public void setObjectsCount(long objectsCount) {
    this.objectsCount = objectsCount;
  }

  /**
   * @return the eventsCount
   */
  public long getEventsCount() {
    return eventsCount;
  }

  /**
   * @param eventsCount
   *          the objectsCount to set
   */
  public void setEventsCount(long eventsCount) {
    this.eventsCount = eventsCount;
  }

}
