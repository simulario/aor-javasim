package aors.physim.extsim;

import java.util.ArrayList;
import java.util.List;

import aors.GeneralSpaceModel;
import aors.model.AtomicEvent;
import aors.model.envevt.CollisionEvent;
import aors.model.envsim.Physical;
import aors.physim.baseclasses.ExternalSimulator;
import aors.physim.baseclasses.PhysicsData;
import aors.physim.util.BVHTree;
import aors.physim.util.Vector;
import aors.physim.util.BVHTree.PotentialCollision;

/**
 * CollisionSimulator implements detection of collision between two physical
 * objects in a simulation.
 * 
 * @author Stefan Boecker
 * 
 */
public class CollisionSimulator extends ExternalSimulator {

  /**
   * ActualCollision represents an actual collision between two physical
   * objects.
   * 
   * @author Stefan Boecker
   * 
   */
  public class ActualCollision {

    public Physical po1;
    public Physical po2;

    public aors.physim.util.Vector contactNormal;

    public ActualCollision(Physical o1, Physical o2, aors.physim.util.Vector cn) {
      po1 = o1;
      po2 = o2;
      contactNormal = cn;
    }

  }

  /**
   * List of potential collisions between two physical objects.
   */
  private ArrayList<BVHTree.PotentialCollision> potcol;

  /**
   * The BVHTree is binary tree, that allows an efficient way to detect
   * collisions.
   */
  private BVHTree tree;

  private boolean autoCollision = false;

  public CollisionSimulator(PhysicsData pdata, boolean autoCollision) {
    super(pdata);
    this.autoCollision = autoCollision;
    tree = new BVHTree(getPhysicalObjectList());
    potcol = new ArrayList<BVHTree.PotentialCollision>();
  }

