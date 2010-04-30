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
 * File: ActivityEndEvent.java
 * 
 * Package: aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

import aors.model.envsim.AgentObject;

/**
 * ActivityEndEvent
 * 
 * @author Jens Werner
 * @since 04.05.2009
 * @version $Revision: 1.0 $
 */
public class ActivityStartEvent extends EnvironmentEvent {

  private String activityType;

  private Object __correlationValue = null;

  private AgentObject activityActor = null;

  /**
   * Create a new {@code ActivityStartEvent}.
   * 
   * this constructor is only used to predefine a possible triggering event in
   * rules
   */
  public ActivityStartEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code ActivityStartEvent}.
   * 
   * @param activityType
   * @param occurenceTime
   */
  public ActivityStartEvent(String activityType, long occurenceTime) {
    super(occurenceTime);
    this.activityType = activityType;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code activityType}.
   * 
   * 
   * 
   * @return the {@code activityType}.
   */
  public String getActivityType() {
    return activityType;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code correlationValue}.
   * 
   * 
   * 
   * @return the {@code correlationValue}.
   */
  public Object getCorrelationValue() {
    return __correlationValue;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code correlationValue}.
   * 
   * 
   * 
   * @param correlationValue
   *          The {@code correlationValue} to set.
   */
  public void setCorrelationValue(Object correlationValue) {
    this.__correlationValue = correlationValue;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code activityActor}.
   * 
   * 
   * 
   * @return the {@code activityActor}.
   */
  public AgentObject getActivityActor() {
    return activityActor;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code activityActor}.
   * 
   * 
   * 
   * @param activityActor
   *          The {@code activityActor} to set.
   */
  public void setActivityActor(AgentObject activityActor) {
    this.activityActor = activityActor;
  }

}
