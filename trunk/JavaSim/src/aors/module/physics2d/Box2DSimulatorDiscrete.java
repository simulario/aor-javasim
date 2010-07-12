/**
 * 
 */
package aors.module.physics2d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbox2d.collision.AABB;
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
import aors.module.physics2d.util.MaterialConstants;
import aors.module.physics2d.util.Pair;

/**
 * This is a physics simulator for 2D simulation using the Box2D physics engine.
 * Used in discrete space (grid space).
 * 
 * @author Holger Wuerke
 * @since 30.01.2010
 * 
 */
public class Box2DSimulatorDiscrete extends PhysicsSimulator {

  /**
   * The Box2D world, where everything is simulated.
   */
  private World world;

  private Set<CollisionEvent> potentialCollisions = new HashSet<CollisionEvent>();

  private Set<Pair<PhysicalAgentObject, Physical>> potentialPerceptions = new HashSet<Pair<PhysicalAgentObject, Physical>>();

  private enum BorderType {
    TOP, BOTTOM, LEFT, RIGHT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT
  };

  private enum ObjectType {
    BORDER, OBJECT, PERCEPTION
  };

  /**
   * Creates a Box2DSimulator. This will set up a world and all necessary
   * objects (bodies) in Box2D.
   * 
   * @param simParams
   * @param spaceModel
   * @param simModel
   * @param objects
   * @param agents
   */
  public Box2DSimulatorDiscrete(SimulationParameters simParams,
      GeneralSpaceModel spaceModel, boolean autoKinematics,
      boolean autoCollisionDetection, boolean autoCollisionHandling,
      double gravitation, DataBus databus, List<PhysicalObject> objects,
      List<PhysicalAgentObject> agents) {

    super(simParams, spaceModel, autoKinematics, autoCollisionDetection,
        autoCollisionHandling, gravitation, databus, objects, agents);

    // TODO: write unit converter to use here
    stepDuration = simParams.getStepDuration();

    if (simParams.getTimeUnit().equals("ms")) {
      stepDuration /= 1000;
    }

    AABB aabb = new AABB(new Vec2(-100, -100), new Vec2((float) spaceModel
        .getXMax() + 100, spaceModel.getYMax() + 100));

    Vec2 gravity = new Vec2(0f, (float) gravitation);
    world = new World(aabb, gravity, true);

    CollisionListener cl = new CollisionListener();
    world.setContactListener(cl);

    // add world borders
    float borderWidth = 5;

    // bottom
    BodyDef borderDef = new BodyDef();
    borderDef.position.set((float) (spaceModel.getXMax() / 2), -borderWidth);

    PolygonDef borderShape = new PolygonDef();
    borderShape.friction = 0.5f;
    borderShape.restitution = 1;
    borderShape.isSensor = true;
    borderShape.userData = ObjectType.BORDER;
    borderShape.setAsBox((float) (spaceModel.getXMax() / 2), borderWidth);

    Body border = world.createBody(borderDef);
    border.createShape(borderShape);
    border.setUserData(BorderType.BOTTOM);

    // top
    borderDef.position.set((float) spaceModel.getXMax() / 2, spaceModel
        .getYMax()
        + borderWidth + 1);
    border = world.createBody(borderDef);
    border.createShape(borderShape);
    border.setUserData(BorderType.TOP);

    // left
    borderDef.position.set(-borderWidth, (float) (spaceModel.getYMax() / 2));
    borderShape.setAsBox(borderWidth, (float) (spaceModel.getXMax() / 2));
    border = world.createBody(borderDef);
    border.createShape(borderShape);
    border.setUserData(BorderType.LEFT);

    // right
    borderDef.position.set((float) spaceModel.getXMax() + borderWidth + 1,
        (float) (spaceModel.getYMax() / 2));
    border = world.createBody(borderDef);
    border.createShape(borderShape);
    border.setUserData(BorderType.RIGHT);

    // add a body for every object
    for (Physical object : getPhysicals()) {
      float x = (float) (object.getX());
      float y = (float) (object.getY());

      BodyDef def = new BodyDef();
      def.position.set(x, y);
      def.userData = object;

      Body body = world.createBody(def);

      // shape -> rectangle
      // local coordinates of vertices
      float x1 = (float) (-(Math.floor(object.getWidth() - 1) / 2) - 0.5);
      float x2 = (float) ((Math.ceil(object.getWidth() - 1) / 2) + 0.5);
      float y1 = (float) (-(Math.floor(object.getHeight() - 1) / 2) - 0.5);
      float y2 = (float) ((Math.ceil(object.getHeight() - 1) / 2) + 0.5);

      PolygonDef rectangleShape = new PolygonDef();

      rectangleShape.addVertex(new Vec2(x1, y1));
      rectangleShape.addVertex(new Vec2(x2, y1));
      rectangleShape.addVertex(new Vec2(x2, y2));
      rectangleShape.addVertex(new Vec2(x1, y2));

      rectangleShape.userData = ObjectType.OBJECT;
      rectangleShape.isSensor = true;
      rectangleShape.density = (float) (object.getM() / (object.getWidth() * object
          .getHeight()));
      rectangleShape.restitution = (float) MaterialConstants.restitution(object
          .getMaterialType());
      rectangleShape.friction = (float) MaterialConstants.friction(object
          .getMaterialType());
      body.createShape(rectangleShape);

      if (autoKinematics) {
        Vec2 v = new Vec2((float) object.getVx(), (float) object.getVy());
        body.setLinearVelocity(v);
        body.setAngularVelocity((float) object.getOmegaZ());
      }

      // if agent, add another shape for perception
      if (object instanceof PhysicalAgentObject) {
        double radius = ((PhysicalAgentObject) object).getPerceptionRadius();

        // local coordinates of vertices
        x1 = (float) (-(Math.floor(object.getWidth() - 1) / 2) - 0.5 - radius);
        x2 = (float) ((Math.ceil(object.getWidth() - 1) / 2) + 0.5 + radius);
        y1 = (float) (-(Math.floor(object.getHeight() - 1) / 2) - 0.5 - radius);
        y2 = (float) ((Math.ceil(object.getHeight() - 1) / 2) + 0.5 + radius);

        PolygonDef shape = new PolygonDef();

        shape.addVertex(new Vec2(x1, y1));
        shape.addVertex(new Vec2(x2, y1));
        shape.addVertex(new Vec2(x2, y2));
        shape.addVertex(new Vec2(x1, y2));

        shape.userData = ObjectType.PERCEPTION;
        shape.density = 0;
        shape.isSensor = true;
        body.createShape(shape);
      }

      body.setMassFromShapes();
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

    updateEngineObjects();

    // perform one simulation step in the engine
    // TODO: if stepDuration is too big, perform multiple small steps in the
    // engine
    world.step((float) stepDuration, 10);

    if (autoKinematics) {
      updateAorObjects();
    }

    if (autoCollisionDetection) {
      checkPotentialCollisions();
    }

    checkPotentialPerceptions();
    sendEvents(stepNumber);
  }

  /**
   * Update the PhysicalObjects and PhysicalAgentObjects with the data from the
   * engine.
   */
  private void updateAorObjects() {
    Body body = world.getBodyList();

    while (body != null) {
      if (body.getUserData() instanceof Physical) {
        Physical object = (Physical) body.getUserData();

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          // Toroidal: update position if body is out of bounds -> opposite side

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
        } else {
          // Euclidean: don't let the body move outside

          if ((Math.round(body.getPosition().x) - Math
              .floor((object.getWidth() - 1) / 2)) < 1) {
            Vec2 position = body.getXForm().position;
            position.x = (float) (1 + Math.floor((object.getWidth() - 1) / 2));
            body.setXForm(position, body.getAngle());
          }

          if (Math.round(body.getPosition().x)
              + Math.ceil((object.getWidth() - 1) / 2) > spaceModel.getXMax()) {
            Vec2 position = body.getXForm().position;
            position.x = (float) (spaceModel.getXMax() - Math.ceil((object
                .getWidth() - 1) / 2));
            body.setXForm(position, body.getAngle());
          }

          if ((Math.round(body.getPosition().y) - Math.floor((object
              .getHeight() - 1) / 2)) < 1) {
            Vec2 position = body.getXForm().position;
            position.y = (float) (1 + Math.floor((object.getHeight() - 1) / 2));
            body.setXForm(position, body.getAngle());
          }

          if (Math.round(body.getPosition().y)
              + Math.ceil((object.getHeight() - 1) / 2) > spaceModel.getYMax()) {
            Vec2 position = body.getXForm().position;
            position.y = (float) (spaceModel.getYMax() - Math.ceil((object
                .getHeight() - 1) / 2));
            body.setXForm(position, body.getAngle());
          }
        }

        // position
        double deltaX = Math.floor(Math.abs(body.getPosition().x
            - object.getX()));
        double deltaY = Math.floor(Math.abs(body.getPosition().y
            - object.getY()));

        if (body.getPosition().x > object.getX()) {
          object.setX(object.getX() + deltaX);
        } else {
          object.setX(object.getX() - deltaX);
        }

        if (body.getPosition().y > object.getY()) {
          object.setY(object.getY() + deltaY);
        } else {
          object.setY(object.getY() - deltaY);
        }

        // orientation
        object.setRotZ(body.getAngle());

        // velocity
        object.setVx(body.getLinearVelocity().x);
        object.setVy(body.getLinearVelocity().y);

        object.setOmegaZ(body.getAngle());

        // debug output
        // System.out.print(object.getId() + ") ");
        // System.out.print("x:" + object.getX() + " y:" + object.getY() + " ");
        // System.out
        // .print("vx:" + object.getVx() + " vy:" + object.getVy() + " ");
        // System.out.println();

        // System.out.print(object.getId() + ") ");
        // System.out.print("x:" + body.getPosition().x + " y:"
        // + body.getPosition().y + " ");
        // System.out.print("vx:" + body.getLinearVelocity().x + " vy:"
        // + body.getLinearVelocity().y + " ");
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

        // update only agents properties
        if (body.getUserData() instanceof PhysicalAgentObject) {
          PhysicalAgentObject agent = (PhysicalAgentObject) body.getUserData();

          float x = (float) agent.getX();
          float y = (float) agent.getY();

          body.setXForm(new Vec2(x, y), (float) agent.getRotZ());
          body.setLinearVelocity(new Vec2((float) agent.getVx(), (float) agent
              .getVy()));
          body.setAngularVelocity((float) agent.getOmegaZ());
        }

        // acceleration
        Physical object = (Physical) body.getUserData();

        if (object.getAx() != 0 || object.getAy() != 0) {
          // F = m * a
          Vec2 force = new Vec2((float) (object.getAx() * object.getM()),
              (float) (object.getAy() * object.getM()));
          body.applyForce(force, body.getLocalCenter());
        }
      }

      body = body.getNext();
    }
  }

