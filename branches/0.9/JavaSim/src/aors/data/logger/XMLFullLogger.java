package aors.data.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.ScenarioInfos;
import aors.controller.AbstractSimulator;
import aors.data.evt.sim.CollectionInitEvent;
import aors.logger.model.AgentSimulatorStep;
import aors.logger.model.CollectionType;
import aors.logger.model.Collections;
import aors.logger.model.EnvironmentSimulatorStep;
import aors.logger.model.GridCellType;
import aors.logger.model.GridCells;
import aors.logger.model.InMessageEventType;
import aors.logger.model.InitialState;
import aors.logger.model.MessageType;
import aors.logger.model.OutMessageEventType;
import aors.logger.model.PerceptionEventType;
import aors.logger.model.PhysicalObjectPerceptionEventType;
import aors.logger.model.SimulationScenario;
import aors.logger.model.SlotType;
import aors.model.envsim.Objekt;
import aors.space.AbstractCell;
import aors.util.JsonData;
import aors.util.collection.AORCollection;

public class XMLFullLogger extends SimObserver {

  /**
   * 
   */
  private static final long serialVersionUID = 8499640690122555624L;

  private JAXBContext context;
  private Marshaller marshaller;

  private BufferedWriter writer;
  private BufferedWriter cellInitWriter;

