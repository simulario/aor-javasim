package aors.module.agentControl.controller;

import aors.model.Event;
import aors.model.agtsim.AgentSubject.AgentSubjectFacade;
import aors.model.agtsim.ReactionRule;
import aors.model.agtsim.agentControl.AgentControlBroker;
import aors.model.agtsim.agentControl.AgentControlInitializer;
import aors.model.agtsim.agentControl.InteractionEventFactory;
import aors.model.envevt.PerceptionEvent;
import aors.model.intevt.InternalEvent;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.views.ControlView;
import aors.util.Pair;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates a simple agent controller.
 * @author Thomas Grundmann
 */
public class SimpleAgentControllerFactory implements AgentControllerFactory {

	/**
	 * Instantiates the factory.
	 */
	public SimpleAgentControllerFactory() {}

//	/**
//	 * Instantiates the factory with no parameters.
//	 * @return the instance
//	 */
//	public SimpleAgentControllerFactory instantiate() {
//		return new SimpleAgentControllerFactory();
//	}

	/**
	 * Creates a new simple agent controller.
	 * @param initializer
	 * @return the new instance or <code>null</code> if the controller could not
	 *         have be instantiated
	 */
	@Override
	public AgentController createController(AgentControlInitializer
		initializer) {
		return this.new SimpleAgentControllerImpl(initializer);
	}

	/**
	 * Simple agent controller that servers as core agent controller as well as
	 * module agent controller.
	 */
	private class SimpleAgentControllerImpl implements AgentController {

		/**
		 * Facade to access the controlled agent subject.
		 */
		private AgentSubjectFacade agentSubjectFacade;

		/**
		 * Indicates if the agent that belogs to this controller is controlled by an
		 * user.
		 */
		private boolean agentIsControlled;

		/**
		 * Set of rule name for the rules that are suspended if the agent is
		 * controlled by an user.
		 */
		private Set<String> suspendedRules;

		/**
		 * List of current perception events.
		 */
		private List<Event> perceptionEvents;

		/**
		 * Map of belief properties and their values.
		 */
		private Map<String, Object> beliefProperties;

		/**
		 * Factory to create the interaction events.
		 */
		private InteractionEventFactory interactionEventFactory;

		/**
		 * The controller's control view.
		 */
		private ControlView controlView;

		/**
		 * The user actions that shall be executed at the end of a step.
		 */
		private List<Pair<String, Map<String, String>>> userActions;

	/**
	 * Instantiates the abstract part of the core side agent conroller.
	 * When this part is instantiated the {@link AgentControlBroker} will notify
	 * the agent control module about this new controller if and only if the
	 * agent's id is contained in the array of controllable agents or if the
	 * array is empty.
	 * @param initializer
	 */
		public SimpleAgentControllerImpl(AgentControlInitializer initializer) {
			this.perceptionEvents = new ArrayList<Event>();
			this.beliefProperties = new HashMap<String, Object>();
			this.userActions = new ArrayList<Pair<String, Map<String, String>>>();
			this.controlView = null;
			this.agentIsControlled = true;

			this.interactionEventFactory = initializer.getInteractionEventFactory();
			this.suspendedRules = initializer.getSuspendedRules();
			this.agentSubjectFacade = initializer.getAgentSubjectFacade();
			this.agentSubjectFacade.setCoreAgentController(this);
		}

		/***********************************************************/
		/*** implementation of the CoreAgentController interface ***/
		/***********************************************************/

		/**
		 * Checks if a given reaction rule is suspended.
		 * @param reactionRule
		 * @return <code>true</code> if the rule is suspended
		 */
		@Override
		public boolean ruleIsSuspended(ReactionRule reactionRule) {
			return this.suspendedRules.contains(reactionRule.getName());
		}

		/**
		 * Upates the list of perception events.
		 * @param perceptionEvents
		 */
		@Override
		public void setNewPerceptionEvents(List<PerceptionEvent> perceptionEvents) {
			this.perceptionEvents.clear();
			this.perceptionEvents.addAll(perceptionEvents);
		}

		@Override
		public void replacePerceptionEventWithActualPerceptionEvent(
			PerceptionEvent perceptionEvent,InternalEvent actualPerceptionEvent) {
			int pos = this.perceptionEvents.indexOf(perceptionEvent);
			if(pos > 0) {
				this.perceptionEvents.remove(pos);
				this.perceptionEvents.add(pos, actualPerceptionEvent);
			}
		}

		@Override
		public void setBeliefProperties(Map<String, Object> beliefProperties) {
			if(beliefProperties == null) {
				this.beliefProperties.clear();
				return;
			}
			this.beliefProperties = beliefProperties;
		}

		/**
		 * Performs the user's actions.
		 */
		@Override
		public void performUserActions() {
			List<Pair<String, Map<String, String>>> oldUserActions = this.userActions;
			this.userActions = new ArrayList<Pair<String, Map<String, String>>>();
			if(this.interactionEventFactory != null && this.agentSubjectFacade != null) {
				for(Pair<String, Map<String, String>> action : oldUserActions) {
					this.agentSubjectFacade.processInternalEvent(
						this.interactionEventFactory.createEvent(action.value1, action.value2));
				}
			}
		}

		/**
		 * Updates the view with the newest values for the agent's belief properties
		 * and its current perceptions.
		 */
		@Override
		public void updateView() {
			if(this.controlView != null) {

				// update the belief properties
				if(this.beliefProperties != null) {
					for(String property : this.beliefProperties.keySet()) {
						this.controlView.propertyChange(new PropertyChangeEvent(this,
							property, null, this.beliefProperties.get(property)));
					}
					this.beliefProperties.clear();
				}

				// updates the perceptions
				if(this.perceptionEvents != null) {
					for(Event perceptionEvent : this.perceptionEvents) {
						this.controlView.propertyChange(new PropertyChangeEvent(this,
							perceptionEvent.getType(), null, perceptionEvent));
					}
					this.perceptionEvents.clear();

					// property change to indicate that the end of the percepetion list is
					// reached
					this.controlView.propertyChange(new PropertyChangeEvent(this,
						ModuleAgentController.END_OF_PERCEPTIONS, null, null));
				}
			}
		}

		/*************************************************************/
		/*** implementation of the ModuleAgentController interface ***/
		/*************************************************************/

		/**
		 * Sets the controller's control view.
		 * @param controlView
		 */
		@Override
		public void setControlView(ControlView controlView) {
			this.controlView = controlView;
		}

		/**
		 * Adds the given set of values to the list of user actions that shall be
		 * performed at the step's end.
		 * @param values
		 */
		@Override
		public void addUserAction(Map<String, String> values) {
			this.userActions.add(new Pair<String, Map<String, String>>(
				values.get(Sender.SEND_PROPERTY_NAME), values));
		}
	}
}