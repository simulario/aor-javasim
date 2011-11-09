package aors.model.agtsim.agentControl;

import java.util.HashSet;
import java.util.Set;

/**
 * A broker to notify {@link AgentControlListener}s about new initialized core
 * agent controllers. The broker is implemented as a singleton. That ensures
 * that all {@link AgentControlListener}s and {@link CoreAgentController}s use
 * the same broker.
 * @author Thomas Grundmann
 */
public class AgentControlBroker implements AgentControlListener {

	/**
	 * The instance of the broker.
	 */
	private static AgentControlBroker instance;

	/**
	 * The set of agent control listeners that listen to the broker.
	 */
	private Set<AgentControlListener> agentControlListeners;

	/**
	 * Initializes the broker. It just called once when the broker's instance is
	 * called the first time.
	 */
	private AgentControlBroker() {
		this.agentControlListeners = new HashSet<AgentControlListener>();
	}

	/**
	 * Returns the broker's instance. If the broker is not initialized, it will be
	 * initialized.
	 * @return the instance
	 */
	public static AgentControlBroker getInstance() {
		if(instance == null) {
			instance = new AgentControlBroker();
		}
		return instance;
	}

	/**
	 * Adds an [{@link AgentControlListener}.
	 * @param agentControlListener
	 */
	public void addAgentControlListener(AgentControlListener
		agentControlListener) {
		if(!this.agentControlListeners.contains(agentControlListener)) {
			this.agentControlListeners.add(agentControlListener);
		}
	}

	/**
	 * Removes an {@link AgentControlListener}.
	 * @param agentControlListener
	 */
	public void removeAgentControlListener(AgentControlListener
		agentControlListener) {
		this.agentControlListeners.remove(agentControlListener);
	}

	/**
	 * Notifies the registered {@link AgentControlListener}s about the
	 * instantiation of a new {@link AgentControlInitializer}.
	 * @param agentControlInitializer
	 */
	@Override
	public void registerAgentControlInitializer(AgentControlInitializer
		agentControlInitializer) {
		for(AgentControlListener agentControlListener : this.agentControlListeners) {
			agentControlListener.registerAgentControlInitializer(agentControlInitializer);
		}
	}

	/**
	 * Notifies the registered {@link AgentControlListener}s about the
	 * removal of a {@link AgentControlInitializer}.
	 * @param agentControlInitializer
	 */
	@Override
	public void unregisterAgentControlInitializer(AgentControlInitializer
		agentControlInitializer) {
		for(AgentControlListener agentControlListener : this.agentControlListeners) {
			agentControlListener.unregisterAgentControlInitializer(agentControlInitializer);
		}
	}
}