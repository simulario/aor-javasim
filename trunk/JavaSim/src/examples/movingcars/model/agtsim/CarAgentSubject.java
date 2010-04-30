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
 * File: AgentSubjectCar.java
 * 
 * Package: examples.movingcars.model.agtsim
 *
 **************************************************************************************************************/
package examples.movingcars.model.agtsim;

import java.util.ArrayList;
import java.util.List;

import aors.model.AtomicEvent;
import aors.model.Message;
import aors.model.agtsim.ActualPerceptionRule;
import aors.model.agtsim.AgentSubject;
import aors.model.agtsim.ReactionRule;
import aors.model.envevt.ActionEvent;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.intevt.ActualPerceptionEvent;
import aors.model.intevt.InternalEvent;
import examples.movingcars.model.envevt.SlowDown;
import examples.movingcars.model.envevt.SpeedLimitEndPpEvt;
import examples.movingcars.model.envevt.SpeedLimitStartPpEvt;
import examples.movingcars.model.envevt.SpeedUp;

/**
 * AgentSubjectCar
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 26, 2008
 * @version $Revision: 1.0 $
 */
public class CarAgentSubject extends AgentSubject {

  // self belief attributes
  /**
   * 
   */
  private double myVelocity;

  /**
   * 
   */
  private double maxVelocity;

  /**
   * 
   * Create a new AgentSubjectCar.
   * 
   * @param id
   * @param agtSimListener
   */
  public CarAgentSubject(long id) {
    super(id);

    // instantiate a new ArrayList
    this.reactionRules = new ArrayList<ReactionRule>();
    this.actualPercRules = new ArrayList<ActualPerceptionRule>();

    // rules creation and initialization
    // default constructor needs name and the subjective agent to which this
    // rule belongs
    // this is way we use name and "this".
    SpeedLimitStartPercEvtMappingRule speedLimitStartPercEvtMappingRule = new SpeedLimitStartPercEvtMappingRule(
        "SpeedLimitStartPercEvtMappingRule", this);
    // add the created rule the the list of rules
    this.actualPercRules.add(speedLimitStartPercEvtMappingRule);

    SpeedLimitEndPercEvtMappingRule speedLimitEndPercEvtMappingRule = new SpeedLimitEndPercEvtMappingRule(
        "SpeedLimitEndPercEvtMappingRule", this);
    this.actualPercRules.add(speedLimitEndPercEvtMappingRule);

    SpeedLimitStartPercEvtAgtRule speedLimitStartPercEvtAgtRule = new SpeedLimitStartPercEvtAgtRule(
        "SpeedLimitStartPercEvtAgtRule", this);
    this.reactionRules.add(speedLimitStartPercEvtAgtRule);

    SpeedLimitEndPercEvtAgtRule speedLimitEndPercEvtAgtRule = new SpeedLimitEndPercEvtAgtRule(
        "SpeedLimitEndPercEvtAgtRule", this);
    this.reactionRules.add(speedLimitEndPercEvtAgtRule);
  }

  /**
   * Get the
   * 
   * @code{myVelocity .
   * 
   * @return the
   * @code{myVelocity .
   */
  public double getMyVelocity() {
    return myVelocity;
  }

