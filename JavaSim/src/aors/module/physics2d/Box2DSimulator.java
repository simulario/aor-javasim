/**
 * 
 */
package aors.module.physics2d;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.MassData;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

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
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.model.envsim.Shape2D;
import aors.module.physics2d.util.CollisionObjectType;
import aors.module.physics2d.util.MaterialConstants;
import aors.module.physics2d.util.UnitConverter;

/**
 * A physics simulator for 2D simulation using the Box2D physics engine.
 * 
 * @author Holger Wuerke
 * @since 01.12.2009
 * 
 */
public class Box2DSimulator extends PhysicsSimulator {

  /**
   * A unit converter.
   */
  private UnitConverter unitConverter;

  /**
   * The ratio between the provided step duration value and the value used in
   * Box2D. The step duration value for Box2D should be somewhere between 0.01
   * and 0.1
   */
  private int stepDurationFactor;

  /**
   * The Box2D world, where everything is simulated.
   */
  private World world;

  /**
   * A set that contains all Box2D bodies that have reached the space border
   * (used only in euclidean space).
   */
  private Set<Body> borderReached = new HashSet<Body>();

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

    unitConverter = new UnitConverter(simParams.getTimeUnit(), spaceModel
        .getSpatialDistanceUnit());

    // calculate step duration, the value for Box2D should be between 0.01 and
    // 0.1
    stepDuration = simParams.getStepDuration();
    // if (simParams.getTimeUnit().equals("ms")) {
    // stepDuration /= 1000;
    // }

    int x = (int) Math.log10(stepDuration);
    stepDurationFactor = (int) Math.pow(10, x + 2);
    stepDuration /= stepDurationFactor;

    // System.out.println(stepDuration);

    AABB aabb = new AABB(new Vec2(-100, -100), new Vec2((float) spaceModel
        .getXMax() + 100, spaceModel.getYMax() + 100));

    // Vec2 gravity = new Vec2(0f, (float) gravitation);
    Vec2 gravity = new Vec2(0f, (float) unitConverter
        .accelerationToUser(gravitation)
        * stepDurationFactor * stepDurationFactor);
    world = new World(aabb, gravity, true);
    //System.out.println(gravity);

    CollisionListener cl = new CollisionListener();
    world.setContactListener(cl);

    // add world borders
    // bottom
    BodyDef borderDef = new BodyDef();
    borderDef.position.set((float) (spaceModel.getXMax() / 2), -0.5f);

    PolygonDef borderShape = new PolygonDef();
    borderShape.friction = 0.5f;
    borderShape.restitution = 1;
    borderShape.isSensor = true;
    borderShape.userData = CollisionObjectType.BORDER;
    borderShape.setAsBox((float) (spaceModel.getXMax() / 2), 0.5f);

    Body border = world.createBody(borderDef);
    border.createShape(borderShape);

    // top
    borderDef.position.set((float) spaceModel.getXMax() / 2,
        (float) (spaceModel.getYMax() + 0.5));
    border = world.createBody(borderDef);
    border.createShape(borderShape);

    // left
    borderDef.position.set(-0.5f, (float) (spaceModel.getYMax() / 2));
    borderShape.setAsBox(0.5f, (float) (spaceModel.getXMax() / 2));
    border = world.createBody(borderDef);
    border.createShape(borderShape);

    // right
    borderDef.position.set((float) spaceModel.getXMax() + 0.5f,
        (float) (spaceModel.getYMax() / 2));
    border = world.createBody(borderDef);
    border.createShape(borderShape);

