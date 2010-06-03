package aors.model.agtsim.proxy.agentcontrol_old;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aors.model.agtsim.json.JsonGenerator;
import aors.model.agtsim.json.JsonProcessor;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envsim.AgentObject;
import aors.util.JsonData;

/**
 * This class relays data between a AgentSimulator within the simulation core
 * and the AgentController which implements the AgentSimulatorProxy interface.
 * It translates PerceptionEvents into a JSON format and retranslates
 * JSON-formatted ActionEvents from an external JS client into real ActionEvent
 * instances.
 * 
 * @author Christian Noack
 * @since 21/Aug/2009
 */
public class AgentProxy implements AgentSimulatorProxy, AgentSubjectProxy,
    java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1548956L;

  private AgentSimulatorFacade agentSimulatorFacade;

  private AgentControllerFacade agentControllerFacade;

  private AgentObject agentObject;

  private JsonProcessor jsonProcessor;

  public AgentProxy(AgentControllerFacade agentControllerFacade) {
    this.agentControllerFacade = agentControllerFacade;
    this.agentObject = null;
    this.agentSimulatorFacade = null;
    this.jsonProcessor = new JsonProcessor();

  }

  @Override
  public void setAgentObject(AgentObject agentObject) {
    this.agentObject = agentObject;
  }

  @Override
  public void setSimClassLoader(ClassLoader classLoader) {
    this.jsonProcessor.setClassLoader(classLoader);
  }

  // Methods of AgentSimulatorProxy

  /**
   * Sets the AgentSimulator as Listener that this proxy sends resulting actions
   * to.
   * 
   * @param agentSimulator
   *          AgentSimulator which this Proxy is connected to
   * @author noack
   * @since 21/Aug/2009
   */
  public void setAgentSimulatorFacade(AgentSimulatorFacade agentSimulatorFacade) {
    this.agentSimulatorFacade = agentSimulatorFacade;
  }

  public boolean isAgentSimulatorFacadeSet() {
    return (this.agentSimulatorFacade != null);
  }

  @Override
  public long getAgentId() {
    if (agentSimulatorFacade != null)
      return agentSimulatorFacade.getAgentId();
    return 0;
  }

  @Override
  public String getAgentName() {
    if (agentSimulatorFacade != null)
      return agentSimulatorFacade.getAgentName();
    return "";
  }

  @Override
  public String getAgentType() {
    if (agentSimulatorFacade != null)
      return agentSimulatorFacade.getAgentType();
    return "";
  }

  @Override
  public long getAgentTimeout() {
    if (agentSimulatorFacade != null)
      return agentSimulatorFacade.getAgentTimeout();
    return 0;
  }

  @Override
  public Map<String, Map<String, String>> getSubjectProperties() {
    if (agentSimulatorFacade != null)
      return agentSimulatorFacade.getSubjectProperties();
    return new HashMap<String, Map<String, String>>();
  }

  @Override
  public void notifyActionResponse(JsonData jsonResponse,
      long responseSimulationStep) {
    if (agentSimulatorFacade != null) {
      List<ActionEvent> resultingActions = jsonProcessor.process(
          this.agentObject, jsonResponse);
      this.agentSimulatorFacade.notifyActionEvents(responseSimulationStep,
          resultingActions);
    }
  }

  public void prePassivate() {

  }

  // Methods of AgentSubjectProxy

  @Override
  public int getState() {
    try {
      return this.agentControllerFacade.getState();
    } catch (Exception e) {
      return -1;
    }
  }

  @Override
  public String getUserName() {
    try {
      return this.agentControllerFacade.getUserName();
    } catch (Exception e) {
      return "";
    }
  }

  @Override
  public void notifyPerceptions(List<PerceptionEvent> perceptionEvents,
      long currentSimulationStep, long agentTimeout) {
    try {

      JsonGenerator json = new JsonGenerator(currentSimulationStep);
      json.notifyAgentObject(this.agentObject);
      json.notifyPerceptions(perceptionEvents);

      String jsonString = json.getJsonString(agentTimeout);

      this.agentControllerFacade.notifyPerceptions(jsonString,
          currentSimulationStep);
    } catch (Exception e) {
    }
  }

  @Override
  public void notifyRemoval() {
    try {
      this.agentControllerFacade.notifyRemoval();
    } catch (Exception e) {
    }
  }

  @Override
  public void notifySimulationEnd() {
    try {
      this.agentControllerFacade.notifySimulationEnd();
    } catch (Exception e) {
    }
  }

}
