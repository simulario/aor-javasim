package aors.module.agentControl;

import aors.model.agtsim.AgentSubject;
import aors.model.agtsim.ReactionRule;
import aors.model.envevt.PerceptionEvent;
import aors.model.intevt.InternalEvent;
import aors.module.agentControl.gui.views.ControlView;
import aors.module.agentControl.gui.interaction.InteractiveComponent.Pair;
import aors.module.agentControl.gui.interaction.Sender;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AgentController<T extends AgentSubject>
	extends aors.model.agtsim.AgentSubject.AgentController
	implements PropertyChangeListener {

	private static ModuleController moduleController;
	protected T agentSubject;
	private List<Sender.ValueMap> userInteractionEvents;
	
	private Set<Pair<String, String>> keyEvents;
	private Map<String, Set<Pair<String, String>>> mouseEvents;
	
	protected Set<String> suspendedRules;

	private ControlView controlView;

	public AgentController(T agentSubject) {
		super(agentSubject);
		this.agentSubject = agentSubject;

		moduleController = ModuleController.getInstance();
		moduleController.registerAgentController(this);

		this.userInteractionEvents = new ArrayList<Sender.ValueMap>();
		this.keyEvents = new HashSet<Pair<String, String>>();
		this.mouseEvents = new HashMap<String, Set<Pair<String, String>>>();

		this.suspendedRules = new HashSet<String>();

		this.controlView = null;
	}

	public void setControlView(ControlView controlView) {
		this.controlView = controlView;
	}

	public void updateView() {
		Map<String, Object> properties = this.agentSubject.getBeliefProperties();
		if(properties != null && this.controlView != null) {
			for(String property : properties.keySet()) {
				this.controlView.propertyChange(new PropertyChangeEvent(this,
					property, null, properties.get(property)));
			}
		}
	}

	@Override
	public void performUserActions() {
		List<Sender.ValueMap> oldInteractionEvents = this.userInteractionEvents;
		this.userInteractionEvents = new ArrayList<Sender.ValueMap>();
		for(Sender.ValueMap eventData : oldInteractionEvents) {
			String eventName = eventData.get(Sender.SEND_PROPERTY_NAME);
			this.processInternalEvent(this.createEvent(
				this.getCurrentSimulationStep(), eventName, eventData));
		}
	}

	@Override
	public void setNewEvents(List<PerceptionEvent> perceptionEvents) {
//		for(PerceptionEvent event : perceptionEvents) {
//			System.out.println(event);
//		}
	}


	public void addUserInteractionEvent(Sender.ValueMap values) {
		this.userInteractionEvents.add(values);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null && Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName()) &&
			evt.getNewValue() instanceof Sender.ValueMap) {
			this.userInteractionEvents.add((Sender.ValueMap)evt.getNewValue());
		}
	}

	public abstract InternalEvent createEvent(long occurrenceTime,
		String eventName, Map<String, String> eventData);

	protected void addKeyEvent(String keyName, String action) {
		this.keyEvents.add(new Pair<String, String>(keyName, action));
	}

	protected void addMouseEvent(String sender, String eventType, String action) {
		if(!this.mouseEvents.containsKey(sender)) {
			this.mouseEvents.put(sender, new HashSet<Pair<String, String>>());
		}
		this.mouseEvents.get(sender).add(new Pair<String, String>(eventType, action));
	}

	@Override
	public boolean ruleIsSuspended(ReactionRule reactionRule) {
		return this.agentIsControlled &&
			this.suspendedRules.contains(reactionRule.getName());
	}
	
	public Long getAgentId() {
		if(this.agentSubject != null) {
			return this.agentSubject.getId();
		}
		return null;
	}
	
	public String getAgentType() {
		if(this.agentSubject != null) {
			return this.agentSubject.getType();
		}
		return null;
	}
	
	public String getAgentName() {
		if(this.agentSubject != null) {
			return this.agentSubject.getName();
		}
		return null;
	}

	public Set<Pair<String, String>> getKeyEvents() {
		return this.keyEvents;
	}

	public Map<String, Set<Pair<String, String>>> getMouseEvents() {
		return this.mouseEvents;
	}
}