    // add a body for every object
    for (Physical object : getPhysicals()) {
      addBody(object);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.physics2d.PhysicsSimulator#simulationStarted()
   */
  @Override
  public void simulationStarted() {
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
    // TODO: if stepDuration is too big, perform multiple small steps in the
    // engine

    // start = System.nanoTime();
    world.step((float) stepDuration, 10);
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
   * Adds a Box2D Body to the world which represents the given object.
   * 
   * @param object
   */
  private void addBody(Physical object) {
    BodyDef def = new BodyDef();
    def.position.set((float) object.getPos().getX(), (float) object.getPos()
        .getY());
    def.angle = (float) (object.getRot().getLength() * Math.PI / 180);
    def.userData = object;

    Body body = world.createBody(def);

    Shape2D shapeType = object.getShape2D();

    if (shapeType != null) {
      switch (shapeType) {
      case circle:
        CircleDef circleShape = new CircleDef();
        circleShape.isSensor = !(autoCollisionDetection && autoCollisionHandling);
        circleShape.userData = CollisionObjectType.OBJECT;
        circleShape.radius = (float) (object.getWidth() / 2);
        circleShape.density = (float) (object.getM() / (Math.PI * Math.pow(
            object.getWidth() / 2, 2)));
        if (circleShape.density == 0) {
          circleShape.density = 1;
        }
        circleShape.restitution = (float) MaterialConstants.restitution(object
            .getMaterialType());
        circleShape.friction = (float) MaterialConstants.friction(object
            .getMaterialType());
        body.createShape(circleShape);
        break;

      case rectangle:
        PolygonDef rectangleShape = new PolygonDef();
        rectangleShape.isSensor = !(autoCollisionDetection && autoCollisionHandling);
        rectangleShape.userData = CollisionObjectType.OBJECT;
        rectangleShape.setAsBox((float) object.getWidth() / 2, (float) object
            .getHeight() / 2);
        rectangleShape.density = (float) (object.getM() / (object.getWidth() * object
            .getHeight()));
        if (rectangleShape.density == 0) {
          rectangleShape.density = 1;
        }
        rectangleShape.restitution = (float) MaterialConstants
            .restitution(object.getMaterialType());
        rectangleShape.friction = (float) MaterialConstants.friction(object
            .getMaterialType());
        body.createShape(rectangleShape);
        break;

      default:
        // polygon
        PolygonDef shape = new PolygonDef();
        shape.isSensor = !(autoCollisionDetection && autoCollisionHandling);
        shape.userData = CollisionObjectType.OBJECT;

        List<Double> points = getPointsList(object.getPoints());
        int max = points.size();
        double area = 0;
        for (int i = 0; i < max; i += 2) {
          shape.addVertex(new Vec2(points.get(i).floatValue(), points
              .get(i + 1).floatValue()));
          area += (points.get(i) + points.get((i + 2) % max))
              * (points.get((i + 3) % max) - points.get(i + 1));
        }

        shape.density = (float) (object.getM() / Math.abs(area / 2));
        if (shape.density == 0) {
          shape.density = 1;
        }

        shape.restitution = (float) MaterialConstants.restitution(object
            .getMaterialType());
        shape.friction = (float) MaterialConstants.friction(object
            .getMaterialType());
        body.createShape(shape);
        break;
      }
    } else {
      // if no shape specified use rectangle
      PolygonDef rectangleShape = new PolygonDef();
      rectangleShape.isSensor = !(autoCollisionDetection && autoCollisionHandling);
      rectangleShape.userData = CollisionObjectType.OBJECT;
      rectangleShape.setAsBox((float) object.getWidth() / 2, (float) object
          .getHeight() / 2);
      rectangleShape.density = (float) (object.getM() / (object.getWidth() * object
          .getHeight()));
      if (rectangleShape.density == 0) {
        rectangleShape.density = 1;
      }
      rectangleShape.restitution = (float) MaterialConstants.restitution(object
          .getMaterialType());
      rectangleShape.friction = (float) MaterialConstants.friction(object
          .getMaterialType());
      body.createShape(rectangleShape);
    }

    if (autoKinematics) {
      // Vec2 v = new Vec2((float) (object.getV().getX()), (float)
      // (object.getV()
      // .getY()));
      Vec2 v = new Vec2((float) unitConverter.velocityToUser(object.getV()
          .getX())
          * stepDurationFactor, (float) unitConverter.velocityToUser(object
          .getV().getY())
          * stepDurationFactor);
      body.setLinearVelocity(v);
      body.setAngularVelocity((float) object.getOmega().getLength());
    }

    // if agent with autoPerception, add another shape for perception
    if (object instanceof PhysicalAgentObject
        && ((PhysicalAgentObject) object).getPerceptionRadius() > 0) {

      try {
        Field autoPerceptionField = object.getClass().getDeclaredField(
            "AUTO_PERCEPTION");

        if (autoPerceptionField.getBoolean(object)) {

          CircleDef shape = new CircleDef();
          shape.userData = CollisionObjectType.PERCEPTION;
          shape.density = 0;
          shape.radius = (float) ((PhysicalAgentObject) object)
              .getPerceptionRadius();
          shape.isSensor = true;
          body.createShape(shape);
        }
      } catch (Exception e) {
      }
    }

    body.setMassFromShapes();
  }

  /**
   * Removes the Box2D Body corresponding to the given object from the world.
   * 
   * @param object
   */
  private void removeBody(Physical object) {
    Body body = world.getBodyList();

    while (body != null) {
      if (body.getUserData() instanceof Physical) {
        if (body.getUserData().equals(object)) {
          world.destroyBody(body);
          return;
        }
      }
    }

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
            Vec2 position = body.getXForm().position;
            position.x += spaceModel.getXMax();
            body.setXForm(position, body.getAngle());
          }

          if (body.getPosition().x >= spaceModel.getXMax()) {
            Vec2 position = body.getXForm().position;
            position.x %= spaceModel.getXMax();
            body.setXForm(position, body.getAngle());
          }

          if (body.getPosition().y < 0) {
            Vec2 position = body.getXForm().position;
            position.y += spaceModel.getYMax();
            body.setXForm(position, body.getAngle());
          }

          if (body.getPosition().y >= spaceModel.getYMax()) {
            Vec2 position = body.getXForm().position;
            position.y %= spaceModel.getYMax();
            body.setXForm(position, body.getAngle());
          }
        }

        // synchronize
        Physical object = (Physical) body.getUserData();

        // position
        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          object.setX(body.getPosition().x % spaceModel.getXMax());
          object.setY(body.getPosition().y % spaceModel.getYMax());
        } else {
          object.setX(body.getPosition().x);
          object.setY(body.getPosition().y);
        }

