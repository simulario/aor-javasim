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
 * File: EachCycleEnvRule.java
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
import examples.carsandtrucks.controller.SimulationParameters;
import examples.movingcars.controller.Simulator;
import examples.movingcars.model.envevt.EachCycle;

/**
 * EachCycleEnvRule <br />
 * &lt;EnvironmentRule name="EachCycleEnvRule"&gt; <br />
 * &lt;description&gt;This rule takes care of updating the current position of
 * each car at each simulation step/cycle.&lt;/description&gt;<br />
 * &lt;TriggeringAtomicEventExpr eventType="EachCycle"
 * eventVariable="eachCycle"/&gt;<br />
 * &lt;UpdateObjectiveStateExpr&gt;<br />
 * &lt;UpdateEntitySet type="CarAgent" startID="1" endID="3"
 * entityVariable="agent" loopVariable="i"&gt;<br />
 * &lt;Slot xsi:type="aors:OpaqueExprSlot" property=Physical.PROP_X&gt;<br />
 * &lt;valueExpr language="Java"&gt;(agent.getX() + agent.getVx() *
 * MovingCars.stepDuration) %
 * MovingCars.getSpaceModel().getXMax()&lt;/valueExpr&gt;<br />
 * &lt;/Slot&gt;<br />
 * &lt;/UpdateEntitySet&gt;<br />
 * &lt;/UpdateObjectiveStateExpr&gt;<br />
 * &lt;/EnvironmentRule&gt;<br />
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 26, 2008
 * @version $Revision$
 * 
 */
public class EachCycleEnvRule extends EnvironmentRule {

  /**
   * this is a triggering event for this rule it has to properly initialized
   */
  private EachCycle eachCycle;

  /**
   * InvolvedEntity
   */
  private CarAgentObject car;

  /**
   * 
   * Create a new EachCycleEnvRule.
   * 
   * @param name
   * @param envSim
   */
  public EachCycleEnvRule(String name, EnvironmentSimulator envSim) {
    super(name, envSim);
  }

  /**
   * 
   */
  public void thenStateEffects() {
    car.setX((car.getX() + car.getVx() * SimulationParameters.STEP_DURATION)
        % Simulator.spaceModel.getXMax());
  }

  /**
   * 
   */
  public boolean condition() {
    // actually our rules does not check conditions. Therefore this function
    // always returns true.
    return (true);
  }

  /**
   * 
   */
  public ArrayList<EnvironmentEvent> thenResultingEvents() {
    return (new ArrayList<EnvironmentEvent>());
  }

  /**
   * 
   */
  public boolean createAgent() {
    // this rule does not create any agent
    return false;
  }

  /**
   * 
   */
  public boolean thenDestroyObjekt() {
    // this rule does not destroy any object
    return false;
  }

  /**
   * 
   */
  public boolean createObjekt() {
    // this rule does not create any object
    return false;
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
   * 
   */
  public ArrayList<EnvironmentEvent> execute() {

    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();

    for (PhysicalAgentObject agent : getEnvironmentSimulator()
        .getPhysAgentObjectsByType(CarAgentObject.class)) {

      this.car = (CarAgentObject) agent;

      if (condition()) {
        thenStateEffects();
        result.addAll(thenResultingEvents());
        thenDestroyObjekt();
      }
    }
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
