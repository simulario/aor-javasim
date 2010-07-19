/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner, Mircea Diaconescu
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
 **************************************************************************************************************/
package aors.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NameNotFoundException;

import aors.GeneralSpaceModel;
import aors.data.DataBus;
import aors.data.DataBusInterface;
import aors.data.evt.sim.SimulationStepEvent;
import aors.model.agtsim.AgentSubject;
import aors.model.agtsim.sim.AgentSimulator;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.AgentObject;
import aors.model.envsim.Objekt;
import aors.statistics.AbstractObjectPropertyStatisticVariable;
import aors.statistics.AbstractResourceUtilizationStatisticVariable;
import aors.statistics.AbstractStatisticsVariable;
import aors.statistics.AbstractObjectPropertyStatisticVariable.AbstractObjektIDRefPropertyIterator;
import aors.statistics.AbstractObjectPropertyStatisticVariable.AbstractPropertyIterator;
import aors.statistics.AbstractObjectPropertyStatisticVariable.ObjektIdPropertyData;

/**
 * This objects contains necessarily data about the initial simulation state.
 * 
 * NOTE: do not change the fields form this class since some of them are
 * referenced via reflection, therefore without a right check this will lead in
 * logical and runtime errors.
 * 
 * 
 * @author Mircea Diaconescu
 * @since July 26, 2009
 * @version $Revision$
 */
public class InitialState {

  /**
   * a reference to the simulator instance - this is used only for internal
   * operations and is not accessible directly from outside
   */
  private AbstractSimulator simulator = null;

  /** the data bus object currently used in the simulator */
  private DataBus databus = null;

  /** the simulation description object */
  private SimulationDescription simulationDescription;

  /** the simulation space model */
  private GeneralSpaceModel spaceModel = null;

  /** the statistic variables reference, via a hash map **/
  private Map<String, AbstractStatisticsVariable> statisticVariables;

  /**
   * This map contains all existing aor-objekts (objekts, physical objects,
   * agents and physical agents) with an ID
   */
  private Map<Long, Objekt> aorObjectsById;

  /**
   * This map contains lists for every user defined aor-objekt type and the
   * abstract types: Objekt, AgentObject, PhysicalObject, PhysicalAgentObject
   * and Physical The lists contains all existing aor-objects The key is the
   * type of the object and the value is the list If exists inheritances, then
   * contains the list for the super class instances the extended instances too
   */
  private Map<String, List<Objekt>> aorObjectsByType;

  /**
   * Get the {@code simulationDescription} field value.
   * 
   * @return the {@code simulationDescription}.
   */
  public SimulationDescription getSimulationDescription() {
    return simulationDescription;
  }

  /**
   * Set the {@code simulationDescription} field value.
   * 
   * @param simulationDescription
   *          the new simulationDescription value.
   */
  public void setSimulationDescription(
      SimulationDescription simulationDescription) {
    this.simulationDescription = simulationDescription;
  }

  /**
   * 
   * Returns a Entity depends from the id; if such Entity not found in
   * EntityList is {@code null} returned
   * 
   * @param id
   *          - the entity ID
   * @return the Entity identified by this {@code id}
   */
  public Objekt getObjektById(long id) {
    return this.aorObjectsById.get(id);
  }

  /**
   * Returns an list of entities for the specified type
   * 
   * @param type
   *          - the entity type
   * @return the list with entities of the specified type
   */
  public List<Objekt> getObjectsByType(Class<?> type) {

    if (this.aorObjectsByType.containsKey(type.getSimpleName())) {
      return this.aorObjectsByType.get(type.getSimpleName());
    }
    return new ArrayList<Objekt>();
  }

