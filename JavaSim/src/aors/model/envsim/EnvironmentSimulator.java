/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * File: EnvironmentSimulator.java
 *
 **************************************************************************************************************/
package aors.model.envsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import aors.controller.AbstractSimulator;
import aors.data.DataBus;
import aors.data.DataBusInterface;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektDestroyEventListener;
import aors.data.java.ObjektInitEvent;
import aors.data.java.ObjektInitEventListener;
import aors.data.java.SimulationEvent;
import aors.data.java.helper.AbstractSimState;
import aors.model.Message;
import aors.model.agtsim.AgentSubject;
import aors.model.envevt.AgentSubjectCreationEvent;
import aors.model.envevt.AgentSubjectDestructionEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.ExogenousEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.OutMessageEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envevt.StopSimulationEvent;
import aors.physim.PhysicsSimulator;
import aors.util.collection.AORCollection;

/**
 * EnvironmentSimulator
 * 
 * @author Emilian Pascalau, Adrian Giurca, Marco Pehla, Jens Werner, Mircea
 *         Diaconescu
 * @since May 25, 2008
 * @version $Revision$
 */
public class EnvironmentSimulator implements EnvironmentAccessFacet {

	private final String AOR_OBJECT = "Objekt";
	private final String AOR_AGENT = "AgentObject";
	private final String AOR_PHYSICAL_AGENT = "PhysicalAgentObject";
	private final String AOR_PHYSICAL_OBJECT = "PhysicalObject";
	private final String AOR_PHYSICAL = "Physical";

	/**
	 * This map contains all existing aor-objekts (objekts, physical objects,
	 * agents and physical agents) with an ID
	 */
	private Map<Long, Objekt> aorObjectsById;

	/**
	 * This map contains objects with defined names; if the name is unique in
	 * the simulation (...)
	 */
	private Map<String, Objekt> aorObjectsByName;

	/**
	 * This map contains lists for every user defined aor-objekt type and the
	 * abstract types: Objekt, AgentObject, PhysicalObject, PhysicalAgentObject
	 * and Physical The lists contains all existing aor-objects The key is the
	 * type of the object and the value is the list If exists inheritances, then
	 * contains the list for the super class instances the extended instances
	 * too
	 */
	private Map<String, List<Objekt>> aorObjectsByType;

	/*
	 * The DataBus used for sending messages and notifications
	 */
	private DataBusInterface dataBus;

	/**
	 * Comments: this is used to collect the objectDestroyListner
	 */
	private final List<ObjektDestroyEventListener> objDestroyListener = new ArrayList<ObjektDestroyEventListener>();

	/**
	 * Reference to the global Events list of AbstractSimulator processed events
	 * will be removed in abstract simulator
	 * 
	 */
	private List<EnvironmentEvent> currentEvents;

	/**
	 * List of new events. Events will be added to global events list at the end
	 * of EnvironmentSimulator Execution
	 */
	private List<EnvironmentEvent> newEvents = new ArrayList<EnvironmentEvent>();

	/**
	 * persists the current simulation step
	 */
	private long currentSimulationStep;

	/**
   * 
   */
	private List<EnvironmentRule> rules = new ArrayList<EnvironmentRule>();

	/**
   * 
   */
	private Map<Long, AORCollection<? extends Objekt>> collectionsById = new HashMap<Long, AORCollection<? extends Objekt>>();

	/**
   * 
   */
	private Map<String, AORCollection<? extends Objekt>> collectionsByName = new HashMap<String, AORCollection<? extends Objekt>>();

	/**
   * 
   */
	private long autoId = -1;

	/**
	 * the activityMangager
	 */
	// activity by jw
	protected ActivityManager activityManager;
	// ***

	/**
	 * Instance of PhySim (physics simulation extension)
	 */
	private PhysicsSimulator physim;

	/**
	 * only used by the physim
	 */
	private List<PhysicalAgentObject> physAgentObjects = new ArrayList<PhysicalAgentObject>();

	/**
	 * only used by the physim
	 */
	private List<PhysicalObject> physObjects = new ArrayList<PhysicalObject>();

	/**
	 * Force simulation stop
	 */
	private boolean forceStopSimulation = false;

	/**
	 * Get the value of forceStopSimulation property. This method must be called
	 * only after <code>processCurrentEvents</code> is called.
	 * 
	 * @return the value of forceStopSimulation field
	 */
	public boolean isForceStopSimulation() {
		return this.forceStopSimulation;
	}

