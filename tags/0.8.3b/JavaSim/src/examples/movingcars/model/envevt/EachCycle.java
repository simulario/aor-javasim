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
 * File: EachCycle.java
 * 
 * Package: examples.movingcars.model.envevt
 *
 **************************************************************************************************************/
package examples.movingcars.model.envevt;

import aors.model.envevt.ExogenousEvent;
import examples.carsandtrucks.controller.SimulationParameters;

/**
 * EachCycle
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 26, 2008
 * @version $Revision$
 */
public class EachCycle extends ExogenousEvent {

  public EachCycle() {
    super();
  }

  /**
   * 
   * Create a new EachCycle.
   * 
   * @param occurrenceTime
   */
  public EachCycle(long occurrenceTime) {
    super(occurrenceTime);
  }

  public EachCycle(String name, long occurrenceTime) {
    super(name, occurrenceTime);
  }

  /**
   * 
   */
  public long periodicity() {
    return (1);
  }

  /**
   * 
   */
  public boolean stopCondition() {
    return (getOccurrenceTime() > SimulationParameters.SIMULATION_STEPS);
  }

}
