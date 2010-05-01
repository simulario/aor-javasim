/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
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
 **************************************************************************************************************/

package aors.data.java;

import java.io.File;
import java.util.EventListener;

import aors.controller.SimulationDescription;
import aors.model.envevt.EnvironmentEvent;
import aors.module.InitialState;

/**
 * SimulationEventListener - This interface has to be implemented by any part
 * that has to be an listener for simulation events, such as start, stop, pause
 * or initialize the simulation.
 * 
 * @author Mircea Diaconescu
 * @since October 26, 2009
 * @version $Revision$
 */
public interface SimulationEventListener extends EventListener {
  /**
   * This method is called whenever the simulation is initialized.
   * 
   * @param initialState
   *          the initial state
   */
  public void simulationInitialize(InitialState initialState);

  /**
   * This method is called whenever the DOM is initialized or reinitialized.
   * 
   * @param dom
   *          the current dom
   */
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription);

  /**
   * This method is called when the simulation starts.
   * 
   * @param startTime
   *          the start time
   * @param steps
   *          the number of steps
   */
  public void simulationStarted();

  /**
   * This method is called when the simulation is paused or continued after a
   * previous pause.
   * 
   * @param pauseState
   *          true if pause the simulation and false if continue the simulation
   *          after pause
   */
  public void simulationPaused(boolean pauseState);

  /**
   * This method is called when the simulation ends.
   */
  public void simulationEnded();

  /**
   * This method is called whenever an environment event occurs
   * 
   * @param environemntEvent
   *          the environment event that occurred
   */
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent);

  /**
   * This is called whenever an informational data has to be send to listeners
   * 
   * @param simulationEvent
   *          - the simulation event to be sent
   */
  public void simulationInfosEvent(SimulationEvent simulationEvent);

  /**
   * This method is called whenever the project directory is changed.
   * 
   * @param projectDirectory
   *          - the new project directory
   */
  public void simulationProjectDirectoryChanged(File projectDirectory);
}
