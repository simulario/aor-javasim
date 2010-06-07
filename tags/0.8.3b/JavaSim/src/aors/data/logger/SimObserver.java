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
 * File: SimObserver.java
 * 
 * Package: aors.logger
 *
 **************************************************************************************************************/
package aors.data.logger;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.ScenarioInfos;
import aors.controller.AbstractSimulator;
import aors.data.java.CollectionEvent;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.logger.model.ActionEventType;
import aors.logger.model.ActivityEndEventType;
import aors.logger.model.ActivityStartEventType;
import aors.logger.model.ActivityType;
import aors.logger.model.AgentSimulatorStep;
import aors.logger.model.Agents;
import aors.logger.model.AgtType;
import aors.logger.model.CausedEventType;
import aors.logger.model.CollectionType;
import aors.logger.model.Collections;
import aors.logger.model.DestroyObjectType;
import aors.logger.model.EntityType;
import aors.logger.model.EnvSimInputEventType;
import aors.logger.model.EnvironmentSimulatorStep;
import aors.logger.model.ExogeneousEventType;
import aors.logger.model.GridCellType;
import aors.logger.model.GridCells;
import aors.logger.model.InMessageEventType;
import aors.logger.model.MessageType;
import aors.logger.model.ObjType;
import aors.logger.model.ObjectFactory;
import aors.logger.model.ObjectType;
import aors.logger.model.Objects;
import aors.logger.model.OutMessageEventType;
import aors.logger.model.PerceptionEventType;
import aors.logger.model.PhysAgtType;
import aors.logger.model.PhysicalAgents;
import aors.logger.model.PhysicalObjType;
import aors.logger.model.PhysicalObjectType;
import aors.logger.model.PhysicalObjects;
import aors.logger.model.PhysicsSimulationType;
import aors.logger.model.ResultingStateChangesType;
import aors.logger.model.SimulationStep;
import aors.logger.model.SlotType;
import aors.logger.model.EnvSimInputEventType.Activities;
import aors.logger.model.EnvSimInputEventType.ResultingEvents;
import aors.logger.model.EnvSimInputEventType.Activities.FinalizeActivity;
import aors.logger.model.EnvSimInputEventType.Activities.StartActivity;
import aors.logger.model.SimulationStep.AgentSimResultingStateChanges;
import aors.model.Entity;
import aors.model.Message;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.ActivityEndEvent;
import aors.model.envevt.ActivityStartEvent;
import aors.model.envevt.CausedEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.ExogenousEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.OutMessageEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envevt.activity.Activity;
import aors.model.envsim.AgentObject;
import aors.model.envsim.EnvironmentRule;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.physim.PhySimEnvironmentEvent;
import aors.physim.PhySimKinematicsRule;
import aors.space.AbstractCell;
import aors.util.JsonData;
import aors.util.collection.AORCollection;

/**
 * SimObserver
 * 
 * This class implement only the {@link Logger} - methods to create all
 * informations for the visualization @see{@link aors.gui.swing.AORJavaGui}
 * method simulationStepEvent()
 * 
 * @author Jens Werner
 * @since 23.05.2009
 * @version $Revision$
 */
public class SimObserver extends Logger {

  /**
   * 
   */
  private static final long serialVersionUID = -2136404115241495265L;

  protected ObjectFactory objectFactory;

  protected SimulationStep simulationStep;

  public SimObserver() {
    // initialize();
    this.objectFactory = new ObjectFactory();
  }

  @Override
  public void initialize() {
  }

  @Override
  public void notifyEnd() {
  }

  @Override
  public void notifyEnvEvent(EnvironmentEvent envEvent) {

    if (AbstractSimulator.runLogger) {

      EnvironmentSimulatorStep environmentSimulatorStep = this.simulationStep
          .getEnvironmentSimulatorStep();

      if (envEvent instanceof PhySimEnvironmentEvent) {
        environmentSimulatorStep.setPhysicsSimulation(objectFactory
            .createPhysicsSimulationType());
        // PhySimEnvironmentEvent aorPhySimEnvironmentEvent =
        // (PhySimEnvironmentEvent) envEvent;

      } else if (envEvent instanceof ExogenousEvent) {

        ExogenousEvent aorExogenousEvent = (ExogenousEvent) envEvent;

        ExogeneousEventType exogeneousEventType = objectFactory
            .createExogeneousEventType();
        exogeneousEventType.setType(envEvent.getType());

        if (aorExogenousEvent.getNextOccurrenceTime() > 0) {
          exogeneousEventType.setNextOccurrenceTime(new Double(
              aorExogenousEvent.getNextOccurrenceTime()));
        }

        environmentSimulatorStep.getEnvSimInputEvent().add(
            objectFactory.createExogenousEvent(exogeneousEventType));
      }

      else if (envEvent instanceof OutMessageEvent) {

        OutMessageEvent aorOutMessageEvent = (OutMessageEvent) envEvent;
        OutMessageEventType outMessageEventType = objectFactory
            .createOutMessageEventType();
        outMessageEventType.setReceiverIdRef(aorOutMessageEvent
            .getReceiverIdRef());
        // outMessageEventType.setType(envEvent.getType());
        outMessageEventType.setSenderIdRef(aorOutMessageEvent.getActorIdRef());
        outMessageEventType.setMessageType(aorOutMessageEvent.getMessage()
            .getType());
        environmentSimulatorStep.getEnvSimInputEvent().add(
            objectFactory.createOutMessageEvent(outMessageEventType));

      } else if (envEvent instanceof ActionEvent) {

        ActionEventType actionEventType = objectFactory.createActionEventType();
        actionEventType.setType(envEvent.getType());
        actionEventType.setActorIdRef(((ActionEvent) envEvent).getActorIdRef());
        environmentSimulatorStep.getEnvSimInputEvent().add(
            objectFactory.createActionEvent(actionEventType));

      } else if (envEvent instanceof ActivityStartEvent) {

        ActivityStartEventType activityStartEventType = objectFactory
            .createActivityStartEventType();
        activityStartEventType.setActivity(((ActivityStartEvent) envEvent)
            .getActivityType());
        environmentSimulatorStep.getEnvSimInputEvent().add(
            objectFactory.createActivityStartEvent(activityStartEventType));

      } else if (envEvent instanceof ActivityEndEvent) {

        ActivityEndEventType activityEndEventType = objectFactory
            .createActivityEndEventType();
        activityEndEventType.setActivity(((ActivityEndEvent) envEvent)
            .getActivity().getName());
        environmentSimulatorStep.getEnvSimInputEvent().add(
            objectFactory.createActivityEndEvent(activityEndEventType));

      } else if (envEvent instanceof CausedEvent) {

        CausedEventType causedEventType = objectFactory.createCausedEventType();
        causedEventType.setType(envEvent.getType());
        environmentSimulatorStep.getEnvSimInputEvent().add(
            objectFactory.createCausedEvent(causedEventType));
      } else if (envEvent instanceof PerceptionEvent) {
        // nothing to do yet

      } else {
        System.err.println("Unknown Eventclass " + envEvent.getType()
            + " in Logger!");
      }
    }
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code notifyActivityStart} from super class
   * 
   * 
   * 
   * @param activity
   * @param envEventList
   *          - this list is now unused (contains normaly only an
   *          ActivityFinalizeEvent)
   * @param activityFinalizeEvent
   *          - an ActivityFinalizeEvent
   */
  @Override
  public void notifyActivityStart(Activity activity,
      Collection<EnvironmentEvent> envEventList,
      ActivityEndEvent activityFinalizeEvent) {

    if (AbstractSimulator.runLogger) {

      int indexOfCurrentEvent = this.simulationStep
          .getEnvironmentSimulatorStep().getEnvSimInputEvent().size() - 1;
      if (indexOfCurrentEvent >= 0) {
        EnvSimInputEventType currentEvent = this.simulationStep
            .getEnvironmentSimulatorStep().getEnvSimInputEvent().get(
                indexOfCurrentEvent).getValue();

        StartActivity startActivity = this.objectFactory
            .createEnvSimInputEventTypeActivitiesStartActivity();
        startActivity.setActivityName(activity.getName());

        if (activity.getActor() != null) {
          startActivity.setActor(activity.getActor().getId());
        }
        if (!activity.getStartEventCorrelationProperty().equals("")) {
          startActivity.setCorrelationProperty(activity
              .getStartEventCorrelationProperty());
          if (activity.getCorrelationValue() != null) {
            startActivity.setCorrelationValue(activity.getCorrelationValue()
                .toString());
          } else {
            startActivity.setCorrelationValue("unset");
          }
        }

        // if there is an ActivityFinalizeEvent, set the duration
        if (activityFinalizeEvent != null)
          startActivity.setDuration(activityFinalizeEvent.getOccurrenceTime()
              - this.simulationStep.getStepTime());

        Activities activities = currentEvent.getActivities();
        if (activities == null) {
          activities = this.objectFactory
              .createEnvSimInputEventTypeActivities();
          currentEvent.setActivities(activities);
        }

        // state changes
        this.notifyStateChanges(activity, startActivity);

        activities.getStartActivity().add(startActivity);
      }
    }
  }

