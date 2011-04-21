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
 * File: Simulation.java
 * 
 * Package: examples.movingcars.controller
 *
 **************************************************************************************************************/
package examples.movingcars.controller;

import java.util.ArrayList;

import aors.GeneralSpaceModel;
import aors.controller.AbstractSimulator;
import aors.data.DataBus;
import aors.data.DataBusInterface;
import aors.model.envsim.EnvironmentRule;
import aors.statistics.GeneralStatistics;
import examples.movingcars.model.agtsim.CarAgentSubject;
import examples.movingcars.model.envevt.EachCycle;
import examples.movingcars.model.envsim.CarAgentObject;
import examples.movingcars.model.envsim.EachCycleEnvRule;
import examples.movingcars.model.envsim.EndSpeedLimitEnvRule;
import examples.movingcars.model.envsim.SlowDownEnvRule;
import examples.movingcars.model.envsim.SpeedLimitCancelationSign;
import examples.movingcars.model.envsim.SpeedLimitSign;
import examples.movingcars.model.envsim.SpeedUpEnvRule;
import examples.movingcars.model.envsim.StartSpeedLimitEnvRule;

/**
 * Simulation
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision$
 */
public class Simulator extends AbstractSimulator {

  // define the number of agents (objects & subjects)
  // used for scalability test
  private final static int numberOfAgents = 3;

  public static SpaceModel spaceModel;

  public Simulator() {
    super(SimulationParameters.SIMULATION_STEPS);
  }

  /**
   * here set the scenarioInformation
   */
  @Override
  public void setInformations() {
    this.scenarioInfos.setScenarioName("CarsOnCircularTrack_3Cars_1Sign");
    this.scenarioInfos
        .setScenarioTitle("Three cars moving with max velocity 10, 12 and 15 m/s on a 3000 m track with a 5 m/s speed limit sign at 1500 m");
  }

  /**
   * Comments: the Class SimParams are created for every simulationdescription;
   * the logger needs the information about the params from specific simulation;
   * we create here an instance of SimParams for the logger
   * 
   * @return an instance of the specific SimParams
   * 
   */
  @Override
  public SimulationParameters getSimParams() {
    return new SimulationParameters();
  }

  @Override
  public void createSimModel() {
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

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: create and initialize here the environment simulator create
   * objective agents create objects create environment rules
   * 
   */
  public void createEnvironment() {

    // create objective agents
    CarAgentObject carAgentObject = null;
    for (int i = 1; i <= numberOfAgents; i++) {
      carAgentObject = new CarAgentObject(i);
      carAgentObject.setPerceptionRadius(50);
      carAgentObject.setVx(3 * i % 20);
      carAgentObject.setName("Car" + i);
      this.envSim.addPhysicalAgent(carAgentObject);
    }

    // create objects
    // following the same algorithm create and initialize objects
    SpeedLimitSign speedLimitSign = new SpeedLimitSign(101, 4);
    speedLimitSign.setX(1500);
    speedLimitSign.setName("SpeedLimitSign1");
    this.envSim.addPhysicalObjekt(speedLimitSign);

    SpeedLimitCancelationSign speedLimitCancelationSign = new SpeedLimitCancelationSign(
        102);
    speedLimitCancelationSign.setX(2000);
    speedLimitCancelationSign.setName("SpeedLimitCancelationSign1");
    this.envSim.addPhysicalObjekt(speedLimitCancelationSign);

    // create environment rules
    ArrayList<EnvironmentRule> rules = new ArrayList<EnvironmentRule>();

    EachCycleEnvRule eachCycleEnvRule = new EachCycleEnvRule(
        "EachCycleEnvRule", this.envSim);
    rules.add(eachCycleEnvRule);

    StartSpeedLimitEnvRule startSpeedLimitEnvRule = new StartSpeedLimitEnvRule(
        "StartSpeedLimitEnvRule", this.envSim);
    rules.add(startSpeedLimitEnvRule);

    EndSpeedLimitEnvRule endSpeedLimitEnvRule = new EndSpeedLimitEnvRule(
        "EndSpeedLimitEnvRule", this.envSim);
    rules.add(endSpeedLimitEnvRule);

    SlowDownEnvRule slowDownEnvRule = new SlowDownEnvRule("SlowDownEnvRule",
        this.envSim);
    rules.add(slowDownEnvRule);

    SpeedUpEnvRule speedUpEnvRule = new SpeedUpEnvRule("SpeedUpEnvRule",
        this.envSim);
    rules.add(speedUpEnvRule);

    // set the list of rules
    this.envSim.setRules(rules);
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: create and initialize all agents subjects
   * 
   * 
   * 
   */
  public void createAgentSubjects() {

    CarAgentSubject carAgentSubject = null;

    for (int i = 1; i <= numberOfAgents; i++) {

      carAgentSubject = new CarAgentSubject(i);
      carAgentSubject.setMyVelocity(3 * i % 20);
      carAgentSubject.setMaxVelocity(4 * i % 25);
      // add the created agents to the list of agents
      this.addAgentSubject(carAgentSubject);
    }
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   */
  public void createAgentSubjectFacets() {
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: create initial list of internal events
   * 
   * 
   * 
   */
  public void createInitialEvents() {
    // create and initialise initial environment events
    EachCycle eachCycle = new EachCycle(1);
    // add created events to the list of events
    this.environmentEvents.add(eachCycle);
  }

  public GeneralStatistics createStatistic() {
    return null;
  }

  public static void main(String args[]) {
    // create an concrete simulation instance
    AbstractSimulator movingCars = new Simulator();

    // set some properties (usually made by the simulator!)
    movingCars.setAutoMultithreading(false);

    // get the current time
    long timeStart = System.currentTimeMillis();
    // start the simulation
    DataBusInterface dataBus = new DataBus(DataBus.LoggerType.FULL_XML_LOGGER);
    movingCars.setDataBus(dataBus);
    movingCars.initialize();
    movingCars.runSimulation();
    // calculate and output the simulation time
    System.out.println("Zeit: " + (System.currentTimeMillis() - timeStart));
  }

  @Override
  public void executeInitializeRules() {
    // nothing todo
  }

  @Override
  public void setActivityFactory() {
    // TODO Auto-generated method stub
  }

  /**
   * Usage:
   *
   *
   * Comments: Overrides method {@code initGlobalVariables} from super class
   * 
   *
   * 
   */
  @Override
  protected void initGlobalVariables() {
    // TODO Auto-generated method stub
    
  }

}
