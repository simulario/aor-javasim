package aors.physim.extsim;

import java.util.List;

import aors.model.AtomicEvent;
import aors.model.envsim.Physical;
import aors.physim.baseclasses.ExternalSimulator;
import aors.physim.baseclasses.PhysicsData;

/**
 * KinematicsSimulator is an abstract simulator for simulating kinematics in
 * PhySim. Non-abstract classes that inherit from this class, have to implement
 * determinePosition(), determineVelocity() and determineAcceleration().
 * 
 * A external simulator for laws of motion (translation) is already implemented
 * in TranslationSimulator.
 * 
 * @author Stefan Boecker
 * 
 */
public abstract class KinematicsSimulator extends ExternalSimulator {

  protected List<Physical> phyobjs;

  public KinematicsSimulator(PhysicsData pdata) {
    super(pdata);
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.physim.baseclasses.ExternalSimulator#simulate(java.util.List,
   * long)
   */
  public void simulate(List<AtomicEvent> currentEvents, long currentStep) {

    // information not needed here
    this.pdata.getPhysicalCreated().clear();

    this.phyobjs = getPhysicalObjectList();
    determineAcceleration();
    determineVelocity(currentStep);
    determinePosition(currentStep);
  }

  /**
   * Calculate position for all physical objects in simulation.
   */
  protected abstract void determinePosition(long step);

  /**
   * Calculate velocity for all physical objects in simulation.
   */
  protected abstract void determineVelocity(long step);

  /**
   * Calculate acceleration for all physical objects in simulation.
   */
  protected abstract void determineAcceleration();

}