  protected void notifyStateChanges(Activity activity, ActivityType activityType) {

    if (!(dataCollector.isPropChangeListPhysObjIsEmpty()
        && dataCollector.isPropChangeListPhysAgentIsEmpty()
        && dataCollector.isPropChangeListObjIsEmpty()
        && dataCollector.isPropChangeListAgentIsEmpty()
        && dataCollector.isCollectionEventListIsEmpty()
        && dataCollector.isObjDestroyListIsEmpty() && dataCollector
        .isPropChangeListGridCellIsEmpty())) {

      ResultingStateChangesType resultingStateChangesType = objectFactory
          .createResultingStateChangesType();

      // aors.logger.model.EnvSimInputEventType.ResultingStateChanges
      // resultingStateChanges = objectFactory
      // .createEnvSimInputEventTypeResultingStateChanges();
      activityType.setResultingStateChanges(resultingStateChangesType);

      Objects objs = this.getStateChangedObjects();
      if (objs != null)
        resultingStateChangesType.setObjects(objs);

      Agents agts = this.getStateChangedAgents();
      if (agts != null)
        resultingStateChangesType.setAgents(agts);

      PhysicalObjects physObjs = this.getStateChangedPhysicalObjects();
      if (physObjs != null)
        resultingStateChangesType.setPhysicalObjects(physObjs);

      PhysicalAgents physAgts = this.getStateChangedPhysicalAgents();
      if (physAgts != null)
        resultingStateChangesType.setPhysicalAgents(physAgts);

      // GridCells
      GridCells gridCells = this.getStateChangedGridCells();
      if (gridCells != null) {
        resultingStateChangesType.setGridCells(gridCells);
      }

      // create
      this.notifyCreations(resultingStateChangesType);

      Collections colls = this.getStateChangedCollections();
      if (colls != null)
        resultingStateChangesType.setCollections(colls);

      // destroy Obj
      this.notifyDestroy(resultingStateChangesType);
    } // fi statechanges

  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code notifyActivityStop} from super class
   * 
   * 
   * 
   * @param activity
   * @param envEventList
   *          - normally there are only CausedEvents to start the nextActivity
   */
  @Override
  public void notifyActivityStop(Activity activity,
      Collection<EnvironmentEvent> envEventList) {

    if (AbstractSimulator.runLogger) {

      int indexOfCurrentEvent = this.simulationStep
          .getEnvironmentSimulatorStep().getEnvSimInputEvent().size() - 1;
      if (indexOfCurrentEvent >= 0) {
        EnvSimInputEventType currentEvent = this.simulationStep
            .getEnvironmentSimulatorStep().getEnvSimInputEvent().get(
                indexOfCurrentEvent).getValue();

        FinalizeActivity finalizeActivity = this.objectFactory
            .createEnvSimInputEventTypeActivitiesFinalizeActivity();
        finalizeActivity.setActivityName(activity.getName());

        if (!activity.getEndEventCorrelationProperty().equals("")) {
          finalizeActivity.setCorrelationProperty(activity
              .getEndEventCorrelationProperty());
          if (activity.getCorrelationValue() != null) {
            finalizeActivity.setCorrelationValue(activity.getCorrelationValue()
                .toString());
          } else {
            finalizeActivity.setCorrelationValue("unset");
          }
        }

        if (!envEventList.isEmpty()) {

          aors.logger.model.EnvSimInputEventType.Activities.FinalizeActivity.ResultingEvents resultingEvents = this.objectFactory
              .createEnvSimInputEventTypeActivitiesFinalizeActivityResultingEvents();

          finalizeActivity.setResultingEvents(resultingEvents);
          // set the created causedEvents for the nextActivity
          for (EnvironmentEvent envEvent : envEventList) {

            if (CausedEvent.class.isInstance(envEvent)) {

              CausedEvent aorCausedEvent = (CausedEvent) envEvent;

              aors.logger.model.EnvSimInputEventType.Activities.FinalizeActivity.ResultingEvents.CausedEvent causedEvent = this.objectFactory
                  .createEnvSimInputEventTypeActivitiesFinalizeActivityResultingEventsCausedEvent();

              causedEvent.setType(aorCausedEvent.getType());
              causedEvent.setDelay(aorCausedEvent.getOccurrenceTime()
                  - this.simulationStep.getStepTime());
              // if
              // (!aorCausedEvent.getStartEventCorrelationProperty().equals(""))
              // {
              // causedEvent.setCorrelation(aorCausedEvent
              // .getStartEventCorrelationProperty());
              // }
              this.setObjectSlots(aorCausedEvent,
                  (CausedEventType) causedEvent, CausedEvent.class);
              resultingEvents.getCausedEvent().add(causedEvent);
            }
          }

        }

        Activities activities = currentEvent.getActivities();
        if (activities == null) {
          activities = this.objectFactory
              .createEnvSimInputEventTypeActivities();
          currentEvent.setActivities(activities);
        }

        // state changes
        this.notifyStateChanges(activity, finalizeActivity);

        activities.getFinalizeActivity().add(finalizeActivity);
      }
    }
  }

