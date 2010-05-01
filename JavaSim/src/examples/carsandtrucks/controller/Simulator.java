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
 * File: Simulation.java
 * 
 * Package: examples.carsandtrucks.controller
 *
 **************************************************************************************************************/
package examples.carsandtrucks.controller;

import java.util.ArrayList;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.controller.AbstractSimulator;
import aors.data.DataBus;
import aors.data.DataBusInterface;
import aors.model.envsim.EnvironmentRule;
import aors.statistics.GeneralStatistics;
import examples.carsandtrucks.model.agtsim.CarAgentSubject;
import examples.carsandtrucks.model.agtsim.TruckAgentSubject;
import examples.carsandtrucks.model.envevt.EachCycle;
import examples.carsandtrucks.model.envsim.CarAgentObject;
import examples.carsandtrucks.model.envsim.CarEachCycleEnvRule;
import examples.carsandtrucks.model.envsim.CarSlowDownEnvRule;
import examples.carsandtrucks.model.envsim.CarSpeedUpEnvRule;
import examples.carsandtrucks.model.envsim.EndSpeedLimitEnvRule;
import examples.carsandtrucks.model.envsim.EndTruckSpeedLimitEnvRule;
import examples.carsandtrucks.model.envsim.SpeedLimitCancelationSign;
import examples.carsandtrucks.model.envsim.SpeedLimitSign;
import examples.carsandtrucks.model.envsim.StartSpeedLimitEnvRule;
import examples.carsandtrucks.model.envsim.StartTruckSpeedLimitEnvRule;
import examples.carsandtrucks.model.envsim.TruckAgentObject;
import examples.carsandtrucks.model.envsim.TruckEachCycleEnvRule;
import examples.carsandtrucks.model.envsim.TruckSlowDownEnvRule;
import examples.carsandtrucks.model.envsim.TruckSpeedLimitSign;
import examples.carsandtrucks.model.envsim.TruckSpeedUpEnvRule;
import examples.movingcars.controller.SimModel;
import examples.movingcars.controller.SpaceModel;

/**
 * Simulation
 * 
 * @author
 * @since July 13, 2008
 * @version $Revision$
 */
public class Simulator extends AbstractSimulator {

  /**
   * set the simSpacemodel here Comments: this field is static so that it can be
   * accessible all over the code. similar to global variable; this field is set
   * through the method {@code createSpaceModel}
   */
  public static SpaceModel spaceModel;

  public Simulator() {
    super(SimulationParameters.SIMULATION_STEPS);
  }

  public void setInformations() {
    this.scenarioInfos.setScenarioName("CarsAndTrucks_2Cars_1Truck");
    this.scenarioInfos
        .setScenarioTitle("One Truck and two Cars moving on a circular track with one speed limit sign for all and ond speed limit sign only for trucks");
  }

  /**
   * here are set the simulation parameters.
   */
  @Override
  public GeneralSimulationParameters getSimParams() {
    return new SimulationParameters();
  }

  @Override
  protected void createSimModel() {
    this.simModel = new SimModel();
  }

  /**
   * space model is set
   */
  public GeneralSpaceModel createSpaceModel() {
    // Dimensions is a required parameter
    // notice that as value is used the inner defined Dimensions enumeration
    // type
    // it is a workaround to use integer values for dimensions setting
    // SpaceModel spaceModel = new SpaceModel(SpaceModel.Dimensions.one);
    SpaceModel spaceModel = new SpaceModel(GeneralSpaceModel.Dimensions.one);
    spaceModel.setDiscrete(false);
    // here is set the geometry using the inner defined Geometry enumeration
    // type
    spaceModel.setGeometry(GeneralSpaceModel.Geometry.Toroidal);
    // in the same way spatial distance unit is set
    // using the inner defined SpatialDistanceUnit enumeration type
    spaceModel.setSpatialDistanceUnit(GeneralSpaceModel.SpatialDistanceUnit.m);
    spaceModel.setXMax(30000);
    Simulator.spaceModel = spaceModel;
    dataBus.notifySpaceModel(spaceModel);

    return spaceModel;
  }

