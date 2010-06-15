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
 * File: AgentSubject.java
 * 
 * Package: aors.model.agtsim
 *
 **************************************************************************************************************/
package aors.model.agtsim;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import aors.logger.model.AgentSimulatorStep;
import aors.model.AtomicEvent;
import aors.model.Entity;
import aors.model.Rollbackable;
import aors.model.agtsim.beliefs.ERDFBeliefEntityManager;
import aors.model.agtsim.beliefs.ERDFBeliefEntityManagerImpl;
import aors.model.agtsim.jaxb.JaxbLogGenerator;
import aors.model.agtsim.json.JsonLogGenerator;
import aors.model.agtsim.proxy.agentControl.AgentControlBroker;
import aors.model.agtsim.proxy.agentControl.CoreAgentController;
import aors.model.agtsim.proxy.agentControl.ModuleAgentController;
import aors.util.Pair;
import aors.model.agtsim.sim.AgentSimulator;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envsim.AgentObject;
import aors.model.intevt.InternalEvent;
import aors.model.intevt.PeriodicTimeEvent;
import aors.query.sparql.QueryEngine;
import aors.util.Constants;
import aors.util.JsonData;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * AgentSubject
 * 
 * @author
 * @since May 25, 2008
 * @version $Revision$
 */
public abstract class AgentSubject extends Entity implements Rollbackable {

  protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
      this);

  /**
   * Comments: This list holds all internal events. Furthermore is this list
   * thread-safe a version.
   */
  protected List<InternalEvent> internalEvents = new ArrayList<InternalEvent>();

  /**
   * List for new internal events. Its content is appended to internalEvents at
   * the end of the run() method.
   */
  protected List<InternalEvent> newInternalEvents = new ArrayList<InternalEvent>();

  /**
   * Comments: rules are created only by the constructor. There is no setter for
   * this property
   * 
   */
  protected List<ReactionRule> reactionRules;

  /**
   * Actual Perception Rules TODO comment
   */
  protected List<ActualPerceptionRule> actualPercRules = new ArrayList<ActualPerceptionRule>();

  /**
   * Comments: this the associated AgentObject
   */
  protected AgentObject agentObject;

  /**
   * Comments: beliefs list - beliefs of this agent
   */
  private CopyOnWriteArrayList<Entity> beliefEntities = new CopyOnWriteArrayList<Entity>();

  /**
   * Comments: the belief manager It contains the triple store and the methods
   * for manage it.
   */
  private ERDFBeliefEntityManager beliefEntitiesManager = new ERDFBeliefEntityManagerImpl();

  /**
   * AgentMemory
   */
  private AgentMemory agentMemory;

  /**
   * A custom log string of the current step. It contains the information which
   * perception led to which action using which rule.
   */
  protected JsonLogGenerator jsonGen = null;

  private JaxbLogGenerator jaxbLogGenerator = null;

  /**
   * Incoming list of perception events for this AgentSubject
   */
  private List<PerceptionEvent> perceptionEvents;

  /**
   * Current simulation step
   */
  private long currentSimulationStep;

  /**
   * Outgoing list of action events of this AgentSubject
   */
  private List<ActionEvent> resultingActionEvents;

  private AgentController controller;

  private AgentSimulator simulator;

  /**
   * 
   * Comments: Create a new {@code AgentSubject}. This constructor is to be
   * called ONLY by the {@code SimulationEngine} Usage:
   * 
   * @param id
   *          the id of this AgentSubject. It must be the same as the id of its
   *          corresponding AgentObject in the EnvironmentSimulator
   * 
   * @param name
   *          the name of the AgentSubject TODO check if both constructors are
   *          used
   */
  public AgentSubject(long id, String name) {
    super(id, name);
    if ("".equals(name)) {
      this.setName(getClass().getSimpleName() + "_" + id);
    }
    this.controller = null;
  }

  /**
   * Constructor
   * 
   * @param id
   *          TODO check if both constructors are used
   */
  public AgentSubject(long id) {
    super(id);
    if ("".equals(getName())) {
      this.setName(getClass().getSimpleName() + "_" + id);
    }
    this.controller = null;
  }

  public boolean isLogGenerationEnabled() {
    return (jsonGen != null);
  }

  public void enableLogGeneration() {
    this.jsonGen = new JsonLogGenerator();
  }

  public void disableLogGeneration() {
    this.jsonGen = null;
  }

  public boolean isJaxbLogGenerationEnabled() {
    return (this.jaxbLogGenerator != null);
  }

  public void enableJaxbLogGeneration() {
    this.jaxbLogGenerator = new JaxbLogGenerator(this);
  }

  public void disableJaxbLogGeneration() {
    this.jaxbLogGenerator = null;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    if (propertyChangeSupport != null) {
      propertyChangeSupport.addPropertyChangeListener(listener);
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    if (propertyChangeSupport != null) {
      propertyChangeSupport.removePropertyChangeListener(listener);
    }
  }

  /**
   * Returns a list of ReactionRule-Objects
   * 
   * @return a list of ReactionRule-Objects
   */
  public List<ReactionRule> getAgentRules() {
    return this.reactionRules;
  }

  /**
   * Get the actual used Perception Rules
   * 
   * @return
   */
  public List<ActualPerceptionRule> getActualPerceptionRules() {
    return this.actualPercRules;
  }

  /**
   * Get the internal events
   * 
   * @return the internal events collection
   */
  @Deprecated
  public List<InternalEvent> getInternalEvents() {
    return this.internalEvents;
  }

  /**
   * Add a new internal event to the AgentSubject. Used in generated
   * Simulator.java during intialization of AgentSubject.
   * 
   * @param internalEvent
   *          new internal event
   */
  public void addInternalEvent(InternalEvent internalEvent) {
    if (internalEvent instanceof InternalEvent)
      this.internalEvents.add(internalEvent);
  } // addInternalEvent

  /**
   * returns the corresponding AgentObject
   * 
   * @return the agentObject
   */
  public AgentObject getAgentObject() {
    return agentObject;
  }

  /**
   * 
   * @param agentObject
   *          the agentObject to set
   */
  public void setAgentObject(AgentObject agentObject) {
    if (this.agentObject == null) {
      this.agentObject = agentObject;
    }
  }

  /**
   * @return the agentMemory
   */
  public AgentMemory getAgentMemory() {
    return agentMemory;
  }

  /**
   * @param agentMemory
   *          the agentMemory to set
   */
  public void setAgentMemory(AgentMemory agentMemory) {
    this.agentMemory = agentMemory;
  }

  /**
   * Search in this agentsubject's memory for perception event of type
   * 'eventType' with given query-string 'query'.
   * 
   * @param query
   *          String has form '[x,y]' with x as first index (1 is smallest
   *          index!) and y as last index in agent's memory.
   * @param eventType
   *          Type of perception event to look for.
   * @return true if event exists. false otherwise
   */
  public boolean existsEventMemory(String query, String eventType) {
    return agentMemory.existsEventMemory(query, eventType);
  }

  /**
   * 
   * Search in this agentsubject's memory for perception event of type
   * 'eventType' with given query-string 'query'. If an event of that type
   * exists, use 'pattern' and 'values' to search for an event that meets the
   * conditions. (See method getFirstIndexWith() for more information about the
   * 'pattern' argument).
   * 
   * @param query
   *          String has form '[x,y]' with x as first index (1 is smallest
   *          index!) and y as last index in agent's memory.
   * @param eventType
   *          Type of perception event to look for.
   * @param pattern
   *          Conditions
   * @param values
   *          Values for String 'pattern'
   * @return true if event exists. false otherwise
   */
  public boolean existsEventMemory(String query, String eventType,
      String pattern, Object... values) {
    boolean result = this.existsEventMemory(query, eventType);
    if (!result)
      return false;

    if (this.getFirstIndexWith(pattern, values) > -1)
      return true;
    else
      return false;
  }

  /**
   * Returns results (array) of last search in agent's memory.
   * 
   * @return Array of search results
   */
  public Object[] getLastSearchResults() {
    if (this.agentMemory != null) {
      return agentMemory.lastSearchResults();
    } else {
      return null;
    }
  }

  /**
   * Search in this agentsubject's memory for perception event of type
   * 'eventType' with given query-string 'query'.
   * 
   * @param query
   *          String has form '[x,y]' with x as first index (1 is smallest
   *          index!) and y as last index in agent's memory.
   * @param eventType
   *          Type of perception event to look for.
   * @return Array of search results
   */
  public Object[] getEventMemory(String query, String eventType) {
    if (this.agentMemory != null) {
      return agentMemory.getEventMemory(query, eventType);
    } else {
      return null;
    }
  }

  /**
   * Get the first index in search-results that matches the given condition in
   * the string pattern. For informations on the pattern format see
   * AgentMemory#getFirstIndexWith(String pattern, Object... values)
   * 
   * @param pattern
   *          Conditions
   * @param values
   *          Values
   * @return The first index in lastResults, that matches the given conditions.
   *         -1 if there was an error or no match.
   * 
   * @see AgentMemory
   * @see AgentMemory#getFirstIndexWith(String pattern, Object... values)
   */
  public int getFirstIndexWith(String pattern, Object... values) {
    if (this.agentMemory != null) {
      return agentMemory.getFirstIndexWith(pattern, values);
    } else {
      return -1;
    }
  }

  /*******************************************************
   * Beliefs management as RDF triples - Manager methods *
   *******************************************************/
  /**
   * Add an RDF triple for a belief entity
   * 
   * @param id
   *          the belief entity ID
   * @param propName
   *          the property name
   * @param value
   *          the value of the property
   */
  public void addBeliefEntityTriple(long id, String propName, String value) {
    // this.beliefEntitiesManager.addBeliefEntityTriple(id, propName, value);
  }

  /**
   * Get the value of a property for a specified entity ID and Property Name
   * 
   * @param id
   *          the ID of the belief entity
   * @param propName
   *          the name of the property
   * @return the value of the property
   */
  public Object getBeliefEntityPropertyValue(long id, String propName) {
    return null;
    /*
     * return this.beliefEntitiesManager .getBeliefEntityPropertyValue(id,
     * propName);
     */
  }

  /**
   * Remove a belief entity when the ID is known. This will remove all triples
   * of this belief entity.
   * 
   * @param id
   *          the ID of the belief entity
   */
  public void removeBeliefEntity(long id) {
    this.beliefEntitiesManager.removeBeliefEntity(id);
  }

  /**
   * Remove a certain triple of a belief entity
   * 
   * @param id
   *          the ID of the belief entity
   * @param propName
   *          the property of the belief entity
   * @param value
   *          the value of the property
   */
  public void removeBeliefEntityTriple(long id, String propName, String value) {
    this.beliefEntitiesManager.removeBeliefEntityTriple(id, propName, value);
  }

  /**
   * Remove a certain triple of a belief entity. The requested data is the
   * subject and property. All triples having this subject and property are
   * removed no matter which is the object
   * 
   * @param id
   *          the ID of the belief entity
   * @param propName
   *          the property of the belief entity
   * @param value
   *          the value of the property
   */
  public void removeBeliefEntityTriple(long id, String propName) {
    this.beliefEntitiesManager.removeBeliefEntityTriple(id, propName);
  }

  /**
   * Query beliefs.
   * 
   * @param queryLanguage
   *          The language query. Not Case sensitive. It is recommended to use
   *          constants from each Query class
   * @param queryString
   *          The string containing the query
   * @return A hash map with all solutions.
   */
  public List<HashMap<String, String>> executeQuery(String queryLanguage,
      String queryString) throws NoClassDefFoundError {

    List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
    result = this.beliefEntitiesManager.executeQuery(queryLanguage,
        queryString, this.beliefEntities);
    return result;
  }

  /**
   * Set the base URI value required for RDF beliefs management
   * 
   * @param baseURI
   *          the new value of Base URI
   */
  public void setBaseURI(String baseURI) {
    this.beliefEntitiesManager.setBaseURI(baseURI);
  }

  /***********************************************************************
   * END - Beliefs management as RDF triples - Manager methods *
   ***********************************************************************/

  /**
   * Add a belief to the agent beliefs list
   * 
   * @param belief
   *          the belief to be added to agent belief list
   * @return true
   */
  public boolean addBeliefEntity(Entity beliefEntity) {
    return this.beliefEntities.add(beliefEntity);
  }

  /**
   * Return the belief in the given position in the list
   * 
   * @param i
   *          the position in list
   * @return the belief
   */
  protected Entity getBeliefEntity(int position) {
    return this.beliefEntities.get(position);
  }

  /**
   * Returns an belief with a given ID. The first belief found with this id is
   * returned.
   * 
   * @param id
   *          the id to look for
   * @return the first belief from list having this id
   */
  protected Entity getBeliefEntityById(long id) {
    Entity result = null;
    for (Entity belief : beliefEntities) {
      if (belief.getId() == id) {
        result = belief;
        break;
      }
    }

    return result;
  }

  /**
   * Returns a list of beliefs having a given ID
   * 
   * @param id
   *          the id to look for
   * @return the beliefs list having this id
   */
  protected ArrayList<Entity> getBeliefEntitiesById(long id) {
    ArrayList<Entity> result = new ArrayList<Entity>();
    for (Entity belief : beliefEntities) {
      if (belief.getId() == id) {
        result.add(belief);
      }
    }

    return result;
  }

  /**
   * Return a list containing all beliefs from a given type name E.G.
   * getBeliefsByType("City"), where City is a belief type The type is the short
   * name of the inner class (without package and outer class)
   * 
   * @param type
   *          the string containing the belief type name
   * @return the list containing all agent's beliefs about that type
   */
  protected ArrayList<Entity> getBeliefEntitiesByType(String type) {
    ArrayList<Entity> result = new ArrayList<Entity>();

    for (Entity belief : beliefEntities) {
      if (belief.getClass().getSimpleName().equals(type)) {
        result.add(belief);
      }
    }

    return result;
  }

  /**
   * Return the type of a belief with a given id
   * 
   * @param id
   *          the id of the belief
   * @return the type of the belief
   */
  protected Class<?> getBeliefEntityTypeById(long id) {
    return this.getBeliefEntityById(id).getClass();
  }

  /**
   * Returns an collection of belief entities using their type name or their ID
   * that is given as parameter
   * 
   * @param idOrType
   *          the ID or the TypeName of the belief entity
   * @return the list with belief entities.
   */
  protected ArrayList<Entity> getBeliefEntities(Object idOrType) {
    if (idOrType instanceof String) {
      return this.getBeliefEntitiesByType((String) idOrType);
    }

    if (idOrType instanceof Integer) {
      return this.getBeliefEntitiesById((Integer) idOrType);
    }
    if (idOrType instanceof Long) {
      return this.getBeliefEntitiesById((Long) idOrType);
    }

    return (new ArrayList<Entity>());
  }

  /**
   * Delete a belief from a list based on a given position
   * 
   * @param position
   *          the position in list from which need to delete
   * @return the deleted belief
   */
  protected Entity removeBeliefEntity(int position) {
    return this.beliefEntities.remove(position);
  }

  /**
   * Remove the belief with the given id TODO @author please make comment
   * clearer: delete one beliefe or may delete all beliefs with the id
   * 
   * @param id
   *          the id of the belief to remove
   * @return true if removed, false otherwise
   */
  protected boolean removeBeliefEntityById(long id) {
    boolean result = false;
    for (Entity belief : beliefEntities) {
      if (belief.getId() == id) {
        this.beliefEntities.remove(belief);
        result = true;
      }
    }

    return result;
  }

  /**
   * Remove a specified belief
   * 
   * @param belief
   *          the belief to remove
   * @return true if removed, false otherwise
   */
  protected boolean removeBeliefEntity(Entity belief) {
    return this.removeBeliefEntityById(belief.getId());
  }

  /**
   * Create a belief instance for a given belief type name
   * 
   * @param agentSubjectInstance
   *          the agent subject instance containing this belief entity
   * @param beliefEntityTypeName
   *          the name of the belief entity type
   * @param id
   *          the id of the entity to be created
   * @return an instance of this belief entity type
   */
  protected Entity createBeliefEntity(Object agentSubjectInstance,
      String beliefEntityTypeName, long id) {
    Entity belief = null;

    // warning console message! a belief entity with that ID already exist
    if (this.getBeliefEntityById(id) != null) {
      System.out
          .println("Create belief entity failed since an belief entity with ID: "
              + id + " already exists! Belief entity was not created!");
      return belief;
    }

    try {
      Class<?> agentType = agentSubjectInstance.getClass();

      // the real entity type name - containing package and OuterClass
      beliefEntityTypeName = Constants.AGENT_SUBJECT_PACKAGE + "."
          + agentSubjectInstance.getClass().getSimpleName() + "$"
          + beliefEntityTypeName;

      // get the beliefType class
      Class<?> beliefType = agentType.getClassLoader().loadClass(
          beliefEntityTypeName);

      // parameters of the appropriate constructor of the belief type
      Class<?> paramTypes[] = new Class[2];
      paramTypes[0] = agentType;
      paramTypes[1] = Long.TYPE;

      // create a new belief type instance
      Constructor<?> cons = beliefType.getConstructor(paramTypes);
      cons.setAccessible(true);

      // parameters used to instanciate the belief type
      Object params[] = new Object[2];
      params[0] = agentSubjectInstance;
      params[1] = id;

      // create the belief instance
      belief = (Entity) cons.newInstance(params);

    } catch (Exception ex) {
      System.err.println("AgentSubject.createBeliefEntity exception: ");
      ex.printStackTrace();
    }

    return belief;
  }

  /**
   * Create a belief instance for a given belief type
   * 
   * @param agentSubjectInstance
   *          the agent subject instances containing this belief
   * @param beliefEntityType
   *          the type of the belief entity
   * @param id
   *          the id of the entity to be created
   * @return an instance of this belief entity type
   */
  protected Entity createBeliefEntity(Object agentSubjectInstance,
      Class<Entity> beliefEntityType, long id) {
    return createBeliefEntity(agentSubjectInstance, beliefEntityType
        .getSimpleName(), id);
  }

  /**
   * Generate an unique ID used for create a belief entity. The id is different
   * than any other belief ID that belongs to this agent. The ID is in range of
   * positive long values. The range used is the last 1/4 part of the MIN_VALUE
   * (the most negativeof long type and the last 1/4 part MAX_VALUE for long
   * type
   * 
   * @return an unique ID for beliefs of this agent
   */
  public long generateUniqueBeliefId() {
    Random random = new Random();
    long val_1_4_pos = (Long.MAX_VALUE - 1) / 4;
    long val_3_4_pos = Long.MAX_VALUE - val_1_4_pos;
    long val_1_4_neg = (Long.MIN_VALUE + 1) / 4;
    long val_3_4_neg = Long.MIN_VALUE - val_1_4_neg;

    long result = ((System.currentTimeMillis() % 2 == 0 ? (long) (val_1_4_pos
        * random.nextDouble() + val_3_4_pos) : (long) (val_1_4_neg
        * random.nextDouble() + val_3_4_neg)));

    // check for the right not existing ID
    while (this.getBeliefEntityById(result) != null) {
      // recalculate the id since this one is already in use
      result = ((System.currentTimeMillis() % 2 == 0 ? (long) (val_1_4_pos
          * random.nextDouble() + val_3_4_pos) : (long) (val_1_4_neg
          * random.nextDouble() + val_3_4_neg)));

    }

    return result;
  }

  /**
   * Update a given property of a belief with a new value
   * 
   * @param beliefId
   *          the Id of the belief to be updated
   * @param propertyName
   *          the property to be updated
   * @param newValue
   *          the new value for that property
   */
  protected void updateBeliefEntityProperty(long beliefId, String propertyName,
      Object newValue) {

    updateBeliefEntityProperty(getBeliefEntityById(beliefId), propertyName,
        newValue);
  }

  /**
   * Update a given property of a given belief entity with a new value
   * 
   * @param beliefEntity
   *          the belief entity to be updated
   * @param propertyName
   *          the property to be updated
   * @param newValue
   *          the new value for that property
   * @return the updated belief entity.
   */
  protected Entity updateBeliefEntityProperty(Entity beliefEntity,
      String propertyName, Object newValue) {

    // we can't update a null entity.
    if (beliefEntity == null) {
      return null;
    }

    try {
      // get the needed property
      Field prop = beliefEntity.getClass().getDeclaredField(propertyName);

      // set the property to be accessible for update
      prop.setAccessible(true);

      Class<?> propType = prop.getType();

      if (propType.equals(Long.TYPE)) {
        prop.set(beliefEntity, (newValue.getClass().equals(String.class) ? Long
            .parseLong((String) newValue) : newValue));
      } else if (propType.equals(Double.TYPE)) {
        prop.set(beliefEntity,
            (newValue.getClass().equals(String.class) ? Double
                .parseDouble((String) newValue) : newValue));
      } else {
        prop.set(beliefEntity, newValue);
      }

    } catch (IllegalAccessException ex1) {
      System.err.println("T1) AgentSubject.updateBeliefEntity: " + ex1);
    } catch (NoSuchFieldException ex2) {
      System.err.println("T2) AgentSubject.updateBeliefEntity: " + ex2);
    }

    return beliefEntity;
  }

  /**
   * Get the value of a property from a generic belief type
   * 
   * @param beliefEntity
   *          the belief entity
   * @param propertyName
   *          the property name
   * @return the value assigned to that property
   */
  protected Object getBeliefEntityPropertyValue(Entity beliefEntity,
      String propertyName) {

    if (beliefEntity == null) {
      return null;
    }

    try {
      // get the needed property
      Field prop = beliefEntity.getClass().getDeclaredField(propertyName);

      // set the property to be accessible for update
      prop.setAccessible(true);

      // return the value of that porperty
      return prop.get(beliefEntity);

    } catch (IllegalAccessException ex1) {
      System.err.println("T1) AgentSubject.getBeliefEntityPropertyValue: "
          + ex1);
    } catch (NoSuchFieldException ex2) {
      System.err.println("T2) AgentSubject.getBeliefEntityPropertyValue: "
          + ex2);
    }
    return null;
  }

  /**
   * Increments the value of the property with the specified value. If the
   * property has a numeric type, then the + operator is used to increase the
   * property's value with the specified value. If the value is not of numeric
   * type, a warning is shown, but the program continue to work
   * 
   * @param id
   *          the Id of the belief
   * @param propertyName
   *          the property name (which needs to be updated)
   * @param value
   *          the value to be added
   * @return true if operation is successful, false otherwise
   * 
   */
  public boolean incrementBeliefEntityPropVal(long id, String propertyName,
      Number value) {
    Entity belief = this.getBeliefEntityById(id);
    Method method;
    String methodName = "get" + propertyName.substring(0, 1).toUpperCase()
        + (propertyName.length() > 1 ? propertyName.substring(1) : "");

    Class<?> parameterType = value.getClass();
    Map<String, Class<?>> primitives = new HashMap<String, Class<?>>();

    primitives.put("Integer", Long.TYPE);
    primitives.put("Long", Long.TYPE);
    primitives.put("Float", Double.TYPE);
    primitives.put("Double", Double.TYPE);

    Object result = null;

    // obtain the right method to be called
    try {
      method = belief.getClass().getMethod(methodName);
      method.setAccessible(true);
    } catch (NoSuchMethodException exnm) {
      try {
        method = belief.getClass().getMethod(methodName,
            primitives.get(parameterType.getSimpleName()));
        method.setAccessible(true);
      } catch (NoSuchMethodException exnm1) {
        System.err.println("T1) AgentSubject.incrementBeliefEntityPropVal: "
            + exnm);
        return false;
      }
    }

    try {
      result = method.invoke(belief);
    } catch (InvocationTargetException exit) {
      System.err.println("T2) AgentSubject.incrementBeliefEntityPropVal: "
          + exit);
      return false;
    } catch (IllegalAccessException exia) {
      System.err.println("T3) AgentSubject.incrementBeliefEntityPropVal: "
          + exia);
      return false;
    }

    Number newValue = (Number) result;

    // calculate the new value for the property
    if (primitives.get(value.getClass().getSimpleName()) == null) {
      System.err
          .println("T4) AgentSubject.incrementBeliefEntityPropVal: Cannot use + operator for type "
              + value.getClass());
    } else if (value.getClass().getSimpleName().equals(
        Float.class.getSimpleName())
        || value.getClass().getSimpleName()
            .equals(Double.class.getSimpleName())) {
      // call update with new computed value
      this.updateBeliefEntityProperty(id, propertyName,
          (newValue.doubleValue() + value.doubleValue()));
    } else {
      // call update with new computed value
      this.updateBeliefEntityProperty(id, propertyName,
          (newValue.longValue() + value.longValue()));
    }

    // call to the required method was successful
    return true;
  }

  /**
   * Decrements the value of the property with the specified value. If the
   * property has a numeric type, then increment use the - operator to decrease
   * the property's value with the specified value. If the value is not of
   * numeric type, a warning is shown, but the program continue to work
   * 
   * @param id
   *          the Id of the belief
   * @param propertyName
   *          the property name (which needs to be updated)
   * @param value
   *          the value to be subtracted
   * @return true if operation is successful, false otherwise
   */
  public boolean decrementBeliefEntityPropVal(long id, String propertyName,
      Number value) {
    Entity belief = this.getBeliefEntityById(id);
    Method method;
    String methodName = "get" + propertyName.substring(0, 1).toUpperCase()
        + (propertyName.length() > 1 ? propertyName.substring(1) : "");

    Class<?> parameterType = value.getClass();
    Map<String, Class<?>> primitives = new HashMap<String, Class<?>>();

    primitives.put("Byte", Long.TYPE);
    primitives.put("Char", Long.TYPE);
    primitives.put("Short", Long.TYPE);
    primitives.put("Integer", Long.TYPE);
    primitives.put("Long", Long.TYPE);
    primitives.put("Float", Double.TYPE);
    primitives.put("Double", Double.TYPE);

    Object result = null;

    // obtain the right method to be called
    try {
      method = belief.getClass().getMethod(methodName);
      method.setAccessible(true);
    } catch (NoSuchMethodException exnm) {
      try {
        method = belief.getClass().getMethod(methodName,
            primitives.get(parameterType.getSimpleName()));
        method.setAccessible(true);
      } catch (NoSuchMethodException exnm1) {
        System.err.println("T1) AgentSubject.decrementBeliefEntityPropVal: "
            + exnm);
        return false;
      }
    }

    try {
      result = method.invoke(belief);
    } catch (InvocationTargetException exit) {
      System.err.println("T2) AgentSubject.decrementBeliefEntityPropVal: "
          + exit);
      return false;
    } catch (IllegalAccessException exia) {
      System.err.println("T3) AgentSubject.decrementBeliefEntityPropVal: "
          + exia);
      return false;
    }

    Number newValue = (Number) result;

    // calculate the new value for the property
    if (primitives.get(value.getClass().getSimpleName()) == null) {
      System.err
          .println("T4) AgentSubject.decrementBeliefEntityPropVal: Cannot use - operator for type "
              + value.getClass());
    } else if (value.getClass().getSimpleName().equals(
        Float.class.getSimpleName())
        || value.getClass().getSimpleName()
            .equals(Double.class.getSimpleName())) {
      // call update with new computed value
      this.updateBeliefEntityProperty(id, propertyName,
          (newValue.doubleValue() - value.doubleValue()));
    } else {
      // call update with new computed value
      this.updateBeliefEntityProperty(id, propertyName,
          (newValue.longValue() - value.longValue()));
    }

    // call to the required method was successful
    return true;
  }

  /**
   * Query beliefs using SPARQL query.
   * 
   * @param queryString
   *          The string containing the query
   * @return A hash map with all solutions.
   */
  public List<HashMap<String, String>> executeQuery(String queryString) {
    List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
    try {
      result = this.executeQuery(QueryEngine.ENGINE_NAME, queryString);
    } catch (NoClassDefFoundError e) {
      System.out
          .println("Warning: the distribution does not contains libraries for using SPARQL query! All queries results will be empty!");
    }

    return result;
  }

  /***********************************************************************
   ***********************************************************************
   ***********************************************************************/

  /**
   * Sets new perception events for the AgentSubject.
   * 
   * @param perceptionEvents
   *          New perception events
   */
  public void setNewEvents(List<PerceptionEvent> perceptionEvents) {
    if (this.controller != null) {
      this.controller.setNewPerceptionEvents(perceptionEvents);
    }
    this.perceptionEvents = perceptionEvents;
  } // setNewEvents

  /**
   * Sets the current simulation step.
   * 
   * @param currentSimulationStep
   */
  public void setCurrentSimulationStep(long currentSimulationStep) {
    this.currentSimulationStep = currentSimulationStep;
  } // setCurrentSimulationStep

  /**
   * Returns the list of resulting action events.
   * 
   * @return list of resulting action events
   */
  public List<ActionEvent> getActionEvents() {
    return this.resultingActionEvents;
  } // getActionEvents

  /**
   * Processes incoming perception and internal events. The processing
   * eventually generates action events.
   */
  public void run() {
    if (this.isLogGenerationEnabled()) {
      this.jsonGen.reset();
      this.jsonGen.notifyAgentSubject(this);
    }
    if (this.isJaxbLogGenerationEnabled()) {
      this.jaxbLogGenerator.notifyAgentSimulatorStepStart();
    }

    this.resultingActionEvents = new ArrayList<ActionEvent>();

    processActualPerceptions();

    // send perceptions to memory
    this.storeMemories(perceptionEvents);

    processAndRemovePerceptions();

    processAndRemoveInternalEvents();

    // deleteProcessedInternalEvents();

    this.internalEvents.addAll(this.newInternalEvents);
    this.newInternalEvents.clear();
  } // run

  /**
   * Store the PerceptionEvents in the Memory if exists.
   * 
   * @param ArrayList
   *          of perceptionEvents
   * @param currentSimulationStep
   */
  protected void storeMemories(List<PerceptionEvent> perceptionEvents) {
    if (this.agentMemory != null) {// if an agentMemory is set
      List<AtomicEvent> atomicEvents = new ArrayList<AtomicEvent>(
          this.internalEvents);
      atomicEvents.addAll(perceptionEvents);
      this.agentMemory.storeMemories(atomicEvents, this.currentSimulationStep);
    }
  } // storeMemories

  /**
   * Process Perceptions with ActualPerceptionRules.
   */
  private void processActualPerceptions() {
    Iterator<PerceptionEvent> perceptionEventIterator = perceptionEvents
        .iterator();

    while (perceptionEventIterator.hasNext()) {
      PerceptionEvent perceptionEvent = perceptionEventIterator.next();
      if (perceptionEvent.getOccurrenceTime() == currentSimulationStep) {
        if (this.processActualPerception(perceptionEvent)) {
          perceptionEventIterator.remove();
        }
      }
    } // while
  } // processActualPerceptions

  /**
   * 
   * Usage:
   * 
   * Comments:
   * 
   * @param perceptionEvent
   */
  public boolean processActualPerception(PerceptionEvent perceptionEvent) {

    String perceptionEventSimpleName;
    String actPercRuleTriggeringEventSimpleName;
    boolean isProceeded = false;

    perceptionEventSimpleName = perceptionEvent.getClass().getSimpleName();

    for (ActualPerceptionRule actPercRule : this.getActualPerceptionRules()) {

      actPercRuleTriggeringEventSimpleName = actPercRule
          .getTriggeringEventType();

      if (perceptionEventSimpleName
          .equals(actPercRuleTriggeringEventSimpleName)) {
        // event matching was performed and concrete instance is set to
        // the rule
        actPercRule.setTriggeringEvent(perceptionEvent);
        InternalEvent event = actPercRule.resultingInternalEvent();

        if (event != null) {
          this.internalEvents.add(event);
        }
        isProceeded = true;
      }
    }
    return isProceeded;
  } // processActualPerception

  /**
   * Process Perceptions by evaluating AgentRules.
   */
  private void processAndRemovePerceptions() {
    // cycle through all perception events
    Iterator<PerceptionEvent> perceptionEventIterator = perceptionEvents
        .iterator();
    while (perceptionEventIterator.hasNext()) {
      PerceptionEvent perceptionEvent = perceptionEventIterator.next();
      long perceptionEventOccurrenceTime = perceptionEvent.getOccurrenceTime();
      if (perceptionEventOccurrenceTime == currentSimulationStep) {
        this.processPerceptionEvent(perceptionEvent);
        perceptionEventIterator.remove();
      } else if (perceptionEventOccurrenceTime < currentSimulationStep) {
        System.err.println("Warnung: Preceding perception in Agent["
            + this.getId() + "]!");
        perceptionEventIterator.remove();
      }
    } // while
  } // processPerceptions

  /**
   * Process a single PerceptionEvent by evaluating AgentRules.
   * 
   * @param perceptionEvent
   * @return List<ActionEvent> a list of resulting ActionEvents
   */
  private void processPerceptionEvent(PerceptionEvent perceptionEvent) {
    List<ActionEvent> resultingEventList = new ArrayList<ActionEvent>();
    Iterator<ReactionRule> agentRuleIterator;
    ReactionRule reactionRule;
    String agentRuleTriggeringEventSimpleName;
    String perceptionEventSimpleName;
    List<ActionEvent> agentRuleResult;

    if (this.isJaxbLogGenerationEnabled()) {
      this.jaxbLogGenerator.notifyPerceptionEvent(perceptionEvent);
    }

    perceptionEventSimpleName = perceptionEvent.getClass().getSimpleName();

    agentRuleIterator = this.getAgentRules().iterator();
    while (agentRuleIterator.hasNext()) {
      reactionRule = agentRuleIterator.next();
      agentRuleTriggeringEventSimpleName = reactionRule
          .getTriggeringEventType();

      // if its an inmessageEvent, we have to check whether its
      // the correct MessageType to trigger this rule
      boolean skipRule = false;
      if (InMessageEvent.class.isInstance(perceptionEvent)
          && reactionRule.getMessageType() != ""
          && !(((InMessageEvent) perceptionEvent).getMessage()).getClass()
              .getSimpleName().equals(reactionRule.getMessageType()))
        skipRule = true;

      if (!skipRule
          && perceptionEventSimpleName
              .equals(agentRuleTriggeringEventSimpleName)) {
        // event matching was performed and concrete instance is set to
        // the rule
        reactionRule.setTriggeringEvent(perceptionEvent);
        agentRuleResult = executeRule(reactionRule);

        if (this.isJaxbLogGenerationEnabled()) {
          this.jaxbLogGenerator.notifyPerceptionEventResult(reactionRule,
              agentRuleResult);
        }

        // if the produced list contains any elements the content
        // will be added to the list of events that will
        // be sent to SimulationEngine
        resultingEventList.addAll(agentRuleResult);
      }
    }

    if (this.isLogGenerationEnabled()) {
      this.jsonGen.notifyPerception(perceptionEvent, resultingEventList);
    }

    this.resultingActionEvents.addAll(resultingEventList);
  } // processPerceptionEvent

  /**
   * Process internal events, e.g. TimeEvents.
   */
  private void processAndRemoveInternalEvents() {
    // execute rules that are triggered by internalEvents
    Iterator<InternalEvent> internalEventIterator = this.internalEvents
        .iterator();

    while (internalEventIterator.hasNext()) {
      InternalEvent internalEvent = internalEventIterator.next();
      if (internalEvent.getOccurrenceTime() == currentSimulationStep) {
        this.processInternalEvent(internalEvent);
      }
      // is necessary, because the occurrencetime could be incremented
      if (internalEvent.getOccurrenceTime() == currentSimulationStep) {
        internalEventIterator.remove();
      }
    }// end cycling internalEvents

  } // processInternalEvents

  /**
   * Process a single InternalEvent.
   * 
   * @param internalEvent
   * @return
   */
  private void processInternalEvent(InternalEvent internalEvent) {
    Iterator<ReactionRule> agentRuleIterator;
    ReactionRule reactionRule;
    String agentRuleTriggeringEventSimpleName;
    String internalEventSimpleName;
    List<ActionEvent> agentRuleResult;

    // if its a PeriodicTimeEvent, check if stop condition is true
    boolean stop = false;
    if (internalEvent instanceof PeriodicTimeEvent) {
      stop = ((PeriodicTimeEvent) internalEvent).stopCondition();
    }

    if (!stop) {
      agentRuleIterator = this.getAgentRules().iterator();

      while (agentRuleIterator.hasNext()) {
        reactionRule = agentRuleIterator.next();
        internalEventSimpleName = internalEvent.getClass().getSimpleName();
        agentRuleTriggeringEventSimpleName = reactionRule
            .getTriggeringEventType();

        if (internalEventSimpleName.equals(agentRuleTriggeringEventSimpleName)) {
          // event matching was performed and concrete instance is set
          // to the rule
          reactionRule.setTriggeringEvent(internalEvent);
          agentRuleResult = executeRule(reactionRule);

          // if the produced list contains any elements the content
          // will be added to the list of events that will
          // be sent to SimulationEngine

          this.resultingActionEvents.addAll(agentRuleResult);
        }
      }

      // set the next occurrencetime for PeriodicTimeEvent
      if (internalEvent instanceof PeriodicTimeEvent && !stop) {
        PeriodicTimeEvent ptevt = (PeriodicTimeEvent) internalEvent;
        internalEvent.setOccurrenceTime(ptevt.getOccurrenceTime()
            + ptevt.periodicity());
      }
    }
  } // processInternalEvent

  /**
   * TODO check comment (the super class is not "Rule") Overrides method {@code
   * executeRule} from super class rule execution might compute also
   * internalEvents. these events are added directly to the AgentSubject's list
   * of internal events
   * 
   * @param rule
   * @return a list of computed ActionEvents or an empty list
   */
  public List<ActionEvent> executeRule(ReactionRule rule) {

    List<ActionEvent> actions = new ArrayList<ActionEvent>();

    // executes the rule if and only if the rule is not suspended
    if (this.controller == null || !this.controller.ruleIsSuspended(rule)) {

      // execute the rule - conditions are checked inside the rule
      rule.execute();

      // extract resulting internal events
      List<? extends InternalEvent> resultingInternalEvents;
      resultingInternalEvents = rule.getResultingInternalEvents();

      this.newInternalEvents.addAll(resultingInternalEvents);

      /**
       * clear the internal events list for this rule already stored in the
       * internalEvents.
       * 
       * If not cleared, on the next step, we will have also internal events
       * from the previous step
       */
      rule.resultingInternalEvents.clear();

      /**
       * the use of generic type <?> in ReactionRule class as return type for
       * resultingActionEvents implies a cast here. this is to achieve the
       * non-physical agents feature the cast rises warning s for unchecked
       * cast, and for this reason the block has been surrounded by try catch
       * block in case of exception an empty list is returned.
       */
      try {
        actions.addAll((List<? extends ActionEvent>) rule
            .getResultingActionEvents());
        /**
         * clear the action events list for this rule already returned.
         * 
         * If not cleared, on the next step, we will have also action events
         * from the previous step
         */
        rule.resultingActionEvents.clear();

      } catch (ClassCastException e) {
        e.printStackTrace();
        return new ArrayList<ActionEvent>();
      } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<ActionEvent>();
      }
    }

    // return actions for this rules
    return actions;
  }

  /**
   * Return Json encoded data about what the agent has done in the last step.
   * 
   * @return JsonData encapsulated log information
   */
  public JsonData getStepLog() {
    if (this.isLogGenerationEnabled())
      return this.jsonGen.getJson();
    return null;
  } // getStepLog

  public AgentSimulatorStep getJaxbStepLog() {
    if (this.isJaxbLogGenerationEnabled())
      return this.jaxbLogGenerator.getAgentSimulatorStep();
    return null;
  }

  /**
   * Notifies the AgentSubject that it was removed from the simulation
   */
  public void notifyRemoval() {
    // TODO Is there something to do here?
  } // notifyRemoval

  @Override
  public void activateMonitoring() {
    // System.out.println("Monitoring activated for " + this.getName());
  }

  @Override
  public void acceptChanges() {
    // System.out.println("Changes accepted for " + this.getName());
  }

  @Override
  public void rejectChanges() {
    // System.out.println("Changes rejected for " + this.getName());
  }

  public void setAgentSimulator(AgentSimulator simulator) {
    this.simulator = simulator;
  }

  public AgentController getAgentController() {
    return this.controller;
  }

  protected void setController(AgentController controller) {
    this.controller = controller;
  }

  public Map<String, Object> getBeliefProperties() {
    return new HashMap<String, Object>();
  }

  public boolean isControllable() {
    return this.controller != null;
  }

  public boolean isControlled() {
    return this.isControllable() && this.controller.agentIsControlled();
  }

  
	/**
	 * This class represents the core part of the agent controller. To have access
	 * to some private methods and field of the agent subject it is designed as an
	 * inner class of agent subject.
	 * @author Thomas Grundmann
	 */
	public static abstract class AgentController implements CoreAgentController {

		/**
		 * Reference to its enclosing agent subject
		 */
    private AgentSubject agentSubject;

		/**
		 * Reference to its counterpart in the agent control module.
		 */
		private ModuleAgentController moduleAgentController;

		/**
		 * Indicates if the agent that belogs to this controller is controlled by an
		 * user.
		 */
    protected boolean agentIsControlled;

		/**
		 * Set of rule name for the rules that are suspended if the agent is
		 * controlled by an user.
		 */
		protected Set<String> suspendedRules;

		/**
		 * Set of key events on that the agent control gui reacts.
		 * This set is filled by the concrete agent controller that is generated by
		 * the code generation.
		 */
		private Set<Pair<String, String>> keyEvents;

		/**
		 * Set of mouse events on that the agent control gui reacts.
		 * This set is filled by the concrete agent controller that is generated by
		 * the code generation.
		 */
		private Map<String, Set<Pair<String, String>>> mouseEvents;

		private Set<String> UILanguages;

		private String defaultUILanguage;

		/*******************/
		/*** constructor ***/
		/*******************/

		/**
		 * Instantiates the abstract part of the core side agent conroller.
		 * When this part is instantiated the {@link AgentControlBroker} will notify
		 * the agent control module about this new controller if and only if the
		 * agent's id is contains in the array of controllable agents or if the
		 * array is empty.
		 * @param agentSubject
		 * @param controllableAgents
		 */
    protected AgentController(AgentSubject agentSubject,
			Long[] controllableAgents) {
      this.agentSubject = agentSubject;
      this.moduleAgentController = null;
			this.agentIsControlled = false;
			this.suspendedRules = new HashSet<String>();
			this.keyEvents = new HashSet<Pair<String, String>>();
			this.mouseEvents = new HashMap<String, Set<Pair<String, String>>>();
			this.UILanguages = new HashSet<String>();
			this.defaultUILanguage = null;
			if((controllableAgents == null) || (controllableAgents.length == 0) ||
				(Arrays.binarySearch(controllableAgents, this.getAgentId()) >= 0)) {
				AgentControlBroker.getInstance().agentControllerInitialized(this);
			}
    }

		/***********************************************************/
		/*** implementation of the CoreAgentController interface ***/
		/***********************************************************/

		@Override
		public void setModuleAgentController(ModuleAgentController moduleAgentController) {
			this.moduleAgentController = moduleAgentController;
		}

		@Override
		public final Long getAgentId() {
			if(this.agentSubject != null) {
				return this.agentSubject.getId();
			}
			return null;
		}

		@Override
		public final String getAgentName() {
			if(this.agentSubject != null) {
				return this.agentSubject.getName();
			}
			return null;
		}

		@Override
		public final String getAgentType() {
			if(this.agentSubject != null) {
				return this.agentSubject.getType();
			}
			return null;
		}

		@Override
		public Set<Pair<String, String>> getKeyEvents() {
			return this.keyEvents;
		}

		@Override
		public Map<String, Set<Pair<String, String>>> getMouseEvents() {
			return this.mouseEvents;
		}

		@Override
    public void setAgentIsControlled(boolean agentIsControlled) {
			this.agentIsControlled = agentIsControlled;
			if (this.agentSubject.simulator != null) {
				this.agentSubject.simulator.setAgentIsControlled();
			}
    }

		@Override
		public void processInternalEvent(String eventName, Map<String, String> eventData) {
      this.agentSubject.processInternalEvent(this.createEvent(eventName, eventData));
    }

		/*****************************************************/
		/*** methods that are just used by core components ***/
		/*****************************************************/

		public boolean agentIsControlled() {
      return this.agentIsControlled;
    }

		public boolean ruleIsSuspended(ReactionRule reactionRule) {
			return this.agentIsControlled && this.suspendedRules.contains(
				reactionRule.getName());
		}

		public void setNewPerceptionEvents(List<PerceptionEvent> perceptionEvents) {
			if(this.moduleAgentController != null) {
				this.moduleAgentController.setNewPerceptionEvents(perceptionEvents);
			}
		}

    public void performUserActions() {
			if(this.moduleAgentController != null) {
				this.moduleAgentController.performUserActions();
			}
		}

		public void updateView() {
			if(this.moduleAgentController != null) {
				this.moduleAgentController.updateView(this.agentSubject.getBeliefProperties());
			}
		}

		@Override
		public Set<String> getUILanguages() {
			return this.UILanguages;
		}

		@Override
		public String getDefaultUILanguage() {
			return this.defaultUILanguage;
		}

		/************************************************************************/
		/*** methods that are just used by the concrete core agent controller ***/
		/************************************************************************/

		protected long getCurrentSimulationStep() {
      return this.agentSubject.currentSimulationStep;
    }

		protected void addKeyEvent(String keyName, String action) {
			this.keyEvents.add(new Pair<String, String>(keyName, action));
		}

		protected void addMouseEvent(String sender, String eventType, String action) {
			if(!this.mouseEvents.containsKey(sender)) {
				this.mouseEvents.put(sender, new HashSet<Pair<String, String>>());
			}
			this.mouseEvents.get(sender).add(new Pair<String, String>(eventType, action));
		}

		protected abstract InternalEvent createEvent(	String eventName,
			Map<String, String> eventData);

		protected void addUILanguage(String language) {
			this.UILanguages.add(language);
			if(this.defaultUILanguage == null) {
				this.defaultUILanguage = language;
			}
		}
	}
}