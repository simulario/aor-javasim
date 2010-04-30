package aors.physim.extsim;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import aors.GeneralSpaceModel;
import aors.model.AtomicEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.physim.PhysicsSimulator;
import aors.physim.baseclasses.ExternalSimulator;
import aors.physim.baseclasses.PhysicsData;
import aors.physim.util.BVHTree;
import aors.physim.util.BoundingSphere;
import aors.physim.util.Vector;
import aors.physim.util.BVHTree.PotentialCollision;

/**
 * PerceptionSimulator implements the detection of agents perceiving other
 * physical objects. It makes use of the BVHTree, that is also used by
 * CollisionSimulator.
 * 
 * @author Stefan Boecker
 * 
 */
public class PerceptionSimulator extends ExternalSimulator {

  /**
   * BVHTree
   */
  private BVHTree tree;

  /**
   * List of potential perceptions of an agent and another physical object.
   * Because the problem of detecting perceptions is similar to detecting
   * collisions, this list has the type PotentialCollision.
   */
  private List<BVHTree.PotentialCollision> potper;

  private List<BVHTree.PotentialCollision> actualPerception;

  /**
   * Hashtable of all agent's subjects and their attribute idPerceivable.
   */
  private Map<Long, Boolean> idPerceivable = new Hashtable<Long, Boolean>();

  /**
   * Hashtable of all agent's subjects and their attribute autoPerception.
   */
  private Map<Long, Boolean> autoPerception = new Hashtable<Long, Boolean>();

  public PerceptionSimulator(PhysicsData pdata) {
    super(pdata);

    List<Physical> phyobjs = getPhysicalObjectList();

    tree = new BVHTree(phyobjs);
    potper = new ArrayList<BVHTree.PotentialCollision>();
    actualPerception = new ArrayList<BVHTree.PotentialCollision>();

    // determine attribute autoPerception for agentSubjects
    for (PhysicalAgentObject agt : pdata.getPhysAgents()) {

      try {
        Field field = agt.getClass().getDeclaredField(
            PhysicsSimulator.AUTO_PERCEPTION);
        autoPerception.put(agt.getId(), Boolean.valueOf(field.get(null)
            .toString()));
        // System.out.println(agt.getId() + " " +
        // Boolean.valueOf(field.get(null).toString()));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        // System.out.println(agt.getId() + "nosuchfield");
        continue;
      }
    }

    // determine attribute idPerceivable for physical objects
    for (Physical po : phyobjs) {

      try {
        Field field = po.getClass().getDeclaredField(
            PhysicsSimulator.ID_PERCEIVABLE);
        idPerceivable.put(po.getId(), Boolean.valueOf(field.get(null)
            .toString()));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        continue;
      }
    }
  }

  /**
   * Calculate if Agent agt perceives physical object po2
   * 
   * @param agt
   *          Physical agent
   * @param po2
   *          Physical object
   * @return true, if agent perceives object, false otherwise.
   */
  private boolean calcPerception(PhysicalAgentObject agt, Physical po2) {
    if (Math.abs(agt.getX() - po2.getX()) > (agt.getPerceptionRadius() + po2
        .getWidth() / 2))
      return false;
    if (Math.abs(agt.getY() - po2.getY()) > (agt.getPerceptionRadius() + po2
        .getHeight() / 2))
      return false;
    if (Math.abs(agt.getZ() - po2.getZ()) > (agt.getPerceptionRadius() + po2
        .getDepth() / 2))
      return false;

    return true;
  }

