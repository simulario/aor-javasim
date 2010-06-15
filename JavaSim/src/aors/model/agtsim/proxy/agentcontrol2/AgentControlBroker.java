package aors.model.agtsim.proxy.agentcontrol2;

import java.util.HashSet;
import java.util.Set;

public class AgentControlBroker implements AgentControlListener {

	private static AgentControlBroker instance;

	private Set<AgentControlListener> agentControlListeners;

	private AgentControlBroker() {
		this.agentControlListeners = new HashSet<AgentControlListener>();
	}

	public static AgentControlBroker getInstance() {
		if(instance == null) {
			instance = new AgentControlBroker();
		}
		return instance;
	}

	public void addAgentControlListener(AgentControlListener
		agentControlListener) {
		if(!this.agentControlListeners.contains(agentControlListener)) {
			this.agentControlListeners.add(agentControlListener);
		}
	}

	public void removeAgentControlListener(AgentControlListener
		agentControlListener) {
		this.agentControlListeners.remove(agentControlListener);
	}

	@Override
	public void agentControllerInitialized(CoreAgentController agentController) {
		for(AgentControlListener agentControlListener : agentControlListeners) {
			agentControlListener.agentControllerInitialized(agentController);
		}
	}
}