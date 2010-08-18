package aors.model.agtsim.agentControl;

import aors.model.intevt.InternalEvent;
import java.util.Map;

public interface InteractionEventFactory {

	public InternalEvent createEvent(String eventType, Map<String, String> eventData);
}
