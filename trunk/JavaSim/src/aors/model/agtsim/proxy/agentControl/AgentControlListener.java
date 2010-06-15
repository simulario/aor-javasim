package aors.model.agtsim.proxy.agentControl;

/**
 * A listener for initializations of new {@link CoreAgentController}s.
 * @author Thomas Grundmann
 */
public interface AgentControlListener {

	/**
	 * Notifies the listener about a new initialized {@link CoreAgentController}.
	 * @param agentController
	 */
	public void agentControllerInitialized(CoreAgentController agentController);
}