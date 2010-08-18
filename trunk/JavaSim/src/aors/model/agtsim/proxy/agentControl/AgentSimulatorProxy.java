package aors.model.agtsim.proxy.agentControl;

import java.util.Map;

import aors.util.JsonData;

/**
 * An Interface to the AgentSimulator that reveals some internal information
 * about the simulated AgentSubject and is used to send ActionEvents back to the
 * AgentSimulator inside the simulation.
 * 
 * @author Christian Noack
 * @since 21/Aug/2009
 */
public interface AgentSimulatorProxy extends java.io.Serializable {

  /**
   * Returns the id of the simulated AgentSubject
   * 
   * @return the id of the simulated AgentSubject
   */
  public long getAgentId();

  /**
   * Returns the name of the simulated AgentSubject
   * 
   * @return name of AgentSubject
   */
  public String getAgentName();

  /**
   * Returns the type of the simulated AgentSubject
   * 
   * @return type of AgentSubject
   */
  public String getAgentType();

  /**
   * 
   * 
   * @return
   */
  public long getAgentTimeout();

  /**
   * Returns a map of custom properties (SelfBeliefs) of the simulated
   * AgentSubject. The map is being used to initialize external agents. This
   * method is called from the AgentSimulatorProxy.
   * 
   * @return a map of custom AgentSubject properties
   */
  public Map<String, Map<String, String>> getSubjectProperties();

  /**
   * Sends actionEvents back to the agentSimulator. Events are encoded in JSON.
   * 
   * @param jsonResponse
   *          Events encoded as JSON
   * @param responseSimulationStep
   *          the step to which the responses belong
   * @author noack
   * @since 24/Aug/2009
   */
  public void notifyActionResponse(JsonData jsonResponse,
      long responseSimulationStep);

}