  /**
   * This method determines all perceptions in the current simulation step. It
   * make use of the BVHTree.
   * 
   * @param currentEvents
   *          List of current EnvironmentEvents
   * @param currentStep
   *          Current simulation step
   */
  public void simulate(List<AtomicEvent> currentEvents, long currentStep) {
    List<Physical> phyobjs = this.getPhysicalObjectList();

    // delete destroyed physical objects
    for (long id : pdata.getPhysicalDestroyed()) {
      tree.deletePhysicalObjectById(id);
      this.autoPerception.remove(id);
      this.idPerceivable.remove(id);
    }

    // add new created physicals
    // TODO: discuss the reflections
    // maybe we should use classvariables for physicals for perceivable and
    // autoperception
    while (!pdata.getPhysicalCreated().isEmpty()) {

      Physical physical = pdata.getPhysicalCreated().poll();
      tree.addNode(physical);

      try {
        Field field = physical.getClass().getDeclaredField(
            PhysicsSimulator.ID_PERCEIVABLE);
        idPerceivable.put(physical.getId(), Boolean.valueOf(field.get(null)
            .toString()));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        continue;
      }

      if (PhysicalAgentObject.class.isInstance(physical)) {
        try {
          Field field_autoPerc = physical.getClass().getDeclaredField(
              PhysicsSimulator.AUTO_PERCEPTION);
          autoPerception.put(physical.getId(), Boolean.valueOf(field_autoPerc
              .get(null).toString()));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (NoSuchFieldException e) {
          e.printStackTrace();
        }
      }
    }

    // update BVH-Tree
    for (Physical physical : phyobjs) {
      if (pdata.getPositionChanged().get(physical.getId()) != null
          && pdata.getPositionChanged().get(physical.getId())) {
        tree.updatePhysicalObject(physical);
      }
    }

    // test for collision
    potper.clear();
    for (PhysicalAgentObject agt : pdata.getPhysAgents()) {

      // only check, if autoPerception is enabled for agent agt
      if (autoPerception.get(agt.getId()) == false)
        continue;

      // look for agents and objects in perception radius
      // use perceptionradius of agent as bounding sphere
      BoundingSphere bs = new BoundingSphere(agt, agt.getPerceptionRadius());
      tree.testCollision(bs, potper);

    }

    actualPerception.clear();
    // calculate if potential perception is actual perception
    for (PotentialCollision potentialCollision : this.potper) {

      Physical a = potentialCollision.object1;
      Physical o = potentialCollision.object2;

      if (calcPerception((PhysicalAgentObject) (a), o)) {
        // get angle between velocity-vector and distance-vector
        Vector v = new Vector(a.getVx(), a.getVy(), a.getVz());
        Vector dv = getDistanceVector(a, o);
        double angle = dv.getAngle(v);
        if (Double.isNaN(angle))
          angle = 0;
        double distance = dv.getLength();

        // generate and add PhysicalObjectPerceptionEvent
        addPerception(currentEvents, a, o, distance, angle, currentStep);

        actualPerception.add(potentialCollision);
      }
    }

    // in case of toroidal geometry, check if there are perceptions at the
    // border of space
    if (pdata.getSpaceModel().getGeometry() == GeneralSpaceModel.Geometry.Toroidal) {

      for (PhysicalAgentObject agt : pdata.getPhysAgents()) {

        if (agt.getPerceptionRadius() <= 0
            || autoPerception.get(agt.getId()) == false)
          continue;

        // test if agent is at border of space and perceptionradius reaches
        // beyond border
        if ((agt.getX() - agt.getPerceptionRadius() < 0)
            || (agt.getY() - agt.getPerceptionRadius() < 0 && pdata
                .getSpaceModel().getYMax() > 0)
            || (agt.getZ() - agt.getPerceptionRadius() < 0 && pdata
                .getSpaceModel().getZMax() > 0)) {

          for (Physical po : phyobjs) {

            // if physical object is agent or the perception was already
            // handled, skip this
            if (agt.getId() == po.getId() || isInActPerceptions(agt, po))
              continue;

            Vector dv = getDistanceVector(agt, po);
            Vector max = new Vector(pdata.getSpaceModel().getXMax(), pdata
                .getSpaceModel().getYMax(), pdata.getSpaceModel().getZMax());
            dv = dv.getDistanceVector(max);
            dv.x = (max.x > 0) ? (dv.x % max.x) : 0;
            dv.y = (max.y > 0) ? (dv.y % max.y) : 0;
            dv.z = (max.z > 0) ? (dv.z % max.z) : 0;

            if (dv.getLength() <= agt.getPerceptionRadius()) {
              Vector v = new Vector(agt.getVx(), agt.getVy(), agt.getVz());
              double angle = dv.getAngle(v);
              if (Double.isNaN(angle))
                angle = 0;
              double distance = dv.getLength();

              // generate and add PhysicalObjectPerceptionEvent
              addPerception(currentEvents, agt, po, distance, angle,
                  currentStep);
            }
          }
        }

        // test if agent is at border of space and perceptionradius reaches
        // beyond border
        if (((agt.getX() + agt.getPerceptionRadius() >= pdata.getSpaceModel()
            .getXMax())
            || (agt.getY() + agt.getPerceptionRadius() >= pdata.getSpaceModel()
                .getYMax() && pdata.getSpaceModel().getYMax() > 0) || (agt
            .getZ()
            + agt.getPerceptionRadius() >= pdata.getSpaceModel().getZMax() && pdata
            .getSpaceModel().getZMax() > 0))) {

          for (Physical po : phyobjs) {
            // if physical object is agent or the perception was already
            // handled, skip this
            if (agt.getId() == po.getId() || isInActPerceptions(agt, po))
              continue;

            Vector dv = getDistanceVector(agt, po);
            Vector max = new Vector(pdata.getSpaceModel().getXMax(), pdata
                .getSpaceModel().getYMax(), pdata.getSpaceModel().getZMax());
            dv.x = (max.x > 0) ? ((max.x + dv.x) % max.x) : 0;
            dv.y = (max.y > 0) ? ((max.y + dv.y) % max.y) : 0;
            dv.z = (max.z > 0) ? ((max.z + dv.z) % max.z) : 0;

            if (dv.getLength() <= agt.getPerceptionRadius()) {
              Vector v = new Vector(agt.getVx(), agt.getVy(), agt.getVz());
              double angle = dv.getAngle(v);
              if (Double.isNaN(angle))
                angle = 0;
              double distance = dv.getLength();

              // generate and add PhysicalObjectPerceptionEvent
              addPerception(currentEvents, agt, po, distance, angle,
                  currentStep);
            } // if

          } // for

        } // if

      } // for every agent

    } // if toroidal

  }

  /**
   * This method adds a PhysicalObjectPerceptionEvent for agent agt to
   * currentEvents.
   * 
   * @param currentEvents
   *          List of all current EnvironmentEvents. This method will add the
   *          new PhysicalObjectPerceptionEvent to this list.
   * @param agt
   *          Agent that perceives another physical object
   * @param obj
   *          The other physical object
   * @param distance
   *          Distance from agent to physical object
   * @param angle
   *          Angle between agent's vector of velocity and distance-vector (to
   *          physical object)
   * @param currentStep
   *          Current simulation step
   */
  private void addPerception(List<AtomicEvent> currentEvents, Physical agt,
      Physical obj, double distance, double angle, long currentStep) {

    // generate PhysicalObjectPerceptionEvent with all attributes
    PhysicalObjectPerceptionEvent temppope = new PhysicalObjectPerceptionEvent(
        currentStep, agt.getId(), obj.getClass().getSimpleName(), distance);
    temppope.setPerceptionAngle(angle);
    temppope.setPerceivedPhysicalObject(obj);

    // if id of physical object is perceivable, set this attribute in
    // PhysicalObjectPerceptionEvent
    if (idPerceivable.get(obj.getId())) {
      temppope.setPerceivedPhysicalObjectIdRef(obj.getId());
    }

    currentEvents.add(temppope);
  }

  /**
   * This method calculates and returns the distance-vector between physical
   * object po1 and physical object po2.
   * 
   * @param po1
   *          Physical object 1
   * @param po2
   *          Physical object 2
   * @return Distance-vector between physical objects
   */
  private Vector getDistanceVector(Physical po1, Physical po2) {
    Vector d1 = new Vector(po2.getX(), po2.getY(), po2.getZ());

    Vector d2 = new Vector(po1.getX(), po1.getY(), po1.getZ());

    return d1.getDistanceVector(d2);
  }

  private boolean isInActPerceptions(Physical o1, Physical o2) {
    for (int i = 0; i < actualPerception.size(); i++) {
      if (actualPerception.get(i).object1.getId() == o1.getId()
          && actualPerception.get(i).object2.getId() == o2.getId()) {
        return true;
      }
    }
    return false;
  }

  /*
   * private void
   * computePerceptions_simple(CopyOnWriteArrayList<EnvironmentEvent>
   * currentEvents, long currentStep) { for(int i = 0; i < agents.size(); i++) {
   * PhysicalAgentObject agt = agents.get(i);
   * ArrayList<PhysicalObjectPerceptionEvent> pevents = new
   * ArrayList<PhysicalObjectPerceptionEvent>(); for(int j = 0; j <
   * phyobjs.size(); j++) { PhysicalObject po = phyobjs.get(j); //calcCount++;
   * if(calcPerception(agt, po)) { // get angle between velocity-vector and
   * distance-vector Vector v = new Vector(agt.getVx(), agt.getVy(),
   * agt.getVz()); Vector dv = getDistanceVector(agt, po); double angle =
   * dv.getAngle(v); if(Double.isNaN(angle)) angle = 0; double distance =
   * dv.getLength();
   * 
   * // POPE erzeugen und adden addPerception(currentEvents, agt, po, distance,
   * angle, currentStep); } }
   * 
   * // is there were perceptions for this agent, send them for(int j = 0; j <
   * pevents.size(); j++) { currentEvents.add(pevents.get(j)); } }
   * 
   * //System.out.println("calcCount: " + calcCount); }
   */
}
