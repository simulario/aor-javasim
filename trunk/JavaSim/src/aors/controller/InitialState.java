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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NameNotFoundException;

import aors.GeneralSpaceModel;
import aors.data.DataBus;
import aors.data.DataBusInterface;
import aors.data.java.SimulationStepEvent;
import aors.model.envsim.Objekt;
import aors.statistics.AbstractObjectPropertyStatisticVariable;
import aors.statistics.AbstractResourceUtilizationStatisticVariable;
import aors.statistics.AbstractStatisticsVariable;
import aors.statistics.GeneralStatistics;
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
	 * contains the list for the super class instances the extended instances
	 * too
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
	 *            the new simulationDescription value.
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
	 *            - the entity ID
	 * @return the Entity identified by this {@code id}
	 */
	public Objekt getObjektById(long id) {
		return this.aorObjectsById.get(id);
	}

	/**
	 * Returns an list of entities for the specified type
	 * 
	 * @param type
	 *            - the entity type
	 * @return the list with entities of the specified type
	 */
	public List<Objekt> getObjectsByType(Class<?> type) {

		if (this.aorObjectsByType.containsKey(type.getSimpleName())) {
			return this.aorObjectsByType.get(type.getSimpleName());
		}
		return new ArrayList<Objekt>();
	}

	/**
	 * Returns an list of entities for the specified type
	 * 
	 * @param type
	 *            - the entity type as string
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
	 *            the name of the statistic variable
	 * @return the value converted to float
	 */
	public float getStatisticVariableValueFloat(String statisticVarName)
			throws NameNotFoundException {

		if (statisticVariables.get(statisticVarName) == null) {
			throw new NameNotFoundException(
					"The given statistic variable name: " + statisticVarName
							+ " does not exist or variable is null!");
		}
		return statisticVariables.get(statisticVarName).getValue().floatValue();
	}

	/**
	 * Gets the value for the statistic variable which have given name and have
	 * to compute first (e.g. for Source/ValueExpr)
	 * 
	 * @param statisticVarName
	 *            - the name of the statistic variable
	 * @return the computed value of this statistic variable as a float.
	 * @throws NameNotFoundException
	 */
	public float getStatisticVariableComputedValueFloat(String statisticVarName)
			throws NameNotFoundException {

		AbstractStatisticsVariable abstractStatisticsVariable = statisticVariables
				.get(statisticVarName);

		if (abstractStatisticsVariable == null) {
			throw new NameNotFoundException(
					"The given statistic variable name: " + statisticVarName
							+ " does not exist or variable is null!");
		}

		abstractStatisticsVariable.computeVar();

		return abstractStatisticsVariable.getValue().floatValue();
	}

	/**
	 * Gets the value for the statistic variable which have the given name
	 * 
	 * @param statisticVarName
	 *            the name of the statistic variable
	 * @return the value converted to long
	 */
	public long getStatisticVariableValueLong(String statisticVarName)
			throws NameNotFoundException {

		if (statisticVariables.get(statisticVarName) == null) {
			throw new NameNotFoundException(
					"The given statistic variable name: " + statisticVarName
							+ " does not exist or variable is null!");
		}
		return statisticVariables.get(statisticVarName).getValue().longValue();
	}

	/**
	 * Gets the value for the statistic variable which have given name and have
	 * to compute first (e.g. for Source/ValueExpr)
	 * 
	 * @param statisticVarName
	 *            - the name of the statistic variable
	 * @return the computed value of this statistic variable as a long.
	 * @throws NameNotFoundException
	 */
	public long getStatisticVariableComputedValueLong(String statisticVarName)
			throws NameNotFoundException {

		AbstractStatisticsVariable abstractStatisticsVariable = statisticVariables
				.get(statisticVarName);

		if (abstractStatisticsVariable == null) {
			throw new NameNotFoundException(
					"The given statistic variable name: " + statisticVarName
							+ " does not exist or variable is null!");
		}

		abstractStatisticsVariable.computeVar();

		return abstractStatisticsVariable.getValue().longValue();
	}

	/**
	 * Gets the value for the ResourceUtilizationStatisticVariable which have
	 * the given name
	 * 
	 * @param statisticVarName
	 *            * the name of the ResourceUtilizationStatisticVariable
	 * @param step
	 *            * current step
	 * @return the value of a ResourceUtilizationStatisticVariable at given
	 *         simulation step
	 * @throws NameNotFoundException
	 */
	public float getResourceUtilization(String statisticVarName,
			SimulationStepEvent step) throws NameNotFoundException {
		if (statisticVariables.get(statisticVarName) == null) {
			throw new NameNotFoundException(
					"The given statistic variable name: " + statisticVarName
							+ " does not exist or variable is null!");
		}
		return ((AbstractResourceUtilizationStatisticVariable) statisticVariables
				.get(statisticVarName)).getValue(
				step.getSimulationStep().getStepTime()).floatValue();
	}

	/**
	 * Gets the value-list for the ObjectPropertyStatisticVariable which have
	 * the given name
	 * 
	 * @param statisticVarName
	 *            the name of the ObjectPropertyStatisticVariable
	 * @return the value-list
	 * @throws NameNotFoundException
	 */
	public List<Double> getObjectPropertyIteration(String statisticVarName)
			throws NameNotFoundException {
		if (statisticVariables.get(statisticVarName) == null) {
			throw new NameNotFoundException(
					"The given statistic variable name: " + statisticVarName
							+ " does not exist or variable is null!");
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
			throw new NameNotFoundException(
					"The given statistic variable name: " + statisticVarName
							+ " does not exist or variable is null!");
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
	 *            the object type name as String
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
	 *            the object type name as AOR Objekt
	 * @return the number of existing instances of this type
	 */
	public int getInstancesNumberForType(Objekt objekt) {
		if (this.aorObjectsByType.get(objekt.getClass().getSimpleName()) == null) {
			return 0;
		}
		return this.aorObjectsByType.get(objekt.getClass().getSimpleName())
				.size();
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
	 * @param generalStatistics
	 *            the general statistics object that will be stored by this
	 *            initial state object
	 */
	protected void initStatisticVarsMap(GeneralStatistics generalStatistics) {
		if (this.statisticVariables == null) {
			this.statisticVariables = new HashMap<String, AbstractStatisticsVariable>();
		}
		Field[] declaredFields = generalStatistics.getClass()
				.getDeclaredFields();
		for (Field f : declaredFields) {
			int modifiers = f.getModifiers();
			if (Modifier.isStatic(modifiers)) {
				try {
					Object o = f.get(generalStatistics.getClass());
					if (AbstractStatisticsVariable.class.isInstance(o)) {
						this.statisticVariables.put(f.getName(),
								(AbstractStatisticsVariable) o);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Set the new data bus object used in the case that the modules needs to
	 * get access to it.
	 * 
	 * @param databus
	 *            The {@code databus} to set.
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
	 *            the new aorObjectsById value.
	 */
	protected void setAorObjectsById(Map<Long, Objekt> aorObjectsById) {
		this.aorObjectsById = aorObjectsById;
	}

	/**
	 * Delete an object from the simulator by a given object ID.
	 * 
	 * @param id
	 *            the id of the object to be deleted
	 * 
	 */
	public void deleteObjectById(long id) {

	}

	/**
	 * Set the {@code aorObjectsByType} field value.
	 * 
	 * NOTE: this method has to be called only by the AbstractSimulator!
	 * 
	 * @param aorObjectsByType
	 *            the new aorObjectsByType value.
	 */
	protected void setAorObjectsByType(
			Map<String, List<Objekt>> aorObjectsByType) {
		this.aorObjectsByType = aorObjectsByType;
	}

	/**
	 * Set the {@code spaceModel} field value.
	 * 
	 * NOTE: this method has to be called only by the AbstractSimulator!
	 * 
	 * @param spaceModel
	 *            the new spaceModel value.
	 */
	protected void setSpaceModel(GeneralSpaceModel spaceModel) {
		this.spaceModel = spaceModel;
	}
}
