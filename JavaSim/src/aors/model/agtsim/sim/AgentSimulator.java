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
 * File: AgentSimulator.java
 * 
 * Package: info.aors.model.agtsim.sim
 *
 **************************************************************************************************************/
package aors.model.agtsim.sim;

import java.beans.PropertyChangeListener;
import java.util.List;

import aors.model.agtsim.proxy.AgentSubjectProxy;
import aors.model.envevt.PerceptionEvent;
import aors.model.envsim.AgentObject;

/**
 * AgentSimulator Provides an interface for using different implementations in
 * the AbstractSimulator.
 * 
 * @author Emilian Pascalau, Adrian Giurca, Volkmar Kantor, Christian Noack
 * @since May 25, 2008
 * @version $Revision$
 */
public interface AgentSimulator extends Runnable, PropertyChangeListener {

  /**
   * Returns the id of the containing agent.
   * 
   * @return the id of the containing agent
   */
  public long getAgentId();

  /**
   * Returns the name of the simulated AgentSubject
   * 
   * @return name of AgentSubject
   */
  public String getAgentName();

  /**
   * Returns the type of the simulated AgentSubject
   * 
   * @return type of AgentSubject
   */
  public String getAgentType();

  /**
   * In case of externally controlled agents, return the username of the user
   * controlling the agent. This function is only used in WebSim setting for
   * debug output and might be removed without problems.
   * 
   * @return username of controlling user
   */
  public String getUserName();

  /**
   * Sets the BaseURI inside the AgentSubject
   * 
   * @param baseURI
   */
  public void setBaseURI(String baseURI);

  // public boolean executeActualPerceptionRule(PerceptionEvent
  // perceptionEvent);

  /**
   * Sets the class loader to use for constructing specialized ActionEvents from
   * JSON code that was sent from an external agent. This method is used in the
   * WebSim and may not be removed.
   * 
   * @param simClassLoader
   *          The classloader for loading class files of generated source
   */
  public void setSimClassLoader(ClassLoader simClassLoader);

  /**
   * Sets the agent timeout in seconds.
   * 
   * @param agentTimeout
   */
  public void setAgentTimeout(long agentTimeout);

  /**
   * Set the State for one defined SimulationStep. The AgentSimulator / Agent
   * get a list of PerceptionEvents to react. Called by SimulationEngine.run()
   * 
   * @param newStep
   * @param events
   */
  public void setNewEvents(long newStep, List<PerceptionEvent> events);

  /**
   * Sets the AgentSubjectProxy for sending Perceptions to an external
   * AgentSubject
   * 
   * @author noack
   * @param proxy
   */
  public void setAgentSubjectProxy(AgentSubjectProxy proxy);

  /**
   * Returns true if the AgentSubjectProxy is set.
   * 
   * @author noack
   * @return true if the AgentSubjectProxy is set
   */
  public boolean isAgentSubjectProxySet();

  /**
   * Notifies the AgentSimulator that the simulation has ended. Used in the
   * Websimulator to let the externally controlled agent know that the
   * simulation has ended. This method is called from AbstractSimulator.
   * 
   * @author noack
   * @since 20/Aug/2009
   */
  public void notifySimulationEnd();

  /**
   * Notifies the AgentSimulator that it was removed from the simulation. This
   * method is called from AbstractSimulator.
   */
  public void notifyRemoval();

  /**
   * Set the corresponding AgentObject for the simulated AgentSubject
   * 
   * @param agentObject
   */
  public void setCorrespondingAgentObject(AgentObject agentObject);

	/**
	 * Checks if the agent, that belongs to this simulator, can be controlled by
	 * an user.
	 * @return <code>true</code> if the agent can be controlled, otherwise
	 * <code>false</code>
	 */
	public boolean agentIsControllable();

	/**
	 * Checks if the agent, that belongs to this simulator is controlled by an
	 * user.
	 * @return <code>true</code> if the agent is controlled, otherwise
	 * <code>false</code>
	 */
	public boolean agentIsControlled();

	/**
	 * Sets the agents control state. If an agent is controlled is determined by
	 * the agent's subject.
	 */
	public void setAgentIsControlled();

}
