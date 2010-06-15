package aors.model.agtsim.proxy.agentcontrol2;

import java.util.Map;
import java.util.Set;

public interface CoreAgentController {

	public void setModuleAgentController(ModuleAgentController moduleAgentController);

	public Long getAgentId();
	public String getAgentName();
	public String getAgentType();

	public Set<Pair<String, String>> getKeyEvents();
	public Map<String, Set<Pair<String, String>>> getMouseEvents();

	public void setAgentIsControlled(boolean agentIsControlled);

	public void processInternalEvent(String eventName, Map<String, String> eventData);

	public Set<String> getUILanguages();

	public String getDefaultUILanguage();
}