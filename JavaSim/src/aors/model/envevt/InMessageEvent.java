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
 * File: InMessageEvent.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.envevt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import aors.model.Message;

/**
 * InMessageEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision: 1.0 $
 */
public class InMessageEvent extends PerceptionEvent {

  private long senderIdRef;
  private Message message;

  /**
   * 
   * Create a new InMessageEvent.
   * 
   */
  public InMessageEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code InMessageEvent}.
   * 
   * @param receiverIdRef
   * @param senderIdRef
   * @param message
   */
  public InMessageEvent(long occurrenceTime, long receiverIdRef,
      long senderIdRef, Message message) {
    super(occurrenceTime, receiverIdRef);
    this.senderIdRef = senderIdRef;
    this.message = message;
  }

  /**
   * 
   * @param name
   * @param occurrenceTime
   * @param receiverIdRef
   * @param senderIdRef
   * @param message
   */
  public InMessageEvent(String name, long occurrenceTime, long receiverIdRef,
      long senderIdRef, Message message) {
    super(name, occurrenceTime, receiverIdRef);
    this.senderIdRef = senderIdRef;
    this.message = message;
  }

  /**
   * @param id
   * @param name
   * @param occurrenceTime
   * @param receiverIdRef
   * @param senderIdRef
   * @param message
   */
  public InMessageEvent(long id, String name, long occurrenceTime,
      long receiverIdRef, long senderIdRef, Message message) {
    super(id, name, occurrenceTime, receiverIdRef);
    this.senderIdRef = senderIdRef;
    this.message = message;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code senderIdRef}.
   * 
   * 
   * 
   * @return the {@code senderIdRef}.
   */
  public long getSenderIdRef() {
    return senderIdRef;
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

  @SuppressWarnings("unchecked")
  @Override
  public String toString() {
    String details = "";
    if (this.message instanceof Message) {
      Class<Message> c = (Class<Message>) this.message.getClass();
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
          + this.getOccurrenceTime() + " from " + this.getSenderIdRef()
          + " to " + this.getPerceiverIdRef() + " with message "
          + this.getMessage().getType() + " [" + details + "]";
    } else {
      details = "null message!";
      return this.getClass().getSimpleName() + "(" + this.getId() + ") at "
          + this.getOccurrenceTime() + " from " + this.getSenderIdRef()
          + " to " + this.getPerceiverIdRef() + " with message  [" + details
          + "]";
    }

  }

}
