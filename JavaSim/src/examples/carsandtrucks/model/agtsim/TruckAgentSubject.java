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
 * File: TruckAgentSubject.java
 * 
 * Package: examples.carsandtrucks.model.agtsim
 *
 **************************************************************************************************************/
package examples.carsandtrucks.model.agtsim;

import java.util.ArrayList;
import java.util.List;

import aors.model.AtomicEvent;
import aors.model.Message;
import aors.model.agtsim.ReactionRule;
import aors.model.agtsim.AgentSubject;
import aors.model.envevt.ActionEvent;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.intevt.InternalEvent;
import examples.carsandtrucks.model.envevt.TruckSlowDown;
import examples.carsandtrucks.model.envevt.TruckSpeedLimitEndPercEvt;
import examples.carsandtrucks.model.envevt.TruckSpeedLimitStartPercEvt;
import examples.carsandtrucks.model.envevt.TruckSpeedUp;

/**
 * TruckAgentSubject
 * 
 * @author
 * @since July 10, 2008
 * @version $Revision$
 */
public class TruckAgentSubject extends VehicleAgentSubject {

  public TruckAgentSubject(long id) {
    super(id);

    SpeedLimitStartPercEvtAgtRule speedLimitStartPercEvtAgtRule = new SpeedLimitStartPercEvtAgtRule(
        "SpeedLimitStartPercEvtAgtRule", this);
    this.reactionRules.add(speedLimitStartPercEvtAgtRule);

    SpeedLimitEndPercEvtAgtRule speedLimitEndPercEvtAgtRule = new SpeedLimitEndPercEvtAgtRule(
        "SpeedLimitEndPercEvtAgtRule", this);
    this.reactionRules.add(speedLimitEndPercEvtAgtRule);

    TruckSpeedLimitStartPercEvtAgtRule truckSpeedLimitStartPercEvtAgtRule = new TruckSpeedLimitStartPercEvtAgtRule(
        "TruckSpeedLimitStartPercEvtAgtRule", this);
    this.reactionRules.add(truckSpeedLimitStartPercEvtAgtRule);

    TruckSpeedLimitEndPercEvtAgtRule truckSpeedLimitEndPercEvtAgtRule = new TruckSpeedLimitEndPercEvtAgtRule(
        "TruckSpeedLimitEndPercEvtAgtRule", this);
    this.reactionRules.add(truckSpeedLimitEndPercEvtAgtRule);
  }

  class TruckSpeedLimitStartPercEvtAgtRule extends ReactionRule {
    private TruckSpeedLimitStartPercEvt truckSpeedLimitStartPercEvt;
    private TruckAgentSubject truck;

    TruckSpeedLimitStartPercEvtAgtRule(String name, AgentSubject agentSubject) {
      super(name, agentSubject);
      this.truck = (TruckAgentSubject) this.getAgentSubject();
      this.truckSpeedLimitStartPercEvt = new TruckSpeedLimitStartPercEvt();

    }

    @Override
    public String getTriggeringEventType() {
      return "TruckSpeedLimitStartPercEvt";
    }

    public void setTriggeringEvent(AtomicEvent event) {
      this.truckSpeedLimitStartPercEvt = (TruckSpeedLimitStartPercEvt) event;
    }

    public ArrayList<InternalEvent> resultingInternalEvents() {
      return new ArrayList<InternalEvent>();
    }

    public boolean condition() {
      return truck.getVx() > this.truckSpeedLimitStartPercEvt.getAdmMaxVel();
    }

    public void stateEffects() {
      truck.setVx(this.truckSpeedLimitStartPercEvt.getAdmMaxVel());
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
      TruckSlowDown truckSlowDown = new TruckSlowDown(
          this.truckSpeedLimitStartPercEvt.getOccurrenceTime() + 1, this
              .getAgentSubject().getId(), (PhysicalAgentObject) this
              .getAgentSubject().getAgentObject());
      truckSlowDown
          .setVelocity(this.truckSpeedLimitStartPercEvt.getAdmMaxVel());
      actionEvents.add(truckSlowDown);
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

  class TruckSpeedLimitEndPercEvtAgtRule extends ReactionRule {
    private TruckSpeedLimitEndPercEvt truckSpeedLimitEndPercEvt;
    private TruckAgentSubject truck;

    TruckSpeedLimitEndPercEvtAgtRule(String name, AgentSubject agentSubject) {
      super(name, agentSubject);
      this.truck = (TruckAgentSubject) this.getAgentSubject();
      this.truckSpeedLimitEndPercEvt = new TruckSpeedLimitEndPercEvt();
    }

    @Override
    public String getTriggeringEventType() {
      return "TruckSpeedLimitEndPercEvt";
    }

    public void setTriggeringEvent(AtomicEvent event) {
      this.truckSpeedLimitEndPercEvt = (TruckSpeedLimitEndPercEvt) event;
    }

    public ArrayList<InternalEvent> resultingInternalEvents() {
      return new ArrayList<InternalEvent>();
    }

    public boolean condition() {
      return truck.getVx() < truck.getMaxVelocity();
    }

    public void stateEffects() {
      truck.setVx(truck.getMaxVelocity());
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
      TruckSpeedUp truckSpeedUp = new TruckSpeedUp(
          this.truckSpeedLimitEndPercEvt.getOccurrenceTime() + 1, this
              .getAgentSubject().getId(), (PhysicalAgentObject) this
              .getAgentSubject().getAgentObject());
      truckSpeedUp.setVelocity(truck.getMaxVelocity());
      actionEvents.add(truckSpeedUp);
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
