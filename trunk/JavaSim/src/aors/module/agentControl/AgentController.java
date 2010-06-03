package aors.module.agentControl;

import aors.model.agtsim.proxy.agentcontrol.CoreAgentController;
import aors.model.agtsim.proxy.agentcontrol.ModuleAgentController;
import aors.model.envevt.PerceptionEvent;
import aors.module.agentControl.gui.views.ControlView;
import aors.model.agtsim.proxy.agentcontrol.Pair;
import aors.module.agentControl.gui.interaction.Sender;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the module part of the agent controller. It is
 * responsible for the communcation with the view.
 * @author Thomas Grundmann
 */
public class AgentController implements PropertyChangeListener,
	ModuleAgentController {


	private CoreAgentController coreAgentController;

	private ControlView controlView;

	private List<Sender.ValueMap> userInteractionEvents;
	

	/*******************/
	/*** constructor ***/
	/*******************/

	public AgentController(CoreAgentController coreAgentController) {

		this.coreAgentController = coreAgentController;
		this.coreAgentController.setModuleAgentController(this);
		this.coreAgentController.setAgentIsControlled(true);

		this.controlView = null;

		this.userInteractionEvents = new ArrayList<Sender.ValueMap>();
	}

	public void setControlView(ControlView controlView) {
		this.controlView = controlView;
	}

	public Set<Pair<String, String>> getKeyEvents() {
		return this.coreAgentController.getKeyEvents();
	}

	public Map<String, Set<Pair<String, String>>> getMouseEvents() {
		return this.coreAgentController.getMouseEvents();
	}

	public String getAgentType() {
		return this.coreAgentController.getAgentType();
	}

	public void updateView(Map<String, Object> beliefProperties) {
		if(beliefProperties != null && this.controlView != null) {
			for(String property : beliefProperties.keySet()) {
				this.controlView.propertyChange(new PropertyChangeEvent(this,
					property, null, beliefProperties.get(property)));
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null && Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName()) &&
			evt.getNewValue() instanceof Sender.ValueMap) {
			this.userInteractionEvents.add((Sender.ValueMap)evt.getNewValue());
		}
	}

	public void addUserInteractionEvent(Sender.ValueMap values) {
		this.userInteractionEvents.add(values);
	}

	@Override
	public void performUserActions() {
		List<Sender.ValueMap> oldInteractionEvents = this.userInteractionEvents;
		this.userInteractionEvents = new ArrayList<Sender.ValueMap>();
		for(Map<String, String> eventData : oldInteractionEvents) {
			String eventName = eventData.get(Sender.SEND_PROPERTY_NAME);
			this.coreAgentController.processInternalEvent(eventName, eventData);
		}
	}

	@Override
	public void setNewPerceptionEvents(List<PerceptionEvent> perceptionEvents) {
//		for(PerceptionEvent event : perceptionEvents) {
//			System.out.println(event);
//		}
	}
}