/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
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
 * File: CarAgentSubject.java
 * 
 * Package: examples.carsandtrucks.model.agtsim
 *
 **************************************************************************************************************/
package examples.carsandtrucks.model.agtsim;

/**
 * CarAgentSubject
 * 
 * @author
 * @since July 10, 2008
 * @version $Revision: 1.0 $
 */
public class CarAgentSubject extends VehicleAgentSubject {

  public CarAgentSubject(long id) {
    super(id);

    SpeedLimitStartPercEvtAgtRule speedLimitStartPercEvtAgtRule = new SpeedLimitStartPercEvtAgtRule(
        "SpeedLimitStartPercEvtAgtRule", this);
    this.reactionRules.add(speedLimitStartPercEvtAgtRule);
    SpeedLimitEndPercEvtAgtRule speedLimitEndPercEvtAgtRule = new SpeedLimitEndPercEvtAgtRule(
        "SpeedLimitEndPercEvtAgtRule", this);
    this.reactionRules.add(speedLimitEndPercEvtAgtRule);
  }

}