  public void createEnvironment() {

    CarAgentObject car1 = new CarAgentObject(1);
    car1.setName("Car1");
    car1.setVx(4);
    car1.setX(200);
    car1.setPerceptionRadius(50);
    this.envSim.addPhysicalAgent(car1);
    CarAgentObject car2 = new CarAgentObject(2);
    car2.setName("Car2");
    car2.setVx(6);
    car2.setX(300);
    car2.setPerceptionRadius(50);
    this.envSim.addPhysicalAgent(car2);
    TruckAgentObject truck1 = new TruckAgentObject(3);
    truck1.setName("Truck1");
    truck1.setVx(5);
    truck1.setX(100);
    truck1.setPerceptionRadius(50);
    this.envSim.addPhysicalAgent(truck1);

    SpeedLimitSign speedLimitSign = new SpeedLimitSign(101);
    speedLimitSign.setX(1500);
    speedLimitSign.setAdmMaxVel(4);
    speedLimitSign.setName("SpeedLimitSign1");
    this.envSim.addPhysicalObjekt(speedLimitSign);

    TruckSpeedLimitSign truckSpeedLimitSign = new TruckSpeedLimitSign(103);
    truckSpeedLimitSign.setX(2000);
    truckSpeedLimitSign.setAdmMaxVel(3);
    truckSpeedLimitSign.setName("SpeedLimitSign2");
    this.envSim.addPhysicalObjekt(truckSpeedLimitSign);

    SpeedLimitCancelationSign speedLimitCancelationSign = new SpeedLimitCancelationSign(
        102);
    speedLimitCancelationSign.setX(2500);
    speedLimitCancelationSign.setName("SpeedLimitCancelationSign1");
    this.envSim.addPhysicalObjekt(speedLimitCancelationSign);

    // create environment rules
    ArrayList<EnvironmentRule> rules = new ArrayList<EnvironmentRule>();

    CarEachCycleEnvRule carEachCycleEnvRule = new CarEachCycleEnvRule(
        "CarEachCycleEnvRule", this.envSim);
    rules.add(carEachCycleEnvRule);

    TruckEachCycleEnvRule truckEachCycleEnvRule = new TruckEachCycleEnvRule(
        "TruckEachCycleEnvRule", this.envSim);
    rules.add(truckEachCycleEnvRule);

    StartSpeedLimitEnvRule startSpeedLimitEnvRule = new StartSpeedLimitEnvRule(
        "StartSpeedLimitEnvRule", this.envSim);
    rules.add(startSpeedLimitEnvRule);

    EndSpeedLimitEnvRule endSpeedLimitEnvRule = new EndSpeedLimitEnvRule(
        "EndSpeedLimitEnvRule", this.envSim);
    rules.add(endSpeedLimitEnvRule);

    StartTruckSpeedLimitEnvRule truckStartSpeedLimitEnvRule = new StartTruckSpeedLimitEnvRule(
        "TruckStartSpeedLimitEnvRule", this.envSim);
    rules.add(truckStartSpeedLimitEnvRule);

    EndTruckSpeedLimitEnvRule truckEndSpeedLimitEnvRule = new EndTruckSpeedLimitEnvRule(
        "TruckEndSpeedLimitEnvRule", this.envSim);
    rules.add(truckEndSpeedLimitEnvRule);

    CarSlowDownEnvRule carSlowDownEnvRule = new CarSlowDownEnvRule(
        "CarSlowDownEnvRule", this.envSim);
    rules.add(carSlowDownEnvRule);

    CarSpeedUpEnvRule carSpeedUpEnvRule = new CarSpeedUpEnvRule(
        "CarSpeedUpEnvRule", this.envSim);
    rules.add(carSpeedUpEnvRule);

    TruckSlowDownEnvRule truckSlowDownEnvRule = new TruckSlowDownEnvRule(
        "TruckSlowDownEnvRule", this.envSim);
    rules.add(truckSlowDownEnvRule);

    TruckSpeedUpEnvRule truckSpeedUpEnvRule = new TruckSpeedUpEnvRule(
        "TruckSpeedUpEnvRule", this.envSim);
    rules.add(truckSpeedUpEnvRule);

    // set the list of rules
    this.envSim.setRules(rules);

  }

  public void createAgentSubjects() {
    CarAgentSubject car1 = new CarAgentSubject(1);
    car1.setVx(4);
    car1.setMaxVelocity(12);
    this.addAgentSubject(car1);
    CarAgentSubject car2 = new CarAgentSubject(2);
    car2.setVx(6);
    car2.setMaxVelocity(15);
    this.addAgentSubject(car2);
    TruckAgentSubject truck1 = new TruckAgentSubject(3);
    truck1.setVx(5);
    truck1.setMaxVelocity(10);
    this.addAgentSubject(truck1);
  }

  public void createAgentSubjectFacets() {
  }

  public void createInitialEvents() {

    EachCycle eachCycle = new EachCycle(1);

    this.environmentEvents.add(eachCycle);
  }

  public GeneralStatistics createStatistic() {
    return null;
  }

  public static void main(String args[]) {
    Simulator carsAndTrucks = new Simulator();
    long timeStart = System.currentTimeMillis();
    DataBusInterface dataBus = new DataBus(DataBus.LoggerType.FULL_XML_LOGGER);
    carsAndTrucks.setDataBus(dataBus);
    carsAndTrucks.initialize();
    carsAndTrucks.runSimulation();
    System.out.println("Time: " + (System.currentTimeMillis() - timeStart));
  }

  @Override
  public void executeInitializeRules() {
    // nothing to do
  }

  @Override
  public void setActivityFactory() {
    // TODO Auto-generated method stub
  }
}
