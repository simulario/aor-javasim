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
 * File: SlowDownEnvRule.java
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
import examples.movingcars.model.envevt.SlowDown;

/**
 * SlowDownEnvRule
 * 
 * @author Jens Werner
 * @since 04.06.2008
 * @version $Revision: 1.0 $
 */
public class SlowDownEnvRule extends EnvironmentRule {

  /**
   * &lt;TriggeringAtomicEventExpr eventType="SlowDown"
   * eventVariable="slowDownActEvt"/&gt;
   */
  private SlowDown slowDownActEvt;

  /**
   * 
   */
  private CarAgentObject car;

  /**
   * Create a new {@code SlowDownEnvRule}.
   * 
   * @param name
   * @param envSim
   */
  public SlowDownEnvRule(String name, EnvironmentSimulator envSim) {
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
    this.car = (CarAgentObject) getEnvironmentSimulator().getPhysAgentById(
        this.slowDownActEvt.getActorIdRef(), CarAgentObject.class);

    if (this.car != null && condition()) {
      thenStateEffects();
      result.addAll(thenResultingEvents());
      thenDestroyObjekt();
    }
    return result;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code resultingEvents} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public ArrayList<EnvironmentEvent> thenResultingEvents() {
    return (new ArrayList<EnvironmentEvent>());
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
    return true;
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
    return "SlowDown";
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
    this.slowDownActEvt = (SlowDown) atomicEvent;
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
    this.car.setVx(this.slowDownActEvt.getVelocity());
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
