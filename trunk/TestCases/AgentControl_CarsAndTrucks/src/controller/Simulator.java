package controller;

import aors.util.Random;
import controller.SimParameter;

public class Simulator extends aors.controller.AbstractSimulator {
  public static SpaceModel spaceModel;

  public Simulator() {
    super(controller.SimParameter.SIMULATION_STEPS);
  }

  protected void setInformations() {
    this.scenarioInfos.setVersion("0.8.4");
    this.scenarioInfos.setScenarioName("CarsAndTrucksOnCircularTrack_2Cars_1Truck");
    this.scenarioInfos.setScenarioTitle("A truck and two cars moving on a circular track with one speed   limit sign for all and one speed limit sign only for trucks");
  }

  protected controller.SimParameter getSimParams() {
    this.setStepTimeDelay(30L);
    return new controller.SimParameter();
  }

  protected void createSimModel() {
    this.simModel = new controller.SimModel();
  }

  protected aors.GeneralSpaceModel createSpaceModel() {
    if((this.physim != null)) {
      this.physim.setPhysicsAttributes(true, false, false, false, true);
    }
    controller.SpaceModel __generalSpaceModel = new controller.SpaceModel(aors.GeneralSpaceModel.Dimensions.one);
    __generalSpaceModel.setDiscrete(false);
    __generalSpaceModel.setGeometry(aors.GeneralSpaceModel.Geometry.Toroidal);
    __generalSpaceModel.setSpatialDistanceUnit(aors.GeneralSpaceModel.SpatialDistanceUnit.m);
    __generalSpaceModel.setXMax(20000L);
    __generalSpaceModel.initSpace();
    controller.Simulator.spaceModel = __generalSpaceModel;
    this.dataBus.notifySpaceModel(__generalSpaceModel);
    if((this.physim != null)) {
      this.physim.setSpaceModel(__generalSpaceModel);
    }
    return __generalSpaceModel;
  }

  protected void createEnvironment() {
    model.envsim.SpeedLimitSign __speedLimitSign;
    model.envsim.TruckSpeedLimitSign __truckSpeedLimitSign;
    model.envsim.SpeedLimitCancelationSign __speedLimitCancelationSign;
    model.envsim.Car __car;
    model.envsim.Truck __truck;
    __car = new model.envsim.Car(1L, "Car1");
    __car.setX(1000.0);
    __car.setVx(0.0);
    __car.setPerceptionRadius(50.0);
    __car.setWidth(10.0);
    __car.setWidth(180.0);
    this.envSim.addPhysicalAgent(__car);
    __car = new model.envsim.Car(2L, "Car2");
    __car.setX(500.0);
    __car.setVx(0.0);
    __car.setPerceptionRadius(50.0);
    __car.setWidth(10.0);
    __car.setWidth(180.0);
    this.envSim.addPhysicalAgent(__car);
    __truck = new model.envsim.Truck(3L, "Truck1");
    __truck.setX(0.0);
    __truck.setVx(0.0);
    __truck.setPerceptionRadius(50.0);
    __truck.setWidth(20.0);
    __truck.setWidth(230.0);
    __truck.setHeight(230.0);
    this.envSim.addPhysicalAgent(__truck);
    __speedLimitSign = new model.envsim.SpeedLimitSign(101L, "SpeedLimitSign1", 70L);
    __speedLimitSign.setX(6000.0);
    __speedLimitSign.setWidth(150.0);
    this.envSim.addPhysicalObjekt(__speedLimitSign);
    __truckSpeedLimitSign = new model.envsim.TruckSpeedLimitSign(102L, "TruckSpeedLimitSign1", 40L);
    __truckSpeedLimitSign.setX(8000.0);
    __truckSpeedLimitSign.setWidth(150.0);
    this.envSim.addPhysicalObjekt(__truckSpeedLimitSign);
    __speedLimitCancelationSign = new model.envsim.SpeedLimitCancelationSign(103L, "SpeedLimitCancelationSign1");
    __speedLimitCancelationSign.setX(12000.0);
    __speedLimitCancelationSign.setWidth(150.0);
    this.envSim.addPhysicalObjekt(__speedLimitCancelationSign);
    java.util.List<aors.model.envsim.EnvironmentRule> __environmentRules = new java.util.ArrayList<aors.model.envsim.EnvironmentRule>();
    model.envsim.SlowDownEnvRule __slowDownEnvRule = new model.envsim.SlowDownEnvRule("SlowDownEnvRule", this.envSim);
    __environmentRules.add(__slowDownEnvRule);
    model.envsim.SpeedUpEnvRule __speedUpEnvRule = new model.envsim.SpeedUpEnvRule("SpeedUpEnvRule", this.envSim);
    __environmentRules.add(__speedUpEnvRule);
    this.envSim.setRules(__environmentRules);
  }

  protected void createAgentSubjects() {
    model.agtsim.CarAgentSubject __carAgentSubject;
    __carAgentSubject = new model.agtsim.CarAgentSubject(1L, "Car1", "", "", 150L, 0L, 0L);
    __carAgentSubject.addInternalEvent(new aors.model.intevt.EachSimulationStep(1L));
    this.addAgentSubject(__carAgentSubject);
    __carAgentSubject = new model.agtsim.CarAgentSubject(2L, "Car2", "", "", 120L, 0L, 0L);
    __carAgentSubject.addInternalEvent(new aors.model.intevt.EachSimulationStep(1L));
    this.addAgentSubject(__carAgentSubject);
    model.agtsim.TruckAgentSubject __truckAgentSubject;
    __truckAgentSubject = new model.agtsim.TruckAgentSubject(3L, "Truck1", "", 100L, 0L, 0L);
    __truckAgentSubject.addInternalEvent(new aors.model.intevt.EachSimulationStep(1L));
    this.addAgentSubject(__truckAgentSubject);
  }

  protected void createInitialEvents() {
  }

  protected aors.statistics.GeneralStatistics createStatistic() {
    return null;
  }

  public static void main(String[] args) {
    controller.Simulator carsAndTrucksOnCircularTrack = new controller.Simulator();
    carsAndTrucksOnCircularTrack.setDataBus(new aors.data.DataBus(aors.data.DataBus.LoggerType.FULL_XML_LOGGER));
    carsAndTrucksOnCircularTrack.initialize();
    carsAndTrucksOnCircularTrack.runSimulation();
  }

  protected void executeInitializeRules() {
  }

  protected void setActivityFactory() {
  }
}
