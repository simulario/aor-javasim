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
 * File: CommunicationAgent.java
 * 
 * Package: aors.model.agtsim.agt
 *
 **************************************************************************************************************/
package aors.model.agtsim.agt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import aors.model.AtomicEvent;
import aors.model.Entity;
import aors.model.agtsim.AgentSubject;
import aors.model.agtsim.ReactionRule;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.OutMessageEvent;
import aors.model.envsim.msg.Ask;
import aors.model.envsim.msg.Reply;
import aors.model.envsim.msg.Tell;
import aors.model.intevt.InternalEvent;
import aors.query.sparql.QueryEngine;

/**
 * CommunicationAgentSubject
 * 
 * A standard agent enhanced with communication capabilities.
 * 
 * c
 * 
 * @since March 23, 2009
 * @version $Revision$
 */

public class TrustfulAndSincereAgentSubject extends AgentSubject {

  private final String BELIEF_ID_IDENT = "id";

  /**
   * Constructor
   * 
   * @param id
   *          the id of the agent
   */
  public TrustfulAndSincereAgentSubject(long id) {
    super(id);
    this.reactionRules = new ArrayList<ReactionRule>();
    ReceiveAskMessageRule receiveAskMessageRule = new ReceiveAskMessageRule(
        "ReceiveAskMessageRule", this);
    this.reactionRules.add(receiveAskMessageRule);
    ReceiveReplyMessageRule receiveReplyMessageRule = new ReceiveReplyMessageRule(
        "ReceiveReplyMessageRule", this);
    this.reactionRules.add(receiveReplyMessageRule);
    ReceiveTellMessageRule receiveTellMessageRule = new ReceiveTellMessageRule(
        "ReceiveTellMessageRule", this);
    this.reactionRules.add(receiveTellMessageRule);
  }

  /**
   * 
   * Comments: Create a new {@code CommunicationAgentSubject}. This constructor
   * is to be called ONLY by the {@code SimulationEngine} Usage:
   * 
   * @param id
   *          the id of this CommunicationAgentSubject. It must be the same as
   *          the id of its corresponding AgentObject in the
   *          EnvironmentSimulator
   * 
   * @param name
   *          the name of the CommunicationAgentSubject
   */
  public TrustfulAndSincereAgentSubject(long id, String name) {
    super(id, name);
    this.reactionRules = new ArrayList<ReactionRule>();
    ReceiveAskMessageRule receiveAskMessageRule = new ReceiveAskMessageRule(
        "ReceiveAskMessageRule", this);
    this.reactionRules.add(receiveAskMessageRule);
    ReceiveReplyMessageRule receiveReplyMessageRule = new ReceiveReplyMessageRule(
        "ReceiveReplyMessageRule", this);
    this.reactionRules.add(receiveReplyMessageRule);
    ReceiveTellMessageRule receiveTellMessageRule = new ReceiveTellMessageRule(
        "ReceiveTellMessageRule", this);
    this.reactionRules.add(receiveTellMessageRule);
  }

  /**
   * ReceiveAskMessageRule rule
   * 
   * A standard agent rule for for received Ask messages
   */
  public class ReceiveAskMessageRule extends ReactionRule {

    private InMessageEvent inMessageEvent;
    private TrustfulAndSincereAgentSubject communicationAgentSubject;

    public ReceiveAskMessageRule(String name,
        TrustfulAndSincereAgentSubject communicationAgentSubject) {
      super(name, communicationAgentSubject);

      this.inMessageEvent = new InMessageEvent();
      this.communicationAgentSubject = (TrustfulAndSincereAgentSubject) this
          .getAgentSubject();
    }

    /**
     * The received message is an generic Ask message
     * 
     * @return true if the message is an Ask message, or false otherwise
     */
    public boolean condition() {
      if (!aors.model.envsim.msg.Ask.class.isInstance(inMessageEvent
          .getMessage())) {
        return (false);
      }

      return true;
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
     * No state effects
     */
    public void stateEffects() {
    }

    /**
     * Send a generic Reply message as response
     * 
     * @return the corresponding reply message for this ask
     */
    protected ArrayList<ActionEvent> resultingActionEvents() {
      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();

      // create the Reply message
      Reply reply = new Reply();
      Ask ask = (Ask) inMessageEvent.getMessage();

      reply.setBeliefEntityType(ask.getBeliefEntityType());

      if (((Ask) inMessageEvent.getMessage()).getQueryLanguage()
          .equalsIgnoreCase(QueryEngine.ENGINE_NAME)) {

        List<HashMap<String, String>> resultSolutions = new ArrayList<HashMap<String, String>>();

        try {
          // get answers for this query
          resultSolutions = this.communicationAgentSubject.executeQuery(ask
              .getQueryLanguage(), ask.getQueryString());
        } catch (NoClassDefFoundError e) {
          System.out
              .println("Warning: the distribution does not contains libraries for using SPARQL query! All queries results will be empty!");
          return actionEvents;
        }

        // add all solutions to the result set
        int size = resultSolutions.size();
        for (int i = 0; i < size; i++) {
          HashMap<String, String> aSolution = resultSolutions.get(i);

          // test if the query answer contains the ID as solution (required)
          if (aSolution.get(BELIEF_ID_IDENT) == null) {
            System.err.println("The QUERY: " + ask.getQueryString()
                + " must contain the required property: " + BELIEF_ID_IDENT
                + " ! No changes were performed by this request! ");
            continue;
          }

          Iterator<String> iterator = aSolution.keySet().iterator();
          long id = Long.parseLong(aSolution.get(BELIEF_ID_IDENT));
          while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals(BELIEF_ID_IDENT)) {
              continue;
            } else {
              reply.addPropValueForBeliefEntityId(id, key, aSolution.get(key));
            }
          }
        }
      }

