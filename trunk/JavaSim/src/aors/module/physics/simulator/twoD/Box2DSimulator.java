/**
 * 
 */
package aors.module.physics.simulator.twoD;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;


import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.Geometry;
import aors.data.DataBus;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.logger.model.EnvSimInputEventType;
import aors.logger.model.EnvironmentSimulatorStep;
import aors.logger.model.PhysAgtType;
import aors.logger.model.PhysicalObjType;
import aors.logger.model.ResultingStateChangesType;
import aors.logger.model.SimulationParameters;
import aors.logger.model.SimulationStep;
import aors.model.envevt.CollisionEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.Physical;
import aors.model.envsim.Physical.PhysicsType;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.model.envsim.Shape2D;
import aors.module.physics.simulator.PhysicsSimulator;
import aors.module.physics.collision.CollisionObjectType;
import aors.module.physics.collision.Perception2D;
import aors.module.physics.util.MaterialConstants;
import aors.module.physics.util.UtilFunctions;


/**
 * A physics simulator for 2D simulation using the Box2D physics engine.
 * 
 * @author Holger Wuerke
 * 
 */
public class Box2DSimulator extends PhysicsSimulator {

  /**
   * The ratio between the provided step duration value and the value used in
   * Box2D. The step duration value for Box2D should be 1/60.
   */
  private double timeRatio;

  /**
   * timeRatio * timeRatio 
   */
  private double timeRatioSquared;
  
  /**
   * The Box2D world, where everything is simulated.
   */
  private World world;
  
  /**
   * A map that maps AOR-object IDs to Box2D bodies. 
   */
  private Map<Long, Body> bodyMap = new HashMap<Long, Body>();

  /**
   * A set that contains all Box2D bodies that have reached the space border
   * (used only in euclidean space).
   */
  private Set<Body> borderContact = new HashSet<Body>();

  /**
   * A set that contains all perceptions of the current step. We need this 
   * because the collision listener might signal one perception several times 
   * in one step.
   */
  private Set<Perception2D> perceptions = new HashSet<Perception2D>();
  
  /**
   * A set that contains all collision events of the current step. We need this 
   * because the collision listener might signal one collision several times 
   * in one step.
   */
  private Set<CollisionEvent> collisionEvents = new HashSet<CollisionEvent>();
  
  /**
   * Creates a Box2DSimulator. This will set up a world and all neccessary
   * objects (bodies) in Box2D.
   * 
   * @param simParams
   * @param spaceModel
   * @param simModel
   * @param objects
   * @param agents
   */
  public Box2DSimulator(SimulationParameters simParams,
      GeneralSpaceModel spaceModel, boolean autoKinematics,
      boolean autoCollisionDetection, boolean autoCollisionHandling,
      double gravitation, DataBus databus, List<PhysicalObject> objects,
      List<PhysicalAgentObject> agents) {

    super(simParams, spaceModel, autoKinematics, autoCollisionDetection,
        autoCollisionHandling, gravitation, databus, objects, agents);

    // compute time ratio, so we can use a step duration value of 1/60 in Box2D
    timeRatio = stepDuration * 60;
    timeRatioSquared = timeRatio * timeRatio;
    stepDuration = 1f / 60f;

    Vec2 g = new Vec2(0f, (float) (unitConverter.accelerationToUser(gravitation) * timeRatioSquared));
    world = new World(g, true);

    CollisionListener cl = new CollisionListener();
    world.setContactListener(cl);

    // add static objects that act as the world borders for euclidean space
    if (spaceModel.getGeometry().equals(Geometry.Euclidean)) {
      // bottom
      BodyDef borderDef = new BodyDef();
      borderDef.type = BodyType.STATIC;
      borderDef.position.set((float) (spaceModel.getXMax() / 2), -0.5f);
      Body border = world.createBody(borderDef);
  
      PolygonShape borderShape = new PolygonShape();
      borderShape.setAsBox((float) (spaceModel.getXMax() / 2), 0.5f);
      FixtureDef borderFixture = new FixtureDef();
      borderFixture.shape = borderShape;
      borderFixture.userData = CollisionObjectType.BORDER;
  
      border.createFixture(borderFixture);
  
      // top
      borderDef.position.set((float) spaceModel.getXMax() / 2,
          (float) (spaceModel.getYMax() + 0.5));
      border = world.createBody(borderDef);
      border.createFixture(borderFixture);
  
      // left
      borderDef.position.set(-0.5f, (float) (spaceModel.getYMax() / 2));
      border = world.createBody(borderDef);
      borderShape.setAsBox(0.5f, (float) (spaceModel.getXMax() / 2));
      borderFixture.shape = borderShape;
      border.createFixture(borderFixture);
  
      // right
      borderDef.position.set((float) spaceModel.getXMax() + 0.5f,
          (float) (spaceModel.getYMax() / 2));
      border = world.createBody(borderDef);
      border.createFixture(borderFixture);
    }

    // add a body for every object
    for (Physical object : getPhysicals()) {
      addBody(object);
    }
  }


  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
    List<PhysAgtType> agentList = new ArrayList<PhysAgtType>();
    List<PhysicalObjType> objectList = new ArrayList<PhysicalObjType>();

