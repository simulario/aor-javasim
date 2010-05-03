package aors.physim.extsim;

//import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;

import aors.model.AtomicEvent;
import aors.model.envsim.Physical;
import aors.physim.baseclasses.ExternalSimulator;
import aors.physim.baseclasses.PhysicsData;
import aors.physim.util.Vector;

/**
 * DynamicsSimulator implements the calculation of impulses and new velocities
 * (for colliding physical objects) and the force of gravity acting on a
 * physical object.
 * 
 * @author Stefan Boecker
 * 
 */
public class DynamicsSimulator extends ExternalSimulator {

  /**
   * Hashtable for total force acting on a physical object.
   */
  private Hashtable<Long, Vector> phyobjs_forces_hash;

  /**
   * Gravity in meters per second^2
   */
  private final Vector gravity = new Vector(0, -9.81, 0);

  /**
   * The coefficient of restitution. for rest = 1 collision is elastic for rest
   * = 0 collision is inelastic
   */
  private final double rest = 0.7;

  private boolean autoGravitation = false;

  private boolean autoImpulse = false;

  public DynamicsSimulator(PhysicsData pdata, boolean autoGravitation,
      boolean autoImpulse) {
    super(pdata);
    phyobjs_forces_hash = new Hashtable<Long, Vector>();
    // String timeUnit = "";
    this.autoGravitation = autoGravitation;
    this.autoImpulse = autoImpulse;

    /*
     * // determine timeUnit Field[] fields = p.getClass().getDeclaredFields();
     * for (Field f : fields) { try { if
     * (f.getName().equals(aors.GeneralSimulationParameters.TIME_UNIT_NAME)) {
     * timeUnit = (f.get(p).toString()); } } catch (IllegalAccessException e) {
     * e.printStackTrace(); } }
     * 
     * UnitConverter con = new UnitConverter(timeUnit,
     * s.getSpatialDistanceUnit()); gravity.y =
     * con.convertDistancePerTime(gravity.y);
     */

  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.physim.baseclasses.ExternalSimulator#simulate(java.util.List,
   * long)
   */
  public void simulate(List<AtomicEvent> currentEvents, long currentStep) {

    // information not needed here
    pdata.getPhysicalCreated().clear();
    pdata.getPhysicalDestroyed().clear();

    List<Physical> phyobjs = getPhysicalObjectList();
    pdata.getNewVelocity().clear();
    pdata.getNewAcceleration().clear();
    if (autoGravitation) {
      for (int i = 0; i < phyobjs.size(); i++) {
        // zero force
        phyobjs_forces_hash.put(phyobjs.get(i).getId(), new Vector(0, 0, 0));

        determineForce(phyobjs.get(i));
        determineAcceleration(phyobjs.get(i));

      }
    }

    if (autoImpulse) {
      for (int i = 0; i < pdata.getCollisions().size(); i++) {
        // System.out.println("# " + currentStep + " " +
        // collisions.get(i).po1.getId() + " " + +
        // collisions.get(i).po2.getId());
        determineVelocity(pdata.getCollisions().get(i));
      }
    }

  }

  /**
   * Determine acceleration for physical object po.
   * 
   * @param po
   *          Physical object
   */
  private void determineAcceleration(Physical po) {
    Vector force = phyobjs_forces_hash.get(po.getId());
    if (po.getM() > 0) {
      Vector newacc = new Vector(force.x / po.getM(), force.y / po.getM(),
          force.z / po.getM());
      pdata.getNewAcceleration().put(po.getId(), newacc);
    }
  }

  /**
   * Determine force acting on physical object po.
   * 
   * @param po
   *          Physical object
   */
  private void determineForce(Physical po) {
    // force of gravity
    Vector force = new Vector(gravity.x, gravity.y, gravity.z);
    force.mul(po.getM());

    // add other forces here ...

    phyobjs_forces_hash.put(po.getId(), force);
  }

  /**
   * Determine change in velocity for an actual collision between two physical
   * objects.
   * 
   * @param collision
   *          The actual collision.
   */
  private void determineVelocity(CollisionSimulator.ActualCollision collision) {

    Physical po1 = collision.po1;
    Physical po2 = collision.po2;
    Vector cn = collision.contactNormal;

    // zero acceleration (from gravity) for colliding objects.
    // this is a simple method for handling resting contacts.
    // if an object "lies" on another on the ground, it slowly "sinks" into this
    // object.
    // this is because velocity of gravity is higher than separating velocity of
    // collision.
    if (autoGravitation) {
      pdata.getNewAcceleration().put(po1.getId(), new Vector(0, 0, 0));
      pdata.getNewAcceleration().put(po2.getId(), new Vector(0, 0, 0));
    }

    // calculate the separating velocity
    Vector relVel = new Vector(po1.getVx(), po1.getVy(), po1.getVz());
    Vector relVel2 = new Vector(po2.getVx(), po2.getVy(), po2.getVz());
    relVel.sub(relVel2);
    double sepVel = relVel.getScalar(cn);

    // if separating velocity is more than zero, the two objects are moving
    // apart and no
    // impulse calculation is necessary.
    if (sepVel > 0)
      return;

    // calculate the new separating velocity with use of the coefficient of
    // restitution.
    double newsepVel = -sepVel * rest;

    // determine the total change in velocity caused by this collision.
    double deltaVel = newsepVel - sepVel;

    // calculate the impulse
    if (po1.getM() == 0 && po2.getM() == 0)
      return;
    double impulse = (po1.getM() * po2.getM()) / (po1.getM() + po2.getM());
    impulse *= deltaVel;

    // calculate impulse vector
    Vector impulseVector = new Vector(cn.x, cn.y, cn.z);
    impulseVector.mul(impulse);

    // change velocity of both physical objects in actual collision
    Vector newVel;
    // Vector oldVel;
    if (po1.getM() > 0) {
      newVel = new Vector(po1.getVx(), po1.getVy(), po1.getVz());
      Vector temp = new Vector(impulseVector);
      temp.div(po1.getM());
      newVel.add(temp);
      // oldVel = newVelocity.get(po1.getId());
      // if(oldVel != null) {
      // newVel.add(oldVel);
      // }
      pdata.getNewVelocity().put(po1.getId(), newVel);
      // System.out.println("# newVel1: " + newVel);
    }

    if (po2.getM() > 0) {
      newVel = new Vector(po2.getVx(), po2.getVy(), po2.getVz());
      impulseVector.div(-po2.getM());
      newVel.add(impulseVector);
      // oldVel = newVelocity.get(po2.getId());
      // if(oldVel != null) {
      // newVel.add(oldVel);
      // }
      pdata.getNewVelocity().put(po2.getId(), newVel);
      // System.out.println("# newVel2: " + newVel);
    }

  }

}
