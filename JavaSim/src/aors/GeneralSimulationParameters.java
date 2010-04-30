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
 * File: SimulationParameters.java
 * 
 * Package: info.aors
 *
 **************************************************************************************************************/
package aors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * GeneralSimulationParameters
 * 
 * Its intended meaning is to capture the simulation parameters as its
 * properties
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner
 * @since May 25, 2008
 * @version $Revision: 1.0 $
 */
public abstract class GeneralSimulationParameters implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3075928104571176095L;
  // it is used in the logger
  // if you change here someone, please change it in the custom.xsl too
  public static final String SIMULATION_STEPS_NAME = "SIMULATION_STEPS";
  public static final String STEP_DURATION_NAME = "STEP_DURATION";
  public static final String TIME_UNIT_NAME = "TIME_UNIT";
  public static final String STEP_TIME_DELAY_NAME = "STEP_TIME_DELAY";
  public static final String RANDOM_SEED = "RANDOM_SEED";
  public static final String Random_Order_Agent_Simulation = "Random_Order_Agent_Simulation";

  /**
   * TimeUnit This is an enumeration which refers to the possible values of
   * timeUnit from SimulationParameters
   */
  public enum TimeUnit {
    /**
     * ms mili seconds
     */
    ms,
    /**
     * s seconds
     */
    s,
    /**
     * min minute
     */
    min,
    /**
     * h hour
     */
    h,
    /**
     * D day
     */
    D,
    /**
     * W week
     */
    W,
    /**
     * M month
     */
    M,
    /**
     * Y year
     */
    Y;

    static final Map<String, TimeUnit> timeUnitMap = new HashMap<String, TimeUnit>();

    static {
      for (TimeUnit tm : TimeUnit.values())
        timeUnitMap.put(tm.toString(), tm);
    }

  }

}
