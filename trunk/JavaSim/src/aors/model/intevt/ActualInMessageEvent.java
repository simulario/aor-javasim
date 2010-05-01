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
 * File: ActualInMessageEvent.java
 * 
 * Package: info.aors.model.intevt
 *
 **************************************************************************************************************/
package aors.model.intevt;

import aors.model.Message;

/**
 * ActualInMessageEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision$
 */
public class ActualInMessageEvent extends ActualPerceptionEvent {

  /**
   * 
   */
  private String senderId;

  /**
   * 
   */
  private Message message;

  /**
   * 
   * Create a new ActualInMessageEvent.
   * 
   */
  protected ActualInMessageEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code ActualInMessageEvent}.
   * 
   * @param senderId
   * @param message
   */
  public ActualInMessageEvent(long occurrenceTime, String senderId,
      Message message) {
    super(occurrenceTime);
    this.senderId = senderId;
    this.message = message;
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
  public String getSenderId() {
    return senderId;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code message}.
   * 
   * 
   * 
   * @return the {@code message}.
   */
  public Message getMessage() {
    return message;
  }

}