    SimulationStep simStep = simulationStepEvent.getSimulationStep();
    EnvironmentSimulatorStep envStep = simStep.getEnvironmentSimulatorStep();
    if (envStep != null) {
      List<JAXBElement<? extends EnvSimInputEventType>> envSimInputEvents = envStep.getEnvSimInputEvent();
      for (JAXBElement<? extends EnvSimInputEventType> envSimInputEventType : envSimInputEvents) {
        if (envSimInputEventType.getValue() != null) {
          if (envSimInputEventType.getValue().getResultingStateChanges() != null) {
            List<ResultingStateChangesType> changeList = envSimInputEventType
                .getValue().getResultingStateChanges();

            for (ResultingStateChangesType change : changeList) {
              if (change.getPhysicalAgents() != null) {
                if (change.getPhysicalAgents().getPhysAgt() != null) {
                  agentList = change.getPhysicalAgents().getPhysAgt();
                }
              }

              if (change.getPhysicalObjects() != null) {
                if (change.getPhysicalObjects().getPhysObj() != null) {
                  objectList = change.getPhysicalObjects().getPhysObj();
                }
              }
            }
          }
        }
      }
    }
    
    updateEngineObjects(agentList, objectList);
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.physics2d.PhysicsSimulator#simulationStepStart()
   */
  @Override
  public void simulationStepStart(long stepNumber) {
    this.stepNumber = stepNumber;

    assignAcceleration();

    // perform one simulation step in the engine
    world.step((float) stepDuration, 10, 10);

    if (spaceModel.getGeometry().equals(Geometry.Euclidean)) {
      handleBorderContact();
    }

    if (autoKinematics) {
      updateAorObjects();
    }

    events.addAll(collisionEvents);
    collisionEvents.clear();
    processPerceptions();
    sendEvents(stepNumber);
  }