  /**
   * Get an agent subject for the given ID.
   * 
   * @param id
   *          the Id of the AgentSubject
   * @return the agent object for the given ID or null if an agent subject is
   *         not found for that ID
   */
  public AgentSubject getAgentSubjectById(long id) {
    List<AgentSimulator> agtSimulators = this.simulator.getAgentSimulators();
    AgentSubject agentSubject = null;

    // find the right agent subject.
    for (AgentSimulator agtSim : agtSimulators) {
      if(agtSim.getAgentId() == id) {
        agentSubject = agtSim.getAgentSubject();
        break;
      }
    }

    return agentSubject;
  }

  /**
   * Returns an list of entities for the specified type
   * 
   * @param type
   *          - the entity type as string
   * @return the list with entities of the specified type
   */
  public List<Objekt> getObjectsByType(String type) {

    if (this.aorObjectsByType.containsKey(type)) {
      return this.aorObjectsByType.get(type);
    }
    return new ArrayList<Objekt>();
  }

  /**
   * Gets the value for the statistic variable which have the given name
   * 
   * @param statisticVarName
   *          the name of the statistic variable
   * @return the value converted to float
   */
  public float getStatisticVariableValueFloat(String statisticVarName)
      throws NameNotFoundException {

    if (statisticVariables.get(statisticVarName) == null) {
      throw new NameNotFoundException("The given statistic variable name: "
          + statisticVarName + " does not exist or variable is null!");
    }
    return statisticVariables.get(statisticVarName).getValue().floatValue();
  }

  /**
   * Gets the value for the statistic variable which have given name and have to
   * compute first (e.g. for Source/ValueExpr)
   * 
   * @param statisticVarName
   *          - the name of the statistic variable
   * @return the computed value of this statistic variable as a float.
   * @throws NameNotFoundException
   */
  public float getStatisticVariableComputedValueFloat(String statisticVarName)
      throws NameNotFoundException {

    AbstractStatisticsVariable abstractStatisticsVariable = statisticVariables
        .get(statisticVarName);

    if (abstractStatisticsVariable == null) {
      throw new NameNotFoundException("The given statistic variable name: "
          + statisticVarName + " does not exist or variable is null!");
    }

    abstractStatisticsVariable.computeVar();

    return abstractStatisticsVariable.getValue().floatValue();
  }

  /**
   * Gets the value for the statistic variable which have the given name
   * 
   * @param statisticVarName
   *          the name of the statistic variable
   * @return the value converted to long
   */
  public long getStatisticVariableValueLong(String statisticVarName)
      throws NameNotFoundException {

    if (statisticVariables.get(statisticVarName) == null) {
      throw new NameNotFoundException("The given statistic variable name: "
          + statisticVarName + " does not exist or variable is null!");
    }
    return statisticVariables.get(statisticVarName).getValue().longValue();
  }

  /**
   * Gets the value for the statistic variable which have given name and have to
   * compute first (e.g. for Source/ValueExpr)
   * 
   * @param statisticVarName
   *          - the name of the statistic variable
   * @return the computed value of this statistic variable as a long.
   * @throws NameNotFoundException
   */
  public long getStatisticVariableComputedValueLong(String statisticVarName)
      throws NameNotFoundException {

    AbstractStatisticsVariable abstractStatisticsVariable = statisticVariables
        .get(statisticVarName);

    if (abstractStatisticsVariable == null) {
      throw new NameNotFoundException("The given statistic variable name: "
          + statisticVarName + " does not exist or variable is null!");
    }

    abstractStatisticsVariable.computeVar();

    return abstractStatisticsVariable.getValue().longValue();
  }

  /**
   * Gets the value for the ResourceUtilizationStatisticVariable which have the
   * given name
   * 
   * @param statisticVarName
   *          * the name of the ResourceUtilizationStatisticVariable
   * @param step
   *          * current step
   * @return the value of a ResourceUtilizationStatisticVariable at given
   *         simulation step
   * @throws NameNotFoundException
   */
  public float getResourceUtilization(String statisticVarName,
      SimulationStepEvent step) throws NameNotFoundException {
    if (statisticVariables.get(statisticVarName) == null) {
      throw new NameNotFoundException("The given statistic variable name: "
          + statisticVarName + " does not exist or variable is null!");
    }
    return ((AbstractResourceUtilizationStatisticVariable) statisticVariables
        .get(statisticVarName))
        .getValue(step.getSimulationStep().getStepTime()).floatValue();
  }

