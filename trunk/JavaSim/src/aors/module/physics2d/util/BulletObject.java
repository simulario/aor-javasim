/**
 * 
 */
package aors.module.physics2d.util;

import javax.vecmath.Vector3f;

import aors.model.envsim.Physical;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;

/**
 * @author Holger Wuerke
 *
 */
public class BulletObject extends RigidBody {

  /**
   * The corresponding AOR object. 
   */
  private Physical aorObject;
  
  /**
   * The type of collision object this object represents.
   */
  private CollisionObjectType collisionObjectType;
  
  /**
   * @param constructionInfo
   */
  public BulletObject(RigidBodyConstructionInfo constructionInfo) {
    super(constructionInfo);
  }

  /**
   * @param mass
   * @param motionState
   * @param collisionShape
   */
  public BulletObject(float mass, MotionState motionState,
      CollisionShape collisionShape) {
    super(mass, motionState, collisionShape);
  }

  /**
   * @param mass
   * @param motionState
   * @param collisionShape
   * @param localInertia
   */
  public BulletObject(float mass, MotionState motionState,
      CollisionShape collisionShape, Vector3f localInertia) {
    super(mass, motionState, collisionShape, localInertia);
  }

  /**
   * @param aorObject the aorObject to set
   */
  public void setAorObject(Physical aorObject) {
    this.aorObject = aorObject;
  }

  /**
   * @return the aorObject
   */
  public Physical getAorObject() {
    return aorObject;
  }

  /**
   * @param collisionObjectType the collisionObjectType to set
   */
  public void setCollisionObjectType(CollisionObjectType collisionObjectType) {
    this.collisionObjectType = collisionObjectType;
  }

  /**
   * @return the collisionObjectType
   */
  public CollisionObjectType getCollisionObjectType() {
    return collisionObjectType;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "BulletObject: Id: " + aorObject.getId() + 
      " Pos: " + aorObject.getPos();
  }

  
}