  /**
   * Creates perception events for all perceptions of the current step.
   */
  private void processPerceptions() {
    for (Perception2D perception : perceptions) {
      String type = perception.getPerceived().getClass().getSimpleName();
      
      PhysicalObjectPerceptionEvent event = new PhysicalObjectPerceptionEvent(
          stepNumber, perception.getPerceiver().getId(), type, perception.getDistance());
      event.setPerceivedPhysicalObject(perception.getPerceived());
      event.setPerceptionAngle(perception.getAngleInDegrees());

      try {
        Field idPerceivableField = perception.getPerceived()
            .getClass().getDeclaredField("ID_PERCEIVABLE");

        if (idPerceivableField.getBoolean(perception.getPerceived())) {
          event.setPerceivedPhysicalObjectIdRef(perception.getPerceived().getId());
        }
      } catch (Exception e) {
      }

      events.add(event);
      
      //System.out.println(stepNumber + ": " + perception);
    }
    
    perceptions.clear();
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
   * Adds a Box2D Body to the world which represents the specified object.
   * 
   * @param object
   */
  private void addBody(Physical object) {

    BodyDef def = new BodyDef();
    def.position.set((float) object.getPos().getX(), (float) object.getPos()
        .getY());
    def.angle = (float) (UtilFunctions.degreeToRadian(object.getRotZ()));
    def.userData = object;

    Body body = world.createBody(def);

    FixtureDef fixture = new FixtureDef();
    fixture.userData = CollisionObjectType.OBJECT;
    fixture.restitution = (float) MaterialConstants.restitution(object
        .getMaterialType());
    fixture.friction = (float) MaterialConstants.friction(object
        .getMaterialType());
    
    Shape2D shapeType = object.getShape2D();

    if (shapeType == null) {
      System.err.println("Please specify a shape2D for every object!");
    }

    switch (shapeType) {

    case rectangle:
      PolygonShape rectangleShape = new PolygonShape();
      rectangleShape.setAsBox((float) object.getWidth() / 2,
          (float) object.getHeight() / 2);

      fixture.shape = rectangleShape;

      if (object.getPhysicsType().equals(PhysicsType.INFINITE_MASS)) {
        body.setType(BodyType.STATIC);
        fixture.density = 0;
      } else {
        body.setType(BodyType.DYNAMIC);
        fixture.density = (float) (object.getM() / (object.getWidth() * object
            .getHeight()));
      }

      break;

    case polygon:
      // polygon
      PolygonShape polygonShape = new PolygonShape();
      Vec2[] points = pointsStringToList(object.getPoints());
      polygonShape.set(points, points.length);
      
      fixture.shape = polygonShape;

      if (object.getPhysicsType().equals(PhysicsType.INFINITE_MASS)) {
        body.setType(BodyType.STATIC);
        fixture.density = 0;
      } else {
        body.setType(BodyType.DYNAMIC);
        fixture.density = (float) (object.getM() / getPolygonArea(points));
      }

      break;

    // default shape is circle
    default:
      CircleShape circleShape = new CircleShape();
      circleShape.m_radius = (float) (object.getWidth() / 2);

      fixture.shape = circleShape;

      if (object.getPhysicsType().equals(PhysicsType.INFINITE_MASS)) {
        body.setType(BodyType.STATIC);
        fixture.density = 0;
      } else {
        body.setType(BodyType.DYNAMIC);
        fixture.density = (float) (object.getM() / (Math.PI * Math.pow(
            object.getWidth() / 2, 2)));
      }

    }

    body.createFixture(fixture);


    if (autoKinematics) {
      double vx = unitConverter.velocityToUser(object.getV().getX())
        * timeRatio;
      double vy = unitConverter.velocityToUser(object.getV().getY())
        * timeRatio;

      body.setLinearVelocity(new Vec2((float) vx, (float) vy));
      body.setAngularVelocity((float) (unitConverter
          .angularVelocityToUser(UtilFunctions.degreeToRadian(object.getOmegaZ())) * timeRatio));
    }

    // if agent with autoPerception, add another shape for perception
    if (object instanceof PhysicalAgentObject
        && ((PhysicalAgentObject) object).getPerceptionRadius() > 0) {

      try {
        Field autoPerceptionField = object.getClass().getDeclaredField(
            "AUTO_PERCEPTION");

        if (autoPerceptionField.getBoolean(object)) {

          CircleShape shape = new CircleShape();
          shape.m_radius = (float) ((PhysicalAgentObject) object)
          .getPerceptionRadius();
          
          fixture.shape = shape;
          fixture.userData = CollisionObjectType.PERCEPTION;
          fixture.density = 0;

          body.createFixture(fixture);
        }
      } catch (Exception e) {
      }
    }

    bodyMap.put(object.getId(), body);
  }

  /**
   * Removes the Box2D Body corresponding to the given object from the world.
   * 
   * @param object
   */
  private void removeBody(Physical object) {
    Body body = bodyMap.remove(object.getId());
    world.destroyBody(body);
  }

  /**
   * Update the PhysicalObjects and PhysicalAgentObjects with the data from the
   * engine.
   */
  private void updateAorObjects() {
    Body body = world.getBodyList();

    while (body != null) {
      if (body.getUserData() instanceof Physical) {

        // in toroidal space: update position if body is out of bounds
        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          if (body.getPosition().x < 0) {
            Vec2 position = body.getTransform().position;
            position.x += spaceModel.getXMax();
            body.setTransform(position, body.getAngle());
          }

          if (body.getPosition().x >= spaceModel.getXMax()) {
            Vec2 position = body.getTransform().position;
            position.x %= spaceModel.getXMax();
            body.setTransform(position, body.getAngle());
          }

          if (body.getPosition().y < 0) {
            Vec2 position = body.getTransform().position;
            position.y += spaceModel.getYMax();
            body.setTransform(position, body.getAngle());
          }

          if (body.getPosition().y >= spaceModel.getYMax()) {
            Vec2 position = body.getTransform().position;
            position.y %= spaceModel.getYMax();
            body.setTransform(position, body.getAngle());
          }
        }

        // synchronize
        Physical object = (Physical) body.getUserData();

        // position
        object.setX(body.getPosition().x);
        object.setY(body.getPosition().y);

        // orientation
        object.setRotZ(UtilFunctions.radianToDegree(body.getAngle()));

        // velocity
        object.setVx(unitConverter.velocityToMetersPerSeconds(body
            .getLinearVelocity().x) / timeRatio);
        object.setVy(unitConverter.velocityToMetersPerSeconds(body
            .getLinearVelocity().y) / timeRatio);

        object.setOmegaZ(unitConverter.angularVelocityToRadiansPerSeconds(UtilFunctions.radianToDegree(body
            .getAngularVelocity())) / timeRatio);

        // debug output
        // System.out.print(object.getId() + ") ");
        // System.out.print("Pos:" + object.getPos() + " ");
        // System.out.print("Rot:" + object.getRot() + " ");
        // System.out.print("V:" + object.getV() + " ");
        // System.out.print("A:" + object.getA() + " ");
        // System.out.print("Omega:" + object.getOmega() + " ");
        // System.out.print("Alpha:" + object.getAlpha() + " ");
        // System.out.println();

//         System.out.print(object.getId() + ") ");
//         System.out.print("x:" + body.getPosition().x + " y:"
//         + body.getPosition().y + " ");
//         System.out.print("vx:" + body.getLinearVelocity().x + " vy:"
//         + body.getLinearVelocity().y + " ");
//         System.out.print("omega:" + body.getAngularVelocity());
//         System.out.println();
      }

      body = body.getNext();
    }
  }

