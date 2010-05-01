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
 * File: AtomicEvent.java
 * 
 * Package: info.aors.model
 *
 **************************************************************************************************************/
package aors.model;

/**
 * Event
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision$
 */
public abstract class AtomicEvent extends Event {

  /**
   * 
   * Create a new {@code Event}.
   * 
   */
  protected AtomicEvent() {
    super();// call the constructor of Entity
  }

  /**
   * 
   * Create a new {@code Event}.
   * 
   * @param id
   */
  protected AtomicEvent(long id, long occurrenceTime) {
    super(id, occurrenceTime);// call the constructor of Entity
  }

  /**
   * 
   * Create a new {@code Event}.
   * 
   * @param occurrenceTime
   */
  protected AtomicEvent(long occurrenceTime) {
    super(occurrenceTime);// call the constructor of Event
  }

  protected AtomicEvent(String name, long occurenceTime) {
    super(name, occurenceTime);
  }

  protected AtomicEvent(long id, String name, long occurenceTime) {
    super(id, name, occurenceTime);
  }

  @Override
  public void setDuration(long duration) {
    // do nothing
  }

  @Override
  public long getDuration() {
    return 0;
  }

  @Override
  public void setStartTime(long startTime) {
    // do nothing
  }

  @Override
  public long getStartTime() {
    return this.getOccurrenceTime();
  }

}
