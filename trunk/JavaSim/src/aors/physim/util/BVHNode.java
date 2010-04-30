package aors.physim.util;

import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalObject;

/**
 * Class implements one node in the BVH-Tree. A node is associated with one
 * bounding sphere. It can have two children, a parent node and a physical
 * object.
 * 
 * @author Stefan Boecker
 * 
 */
public class BVHNode {

  /**
   * Physical object associated with this node. (optional, can be null)
   */
  public Physical phyobj;

  /**
   * Children of this node (no child or two children)
   */
  public BVHNode children[];

  /**
   * Bounding sphere associated with this node. (not optional)
   */
  public BoundingSphere volume;

  /**
   * Parent node of this node.
   */
  public BVHNode parent;

  public int childNr;

  /**
   * Creates a new blank node with no children an no parent.
   */
  public BVHNode() {
    this.children = new BVHNode[2];
    children[0] = null;
    children[1] = null;
    phyobj = null;
    volume = null;
    parent = null;
    childNr = 0;
  }

  /**
   * Creates a new node with given bounding volume (sphere) and given physical
   * object.
   * 
   * @param volume
   *          Bounding volume of new node
   * @param obj
   *          Physical object of new node
   */
  public BVHNode(BoundingSphere volume, Physical obj) {
    this.volume = volume;
    this.phyobj = obj;
    this.parent = null;
    this.children = new BVHNode[2];
    children[0] = null;
    children[1] = null;
    childNr = 0;
  }

  /**
   * This method inserts a new node below this node in the tree. If this node is
   * a leaf, the new node will be it's second child.
   * 
   * @param node
   *          New node to insert
   */
  public void insertNode(BVHNode node) {
    if (isLeaf()) {
      children[0] = new BVHNode(volume, phyobj);
      children[0].childNr = 0;
      children[0].parent = this;
      children[1] = node;
      children[1].childNr = 1;
      children[1].parent = this;
      phyobj = null;
    } else {
      if (children[0].newRadius(node.volume) < children[1]
          .newRadius(node.volume)) {
        children[0].insertNode(node);
      } else {
        children[1].insertNode(node);
      }
    }
    volume = new BoundingSphere(children[0].volume, children[1].volume);
  }

  /**
   * Insert a physical object below this node in the tree.
   * 
   * @param o
   *          New physical object to insert
   */
  public void insertPhysicalObject(PhysicalObject o) {
    BoundingSphere bs = new BoundingSphere(o);
    BVHNode n = new BVHNode(bs, o);
    insertNode(n);
  }

  /**
   * Removes the given node that is below this node in the tree. Notice: Because
   * a node has to have no or two children, the parent node of the given node
   * will be removed, too. In that case, the sibling of the node you want to
   * delete will be the new child of it's parent's parent. If a node was
   * deleted, the bounding volume of the parent node will be recalculated.
   * 
   * @param node
   *          Node to remove.
   * @return Root of the tree (can be null)
   */
  public BVHNode deleteNode(BVHNode node) {
    if (isLeaf())
      return null;

    if (children[0] == node) {
      if (parent == null) {
        return children[1];
      } else {
        parent.children[childNr] = children[1];
        return parent;
      }
    }

    if (children[1] == node) {
      if (parent == null) {
        return children[0];
      } else {
        parent.children[childNr] = children[0];
        return parent;
      }
    }

    if (children[0].deleteNode(node) == this
        || children[1].deleteNode(node) == this) {
      volume = new BoundingSphere(children[0].volume, children[1].volume);
      if (parent != null)
        return parent;
    }

    return this;
  }

  /**
   * Removes physical object from tree below this node. If a node was deleted,
   * all bounding volumes of nodes above it, will be recalculated.
   * 
   * @param po
   *          Physical object to remove
   * @return true if a node was deleted. false, otherwise
   */
  public boolean deletePhysical(Physical po) {
    if (isLeaf())
      return false;

    if (children[0].isLeaf()) {
      if (children[0].phyobj.getId() == po.getId()) {
        volume = children[1].volume;
        phyobj = children[1].phyobj;
        children = children[1].children;
        return true;
      }
    }

    if (children[1].isLeaf()) {
      if (children[1].phyobj.getId() == po.getId()) {
        volume = children[0].volume;
        phyobj = children[0].phyobj;
        children = children[0].children;
        return true;
      }
    }

    boolean changed = children[0].deletePhysical(po);
    changed |= children[1].deletePhysical(po);

    if (changed) {
      volume = new BoundingSphere(children[0].volume, children[1].volume);
    }

    return changed;
  }

  /**
   * Removes physical object from tree below this node. If a node was deleted,
   * all bounding volumes of nodes above it, will be recalculated.
   * 
   * @param id
   *          Id of physical object to remove
   * @return true if a node was deleted. false, otherwise
   */
  public boolean deletePhysicalObjectById(long id) {
    if (isLeaf())
      return false;

    if (children[0].isLeaf()) {
      if (children[0].phyobj.getId() == id) {
        volume = children[1].volume;
        phyobj = children[1].phyobj;
        children = children[1].children;
        return true;
      }
    }

    if (children[1].isLeaf()) {
      if (children[1].phyobj.getId() == id) {
        volume = children[0].volume;
        phyobj = children[0].phyobj;
        children = children[0].children;
        return true;
      }
    }

    boolean changed = children[0].deletePhysicalObjectById(id);
    changed |= children[1].deletePhysicalObjectById(id);

    if (changed) {
      volume = new BoundingSphere(children[0].volume, children[1].volume);
    }

    return changed;
  }

  /**
   * Determines if this node is a leaf. A leaf has a physical object associated
   * with it.
   * 
   * @return true, if node is a leaf. false, otherwise.
   */
  public boolean isLeaf() {
    return (phyobj != null);
  }

  /**
   * Determines if bounding volume of this node overlaps the bounding volume of
   * the given node.
   * 
   * @param node
   *          Other node
   * @return true, if it overlaps. false, otherwise
   */
  public boolean overlaps(BVHNode node) {
    return volume.overlaps(node.volume);
  }

  /**
   * Calculates the radius of a new bounding sphere, that would enclose this
   * bounding sphere and the given one.
   * 
   * @param newSphere
   *          Other bounding sphere
   * @return New radius of bounding sphere, that encloses both bounding spheres
   */
  private double newRadius(BoundingSphere newSphere) {
    return (volume.center.getDistanceVector(newSphere.center).getLength()
        + volume.radius + newSphere.radius) / 2;
  }

  /*
   * public void debug(int count) { System.out.println("###### count: " +
   * count); System.out.println("node: " + this); if(isLeaf()) {
   * System.out.println("leaf found: " + phyobj.getClass().getSimpleName() +
   * " (" + phyobj.getId() + ")"); } System.out.println("volume: r=" +
   * volume.radius + " c=" + volume.center.toString());
   * System.out.println("parent: " + parent);
   * System.out.println("###### ende count: " + count); if(!isLeaf()) {
   * children[0].debug(count+1); children[1].debug(count+1); } }
   */
}
