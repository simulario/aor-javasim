package aors.model.agtsim.proxy.agentControl;

import aors.model.agtsim.AgentSubject.AgentSubjectFacade;
import aors.util.Pair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AgentControlInitializer {
	
	private AgentSubjectFacade agentSubjectFacade;
	private Set<Pair<String, String>> keyEvents;
	private Map<String, Set<Pair<String, String>>> mouseEvents;
	private Set<String> uiLanguages;
	private String defaultUILanguage;
	private Set<String> suspendedRules;
	private boolean agentIsControllable;

	/**
	 * Instantiates an new AgentControlInitializer.
	 * @param agentSubjectFacade
	 * @param controllableAgents
	 */
	protected AgentControlInitializer(AgentSubjectFacade agentSubjectFacade,
		Long[] controllableAgents) {
		this.agentSubjectFacade = agentSubjectFacade;
		this.keyEvents = new HashSet<Pair<String, String>>();
		this.mouseEvents = new HashMap<String, Set<Pair<String, String>>>();
		this.uiLanguages = new HashSet<String>();
		this.defaultUILanguage = null;
		this.suspendedRules = new HashSet<String>();

		this.agentIsControllable = (controllableAgents == null) ||
			(controllableAgents.length == 0) || (Arrays.binarySearch(
			controllableAgents, agentSubjectFacade.getAgentId()) >= 0);
	}

	/**
	 * Returns <code>true</code> if and only if the agent is controllable.
	 * @return <code>true</code> if and only if the agent is controllable.
	 */
	public boolean agentIsControllable() {
		return this.agentIsControllable;
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
	 * Adds a keyboard event.
	 * @param keyName
	 * @param action
	 */
	protected void addKeyEvent(String keyName, String action) {
		this.keyEvents.add(new Pair<String, String>(keyName, action));
	}

	/**
	 * Adds the name of a suspended rule.
	 * @param suspendedRuleName
	 */
	protected void addSuspendedRule(String suspendedRuleName) {
		this.suspendedRules.add(suspendedRuleName);
	}

	/**
	 * Return ths agent subject's facade.
	 * @return the facade to the agents (private) functionalies
	 */

	public AgentSubjectFacade getAgentSubjectFacade() {
		return this.agentSubjectFacade;
	}

	/**
	 * Returns the agent subject's id.
	 * @return the id
	 */
	public Long getAgentId() {
		return this.agentSubjectFacade.getAgentId();
	}

	/**
	 * Returns the agent subject's name.
	 * @return the name
	 */
	public String getAgentName() {
		return this.agentSubjectFacade.getAgentName();
	}

	/**
	 * Returns the agent subject's type.
	 * @return the type
	 */
	public String getAgentType() {
		return this.agentSubjectFacade.getAgentType();
	}

	/**
	 * Returns the defined key events for this agent controller.
	 * @return the key events
	 */
	public Set<Pair<String, String>> getKeyEvents() {
		return this.keyEvents;
	}

	/**
	 * Returns the defined mouse events for this agent controller.
	 * @return the mouse events
	 */
	public Map<String, Set<Pair<String, String>>> getMouseEvents() {
		return this.mouseEvents;
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

	/**
	 * Returns the set of suspended rule names.
	 * @return the set
	 */
	public Set<String> getSuspendedRules() {
		return this.suspendedRules;
	}

	/**
	 * Returns the {@link InteractionEventFactory} for the the interaction event
	 * creation.
	 * @return the interaction event factory
	 */
	public abstract InteractionEventFactory getInteractionEventFactory();
}