/**
 * 
 */
package aors.module.physics.simulator.twoD;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.jbox2d.common.Vec2;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.AxisSweep3_32;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.Geometry;
import aors.data.DataBus;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.logger.model.SimulationParameters;
import aors.model.envevt.CollisionEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.Physical;
import aors.model.envsim.Physical.PhysicsType;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.model.envsim.Shape2D;
import aors.model.envsim.Shape3D;
import aors.module.physics.simulator.PhysicsSimulator;
import aors.module.physics.collision.CollisionObjectType;
import aors.module.physics.util.BulletObject;
import aors.module.physics.util.MaterialConstants;
import aors.module.physics.util.UnitConverter;
import aors.util.Vector;

/**
 * A physics simulator for 3D simulation using the Bullet physics engine.
 * 
 * @author Holger Wuerke
 * 
 */
public class BulletSimulator2D extends PhysicsSimulator {

  /**
   * The ratio between the provided step duration value and the value used in
   * Bullet. The step duration value for Bullet should be 1/60.
   */
  private double timeRatio;
  
  /**
   * The Bullet world, where everything is simulated.
   */
  private DiscreteDynamicsWorld world;

  /**
   * A list of all Bullet objects. 
   */
  private List<BulletObject> bulletObjects = new ArrayList<BulletObject>();
  
  /**
   * A set that contains all Bullet bodies that have reached the space border
   * (used only in euclidean space).
   */
  private Set<BulletObject> borderReached = new HashSet<BulletObject>();

  /**
   * Creates a BulletSimulator. This will set up a world and all neccessary
   * objects in Bullet.
   * 
   * @param simParams
   * @param spaceModel
   * @param simModel
   * @param objects
   * @param agents
   */
  public BulletSimulator2D(SimulationParameters simParams,
      GeneralSpaceModel spaceModel, boolean autoKinematics,
      boolean autoCollisionDetection, boolean autoCollisionHandling,
      double gravitation, DataBus databus, List<PhysicalObject> objects,
      List<PhysicalAgentObject> agents) {

    super(simParams, spaceModel, autoKinematics, autoCollisionDetection,
        autoCollisionHandling, gravitation, databus, objects, agents);
    
    Vector v = new Vector(3, 1.5, 2);
    Quat4f q = eulerToQuaternion(v);
    Transform t = new Transform();
    t.setRotation(q);
    System.out.println(matrixToEuler(t.basis));
    

    // compute time ratio, so we can use a step duration value of 1/60 in Box2D
    timeRatio = stepDuration * 60;
    stepDuration = 1f / 60f;
    
    CollisionConfiguration cc = new DefaultCollisionConfiguration();
    Dispatcher dispatcher = new CollisionDispatcher(cc);
//    BroadphaseInterface bi = new AxisSweep3_32(new Vector3f(0, 0, 0), new Vector3f(spaceModel.getXMax(), spaceModel.getYMax(), spaceModel.getZMax()));
    Vector3f worldAabbMin = new Vector3f(-100, -100, -100);
    Vector3f worldAabbMax = new Vector3f(spaceModel.getXMax(), spaceModel.getYMax(), spaceModel.getZMax());
    int maxProxies = 1024;
    AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
    ConstraintSolver cs = new SequentialImpulseConstraintSolver();
    
    world = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, cs, cc);
    
    float g =  (float) (unitConverter.accelerationToUser(gravitation) * timeRatio * timeRatio);    
    world.setGravity(new Vector3f(0, g, 0));

    // add world borders
    

