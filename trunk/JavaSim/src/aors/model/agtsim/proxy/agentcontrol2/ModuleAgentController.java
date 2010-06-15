package aors.model.agtsim.proxy.agentcontrol2;

import aors.model.envevt.PerceptionEvent;
import java.util.List;
import java.util.Map;

public interface ModuleAgentController {
	
	public void setNewPerceptionEvents(List<PerceptionEvent> perceptionEvents);

	public void performUserActions();

	public void updateView(Map<String, Object> beliefProperties);

}