  /**
   * Generates forces that result in an acceleration for those objects, that 
   * have linear or angular acceleration set.
   */
  private void assignAcceleration() {
    Body body = world.getBodyList();

    while (body != null) {
      if (body.getUserData() instanceof Physical) {
        Physical object = (Physical) body.getUserData();

        // linear
        if (object.getAx() != 0 || object.getAy() != 0) {
          // F = m * a
          double fx = unitConverter.accelerationToUser(object.getAx())
              * timeRatioSquared * object.getM();
          double fy = unitConverter.accelerationToUser(object.getAy())
              * timeRatioSquared * object.getM();
          Vec2 force = new Vec2((float) fx, (float) fy);
          body.applyForce(force, body.getWorldCenter());
        }

        // angular
        if (object.getAlphaZ() != 0) {
          // torque = inertia * alpha
          double alpha = unitConverter.angularAccelerationToUser(UtilFunctions.degreeToRadian(object
              .getAlphaZ())) * timeRatioSquared;
          body.applyTorque((float) (body.getInertia() * alpha));
        }
      }

      body = body.getNext();
    }
    
  }
  
  
  /**
   * Updates the engine objects whose corresponding AOR-Objects have changed
   * some properties.
   * 
   * @param agentList
   * @param objectList
   */
  private void updateEngineObjects(List<PhysAgtType> agentList,
      List<PhysicalObjType> objectList) {
    // update agents
    for (PhysAgtType agent : agentList) {
      Body body = bodyMap.get(agent.getId());
      
      // position, orientation
      if (agent.getX() != null || agent.getY() != null || 
          agent.getRotationAngleZ() != null) {
        Transform t = body.getTransform();
        Vec2 pos = t.position;
        float angle = t.getAngle();
        
        
        if (agent.getX() != null) {
          pos.x = agent.getX().floatValue();
        }

        if (agent.getY() != null) {
          pos.y = agent.getY().floatValue();
        }

        if (agent.getRotationAngleZ() != null) {
          angle = (float) UtilFunctions.degreeToRadian(agent.getRotationAngleZ());
        }
        
        body.setTransform(pos, angle);
      }
            
      // velocity
      if (autoKinematics) {
        if (agent.getVx() != null) {
          float vx = (float) (unitConverter.velocityToUser(agent.getVx())
              * timeRatio);
          float vy = body.getLinearVelocity().y;
          body.setLinearVelocity(new Vec2(vx, vy));
        }
        
        if (agent.getVy() != null) {
          float vx = body.getLinearVelocity().x;
          float vy = (float) (unitConverter.velocityToUser(agent.getVy())
              * timeRatio);
          body.setLinearVelocity(new Vec2(vx, vy));
        }
  
        if (agent.getOmegaZ() != null) {
          body.setAngularVelocity((float) (unitConverter
              .angularVelocityToUser(UtilFunctions.degreeToRadian(agent.getOmegaZ())) * timeRatio));        
        }
      }
    }
  
    // update objects
    for (PhysicalObjType object : objectList) {
      Body body = bodyMap.get(object.getId());
      
      // position, orientation
      if (object.getX() != null || object.getY() != null || 
          object.getRotationAngleZ() != null) {
        Transform t = body.getTransform();
        Vec2 pos = t.position;
        float angle = t.getAngle();
        
        
        if (object.getX() != null) {
          pos.x = object.getX().floatValue();
        }

        if (object.getY() != null) {
          pos.y = object.getY().floatValue();
        }

        if (object.getRotationAngleZ() != null) {
          angle = (float) UtilFunctions.degreeToRadian(object.getRotationAngleZ());
        }
        
        body.setTransform(pos, angle);
      }
            
      // polygon vertices
      if (object.getPoints() != null) {
        Vec2[] points = pointsStringToList(object.getPoints());
        
        Fixture f = body.getFixtureList();
        while (f != null) {
          if (f.getShape().m_type.equals(ShapeType.POLYGON)) {
            ((PolygonShape)f.getShape()).set(points, points.length);
            break;
          }
          
          f = f.getNext();
        }
      }

      // velocity
      if (autoKinematics) {
        if (object.getVx() != null) {
          float vx = (float) (unitConverter.velocityToUser(object.getVx())
              * timeRatio);
          float vy = body.getLinearVelocity().y;
          body.setLinearVelocity(new Vec2(vx, vy));
        }
        
        if (object.getVy() != null) {
          float vx = body.getLinearVelocity().x;
          float vy = (float) (unitConverter.velocityToUser(object.getVy())
              * timeRatio);
          body.setLinearVelocity(new Vec2(vx, vy));
        }
  
        if (object.getOmegaZ() != null) {
          body.setAngularVelocity((float) (unitConverter
              .angularVelocityToUser(UtilFunctions.degreeToRadian(object.getOmegaZ())) * timeRatio));        
        }
      }
    }
  }  
  