  @Override
  public void notifyEnvEventResult(EnvironmentRule environmentRule,
      Collection<EnvironmentEvent> envEventList) {

    if (AbstractSimulator.runLogger) {

      // we differentiate here between PhySimKinematicsRule and other rules,
      // because we have different resultingStateChanges- and
      // resultingEventsTypes
      // in the log-schema (=> jaxb); ones for PhysicsSimulationType and ones
      // for
      // EnvSimInputEventType
      if (environmentRule instanceof PhySimKinematicsRule) {
        PhysicsSimulationType physicsSimulationType = this.simulationStep
            .getEnvironmentSimulatorStep().getPhysicsSimulation();
        if (physicsSimulationType != null) {

          // state changes
          this.notifyStateChanges(environmentRule, physicsSimulationType);

          // resulting events
          // we have null, if there was an event from autoKinematics
          // => autoKinematics have no resultingevents
          if (envEventList != null)
            this.notifyResultingEvents(environmentRule, envEventList,
                physicsSimulationType);
        }

      } else {
        // get the last(current) logged event
        int indexOfCurrentEvent = this.simulationStep
            .getEnvironmentSimulatorStep().getEnvSimInputEvent().size() - 1;
        if (indexOfCurrentEvent >= 0) {
          EnvSimInputEventType currentEvent = this.simulationStep
              .getEnvironmentSimulatorStep().getEnvSimInputEvent().get(
                  indexOfCurrentEvent).getValue();

          // state changes
          // if environmentRule == null; then the result is created by an
          // automapping
          if (environmentRule != null)
            this.notifyStateChanges(environmentRule, currentEvent);

          // resulting events
          this.notifyResultingEvents(environmentRule, envEventList,
              currentEvent);
        }
      }
    }
  }

