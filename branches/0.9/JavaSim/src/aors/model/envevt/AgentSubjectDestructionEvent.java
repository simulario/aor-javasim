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
 * File: AgentSubjectDestructionEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

/**
 * AgentSubjectDestructionEvent
 * 
 * This event extends the abstract class EnvironmentEvent. It is used from the
 * EnvironementSimulator to inform the SimulationEngine about to destroy the
 * AgentSubject instance, specified by the given ID.
 * 
 * It is usually encapsulated in an object of the type
 * EnvironmentSimulatorJavaEvent.
 * 
 * @author Marco Pehla
 * @since 05.06.2008
 * @version $Revision$
 */
public class AgentSubjectDestructionEvent extends EnvironmentEvent {

  private long agentId;

  /**
   * Create a new {@code AgentSubjectDestructionEvent}.
   * 
   * @param occurrenceTime
   *          the occurence time of this event
   * @param agentId
   *          the Id of the subjective agent, who should be destroyed by the
   *          simulation engine instance
   * 
   */
  public AgentSubjectDestructionEvent(long occurrenceTime, long agentId) {
    super(occurrenceTime);
    this.agentId = agentId;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * @return String the encapsulated agent's ID
   */
  public long getAgentId() {
    return this.agentId;
  }
}