  /**
   * Checks if the perception angle lies inside the agents perception scope.
   * 
   * @param agent
   * @param angle
   * @return true if angle inside scope, otherwise false
   */
  private boolean checkPerceptionAngle(PhysicalAgentObject agent, double angle) {
    if (agent.getPerceptionAngle() == 360) {
      return true;
    }
    
    double orientation = UtilFunctions.degreeToRadian(agent.getRotZ());
    double perceptionDirection = (Math.atan2(agent.getPerceptionDirection().getY(), agent.getPerceptionDirection().getX()) + orientation) % (2 * Math.PI);
    double pHalfAngle = UtilFunctions.degreeToRadian(agent.getPerceptionAngle()) / 2;
    double pStart = perceptionDirection - pHalfAngle;
    double pEnd = (perceptionDirection + pHalfAngle) % (2 * Math.PI);
    
    if (pStart < 0) {
      pStart += 2 * Math.PI;
    }
    
    // special case if perception scope lies around 0
    if (pStart > pEnd) {
      return  (pStart <= angle || angle <= pEnd);
    }
    
    return (pStart <= angle && angle <= pEnd);
  }

  /**
   * For objects that have reached the space border, replace the dynamic bodies
   * with static bodies in Box2D (only used in euclidean space).
   */
  private void handleBorderContact() {
    for (Body body : borderContact) {
      body.setLinearVelocity(new Vec2(0, 0));
      body.setAngularVelocity(0);
      body.setType(BodyType.STATIC);
      
      Physical object = (Physical) body.getUserData();
      object.setVx(0);
      object.setVy(0);
      object.setAx(0);
      object.setAy(0);
      object.setOmegaZ(0);
      object.setAlphaZ(0);
    }

    borderContact.clear();
  }

