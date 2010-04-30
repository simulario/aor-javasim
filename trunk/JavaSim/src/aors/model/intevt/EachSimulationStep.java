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
 * File: OnEveryStepIntEvent.java
 * 
 * Package: aors.model.intevt
 *
 **************************************************************************************************************/
package aors.model.intevt;

/**
 * OnEveryStepIntEvent
 * 
 * @author Jens Werner
 * @since 07.11.2009
 * @version $Revision: 1.0 $
 */
public class EachSimulationStep extends PeriodicTimeEvent {

  public EachSimulationStep() {
    super();
  }

  public EachSimulationStep(long occurenceTime) {
    super(occurenceTime);
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code periodicity} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public long periodicity() {
    return 1;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code stopCondition} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public boolean stopCondition() {
    return false;
  }

}
