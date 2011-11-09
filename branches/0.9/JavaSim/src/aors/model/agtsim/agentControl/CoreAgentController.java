package aors.model.agtsim.agentControl;

import aors.model.agtsim.ReactionRule;
import aors.model.envevt.PerceptionEvent;
import aors.model.intevt.InternalEvent;
import java.util.List;
import java.util.Map;

/**
 * Interface for the agent controller. It's used by the agent subject (mvc model).
 * @author Thomas Grundmann
 */
public interface CoreAgentController {

	/**
	 * Upates the list of perception events.
	 * @param perceptionEvents
	 */
	public void setNewPerceptionEvents(List<PerceptionEvent> perceptionEvents);

	/**
	 * Replaces a perception event with its subjective representation.
	 * @param perceptionEvent
	 * @param actualPerceptionEvent
	 */
	public void replacePerceptionEventWithActualPerceptionEvent(
		PerceptionEvent perceptionEvent, InternalEvent actualPerceptionEvent);

	/**
	 * Updates the belief property values.
	 * @param beliefProperties
	 */
	public void setBeliefProperties(Map<String, Object> beliefProperties);

	/**
	 * Checks if a given reaction rule is suspended.
	 * @param reactionRule
	 * @return <code>true</code> if the rule is suspended
	 */
	public boolean ruleIsSuspended(ReactionRule reactionRule);

	/**
	 * Causes the controller to update the view using its representations for the
	 * belief properties and perception events.
	 */
	public void updateView();

		/**
	 * Performs the user's actions.
	 */
	public void performUserActions();
}