  /**
   * Converts the points attribute string from the Physical interface into an
   * array of Vec2. 
   * 
   * @param points
   * @return an array with all points as Vec2
   */
  private Vec2[] pointsStringToList(String points) {
    ArrayList<Vec2> list = new ArrayList<Vec2>();
    String[] pointsStr = points.split(" ");
    
    for (int i = 0; i < pointsStr.length; i++) {
      if (pointsStr[i].equals("")) {
        continue;
      }
      
      String[] tmp = pointsStr[i].split(",");
      list.add(new Vec2(Float.valueOf(tmp[0]), Float.valueOf(tmp[1])));
    }

    return list.toArray(new Vec2[list.size()]);
  }
  
  
  /**
   * Calculates the area of a polygon. The vertices are specified in the points array.
   * 
   * @param points
   * @return area
   */
  private double getPolygonArea(Vec2[] points) {
    double area = 0;
    int size = points.length;
    for (int i = 0; i < points.length; i++) {
      area += (points[i].x + points[(i+1) % size].x) * (points[(i+1) % size].y - points[i].y);
    }
    
    return Math.abs(area) / 2;
  }
 

  /**
   * The collision listener for Box2D. Used to create collision and perception
   * events and to determine when an object reaches the space border.
   * 
   * @author Holger Wuerke
   * 
   */
  private class CollisionListener implements ContactListener {

    public CollisionListener() {
    }

    @Override
    public void beginContact(Contact contact) {            
    }