      // create the OutMessageEvent
      OutMessageEvent outMessageEvent;
      outMessageEvent = new OutMessageEvent(this.inMessageEvent
          .getOccurrenceTime() + 1, this.inMessageEvent.getSenderIdRef(),
          this.communicationAgentSubject.getId(),
          this.communicationAgentSubject.getAgentObject(), reply);

      actionEvents.add(outMessageEvent);

      return (actionEvents);
    }

    @Override
    public String getTriggeringEventType() {
      return "InMessageEvent";
    }

    /**
     * Set the triggering event.
     * 
     * @param atomicEvent
     *          the new value for triggering event
     */
    public void setTriggeringEvent(AtomicEvent atomicEvent) {
      this.inMessageEvent = (InMessageEvent) atomicEvent;
    }

    /**
     * No resulting internal events.
     * 
     * @return an empty list of resulting internal events
     */
    public ArrayList<InternalEvent> resultingInternalEvents() {
      ArrayList<InternalEvent> internalEvents = new ArrayList<InternalEvent>();

      return (internalEvents);
    }

    @Override
    public String getMessageType() {
      return "Ask";
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
   * ReceiveReplyMessageRule rule
   * 
   * A standard agent rule for for received Reply messages
   */
  public class ReceiveReplyMessageRule extends ReactionRule {

    private InMessageEvent inMessageEvent;
    private TrustfulAndSincereAgentSubject communicationAgentSubject;

    public ReceiveReplyMessageRule(String name,
        TrustfulAndSincereAgentSubject communicationAgentSubject) {
      super(name, communicationAgentSubject);

      this.inMessageEvent = new InMessageEvent();
      this.communicationAgentSubject = (TrustfulAndSincereAgentSubject) this
          .getAgentSubject();
    }

    /**
     * The received message is an generic Reply message
     * 
     * @return true if the message is an Reply message, or false otherwise
     */
    public boolean condition() {
      if (!aors.model.envsim.msg.Reply.class.isInstance(inMessageEvent
          .getMessage())) {
        return (false);
      }

      return true;
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
     * All information (beliefs) received via this Reply message are added as
     * beliefs for this agent or update the already existing beliefs.
     */
    public void stateEffects() {
      Reply replyMsg = (Reply) inMessageEvent.getMessage();

      Iterator<Long> iterator_id = replyMsg.getAnswerSet().keySet().iterator();

      // for all different IDs
      while (iterator_id.hasNext()) {
        long id = iterator_id.next();

        HashMap<String, Object> incomingBelief = replyMsg.getAnswerSet()
            .get(id);
        Iterator<String> iterator_prop = incomingBelief.keySet().iterator();

        // if the belief already exists, then update it,
        // otherwise create it
        if (communicationAgentSubject.getBeliefEntityById(id) == null) {
          Entity resultBeliefEntity = communicationAgentSubject
              .createBeliefEntity(communicationAgentSubject, replyMsg
                  .getBeliefEntityType(), id);
          communicationAgentSubject.addBeliefEntity(resultBeliefEntity);
        }

        // update properties for the belief with the given ID
        while (iterator_prop.hasNext()) {
          String propName = iterator_prop.next();
          this.communicationAgentSubject.updateBeliefEntityProperty(id,
              propName, incomingBelief.get(propName));
        }
      }
    }

    /**
     * No actions
     * 
     * @return an empty actions list
     */
    protected ArrayList<ActionEvent> resultingActionEvents() {
      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();

      return (actionEvents);
    }

    @Override
    public String getTriggeringEventType() {
      return "InMessageEvent";
    }

    /**
     * Set the triggering event.
     * 
     * @param atomicEvent
     *          the new value for triggering event
     */
    public void setTriggeringEvent(AtomicEvent atomicEvent) {
      this.inMessageEvent = (InMessageEvent) atomicEvent;
    }

    /**
     * No resulting internal events.
     * 
     * @return an empty list of resulting internal events
     */
    protected ArrayList<InternalEvent> resultingInternalEvents() {
      ArrayList<InternalEvent> internalEvents = new ArrayList<InternalEvent>();

      return (internalEvents);
    }

    @Override
    public String getMessageType() {
      return "Reply";
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
   * ReceiveTellMessageRule rule
   * 
   * A standard agent rule for received Tell messages
   */
  public class ReceiveTellMessageRule extends ReactionRule {

    private InMessageEvent inMessageEvent;
    private TrustfulAndSincereAgentSubject communicationAgentSubject;

    public ReceiveTellMessageRule(String name,
        TrustfulAndSincereAgentSubject communicationAgentSubject) {
      super(name, communicationAgentSubject);

      this.inMessageEvent = new InMessageEvent();
      this.communicationAgentSubject = (TrustfulAndSincereAgentSubject) this
          .getAgentSubject();
    }

    /**
     * The received message is an generic Tell message
     * 
     * @return true if the message is an Tell message, or false otherwise
     */
    public boolean condition() {
      if (!aors.model.envsim.msg.Tell.class.isInstance(inMessageEvent
          .getMessage())) {
        return (false);
      }

      return true;
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
     * All information (beliefs) received via this Tell message are added as
     * beliefs for this agent or update the already existing beliefs.
     */
    public void stateEffects() {
      Tell tellMsg = (Tell) inMessageEvent.getMessage();

      Iterator<Long> iterator_id = tellMsg.getInfoSet().keySet().iterator();

      // for all different IDs
      while (iterator_id.hasNext()) {
        long id = iterator_id.next();

        HashMap<String, Object> incomingBelief = tellMsg.getInfoSet().get(id);
        Iterator<String> iterator_prop = incomingBelief.keySet().iterator();

        // if the belief already exists, then update it,
        // otherwise create it
        if (communicationAgentSubject.getBeliefEntityById(id) == null) {
          Entity resultBeliefEntity = communicationAgentSubject
              .createBeliefEntity(communicationAgentSubject, tellMsg
                  .getBeliefEntityType(), id);
          communicationAgentSubject.addBeliefEntity(resultBeliefEntity);
        }
        // update properties for the belief with the given ID
        while (iterator_prop.hasNext()) {
          String propName = iterator_prop.next();
          this.communicationAgentSubject.updateBeliefEntityProperty(id,
              propName, incomingBelief.get(propName));
        }
      }
    }

    /**
     * No actions
     * 
     * @return an empty actions list
     */
    protected ArrayList<ActionEvent> resultingActionEvents() {
      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();

      return (actionEvents);
    }

    @Override
    public String getTriggeringEventType() {
      return "InMessageEvent";
    }

    /**
     * Set the triggering event.
     * 
     * @param atomicEvent
     *          the new value for triggering event
     */
    public void setTriggeringEvent(AtomicEvent atomicEvent) {
      this.inMessageEvent = (InMessageEvent) atomicEvent;
    }

    /**
     * No resulting internal events.
     * 
     * @return an empty list of resulting internal events
     */
    protected ArrayList<InternalEvent> resultingInternalEvents() {
      ArrayList<InternalEvent> internalEvents = new ArrayList<InternalEvent>();

      return (internalEvents);
    }

    @Override
    public String getMessageType() {
      return "Tell";
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
   * ReceiveUntellMessageRule rule
   * 
   * A standard agent rule for received Untell messages
   */
  public class ReceiveUntellMessageRule extends ReactionRule {

    private InMessageEvent inMessageEvent;
    private TrustfulAndSincereAgentSubject communicationAgentSubject;

    public ReceiveUntellMessageRule(String name,
        TrustfulAndSincereAgentSubject communicationAgentSubject) {
      super(name, communicationAgentSubject);

      this.inMessageEvent = new InMessageEvent();
      this.communicationAgentSubject = (TrustfulAndSincereAgentSubject) this
          .getAgentSubject();
    }

    /**
     * The received message is an generic Untell message
     * 
     * @return true if the message is an Untell message, or false otherwise
     */
    public boolean condition() {
      if (!aors.model.envsim.msg.Untell.class.isInstance(inMessageEvent
          .getMessage())) {
        return (false);
      }

      return true;
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
     * All information (beliefs) received via this Untell message are deleted
     * from the beliefs of the receiver.
     */
    public void stateEffects() {
      Tell tellMsg = (Tell) inMessageEvent.getMessage();

      Iterator<Long> iterator_id = tellMsg.getInfoSet().keySet().iterator();

      // for all different IDs
      while (iterator_id.hasNext()) {

        long id = iterator_id.next();
        communicationAgentSubject.removeBeliefEntityById(id);
      }
    }

    /**
     * No actions
     * 
     * @return an empty actions list
     */
    protected ArrayList<ActionEvent> resultingActionEvents() {
      ArrayList<ActionEvent> actionEvents = new ArrayList<ActionEvent>();

      return (actionEvents);
    }

    @Override
    public String getTriggeringEventType() {
      return "InMessageEvent";
    }

    /**
     * Set the triggering event.
     * 
     * @param atomicEvent
     *          the new value for triggering event
     */
    public void setTriggeringEvent(AtomicEvent atomicEvent) {
      this.inMessageEvent = (InMessageEvent) atomicEvent;
    }

    /**
     * No resulting internal events.
     * 
     * @return an empty list of resulting internal events
     */
    protected ArrayList<InternalEvent> resultingInternalEvents() {
      ArrayList<InternalEvent> internalEvents = new ArrayList<InternalEvent>();

      return (internalEvents);
    }

    @Override
    public String getMessageType() {
      return "Untell";
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