  /**
   * Gets the value-list for the ObjectPropertyStatisticVariable which have the
   * given name
   * 
   * @param statisticVarName
   *          the name of the ObjectPropertyStatisticVariable
   * @return the value-list
   * @throws NameNotFoundException
   */
  public List<Double> getObjectPropertyIteration(String statisticVarName)
      throws NameNotFoundException {
    if (statisticVariables.get(statisticVarName) == null) {
      throw new NameNotFoundException("The given statistic variable name: "
          + statisticVarName + " does not exist or variable is null!");
    }
    List<Double> list = new ArrayList<Double>();
    AbstractPropertyIterator iterator = ((AbstractObjectPropertyStatisticVariable) statisticVariables
        .get(statisticVarName)).getPropertyIterator();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return list;
  }

  /**
   * Gets a map with idRef - property value pairs
   * 
   * @param statisticVarName
   * @return
   * @throws NameNotFoundException
   */
  public Map<Long, Double> getObjektIDRefPropertyIteration(
      String statisticVarName) throws NameNotFoundException {
    if (statisticVariables.get(statisticVarName) == null) {
      throw new NameNotFoundException("The given statistic variable name: "
          + statisticVarName + " does not exist or variable is null!");
    }
    Map<Long, Double> map = new HashMap<Long, Double>();
    AbstractObjektIDRefPropertyIterator iterator = ((AbstractObjectPropertyStatisticVariable) statisticVariables
        .get(statisticVarName)).getObjektIDRefPropertyIterator();
    while (iterator.hasNext()) {
      ObjektIdPropertyData oIdData = iterator.next();
      map.put(oIdData.getIdRef(), oIdData.getPropertyValue());
    }
    return map;
  }

  /**
   * Gets the number of instances of a specified type existing in
   * EnvironmentSimulator
   * 
   * @param name
   *          the object type name as String
   * @return the number of existing instances of this type
   */
  public int getInstancesNumberForType(String name) {
    if (this.aorObjectsByType.get(name) == null) {
      return 0;
    }

    return this.aorObjectsByType.get(name).size();
  }

  /**
   * Gets the number of instances of a specified type existing in
   * EnvironmentSimulator
   * 
   * @param Objekt
   *          the object type name as AOR Objekt
   * @return the number of existing instances of this type
   */
  public int getInstancesNumberForType(Objekt objekt) {
    if (this.aorObjectsByType.get(objekt.getClass().getSimpleName()) == null) {
      return 0;
    }
    return this.aorObjectsByType.get(objekt.getClass().getSimpleName()).size();
  }

  /**
   * @return the databus
   */
  public DataBus getDatabus() {
    return databus;
  }

  /**
   * @return the space model
   */
  public GeneralSpaceModel getSpaceModel() {
    return this.spaceModel;
  }

  /**
   * 
   * Initialization of the map containing statistic variables.
   * 
   * NOTE: this method has to be called only by the AbstractSimulator!
   * 
   * @param statisticVariables
   */
  protected void setStatisticVariables(
      Map<String, AbstractStatisticsVariable> statisticVariables) {
    this.statisticVariables = statisticVariables;
  }

  /**
   * Set the new data bus object used in the case that the modules needs to get
   * access to it.
   * 
   * @param databus
   *          The {@code databus} to set.
   */
  public void setDatabus(DataBusInterface databus) {
    if (this.databus == null)
      this.databus = (DataBus) databus;
  }

