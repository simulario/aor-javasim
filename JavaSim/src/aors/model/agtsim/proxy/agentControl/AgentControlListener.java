package aors.model.agtsim.proxy.agentControl;

/**
 * A listener for initializations of new {@link CoreAgentController}s.
 * @author Thomas Grundmann
 */
public interface AgentControlListener {

	/**
	 * Registers an agent control initializer.
	 * @param agentControlInitializer
	 */
	public void registerAgentControInitializer(AgentControlInitializer
		agentControlInitializer);

	/**
	 * Unregisters an agent control initializer.
	 * @param agentControlInitializer
	 */
	public void unregisterAgentControlInitializer(AgentControlInitializer
		agentControlInitializer);
}