  protected void notifyResultingEvents(EnvironmentRule environmentRule,
      Collection<EnvironmentEvent> envEventList,
      EnvSimInputEventType currentEvent) {
    if (!envEventList.isEmpty()) {

      ResultingEvents resultingEvents = objectFactory
          .createEnvSimInputEventTypeResultingEvents();

      String ruleName = "AUTOCREATED";
      if (environmentRule != null) {
        ruleName = environmentRule.getName();
      }
      resultingEvents.setResultingFromRule(ruleName);
      currentEvent.getResultingEvents().add(resultingEvents);

      // log the ResultingEvents
      for (EnvironmentEvent resEvt : envEventList) {

        if (resEvt instanceof InMessageEvent) {

          InMessageEvent aorInMessageEvent = (InMessageEvent) resEvt;

          InMessageEventType inMessageEvent = objectFactory
              .createInMessageEventType();

          MessageType message = objectFactory.createMessageType();

          message.setType(aorInMessageEvent.getMessage().getType());
          message.setId(aorInMessageEvent.getMessage().getId());
          message.setName(aorInMessageEvent.getMessage().getName());
          inMessageEvent.setOccurrenceTime(aorInMessageEvent
              .getOccurrenceTime());
          inMessageEvent.setSenderIdRef(aorInMessageEvent.getSenderIdRef());
          inMessageEvent
              .setReceiverIdRef(aorInMessageEvent.getPerceiverIdRef());
          inMessageEvent.setMessage(message);

          // set the message-slots
          Class<?> c = aorInMessageEvent.getMessage().getClass();
          do {
            Field[] fields = c.getDeclaredFields();

            for (Field field : fields) {
              field.setAccessible(true);
              SlotType slotType = objectFactory.createSlotType();
              slotType.setProperty(field.getName());
              try {
                slotType.setValue(String.valueOf(field.get(aorInMessageEvent
                    .getMessage())));
              } catch (IllegalArgumentException e) {
                e.printStackTrace();
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
              message.getSlot().add(slotType);
            }

            c = c.getSuperclass();

          } while (c != Message.class);

          resultingEvents.getCausedEventOrPerceptionEventOrInMessageEvent()
              .add(inMessageEvent);

        } else if (resEvt instanceof PerceptionEvent) {

          resultingEvents.getCausedEventOrPerceptionEventOrInMessageEvent()
              .add(this.getResultingPerceptionEvent((PerceptionEvent) resEvt));

        } else if (resEvt instanceof ActivityStartEvent) {

          resultingEvents
              .getCausedEventOrPerceptionEventOrInMessageEvent()
              .add(
                  this
                      .getResultungActivityStartEvent((ActivityStartEvent) resEvt));

        } else if (resEvt instanceof ActivityEndEvent) {

          resultingEvents
              .getCausedEventOrPerceptionEventOrInMessageEvent()
              .add(this.getResultungActivityEndEvent((ActivityEndEvent) resEvt));

        } else if (resEvt instanceof CausedEvent) {

          resultingEvents.getCausedEventOrPerceptionEventOrInMessageEvent()
              .add(this.getResultingCausedEvent((CausedEvent) resEvt));
        } else {
          System.err.println("Unknown resultingevent!");
        }
      }
    }
  }

  protected void notifyResultingEvents(EnvironmentRule environmentRule,
      Collection<EnvironmentEvent> envEventList,
      PhysicsSimulationType physicsSimulationType) {

    if (!envEventList.isEmpty()) {

      aors.logger.model.PhysicsSimulationType.ResultingEvents resultingEvents = objectFactory
          .createPhysicsSimulationTypeResultingEvents();

      physicsSimulationType.getResultingEvents().add(resultingEvents);

      // log the ResultingEvents
      for (EnvironmentEvent resEvt : envEventList) {

        if (resEvt instanceof PerceptionEvent) {

          resultingEvents.getCausedEventOrPerceptionEvent().add(
              this.getResultingPerceptionEvent((PerceptionEvent) resEvt));

        } else if (resEvt instanceof CausedEvent) {

          resultingEvents.getCausedEventOrPerceptionEvent().add(
              this.getResultingCausedEvent((CausedEvent) resEvt));
        } else {
          System.err.println("Unknown resultingevent!");
        }
      }
    }
  }

  protected PerceptionEventType getResultingPerceptionEvent(
      PerceptionEvent aorPercEvt) {
    PerceptionEventType perceptionEventType = objectFactory
        .createPerceptionEventType();

    perceptionEventType.setType(aorPercEvt.getType());

    this.setObjectSlots(aorPercEvt, perceptionEventType, PerceptionEvent.class);

    // set the perceiver
    if (aorPercEvt.getPerceiverIdRef() != 0) {
      perceptionEventType.setPerceiverIdRef(aorPercEvt.getPerceiverIdRef());
    }

    return perceptionEventType;
  }

  protected CausedEventType getResultingCausedEvent(CausedEvent aorCausedEvent) {
    CausedEventType causedEventType = objectFactory.createCausedEventType();

    causedEventType.setType(aorCausedEvent.getType());
    causedEventType.setDelay(aorCausedEvent.getOccurrenceTime()
        - this.simulationStep.getStepTime());

    this.setObjectSlots(aorCausedEvent, causedEventType, CausedEvent.class);
    return causedEventType;
  }

  protected ActivityStartEventType getResultungActivityStartEvent(
      ActivityStartEvent aorActivityStartEvent) {
    ActivityStartEventType activityStartEventType = objectFactory
        .createActivityStartEventType();

    activityStartEventType.setActivity(aorActivityStartEvent.getActivityType());
    activityStartEventType.setDelay(aorActivityStartEvent.getOccurrenceTime()
        - this.simulationStep.getStepTime());

    return activityStartEventType;
  }

  protected ActivityEndEventType getResultungActivityEndEvent(
      ActivityEndEvent aorActivityEndEvent) {
    ActivityEndEventType activityEndEventType = objectFactory
        .createActivityEndEventType();

    activityEndEventType.setActivity(aorActivityEndEvent.getActivityType());
    activityEndEventType.setDelay(aorActivityEndEvent.getOccurrenceTime()
        - this.simulationStep.getStepTime());

    return activityEndEventType;
  }

  /**
   * Notify statechanges for EnvironmentSimulator InputEvents
   * 
   * @param environmentRule
   * @param envSimInputEvent
   */
  protected void notifyStateChanges(EnvironmentRule environmentRule,
      EnvSimInputEventType envSimInputEvent) {
    if (dataCollector.isStateChange()) {

      ResultingStateChangesType resultingStateChangesType = objectFactory
          .createResultingStateChangesType();

      // aors.logger.model.EnvSimInputEventType.ResultingStateChanges
      // resultingStateChanges = objectFactory
      // .createEnvSimInputEventTypeResultingStateChanges();
      resultingStateChangesType.setResultingFromRule(environmentRule.getName());
      envSimInputEvent.getResultingStateChanges()
          .add(resultingStateChangesType);

      Objects objs = this.getStateChangedObjects();
      if (objs != null)
        resultingStateChangesType.setObjects(objs);

      Agents agts = this.getStateChangedAgents();
      if (agts != null)
        resultingStateChangesType.setAgents(agts);

      PhysicalObjects physObjs = this.getStateChangedPhysicalObjects();
      if (physObjs != null)
        resultingStateChangesType.setPhysicalObjects(physObjs);

      PhysicalAgents physAgts = this.getStateChangedPhysicalAgents();
      if (physAgts != null)
        resultingStateChangesType.setPhysicalAgents(physAgts);

      // GridCells
      GridCells gridCells = this.getStateChangedGridCells();
      if (gridCells != null) {
        resultingStateChangesType.setGridCells(gridCells);
      }

      // create
      this.notifyCreations(resultingStateChangesType);

      Collections colls = this.getStateChangedCollections();
      if (colls != null)
        resultingStateChangesType.setCollections(colls);

      // destroy Obj
      this.notifyDestroy(resultingStateChangesType);
    } // fi statechanges
  }

  /**
   * Notify statechanges from PysicSimulation
   * 
   * @param environmentRule
   * @param physicsSimulationType
   */
  protected void notifyStateChanges(EnvironmentRule environmentRule,
      PhysicsSimulationType physicsSimulationType) {

    if (!(dataCollector.isPropChangeListPhysObjIsEmpty()
        && dataCollector.isPropChangeListPhysAgentIsEmpty() && dataCollector
        .isCollectionEventListIsEmpty())) {
      aors.logger.model.PhysicsSimulationType.ResultingStateChanges resultingStateChanges = objectFactory
          .createPhysicsSimulationTypeResultingStateChanges();
      physicsSimulationType.getResultingStateChanges().add(
          resultingStateChanges);

      PhysicalObjects physObjs = this.getStateChangedPhysicalObjects();
      if (physObjs != null)
        resultingStateChanges.setPhysicalObjects(physObjs);

      PhysicalAgents physAgts = this.getStateChangedPhysicalAgents();
      if (physAgts != null)
        resultingStateChanges.setPhysicalAgents(physAgts);

      Collections colls = this.getStateChangedCollections();
      if (colls != null)
        resultingStateChanges.setCollections(colls);
    }

  }

  protected Objects getStateChangedObjects() {

    Objects objects = null;

    if (!dataCollector.isPropChangeListObjIsEmpty()) {

      objects = objectFactory.createObjects();

      while (!dataCollector.isPropChangeListObjIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextObjektChange();
        Objekt aorObjekt = (Objekt) propertyChangeEvent.getSource();

        ObjType objType = objectFactory.createObjType();
        objects.getObj().add(objType);

        if (aorObjekt.getId() != 0) {
          objType.setId(aorObjekt.getId());
        }

        String property = propertyChangeEvent.getPropertyName();
        String value = "";
        long refId = 0;
        if (propertyChangeEvent.getNewValue() != null) {
          if (propertyChangeEvent.getNewValue() instanceof Objekt) {
            refId = ((Objekt) propertyChangeEvent.getNewValue()).getId();
          } else {
            value = propertyChangeEvent.getNewValue().toString();
          }
        }

        SlotType slotType = objectFactory.createSlotType();
        slotType.setProperty(property);
        if (!value.equals(""))
          slotType.setValue(value);
        if (refId != 0)
          slotType.setRefId(refId);

        objType.getSlot().add(slotType);

      }
    } // fi objekt state changes
    return objects;
  }

  protected Agents getStateChangedAgents() {

    Agents agents = null;

    if (!dataCollector.isPropChangeListAgentIsEmpty()) {

      agents = objectFactory.createAgents();

      while (!dataCollector.isPropChangeListAgentIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextAgentChange();
        AgentObject aorAgentObject = (AgentObject) propertyChangeEvent
            .getSource();

        AgtType agtType = objectFactory.createAgtType();
        agents.getAgt().add(agtType);

        if (aorAgentObject.getId() != 0) {
          agtType.setId(aorAgentObject.getId());
        }

        String property = propertyChangeEvent.getPropertyName();
        String value = "";
        long refId = 0;
        if (propertyChangeEvent.getNewValue() != null) {
          if (propertyChangeEvent.getNewValue() instanceof Objekt) {
            refId = ((Objekt) propertyChangeEvent.getNewValue()).getId();
          } else {
            value = propertyChangeEvent.getNewValue().toString();
          }
        }

        SlotType slotType = objectFactory.createSlotType();
        slotType.setProperty(property);
        if (!value.equals(""))
          slotType.setValue(value);
        if (refId != 0)
          slotType.setRefId(refId);

        agtType.getSlot().add(slotType);

      }
    } // fi objekt state changes
    return agents;
  }

  protected PhysicalObjects getStateChangedPhysicalObjects() {

    PhysicalObjects physicalObjects = null;

    if (!dataCollector.isPropChangeListPhysObjIsEmpty()) {

      physicalObjects = objectFactory.createPhysicalObjects();

      while (!dataCollector.isPropChangeListPhysObjIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextPhysObjektChange();
        PhysicalObject aorPhysObject = (PhysicalObject) propertyChangeEvent
            .getSource();

        PhysicalObjType physicalObjType = objectFactory.createPhysicalObjType();
        physicalObjects.getPhysObj().add(physicalObjType);

        if (aorPhysObject.getId() != 0) {
          physicalObjType.setId(aorPhysObject.getId());
        }

        String property = propertyChangeEvent.getPropertyName();
        String value = "";
        long refId = 0;
        if (propertyChangeEvent.getNewValue() != null) {
          if (propertyChangeEvent.getNewValue() instanceof Objekt) {
            refId = ((Objekt) propertyChangeEvent.getNewValue()).getId();
          } else {
            value = propertyChangeEvent.getNewValue().toString();
          }
        }

        if (refId != 0 || !this.setAttribute(property, value, physicalObjType)) {

          SlotType slotType = objectFactory.createSlotType();
          slotType.setProperty(property);
          if (!value.equals(""))
            slotType.setValue(value);
          if (refId != 0)
            slotType.setRefId(refId);
          physicalObjType.getSlot().add(slotType);
        }
      }
    } // fi physObject state changes
    return physicalObjects;
  }

  protected PhysicalAgents getStateChangedPhysicalAgents() {

    PhysicalAgents physicalAgents = null;

    if (!dataCollector.isPropChangeListPhysAgentIsEmpty()) {

      physicalAgents = objectFactory.createPhysicalAgents();

      while (!dataCollector.isPropChangeListPhysAgentIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextPhysAgentChange();
        PhysicalAgentObject aorPhysAgentObject = (PhysicalAgentObject) propertyChangeEvent
            .getSource();

        PhysAgtType physAgtType = objectFactory.createPhysAgtType();
        physicalAgents.getPhysAgt().add(physAgtType);

        if (aorPhysAgentObject.getId() != 0) {
          physAgtType.setId(aorPhysAgentObject.getId());
        }

        String property = propertyChangeEvent.getPropertyName();
        String value = "";
        long refId = 0;
        if (propertyChangeEvent.getNewValue() != null) {
          if (propertyChangeEvent.getNewValue() instanceof Objekt) {
            refId = ((Objekt) propertyChangeEvent.getNewValue()).getId();
          } else {
            value = propertyChangeEvent.getNewValue().toString();
          }
        }

        if (refId != 0 || !this.setAttribute(property, value, physAgtType)) {

          SlotType slotType = objectFactory.createSlotType();
          slotType.setProperty(property);
          if (!value.equals(""))
            slotType.setValue(value);
          if (refId != 0)
            slotType.setRefId(refId);
          physAgtType.getSlot().add(slotType);
        }
      }
    } // fi agent state changes
    return physicalAgents;
  }

  protected GridCells getStateChangedGridCells() {
    GridCells gridCells = null;

    if (!this.dataCollector.isPropChangeListGridCellIsEmpty()) {

      gridCells = objectFactory.createGridCells();

      while (!this.dataCollector.isPropChangeListGridCellIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextGridCellChange();
        AbstractCell abstractCell = (AbstractCell) propertyChangeEvent
            .getSource();

        GridCellType gridCellType = objectFactory.createGridCellType();
        gridCellType.setX(abstractCell.getPosX());
        gridCellType.setY(abstractCell.getPosY());
        gridCells.getGridCell().add(gridCellType);

        SlotType slotType = objectFactory.createSlotType();
        slotType.setProperty(propertyChangeEvent.getPropertyName());
        slotType.setValue(propertyChangeEvent.getNewValue().toString());
        gridCellType.getSlot().add(slotType);

      }
    }
    return gridCells;
  }

  @SuppressWarnings("unchecked")
  protected Collections getStateChangedCollections() {

    Collections collections = null;
    if (!dataCollector.isCollectionEventListIsEmpty()) {

      collections = objectFactory.createCollections();

      while (!dataCollector.isCollectionEventListIsEmpty()) {
        CollectionEvent collectionEvent = dataCollector
            .getNextCollectionEvent();

        CollectionType collectionType = objectFactory.createCollectionType();
        ObjType objType = objectFactory.createObjType();

        AORCollection<Objekt> aorCollection = (AORCollection<Objekt>) collectionEvent
            .getSource();
        Objekt aorCollectionObjekt = collectionEvent.getObjekt();
        if (aorCollectionObjekt != null) {
          // set the aorobjektinfos
          objType.setId(aorCollectionObjekt.getId());
          objType.setType(aorCollectionObjekt.getType());
          if (!aorCollectionObjekt.getName().equals("")) {
            objType.setName(aorCollectionObjekt.getName());
          }
        } else {
          objType.setType("Null");
        }
        collectionType.setObj(objType);

        // set the collection-infos
        if (aorCollection.getId() != 0) {
          collectionType.setId(aorCollection.getId());
        }
        if (!aorCollection.getName().equals("")) {
          collectionType.setName(aorCollection.getName());
        }

        collectionType.setAction(collectionEvent.getCollectionAction()
            .toString());
        collectionType.setNewSize(collectionEvent.getCurrentSize());

        collections.getColl().add(collectionType);
      }
    } // fi collectionEvents
    return collections;
  }

  protected void notifyCreations(
      ResultingStateChangesType resultingStateChangeType) {

    boolean isCreate = false;
    aors.logger.model.ResultingStateChangesType.Create create = this.objectFactory
        .createResultingStateChangesTypeCreate();

    Objects objects = this.notifyObjektInitialisation();
    if (!objects.getObj().isEmpty()) {
      create.setObjects(objects);
      isCreate = true;
    }

    Agents agents = this.notifyAgentInitialisation();
    if (!agents.getAgt().isEmpty()) {
      create.setAgents(agents);
      isCreate = true;
    }

    PhysicalObjects physicalObjects = this.notifyPhysObjInitialisation();
    if (!physicalObjects.getPhysObj().isEmpty()) {
      create.setPhysicalObjects(physicalObjects);
      isCreate = true;
    }

    PhysicalAgents physicalAgents = this.notifyPhysAgtInitialisation();
    if (!physicalAgents.getPhysAgt().isEmpty()) {
      create.setPhysicalAgents(physicalAgents);
      isCreate = true;
    }

    if (isCreate)
      resultingStateChangeType.setCreate(create);
  }

  protected void notifyDestroy(ResultingStateChangesType resultingStateChanges) {
    if (!dataCollector.isObjDestroyListIsEmpty()) {
      aors.logger.model.ResultingStateChangesType.DestroyObjects destroyObjects = this.objectFactory
          .createResultingStateChangesTypeDestroyObjects();

      DestroyObjectType destroyObjectType;

      while (!dataCollector.isObjDestroyListIsEmpty()) {
        ObjektDestroyEvent objektDestroyEvent = dataCollector
            .getNextObjektDestroyEvent();
        Objekt aorObjekt = (Objekt) objektDestroyEvent.getSource();

        destroyObjectType = objectFactory.createDestroyObjectType();

        if (aorObjekt.getId() != 0) {
          destroyObjectType.setId(aorObjekt.getId());
        }

        if (!aorObjekt.getName().equals("")) {
          destroyObjectType.setObjectName(aorObjekt.getName());
        }

        destroyObjectType.setObjectType(aorObjekt.getType());

        destroyObjects.getDestroyObj().add(destroyObjectType);
      }

      resultingStateChanges.setDestroyObjects(destroyObjects);
    }
  }

  @Override
  public void notifyInitialisation() {
    dataCollector.deleteAllBuffer();
  }

  protected Objects notifyObjektInitialisation() {

    Objects objects = objectFactory.createObjects();

    // get the initStates from objects (as HashMap<String, String>)
    while (!dataCollector.isObjInitListIsEmpty()) {

      ObjektInitEvent objInitEvent = dataCollector.getNextObjektInitEvent();
      Objekt aorObjekt = (Objekt) objInitEvent.getSource();

      ObjType objType = objectFactory.createObjType();

      this.setEntityValues(aorObjekt, objType);
      // currently without effect
      this.setObjektValues(aorObjekt, objType);

      objType.getSlot().addAll(
          this.getStringProperties(aorObjekt.getInheritedProperty()
              .getStringProperties()));
      objType.getSlot().addAll(
          this.getBooleanProperties(aorObjekt.getInheritedProperty()
              .getBooleanProperties()));
      objType.getSlot().addAll(
          this.getLongProperties(aorObjekt.getInheritedProperty()
              .getLongProperties()));
      objType.getSlot().addAll(
          this.getDoubleProperties(aorObjekt.getInheritedProperty()
              .getDoubleProperties()));
      objType.getSlot().addAll(
          this.getObjektProperties(aorObjekt.getInheritedProperty()
              .getObjektProperties()));

      objects.getObj().add(objType);
    }
    return objects;
  }

  protected Agents notifyAgentInitialisation() {

    Agents agents = this.objectFactory.createAgents();

    // get the initStates from objects (as HashMap<String, String>)
    while (!dataCollector.isAgentInitListIsEmpty()) {

      ObjektInitEvent objInitEvent = dataCollector.getNextAgentInitEvent();
      AgentObject aorAgentObject = (AgentObject) objInitEvent.getSource();

      AgtType agtType = objectFactory.createAgtType();

      this.setEntityValues(aorAgentObject, agtType);

      agtType.getSlot().addAll(
          this.getStringProperties(aorAgentObject.getInheritedProperty()
              .getStringProperties()));
      agtType.getSlot().addAll(
          this.getBooleanProperties(aorAgentObject.getInheritedProperty()
              .getBooleanProperties()));
      agtType.getSlot().addAll(
          this.getLongProperties(aorAgentObject.getInheritedProperty()
              .getLongProperties()));
      agtType.getSlot().addAll(
          this.getDoubleProperties(aorAgentObject.getInheritedProperty()
              .getDoubleProperties()));
      agtType.getSlot().addAll(
          this.getObjektProperties(aorAgentObject.getInheritedProperty()
              .getObjektProperties()));

      agents.getAgt().add(agtType);
    }
    return agents;
  }

  protected PhysicalObjects notifyPhysObjInitialisation() {

    PhysicalObjects physicalObjects = objectFactory.createPhysicalObjects();

    // get the initStates from physicalobjects (as HashMap<String, String>)
    while (!dataCollector.isPhysObjInitListIsEmpty()) {

      ObjektInitEvent physObjInitEvent = dataCollector
          .getNextPhysObjInitEvent();
      PhysicalObject aorPhysObject = (PhysicalObject) physObjInitEvent
          .getSource();

      PhysicalObjType physicalObjType = objectFactory.createPhysicalObjType();

      this.setEntityValues(aorPhysObject, physicalObjType);
      this.setObjektValues(aorPhysObject, physicalObjType);
      this.setPhysObjValues(aorPhysObject, physicalObjType);

      physicalObjType.getSlot().addAll(
          this.getStringProperties(aorPhysObject.getInheritedProperty()
              .getStringProperties()));
      physicalObjType.getSlot().addAll(
          this.getBooleanProperties(aorPhysObject.getInheritedProperty()
              .getBooleanProperties()));
      physicalObjType.getSlot().addAll(
          this.getLongProperties(aorPhysObject.getInheritedProperty()
              .getLongProperties()));
      physicalObjType.getSlot().addAll(
          this.getDoubleProperties(aorPhysObject.getInheritedProperty()
              .getDoubleProperties()));

      physicalObjects.getPhysObj().add(physicalObjType);
    }
    return physicalObjects;
  }

  protected PhysicalAgents notifyPhysAgtInitialisation() {

    PhysicalAgents physicalAgents = objectFactory.createPhysicalAgents();

    // get the initstates from physicalAgents (as HashMap<String, String>)
    while (!dataCollector.isPhysAgentInitListIsEmpty()) {

      ObjektInitEvent physObjInitEvent = dataCollector
          .getNextPhysAgentInitEvent();
      PhysicalAgentObject aorAgentObject = (PhysicalAgentObject) physObjInitEvent
          .getSource();

      PhysAgtType physAgtType = objectFactory.createPhysAgtType();

      this.setEntityValues(aorAgentObject, physAgtType);
      this.setPhysObjValues(aorAgentObject, physAgtType);
      this.setPhysicalAgentObjectValues(aorAgentObject, physAgtType);

      physAgtType.getSlot().addAll(
          this.getStringProperties(aorAgentObject.getInheritedProperty()
              .getStringProperties()));
      physAgtType.getSlot().addAll(
          this.getBooleanProperties(aorAgentObject.getInheritedProperty()
              .getBooleanProperties()));
      physAgtType.getSlot().addAll(
          this.getLongProperties(aorAgentObject.getInheritedProperty()
              .getLongProperties()));
      physAgtType.getSlot().addAll(
          this.getDoubleProperties(aorAgentObject.getInheritedProperty()
              .getDoubleProperties()));

      physicalAgents.getPhysAgt().add(physAgtType);
    }
    return physicalAgents;
  }

  protected ArrayList<SlotType> getStringProperties(HashMap<String, String> map) {
    ArrayList<SlotType> result = new ArrayList<SlotType>();
    for (String boolProp : map.keySet()) {
      SlotType slotType = objectFactory.createSlotType();
      slotType.setProperty(boolProp);
      slotType.setValue(String.valueOf(map.get(boolProp)));
      result.add(slotType);
    }
    return result;
  }

  protected ArrayList<SlotType> getBooleanProperties(
      HashMap<String, Boolean> map) {
    ArrayList<SlotType> result = new ArrayList<SlotType>();
    for (String boolProp : map.keySet()) {
      SlotType slotType = objectFactory.createSlotType();
      slotType.setProperty(boolProp);
      slotType.setValue(String.valueOf(map.get(boolProp)));
      result.add(slotType);
    }
    return result;
  }

  protected ArrayList<SlotType> getLongProperties(HashMap<String, Long> map) {
    ArrayList<SlotType> result = new ArrayList<SlotType>();
    for (String longProp : map.keySet()) {
      SlotType slotType = objectFactory.createSlotType();
      slotType.setProperty(longProp);
      slotType.setValue(String.valueOf(map.get(longProp)));
      result.add(slotType);
    }
    return result;
  }

  protected ArrayList<SlotType> getDoubleProperties(HashMap<String, Double> map) {
    ArrayList<SlotType> result = new ArrayList<SlotType>();
    for (String doubleProp : map.keySet()) {
      SlotType slotType = objectFactory.createSlotType();
      slotType.setProperty(doubleProp);
      slotType.setValue(String.valueOf(map.get(doubleProp)));
      result.add(slotType);
    }
    return result;
  }

  protected ArrayList<SlotType> getObjektProperties(HashMap<String, Objekt> map) {
    ArrayList<SlotType> result = new ArrayList<SlotType>();
    for (String objektProp : map.keySet()) {
      SlotType slotType = objectFactory.createSlotType();
      slotType.setProperty(objektProp);
      // slotType.setValue("null");
      Objekt o = map.get(objektProp);
      if (o != null) {
        slotType.setRefId(map.get(objektProp).getId());
      } else {
        slotType.setValue("Null");
      }
      result.add(slotType);
    }
    return result;
  }

  @Override
  public void notifySimStepEnd() {
    if (AbstractSimulator.runLogger) {
      this.lastSimulationStep = this.simulationStep;
      this.simulationStep = null;
    }
  }

  @Override
  public void notifySimStepStart(Long aorStepTime) {

    if (AbstractSimulator.runLogger) {

      this.simulationStep = objectFactory.createSimulationStep();
      this.simulationStep.setStepTime(aorStepTime);

      EnvironmentSimulatorStep environmentSimulatorStep = objectFactory
          .createEnvironmentSimulatorStep();
      this.simulationStep.setEnvironmentSimulatorStep(environmentSimulatorStep);

    }
  }

  @Override
  public void notifyAgentSimulatorStep(JsonData agentStepLog) {

  }

  @Override
  public void notifyAgentSimulatorStep(AgentSimulatorStep agentSimulatorStep) {

  }

  @Override
  public void notifySimulationScenario(ScenarioInfos scenarioInfos,
      GeneralSimulationParameters aorSimParameters,
      Map<String, String> modelParamMap) {
  }

  @Override
  public void notifySimulationStart(long startTime, long steps) {

  }

  @Override
  public void notifySpaceModel(GeneralSpaceModel aorSpaceModel) {

  }

  @Override
  public void notifyStart() {
  }

  // privates //
  /**
   * check if the property is an attribute in JAXB and set it notice: if
   * valueStr not convertible to a double (e.g. it is from a boolean or from a
   * string), then return false; that means, if there in the future an attribute
   * that isn't a double, we have to handle it explicit
   * 
   * @param propertie
   *          - is the name of the property
   * @param valueStr
   *          - is the value of the property as a String
   * @param physicalObjectTyp
   *          - the JaxB object
   * 
   * @return true - if is an Attribute, otherwise false
   */
  protected boolean setAttribute(String propertie, String valueStr,
      PhysicalObjectType physicalObjectType) {

    try {
      Double value = Double.valueOf(valueStr);
      if (propertie.equals(Physical.PROP_X)) {
        physicalObjectType.setX(value);
      } else if (propertie.equals(Physical.PROP_Y)) {
        physicalObjectType.setY(value);
      } else if (propertie.equals(Physical.PROP_Z)) {
        physicalObjectType.setY(value);
      } else if (propertie.equals(Physical.PROP_VX)) {
        physicalObjectType.setVx(value);
      } else if (propertie.equals(Physical.PROP_VY)) {
        physicalObjectType.setVy(value);
      } else if (propertie.equals(Physical.PROP_VZ)) {
        physicalObjectType.setVz(value);
      } else if (propertie.equals(Physical.PROP_AX)) {
        physicalObjectType.setAx(value);
      } else if (propertie.equals(Physical.PROP_AY)) {
        physicalObjectType.setAy(value);
      } else if (propertie.equals(Physical.PROP_AZ)) {
        physicalObjectType.setAz(value);
      } else if (propertie.equals(Physical.PROP_ROTATION_ANGLE_X)) {
        physicalObjectType.setRotationAngleX(value);
      } else if (propertie.equals(Physical.PROP_ROTATION_ANGLE_Y)) {
        physicalObjectType.setRotationAngleY(value);
      } else if (propertie.equals(Physical.PROP_ROTATION_ANGLE_Z)) {
        physicalObjectType.setRotationAngleZ(value);
      } else if (propertie.equals(Physical.PROP_DEPTH)) {
        physicalObjectType.setDepth(value);
      } else if (propertie.equals(Physical.PROP_WIDTH)) {
        physicalObjectType.setWidth(value);
      } else if (propertie.equals(Physical.PROP_HEIGHT)) {
        physicalObjectType.setHeight(value);
      } else if (propertie.equals(Physical.PROP_M)) {
        physicalObjectType.setM(value);
      } else {
        return false;
      }
    } catch (NumberFormatException e) {
      if (propertie.equals(Physical.PROP_MATERIALTYPE)) {
        physicalObjectType.setMaterialType(valueStr);
        return true;
      } else if (propertie.equals(Physical.PROP_SHAPE2D)) {
        physicalObjectType.setShape2D(valueStr);
        return true;
      } else if (propertie.equals(Physical.PROP_SHAPE3D)) {
        physicalObjectType.setShape3D(valueStr);
        return true;
      } else if (propertie.equals(Physical.PROP_POINTS)) {
        physicalObjectType.setPoints(valueStr);
        return true;
      }
      return false;
    }
    return true;
  }

  protected void setEntityValues(Entity aorEntity, EntityType entityType) {

    if (aorEntity.getId() != 0) {
      entityType.setId(aorEntity.getId());
    }
    if (!aorEntity.getName().equals("")) {
      entityType.setName(aorEntity.getName());
    }
    entityType.setType(aorEntity.getType());
  }

  protected void setObjektValues(Objekt aorObjekt, ObjectType objectType) {
    // do nothing, because currently we have no additional values here
  }

  protected void setPhysObjValues(Physical aorPhysObj,
      PhysicalObjectType physicalObjectType) {

    physicalObjectType.setX(aorPhysObj.getX());
    physicalObjectType.setY(aorPhysObj.getY());
    physicalObjectType.setZ((aorPhysObj.getZ()));
    physicalObjectType.setVx(aorPhysObj.getVx());
    physicalObjectType.setVy(aorPhysObj.getVy());
    physicalObjectType.setVz(aorPhysObj.getVz());
    physicalObjectType.setAx(aorPhysObj.getAx());
    physicalObjectType.setAy(aorPhysObj.getAy());
    physicalObjectType.setAz(aorPhysObj.getAz());
    physicalObjectType.setDepth(aorPhysObj.getDepth());
    physicalObjectType.setWidth(aorPhysObj.getWidth());
    physicalObjectType.setHeight(aorPhysObj.getHeight());
    physicalObjectType.setM(aorPhysObj.getM());
    physicalObjectType.setAlphaX(aorPhysObj.getAlphaX());
    physicalObjectType.setAlphaY(aorPhysObj.getAlphaY());
    physicalObjectType.setAlphaZ(aorPhysObj.getAlphaZ());
    physicalObjectType.setOmegaX(aorPhysObj.getOmegaX());
    physicalObjectType.setOmegaY(aorPhysObj.getOmegaY());
    physicalObjectType.setOmegaZ(aorPhysObj.getOmegaZ());
    physicalObjectType.setRotationAngleX(aorPhysObj.getRotationAngleX());
    physicalObjectType.setRotationAngleY(aorPhysObj.getRotationAngleY());
    physicalObjectType.setRotationAngleZ(aorPhysObj.getRotationAngleZ());

    if (aorPhysObj.getMaterialType() != null) {
      physicalObjectType.setMaterialType(aorPhysObj.getMaterialType()
          .toString());
    }

    if (aorPhysObj.getShape2D() != null) {
      physicalObjectType.setShape2D(aorPhysObj.getShape2D().toString());
    }
    if (aorPhysObj.getShape3D() != null) {
      physicalObjectType.setShape3D(aorPhysObj.getShape3D().toString());
    }
    if (!aorPhysObj.getPoints().equals("")) {
      physicalObjectType.setPoints(aorPhysObj.getPoints());
    }
  }

  protected void setPhysicalAgentObjectValues(
      PhysicalAgentObject aorAgentObject, PhysAgtType physAgtType) {
    physAgtType.setPerceptionRadius(aorAgentObject.getPerceptionRadius());
  }

  /**
   * 
   * @param object
   *          - this object contains the attributes which have to logged as
   *          slots
   * @param entityType
   *          - it will be become the attributes as slots
   * @param clazz
   *          - final superclass for attributes-searches
   */
  protected void setObjectSlots(Object object,
      aors.logger.model.EntityType entityType, Class<?> clazz) {

    Class<?> c = object.getClass();

    do {

      Field[] fields = c.getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        SlotType slotType = objectFactory.createSlotType();
        slotType.setProperty(field.getName());
        try {
          // assume, if the method getId() exists, then it is an AOREntity
          // (or at least an other non-simpletype object); then set the @idRef
          Object o = field.get(object);
          if (o != null) {
            Method getIdMethod = o.getClass().getMethod("getId");
            slotType.setRefId(Long.valueOf(getIdMethod.invoke(o).toString()));
          }
          // slotType.setValue(String.valueOf(field.get(object)));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
          // if no getId(), then use set the @value
          try {
            slotType.setValue(String.valueOf(field.get(object)));
          } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
          } catch (IllegalAccessException e1) {
            e1.printStackTrace();
          }
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
        entityType.getSlot().add(slotType);
      }
      c = c.getSuperclass();

    } while (c != clazz);
  }

  @Override
  public void notifyAgentSimulatorsResultingStateChanges() {

    if (AbstractSimulator.runLogger) {

      if (!(dataCollector.isPropChangeListPhysAgentIsEmpty() && dataCollector
          .isPropChangeListAgentIsEmpty())) {

        AgentSimResultingStateChanges agentSimResultingStateChanges = this.objectFactory
            .createSimulationStepAgentSimResultingStateChanges();

        Agents agts = this.getStateChangedAgents();
        if (agts != null)
          agentSimResultingStateChanges.setAgents(agts);

        PhysicalAgents physAgts = this.getStateChangedPhysicalAgents();
        if (physAgts != null)
          agentSimResultingStateChanges.setPhysicalAgents(physAgts);

        this.simulationStep
            .setAgentSimResultingStateChanges(agentSimResultingStateChanges);
      }
    }
  }

}