  /**
   * Checks the list of potential collisions and creates events for all actual
   * collisions.
   */
  private void checkPotentialCollisions() {
    // TODO: filter multiple collisions
    for (CollisionEvent event : potentialCollisions) {
      Physical object1 = event.getPhysicalObject1();
      Physical object2 = event.getPhysicalObject2();

      // determine start and end cells of the area covered by the objects
      double xs1 = object1.getX() - Math.floor((object1.getWidth() - 1) / 2);
      double xe1 = xs1 + object1.getWidth() - 1;
      double ys1 = object1.getY() - Math.floor((object1.getHeight() - 1) / 2);
      double ye1 = ys1 + object1.getHeight() - 1;

      double xs2 = object2.getX() - Math.floor((object2.getWidth() - 1) / 2);
      double xe2 = xs2 + object2.getWidth() - 1;
      double ys2 = object2.getY() - Math.floor((object2.getHeight() - 1) / 2);
      double ye2 = ys2 + object2.getHeight() - 1;

      // check if some cells are overlapping -> if so, we have a collision
      if (((xs2 >= xs1 && xs2 <= xe1) || (xe2 >= xs1 && xe2 <= xe1))
          && ((ys2 >= ys1 && ys2 <= ye1) || (ye2 >= ys1 && ye2 <= ye1))) {
        // System.out.println(stepNumber + ") Collision: " + object1 + " " +
        // object2);
        events.add(event);
      }
    }

    potentialCollisions.clear();
  }