  /**
   * Set the myVelocity.
   * 
   * @param myVelocity
   *          The myVelocity to set.
   */
  public void setMyVelocity(double myVelocity) {
    this.myVelocity = myVelocity;
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

  // internal events definition
  /**
   * 
   * SpeedLimitStartApEvt definition of an ActualPerception event, notice that
   * this inner class has the default package accessibility modifier
   */
  class SpeedLimitStartApEvt extends ActualPerceptionEvent {
    /**
     * attribute of this internal event
     */
    private double admMaxVel;

    /**
     * Create a new AgentSubjectCar.SpeedLimitStartApEvt.
     * 
     */
    SpeedLimitStartApEvt() {
      super();
    }

    /**
     * 
     * Create a new SpeedLimitStartApEvt.
     * 
     * @param occurrenceTime
     */
    SpeedLimitStartApEvt(long occurrenceTime) {
      super(occurrenceTime);
    }

    /**
     * Get the
     * 
     * @code{admMaxVel .
     * 
     * @return the
     * @code{admMaxVel .
     */
    public double getAdmMaxVel() {
      return admMaxVel;
    }

    /**
     * Set the admMaxVel.
     * 
     * @param admMaxVel
     *          The admMaxVel to set.
     */
    public void setAdmMaxVel(double admMaxVel) {
      this.admMaxVel = admMaxVel;
    }
  }

  class SpeedLimitEndApEvt extends ActualPerceptionEvent {

    /**
     * Create a new {@code SpeedLimitEndPpEvt}.
     * 
     * @param occurrecenteTime
     */
    SpeedLimitEndApEvt(long occurrecenteTime) {
      super(occurrecenteTime);
    }

  }

  // rules definition
  /**
   * SpeedLimitStartPercEvtMappingRule
   */
  class SpeedLimitStartPercEvtMappingRule extends ActualPerceptionRule {

    /**
     * triggeringEvent of this rule
     */
    private SpeedLimitStartPpEvt speedLimitStartPpEvt;

    protected SpeedLimitStartPercEvtMappingRule(String name,
        AgentSubject agentSubject) {
      super(name, agentSubject);
      this.speedLimitStartPpEvt = new SpeedLimitStartPpEvt();
    }

    @Override
    public void setTriggeringEvent(AtomicEvent atomicEvent) {
      this.speedLimitStartPpEvt = (SpeedLimitStartPpEvt) atomicEvent;
    }

    @Override
    public InternalEvent resultingInternalEvent() {
      SpeedLimitStartApEvt speedLimitStartApEvt = new SpeedLimitStartApEvt(
          this.speedLimitStartPpEvt.getOccurrenceTime());
      speedLimitStartApEvt.setAdmMaxVel(this.speedLimitStartPpEvt
          .getAdmMaxVel());
      return speedLimitStartApEvt;
    }

    @Override
    public boolean condition() {
      return true;
    }

    @Override
    public Class<? extends Message> getMessageType() {
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

    /**
     * Usage:
     *
     *
     * Comments: Overrides method {@code getTriggeringEventType} from super class
     * 
     *
     * 
     * @return
     */
    @Override
    public String getTriggeringEventType() {
      return "SpeedLimitStartPpEvt";
    }
  }

  /**
   * 
   * SpeedLimitStartPercEvtAgtRule
   * 
   */
  class SpeedLimitStartPercEvtAgtRule extends ReactionRule {

    /**
     * triggeringEvent
     */
    private SpeedLimitStartApEvt percEvt;

    /**
     * it's created if we have
     * 
     * @agentVariable
     */
    private CarAgentSubject car;

    SpeedLimitStartPercEvtAgtRule(String name, AgentSubject agentSubject) {
      super(name, agentSubject);

      // this initialization is needed because if the event is not initialized
      // its type cannot be retrieved
      // this initialization regards event matching
      // a concrete event is set at rule execution time
      this.percEvt = new SpeedLimitStartApEvt(0);

      this.car = (CarAgentSubject) this.getAgentSubject();
    }

    /**
     * 
     */
    public String getTriggeringEventType() {
      return "SpeedLimitStartApEvt";
    }

    public void setTriggeringEvent(AtomicEvent atomicEvent) {
      this.percEvt = (SpeedLimitStartApEvt) atomicEvent;
    }

    /**
     * 
     */
    public boolean condition() {
      // return the result of the condition
      return car.getMyVelocity() > this.percEvt.getAdmMaxVel();
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

    /**
     * 
     */
    public void stateEffects() {
      this.car.setMyVelocity(percEvt.getAdmMaxVel());
    }

    /**
     * create list of resulting action events if any
     */
    public ArrayList<ActionEvent> resultingActionEvents() {
      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();
      SlowDown slowDown = new SlowDown(this.percEvt.getOccurrenceTime() + 1,
          this.getAgentSubject().getId(), (PhysicalAgentObject) this
              .getAgentSubject().getAgentObject());
      slowDown.setVelocity(this.percEvt.getAdmMaxVel());
      actionEvents.add(slowDown);
      return actionEvents;
    }

    /**
     * create list of internal events if any if not internal events are return
     * an empty list
     */
    public ArrayList<InternalEvent> resultingInternalEvents() {
      return new ArrayList<InternalEvent>();
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

  /**
   * SpeedLimitEndPercEvtMappingRule
   * 
   * mapped from perception to actualperception
   * 
   */
  class SpeedLimitEndPercEvtMappingRule extends ActualPerceptionRule {

    /**
     * 
     */
    private SpeedLimitEndPpEvt speedLimitEndPpEvt;

    /**
     * Create a new {@code SpeedLimitEndPercEvtMappingRule}.
     * 
     * @param name
     * @param agentSubject
     */
    SpeedLimitEndPercEvtMappingRule(String name, AgentSubject agentSubject) {
      super(name, agentSubject);
      this.speedLimitEndPpEvt = new SpeedLimitEndPpEvt();
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code resultingInternalEvents} from super
     * class
     * 
     * 
     * 
     * @return
     */
    @Override
    public InternalEvent resultingInternalEvent() {

      SpeedLimitEndApEvt speedLimitEndApEvt = new SpeedLimitEndApEvt(
          this.speedLimitEndPpEvt.getOccurrenceTime());
      return speedLimitEndApEvt;
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
      return "SpeedLimitEndPpEvt";
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
      this.speedLimitEndPpEvt = (SpeedLimitEndPpEvt) atomicEvent;

    }

    @Override
    public Class<? extends Message> getMessageType() {
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

    /**
     * 
     */
    private CarAgentSubject car;

    /**
     * 
     */
    private SpeedLimitEndApEvt speedLimitEndApEvt;

    /**
     * Create a new {@code SpeedLimitEndPercEvtAgtRule}.
     * 
     * @param name
     * @param agentSubject
     */
    SpeedLimitEndPercEvtAgtRule(String name, AgentSubject agentSubject) {
      super(name, agentSubject);

      this.speedLimitEndApEvt = new SpeedLimitEndApEvt(0);
      this.car = (CarAgentSubject) this.getAgentSubject();
    }

    /**
     * Usage:
     * 
     * 
     * Comments: Overrides method {@code resultingActionEvents} from super class
     * 
     * 
     * 
     * @return
     */
    @Override
    public ArrayList<ActionEvent> doResultingActionEvents() {

      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();

      SpeedUp speedUp = new SpeedUp(
          this.speedLimitEndApEvt.getOccurrenceTime() + 1, this
              .getAgentSubject().getId(), (PhysicalAgentObject) this
              .getAgentSubject().getAgentObject());
      speedUp.setVelocity(this.car.getMaxVelocity());

      actionEvents.add(speedUp);

      return actionEvents;
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
      return (car.getMyVelocity() < car.getMaxVelocity());
    }

    /**
     * Execute the rule and compute the state efects, internal events and action
     * events.
     */
    public void execute() {
      if (condition()) {
        this.resultingInternalEvents = this.doResultingInternalEvents();
        this.resultingActionEvents = this.doResultingActionEvents();
        this.doStateEffects();
      }
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
      return "SpeedLimitEndApEvt";
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
      this.speedLimitEndApEvt = (SpeedLimitEndApEvt) atomicEvent;

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
    public void doStateEffects() {
      this.car.setMyVelocity(this.car.getMaxVelocity());
    }

    @Override
    public String getMessageType() {
      return "";
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
    protected List<InternalEvent> doResultingInternalEvents() {
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
