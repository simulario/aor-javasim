package aors.module.agentControl;

import aors.model.agtsim.proxy.agentControl.CoreAgentController;
import aors.model.agtsim.proxy.agentControl.ModuleAgentController;
import aors.model.envevt.PerceptionEvent;
import aors.module.agentControl.gui.views.ControlView;
import aors.module.agentControl.gui.interaction.Sender;
import aors.util.Pair;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the module part of the agent controller. It is
 * responsible for the communcation with the view.
 * @author Thomas Grundmann
 */
public class AgentController implements	ModuleAgentController {

	/**
	 * The controller's counterpart in the core; the core agent controller.
	 */
	private CoreAgentController coreAgentController;

	/**
	 * The contrller's control view.
	 */
	private ControlView controlView;

	/**
	 * The user interaction events that shall be executed at the end of a step.
	 */
	private List<Sender.ValueMap> userInteractionEvents;
	

	/*******************/
	/*** constructor ***/
	/*******************/

	/**
	 * Initializes the agent controller, connects it with its counterpart in
	 * the core and marks the agent as controlled.
	 * @param coreAgentController
	 */
	public AgentController(CoreAgentController coreAgentController) {
		this.coreAgentController = coreAgentController;
		this.controlView = null;
		this.userInteractionEvents = new ArrayList<Sender.ValueMap>();

		this.coreAgentController.setModuleAgentController(this);
		this.coreAgentController.setAgentIsControlled(true);
	}

	/*********************************************************/
	/*** methods related to the controller's agent subject ***/
	/*********************************************************/

	/**
	 * Returns the type of the agent subject to that the controller belongs.
	 * @return the agent's type
	 */
	public String getAgentType() {
		return this.coreAgentController.getAgentType();
	}

	/*******************************************/
	/*** methods related to the control view ***/
	/*******************************************/

	/**
	 * Sets the controller's control view.
	 * @param controlView
	 */
	public void setControlView(ControlView controlView) {
		this.controlView = controlView;
	}

	/**
	 * Returns the key events that are associated with this controller.
	 * @return the key events
	 */
	public Set<Pair<String, String>> getKeyEvents() {
		return this.coreAgentController.getKeyEvents();
	}

	/**
	 * Returns the mouse events that are associated with this controller.
	 * @return the mouse events
	 */
	public Map<String, Set<Pair<String, String>>> getMouseEvents() {
		return this.coreAgentController.getMouseEvents();
	}

	/**
	 * Updates the controller's view by updateing the displayed values for the
	 * given set of belief properties.
	 * @param beliefProperties
	 */
	@Override
	public void updateView(Map<String, Object> beliefProperties) {
		if(beliefProperties != null && this.controlView != null) {
			for(String property : beliefProperties.keySet()) {
				this.controlView.propertyChange(new PropertyChangeEvent(this,
					property, null, beliefProperties.get(property)));
			}
		}
	}

	/*****************************************/
	/*** methods related to user's actions ***/
	/*****************************************/

	/**
	 * Adds the given set of values to the list of user interaction events that
	 * shall be performed at the step's end.
	 * @param values
	 */
	public void addUserInteractionEvent(Sender.ValueMap values) {
		this.userInteractionEvents.add(values);
	}

	/**
	 * Processes the stored user interaction events and resets the list.
	 */
	@Override
	public void performUserActions() {
		List<Sender.ValueMap> oldInteractionEvents = this.userInteractionEvents;
		this.userInteractionEvents = new ArrayList<Sender.ValueMap>();
		for(Sender.ValueMap eventData : oldInteractionEvents) {
			this.coreAgentController.processInternalEvent(eventData.get(
				Sender.SEND_PROPERTY_NAME), eventData);
		}
	}

	/**************************************/
	/*** methods related to perceptions ***/
	/**************************************/

	/**
	 * Adds the given list of perception events to the list of events that shall
	 * be displayed at the end of the step.
	 * @param perceptionEvents
	 */
	@Override
	public void setNewPerceptionEvents(List<PerceptionEvent> perceptionEvents) {
//		for(PerceptionEvent event : perceptionEvents) {
//			System.out.println(event);
//		}
	}
}