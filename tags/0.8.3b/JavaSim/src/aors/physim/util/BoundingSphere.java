package aors.physim.util;

import aors.model.envsim.Physical;

/**
 * This class implements a bounding buffer for physical objects. The bounded
 * buffer is given by a center (vector) and a radius.
 * 
 * @author Stefan Boecker
 * 
 */
public final class BoundingSphere {

  /**
   * Radius of the bounding sphere
   */
  public double radius;

  /**
   * Center of the bounding sphere
   */
  public Vector center;

  /**
   * Physical object, that is associated with this bounded buffer.
   */
  public Physical phyobj = null;

  /**
   * Creates a new BoundingSphere for the given physical object. Radius and
   * center will be calculated from object's position and size.
   * 
   * @param object
   *          Physical object
   */
  public BoundingSphere(Physical object) {
    // center is just the current position of this object
    center = new Vector(object.getX(), object.getY(), object.getZ());
    // radius is the distance between center and farthest point in objects shape
    radius = center.getDistanceVector(
        new Vector(center.x + object.getWidth() / 2, center.y
            + object.getHeight() / 2, center.z + object.getDepth() / 2))
        .getLength();
    phyobj = object;
  }

  /**
   * Creates a new bounding sphere with given center and radius.
   * 
   * @param c
   *          Center of bounding sphere
   * @param r
   *          Radius of bounding sphere
   */
  public BoundingSphere(Vector c, double r) {
    radius = r;
    center = c;
  }

  /**
   * Creates a new bounding sphere for given physical object and with given
   * radius. Center will be the position of the object.
   * 
   * @param object
   *          Physical object
   * @param radius
   *          Radius for new bounding sphere
   */
  public BoundingSphere(Physical object, double radius) {
    // center is just the current position of this object
    center = new Vector(object.getX(), object.getY(), object.getZ());
    phyobj = object;
    this.radius = radius;
  }

  /**
   * Creates new bounding sphere that encloses both given bounding spheres.
   * 
   * @param sphere1
   *          Bounding sphere 1
   * @param sphere2
   *          Bounding sphere 2
   */
  public BoundingSphere(BoundingSphere sphere1, BoundingSphere sphere2) {
    if (sphere1.center.equals(sphere2.center)) {
      center = new Vector(sphere1.center.x, sphere1.center.y, sphere1.center.z);
      radius = sphere1.radius > sphere2.radius ? sphere1.radius
          : sphere2.radius;
    } else {
      radius = (sphere1.radius + sphere2.radius + sphere1.center
          .getDistanceVector(sphere2.center).getLength()) / 2;
      center = sphere2.center.getDistanceVector(sphere1.center);
      center.mul(radius - sphere1.radius);
      center.div(sphere1.center.getDistanceVector(sphere2.center).getLength());
      center.add(sphere1.center);
    }
  }

  /**
   * Determines of this bounding sphere overlaps the given bounding sphere.
   * 
   * @param volume
   *          Bounding sphere
   * @return true, if this bounding sphere overlaps the given one. false,
   *         otherwise
   */
  public boolean overlaps(BoundingSphere volume) {
    Vector dv = center.getDistanceVector(volume.center);
    return (dv.getLength() <= radius + volume.radius);
  }

  /*
   * public BoundingSphere(List<BoundingSphere> objectBVs, int start, int end) {
   * int numOfObjects = end - start + 1; // first calculate the center for this
   * boundingsphere double x=0, y=0, z=0; for(int i = start; i <= end; i++) { x
   * += objectBVs.get(i).center.x; y += objectBVs.get(i).center.y; z +=
   * objectBVs.get(i).center.z; } center = new Vector(x/numOfObjects,
   * y/numOfObjects, z/numOfObjects);
   * 
   * // now calculate the radius // it is the farthest point from center radius
   * = 0; for(int i = start; i <= end; i++) { BoundingSphere s =
   * objectBVs.get(i); double newRadius =
   * center.getDistanceVector(s.center).getLength() + s.radius; if(newRadius >
   * radius) { radius = newRadius; } } }
   * 
   * public void updatePosition(PhysicalObject object) { center = new
   * Vector(object.getX(), object.getY(), object.getZ()); }
   */

}
