package aors.physim.extsim;

import java.lang.reflect.Field;

import aors.GeneralSpaceModel;
import aors.model.envsim.Physical;
import aors.physim.baseclasses.PhysicsData;
import aors.physim.util.UnitConverter;
import aors.physim.util.Vector;

/**
 * TranslationSimulator implements the laws of motion (translation) of physical
 * objects. KinematicsSimulator is it's baseclass.
 * 
 * @author Stefan Boecker
 * 
 */
public final class TranslationSimulator extends KinematicsSimulator {

  /**
   * Duration of one step in the simulation.
   */
  private double stepDuration;

  /**
   * Geometry of the space-model.
   */
  private GeneralSpaceModel.Geometry geometry;

  /**
   * Border of the space-model.
   */
  private long xMax, yMax, zMax;

  private UnitConverter ucon;

  public TranslationSimulator(PhysicsData pdata) {
    super(pdata);
    this.geometry = pdata.getSpaceModel().getGeometry();
    String timeUnit = "";

    // determine stepDuration
    Field[] fields = pdata.getParams().getClass().getDeclaredFields();
    for (Field f : fields) {
      try {
        if (f.getName().equals(
            aors.GeneralSimulationParameters.STEP_DURATION_NAME)) {
          this.stepDuration = (Double.valueOf(f.get(pdata.getParams())
              .toString()));
          // System.out.println("stepDuration: "+this.stepDuration);
        }
        if (f.getName().equals(aors.GeneralSimulationParameters.TIME_UNIT_NAME)) {
          timeUnit = (f.get(pdata.getParams()).toString());
          // System.out.println("timeUnit: "+timeUnit);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    this.xMax = pdata.getSpaceModel().getXMax();
    this.yMax = pdata.getSpaceModel().getYMax();
    this.zMax = pdata.getSpaceModel().getZMax();

    this.ucon = new UnitConverter(timeUnit, pdata.getSpaceModel()
        .getSpatialDistanceUnit());
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.physim.extsim.KinematicsSimulator#determinePosition()
   */
  protected void determinePosition(long step) {
    // ArrayList<Physical> phyobjs = getPhysicalObjectList();
    double x, y, z, xOld, yOld, zOld;

    // determine positions for all physical agents and physical objects in
    // simulation
    for (Physical phyobj : phyobjs) {

      xOld = phyobj.getX();
      yOld = phyobj.getY();
      zOld = phyobj.getZ();

      x = phyobj.getVx() * ucon.convertTimeUnitToSeconds(stepDuration)
          + ucon.convertDistanceUnitToMeter(xOld);
      y = phyobj.getVy() * ucon.convertTimeUnitToSeconds(stepDuration)
          + ucon.convertDistanceUnitToMeter(yOld);
      z = phyobj.getVz() * ucon.convertTimeUnitToSeconds(stepDuration)
          + ucon.convertDistanceUnitToMeter(zOld);

      x = ucon.convertDistanceUnit(x);
      y = ucon.convertDistanceUnit(y);
      z = ucon.convertDistanceUnit(z);

      // mark this physical object as "position changed"
      // this is necessary for collision-detection, because it's more efficient
      if (x != xOld || y != yOld || z != zOld) {
        pdata.getPositionChanged().put(phyobj.getId(), true);
      } else {
        pdata.getPositionChanged().put(phyobj.getId(), false);
      }

      // set new position
      phyobj.setX(pdata.getSpaceModel().newX(phyobj, x - xOld));
      phyobj.setY(pdata.getSpaceModel().newY(phyobj, y - yOld));
      phyobj.setZ(pdata.getSpaceModel().newZ(phyobj, z - zOld));
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.physim.extsim.KinematicsSimulator#determineVelocity()
   */
  protected void determineVelocity(long step) {
    // ArrayList<Physical> phyobjs = getPhysicalObjectList();
    double vx, vy, vz, vxOld, vyOld, vzOld;

    // agents and objects
    for (Physical phyobj : phyobjs) {

      vxOld = phyobj.getVx();
      vyOld = phyobj.getVy();
      vzOld = phyobj.getVz();

      Vector newVel = pdata.getNewVelocity().get(phyobj.getId());
      if (newVel != null) {
        vxOld = newVel.x;
        vyOld = newVel.y;
        vzOld = newVel.z;
      }

      vx = phyobj.getAx() * ucon.convertTimeUnitToSeconds(stepDuration) + vxOld;
      vy = phyobj.getAy() * ucon.convertTimeUnitToSeconds(stepDuration) + vyOld;
      vz = phyobj.getAz() * ucon.convertTimeUnitToSeconds(stepDuration) + vzOld;

      // if geometry is Euclidean, set velocity to zero
      // if xMax, yMax, zMax or zero is reached
      if (geometry == GeneralSpaceModel.Geometry.Euclidean) {

        if (phyobj.getX() + phyobj.getWidth() / 2 >= xMax
            || phyobj.getX() - phyobj.getWidth() / 2 <= 0) {
          vx = 0;
        }

        if (phyobj.getY() + phyobj.getHeight() / 2 >= yMax
            || phyobj.getY() - phyobj.getHeight() / 2 <= 0) {
          vy = 0;
        }

        if (phyobj.getZ() + phyobj.getDepth() / 2 >= zMax
            || phyobj.getZ() - phyobj.getDepth() / 2 <= 0) {
          vz = 0;
        }

      }

      // set new velocity
      phyobj.setVx(vx);
      phyobj.setVy(vy);
      phyobj.setVz(vz);

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.physim.extsim.KinematicsSimulator#determineAcceleration()
   */
  protected void determineAcceleration() {
    // if geometry is Euclidean, set velocity to zero, if xMax, yMax, zMax is
    // reached
    // otherwise we don't have to change acceleration in translationsimulator

    // agents and objects
    // ArrayList<Physical> phyobjs = getPhysicalObjectList();
    double ax, ay, az;

    for (Physical phyobj : phyobjs) {

      ax = phyobj.getAx();
      ay = phyobj.getAy();
      az = phyobj.getAz();

      Vector newAcc = pdata.getNewAcceleration().get(phyobj.getId());
      if (newAcc != null) {
        ax = newAcc.x;
        ay = newAcc.y;
        az = newAcc.z;
      }

      if (geometry == GeneralSpaceModel.Geometry.Euclidean) {
        if (phyobj.getX() + phyobj.getWidth() / 2 >= xMax
            || phyobj.getX() - phyobj.getWidth() / 2 <= 0) {
          ax = 0;
        }

        if (phyobj.getY() + phyobj.getHeight() / 2 >= yMax
            || phyobj.getY() - phyobj.getHeight() / 2 <= 0) {
          ay = 0;
        }

        if (phyobj.getZ() + phyobj.getDepth() / 2 >= zMax
            || phyobj.getZ() - phyobj.getDepth() / 2 <= 0) {
          az = 0;
        }
      }

      phyobj.setAx(ax);
      phyobj.setAy(ay);
      phyobj.setAz(az);

    }

  }

}
