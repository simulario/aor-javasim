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
 * File: ActualPerceptionRule.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package aors.model.agtsim;

import aors.model.AtomicEvent;
import aors.model.Message;
import aors.model.Rule;
import aors.model.intevt.InternalEvent;

/**
 * ActualPerceptionRule
 * 
 * @author Jens Werner
 * @since May 25, 2008
 * @version $Revision: 1.0 $
 */
public abstract class ActualPerceptionRule extends Rule {

  /**
   * An ReactionRule refers to a specific AgentSubject with the help of this
   * property we keep the bidirectional association between a AgentSubject and
   * its rules (ReactionRule) this must be added on the rule constructor
   */
  private AgentSubject agentSubject;

  protected ActualPerceptionRule(String name, AgentSubject agentSubject) {
    super(name);
    this.agentSubject = agentSubject;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: contains a Internal Event that are computed after the rule
   * execution
   * 
   * @return internal event after the rule processing
   */
  public abstract InternalEvent resultingInternalEvent();

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return the subjective agent, owner of this rule
   */
  public AgentSubject getAgentSubject() {
    return this.agentSubject;
  }
  
  @Deprecated
  public abstract Class<? extends Message> getMessageType();

}