	public EnvironmentSimulator(DataBusInterface dataBus,
			PhysicsSimulator physim) {
		this.dataBus = dataBus;
		this.physim = physim;
		// activity by jw
		if (AbstractSimulator.runActivities)
			this.activityManager = new ActivityManager(this, dataBus);
		// ****

		this.aorObjectsById = new HashMap<Long, Objekt>();
		this.aorObjectsByType = new HashMap<String, List<Objekt>>();
		this.aorObjectsByName = new HashMap<String, Objekt>();
		// add the default type lists
		this.aorObjectsByType.put(AOR_OBJECT, new ArrayList<Objekt>());
		this.aorObjectsByType.put(AOR_AGENT, new ArrayList<Objekt>());
		this.aorObjectsByType.put(AOR_PHYSICAL_OBJECT, new ArrayList<Objekt>());
		this.aorObjectsByType.put(AOR_PHYSICAL_AGENT, new ArrayList<Objekt>());
		this.aorObjectsByType.put(AOR_PHYSICAL, new ArrayList<Objekt>());
	}

	/**
	 * Notice: use this method after the logger has been set
	 */
	public void initialize() {
		this.objDestroyListener.add(this.dataBus);

		// initialization for PhySim extension
		// phySim.setInit(false);
		if (AbstractSimulator.runPhysics) {

			physim.setPhysicalAgents(this.physAgentObjects);
			physim.setPhysicalObjects(this.physObjects);
			physim.initPhys();
		}
	}

	public void setEventsList(List<EnvironmentEvent> events) {
		this.currentEvents = events;
	}

	/**
	 * Run the EnvironmentSimulator in the current step. The EnvSim runs on the
	 * reference to the global events list.
	 * 
	 * @param currentSimulationStep
	 */
	public void run(long currentSimulationStep) {
		this.currentSimulationStep = currentSimulationStep;
		this.newEvents.clear();

		// perform Physical simulation (Translation- and DynamicsSimulator)
		if (AbstractSimulator.runPhysics)
			physim.simulationStepExternal(currentEvents, currentSimulationStep,
					true);

		// add events that were generated by external simulators like the
		// physics
		// module which generate collision and other like events.
		this.currentEvents.addAll(((DataBus) this.dataBus).getEvtEvents());
		((DataBus) this.dataBus).clearEvtEvents();

		this.processCurrentEvents();

		// perform Physical simulation (Perception- and CollisionSimulator)
		if (AbstractSimulator.runPhysics)
			physim.simulationStepExternal(currentEvents, currentSimulationStep,
					false);

		computeAbstractSimState();

		// append new EnvironmentEvents to the global events list
		this.currentEvents.addAll(newEvents);
	} // run

	/**
	 * Cycles through the list of global events (currentEvents) and processes
	 * all events that are for current simulation step.
	 */
	private void processCurrentEvents() {

		// cycle through all events
		Iterator<EnvironmentEvent> currentEventsIterator = this.currentEvents
				.iterator();

		while (currentEventsIterator.hasNext()) {
			EnvironmentEvent environmentEvent = currentEventsIterator.next();

			// only for ExogenousEvents
			boolean skip = false;
			// only events concerning the currentSimulationStep are processed
			if (environmentEvent.getOccurrenceTime() == this.currentSimulationStep) {

				// if StopSimulation event occurs then force simulation stop
				if (environmentEvent instanceof StopSimulationEvent) {
					this.forceStopSimulation = true;
				}

				// check the stop condition of an ExogenousEvent (incl.
				// OnEveryStepEnvEvent)
				if (environmentEvent instanceof ExogenousEvent) {

					ExogenousEvent exogenousEvent = (ExogenousEvent) environmentEvent;

					skip = ((ExogenousEvent) environmentEvent).stopCondition();

					if (skip) {
						// if the stopCondition true, set the nextOccurrencetime
						// to 0
						// to show in the logger, that we have no new occurrence
						// time
						exogenousEvent.setNextOccurrenceTime(0);
					} else {
						exogenousEvent.setNextOccurrenceTime(environmentEvent
								.getOccurrenceTime()
								+ exogenousEvent.periodicity());
					}
				}

				if (!skip) {
					dataBus.notifyEnvEvent(environmentEvent);
					if (!(environmentEvent instanceof PerceptionEvent)) {
						processNonPerceptionEvent(environmentEvent);
					}
				} // if (!skip)
			} // if (environmentEvent.getOccurrenceTime() ==
			// currentSimulationStep)
		}// while (currentEventsIterator.hasNext())
	} // processCurrentEvents

