/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
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
 * File: EndSpeedLimitEnvRule.java
 * 
 * Package: examples.movingcars.model.envsim
 *
 **************************************************************************************************************/
package examples.movingcars.model.envsim;

import java.util.ArrayList;

import aors.model.AtomicEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.EnvironmentRule;
import aors.model.envsim.EnvironmentSimulator;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import examples.movingcars.model.envevt.EachCycle;
import examples.movingcars.model.envevt.SpeedLimitEndPpEvt;

/**
 * EndSpeedLimitEnvRule
 * 
 * @author Jens Werner
 * @since 04.06.2008
 * @version $Revision: 1.0 $
 */
public class EndSpeedLimitEnvRule extends EnvironmentRule {

  /**
   * &lt;TriggeringAtomicEventExpr eventType="EachCycle"
   * eventVariable="eachCycle"/&gt;
   */
  private EachCycle eachCycle;

  /**
   * &lt;AgentVariableDeclaration type="CarAgent" variable="car"/&gt;
   */
  private CarAgentObject car;

  /**
   * &lt;ObjectVariableDeclaration type="SpeedLimitCancelationSign"
   * variable="sign"/&gt;
   */
  private SpeedLimitCancelationSign sign;

  /**
   * Create a new {@code EndSpeedLimitEnvRule}.
   * 
   * @param name
   * @param envSim
   */
  public EndSpeedLimitEnvRule(String name, EnvironmentSimulator envSim) {
    super(name, envSim);
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code destroyObject} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public boolean thenDestroyObjekt() {
    return false;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code execute} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public ArrayList<EnvironmentEvent> execute() {

    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();

    for (PhysicalObject sign : getEnvironmentSimulator()
        .getPhysicalObjectsByType(SpeedLimitCancelationSign.class)) {

      this.sign = (SpeedLimitCancelationSign) sign;

      for (PhysicalAgentObject agent : getEnvironmentSimulator()
          .getPhysAgentObjectsByType(CarAgentObject.class)) {

        this.car = (CarAgentObject) agent;

        if (condition()) {
          thenStateEffects();
          result.addAll(thenResultingEvents());
          thenDestroyObjekt();
        }
      }
    }
    return result;
  }

  /**
   * Usage:
   * 
   * Comments: Overrides method {@code resultingEvents} from super class
   * 
   * 
   * @return
   */
  @Override
  public ArrayList<EnvironmentEvent> thenResultingEvents() {

    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();

    long perceiver;

    perceiver = car.getId();
    SpeedLimitEndPpEvt speedLimitEndPpEvt = new SpeedLimitEndPpEvt(eachCycle
        .getOccurrenceTime() + 1, perceiver);

    result.add(speedLimitEndPpEvt);
    return result;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code condition} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public boolean condition() {
    return (((sign.getX() - car.getX()) > 0) && ((sign.getX() - car.getX()) < car
        .getPerceptionRadius()));
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code getTriggeringEvent} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public String getTriggeringEventType() {
    return "EachCycle";
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code setTriggeringEvent} from super class
   * 
   * 
   * 
   * @param atomicEvent
   */
  @Override
  public void setTriggeringEvent(AtomicEvent atomicEvent) {
    this.eachCycle = (EachCycle) atomicEvent;

  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code stateEffects} from super class
   * 
   * 
   * 
   */
  @Override
  public void thenStateEffects() {
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