  /**
   * Set the {@code aorObjectsById} field value.
   * 
   * NOTE: this method has to be called only by the AbstractSimulator!
   * 
   * @param aorObjectsById
   *          the new aorObjectsById value.
   */
  protected void setAorObjectsById(Map<Long, Objekt> aorObjectsById) {
    this.aorObjectsById = aorObjectsById;
  }

  /**
   * Set the {@code aorObjectsByType} field value.
   * 
   * NOTE: this method has to be called only by the AbstractSimulator!
   * 
   * @param aorObjectsByType
   *          the new aorObjectsByType value.
   */
  protected void setAorObjectsByType(Map<String, List<Objekt>> aorObjectsByType) {
    this.aorObjectsByType = aorObjectsByType;
  }

  /**
   * Set the {@code spaceModel} field value.
   * 
   * NOTE: this method has to be called only by the AbstractSimulator!
   * 
   * @param spaceModel
   *          the new spaceModel value.
   */
  protected void setSpaceModel(GeneralSpaceModel spaceModel) {
    this.spaceModel = spaceModel;
  }

  /**
   * Delete an object from the simulator by a given object ID.
   * 
   * @param id
   *          the id of the object to be deleted
   * @return true if an entity with the given is found and deleted
   * 
   */
  public boolean deleteAORObjektById(long id) {
    boolean check = (this.aorObjectsById.get(id) != null);

    if (check) {
      this.aorObjectsById.remove(id);
      return true;
    }

    return false;
  }

  /**
   * Add an AORS-Agent (subjective and objective parts) to the system.
   * 
   * @param agentObject
   *          the objective agent
   * @param agentSubject
   *          the subjective agent
   * @return true if the add is performed successfully, false otherwise
   */
  public boolean addAgent(AgentObject agentObject, AgentSubject agentSubject) {

    if (agentObject == null || agentSubject == null) {
      return false;
    }

    return (this.simulator.getEnvironmentSimulator().addAgent(agentObject) && this.simulator
        .addAgentSubject(agentSubject));

  }

  /**
   * Add an AORS-Objekt to the system.
   * 
   * @param objekt
   *          the object to add
   * @return true if the add is successful, false otherwise
   */
  public boolean addObjekt(Objekt objekt) {
    if (objekt == null) {
      return false;
    }

    return this.simulator.getEnvironmentSimulator().addObjekt(objekt);
  }

  /**
   * Add an AORS-EnvironmentEvent to the system.
   * 
   * @param environmentEvent
   *          the environment event to add
   * @return true if the add is successful, false otherwise
   */
  public boolean addEnvironmentEvent(EnvironmentEvent environmentEvent) {
    if (environmentEvent == null) {
      return false;
    }
    return this.simulator.environmentEvents.add(environmentEvent);
  }

  /**
   * Return all the environments events from the system
   * 
   * @return the environment events
   */
  public List<EnvironmentEvent> getEvents() {
    return this.simulator.environmentEvents;
  }

  /**
   * Remove an a given environment event from the simulator
   * 
   * @param environmentEvent
   *          the environment event instance to remove
   * @return true if environment event was found and removed, false otherwise
   */
  public boolean deleteEnvironmentEvent(EnvironmentEvent environmentEvent) {
    if (environmentEvent == null) {
      return false;
    }
    return this.simulator.environmentEvents.remove(environmentEvent);
  }

  /**
   * Set the reference to the abstract simulator implementation.
   * 
   * @param simulator
   *          the reference to the AbstractSimulator implementation
   */
  protected void setSimulator(AbstractSimulator simulator) {
    this.simulator = simulator;
  }

  /**
   * Get the class context for an given packageAndClass name. This is required
   * for being able to create any dynamic instances inside the simulator from
   * outside (modules like InitialStateUI module)
   * 
   * @param packageAndClassName
   * @return
   */
  public Class<?> classForName(String packageAndClassName) {
    try {
      return this.simulator.getClass().getClassLoader().loadClass(
          packageAndClassName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

}
