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
 * File: EnvironmentRule.java
 * 
 * Package: info.aors.model.envsim
 *
 **************************************************************************************************************/
package aors.model.envsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import aors.model.Rule;
import aors.model.envevt.EnvironmentEvent;

/**
 * EnvironmentRule
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner
 * @since May 23, 2008
 * @version $Revision$
 */
public abstract class EnvironmentRule extends Rule {

  /**
   * 
   */
  private EnvironmentAccessFacet envSim;

  /**
   * 
   * Create a new {@code EnvironmentRule}.
   * 
   * @param name
   * @param envSim
   */
  public EnvironmentRule(String name, EnvironmentAccessFacet envSim) {
    super(name);
    this.envSim = envSim;
  }

  /**
   * 
   * Usage: for each destruction it must call envSim.destroObjekt(long id);
   * 
   * 
   * Comments: this method destroys an AgentObject notice that it has to inform
   * the SimulationEngine about the destruction of the AgentObject so that the
   * SimulationEngine can destroy also the corresponding AgentSubject.
   * 
   * 
   * 
   * @return true if the agent is destroyed; false otherwise
   */
  protected boolean doDestroyObjekt() {
    return false;
  }

  protected boolean thenDestroyObjekt() {
    return false;
  }

  protected boolean elseDestroyObjekt() {
    return false;
  }

  public abstract ArrayList<EnvironmentEvent> execute();

  public abstract String getMessageType();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code envSim}.
   * 
   * 
   * 
   * @param envSim
   *          The {@code envSim} to set.
   */
  public void setEnvironmentSimulator(EnvironmentAccessFacet envSim) {
    this.envSim = envSim;
  }

  /**
   * 
   * Create Events For PerceptionEvents and InMessageEvents create one Event for
   * every perceiver
   * 
   * Is a hook - overload this if necessary
   * 
   */
  protected Collection<EnvironmentEvent> doResultingEvents() {
    return Collections.emptyList();
  }

  protected Collection<EnvironmentEvent> thenResultingEvents() {
    return Collections.emptyList();
  }

  protected Collection<EnvironmentEvent> elseResultingEvents() {
    return Collections.emptyList();
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code envSim}.
   * 
   * @return the {@code envSim}.
   */
  public EnvironmentAccessFacet getEnvironmentSimulator() {
    return this.envSim;
  }

}
