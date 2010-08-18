package aors.model.agtsim.proxy.agentControl;

import java.util.List;
import java.util.Map;

import aors.model.envevt.ActionEvent;

/**
 * An Interface to the AgentSimulator that reveals some internal information
 * about the simulated AgentSubject and is used to send ActionEvents back to the
 * AgentSimulator inside the simulation.
 * 
 * @author Christian Noack
 * @since 21/Aug/2009
 */
public interface AgentSimulatorFacade extends java.io.Serializable {

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
   * Sends actionEvents back to the agentSimulator. Method is called from
   * AgentProxy.
   * 
   * @param responseSimulationStep
   *          the step to which the responses belong
   * @param actionEvents
   *          the list of generated ActionEvents
   * @author noack
   * @since 21/Aug/2009
   */
  public void notifyActionEvents(long responseSimulationStep,
      List<ActionEvent> actionEvents);

}
