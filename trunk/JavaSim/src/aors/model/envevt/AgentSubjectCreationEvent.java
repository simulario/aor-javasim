/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
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
 * File: AgentSubjectCreationEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

import aors.model.agtsim.AgentSubject;

/**
 * AgentSubjectCreationEvent
 * 
 * This event extends the abstract class EnvironmentEvent. It is used from the
 * EnvironementSimulator to inform the SimulationEngine about new AgentSubject
 * instances.
 * 
 * It is usually encapsulated in an object of the type
 * EnvironmentSimulatorJavaEvent. The AgentSubject instance is necessary for the
 * SimulationEngine to simulate it with an AgentSimulator.
 * 
 * @author Marco Pehla
 * @since June 4, 2008
 * @version $Revision: 1.0 $
 */
public class AgentSubjectCreationEvent extends EnvironmentEvent {

  private AgentSubject agentSubject;

  /**
   * 
   * Create a new {@code AgentSubjectCreationEvent}.
   * 
   * @param occurrenceTime
   * @param agentSubject
   */
  public AgentSubjectCreationEvent(long occurrenceTime,
      AgentSubject agentSubject) {
    super(occurrenceTime);
    this.agentSubject = agentSubject;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * @return the {@code AgentSubject} instance, encapsulated by the event object
   */
  public AgentSubject getAgentSubject() {
    return this.agentSubject;
  }
}
