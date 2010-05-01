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
 * File: ExogenousEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

/**
 * ExogenousEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision$
 */
public abstract class ExogenousEvent extends EnvironmentEvent {

  private long nextOccurrenceTime;

  /**
   * 
   * Create a new {@code ExogenousEvent}.
   * 
   */
  public ExogenousEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code ExogenousEvent}.
   * 
   * @param id
   */
  public ExogenousEvent(long id, long occurrenceTime) {
    super(id, occurrenceTime);
  }

  /**
   * 
   * Create a new {@code ExogenousEvent}.
   * 
   * @param id
   */
  public ExogenousEvent(String name, long occurrenceTime) {
    super(name, occurrenceTime);
  }

  /**
   * 
   * Create a new {@code ExogenousEvent}.
   * 
   * @param occurrenceTime
   */
  public ExogenousEvent(long occurrenceTime) {
    super(occurrenceTime);
  }

  /**
   * 
   * @param id
   * @param name
   * @param occurrenceTime
   */
  public ExogenousEvent(long id, String name, long occurrenceTime) {
    super(id, name, occurrenceTime);
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return periodicity of this event
   */
  public abstract long periodicity();

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return true if condition holds for this event
   */
  public abstract boolean stopCondition();

  /**
   * @return the nextOccurenceTime
   */
  public long getNextOccurrenceTime() {
    return nextOccurrenceTime;
  }

  /**
   * @param nextOccurenceTime
   *          the nextOccurenceTime to set
   */
  public void setNextOccurrenceTime(long nextOccurenceTime) {
    this.nextOccurrenceTime = nextOccurenceTime;
    if (this.stopCondition())
      this.nextOccurrenceTime = 0;
  }

}
