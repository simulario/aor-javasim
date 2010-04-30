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
 * File: Rule.java
 * 
 * Package: info.aors.model
 *
 **************************************************************************************************************/
package aors.model;

/**
 * Rule
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner
 * @since May 23, 2008
 * @version $Revision: 1.0 $
 */
public abstract class Rule {

  /**
   * given name to a rule.
   */
  private String name;

  /**
   * description of rule
   */
  private String description;

  /**
   * helpful to know when the rules is triggered
   */
  private long triggeredTime;

  /**
   * 
   * Create a new {@code Rule}. according to the XML schema name attribute is
   * required
   * 
   * @param name
   */
  protected Rule(String name) {
    this.name = name;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code name}.
   * 
   * 
   * 
   * @return the {@code name}.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code description}.
   * 
   * 
   * 
   * @return the {@code description}.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code description}.
   * 
   * 
   * 
   * @param description
   *          The {@code description} to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * 
   * Usage: It is used only in AgentSimulatorDefaultImpl and in
   * EnvironmentSimulator
   * 
   * Comments: it is used to set a concrete event for the rule after event
   * matching has been performed.
   * 
   * 
   * @param atomicEvent
   */
  public abstract void setTriggeringEvent(AtomicEvent atomicEvent);

  /**
   * 
   * Usage: is implemented as a hook and should be overloaded if necessary
   * 
   * 
   * Comments:
   * 
   * @TODO rename, i.e. in "isConditionComplied" or "isConditionTrue"
   * 
   * @return true if the rule condition holds; otherwise false
   */
  protected boolean condition() {return true;};

  /**
   * Comments: hook
   * 
   */
  protected void doStateEffects() {};
  protected void thenStateEffects() {};
  protected void elseStateEffects() {};

  public abstract String getTriggeringEventType();
  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code triggeredTime}.
   * 
   * 
   * 
   * @return the {@code triggeredTime}.
   */
  protected long getTriggeredTime() {
    return triggeredTime;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code triggeredTime}.
   * 
   * 
   * 
   * @param triggeredTime
   *          The {@code triggeredTime} to set.
   */
  public void setTriggeredTime(long triggeredTime) {
    this.triggeredTime = triggeredTime;
  }

}
