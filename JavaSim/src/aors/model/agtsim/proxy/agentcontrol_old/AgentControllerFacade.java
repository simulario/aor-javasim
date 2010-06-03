package aors.model.agtsim.proxy.agentcontrol_old;

/**
 * AgentSubjectProxy acts as a proxy between a controlled AgentSimulator and the
 * "Controller". If registered within an AgentSimulator, it receives all
 * perceptions instead of the default AgentSubject. This way, an external
 * AgentSimulator (e.g. JavaScript running on WebSim) is able to control a
 * certain AgentSimulator.
 * 
 * @author noack
 * @since 1/Jun/2009
 * 
 */
public interface AgentControllerFacade extends java.io.Serializable {

  /**
   * Asks the Proxy for its state. 0 - idle, peer is not initialized 1 -
   * connected 1st step 2 - handshake done, no perceptions processed yet 3 -
   * active mode - events going back and forth 4 - finished. shutdown state
   * after simulation has ended
   * 
   * @return state the state the AgentControllerBean Proxy is in
   * @since 26/Jun/2009
   */
  public int getState();

  /**
   * Returns the username of the user who controls this agent
   * 
   * @return username
   */
  public String getUserName();

  /**
   * Sends perceptions to the Proxy. This method is called from the AgentProxy.
   * 
   * @param jsonString
   *          encoded perceptions
   * @param currentSimulationStep
   *          the current simulation step
   * @param agentTimeout
   * @author noack
   * @since 1/Jun/2009
   */
  public void notifyPerceptions(String jsonString, long currentSimulationStep);

  /**
   * Notifies the Proxy that the simulation has ended.
   * 
   * @author noack
   * @since 8/Jun/2009
   */
  public void notifySimulationEnd();

  /**
   * Notifies the Proxy that it was removed from the simulation.
   */
  public void notifyRemoval();

}
