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
 * File: ActionEvent.java
 * 
 * Package: aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

import aors.model.envsim.AgentObject;

/**
 * ActionEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner
 * @since May 25, 2008
 * @version $Revision: 1.0 $
 */
public abstract class ActionEvent extends EnvironmentEvent {

  /**
   * actorId is identified by its id;
   */
  private long actorIdRef;

  /**
   * actorRef is a reference to the actor
   */
  private AgentObject actor;

  /**
   * 
   * Create a new ActionEvent.
   * 
   */
  protected ActionEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code ActionEvent}.
   * 
   * @param actorIdRef
   */
  @Deprecated
  public ActionEvent(long occurrenceTime, long actorIdRef) {
    super(occurrenceTime);
    this.actorIdRef = actorIdRef;
  }

  /**
   * 
   * Create a new {@code ActionEvent}.
   * 
   * @param actorIdRef
   * @param acPhysicalAgentObject
   */
  public ActionEvent(long occurrenceTime, long actorIdRef, AgentObject actor) {
    super(occurrenceTime);
    this.actorIdRef = actorIdRef;
    this.actor = actor;
  }

  /**
   * 
   * @param id
   * @param occurrenceTime
   * @param senderId
   */
  public ActionEvent(long id, long occurrenceTime, long senderId) {
    super(id, occurrenceTime);
    this.actorIdRef = senderId;
  }

  public ActionEvent(String name, long occurrenceTime, long senderId) {
    super(name, occurrenceTime);
    this.actorIdRef = senderId;
  }

  /**
   * 
   * @param id
   * @param occurrenceTime
   * @param actorIdRef
   */
  public ActionEvent(long id, String name, long occurrenceTime, long actorIdRef) {
    super(id, name, occurrenceTime);
    this.actorIdRef = actorIdRef;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code senderId}.
   * 
   * 
   * 
   * @return the {@code senderId}.
   */
  public long getActorIdRef() {
    return actorIdRef;
  }

  public AgentObject getActor() {
    return this.actor;
  }

}