    // add a body for every object
    for (Physical object : getPhysicals()) {
      addBody(object);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.physics2d.PhysicsSimulator#simulationStepEnd()
   */
  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.physics2d.PhysicsSimulator#simulationStepStart()
   */
  @Override
  public void simulationStepStart(long stepNumber) {
    this.stepNumber = stepNumber;

    // long start, end;

    // start = System.nanoTime();
    updateEngineObjects();
    // end = System.nanoTime();

    // System.out.println("updateEngineObjects: " + (end-start));

    // perform one simulation step in the engine
    // start = System.nanoTime();
    world.stepSimulation(1f/60f, 10);
    // end = System.nanoTime();

    // System.out.println("step: " + (end-start));

    if (spaceModel.getGeometry().equals(Geometry.Euclidean)) {
      handleBorderContact();
    }

    // start = System.nanoTime();
    if (autoKinematics) {
      updateAorObjects();
    }
    // end = System.nanoTime();

    // System.out.println("updateAorObjects: " + (end-start));

    sendEvents(stepNumber);
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    if (objektDestroyEvent.getSource() instanceof Physical) {
      removeBody((Physical) (objektDestroyEvent.getSource()));
    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    if (objInitEvent.getSource() instanceof Physical) {
      addBody((Physical) (objInitEvent.getSource()));
    }
  }

  /**
   * Adds a Bullet RigidBody to the world which represents the given object.
   * 
   * @param object
   */
  private void addBody(Physical object) {
    Shape2D shapeType = object.getShape2D();
    BulletObject body;

    CollisionShape shape = null;
    Vector3f inertia = new Vector3f(0, 0, 0);
    Transform t = new Transform();
    t.origin.set((float) object.getX(), (float) object.getY(), (float) object.getZ());
    t.setRotation(eulerToQuaternion(object.getRot()));
    DefaultMotionState ms = new DefaultMotionState(t);
    
    switch (shapeType) {
      case rectangle:
        shape = new BoxShape(new Vector3f((float) object.getWidth() / 2, (float) object.getHeight() / 2, 0.5f));
        break;
    
      default:
        // sphere
        shape = new SphereShape((float) object.getWidth() / 2);
    }

    float mass = (float) object.getM();
    
    if (object.getPhysicsType().equals(PhysicsType.INFINITE_MASS)) {
      mass = 0;
    } else {
      shape.calculateLocalInertia(mass, inertia);      
    }
    
    body = new BulletObject(mass, ms, shape, inertia);
    body.setRestitution((float) MaterialConstants.restitution(object
        .getMaterialType()));
    body.setFriction((float) MaterialConstants.friction(object
        .getMaterialType()));

    
    if (autoKinematics) {
      // velocity
      float vx = (float) (unitConverter.velocityToUser(object.getV().getX()) * timeRatio);
      float vy = (float) (unitConverter.velocityToUser(object.getV().getY()) * timeRatio);
      float vz = (float) (unitConverter.velocityToUser(object.getV().getZ()) * timeRatio);
    
      body.setLinearVelocity(new Vector3f(vx, vy, vz));

      float omegaX = (float) (unitConverter.angularVelocityToUser(object.getOmegaX() * Math.PI / 180) * timeRatio); 
      float omegaY = (float) (unitConverter.angularVelocityToUser(object.getOmegaY() * Math.PI / 180) * timeRatio); 
      float omegaZ = (float) (unitConverter.angularVelocityToUser(object.getOmegaZ() * Math.PI / 180) * timeRatio); 

      body.setAngularVelocity(new Vector3f(omegaX, omegaY, omegaZ));
    }

    body.setAorObject(object);
    bulletObjects.add(body);
    world.addRigidBody(body);
  }

  /**
   * Removes the Box2D Body corresponding to the given object from the world.
   * 
   * @param object
   */
  private void removeBody(Physical object) {
    for (Iterator<BulletObject> it = bulletObjects.iterator(); it.hasNext();) {
      BulletObject bo = it.next();
      if (bo.getAorObject().equals(object)) {
        world.removeRigidBody(bo);
        it.remove();
        break;
      }
    }
  }

  /**
   * Update the PhysicalObjects and PhysicalAgentObjects with the data from the
   * engine.
   */
  private void updateAorObjects() {
    for (BulletObject object : bulletObjects) {
      Physical aorObject = object.getAorObject();
      Transform t = new Transform();
      object.getMotionState().getWorldTransform(t);

      // in toroidal space: update position if body is out of bounds
      if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
        if (t.origin.x < 0) {
          t.origin.x += spaceModel.getXMax();
        }

        if (t.origin.x >= spaceModel.getXMax()) {
          t.origin.x %= spaceModel.getXMax();
        }

        if (t.origin.y < 0) {
          t.origin.y += spaceModel.getYMax();
        }

        if (t.origin.y >= spaceModel.getYMax()) {
          t.origin.y %= spaceModel.getYMax();
        }

        if (t.origin.z < 0) {
          t.origin.z += spaceModel.getZMax();
        }

        if (t.origin.z >= spaceModel.getZMax()) {
          t.origin.z %= spaceModel.getZMax();
        }
        
        object.getMotionState().setWorldTransform(t);
      }
      
      // position
      aorObject.setX(t.origin.getX());
      aorObject.setY(t.origin.getY());
      aorObject.setZ(t.origin.getZ());
      
      // orientation
      Matrix3f m = t.basis;
      aorObject.setRot(matrixToEuler(m));
      
      
      // velocity
      Vector3f v =  new Vector3f();
      object.getLinearVelocity(v);
      aorObject.setVx(unitConverter.velocityToMetersPerSeconds(v.getX()) / timeRatio);
      aorObject.setVy(unitConverter.velocityToMetersPerSeconds(v.getY()) / timeRatio);
      aorObject.setVz(unitConverter.velocityToMetersPerSeconds(v.getZ()) / timeRatio);
      
      object.getAngularVelocity(v);
      aorObject.setOmegaX(unitConverter.angularVelocityToRadiansPerSeconds(v.getX() * 180 / Math.PI) / timeRatio);
      aorObject.setOmegaY(unitConverter.angularVelocityToRadiansPerSeconds(v.getY() * 180 / Math.PI) / timeRatio);
      aorObject.setOmegaZ(unitConverter.angularVelocityToRadiansPerSeconds(v.getZ() * 180 / Math.PI) / timeRatio);

      System.out.print(aorObject.getId() + ") ");
      System.out.print("x:" + t.origin.x + " y:"
      + t.origin.y + " z:" + t.origin.z + " ");
      v = object.getLinearVelocity(v);
      System.out.print("vx:" + v.x + " vy:"
      + v.y + " vz:" + v.z + " ");
      v = object.getAngularVelocity(v);
      System.out.print("omegaX:" + v.x + " omegaY:" + v.y + " omegaZ:" + v.z + " ");
      System.out.println();
      
    }
  }

  /**
   * Update the engine objects with the data from the PhysicalAgentObjects.
   */
  private void updateEngineObjects() {
    for (BulletObject object : bulletObjects) {
      Physical aorObject = object.getAorObject();
      
      // position, orientation
      Transform t = new Transform();
      t.origin.set((float) aorObject.getX(), (float) aorObject.getY(), (float) aorObject.getZ());
      t.setRotation(eulerToQuaternion(aorObject.getRot()));
      object.getMotionState().setWorldTransform(t);
      
      // velocity
      float vx = (float) (unitConverter.velocityToUser(aorObject.getV().getX()) * timeRatio);
      float vy = (float) (unitConverter.velocityToUser(aorObject.getV().getY()) * timeRatio);
      float vz = (float) (unitConverter.velocityToUser(aorObject.getV().getZ()) * timeRatio);
      
      object.setLinearVelocity(new Vector3f(vx, vy, vz));
      
      float omegaX = (float) (unitConverter.angularVelocityToUser(aorObject.getOmegaX() * Math.PI / 180) * timeRatio); 
      float omegaY = (float) (unitConverter.angularVelocityToUser(aorObject.getOmegaY() * Math.PI / 180) * timeRatio); 
      float omegaZ = (float) (unitConverter.angularVelocityToUser(aorObject.getOmegaZ() * Math.PI / 180) * timeRatio); 

      object.setAngularVelocity(new Vector3f(omegaX, omegaY, omegaZ));
     
      // acceleration
      if (aorObject.getAx() != 0 || aorObject.getAy() != 0 || aorObject.getAz() != 0) {
        // F = m * a
        float fx = (float) (unitConverter.accelerationToUser(aorObject.getAx())
            * timeRatio * timeRatio * aorObject.getM());
        float fy = (float) (unitConverter.accelerationToUser(aorObject.getAy())
            * timeRatio * timeRatio * aorObject.getM());
        float fz = (float) (unitConverter.accelerationToUser(aorObject.getAz())
            * timeRatio * timeRatio * aorObject.getM());
        
        object.applyCentralForce(new Vector3f(fx, fy, fz));   
      }
      
      if (aorObject.getAlphaX() != 0 || aorObject.getAlphaY() != 0 || aorObject.getAlphaZ() != 0) {
        // torque = inertia * alpha
        float alphaX = (float) (unitConverter.angularAccelerationToUser(aorObject
            .getAlphaX() * Math.PI / 180) * timeRatio * timeRatio);
        float alphaY = (float) (unitConverter.angularAccelerationToUser(aorObject
            .getAlphaY() * Math.PI / 180) * timeRatio * timeRatio);
        float alphaZ = (float) (unitConverter.angularAccelerationToUser(aorObject
            .getAlphaZ() * Math.PI / 180) * timeRatio * timeRatio);
        
        object.applyTorque(new Vector3f(alphaX, alphaY, alphaZ));
      }
      
    }
  }
  
  /**
   * Converts a rotation represented as a quaternion into the same rotation
   * represented by 3 euler angles.
   * 
   * @param q the quaternion
   * @return a vector with the 3 euler angles
   */
  private Vector quaternionToEuler(Quat4f q) {
    float m00 = 1.0f - 2.0f * (q.getY() * q.getY() + q.getZ() * q.getZ());
    float m01 = 2.0f * (q.getX() * q.getY() + q.getZ() * q.getW());
    float m02 = 2.0f * (q.getZ() * q.getX() - q.getY() * q.getW());

    float m10 = 2.0f * (q.getX() * q.getY() - q.getZ() * q.getW());
    float m11 = 1.0f - 2.0f * (q.getZ() * q.getZ() + q.getX() * q.getX());
    float m12 = 2.0f * (q.getY() * q.getZ() + q.getX() * q.getW());

    float m20 = 2.0f * (q.getZ() * q.getX() + q.getY() * q.getW());
    float m21 = 2.0f * (q.getY() * q.getZ() - q.getX() * q.getW());
    float m22 = 1.0f - 2.0f * (q.getY() * q.getY() + q.getX() * q.getX());
    
    Matrix3f m = new Matrix3f(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    m.transpose();

    double cosY = Math.sqrt(m.m00 * m.m00 + m.m10 * m.m10);
    
    double x, y, z;
    if (cosY > 16 * BulletGlobals.FLT_EPSILON) {
      x = Math.atan2(1.0f * m.m21, m.m22);
      y = Math.atan2(-1.0f * m.m20, cosY);
      z = Math.atan2(1.0f * m.m10, m.m00);
    } else {
      x = Math.atan2(-1.0f * m.m12, m.m11);
      y = Math.atan2(-1.0f * m.m20, cosY);
      z = 0.0;
    }
    
    return new Vector(x, y, z);
  }
  
  /**
   * Converts a rotation specified in euler angles into the same rotation 
   * represented as a quaternion.
   * 
   * @param v a vector with the 3 euler angles
   * @return the quaternion
   */
  private Quat4f eulerToQuaternion(Vector v) {
//    double sx = Math.sin(v.getX() * 0.5);
//    double cx = Math.cos(v.getX() * 0.5);
//
//    double sy = Math.sin(v.getY() * 0.5);
//    double cy = Math.cos(v.getY() * 0.5);
//    
//    double sz = Math.sin(v.getZ() * 0.5);
//    double cz = Math.cos(v.getZ() * 0.5);
//
//    float w = (float) ((cx * cy * cz) + (sx * sy * sz));
//    float x = (float) ((sx * cy * cz) - (cx * sy * sz));
//    float y = (float) ((cx * sy * cz) + (sx * cy * sz));
//    float z = (float) ((cx * cy * sz) - (sx * sy * cz));
//
//    return new Quat4f(x, y, z, w);
    
    double sinx = Math.sin(v.getX());
    double cosx = Math.cos(v.getX());
    
    double siny = Math.sin(v.getY());
    double cosy = Math.cos(v.getY());
    
    double sinz = Math.sin(v.getZ());
    double cosz = Math.cos(v.getZ());
    
    float m11 = (float) (cosz * cosy);
    float m12 = (float) (cosz * siny * sinx - sinz * cosx);
    float m13 = (float) (cosz * siny * cosx + sinz * sinx);
    float m21 = (float) (sinz * cosy);
    float m22 = (float) (sinz * siny * sinx + cosz * cosx);
    float m23 = (float) (sinz * siny * cosx - cosz * sinx);
    float m31 = (float) -siny;
    float m32 = (float) (cosy * sinx);
    float m33 = (float) (cosy * cosx);
    
    Matrix3f m = new Matrix3f(m11, m12, m13, m21, m22, m23, m31, m32, m33);

    Quat4f q = new Quat4f();
    q.set(m);
    
    return q;
  }
  
  /**
   * Converts a rotation represented as a matrix into the same rotation
   * represented by 3 euler angles.
   * 
   * @param m the rotation matrix
   * @return a vector with the 3 euler angles
   */
  private Vector matrixToEuler(Matrix3f m) {
    double y = Math.atan2(-m.m20, Math.sqrt(m.m00 * m.m00 + m.m10 * m.m10));
    double cosy = Math.cos(y);
    
    if (cosy != 0) {
      double x = Math.atan2(m.m21 / cosy, m.m22 / cosy);
      double z = Math.atan2(m.m10 / cosy, m.m00 / cosy);
      return new Vector(x, y, z);
    }
    
    double z = 0;
    double x = Math.atan2(m.m01, m.m11);
    
    if (y < 0) {
      x = -x; 
    } 
    
    return new Vector(x, y, z);
  }

  /**
   * For objects that have reached the space border, replace the dynamic bodies
   * with static bodies in Bullet (only used in euclidean space).
   */
  private void handleBorderContact() {
  }

  /**
   * Converts the points attribute string from the Physical interface into a
   * list of doubles. x- and y-values are saved in alternate order.
   * 
   * @param points
   * @return
   */
  private List<Double> getPointsList(String points) {
    List<Double> list = new ArrayList<Double>();

    String[] tmp = points.split("[, ]");
    for (int i = 0; i < tmp.length; i++) {
      if (tmp[i].equals("")) {
        continue;
      }

      list.add(Double.valueOf(tmp[i]));
    }

    return list;
  }

}