  public XMLFullLogger() {
    super();
    if (AbstractSimulator.runLogger) {
      try {
        context = JAXBContext.newInstance("aors.logger.model");
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      } catch (JAXBException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void initialize() {

    if (AbstractSimulator.runLogger) {

      try {
        this.writer = new BufferedWriter(new FileWriter(path
            + System.getProperty("file.separator") + fileName));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void notifyEnd() {

    if (AbstractSimulator.runLogger) {

      String out = "</log:SimulationLog>";
      this.printOutput(out);
      try {
        this.writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void notifyInitialisation() {

    if (AbstractSimulator.runLogger) {

      InitialState initialState = objectFactory.createInitialState();

      initialState.setObjects(this.notifyObjektInitialisation());
      initialState.setAgents(this.notifyAgentInitialisation());
      initialState.setPhysicalObjects(this.notifyPhysObjInitialisation());
      initialState.setPhysicalAgents(this.notifyPhysAgtInitialisation());
      initialState.setCollections(this.notifyCollectionInitialisation());

      this.printOutput(initialState);
      this.printCellInitFragment(this
          .notifyGridCellInitialisation(dataCollector.getGridCellInitState()));

      // delete the bufferqueus, because we have some
      // statechanges while the initialization
      dataCollector.deleteAllBuffer();
    }
  }

  private GridCells notifyGridCellInitialisation(AbstractCell[][] abstractCell) {

    GridCells gridCells = null;

    if (abstractCell != null) {

      gridCells = objectFactory.createGridCells();

      for (AbstractCell[] abstractCellX : abstractCell) {

        for (AbstractCell abstractCellY : abstractCellX) {

          GridCellType gridCellType = objectFactory.createGridCellType();
          gridCellType.setX(abstractCellY.getPosX());
          gridCellType.setY(abstractCellY.getPosY());
          gridCells.getGridCell().add(gridCellType);

          Field[] fields = abstractCellY.getClass().getDeclaredFields();
          for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())
                && !Modifier.isFinal(field.getModifiers())) {
              field.setAccessible(true);

              SlotType slotType = objectFactory.createSlotType();
              slotType.setProperty(field.getName());
              try {
                slotType.setValue(String.valueOf(field.get(abstractCellY)));
              } catch (IllegalArgumentException e) {
                e.printStackTrace();
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
              gridCellType.getSlot().add(slotType);
            }
          }
        }
      }
    }

    return gridCells;
  }

  @SuppressWarnings("unchecked")
  private Collections notifyCollectionInitialisation() {

    Collections collections = objectFactory.createCollections();

    // get the initstates from collections
    while (!dataCollector.isCollectionInitListIsEmpty()) {

      CollectionInitEvent collectionInitEvent = dataCollector
          .getNextCollectionInitEvent();
      AORCollection<? extends Objekt> aorCollection = (AORCollection<Objekt>) collectionInitEvent
          .getSource();

      CollectionType collectionType = objectFactory.createCollectionType();
      collectionType.setType(aorCollection.getCollectionType().toString());

      if (aorCollection.getId() != 0) {
        collectionType.setId(aorCollection.getId());
      }

      if (!aorCollection.getName().equals("")) {
        collectionType.setName(aorCollection.getName());
      }

      collectionType.setItemType(aorCollection.getDataType());

      collections.getColl().add(collectionType);
    }

    return collections;
  }

  @Override
  public void notifySimStepEnd() {
    if (AbstractSimulator.runLogger) {
      this.printOutput(this.simulationStep);
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
  public synchronized void notifyAgentSimulatorStep(
      AgentSimulatorStep agentSimulatorStep) {

    if (agentSimulatorStep == null)
      return;

    if (AbstractSimulator.runLogger) {
      this.simulationStep.getAgentSimulatorStep().add(agentSimulatorStep);
    }
  }

  @SuppressWarnings("unchecked")
  // TODO: test for leaks
  public synchronized void notifyAgentSimulatorStep(JsonData agentStepLog) {
    if (agentStepLog == null)
      return;

    if (AbstractSimulator.runLogger) {

      agentStepLog.process();

      // System.err.println(agentStepLog.getJson());
      AgentSimulatorStep agentSimulatorStep = objectFactory
          .createAgentSimulatorStep();

      agentSimulatorStep.setAgent((Long) agentStepLog.get("id"));
      // String name = (String) agentStepLog.get("name");
      // if (name != null && !name.equals(""))
      // agentSimulatorStep.setAgentName((String) agentStepLog.get("name"));
      agentSimulatorStep.setAgentType((String) agentStepLog.get("type"));

      ArrayList<Object> perceptions = (ArrayList<Object>) agentStepLog
          .get("perceptions");
      Iterator<Object> iterator = perceptions.iterator();
      // cycle over Perceptions
      while (iterator.hasNext()) {
        Map<String, Object> perception = (Map<String, Object>) iterator.next();

        String perceptionType = (String) perception.get("type");
        boolean processed = false;

        if (perceptionType.equals("InMessageEvent")) {
          processed = true;
          InMessageEventType imEvent = objectFactory.createInMessageEventType();
          imEvent.setId((Long) perception.get("id"));
          imEvent.setName((String) perception.get("name"));
          imEvent.setReceiverIdRef((Long) perception.get("perceiver"));
          imEvent.setSenderIdRef((Long) perception.get("sender"));
          MessageType message = objectFactory.createMessageType();
          message.setId((Long) perception.get("message.id"));
          message.setName((String) perception.get("message.name"));
          message.setType((String) perception.get("message.type"));

          Map<String, Object> slots = (Map<String, Object>) perception
              .get("slots");
          if (!slots.isEmpty()) {
            for (String key : slots.keySet()) {
              SlotType slot = objectFactory.createSlotType();
              slot.setProperty(key);
              slot.setValue(String.valueOf(slots.get(key)));
              message.getSlot().add(slot);
            }
          }

          imEvent.setMessage(message);

          ArrayList<Object> resEvents = (ArrayList<Object>) perception
              .get("resultingEvents");
          if (!resEvents.isEmpty()) {
            InMessageEventType.ResultingEvents resLEvents = new InMessageEventType.ResultingEvents();

            Iterator<Object> iterator2 = resEvents.iterator();
            while (iterator2.hasNext()) {
              Map<String, Object> resEvent = (Map<String, Object>) iterator2
                  .next();
              String actionEventType = (String) resEvent.get("type");
              if (actionEventType.equals("OutMessageEvent")) {
                OutMessageEventType omEvent = objectFactory
                    .createOutMessageEventType();
                omEvent.setId((Long) resEvent.get("id"));
                omEvent.setName((String) resEvent.get("name"));
                omEvent.setType((String) resEvent.get("type"));
                omEvent
                    .setOccurrenceTime((Long) resEvent.get("occurrenceTime"));
                omEvent.setMessageType((String) resEvent.get("message.type"));
                resLEvents.getActionEventOrOutMessageEvent().add(omEvent);
              }
            }
            imEvent.getResultingEvents().add(resLEvents);
          }
          agentSimulatorStep.getAgtSimInputEvent().add(
              objectFactory.createInMessageEvent(imEvent));
        }

        if (perceptionType.equals("PhysicalObjectPerceptionEvent")) {
          processed = true;
          PhysicalObjectPerceptionEventType pope = objectFactory
              .createPhysicalObjectPerceptionEventType();
          pope.setId((Long) perception.get("id"));
          pope.setName((String) perception.get("name"));
          // pope.setType((String) perception.get("type"));
          pope.setPerceiverIdRef((Long) perception.get("perceiver"));
          pope.setPerceivedId((Long) perception.get("perceived.id"));
          pope.setPerceivedType((String) perception.get("perceived.type"));
          pope.setDistance((Double) perception.get("distance"));
          pope.setPerceptionAngle((Double) perception.get("perceptionangle"));

          ArrayList<Object> resEvents = (ArrayList<Object>) perception
              .get("resultingEvents");
          if (!resEvents.isEmpty()) {
            PhysicalObjectPerceptionEventType.ResultingEvents resLEvents = objectFactory
                .createAgtSimInputEventTypeResultingEvents();
            Iterator<Object> iterator2 = resEvents.iterator();
            // cycle over ResultingEvents of current perception
            while (iterator2.hasNext()) {
              Map<String, Object> resEvent = (Map<String, Object>) iterator2
                  .next();

              aors.logger.model.AgtSimInputEventType.ResultingEvents.ActionEvent ae = objectFactory
                  .createAgtSimInputEventTypeResultingEventsActionEvent();

              long resultId = (Long) resEvent.get("id");
              if (resultId != 0)
                ae.setId(resultId);

              String resultName = (String) resEvent.get("name");
              if (resultName != null && !resultName.equals(""))
                ae.setName(resultName);

              ae.setType((String) resEvent.get("type"));
              ae.setOccurrenceTime((Long) resEvent.get("occurrenceTime"));
              resLEvents.getActionEventOrOutMessageEvent().add(ae);
            }
            pope.getResultingEvents().add(resLEvents);
          }
          agentSimulatorStep.getAgtSimInputEvent().add(
              objectFactory.createPhysicalObjectPerceptionEvent(pope));
        }

        if (processed == false) {
          PerceptionEventType perceptionEvent = objectFactory
              .createPerceptionEventType();

          long id = (Long) perception.get("id");
          if (id != 0)
            perceptionEvent.setId(id);

          String percName = (String) perception.get("name");
          if (percName != null && !percName.equals(""))
            perceptionEvent.setName(percName);

          perceptionEvent.setType((String) perception.get("type"));
          // perceptionEvent.setPerceiverIdRef((Long)
          // perception.get("perceiver"));

          ArrayList<Object> resEvents = (ArrayList<Object>) perception
              .get("resultingEvents");
          if (!resEvents.isEmpty()) {
            PerceptionEventType.ResultingEvents resLEvents = objectFactory
                .createAgtSimInputEventTypeResultingEvents();

            // cycle over ResultingEvents of current perception
            for (Object resEvt : resEvents) {
              Map<String, Object> resEvent = (Map<String, Object>) resEvt;

              aors.logger.model.AgtSimInputEventType.ResultingEvents.ActionEvent ae = objectFactory
                  .createAgtSimInputEventTypeResultingEventsActionEvent();

              long resultId = (Long) resEvent.get("id");
              if (resultId != 0)
                ae.setId(resultId);

              String resultName = (String) resEvent.get("name");
              if (resultName != null && !resultName.equals(""))
                ae.setName(resultName);

              ae.setType((String) resEvent.get("type"));
              ae.setOccurrenceTime((Long) resEvent.get("occurrenceTime"));
              resLEvents.getActionEventOrOutMessageEvent().add(ae);
            }

            perceptionEvent.getResultingEvents().add(resLEvents);
          }
          agentSimulatorStep.getAgtSimInputEvent().add(
              objectFactory.createPerceptionEvent(perceptionEvent));
        }
      }
      this.simulationStep.getAgentSimulatorStep().add(agentSimulatorStep);
    }
  }

  @Override
  public void notifySimulationScenario(ScenarioInfos scenarioInfos,
      GeneralSimulationParameters aorSimParameters,
      Map<String, String> modelParamMap) {

    super.notifySimulationScenario(scenarioInfos, aorSimParameters,
        modelParamMap);

    if (AbstractSimulator.runLogger) {

      SimulationScenario simulationScenario = objectFactory
          .createSimulationScenario();
      // simulationScenario.setModelName(modelInfos.getModelName());
      // simulationScenario.setModelTitle(modelInfos.getModelTitle());
      simulationScenario.setScenarioName(scenarioInfos.getScenarioName());
      simulationScenario.setScenarioTitle(scenarioInfos.getScenarioTitle());
      simulationScenario.setVersion(scenarioInfos.getVersion());

      aors.logger.model.SimulationParameters simulationParameters = objectFactory
          .createSimulationParameters();

      // set the parameter
      Field[] fields = aorSimParameters.getClass().getDeclaredFields();
      for (Field f : fields) {
        if (Modifier.isStatic(f.getModifiers())) {
          try {
            if (f.getName().equals(
                aors.GeneralSimulationParameters.SIMULATION_STEPS_NAME)) {
              simulationParameters.setSimulationSteps(Long.valueOf(f.get(
                  aorSimParameters).toString()));
            } else if (f.getName().equals(
                aors.GeneralSimulationParameters.STEP_DURATION_NAME)) {
              simulationParameters.setStepDuration(Double.valueOf(f.get(
                  aorSimParameters).toString()));
            } else if (f.getName().equals(
                aors.GeneralSimulationParameters.STEP_TIME_DELAY_NAME)) {
              simulationParameters.setStepTimeDelay(Double.valueOf(f.get(
                  aorSimParameters).toString()));
            } else if (f.getName().equals(
                aors.GeneralSimulationParameters.TIME_UNIT_NAME)) {
              simulationParameters.setTimeUnit(f.get(aorSimParameters)
                  .toString());
            } else if (f.getName().equals(
                aors.GeneralSimulationParameters.RANDOM_SEED)) {
              simulationParameters.setRandomSeed(f.getLong(aorSimParameters));
            } else {
              // log the additional parameters
              SlotType slotType = objectFactory.createSlotType();
              slotType.setProperty(f.getName());
              slotType.setValue(f.get(aorSimParameters).toString());
              simulationParameters.getSlot().add(slotType);
            }
          } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
      simulationScenario.setSimulationParameters(simulationParameters);

      aors.logger.model.SimulationModel simulationModel = objectFactory
          .createSimulationModel();
      // set the modelinfos
      for (String key : modelParamMap.keySet()) {
        try {
          if (key.equals(aors.GeneralSimulationModel.ModelParameter.MODEL_NAME
              .toString())) {
            simulationModel.setModelName(modelParamMap.get(key));
          } else if (key
              .equals(aors.GeneralSimulationModel.ModelParameter.MODEL_TITLE
                  .toString())) {
            String modelTitle = modelParamMap.get(key);
            if (!modelTitle.equals(""))
              simulationModel.setModelTitle(modelTitle);
          }
          // else if (key
          // .equals(aors.GeneralSimulationModel.ModelParameter.AUTO_KINEMATICS
          // .toString())) {
          // simulationModel.setAutoKinematics(Boolean.valueOf(modelParamMap
          // .get(key)));
          // } else if (key
          // .equals(aors.GeneralSimulationModel.ModelParameter.AUTO_GRAVITATION
          // .toString())) {
          // simulationModel.setAutoGravitation(Boolean.valueOf(modelParamMap
          // .get(key)));
          // } else if (key
          // .equals(aors.GeneralSimulationModel.ModelParameter.AUTO_IMPULSE
          // .toString())) {
          // simulationModel.setAutoImpulse(Boolean.valueOf(modelParamMap
          // .get(key)));
          // } else if (key
          // .equals(aors.GeneralSimulationModel.ModelParameter.AUTO_COLLISION
          // .toString())) {
          // simulationModel.setAutoCollision(Boolean.valueOf(modelParamMap
          // .get(key)));
          // } else if (key
          // .equals(aors.GeneralSimulationModel.ModelParameter.BASE_URI
          // .toString())) {
          // String baseURI = modelParamMap.get(key);
          // if (!baseURI.equals(""))
          // simulationModel.setBaseURI(baseURI);
          // }
        } catch (IllegalArgumentException iae) {
          // TODO: handle exception
        }

      }
      simulationScenario.setSimulationModel(simulationModel);

      this.printOutput(simulationScenario);
    }
  }

  @Override
  public void notifySimulationStart(long startTime, long steps) {

  }

  @Override
  public void notifySpaceModel(GeneralSpaceModel aorSpaceModel) {

    if (AbstractSimulator.runLogger) {

      aors.logger.model.SpaceModel spaceModel = objectFactory
          .createSpaceModel();
      spaceModel.setDimensions(aors.GeneralSpaceModel.Dimensions.dimensionsMap
          .get(aorSpaceModel.getDimensions().toString()).toString());
      spaceModel.setGeometry(aorSpaceModel.getGeometry().toString());
      spaceModel.setSpatialDistanceUnit(aorSpaceModel.getSpatialDistanceUnit()
          .toString());
      spaceModel.setXMax((double) aorSpaceModel.getXMax());
      spaceModel.setYMax((double) aorSpaceModel.getYMax());
      spaceModel.setZMax((double) aorSpaceModel.getZMax());
      spaceModel.setDiscrete(aorSpaceModel.isDiscrete());
      spaceModel.setAutoKinematics(aorSpaceModel.getSpace().isAutoKinematics());
      spaceModel.setAutoCollisionDetection(aorSpaceModel.getSpace()
          .isAutoCollisionDetection());
      spaceModel.setAutoCollisionHandling(aorSpaceModel.getSpace()
          .isAutoCollisionHandling());
      spaceModel.setGravitation(aorSpaceModel.getSpace().getGravitation());

      this.printOutput(spaceModel);
    }
  }

  @Override
  public void notifyStart() {

    if (AbstractSimulator.runLogger) {

      String out = "<log:SimulationLog xmlns:log=\"http://aor-simulation.org/log\">";
      this.printOutput(out);
    }
  }

  /**
   * print a jaxbFragment to the output
   * 
   * @param jaxbFragment
   *          - JAXBFragment
   */
  private void printOutput(Object jaxbFragment) {

    StringWriter fragmentLogString = new StringWriter();

    try {
      synchronized (this) {
        marshaller.marshal(jaxbFragment, fragmentLogString);
      }
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    // System.out.println(fragmentLogString);
    this.printOutput(fragmentLogString.toString());

  }

  private void printCellInitFragment(Object cellInitJaxbFragment) {

    if (cellInitJaxbFragment != null) {

      try {
        this.cellInitWriter = new BufferedWriter(new FileWriter(this.path
            + System.getProperty("file.separator") + this.cellInitFileName));
      } catch (IOException e) {
        e.printStackTrace();
      }

      StringWriter fragmentLogString = new StringWriter();

      try {
        marshaller.marshal(cellInitJaxbFragment, fragmentLogString);
      } catch (JAXBException e) {
        e.printStackTrace();
      }

      if (this.cellInitWriter != null) {

        try {
          this.cellInitWriter.append(fragmentLogString.toString());
          this.cellInitWriter.flush();
        } catch (IOException e) {
          System.err.println("IOException in Logwriter");
          e.printStackTrace();
        }

        try {
          this.cellInitWriter.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * Write a string to the outputfile.
   * 
   * @param output
   *          - OutputString
   */
  private void printOutput(String output) {
    if (writer != null) {
      try {
        writer.append(output);
        writer.newLine();
        writer.flush();
      } catch (IOException e) {
        System.err.println("IOException in Logwriter");
        e.printStackTrace();
      }
    }
  }

}
