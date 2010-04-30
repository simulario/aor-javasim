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
 * File: Event.java
 * 
 * Package: aors.model
 *
 **************************************************************************************************************/
package aors.model;

/**
 * Event
 * 
 * @author Jens Werner
 * @since 27.04.2009
 * @version $Revision: 1.0 $
 */
public abstract class Event extends Entity {

  /**
   * 
   */
  private long occurrenceTime;

  /**
   * 
   */
  private long startTime;

  /**
   * 
   */
  private long duration = 0;

  protected Event() {
  }

  protected Event(long occurenceTime) {
    super();
    this.occurrenceTime = occurenceTime;
  }

  protected Event(long id, long occurenceTime) {
    super(id);
    this.occurrenceTime = occurenceTime;
  }

  protected Event(String name, long occurenceTime) {
    super();
    this.occurrenceTime = occurenceTime;
    this.setName(name);
  }

  protected Event(long id, String name, long occurenceTime) {
    super(id);
    this.occurrenceTime = occurenceTime;
    this.setName(name);
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code duration}.
   * 
   * 
   * 
   * @return the {@code duration}.
   */
  public long getDuration() {
    return duration;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code duration}.
   * 
   * 
   * 
   * @param duration
   *          The {@code duration} to set.
   */
  public void setDuration(long duration) {
    this.duration = duration;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code occurrenceTime}.
   * 
   * 
   * 
   * @return the {@code occurrenceTime}.
   */
  public long getOccurrenceTime() {
    return occurrenceTime;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code occurrenceTime}. Notice that this method is useful
   * to avoid creation of new objects in memory when only the cocurenceTime
   * might change. For example ExogenousEvents.
   * 
   * @param occurrenceTime
   *          The {@code occurrenceTime} to set.
   */
  public void setOccurrenceTime(long occurrenceTime) {
    this.occurrenceTime = occurrenceTime;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code startTime}.
   * 
   * 
   * 
   * @return the {@code startTime}.
   */
  public long getStartTime() {
    return startTime;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code startTime}.
   * 
   * 
   * 
   * @param startTime
   *          The {@code startTime} to set.
   */
  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  // public long computeStartTime() {
  // return this.occurrenceTime - this.duration;
  // }
  //  
  // public long computeOccurenceTime() {
  // return this.startTime + this.duration;
  // }
  //  
  // public long computeDuration() {
  // return this.occurrenceTime - this.startTime;
  // }

}
