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
 * File: OutMessageEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import aors.model.Message;
import aors.model.envsim.AgentObject;

/**
 * OutMessageEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner
 * @since May 25, 2008
 * @version $Revision$
 */
public class OutMessageEvent extends ActionEvent {

  /**
   * 
   */
  private long receiverIdRef;

  /**
   * 
   */
  private Message message;

  /**
   * 
   * Create a new OutMessageEvent.
   * 
   */
  public OutMessageEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code OutMessageEvent}.
   * 
   * @param occurrenceTime
   * @param receiver
   * @param senderId
   * @param message
   */
  @Deprecated
  public OutMessageEvent(long occurrenceTime, long receiver, long senderId,
      Message message) {
    super(occurrenceTime, senderId);
    this.message = message;
    this.receiverIdRef = receiver;
  }

  /**
   * 
   * Create a new {@code OutMessageEvent}.
   * 
   * @param occurrenceTime
   * @param receiverIdRef
   * @param senderIdRef
   * @param sender
   * @param message
   */
  public OutMessageEvent(long occurenceTime, long receiverIdRef,
      long senderIdRef, AgentObject sender, Message message) {
    super(occurenceTime, senderIdRef, sender);
    this.message = message;
    this.receiverIdRef = receiverIdRef;
  }

  /**
   * Usage: Comments: Get the {@code receiverIdRef}.
   * 
   * @return the {@code receiverIdRef}.
   */
  public long getReceiverIdRef() {
    return this.receiverIdRef;
  }

  /**
   * Usage: Set the {@code receiverIdRef}. Comments:
   * 
   * @param receiverIdRef
   *          The {@code receiverIdRef} to set.
   */
  public void setReceiverIdRef(long receiverIdRef) {
    this.receiverIdRef = receiverIdRef;
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

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code message}.
   * 
   * 
   * 
   * @param message
   *          The {@code message} to set.
   */
  public void setMessage(Message message) {
    this.message = message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String toString() {
    Class<Message> c = (Class<Message>) this.message.getClass();
    String details = "";
    Method[] declaredMethods = c.getDeclaredMethods();
    for (Method m : declaredMethods) {
      if (m.getName().startsWith("get")) {
        try {
          Object o = m.invoke(this.message, new Object[0]);
          if (!details.equals(""))
            details += ", ";
          details += m.getName() + ":" + o;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
      }
    }
    return this.getClass().getSimpleName() + "(" + this.getId() + ") at "
        + this.getOccurrenceTime() + " from " + this.getActorIdRef() + " to "
        + this.getReceiverIdRef() + " with message "
        + this.getMessage().getType() + " [" + details + "]";
  }

}