    /**
     * Creates a perception event.
     * 
     * @param perceiver
     * @param perceived
     * @param point
     */
    private void processPerception(Body perceiver, Body perceived, Vec2 point) {
      // if perceived object is phantom, no perception
      if (((Physical) perceived.getUserData()).getPhysicsType().equals(PhysicsType.PHANTOM)) {
        return;
      }
      
      // update agent orientation
      PhysicalAgentObject agent = (PhysicalAgentObject) perceiver.getUserData();
      agent.setRotZ(UtilFunctions.radianToDegree(perceiver.getAngle()));

      double distance = Math.sqrt(Math.pow((point.x - perceiver.getPosition().x), 2)
          + Math.pow((point.y - perceiver.getPosition().y), 2));

      double globalAngle = Math.atan2(point.y - perceiver.getPosition().y, point.x - perceiver.getPosition().x);
      
      if (globalAngle < 0) {
        globalAngle += 2 * Math.PI;
      }
      
      if (!checkPerceptionAngle(agent, globalAngle)) {
        return;
      }

      double orientation = UtilFunctions.degreeToRadian(agent.getRotZ());
      double perceptionDirection = (Math.atan2(agent.getPerceptionDirection().getY(), agent.getPerceptionDirection().getX()) + orientation) % (2 * Math.PI);
      double angle = globalAngle - perceptionDirection;
      
      if (angle > (agent.getPerceptionAngle() / 2)) {
        angle -= 2 * Math.PI;
      }
      
      if (angle < -(agent.getPerceptionAngle() / 2)) {
        angle += 2 * Math.PI;
      }
      
      Perception2D perception = new Perception2D((PhysicalAgentObject) perceiver.getUserData(), (Physical) perceived.getUserData(), spaceModel);
      perception.setDistance(distance);
      perception.setAngle(angle);
      perceptions.add(perception);
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
      CollisionObjectType cot1 = (CollisionObjectType) contact.getFixtureA().getUserData();
      CollisionObjectType cot2 = (CollisionObjectType) contact.getFixtureB().getUserData();

      // filter out collisions between two perception radiuses and between a perception radius and a space borders
      if ((cot1.equals(CollisionObjectType.PERCEPTION) && cot2.equals(CollisionObjectType.PERCEPTION)) ||
          (cot1.equals(CollisionObjectType.PERCEPTION) && cot2.equals(CollisionObjectType.BORDER)) ||
          (cot1.equals(CollisionObjectType.BORDER) && cot2.equals(CollisionObjectType.PERCEPTION))) {
        contact.setEnabled(false);
        return;
      }
      
      // collision
      if (cot1.equals(CollisionObjectType.OBJECT) && cot2.equals(CollisionObjectType.OBJECT)) {
        Physical o1 = (Physical) contact.getFixtureA().getBody().getUserData();
        Physical o2 = (Physical) contact.getFixtureB().getBody().getUserData();
        
        // immaterial and phantom objects do not collide
        if (o1.getPhysicsType().equals(PhysicsType.IMMATERIAL) || 
            o2.getPhysicsType().equals(PhysicsType.IMMATERIAL) ||
            o1.getPhysicsType().equals(PhysicsType.PHANTOM) ||
            o2.getPhysicsType().equals(PhysicsType.PHANTOM)) {
          contact.setEnabled(false);
          return;
        }
        
        // add collision event
        if (autoCollisionDetection) {
          CollisionEvent event = new CollisionEvent(stepNumber);
          event.setPhysicalObject1(o1);
          event.setPhysicalObject2(o2);
          //System.out.println("COLLISION: " + o1+ " " + o2);
          collisionEvents.add(event);
        }
        
        // disable if collision handling turned off
        if (!autoCollisionHandling) {
          contact.setEnabled(false);
        }
      }
     
      // perception
      if (cot1.equals(CollisionObjectType.PERCEPTION) && cot2.equals(CollisionObjectType.OBJECT)) {
        WorldManifold wm = new WorldManifold();
        contact.getWorldManifold(wm);
        processPerception(contact.getFixtureA().getBody(), contact.getFixtureB().getBody(), wm.points[0]);
        contact.setEnabled(false);
        return;        
      }

      if (cot1.equals(CollisionObjectType.OBJECT) && cot2.equals(CollisionObjectType.PERCEPTION)) {
        WorldManifold wm = new WorldManifold();
        contact.getWorldManifold(wm);
        processPerception(contact.getFixtureB().getBody(), contact.getFixtureA().getBody(), wm.points[0]);
        contact.setEnabled(false);
        return;
      }

      // space border reached
      if (cot1.equals(CollisionObjectType.OBJECT) && (cot2.equals(CollisionObjectType.BORDER))) {
        borderContact.add(contact.getFixtureA().getBody());
        contact.setEnabled(false);
        return;
      }

      if (cot1.equals(CollisionObjectType.BORDER) && cot2.equals(CollisionObjectType.OBJECT)) {
        borderContact.add(contact.getFixtureB().getBody());
        contact.setEnabled(false);
        return;
      }
      
    }

  }

}
