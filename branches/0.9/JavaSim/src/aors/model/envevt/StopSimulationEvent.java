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
 * File: StopSimulationEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

/**
 * StopSimulationEvent
 * 
 * @author Mircea Diaconescu
 * @since November 10, 2008
 * @version $Revision$
 */

public class StopSimulationEvent extends CausedEvent {
  /**
   * Create a new StopSimulationEvent
   */
  public StopSimulationEvent() {
    super();
  }

  /**
   * Create a new StopSimulationEvent
   * 
   * @param id
   *          the Event's ID
   */
  public StopSimulationEvent(long id, long occurrenceTime) {
    super(id, occurrenceTime);
  }

  public StopSimulationEvent(long occurrenceTime) {
    super(occurrenceTime);
  }

  /**
   * Create a new StopSimulationEvent
   * 
   * @param id
   *          the Event's ID
   * @param name
   *          the Event's Name
   * @param occurrenceTime
   *          the Occurrence Time for this Event
   */
  public StopSimulationEvent(long id, String name, long occurrenceTime) {
    super(id, name, occurrenceTime);
  }

  /**
   * Create a new StopSimulationEvent
   * 
   * @param name
   *          the Event's Name
   * @param occurrenceTime
   *          the Occurrence Time for this Event
   */
  public StopSimulationEvent(String name, long occurrenceTime) {
    super(name, occurrenceTime);
  }

}