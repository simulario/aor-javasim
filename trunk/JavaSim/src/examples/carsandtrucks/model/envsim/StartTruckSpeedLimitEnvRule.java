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
 * File: StartTruckSpeedLimitEnvRule.java
 * 
 * Package: examples.carsandtrucks.model.envsim
 *
 **************************************************************************************************************/
package examples.carsandtrucks.model.envsim;

import java.util.ArrayList;

import aors.model.AtomicEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.EnvironmentRule;
import aors.model.envsim.EnvironmentSimulator;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import examples.carsandtrucks.model.envevt.EachCycle;
import examples.carsandtrucks.model.envevt.TruckSpeedLimitStartPercEvt;

/**
 * StartTruckSpeedLimitEnvRule
 * 
 * @author
 * @since July 13, 2008
 * @version $Revision: 1.0 $
 */
public class StartTruckSpeedLimitEnvRule extends EnvironmentRule {
  private EachCycle eachCycle;

  private TruckAgentObject truck;

  private TruckSpeedLimitSign sign;

  /**
   * 
   * Create a new {@code StartSpeedLimitEnvRule}.
   * 
   * @param name
   * @param envSim
   */
  public StartTruckSpeedLimitEnvRule(String name, EnvironmentSimulator envSim) {
    super(name, envSim);
  }

  public boolean thenDestroyObjekt() {

    return false;
  }

  /**
   * 
   */
  public ArrayList<EnvironmentEvent> execute() {

    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();

    for (PhysicalObject sign : getEnvironmentSimulator()
        .getPhysicalObjectsByType(TruckSpeedLimitSign.class)) {

      this.sign = (TruckSpeedLimitSign) sign;

      for (PhysicalAgentObject agent : getEnvironmentSimulator()
          .getPhysAgentObjectsByType(TruckAgentObject.class)) {

        this.truck = (TruckAgentObject) agent;

        if (condition()) {
          thenStateEffects();
          result.addAll(thenResultingEvents());
          thenDestroyObjekt();
        }
      }
    }
    return result;
  }

  public boolean condition() {
    return (((sign.getX() - truck.getX()) > 0) && ((sign.getX() - truck.getX()) < truck
        .getPerceptionRadius()));
  }

  public String getTriggeringEventType() {
    return "EachCycle";
  }

  public void setTriggeringEvent(AtomicEvent event) {
    this.eachCycle = (EachCycle) event;
  }

  public void thenStateEffects() {

  }

  public ArrayList<EnvironmentEvent> thenResultingEvents() {
    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();

    long perceiver;

    perceiver = truck.getId();
    TruckSpeedLimitStartPercEvt truckSpeedLimitStartPercEvt = new TruckSpeedLimitStartPercEvt(
        eachCycle.getOccurrenceTime(), perceiver);
    truckSpeedLimitStartPercEvt.setAdmMaxVel(this.sign.getAdmMaxVel());
    result.add(truckSpeedLimitStartPercEvt);
    return result;
  }

  @Override
  public String getMessageType() {
    // TODO Auto-generated method stub
    return "";
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code elseResultingEvents} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public ArrayList<EnvironmentEvent> elseResultingEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code elseStateEffects} from super class
   * 
   * 
   * 
   */
  @Override
  public void elseStateEffects() {
    // TODO Auto-generated method stub

  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code elseDestroyObjekt} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public boolean elseDestroyObjekt() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean doDestroyObjekt() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ArrayList<EnvironmentEvent> doResultingEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void doStateEffects() {
    // TODO Auto-generated method stub

  }
}
