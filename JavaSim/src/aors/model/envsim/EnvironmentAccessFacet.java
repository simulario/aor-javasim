/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2009 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
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
 * File: EnvironmentAccessFacet.java
 * 
 * Package: aors.model.envsim
 *
 **************************************************************************************************************/
package aors.model.envsim;

import java.util.List;

import aors.model.agtsim.AgentSubject;
import aors.util.collection.AORCollection;

/**
 * EnvironmentAccessFacet
 * 
 * Is used to define the method there will be accessible in environment rules
 * 
 * @author Jens Werner
 * @since 09.11.2009
 * @version $Revision$
 */
public interface EnvironmentAccessFacet {

  public boolean createObjekt(Objekt objekt);

  public boolean createAgent(AgentObject agentObject, AgentSubject agentSubject);

  public boolean createPhysicalObject(PhysicalObject object);

  public boolean destroyObject(long id);

  public AgentObject getActivityActorById(long id);

  public Objekt getObjectById(long id);

  public Objekt getObjectById(long id, Class<?> type);

  public List<Objekt> getObjectsByType(Class<?> type);

  public List<Objekt> getObjectsByType(String type);

  public Objekt getObjectByName(String name);

  public AgentObject getAgentById(long id);

  public AgentObject getAgentById(long id, Class<?> type);

  public List<AgentObject> getAgentObjectsByType(Class<?> type);

  public PhysicalAgentObject getPhysAgentById(long id);

  public PhysicalAgentObject getPhysAgentById(long id, Class<?> type);

  public List<PhysicalAgentObject> getPhysAgentObjectsByType(Class<?> type);

  public PhysicalObject getPhysObjectById(long id);

  public PhysicalObject getPhysicalObjectById(long id, Class<?> type);

  public List<PhysicalObject> getPhysicalObjectsByType(Class<?> type);

  public AORCollection<? extends Objekt> getCollectionById(long id);

  public AORCollection<? extends Objekt> getCollectionByName(String name);

  /**
   * Creates an auto-id lower then 0
   * 
   * @return autoId
   */
  public long getAutoId();

  public int getInstancesNumberForType(String name);

  public int getInstancesNumberForType(Objekt objekt);
}
