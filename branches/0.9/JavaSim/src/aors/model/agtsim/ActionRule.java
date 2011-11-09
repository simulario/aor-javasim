/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner, Mircea Diaconescu
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
 * File: ActionRule.java
 * 
 * Package: aors.model.agtsim
 *
 **************************************************************************************************************/
package aors.model.agtsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import aors.model.Rule;
import aors.model.envevt.ActionEvent;
import aors.model.intevt.InternalEvent;

public abstract class ActionRule extends Rule {
  /**
   * An ActionRule refers to a specific AgentSubject with the help of this
   * property we keep the bidirectional association between a AgentSubject and
   * its rules (ActionRule) this must be added on the rule constructor
   */
  private AgentSubject agentSubject;

  /**
   * The list of resulting internal events
   */
  protected List<InternalEvent> resultingInternalEvents;

  /**
   * The list of resulting action events
   */
  protected List<ActionEvent> resultingActionEvents;

  /**
   * Usage: ALWAYS call super in the sub-classes Create a new {@code ActionRule}
   * .
   * 
   * @param agentSubject
   */
  public ActionRule(String name, AgentSubject agentSubject) {
    super(name);
    this.agentSubject = agentSubject;
    this.resultingInternalEvents = new ArrayList<InternalEvent>();
    this.resultingActionEvents = new ArrayList<ActionEvent>();
  }

  /**
   * This call will say to the rule which of the rule parameters were set to be
   * used when rule executed.
   * 
   * @param parameters
   *          the parameters to set for this rule
   */
  protected abstract void setParameters(HashMap<String, Object> parameters);

  /**
   * get the list with resulted internal events after the rule execution. This
   * must be an empty list if the condition is not satisfied.
   * 
   * @return the resulting internal events list
   */
  public List<? extends InternalEvent> getResultingInternalEvents() {
    return resultingInternalEvents;
  }

  /**
   * get the list with resulted action events after the rule execution. This
   * must be an empty list if the condition is not satisfied.
   * 
   * @return the resulting action events list
   */
  public List<? extends ActionEvent> getResultingActionEvents() {
    return resultingActionEvents;
  }

  /**
   * hooks; may overwrite in instances
   * 
   * @return list of internal events after the rule processing
   */
  protected List<? extends InternalEvent> doResultingInternalEvents() {
    return Collections.emptyList();
  }

  protected List<? extends InternalEvent> thenResultingInternalEvents() {
    return Collections.emptyList();
  }

  protected List<? extends InternalEvent> elseResultingInternalEvents() {
    return Collections.emptyList();
  }

  /**
   * hooks; may overwrite in instances
   * 
   * @return list of ActionEvents after processing the rule
   */
  protected List<? extends ActionEvent> doResultingActionEvents() {
    return Collections.emptyList();
  }

  protected List<? extends ActionEvent> thenResultingActionEvents() {
    return Collections.emptyList();
  }

  protected List<? extends ActionEvent> elseResultingActionEvents() {
    return Collections.emptyList();
  }

  /**
   * Get the agent subject that contains this rule.
   * 
   * @return the subjective agent, owner of this rule
   */
  public AgentSubject getAgentSubject() {
    return this.agentSubject;
  }

  /**
   * Usage: this method perform the rule execution by checking the condition and
   * computing the necessarily action events, or perception events.
   */
  public abstract void execute();

  /**
   * 
   * If the rule is triggered by an InMessageEvent or OutMessageEvent
   * (otherwise, it was a null returned), we need this method to check the
   * MessageType.
   * 
   * @return a string that is the message type name
   * 
   */
  public abstract String getMessageType();
}
