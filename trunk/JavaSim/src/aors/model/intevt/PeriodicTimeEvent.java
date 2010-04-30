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
 * File: PeriodicEvent.java
 * 
 * Package: info.aors.model.intevt
 *
 **************************************************************************************************************/
package aors.model.intevt;

/**
 * PeriodicEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision: 1.0 $
 */
public abstract class PeriodicTimeEvent extends TimeEvent {

  /**
   * 
   * Create a new {@code PeriodicTimeEvent}.
   * 
   */
  public PeriodicTimeEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code PeriodicTimeEvent}.
   * 
   * @param id
   */
  public PeriodicTimeEvent(long id, long occurenceTime) {
    super(id, occurenceTime);
  }

  /**
   * 
   * Create a new {@code PeriodicTimeEvent}.
   * 
   * @param occurenceTime
   */
  public PeriodicTimeEvent(long occurenceTime) {
    super(occurenceTime);
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
   * @return event periodicity
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
   * @return true if condition holds; otherwise false;
   */
  public abstract boolean stopCondition();

}