  /**
   * Checks the list of potential perceptions and creates events for all actual
   * perceptions.
   */
  private void checkPotentialPerceptions() {
    // TODO: filter multiple perceptions
    for (Pair<PhysicalAgentObject, Physical> pair : potentialPerceptions) {
      PhysicalAgentObject perceiver = pair.getFirst();
      Physical perceived = pair.getSecond();

      // determine start and end cells of the area covered by the perceived
      // object and the perception area
      double xs1 = perceiver.getX()
          - Math.floor((perceiver.getWidth() - 1) / 2)
          - perceiver.getPerceptionRadius();
      double xe1 = xs1 + perceiver.getWidth() + 2
          * perceiver.getPerceptionRadius() - 1;
      double ys1 = perceiver.getY()
          - Math.floor((perceiver.getHeight() - 1) / 2)
          - perceiver.getPerceptionRadius();
      double ye1 = ys1 + perceiver.getHeight() + 2
          * perceiver.getPerceptionRadius() - 1;

      double xs2 = perceived.getX()
          - Math.floor((perceived.getWidth() - 1) / 2);
      double xe2 = xs2 + perceived.getWidth() - 1;
      double ys2 = perceived.getY()
          - Math.floor((perceived.getHeight() - 1) / 2);
      double ye2 = ys2 + perceived.getHeight() - 1;

      // check if some cells are overlapping -> if so, we have a perception
      if (((xs2 >= xs1 && xs2 <= xe1) || (xe2 >= xs1 && xe2 <= xe1))
          && ((ys2 >= ys1 && ys2 <= ye1) || (ye2 >= ys1 && ye2 <= ye1))) {

        // find nearest cell of perceived object (perceivedX, perceivedY) with
        // distance distX, distY to the perceiver
        double distX, distY, perceivedX, perceivedY;

        if (xs2 < perceiver.getX() && perceiver.getX() < xe2) {
          perceivedX = perceiver.getX();
          distX = 0;
        } else {

          double deltaXS = Math.abs(xs2 - perceiver.getX());
          double deltaXE = Math.abs(xe2 - perceiver.getX());

          if (deltaXS < deltaXE) {
            perceivedX = xs2;
            distX = deltaXS;
          } else {
            perceivedX = xe2;
            distX = deltaXE;
          }
        }

        if (ys2 < perceiver.getY() && perceiver.getY() < ye2) {
          perceivedY = perceiver.getY();
          distY = 0;
        } else {

          double deltaYS = Math.abs(ys2 - perceiver.getY());
          double deltaYE = Math.abs(ye2 - perceiver.getY());

          if (deltaYS < deltaYE) {
            perceivedY = ys2;
            distY = deltaYS;
          } else {
            perceivedY = ye2;
            distY = deltaYE;
          }
        }

        // TODO: toroidal
        // distance
        double distance = Math.max(distX, distY);

        // angle
        double orientation = perceiver.getRotZ();
        double angle = Math.atan2(perceivedY - perceiver.getY(), perceivedX
            - perceiver.getX());
        angle = (angle < 0) ? angle + 2 * Math.PI : angle;
        angle = (angle < orientation) ? (2 * Math.PI - (orientation - angle))
            : angle - orientation;

        PhysicalObjectPerceptionEvent event = new PhysicalObjectPerceptionEvent(
            stepNumber, perceiver.getId(), perceived.getType(), perceived
                .getId(), distance, angle);

        // System.out.println(stepNumber + ") Perception: " + perceiver + " " +
        // perceived);
        // System.out.println("Cell: " + perceivedX + ", " + perceivedY);
        // System.out.println("Dist: " + distance + " Angle: " + angle * 180 /
        // Math.PI);

        events.add(event);
      }
    }

    potentialPerceptions.clear();
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
      // System.out.println(stepNumber + ": ADD "
      // + arg0.shape1.getBody().getUserData() + " "
      // + arg0.shape2.getBody().getUserData() + " " + arg0.position);

      // perception
      if ((arg0.shape1.getUserData().equals(ObjectType.PERCEPTION) && arg0.shape2
          .getUserData().equals(ObjectType.OBJECT))
          || (arg0.shape1.getUserData().equals(ObjectType.OBJECT) && arg0.shape2
              .getUserData().equals(ObjectType.PERCEPTION))) {

        Shape perceiverShape, perceivedShape;
        if (arg0.shape1.getUserData().equals(ObjectType.PERCEPTION)) {
          perceiverShape = arg0.shape1;
          perceivedShape = arg0.shape2;
        } else {
          perceiverShape = arg0.shape2;
          perceivedShape = arg0.shape1;
        }

        potentialPerceptions.add(new Pair<PhysicalAgentObject, Physical>(
            (PhysicalAgentObject) perceiverShape.getBody().getUserData(),
            (Physical) perceivedShape.getBody().getUserData()));
      }

      // collision
      if (arg0.shape1.getUserData().equals(ObjectType.OBJECT)
          && arg0.shape2.getUserData().equals(ObjectType.OBJECT)) {

        CollisionEvent event = new CollisionEvent(stepNumber);
        event
            .setPhysicalObject1((Physical) arg0.shape1.getBody().getUserData());
        event
            .setPhysicalObject2((Physical) arg0.shape2.getBody().getUserData());

        potentialCollisions.add(event);
      }

    }

