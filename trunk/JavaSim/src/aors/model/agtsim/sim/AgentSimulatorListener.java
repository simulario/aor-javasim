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
 * File: AgentSubjectListener.java
 * 
 * Package: info.aors.model.agtsim.java
 *
 **************************************************************************************************************/
package aors.model.agtsim.sim;

import java.util.EventListener;
import java.util.List;

import aors.logger.model.AgentSimulatorStep;
import aors.model.envevt.ActionEvent;
import aors.util.JsonData;

/**
 * AgentSubjectListener, listener for transfering ActionEvents to the
 * AbstractSimulator.
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 26, 2008
 * @version $Revision: 1.0 $
 */
public interface AgentSimulatorListener extends EventListener {

  /**
   * Function to use in i.e. a AgentSimulator to transfer ActionEvents to the
   * AbstractSimulator.
   * 
   * @param agentSubjectEvent
   */
  public void receiveActionEvents(List<ActionEvent> actions, JsonData agentLog);

  public void receiveActionEvents(List<ActionEvent> actions,
      AgentSimulatorStep agentSimulatorStep);

}
