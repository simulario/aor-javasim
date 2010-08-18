package aors.model.agtsim.agentControl;

import aors.model.agtsim.AgentSubject.AgentSubjectFacade;
import aors.model.agtsim.ReactionRule;
import aors.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AgentControlInitializer {
	
	private Long[] controllableAgents;
	private AgentSubjectFacade agentSubjectFacade;
	private InteractionEventFactory interactionEventFactory;
	private List<ReactionRule> agentControlRules;
	private Set<String> suspendedRules;
	private Set<Pair<String, String>> keyEvents;
	private Map<String, Set<Pair<String, String>>> mouseEvents;
	private Set<String> uiLanguages;
	private String defaultUILanguage;

	/**
	 * Instantiates an new AgentControlInitializer with the set of controllable
	 * agents.
	 * @param controllableAgents
	 */
	protected AgentControlInitializer(Long[] controllableAgents) {
		this.controllableAgents = controllableAgents;

		this.agentSubjectFacade = null;
		this.interactionEventFactory = null;

		this.agentControlRules = new ArrayList<ReactionRule>();
		this.suspendedRules = new HashSet<String>();

		this.keyEvents = new HashSet<Pair<String, String>>();
		this.mouseEvents = new HashMap<String, Set<Pair<String, String>>>();

		this.uiLanguages = new HashSet<String>();
		this.defaultUILanguage = null;
	}

	/**
	 * Returns <code>true</code> if and only if the agent with the given id is
	 * controllable.
	 * @param agentId
	 * @return <code>true</code> if and only if the agent is controllable.
	 */
	public boolean isAgentControllable(long agentId) {
		return (controllableAgents == null) || (controllableAgents.length == 0) ||
			(Arrays.binarySearch(controllableAgents, agentId) >= 0);
	} 

	/**
	 * Initializes the initializer with the concrete values.
	 * @param agentSubjectFacade
	 */
	public abstract void init(AgentSubjectFacade agentSubjectFacade);

	/**
	 * Sets the agent subject facade.
	 * @param agentSubjectFacade
	 */
	protected void setAgentSubjectFacade(AgentSubjectFacade agentSubjectFacade) {
		this.agentSubjectFacade = agentSubjectFacade;
	}

	/**
	 * Return ths agent subject's facade.
	 * @return the facade to the agents (private) functionalies
	 */
	public AgentSubjectFacade getAgentSubjectFacade() {
		return this.agentSubjectFacade;
	}

	/**
	 * Sets the interaction event factory.
	 * @param interactionEventFactory
	 */
	protected void setInteractionEventFactory(InteractionEventFactory
		interactionEventFactory) {
		this.interactionEventFactory = interactionEventFactory;
	}


	/**
	 * Returns the {@link InteractionEventFactory} for the the interaction event
	 * creation.
	 * @return the interaction event factory
	 */
	public InteractionEventFactory getInteractionEventFactory() {
		return this.interactionEventFactory;
	};

	/**
	 * Adds an agent control rule.
	 * @param agentControlRule
	 */
	protected void addAgentControlRule(ReactionRule agentControlRule) {
		this.agentControlRules.add(agentControlRule);
	}

	/**
	 * Return the list of agent control rules.
	 * @return list of agent control rules
	 */
	public List<ReactionRule> getAgentControlRules() {
		return this.agentControlRules;
	}

	/**
	 * Adds the name of a suspended rule.
	 * @param suspendedRuleName
	 */
	protected void addSuspendedRule(String suspendedRuleName) {
		this.suspendedRules.add(suspendedRuleName);
	}

	/**
	 * Returns the set of suspended rule names.
	 * @return the set
	 */
	public Set<String> getSuspendedRules() {
		return this.suspendedRules;
	}

	/**
	 * Adds a keyboard event.
	 * @param keyName
	 * @param shiftPressed
	 * @param controlPressed
	 * @param action
	 */
	protected void addKeyEvent(String keyName, boolean shiftPressed, boolean
		controlPressed, String action) {
		this.keyEvents.add(new Pair<String, String>(keyName + "_" +
			Boolean.toString(shiftPressed) + "_" + Boolean.toString(controlPressed),
			action));
	}

	/**
	 * Returns the defined key events for this agent controller.
	 * @return the key events
	 */
	public Set<Pair<String, String>> getKeyEvents() {
		return this.keyEvents;
	}

	/**
	 * Adds a mouse event.
	 * @param sender
	 * @param eventType
	 * @param action
	 */
	protected void addMouseEvent(String sender, String eventType, String action) {
		if(!this.mouseEvents.containsKey(sender)) {
			this.mouseEvents.put(sender, new HashSet<Pair<String, String>>());
		}
		this.mouseEvents.get(sender).add(new Pair<String, String>(eventType, action));
	}

	/**
	 * Returns the defined mouse events for this agent controller.
	 * @return the mouse events
	 */
	public Map<String, Set<Pair<String, String>>> getMouseEvents() {
		return this.mouseEvents;
	}

	/**
	 * Adds an user interface language.
	 * @param language
	 */
	protected void addUILanguage(String language) {
		this.uiLanguages.add(language);
		if(this.uiLanguages.size() == 1) {
			this.defaultUILanguage = language;
		}
	}

	/**
	 * Returns the set of available user inteface languages.
	 * @return the set of languages
	 */
	public Set<String> getUILanguages() {
		return this.uiLanguages;
	}

	/**
	 * Returns the default user interface language.
	 * @return the language
	 */
	public String getDefaultUILanguage() {
		return this.defaultUILanguage;
	}
}