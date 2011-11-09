package aors.model.agtsim.agentControl;

/**
 * A listener for initializations of new {@link CoreAgentController}s.
 * @author Thomas Grundmann
 */
public interface AgentControlListener {

	/**
	 * Registers an agent control initializer.
	 * @param agentControlInitializer
	 */
	public void registerAgentControlInitializer(AgentControlInitializer
		agentControlInitializer);

	/**
	 * Unregisters an agent control initializer.
	 * @param agentControlInitializer
	 */
	public void unregisterAgentControlInitializer(AgentControlInitializer
		agentControlInitializer);
}