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
 * File: PerceptionEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

/**
 * PerceptionEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision: 1.0 $
 */
public abstract class PerceptionEvent extends EnvironmentEvent {

  /**
   * 
   */
  private long perceiverIdRef;

  /**
   * 
   * Create a new PerceptionEvent.
   * 
   */
  protected PerceptionEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code PerceptionEvent}.
   * 
   * @param perceivers
   */
  public PerceptionEvent(long occurrenceTime, long perceiverIdRef) {
    super(occurrenceTime);
    this.perceiverIdRef = perceiverIdRef;
  }

  /**
   * 
   * @param name
   * @param occurrenceTime
   * @param perceivers
   */
  public PerceptionEvent(String name, long occurrenceTime, long perceiverIdRef) {
    super(name, occurrenceTime);
    this.perceiverIdRef = perceiverIdRef;
  }

  /**
   * 
   * @param id
   * @param name
   * @param occurrenceTime
   * @param perceivers
   */
  public PerceptionEvent(long id, String name, long occurrenceTime,
      long perceiverIdRef) {
    super(id, name, occurrenceTime);
    this.perceiverIdRef = perceiverIdRef;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code perceivers}.
   * 
   * 
   * 
   * @return the {@code perceivers}.
   */
  public long getPerceiverIdRef() {
    return this.perceiverIdRef;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + this.getId() + ") at "
        + this.getOccurrenceTime() + " for " + this.perceiverIdRef;
  }

}
