/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2009 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
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
 * File: JaxbLogGenerator.java
 * 
 * Package: aors.model.agtsim.jaxb
 *
 **************************************************************************************************************/
package aors.model.agtsim.jaxb;

import java.util.List;

import aors.logger.model.AgentSimulatorStep;
import aors.logger.model.AgtSimInputEventType;
import aors.logger.model.InMessageEventType;
import aors.logger.model.MessageType;
import aors.logger.model.ObjectFactory;
import aors.logger.model.PerceptionEventType;
import aors.logger.model.PhysicalObjectPerceptionEventType;
import aors.model.agtsim.ReactionRule;
import aors.model.agtsim.AgentSubject;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.OutMessageEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.intevt.InternalEvent;

/**
 * JaxbLogGenerator
 * 
 * @author Jens Werner
 * @since 24.11.2009
 * @version $Revision: 1.0 $
 */
public class JaxbLogGenerator {

  private AgentSimulatorStep agentSimulatorStep;

  private ObjectFactory objectFactory;

  private AgentSubject agentSubject;
  private String agentSubjectType;

  public JaxbLogGenerator(AgentSubject agentSubject) {
    this.objectFactory = new ObjectFactory();
    this.agentSubject = agentSubject;
    this.agentSubjectType = agentSubject.getType();
    if (this.agentSubjectType.endsWith("AgentSubject")) {
      // prefix MUST correlate with ext/javagen/custom.xsl prefix.agentSubject
      // declaration
      this.agentSubjectType = this.agentSubjectType.substring(0,
          this.agentSubjectType.length() - 12);
    }
  }

  public void notifyAgentSimulatorStepStart() {
    this.agentSimulatorStep = this.objectFactory.createAgentSimulatorStep();
    this.agentSimulatorStep.setAgent(this.agentSubject.getId());
    this.agentSimulatorStep.setAgentType(this.agentSubjectType);
  }

  public void notifyPerceptionEvent(PerceptionEvent perceptionEvent) {

    if (this.agentSimulatorStep != null) {

      // +++ PhysicalObjectPerceptionEvent +++
      if (PhysicalObjectPerceptionEvent.class.isInstance(perceptionEvent)) {

        PhysicalObjectPerceptionEvent aorPhysicalObjectPerceptionEvent = (PhysicalObjectPerceptionEvent) perceptionEvent;
        PhysicalObjectPerceptionEventType physicalObjectPerceptionEventType = this.objectFactory
            .createPhysicalObjectPerceptionEventType();

        physicalObjectPerceptionEventType
            .setType("PhysicalObjectPerceptionEvent");
        physicalObjectPerceptionEventType
            .setPerceptionAngle(aorPhysicalObjectPerceptionEvent
                .getPerceptionAngle());
        physicalObjectPerceptionEventType
            .setDistance(aorPhysicalObjectPerceptionEvent.getDistance());
        physicalObjectPerceptionEventType
            .setPerceivedType(aorPhysicalObjectPerceptionEvent
                .getPerceivedPhysicalObjectType());
        physicalObjectPerceptionEventType
            .setPerceivedId(aorPhysicalObjectPerceptionEvent
                .getPerceivedPhysicalObjectIdRef());
        // physicalObjectPerceptionEventType.setPerceiverIdRef(this.agentSubject.getId());

        this.agentSimulatorStep
            .getAgtSimInputEvent()
            .add(
                this.objectFactory
                    .createPhysicalObjectPerceptionEvent(physicalObjectPerceptionEventType));

      } else if (InMessageEvent.class.isInstance(perceptionEvent)) {

        InMessageEvent aorInMessageEvent = (InMessageEvent) perceptionEvent;
        InMessageEventType inMessageEventType = this.objectFactory
            .createInMessageEventType();

        // Message message = inMessageEvent.getMessage();

        MessageType messageType = this.objectFactory.createMessageType();
        messageType.setType(aorInMessageEvent.getMessage().getType());

        inMessageEventType.setMessage(messageType);
        inMessageEventType.setSenderIdRef(aorInMessageEvent.getSenderIdRef());

        this.agentSimulatorStep.getAgtSimInputEvent().add(
            this.objectFactory.createActualInMessageEvent(inMessageEventType));

      } else if (PerceptionEvent.class.isInstance(perceptionEvent)) {

        PerceptionEventType perceptionEventType = objectFactory
            .createPerceptionEventType();

        perceptionEventType.setType(perceptionEvent.getType());

        this.agentSimulatorStep.getAgtSimInputEvent().add(
            this.objectFactory.createPerceptionEvent(perceptionEventType));
      }

    }

  }

  public void notifyPerceptionEventResult(ReactionRule reactionRule,
      List<ActionEvent> resultingEventList) {

    int indexOfCurrentEvent = this.agentSimulatorStep.getAgtSimInputEvent()
        .size() - 1;

    if (indexOfCurrentEvent >= 0) {
      AgtSimInputEventType currentEvent = this.agentSimulatorStep
          .getAgtSimInputEvent().get(indexOfCurrentEvent).getValue();

      this
          .setResultingActionEvents(reactionRule, resultingEventList, currentEvent);

    }
  }

  // public void notifyPerception(PerceptionEvent perceptionEvent,
  // List<ActionEvent> resultingEventList, ReactionRule agentRule) {
  // }

  private void setResultingActionEvents(ReactionRule reactionRule,
      List<ActionEvent> acList, AgtSimInputEventType agtSimInputEventType) {

    if (!acList.isEmpty()) {

      PerceptionEventType.ResultingEvents resLEvents = this.objectFactory
          .createAgtSimInputEventTypeResultingEvents();
      resLEvents.setResultingFromRule(reactionRule.getName());

      agtSimInputEventType.getResultingEvents().add(resLEvents);

      for (ActionEvent aorActionEvent : acList) {

        if (OutMessageEvent.class.isInstance(aorActionEvent)) {

          OutMessageEvent aorOutMessageEvent = (OutMessageEvent) aorActionEvent;
          aors.logger.model.AgtSimInputEventType.ResultingEvents.OutMessageEvent outMessageEvent = this.objectFactory
              .createAgtSimInputEventTypeResultingEventsOutMessageEvent();

          outMessageEvent.setMessageType(aorOutMessageEvent.getMessage()
              .getType());
          outMessageEvent.setReceiverIdRef(aorOutMessageEvent
              .getReceiverIdRef());
          outMessageEvent.setOccurrenceTime(aorOutMessageEvent
              .getOccurrenceTime());

          resLEvents.getActionEventOrOutMessageEvent().add(outMessageEvent);

        } else {

          aors.logger.model.AgtSimInputEventType.ResultingEvents.ActionEvent actionEvent = this.objectFactory
              .createAgtSimInputEventTypeResultingEventsActionEvent();

          actionEvent.setType(aorActionEvent.getType());
          actionEvent.setOccurrenceTime(aorActionEvent.getOccurrenceTime());

          resLEvents.getActionEventOrOutMessageEvent().add(actionEvent);

        }
      }
    }
  }

  public void notifyInternalEvent(InternalEvent internalEvent,
      List<ActionEvent> acList) {

    if (this.agentSimulatorStep != null) {

    }

  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code agentSimulatorStep}.
   * 
   * 
   * 
   * @return the {@code agentSimulatorStep}.
   */
  public AgentSimulatorStep getAgentSimulatorStep() {
    return agentSimulatorStep;
  }

}