	/**
	 * Process a single EnvironmentEvent of the current step which is not a
	 * PerceptionEvent
	 * 
	 * @param environmentEvent
	 *            the EnvironmentEvent to process
	 */
	private void processNonPerceptionEvent(EnvironmentEvent environmentEvent) {

		List<EnvironmentEvent> resultingEventList = new ArrayList<EnvironmentEvent>();

		Iterator<EnvironmentRule> environmentRulesIterator;
		EnvironmentRule environmentRule;
		String currentEventSimpleName;
		String ruleTriggeringEventSimpleName;
		List<EnvironmentEvent> environmentRuleResult;

		environmentRuleResult = new ArrayList<EnvironmentEvent>();

		// activities by jw
		// checking of available Activities (starts/ends by
		// environmentEvent) and execute the tasks
		if (this.activityManager != null)
			resultingEventList.addAll(this.activityManager
					.processEvent(environmentEvent));

		environmentRulesIterator = rules.iterator();

		boolean skipMessageMapping = false;
		currentEventSimpleName = environmentEvent.getClass().getSimpleName();

		while (environmentRulesIterator.hasNext()) {

			environmentRule = environmentRulesIterator.next();
			ruleTriggeringEventSimpleName = environmentRule
					.getTriggeringEventType();

			if (currentEventSimpleName.equals(ruleTriggeringEventSimpleName)
					&& this.checkMessageTypeForRulesTriggeredByOutMessageEvent(
							environmentEvent, environmentRule)) {
				// event matching was performed and concrete instance is set to
				// the rule
				environmentRule.setTriggeringEvent(environmentEvent);
				environmentRule.setTriggeredTime(environmentEvent
						.getOccurrenceTime());
				environmentRuleResult = environmentRule.execute();
				dataBus.notifyEnvEventResult(environmentRule,
						environmentRuleResult);

				// either if the resulting list is empty or not,
				// the content will be added to the the list of events
				// that will be sent to SimulationEngine
				resultingEventList.addAll(environmentRuleResult);
				skipMessageMapping = true;
			}// if
			environmentRuleResult.clear();

		}// while (environmentRulesIterator.hasNext()) {

		// if it's an OutMessageEvent and it isn't triggered some rule, then
		// create
		// an corresponding InMessageEvent
		if (!skipMessageMapping && environmentEvent instanceof OutMessageEvent) {
			// auto-mapping from out-message-event to in-message-event
			EnvironmentEvent envEvt = this
					.messageMapping((OutMessageEvent) environmentEvent);
			resultingEventList.add(envEvt);
			environmentRuleResult.add(envEvt);
			dataBus.notifyEnvEventResult(null, environmentRuleResult);
		}

		// add all new events to newEvents list to later append it to global
		// list
		newEvents.addAll(resultingEventList);
	} // processNonPerceptionEvent

	/**
	 * Check, if the environmentEvent argument is an {@link OutMessageEvent} and
	 * in the second step, if the {@link Message} is the correct message for the
	 * argument {@link EnvironmentRule#}
	 * 
	 * @param environmentEvent
	 * @param environmentRule
	 * @return true, if its not an {@link OutMessageEvent} or it is an
	 *         {@link OutMessageEvent} and it is the right {@link Message} to
	 *         trigger the rule, otherwise false
	 */
	private boolean checkMessageTypeForRulesTriggeredByOutMessageEvent(
			EnvironmentEvent environmentEvent, EnvironmentRule environmentRule) {

		if (!environmentEvent.getClass().equals(OutMessageEvent.class))
			return true;

		if (environmentRule.getMessageType().equals(""))
			return false;

		String eventMessageType = ((OutMessageEvent) environmentEvent)
				.getMessage().getClass().getSimpleName();
		if (eventMessageType.equals(environmentRule.getMessageType()))
			return true;

		return false;
	}

	/**
	 * Compute the AbstractSimState
	 */
	private void computeAbstractSimState() {
		AbstractSimState abstractSimState = new AbstractSimState();
		abstractSimState.setSimStep(currentSimulationStep);
		abstractSimState.setAgentsCount(this.aorObjectsByType.get(AOR_AGENT)
				.size());
		abstractSimState.setObjectsCount(this.aorObjectsByType.get(AOR_OBJECT)
				.size()
				- this.aorObjectsByType.get(AOR_AGENT).size());
		abstractSimState.setEventsCount(this.currentEvents.size());

		dataBus.notifySimulationInfo(new SimulationEvent(this,
				DataBus.LoggerEvent.EVENT_INFOS, abstractSimState));
	} // computeAbstractSimState

	/**
	 * Comment: Adds an {@link PhysicalAgentObject} to the AgentObjectList and
	 * fire a {@link ObjektInitEvent}
	 * 
	 * @param physAgentObject
	 * @return true if the addition is successful
	 */
	public boolean addPhysicalAgent(PhysicalAgentObject physAgentObject) {
		if (physAgentObject != null) {
			this.aorObjectsByType.get(AOR_OBJECT).add(physAgentObject);
			this.aorObjectsByType.get(AOR_AGENT).add(physAgentObject);
			// this.aorObjectsByType.get(AOR_PHYSICAL_OBJECT).add(physAgentObject);
			this.aorObjectsByType.get(AOR_PHYSICAL_AGENT).add(physAgentObject);
			this.aorObjectsByType.get(AOR_PHYSICAL).add(physAgentObject);
			// only for the current physim
			this.physAgentObjects.add(physAgentObject);
		}
		return this.addAORObject(physAgentObject);
	}