    @Override
    public void persist(ContactPoint arg0) {
      // System.out.println(stepNumber + ": PER "
      // + arg0.shape1.getBody().getUserData() + " "
      // + arg0.shape2.getBody().getUserData() + " " + arg0.position);

      // perception
      if ((arg0.shape1.getUserData().equals(ObjectType.PERCEPTION) && arg0.shape2
          .getUserData().equals(ObjectType.OBJECT))
          || (arg0.shape1.getUserData().equals(ObjectType.OBJECT) && arg0.shape2
              .getUserData().equals(ObjectType.PERCEPTION))) {

        Shape perceiverShape, perceivedShape;
        if (arg0.shape1.getUserData().equals(ObjectType.PERCEPTION)) {
          perceiverShape = arg0.shape1;
          perceivedShape = arg0.shape2;
        } else {
          perceiverShape = arg0.shape2;
          perceivedShape = arg0.shape1;
        }

        potentialPerceptions.add(new Pair<PhysicalAgentObject, Physical>(
            (PhysicalAgentObject) perceiverShape.getBody().getUserData(),
            (Physical) perceivedShape.getBody().getUserData()));
      }

      // collision
      if (autoCollisionDetection
          && arg0.shape1.getUserData().equals(ObjectType.OBJECT)
          && arg0.shape2.getUserData().equals(ObjectType.OBJECT)) {

        CollisionEvent event = new CollisionEvent(stepNumber);
        event
            .setPhysicalObject1((Physical) arg0.shape1.getBody().getUserData());
        event
            .setPhysicalObject2((Physical) arg0.shape2.getBody().getUserData());

        potentialCollisions.add(event);
      }
    }

    @Override
    public void remove(ContactPoint arg0) {
      // System.out.println(stepNumber + ": REM "
      // + arg0.shape1.getBody().getUserData() + " "
      // + arg0.shape2.getBody().getUserData() + " " + arg0.position);
    }

    @Override
    public void result(ContactResult arg0) {
    }

  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    // TODO Auto-generated method stub

  }

}
