/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
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
 * File: SpeedLimitSign.java
 * 
 * Package: examples.movingcars.model.envsim
 *
 **************************************************************************************************************/
package examples.movingcars.model.envsim;

import java.beans.PropertyChangeEvent;

import aors.model.envsim.PhysicalObject;

/**
 * SpeedLimitSign
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since June 2, 2008
 * @version $Revision$
 */
public class SpeedLimitSign extends PhysicalObject {

  /**
   * 
   */
  private double admMaxVel;

  /**
   * 
   * Create a new SpeedLimitSign.
   * 
   * @param id
   */
  public SpeedLimitSign(long id, double admMaxVel) {
    super(id);
    this.setAdmMaxVel(admMaxVel);
    this.getInheritedProperty().setDoubleProperty("admMaxVel", admMaxVel);
  }

  /**
   * Get the
   * 
   * @code{admMaxVel .
   * 
   * @return the
   * @code{admMaxVel .
   */
  public double getAdmMaxVel() {
    return admMaxVel;
  }

  /**
   * Set the admMaxVel.
   * 
   * @param admMaxVel
   *          The admMaxVel to set.
   */
  public void setAdmMaxVel(double admMaxVel) {
    if (this.admMaxVel != admMaxVel) {
      this.admMaxVel = admMaxVel;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, "admMaxVel", null, this.admMaxVel));
    }
  }

}
