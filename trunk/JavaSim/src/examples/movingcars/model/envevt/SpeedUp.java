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
 * File: SpeedUp.java
 * 
 * Package: examples.movingcars.model.envevt
 *
 **************************************************************************************************************/
package examples.movingcars.model.envevt;

import aors.model.envevt.ActionEvent;
import aors.model.envsim.PhysicalAgentObject;

/**
 * SpeedUp
 * 
 * @author Jens Werner
 * @since 02.06.2008
 * @version $Revision: 1.0 $
 */
public class SpeedUp extends ActionEvent {

  /*
   * 
   */
  private double velocity;

  public SpeedUp() {
    super();
  }

  /**
   * 
   * Create a new {@code SpeedUp}.
   * 
   * @param occurrenceTime
   * @param senderId
   */
  public SpeedUp(long occurrenceTime, long senderId,
      PhysicalAgentObject agentObject) {
    super(occurrenceTime, senderId, agentObject);
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code velocity}.
   * 
   * 
   * 
   * @return the {@code velocity}.
   */
  public double getVelocity() {
    return velocity;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code velocity}.
   * 
   * 
   * 
   * @param velocity
   *          The {@code velocity} to set.
   */
  public void setVelocity(double velocity) {
    this.velocity = velocity;
  }

}
