/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 *
 * Copyright (C) 2008 AOR Team: Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
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
 **************************************************************************************************************/
package aors.model.envevt;

import aors.model.envsim.Physical;

/**
 * PerceptionEvent
 * 
 * @author Gerd Wagner, Jens Werner
 * @since 31 Juli 2008
 * @version $Revision$
 */
public class PhysicalObjectPerceptionEvent extends PerceptionEvent {

  private String perceivedPhysicalObjectType;

  private long perceivedPhysicalObjectIdRef;
  private Physical perceivedPhysicalObject;

  private double distance;
  private double perceptionAngle;

  public PhysicalObjectPerceptionEvent() {
    super();
  }

  public PhysicalObjectPerceptionEvent(long occurrenceTime, long perceiver,
      String physicalObjectType, double distance) {
    super(occurrenceTime, perceiver);
    this.perceivedPhysicalObjectType = physicalObjectType;
    this.distance = distance;
  }

  public PhysicalObjectPerceptionEvent(long occurrenceTime, long perceiver,
      String physicalObjectType, long physicalObjectId, double distance,
      double perceptionAngle) {
    super(occurrenceTime, perceiver);
    this.perceivedPhysicalObjectType = physicalObjectType;
    this.perceivedPhysicalObjectIdRef = physicalObjectId;
    this.distance = distance;
  }

  /**
   * @return the physicalObjectType
   */
  public String getPerceivedPhysicalObjectType() {
    return perceivedPhysicalObjectType;
  }

  /**
   * @param physicalObjectType
   *          the physicalObjectType to set
   */
  public void setPerceivedPhysicalObjectType(String physicalObjectType) {
    this.perceivedPhysicalObjectType = physicalObjectType;
  }

  /**
   * @return the physicalObject
   */
  public long getPerceivedPhysicalObjectIdRef() {
    return perceivedPhysicalObjectIdRef;
  }

  /**
   * @param physicalObject
   *          the physicalObject to set
   */
  public void setPerceivedPhysicalObjectIdRef(long physicalObjectId) {
    this.perceivedPhysicalObjectIdRef = physicalObjectId;
  }

  /**
   * @return the distance
   */
  public double getDistance() {
    return distance;
  }

  /**
   * @param distance
   *          the distance to set
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * @return the perceptionAngle
   */
  public double getPerceptionAngle() {
    return perceptionAngle;
  }

  /**
   * @param perceptionAngle
   *          the perceptionAngle to set
   */
  public void setPerceptionAngle(double perceptionAngle) {
    this.perceptionAngle = perceptionAngle;
  }

  /**
   * @return the physicalObjectRef
   */
  public Physical getPerceivedPhysicalObject() {
    return perceivedPhysicalObject;
  }

  /**
   * @param physicalObjectRef
   *          the physicalObjectRef to set
   */
  public void setPerceivedPhysicalObject(Physical physicalObject) {
    this.perceivedPhysicalObject = physicalObject;
    this.perceivedPhysicalObjectIdRef = physicalObject.getId();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + this.getId() + ") at "
        + this.getOccurrenceTime() + " for " + this.getPerceiverIdRef()
        + ": perceived " + this.perceivedPhysicalObjectType + "("
        + this.perceivedPhysicalObjectIdRef + ") in distance "
        + this.getDistance() + " at angle " + this.getPerceptionAngle();
  }

}