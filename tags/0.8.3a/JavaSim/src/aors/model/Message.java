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
 * File: Message.java
 * 
 * Package: info.aors.model
 *
 **************************************************************************************************************/
package aors.model;

import java.util.UUID;

/**
 * Message
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 23, 2008
 * @version $Revision$
 */
public abstract class Message extends Entity {
  /**
   * The identity of the message. This will be an auto-generated ID
   */
  private String messageIdentity;

  /**
   * 
   * Create a new {@code Message}.
   * 
   */
  public Message() {
    // call the constructor of the class Entity
    super();
    this.messageIdentity = UUID.randomUUID().toString();
  }

  /**
   * 
   * Create a new {@code Message}.
   * 
   * @param id
   */
  public Message(long id) {
    // call the constructor of the class Entity
    super(id);
    this.messageIdentity = UUID.randomUUID().toString();
  }

  /**
   * 
   * @param id
   * @param name
   */
  public Message(long id, String name) {
    // call the constructor of the class Entity
    super(id, name);
    this.messageIdentity = UUID.randomUUID().toString();
  }

  /**
   * @return the messageIdentity
   */
  public String getMessageIdentity() {
    return messageIdentity;
  }

}
