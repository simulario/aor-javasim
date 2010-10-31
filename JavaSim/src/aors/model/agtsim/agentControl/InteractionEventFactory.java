package aors.model.agtsim.agentControl;

import aors.model.intevt.InternalEvent;
import java.util.Map;

/**
 * Interface for the factories that create interaction events based on user
 * inputs.
 * @author Thomas Grundmann
 */
public interface InteractionEventFactory {

	/**
	 * Creates an internal event for a given even type and set of data.
	 * @param eventType
	 * @param eventData
	 * @return The internal event representing the user input.
	 */
	public InternalEvent createEvent(String eventType, Map<String, String> eventData);
}