	public boolean addAgent(AgentObject agentObject) {
		if (agentObject != null) {
			this.aorObjectsByType.get(AOR_OBJECT).add(agentObject);
			this.aorObjectsByType.get(AOR_AGENT).add(agentObject);
		}
		return this.addAORObject(agentObject);
	}

	/**
	 * 
	 * @param physicalObject
	 * @return true if the addition was successful
	 */
	public boolean addPhysicalObjekt(PhysicalObject physicalObject) {
		if (physicalObject != null) {
			this.aorObjectsByType.get(AOR_OBJECT).add(physicalObject);
			this.aorObjectsByType.get(AOR_PHYSICAL_OBJECT).add(physicalObject);
			this.aorObjectsByType.get(AOR_PHYSICAL).add(physicalObject);
			// only for the current physim
			this.physObjects.add(physicalObject);
		}
		return this.addAORObject(physicalObject);
	}

	/**
	 * Add a new instance of an aor-objekt to the system
	 * 
	 * @param objekt
	 *            the new aor-objekt
	 * @return true, if the addition was successful, otherwise false
	 */
	public boolean addObjekt(Objekt objekt) {
		if (objekt != null)
			this.aorObjectsByType.get(AOR_OBJECT).add(objekt);
		return this.addAORObject(objekt);
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments:
	 * 
	 * 
	 * 
	 * @param objekt
	 * @return
	 */
	private boolean addAORObject(Objekt objekt) {

		if (objekt == null)
			return false;

		if (objekt.getId() == 0) {
			objekt.setId(this.getAutoId());
		}

		if (this.aorObjectsById.containsKey(objekt.getId())) {
			System.err.println("The ID " + objekt.getId()
					+ " is allready used. The " + objekt.getClass()
					+ "was not created.");
			return false;
		}

		// add to the id-map
		this.aorObjectsById.put(objekt.getId(), objekt);

		// add to the type-map
		this.addObjektToTypeMap(objekt.getClass(), objekt);

		// add to the name-map
		this.addToNameMap(objekt);

		objekt.addPropertyChangeListener(dataBus);
		objekt.addObjektInitListener(dataBus);

		ObjektInitEvent physObjInitEvent = new ObjektInitEvent(objekt);
		for (ObjektInitEventListener eventListener : objekt.initListener) {
			eventListener.objektInitEvent(physObjInitEvent);
		}

		return true;
	}

	/**
	 * 
	 * Usage: adds an objekt to the objekt map with a name as a key
	 * 
	 * Comments: if the name are exists in the map, then the value will be set
	 * to 'null'; this means the name is not unique in the system
	 * 
	 * @param objekt
	 */
	private void addToNameMap(Objekt objekt) {

		String name = objekt.getName();
		if (!name.equals("")) {

			if (this.aorObjectsByName.containsKey(name)) {
				this.aorObjectsByName.put(name, null);
			} else {
				this.aorObjectsByName.put(name, objekt);
			}
		}
	}

	/**
	 * Add an objekt instance to a list in objekt type list
	 * 
	 * @param objekt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void addObjektToTypeMap(Class<? extends Objekt> clazz, Objekt objekt) {

		String objType = clazz.getSimpleName();
		// System.out.println("ADD " + objekt.getType() + " to " + objType);
		if (this.aorObjectsByType.containsKey(objType)) {
			this.aorObjectsByType.get(objType).add(objekt);
		} else {
			List<Objekt> objL = new ArrayList<Objekt>();
			objL.add(objekt);
			this.aorObjectsByType.put(objType, objL);
		}

		clazz = (Class<? extends Objekt>) clazz.getSuperclass();
		if (!(clazz.equals(PhysicalAgentObject.class)
				|| clazz.equals(PhysicalObject.class)
				|| clazz.equals(AgentObject.class) || clazz
				.equals(Objekt.class))) {
			this.addObjektToTypeMap(clazz, objekt);
		}

	}

	/**
   * 
   */
	public void addCollection(AORCollection<? extends Objekt> collection) {

		if (collection != null) {

			collection.addCollectionListener(dataBus);
			collection.addCollectionInitListener(dataBus);
			collection.notifyCollectionInitEvent();

			this.collectionsById.put(collection.getId(), collection);
			if (!collection.getName().equals("")) {
				this.collectionsByName.put(collection.getName(), collection);
			}
		}
	}

	@Override
	public AORCollection<? extends Objekt> getCollectionById(long id) {
		return this.collectionsById.get(id);
	}

	@Override
	public AORCollection<? extends Objekt> getCollectionByName(String name) {
		return this.collectionsByName.get(name);
	}

	/**
	 * Usage:
	 * 
	 * 
	 * Comments: Set the {@code rules}.
	 * 
	 * 
	 * 
	 * @param rules
	 *            The {@code rules} to set.
	 */
	public void setRules(List<EnvironmentRule> rules) {
		this.rules = rules;
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments: search for an {@code agent} in the list of {@code agentObjects}
	 * with specific {@code id}; if such an object is found then this {@code
	 * agent} is returned; otherwise it is returned {@code null}
	 * 
	 * @param id
	 * @param type
	 *            - class of requested AgentObject
	 * @return the agent identified by this {@code id}
	 */
	@Override
	public PhysicalAgentObject getPhysAgentById(long id, Class<?> type) {

		Objekt objekt = this.getObjectFromAllByID(id);
		if (PhysicalAgentObject.class.isInstance(objekt)
				&& type.isInstance(objekt))
			return (PhysicalAgentObject) objekt;

		return null;
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments: search for an {@code agent} in the list of {@code agentObjects}
	 * with specific {@code id}; if such an object is found then this {@code
	 * agent} is returned; otherwise it is returned {@code null}
	 * 
	 * @param id
	 * @return the agent identified by this {@code id}
	 */
	@Override
	public PhysicalAgentObject getPhysAgentById(long id) {

		Objekt objekt = this.getObjectFromAllByID(id);
		if (PhysicalAgentObject.class.isInstance(objekt))
			return (PhysicalAgentObject) objekt;

		return null;
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments: search for an {@code agent} in the list of {@code agentObjects}
	 * with specific {@code id}; if such an object is found then this {@code
	 * agent} is returned; otherwise it is returned {@code null}
	 * 
	 * @param id
	 * @param type
	 *            - class of requested AgentObject
	 * @return the agent identified by this {@code id}
	 */
	@Override
	public AgentObject getAgentById(long id, Class<?> type) {

		Objekt objekt = this.getObjectFromAllByID(id);
		if (AgentObject.class.isInstance(objekt) && type.isInstance(objekt))
			return (AgentObject) objekt;

		return null;
	}

	/**
	 * @param type
	 * @return
	 * @deprecated please use: {@link #getObjectsByType(Class)}
	 */
	@Override
	@Deprecated
	public List<AgentObject> getAgentObjectsByType(Class<?> type) {
		// instantiate an empty list
		List<AgentObject> result = new ArrayList<AgentObject>();

		// get an iterator over the agent object list
		for (Objekt agentObject : this.aorObjectsByType.get(AOR_AGENT)) {
			// if the given type matches
			if (type.isInstance(agentObject)) {
				// add this agent object to the result list
				result.add((AgentObject) agentObject);
			}

		}
		return result;
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments: search for an {@code agent} in the list of {@code agentObjects}
	 * with specific {@code id}; if such an object is found then this {@code
	 * agent} is returned; otherwise it is returned {@code null}
	 * 
	 * @param id
	 * @return the agent identified by this {@code id}
	 */
	@Override
	public AgentObject getAgentById(long id) {

		Objekt objekt = this.getObjectFromAllByID(id);
		if (AgentObject.class.isInstance(objekt))
			return (AgentObject) objekt;

		return null;
	}

	/**
	 * Return the AgentObject by id which can be an AgentObject or a
	 * PhysicalAgentObject
	 * 
	 * Is used in AbstractSimulator in addAgentSubject()
	 * 
	 * @param agentId
	 *            id of the AgentObject to find.
	 * @return the AgentObject with the given id
	 */
	public AgentObject getAgentObjectById(long agentId) {

		Objekt objekt = this.getObjectFromAllByID(agentId);
		if (AgentObject.class.isInstance(objekt))
			return (AgentObject) objekt;

		return null;
	}

	/**
	 * @param id
	 *            - the ID for the agent
	 * @return an non-physical or a physical agent with ID id or null
	 */
	@Override
	public AgentObject getActivityActorById(long id) {

		AgentObject result = null;

		result = this.getAgentById(id);
		if (result == null)
			result = this.getPhysAgentById(id);

		return result;
	}

	/**
	 * 
	 * Returns a PhysicalObject depends from the id; if such PhysicalObject not
	 * found in AgentList or in ObjectList is {@code null} returned
	 * 
	 * @param id
	 * @return the PhysicalObject identified by this {@code id}
	 */
	@Deprecated
	public Physical getPhysicalObjectById(long id) {

		Physical result = this.getPhysAgentById(id);
		if (result != null)
			return result;

		return this.getPhysObjectById(id);
	}

	/**
	 * 
	 * Returns a Entity depends from the id; if such Entity not found in
	 * EntityList is {@code null} returned
	 * 
	 * @param id
	 * @return the Entity identified by this {@code id}
	 */
	public Objekt getObjectById(long id) {
		return this.aorObjectsById.get(id);
	}

	public Objekt getObjectById(long id, Class<?> type) {

		Objekt objekt = this.getObjectFromAllByID(id);
		if (type.isInstance(objekt))
			return objekt;

		return null;
	}

	/**
	 * 
	 * Usage: it can be used in environmentRules
	 * 
	 * 
	 * Comments: search for an {@code object} in the list of {@code objects}
	 * with specific {@code id}; if such an object is found then this {@code
	 * object} is returned; otherwise it is returned {@code null}
	 * 
	 * @param id
	 * @param type
	 *            - class of requested physical object
	 * @return object identified by this {@code id}
	 */
	@Override
	public PhysicalObject getPhysicalObjectById(long id, Class<?> type) {

		Objekt objekt = this.getObjectFromAllByID(id);
		if (PhysicalObject.class.isInstance(objekt) && type.isInstance(objekt))
			return (PhysicalObject) objekt;

		return null;
	}

	/**
	 * 
	 * Usage: it can be used in environmentRules
	 * 
	 * 
	 * Comments: search for an {@code object} in the list of {@code objects}
	 * with specific {@code id}; if such an object is found then this {@code
	 * object} is returned; otherwise it is returned {@code null}
	 * 
	 * @param id
	 * @return object identified by this {@code id}
	 */
	@Override
	public PhysicalObject getPhysObjectById(long id) {

		Objekt objekt = this.getObjectFromAllByID(id);
		if (PhysicalObject.class.isInstance(objekt))
			return (PhysicalObject) objekt;

		return null;
	}

	private Objekt getObjectFromAllByID(long id) {
		return this.aorObjectsById.get(id);
	}

	/**
	 * 
	 * Usage: used in createObject from any EnvironmentRule
	 * 
	 * 
	 * Comments: This method adds the given parameter {@code object} to the list
	 * of objects. It returns {@code true} if this operation finished successful
	 * and {@code false} when not.
	 * 
	 * @param object
	 */
	@Override
	public boolean createPhysicalObject(PhysicalObject object) {

		boolean result = this.addPhysicalObjekt(object);

		if (result && AbstractSimulator.runPhysics)
			physim.physicalCreated((Physical) object);

		return result;
	}

	@Override
	public boolean createObjekt(Objekt objekt) {
		return this.addObjekt(objekt);
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments:
	 * 
	 * The occurrence time is derived from the current simulation step. A
	 * AgentSubjectCreationEvent instance is created with the {@code
	 * occuranceTime} and the {@code agentSubject}. The event instance is
	 * afterwards added to a ArrayList which is content of a
	 * EnvironmentSimulatorJavaEvent instance. This event instance is then
	 * notified to all listeners with the help of the {@code fireEvent} method.
	 * 
	 * @param agentObject
	 * @param agentSubject
	 */
	@Override
	public boolean createAgent(AgentObject agentObject,
			AgentSubject agentSubject) {

		boolean result = false;

		/**
		 * SimulationEngine must be informed about the creation of new Agents
		 * implement the operation of informing similar the informing operation
		 * about events to be performed
		 */
		if (PhysicalAgentObject.class.isInstance(agentObject)) {

			result = this.addPhysicalAgent((PhysicalAgentObject) agentObject);
			if (result && AbstractSimulator.runPhysics) {
				this.physim.physicalCreated((Physical) agentObject);
			}

		} else {
			result = this.addAgent(agentObject);
		}

		if (result) {
			// occurred time is this simulation step
			long occurenceTime = this.currentSimulationStep;

			// create a new creation event for the AgentSubject
			AgentSubjectCreationEvent agentSubjectCreationEvent = new AgentSubjectCreationEvent(
					occurenceTime, agentSubject);

			// fire the event and inform the listener
			this.newEvents.add(agentSubjectCreationEvent);
		}

		return result;
	}

	@Override
	public boolean destroyObject(long id) {
		return this.destroyAORObject(id);
	}

	/**
	 * 
	 * Comments: This method searches the list of objects for an instance with
	 * the given parameter {@code id}. If one is found, this instance is deleted
	 * from the list and {@code true} is returned. Otherwise the content of list
	 * does not change and {@code false} is returned by this method.
	 * 
	 * 
	 * @param id
	 */
	private boolean destroyAORObject(long id) {

		// remove from id-list
		Objekt objekt = this.aorObjectsById.remove(id);
		if (objekt == null)
			return false;

		// **** remove from type-lists ****
		// user defined type list
		this.deleteObjektFromTypeMap(objekt.getClass(), objekt);

		// pre defined type lists
		if (AgentObject.class.isInstance(objekt)) {
			// the remove will be executed in the following method for
			// AgentObjects
			// and PhysicalAgentObjects
			this.destroyAgent((AgentObject) objekt);

		} else if (PhysicalObject.class.isInstance(objekt)) {

			// delete from pysim list
			this.physObjects.remove(objekt);
			if (AbstractSimulator.runPhysics)
				physim.physicalDestroyed(id);

			this.aorObjectsByType.get(AOR_OBJECT).remove(objekt);
			this.aorObjectsByType.get(AOR_PHYSICAL_OBJECT).remove(objekt);
			this.aorObjectsByType.get(AOR_PHYSICAL).remove(objekt);

		} else {
			this.aorObjectsByType.get(AOR_OBJECT).remove(objekt);
		}

		// **** remove from the name-map ****
		this.deleteObjektFromNameMap(objekt);

		this.notifyDestroyEventListener(new ObjektDestroyEvent(objekt,
				this.currentSimulationStep));

		return true;
	}

	/**
	 * Usage: delete an objekt from the name-map
	 * 
	 * Comments: the name should be unique in the system (otherwise the value in
	 * the map is null), so we can delete this element frome the name-map
	 * 
	 * @param objekt
	 */
	private void deleteObjektFromNameMap(Objekt objekt) {

		String name = objekt.getName();

		if (!name.equals("")) {
			this.aorObjectsByName.remove(name);
		}
	}

	/**
	 * Delete an objekt instance from a list in objekt type list
	 * 
	 * @param clazz
	 *            - type of current {@link Object}
	 * @param objekt
	 *            - aor-object to delete from list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void deleteObjektFromTypeMap(Class<? extends Objekt> clazz,
			Objekt objekt) {

		String objType = clazz.getSimpleName();
		if (this.aorObjectsByType.containsKey(objType)) {
			this.aorObjectsByType.get(objType).remove(objekt);
			// System.out.println("REMOVE " + objekt.getType() + " from " +
			// objType);
		}

		clazz = (Class<? extends Objekt>) clazz.getSuperclass();
		if (!(clazz.equals(PhysicalAgentObject.class)
				|| clazz.equals(PhysicalObject.class)
				|| clazz.equals(AgentObject.class) || clazz
				.equals(Objekt.class))) {
			this.deleteObjektFromTypeMap(clazz, objekt);
		}
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments: This method does not check, for performance reasons, if the
	 * agent's id is of an known agent
	 * 
	 * 
	 * 
	 * @param id
	 *            the {@code id} of an agent
	 */
	private void destroyAgent(AgentObject agentObject) {

		// occurred time is this simulation step
		long occuranceTime = this.currentSimulationStep;
		long id = agentObject.getId();

		// create a new event for the agents subject destruction from the
		// give agent id
		AgentSubjectDestructionEvent agentSubjectDestructionEvent = new AgentSubjectDestructionEvent(
				occuranceTime, id);

		// send the event to the listener, usually an simulation engine instance
		this.newEvents.add(agentSubjectDestructionEvent);

		if (PhysicalAgentObject.class.isInstance(agentObject)) {

			// delete from the list for physim
			this.physAgentObjects.remove(agentObject);

			this.aorObjectsByType.get(AOR_OBJECT).remove(agentObject);
			this.aorObjectsByType.get(AOR_AGENT).remove(agentObject);
			this.aorObjectsByType.get(AOR_PHYSICAL_OBJECT).remove(agentObject);
			this.aorObjectsByType.get(AOR_PHYSICAL_AGENT).remove(agentObject);
			this.aorObjectsByType.get(AOR_PHYSICAL).remove(agentObject);

			if (AbstractSimulator.runPhysics)
				physim.physicalDestroyed(id);

		} else {

			this.aorObjectsByType.get(AOR_OBJECT).remove(agentObject);
			this.aorObjectsByType.get(AOR_AGENT).remove(agentObject);
		}
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments: This method returns an {@code ArrayList} of agent objects of
	 * the given parameter {@code type}. The {@code ArrayList} is empty if there
	 * no matching agent objects.
	 * 
	 * @param type
	 *            the {@code type} of an agent object
	 * @return ArrayList of agent objects of the given parameter {@code type}
	 * @deprecated please use: {@link #getObjectsByType(Class)}
	 */
	@Override
	@Deprecated
	public List<PhysicalAgentObject> getPhysAgentObjectsByType(Class<?> type) {
		// instantiate an empty list
		List<PhysicalAgentObject> result = new ArrayList<PhysicalAgentObject>();

		// get an iterator over the agent object list
		Iterator<PhysicalAgentObject> i = physAgentObjects.iterator();

		PhysicalAgentObject agentObject;
		// as long as agent objects are in the list
		while (i.hasNext()) {
			// get the next agent object
			agentObject = i.next();

			// if the given type matches
			if (type.isInstance(agentObject)) {
				// add this agent object to the result list
				result.add(agentObject);
			}
		}
		// return the result list
		return result;
	}

	/**
	 * 
	 * Usage:
	 * 
	 * 
	 * Comments: This method returns an {@code ArrayList} of physical objects of
	 * the given parameter {@code type}. The {@code ArrayList} is empty if there
	 * are no matching physical objects.
	 * 
	 * 
	 * 
	 * @param type
	 *            the {@code type} of an physical object
	 * @deprecated please use: {@link #getObjectsByType(Class)}
	 */
	@Override
	@Deprecated
	public List<PhysicalObject> getPhysicalObjectsByType(Class<?> type) {
		// instantiate an empty list
		List<PhysicalObject> result = new ArrayList<PhysicalObject>();

		// get an iterator over the list of physical objects
		Iterator<PhysicalObject> i = physObjects.iterator();

		PhysicalObject physicalObject;

		// as long as physical objects are in the list
		while (i.hasNext()) {
			// get the next physical object
			physicalObject = i.next();

			// if the given type matches
			if (type.isInstance(physicalObject)) {
				// add this object to the result list
				result.add(physicalObject);
			}
		}

		return result;
	}

	public List<Objekt> getObjectsByType(Class<?> type) {
		return this.getObjectsByType(type.getSimpleName());
	}

	@Override
	public List<Objekt> getObjectsByType(String type) {
		if (this.aorObjectsByType.containsKey(type)) {
			return new ArrayList<Objekt>(this.aorObjectsByType.get(type));
		}
		return new ArrayList<Objekt>();
	}

	/**
	 * Creates an auto-id lower then 0
	 * 
	 * @return autoId
	 */
	@Override
	public long getAutoId() {
		if (this.autoId >= Long.MIN_VALUE)
			return this.autoId--;
		System.err.println("No autoId available!");
		return 0;
	}

	/**
   * 
   */
	private void notifyDestroyEventListener(
			ObjektDestroyEvent objektDestroyEvent) {
		for (ObjektDestroyEventListener odel : this.objDestroyListener) {
			odel.objektDestroyEvent(objektDestroyEvent);
		}
	}

	/**
	 * 
	 * @param outMessageEvent
	 * @return the mapped {@link InMessageEvent}
	 */
	private EnvironmentEvent messageMapping(OutMessageEvent outMessageEvent) {
		// TODO test the examples
		return new InMessageEvent(this.currentSimulationStep, outMessageEvent
				.getReceiverIdRef(), outMessageEvent.getActorIdRef(),
				outMessageEvent.getMessage());
	}

	/**
	 * @return the activityManager
	 */
	public ActivityManager getActivityManager() {
		return activityManager;
	}

	/**
	 * @return the physAgentObjects
	 */
	public List<PhysicalAgentObject> getPhysAgentObjects() {
		return new ArrayList<PhysicalAgentObject>(this.physAgentObjects);
	}

	/**
	 * @return the physObjects
	 */
	public List<PhysicalObject> getPhysObjects() {
		return new ArrayList<PhysicalObject>(this.physObjects);
	}

	/**
	 * @return the agentObjects
	 */
	// TODO: delete this
	public List<AgentObject> getAgentObjects() {
		return null;
	}

	/**
	 * @return the objekts
	 */
	public List<Objekt> getObjekts() {
		return new ArrayList<Objekt>(this.aorObjectsByType.get(AOR_OBJECT));
	}

	/**
	 * Usage:
	 * 
	 * 
	 * Comments: Overrides method {@code getNmrOfInstances} from super class
	 * 
	 * 
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public int getInstancesNumberForType(String type) {

		if (this.aorObjectsByType.get(type) != null)
			return this.aorObjectsByType.get(type).size();
		return 0;
	}

	/**
	 * Usage:
	 * 
	 * 
	 * Comments: Overrides method {@code getNmrOfInstances} from super class
	 * 
	 * 
	 * 
	 * @param objekt
	 * @return
	 */
	@Override
	public int getInstancesNumberForType(Objekt objekt) {
		return this.getInstancesNumberForType(objekt.getType());
	}

	/**
	 * Usage:
	 * 
	 * 
	 * Comments: Overrides method {@code getObjectByName} from super class
	 * 
	 * 
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public Objekt getObjectByName(String name) {
		return this.aorObjectsByName.get(name);
	}

	/**
	 * Get the {@code aorObjectsById} field value.
	 * 
	 * @return the value {@code aorObjectsById} field
	 */
	public Map<Long, Objekt> getAorObjectsById() {
		return this.aorObjectsById;
	}

	/**
	 * Get the {@code aorObjectsByType} field value.
	 * 
	 * @return the value {@code aorObjectsByType} field
	 */
	public Map<String, List<Objekt>> getAorObjectsByType() {
		return this.aorObjectsByType;
	}
}