  /**
   * Calculate if two physical objects are in touch.
   * 
   * @param po1
   *          Physical object 1
   * @param po2
   *          Physical object 2
   * @return true if the two objects are in collision, false otherwise
   */
  private boolean calcCollision(Physical po1, Physical po2) {
    if (Math.abs(po1.getX() - po2.getX()) > (po1.getWidth() / 2 + po2
        .getWidth() / 2))
      return false;
    if (Math.abs(po1.getY() - po2.getY()) > (po1.getHeight() / 2 + po2
        .getHeight() / 2))
      return false;
    if (Math.abs(po1.getZ() - po2.getZ()) > (po1.getDepth() / 2 + po2
        .getDepth() / 2))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.physim.baseclasses.ExternalSimulator#simulate(java.util.List,
   * long)
   */
  public void simulate(List<AtomicEvent> currentEvents, long currentStep) {
    List<Physical> phyobjs = getPhysicalObjectList();
    tree = new BVHTree(getPhysicalObjectList());
    potcol = new ArrayList<BVHTree.PotentialCollision>();

    // delete destroyed physical objects
    // TODO: check if its neccessary, because it is run in PerceptionSimulator
    /*
     * for (long id : physicalObjectDestroyed) {
     * tree.deletePhysicalObjectById(id); }
     */

    // update BVH-Tree
    for (Physical physical : phyobjs) {
      if (pdata.getPositionChanged().get(physical.getId())) {
        tree.updatePhysicalObject(physical);
      }
    }

    // test every object (node) for collision
    potcol.clear();
    pdata.getCollisions().clear();
    for (Physical physical : phyobjs) {
      tree.testCollision(physical, potcol);
    }

    // calculate if potential collision is actual collision
    for (PotentialCollision potentialCollision : potcol) {

      if (calcCollision(potentialCollision.object1, potentialCollision.object2)) {

        // determine the collision normal
        Physical o1 = potentialCollision.object1;
        Physical o2 = potentialCollision.object2;

        Vector cn = new Vector();

        if (pdata.getSpaceModel().getDimensions() == GeneralSpaceModel.Dimensions.two) {
          /*
           * A ---- B | | | | D ---- C
           */

          ArrayList<Vector> pts = new ArrayList<Vector>();

          Vector A1 = new Vector(o1.getX() - o1.getWidth() / 2, o1.getY()
              + o1.getHeight() / 2);
          Vector B1 = new Vector(o1.getX() + o1.getWidth() / 2, o1.getY()
              + o1.getHeight() / 2);
          Vector C1 = new Vector(o1.getX() + o1.getWidth() / 2, o1.getY()
              - o1.getHeight() / 2);
          Vector D1 = new Vector(o1.getX() - o1.getWidth() / 2, o1.getY()
              - o1.getHeight() / 2);

          Vector A2 = new Vector(o2.getX() - o2.getWidth() / 2, o2.getY()
              + o2.getHeight() / 2);
          Vector B2 = new Vector(o2.getX() + o2.getWidth() / 2, o2.getY()
              + o2.getHeight() / 2);
          Vector C2 = new Vector(o2.getX() + o2.getWidth() / 2, o2.getY()
              - o2.getHeight() / 2);
          Vector D2 = new Vector(o2.getX() - o2.getWidth() / 2, o2.getY()
              - o2.getHeight() / 2);

          // System.out.println("A2: " + A2 + " # D2: " + C2);
          if (isInPlane2D(A1, A2, C2))
            pts.add(A1);
          if (isInPlane2D(B1, A2, C2))
            pts.add(B1);
          if (isInPlane2D(C1, A2, C2))
            pts.add(C1);
          if (isInPlane2D(D1, A2, C2))
            pts.add(D1);

          if (isInPlane2D(A2, A1, C1))
            pts.add(A2);
          if (isInPlane2D(B2, A1, C1))
            pts.add(B2);
          if (isInPlane2D(C2, A1, C1))
            pts.add(C2);
          if (isInPlane2D(D2, A1, C1))
            pts.add(D2);

          // System.out.println(java.util.Arrays.toString(pts.toArray()));

          if ((pts.contains(A1) && pts.contains(B1))
              || (pts.contains(D2) && pts.contains(C2)))
            cn = new Vector(0, -1);
          else if (pts.contains(A1) && pts.contains(D1)
              || (pts.contains(B2) && pts.contains(C2)))
            cn = new Vector(1, 0);
          else if (pts.contains(B1) && pts.contains(C1)
              || (pts.contains(A2) && pts.contains(D2)))
            cn = new Vector(-1, 0);
          else if (pts.contains(D1) && pts.contains(C1)
              || (pts.contains(A2) && pts.contains(B2)))
            cn = new Vector(0, 1);
          else {
            cn = new Vector(o1.getX(), o1.getY(), o1.getZ());
            cn = cn.getDistanceVector(new Vector(o2.getX(), o2.getY(), o2
                .getZ()));
            cn.normalize();
          }

        } else if (pdata.getSpaceModel().getDimensions() == GeneralSpaceModel.Dimensions.three) {

          /*
           * E --- F / /| A --- B | | H | G | |/ D --- C
           */
          /*
           * Vector A1 = new Vector (o1.getX()-o1.getWidth()/2,
           * o1.getY()+o1.getHeight()/2); Vector B1 = new Vector
           * (o1.getX()+o1.getWidth()/2, o1.getY()+o1.getHeight()/2); Vector C1
           * = new Vector (o1.getX()+o1.getWidth()/2,
           * o1.getY()-o1.getHeight()/2); Vector D1 = new Vector
           * (o1.getX()-o1.getWidth()/2, o1.getY()-o1.getHeight()/2); Vector E1
           * = new Vector (o1.getX()-o1.getWidth()/2,
           * o1.getY()+o1.getHeight()/2); Vector F1 = new Vector
           * (o1.getX()+o1.getWidth()/2, o1.getY()+o1.getHeight()/2); Vector G1
           * = new Vector (o1.getX()+o1.getWidth()/2,
           * o1.getY()-o1.getHeight()/2); Vector H1 = new Vector
           * (o1.getX()-o1.getWidth()/2, o1.getY()-o1.getHeight()/2);
           */

          cn = new Vector(o1.getX(), o1.getY(), o1.getZ());
          cn = cn
              .getDistanceVector(new Vector(o2.getX(), o2.getY(), o2.getZ()));
          cn.normalize();
        } else {
          cn = new Vector(o1.getX(), o1.getY(), o1.getZ());
          cn = cn
              .getDistanceVector(new Vector(o2.getX(), o2.getY(), o2.getZ()));
          cn.normalize();
        }

        if (!isInCollisions(o2, o1)) {
          // add to actual collisions
          pdata.getCollisions().add(new ActualCollision(o1, o2, cn));

          // send CollisionEvent to both physical objects
          if (autoCollision) {
            /*
             * System.out.println(currentStep + ": collision: " + o1.getId() +
             * "(y=" + o1.getY() + ")" + " " + o2.getId() + "(y=" + o2.getY() +
             * ")"); for (int k = 0; k < phyobjs.size(); k++)
             * System.out.print(" " + phyobjs.get(k).getId());
             * System.out.println();
             */

            CollisionEvent cevent = new CollisionEvent(currentStep + 1);
            cevent.setPhysicalObject1(o1);
            cevent.setPhysicalObject2(o2);

            currentEvents.add(cevent);
          }
        }
      }
    }
  }

  private boolean isInCollisions(Physical o1, Physical o2) {
    for (int i = 0; i < pdata.getCollisions().size(); i++) {
      if (pdata.getCollisions().get(i).po1.getId() == o1.getId()
          && pdata.getCollisions().get(i).po2.getId() == o2.getId()) {
        return true;
      }
    }
    return false;
  }

  private boolean isInPlane2D(Vector P, Vector A, Vector D) {
    return (P.x >= A.x && P.x <= D.x && P.y >= D.y && P.y <= A.y);
  }

  /*
   * public void simulate(EnvironmentRuleResult result) {
   * System.out.println("simstep: " + step); step++; for(int i = 0; i <
   * phyobjs.size(); i++) { for(int j = 0; j < phyobjs.size(); j++) { if(i != j)
   * { if(calcCollision(phyobjs.get(i), phyobjs.get(j))) {
   * System.out.println("collision of " + phyobjs.get(i).getId() + " and " +
   * phyobjs.get(j).getId()); } numberOfCalcs++; } } }
   * 
   * if(step == 100) { System.out.println("numberOfCalcs (simple): " +
   * numberOfCalcs); } }
   */

}
