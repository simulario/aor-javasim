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
 * File: EnvironmentEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

import aors.model.AtomicEvent;

/**
 * EnvironmentEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner
 * @since May 25, 2008
 * @version $Revision$
 */
public abstract class EnvironmentEvent extends AtomicEvent {

  private long occurrenceLocX;
  private long occurrenceLocY;
  private long occurrenceLocZ;

  /**
   * 
   * Create a new {@code EnvironmentEvent}.
   * 
   */
  protected EnvironmentEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code EnvironmentEvent}.
   * 
   * @param id
   */
  protected EnvironmentEvent(long id, long occurrenceTime) {
    super(id, occurrenceTime);
  }

  /**
   * 
   * Create a new {@code EnvironmentEvent}.
   * 
   * @param occurrenceTime
   */
  protected EnvironmentEvent(long occurrenceTime) {
    super(occurrenceTime);
  }

  /**
   * 
   * @param occurrenceTime
   * @param name
   */
  protected EnvironmentEvent(String name, long occurrenceTime) {
    super(name, occurrenceTime);
  }

  protected EnvironmentEvent(long id, String name, long occurrenceTime) {
    super(id, name, occurrenceTime);
  }

  /**
   * @return the occurrenceLocX
   */
  public long getOccurrenceLocX() {
    return occurrenceLocX;
  }

  /**
   * @param occurrenceLocX
   *          the occurrenceLocX to set
   */
  public void setOccurrenceLocX(long occurrenceLocX) {
    this.occurrenceLocX = occurrenceLocX;
  }

  /**
   * @return the occurrenceLocY
   */
  public long getOccurrenceLocY() {
    return occurrenceLocY;
  }

  /**
   * @param occurrenceLocY
   *          the occurrenceLocY to set
   */
  public void setOccurrenceLocY(long occurrenceLocY) {
    this.occurrenceLocY = occurrenceLocY;
  }

  /**
   * @return the occurrenceLocZ
   */
  public long getOccurrenceLocZ() {
    return occurrenceLocZ;
  }

  /**
   * @param occurrenceLocZ
   *          the occurrenceLocZ to set
   */
  public void setOccurrenceLocZ(long occurrenceLocZ) {
    this.occurrenceLocZ = occurrenceLocZ;
  }

}
