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
 * File: ReminderEvent.java
 * 
 * Package: info.aors.model.intevt
 *
 **************************************************************************************************************/
package aors.model.intevt;

/**
 * ReminderEvent
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner
 * @since May 25, 2008
 * @version $Revision$
 */
public class ReminderEvent extends TimeEvent {

  private String reminderMsg;

  /**
   * 
   * Create a new {@code ReminderEvent}.
   * 
   */
  public ReminderEvent() {
    super();
  }

  /**
   * 
   * Create a new {@code ReminderEvent}.
   * 
   * @param id
   */
  public ReminderEvent(long id, long occurrenceTime, String reminderMsg) {
    super(id, occurrenceTime);
    this.reminderMsg = reminderMsg;
  }

  public ReminderEvent(long occurrenceTime, String reminderMsg) {
    super(occurrenceTime);
    this.reminderMsg = reminderMsg;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code reminderMessage}.
   * 
   * 
   * 
   * @return the {@code reminderMessage}.
   */
  public String getReminderMsg() {
    return reminderMsg;
  }

}
