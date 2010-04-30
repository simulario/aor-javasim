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
 * File: StartSpeedLimitEnvRule.java
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
import examples.movingcars.model.envevt.SpeedLimitStartPpEvt;

/**
 * StartSpeedLimitEnvRule <EnvironmentRule name="StartSpeedLimitEnvRule">
 * <description>This rule takes care of creating a speed limit perception event
 * whenever a car agent approaches a speed limit sign (gets closer than the
 * perception radius)</description> <TriggeringAtomicEventExpr
 * eventType="EachCycle" eventVariable="eachCycle"/> <InvolvedEntities>
 * <AgentVariableDeclaration type="CarAgent" variable="car"/>
 * <ObjectVariableDeclaration type="SpeedLimitSign" variable="sign"
 * object="101"/> </InvolvedEntities> <Condition language="Java">((sign.getX() -
 * car.getX()) &gt; 0 ) &amp;&amp; ((sign.getX() - car.getX()) &lt;
 * car.getPerceptionRadius())</Condition> <ResultingAtomicEventExpr>
 * <PotentialPerceptionEventExpr eventType="SpeedLimitStartPpEvt"> <perceiver
 * language="Java">car.getId()</perceiver> <Slot xsi:type="aors:OpaqueExprSlot"
 * property="occurrenceTime"> <valueExpr
 * language="Java">eachCycle.getOccurrenceTime()</valueExpr> </Slot> <Slot
 * xsi:type="aors:OpaqueExprSlot" property="admMaxVel"> <valueExpr
 * language="Java">sign.admMaxVel</valueExpr> </Slot>
 * </PotentialPerceptionEventExpr> </ResultingAtomicEventExpr>
 * </EnvironmentRule>
 * 
 * @author Jens Werner
 * @since 02.06.2008
 * @version $Revision: 1.0 $
 */
public class StartSpeedLimitEnvRule extends EnvironmentRule {

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
   * &lt;ObjectVariableDeclaration type="SpeedLimitSign" variable="sign"/&gt;
   */
  private SpeedLimitSign sign;

  /**
   * 
   * Create a new {@code StartSpeedLimitEnvRule}.
   * 
   * @param name
   * @param envSim
   */
  public StartSpeedLimitEnvRule(String name, EnvironmentSimulator envSim) {
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
    // this rule does not destroy any object
    return false;
  }

  /**
   * 
   */
  public ArrayList<EnvironmentEvent> execute() {

    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();

    for (PhysicalObject sign : getEnvironmentSimulator()
        .getPhysicalObjectsByType(SpeedLimitSign.class)) {

      this.sign = (SpeedLimitSign) sign;

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
   * 
   * Usage:
   * 
   * 
   * Comments: returns the triggering event of this rule
   * 
   * 
   * 
   * @return a concrete instance of EachCycle
   */
  public String getTriggeringEventType() {
    return "EachCycle";
  }

  /**
   * Usage: it is used in EnvironmentSimulator
   * 
   * Comments: sets the concrete instance for the triggering event
   */
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
    // nothing to change by this rule

  }

  public ArrayList<EnvironmentEvent> thenResultingEvents() {
    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();

    long perceiver;

    perceiver = car.getId();
    SpeedLimitStartPpEvt speedLimitStartPpEvt = new SpeedLimitStartPpEvt(
        eachCycle.getOccurrenceTime() + 1, perceiver);
    speedLimitStartPpEvt.setAdmMaxVel(this.sign.getAdmMaxVel());
    result.add(speedLimitStartPpEvt);
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
