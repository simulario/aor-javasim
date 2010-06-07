package aors.physim.util;

import java.util.ArrayList;
import java.util.List;

import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalObject;

/**
 * Class implements a Tree of BVHNodes. It also defines an inner class
 * PotentialCollision.
 * 
 * @author Stefan Boecker
 * 
 */
public class BVHTree {

  /**
   * Class represents a potential collision between two physical objects.
   * 
   * @author Stefan Boecker
   * 
   */
  public class PotentialCollision {
    public Physical object1;
    public Physical object2;

    public PotentialCollision(Physical o1, Physical o2) {
      object1 = o1;
      object2 = o2;
    }
  }

  /**
   * The root of the tree.
   */
  private BVHNode root;

  /**
   * List of physical objects in this tree.
   */
  private List<Physical> phyobjs;

  /**
   * Builds a new BVHTree (using insertion method) with given list of physical
   * objects.
   * 
   * @param phyobjs
   *          List of physical objects
   */
  public BVHTree(List<Physical> phyobjs) {
    this.phyobjs = phyobjs;
    if (phyobjs.size() > 0) {
      buildBVHTreeInsertion();
    }
  }

  /**
   * Tests, if there are potential collisions with the given physical object.
   * All potential collisions will be added to the given List potcol.
   * 
   * @param o
   *          Physical object
   * @param potcol
   *          List of potential collisions (should be empty)
   */
  public void testCollision(Physical o, ArrayList<PotentialCollision> potcol) {
    BoundingSphere sphere = new BoundingSphere(o);
    BVHNode node = new BVHNode(sphere, o);
    testCollision(node, root, potcol);
  }

  /**
   * Deletes physical objects in tree.
   * 
   * @param o
   *          Physical object
   */
  public void deletePhysicalObject(Physical o) {
    root.deletePhysical(o);
  }

  public void deletePhysicalObjectById(long id) {
    root.deletePhysicalObjectById(id);
  }

  /**
   * Inserts given physical object into tree.
   * 
   * @param o
   *          Physical object
   */
  public void insertPhysicalObject(PhysicalObject o) {
    root.insertPhysicalObject(o);
  }

  /**
   * Remove node from tree.
   * 
   * @param n
   *          Node to delete
   */
  public void deleteNode(BVHNode n) {
    root.deleteNode(n);
  }

  /**
   * Insert node into tree.
   * 
   * @param n
   *          Node to insert
   */
  public void insertNode(BVHNode n) {
    root.insertNode(n);
  }

  /**
   * Update physical object in tree. Use this method if position or size of
   * physical objects changed.
   * 
   * @param o
   *          Physical object
   */
  public void updatePhysicalObject(Physical o) {
    root.deletePhysical(o);
    BoundingSphere bs = new BoundingSphere(o);
    BVHNode n = new BVHNode(bs, o);
    root.insertNode(n);
  }

  /**
   * Test if there is a collision with the given bounding sphere and another
   * bounding sphere in the tree. All potential collisions will be added to the
   * list potcol.
   * 
   * @param bs
   *          Bounding sphere
   * @param potcol
   *          List of potential collisions (should be empty)
   */
  public void testCollision(BoundingSphere bs, List<PotentialCollision> potcol) {
    BVHNode node = new BVHNode(bs, bs.phyobj);
    testCollision(node, root, potcol);
  }

  /**
   * Test if there is a collision between the two given nodes or their children.
   * All potential collisions will be added to the list potcol.
   * 
   * @param node1
   *          Node 1
   * @param node2
   *          Node 2
   * @param potcol
   *          List of potential collisions (should be empty)
   */
  private void testCollision(BVHNode node1, BVHNode node2,
      List<PotentialCollision> potcol) {
    if (!node1.overlaps(node2) || node1.phyobj == node2.phyobj) {
      return;
    }

    if (node1.isLeaf() && node2.isLeaf()) {
      // potential collision detected
      potcol.add(new PotentialCollision(node1.phyobj, node2.phyobj));
    } else {
      if (node1.isLeaf()) {
        testCollision(node1, node2.children[0], potcol);
        testCollision(node1, node2.children[1], potcol);
      } else {
        testCollision(node1.children[0], node2, potcol);
        testCollision(node1.children[1], node2, potcol);
      }
    }

    return;
  }

  /**
   * Builds up the BVHTree using the insertion method.
   */
  private void buildBVHTreeInsertion() {
    // create root
    root = new BVHNode(new BoundingSphere(phyobjs.get(0)), phyobjs.get(0));

    // create other nodes
    for (int i = 1; i < phyobjs.size(); i++) {
      Physical o = phyobjs.get(i);
      BoundingSphere bs = new BoundingSphere(o);
      BVHNode n = new BVHNode(bs, o);
      root.insertNode(n);
    }

  }

  public void addNode(Physical physical) {

    if (physical != null) {

      if (root == null) {
        // create root
        root = new BVHNode(new BoundingSphere(physical), physical);
      } else {

        BoundingSphere bs = new BoundingSphere(physical);
        BVHNode n = new BVHNode(bs, physical);
        root.insertNode(n);
      }
    }
  }

  /*
   * public void debug() { root.debug(0); }
   * 
   * 
   * private void sortPhysicalObjects(List<BoundingSphere> o) {
   * 
   * }
   * 
   * private BVHNode buildBVHTreeTopDown(List<BoundingSphere> o, int start, int
   * end) { BVHNode node = new BVHNode(); BoundingSphere sphere = new
   * BoundingSphere(o, start, end); node.volume = sphere;
   * 
   * if(start == end) { node.phyobj = o.get(start).phyobj;
   * objectnodes.add(node); } else { sortPhysicalObjects(o); int m = ((end -
   * start) / 2) + 1; node.children[0] = buildBVHTreeTopDown(o, start, end - m);
   * node.children[1] = buildBVHTreeTopDown(o, end - m + 1, end); }
   * 
   * return node; }
   * 
   * public BVHTree(ArrayList<Agent> agents, ArrayList<Object> objects) { int
   * objectstart = 0; int agentstart = 0; // create root if(objects.size() > 0)
   * { root = new BVHNode(new BoundingSphere(objects.get(0)), objects.get(0));
   * objectstart = 1; agentstart = 0; } else { if(agents.size() > 0) { root =
   * new BVHNode(new BoundingSphere(agents.get(0)), agents.get(0)); objectstart
   * = 0; agentstart = 1; } }
   * 
   * // create nodes for objects for(int i = objectstart; i < objects.size();
   * i++) { Object o = objects.get(i); BoundingSphere bs = new
   * BoundingSphere(o); BVHNode n = new BVHNode(bs, o); root.insertNode(n); }
   * 
   * // create nodes for agents with perceptionradius as boundingsphere for(int
   * i = agentstart; i < agents.size(); i++) { Agent a = agents.get(i);
   * BoundingSphere bs = new BoundingSphere(a, a.getPerceptionRadius()); BVHNode
   * n = new BVHNode(bs, a); root.insertNode(n); }
   * 
   * }
   */
}