        // orientation
        object.setRotZ(body.getAngle() * 180 / Math.PI);

        // velocity
        // object.setVx(body.getLinearVelocity().x);
        // object.setVy(body.getLinearVelocity().y);
        object.setVx(unitConverter.velocityToMetersPerSeconds(body
            .getLinearVelocity().x)
            / stepDurationFactor);
        object.setVy(unitConverter.velocityToMetersPerSeconds(body
            .getLinearVelocity().y)
            / stepDurationFactor);

        object.setOmegaZ(unitConverter.angularVelocityToRadiansPerSeconds(body.getAngularVelocity()) / stepDurationFactor);

        // debug output
        // System.out.print(object.getId() + ") ");
        // System.out.print("Pos:" + object.getPos() + " ");
        // System.out.print("Rot:" + object.getRot() + " ");
        // System.out.print("V:" + object.getV() + " ");
        // System.out.print("A:" + object.getA() + " ");
        // System.out.print("Omega:" + object.getOmega() + " ");
        // System.out.print("Alpha:" + object.getAlpha() + " ");
        // System.out.println();

        // System.out.print(object.getId() + ") ");
        // System.out.print("x:" + body.getPosition().x + " y:"
        // + body.getPosition().y + " ");
        // System.out.print("vx:" + body.getLinearVelocity().x + " vy:"
        // + body.getLinearVelocity().y + " ");
        // System.out.print("omega:" + body.getAngularVelocity());
        // System.out.println();
      }

      body = body.getNext();
    }
  }

  /**
   * Update the engine objects with the data from the PhysicalAgentObjects.
   */
  private void updateEngineObjects() {
    Body body = world.getBodyList();

    while (body != null) {
      if (body.getUserData() instanceof Physical) {
        Physical object = (Physical) body.getUserData();

        // update object properties
        body.setXForm(new Vec2((float) object.getX(), (float) object.getY()),
            (float) (object.getRotZ() * Math.PI / 180));

        // Vec2 v = new Vec2((float) object.getV().getX(), (float) object.getV()
        // .getY());
        double vx = unitConverter.velocityToUser(object.getV().getX())
            * stepDurationFactor;
        double vy = unitConverter.velocityToUser(object.getV().getY())
            * stepDurationFactor;
        
        body.setLinearVelocity(new Vec2((float) vx, (float) vy));
        body.setAngularVelocity((float) unitConverter
            .angularVelocityToUser(object.getOmegaZ())
            * stepDurationFactor);

        // acceleration
        if (object.getAx() != 0 || object.getAy() != 0) {
          // F = m * a
          double fx = unitConverter.accelerationToUser(object.getAx())
              * stepDurationFactor * stepDurationFactor * object.getM();
          double fy = unitConverter.accelerationToUser(object.getAy())
              * stepDurationFactor * stepDurationFactor * object.getM();
          Vec2 force = new Vec2((float) fx, (float) fy);
          body.applyForce(force, body.getWorldCenter());
        }

        if (object.getAlphaZ() != 0) {
          // torque = inertia * alpha
          double alpha = unitConverter.angularAccelerationToUser(object
              .getAlphaZ())
              * stepDurationFactor * stepDurationFactor;
          body.applyTorque((float) (body.getInertia() * alpha));
        }
      }

      body = body.getNext();
    }
  }

  /**
   * For objects that have reached the space border, replace the dynamic bodies
   * with static bodies in Box2D (only used in euclidean space).
   */
  private void handleBorderContact() {
    for (Body body : borderReached) {
      body.setMass(new MassData());
    }

    borderReached.clear();
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
    public void add(ContactPoint arg0) {
      // System.out.println(stepNumber + ": ADD " +
      // arg0.shape1.getBody().getUserData() + " " +
      // arg0.shape2.getBody().getUserData() + " " + arg0.position);

      // perception
      if ((arg0.shape1.getUserData().equals(CollisionObjectType.PERCEPTION) && arg0.shape2
          .getUserData().equals(CollisionObjectType.OBJECT))
          || (arg0.shape1.getUserData().equals(CollisionObjectType.OBJECT) && arg0.shape2
              .getUserData().equals(CollisionObjectType.PERCEPTION))) {

        Shape perceiverShape, perceivedShape;
        if (arg0.shape1.getUserData().equals(CollisionObjectType.PERCEPTION)) {
          perceiverShape = arg0.shape1;
          perceivedShape = arg0.shape2;
        } else {
          perceiverShape = arg0.shape2;
          perceivedShape = arg0.shape1;
        }

        PhysicalAgentObject perceiver = (PhysicalAgentObject) perceiverShape
            .getBody().getUserData();
        Physical perceived = (Physical) perceivedShape.getBody().getUserData();

        String type = perceived.getClass().getSimpleName();

        // TODO: calculate distance for toroidal space
        double distance = Math.sqrt(Math.pow((arg0.position.x - perceiverShape
            .getBody().getPosition().x), 2)
            + Math.pow((arg0.position.y - perceiverShape.getBody()
                .getPosition().y), 2));

        // perception angle
        double orientation = perceiverShape.getBody().getAngle();
        double angle = Math.atan2(arg0.position.x, arg0.position.y)
            % (2 * Math.PI);
        angle = (angle < orientation) ? (2 * Math.PI - (orientation - angle))
            : angle - orientation;

        PhysicalObjectPerceptionEvent event = new PhysicalObjectPerceptionEvent(
            stepNumber, perceiver.getId(), type, distance);
        event.setPerceivedPhysicalObject((Physical) perceivedShape.getBody()
            .getUserData());
        event.setPerceptionAngle(angle);

        try {
          Field idPerceivableField = perceiverShape.getBody().getUserData()
              .getClass().getDeclaredField("ID_PERCEIVABLE");

          if (idPerceivableField.getBoolean(perceiverShape.getBody()
              .getUserData())) {
            event.setPerceivedPhysicalObjectIdRef(perceived.getId());
          }
        } catch (Exception e) {
        }

        events.add(event);

        // System.out.println(stepNumber + ") Perception: " + perceiver + " "
        // + perceived);
        // System.out.println("Dist: " + distance + " Angle: " + angle * 180
        // / Math.PI);
      }

      // collision
      if (autoCollisionDetection
          && arg0.shape1.getUserData().equals(CollisionObjectType.OBJECT)
          && arg0.shape2.getUserData().equals(CollisionObjectType.OBJECT)) {

        CollisionEvent event = new CollisionEvent(stepNumber);
        event
            .setPhysicalObject1((Physical) arg0.shape1.getBody().getUserData());
        event
            .setPhysicalObject2((Physical) arg0.shape2.getBody().getUserData());

        events.add(event);
      }

      // space border reached
      if ((arg0.shape1.getUserData().equals(CollisionObjectType.BORDER) && arg0.shape2
          .getUserData().equals(CollisionObjectType.OBJECT))
          || (arg0.shape1.getUserData().equals(CollisionObjectType.OBJECT) && arg0.shape2
              .getUserData().equals(CollisionObjectType.BORDER))) {

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {

          // // Toroidal: add another shape on the opposite side of the space
          // Shape objectShape;
          // Shape borderShape;
          //
          // if (arg0.shape1.getUserData().equals(ObjectType.OBJECT)) {
          // objectShape = arg0.shape1;
          // borderShape = arg0.shape2;
          // } else {
          // objectShape = arg0.shape2;
          // borderShape = arg0.shape1;
          // }
          //
          // shapesToAdd.add(new Pair<Shape, BorderType>(objectShape,
          // (BorderType) borderShape.getBody().getUserData()));

        } else {
          // Euclidean: set v and a to 0

          Body objBody = (arg0.shape1.getUserData()
              .equals(CollisionObjectType.OBJECT)) ? arg0.shape1.getBody()
              : arg0.shape2.getBody();

          Physical object = (Physical) objBody.getUserData();
          objBody.setLinearVelocity(new Vec2(0, 0));
          objBody.setAngularVelocity(0);
          borderReached.add(objBody);

          object.setVx(0);
          object.setVy(0);
          object.setAx(0);
          object.setAy(0);
        }
      }
    }

    @Override
    public void persist(ContactPoint arg0) {
      // System.out.println(stepNumber + ": PER "
      // + arg0.shape1.getBody().getUserData() + " "
      // + arg0.shape2.getBody().getUserData() + " " + arg0.position);

      // perception
      if ((arg0.shape1.getUserData().equals(CollisionObjectType.PERCEPTION) && arg0.shape2
          .getUserData().equals(CollisionObjectType.OBJECT))
          || (arg0.shape1.getUserData().equals(CollisionObjectType.OBJECT) && arg0.shape2
              .getUserData().equals(CollisionObjectType.PERCEPTION))) {

        Shape perceiverShape, perceivedShape;
        if (arg0.shape1.getUserData().equals(CollisionObjectType.PERCEPTION)) {
          perceiverShape = arg0.shape1;
          perceivedShape = arg0.shape2;
        } else {
          perceiverShape = arg0.shape2;
          perceivedShape = arg0.shape1;
        }

        PhysicalAgentObject perceiver = (PhysicalAgentObject) perceiverShape
            .getBody().getUserData();
        Physical perceived = (Physical) perceivedShape.getBody().getUserData();

        String type = perceived.getClass().getSimpleName();

        // TODO: calculate distance for toroidal space
        double distance = Math.sqrt(Math.pow((arg0.position.x - perceiverShape
            .getBody().getPosition().x), 2)
            + Math.pow((arg0.position.y - perceiverShape.getBody()
                .getPosition().y), 2));

        // perception angle
        double orientation = perceiverShape.getBody().getAngle();
        double angle = Math.atan2(arg0.position.x, arg0.position.y)
            % (2 * Math.PI);
        angle = (angle < orientation) ? (2 * Math.PI - (orientation - angle))
            : angle - orientation;

        PhysicalObjectPerceptionEvent event = new PhysicalObjectPerceptionEvent(
            stepNumber, perceiver.getId(), type, distance);
        event.setPerceivedPhysicalObject((Physical) perceivedShape.getBody()
            .getUserData());
        event.setPerceptionAngle(angle);

        try {
          Field idPerceivableField = perceiverShape.getBody().getUserData()
              .getClass().getDeclaredField("ID_PERCEIVABLE");

          if (idPerceivableField.getBoolean(perceiverShape.getBody()
              .getUserData())) {
            event.setPerceivedPhysicalObjectIdRef(perceived.getId());
          }
        } catch (Exception e) {
        }

        events.add(event);

        // System.out.println(stepNumber + ") Perception: " + perceiver + " "
        // + perceived);
        // System.out.println("Dist: " + distance + " Angle: " + angle * 180
        // / Math.PI);
      }
    }

    @Override
    public void remove(ContactPoint arg0) {
      // System.out.println(stepNumber + ": REM "
      // + arg0.shape1.getBody().getUserData() + " "
      // + arg0.shape2.getBody().getUserData() + " " + arg0.position);

      // if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
      // // Toroidal: remove all shapes from the body that are not inside the
      // // space borders
      //
      // if ((arg0.shape1.getUserData().equals(ObjectType.BORDER) && arg0.shape2
      // .getUserData().equals(ObjectType.OBJECT))
      // || (arg0.shape1.getUserData().equals(ObjectType.OBJECT) && arg0.shape2
      // .getUserData().equals(ObjectType.BORDER))) {
      //
      // Shape objectShape;
      // Shape borderShape;
      //
      // if (arg0.shape1.getUserData().equals(ObjectType.OBJECT)) {
      // objectShape = arg0.shape1;
      // borderShape = arg0.shape2;
      // } else {
      // objectShape = arg0.shape2;
      // borderShape = arg0.shape1;
      // }
      //
      // shapesToRemove.add(new Pair<Shape, BorderType>(objectShape,
      // (BorderType) borderShape.getBody().getUserData()));
      // }
      // }

    }

    @Override
    public void result(ContactResult arg0) {
    }

  }

}
