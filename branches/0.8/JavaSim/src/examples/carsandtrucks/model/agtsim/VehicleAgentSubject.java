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
 * File: VehicleAgentSubject.java
 * 
 * Package: examples.carsandtrucks.model.agtsim
 *
 **************************************************************************************************************/
package examples.carsandtrucks.model.agtsim;

import java.util.ArrayList;
import java.util.List;

import aors.model.AtomicEvent;
import aors.model.agtsim.AgentSubject;
import aors.model.agtsim.ReactionRule;
import aors.model.envevt.ActionEvent;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.intevt.InternalEvent;
import examples.carsandtrucks.model.envevt.SlowDown;
import examples.carsandtrucks.model.envevt.SpeedLimitEndPercEvt;
import examples.carsandtrucks.model.envevt.SpeedLimitStartPercEvt;
import examples.carsandtrucks.model.envevt.SpeedUp;

/**
 * VehicleAgentSubject
 * 
 * @author
 * @since July 10, 2008
 * @version $Revision$
 */
public class VehicleAgentSubject extends AgentSubject {

  private double vx;
  private double maxVelocity;

  /**
   * Get the
   * 
   * @code{myVelocity .
   * 
   * @return the
   * @code{myVelocity .
   */
  public double getVx() {
    return vx;
  }

  /**
   * Set the myVelocity.
   * 
   * @param myVelocity
   *          The myVelocity to set.
   */
  public void setVx(double myVelocity) {
    this.vx = myVelocity;
  }

  /**
   * Get the
   * 
   * @code{maxVelocity .
   * 
   * @return the
   * @code{maxVelocity .
   */
  public double getMaxVelocity() {
    return maxVelocity;
  }

  /**
   * Set the maxVelocity.
   * 
   * @param maxVelocity
   *          The maxVelocity to set.
   */
  public void setMaxVelocity(double maxVelocity) {
    this.maxVelocity = maxVelocity;
  }

  public VehicleAgentSubject(long id) {
    super(id);
    this.reactionRules = new ArrayList<ReactionRule>();
  }

  class SpeedLimitStartPercEvtAgtRule extends ReactionRule {
    private SpeedLimitStartPercEvt speedLimitStartPercEvt;
    private VehicleAgentSubject vehicle;

    SpeedLimitStartPercEvtAgtRule(String name, AgentSubject agentSubject) {
      super(name, agentSubject);

      this.vehicle = (VehicleAgentSubject) this.getAgentSubject();
      this.speedLimitStartPercEvt = new SpeedLimitStartPercEvt();
    }

    @Override
    public String getTriggeringEventType() {
      return "SpeedLimitStartPercEvt";
    }

    public void setTriggeringEvent(AtomicEvent event) {
      this.speedLimitStartPercEvt = (SpeedLimitStartPercEvt) event;
    }

    public ArrayList<InternalEvent> resultingInternalEvents() {
      return new ArrayList<InternalEvent>();
    }

    public boolean condition() {
      return vehicle.getVx() > this.speedLimitStartPercEvt.getAdmMaxVel();
    }

    public void stateEffects() {
      vehicle.setVx(this.speedLimitStartPercEvt.getAdmMaxVel());
    }

    /**
     * Execute the rule and compute the state efects, internal events and action
     * events.
     */
    public void execute() {
      if (condition()) {
        this.resultingInternalEvents = this.resultingInternalEvents();
        this.resultingActionEvents = this.resultingActionEvents();
        this.stateEffects();
      }
    }

    public ArrayList<ActionEvent> resultingActionEvents() {
      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();
      SlowDown slowDown = new SlowDown(this.speedLimitStartPercEvt
          .getOccurrenceTime() + 1, this.getAgentSubject().getId(),
          (PhysicalAgentObject) this.getAgentSubject().getAgentObject());
      slowDown.setVelocity(this.speedLimitStartPercEvt.getAdmMaxVel());
      actionEvents.add(slowDown);
      return actionEvents;
    }

    @Override
    public String getMessageType() {
      return "";
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code doResultingActionEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends ActionEvent> doResultingActionEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code doResultingInternalEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends InternalEvent> doResultingInternalEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code elseResultingActionEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends ActionEvent> elseResultingActionEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code elseResultingInternalEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends InternalEvent> elseResultingInternalEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code thenResultingActionEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends ActionEvent> thenResultingActionEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code thenResultingInternalEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends InternalEvent> thenResultingInternalEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code doStateEffects} from super class
     * 
     * 
     * 
     */
    @Override
    public void doStateEffects() {
      // TODO Auto-generated method stub

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
     * Comments: Overrides method {@code thenStateEffects} from super class
     * 
     * 
     * 
     */
    @Override
    public void thenStateEffects() {
      // TODO Auto-generated method stub

    }
  }

  class SpeedLimitEndPercEvtAgtRule extends ReactionRule {
    private SpeedLimitEndPercEvt speedLimitEndPercEvt;
    private VehicleAgentSubject vehicle;

    SpeedLimitEndPercEvtAgtRule(String name, AgentSubject agentSubject) {
      super(name, agentSubject);

      this.vehicle = (VehicleAgentSubject) this.getAgentSubject();
      this.speedLimitEndPercEvt = new SpeedLimitEndPercEvt();
    }

    @Override
    public String getTriggeringEventType() {
      return "SpeedLimitEndPercEvt";
    }

    public void setTriggeringEvent(AtomicEvent event) {
      this.speedLimitEndPercEvt = (SpeedLimitEndPercEvt) event;
    }

    public ArrayList<InternalEvent> resultingInternalEvents() {
      return new ArrayList<InternalEvent>();
    }

    public boolean condition() {
      return vehicle.getVx() < vehicle.getMaxVelocity();
    }

    public void stateEffects() {
      vehicle.setVx(vehicle.getMaxVelocity());
    }

    /**
     * Execute the rule and compute the state efects, internal events and action
     * events.
     */
    public void execute() {
      if (condition()) {
        this.resultingInternalEvents = this.resultingInternalEvents();
        this.resultingActionEvents = this.resultingActionEvents();
        this.stateEffects();
      }
    }

    public ArrayList<ActionEvent> resultingActionEvents() {
      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();
      SpeedUp speedUp = new SpeedUp(this.speedLimitEndPercEvt
          .getOccurrenceTime() + 1, this.getAgentSubject().getId(),
          (PhysicalAgentObject) this.getAgentSubject().getAgentObject());
      speedUp.setVelocity(vehicle.getMaxVelocity());
      actionEvents.add(speedUp);
      return actionEvents;
    }

    @Override
    public String getMessageType() {
      return "";
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code doResultingActionEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends ActionEvent> doResultingActionEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code doResultingInternalEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends InternalEvent> doResultingInternalEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code elseResultingActionEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends ActionEvent> elseResultingActionEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code elseResultingInternalEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends InternalEvent> elseResultingInternalEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code thenResultingActionEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends ActionEvent> thenResultingActionEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code thenResultingInternalEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    protected List<? extends InternalEvent> thenResultingInternalEvents() {
      // TODO Auto-generated method stub
      return null;
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code doStateEffects} from super class
     * 
     * 
     * 
     */
    @Override
    public void doStateEffects() {
      // TODO Auto-generated method stub

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
     * Comments: Overrides method {@code thenStateEffects} from super class
     * 
     * 
     * 
     */
    @Override
    public void thenStateEffects() {
      // TODO Auto-generated method stub

    }
  }

}
