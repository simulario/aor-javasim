/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * File: PhysicalObjectImpl.java
 * 
 * Package: info.aors.model
 *
 **************************************************************************************************************/
package aors.model.envsim;

import java.beans.PropertyChangeEvent;

import aors.util.Vector;
import java.util.Map;

/**
 * PhysicalObjectImpl comprise mainly the predefined physical properties
 * (concerning kinematics laws) for Objects and Agents entities described
 * modelled by AORS
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 23, 2008
 * @version $Revision$
 */
// @NotThreadSafe
public class PhysicalObject extends Objekt implements Physical {

  private Vector position = new Vector();

  private Vector rotation = new Vector();

  private Vector velocity = new Vector();

  private Vector acceleration = new Vector();

  private Vector omega = new Vector();

  private Vector alpha = new Vector();

  private double mass;

  private double width;

  private double height;

  private double depth;

  private MaterialType materialType;

  private Shape2D shape2D;

  private Shape3D shape3D;

  private String points = "";

  /**
   * 
   * Create a new {@code PhysicalObjectImpl}.
   * 
   */
  protected PhysicalObject(long id) {
    // calls the constructor of the class Entity
    super(id);

    this.mass = 0;
    this.width = 0;
    this.height = 0;
    this.depth = 0;
  }

  protected PhysicalObject(long id, String name) {
    // calls the constructor of the class Entity
    super(id, name);

    this.mass = 0;
    this.width = 0;
    this.height = 0;
    this.depth = 0;
  }

  /**
   * 
   * Create a new {@code PhysicalObjectImpl}.
   * 
   * @param id
   * 
   */
  protected PhysicalObject() {
    super();

    this.mass = 0;
    this.width = 0;
    this.height = 0;
    this.depth = 0;
  }

  /**
   * creates a complete new {@code PhysicalObjectImpl}; notice: calls of setters
   * are necessary, because weed need the statechangeevents for the listener
   */
  protected PhysicalObject(long id, String name, double x, double y, double z,
      double vx, double vy, double vz, double ax, double ay, double az,
      double m, double width, double height) {
    super(id, name);

    this.setX(x);
    this.setY(y);
    this.setZ(z);
    this.setVx(vx);
    this.setVy(vy);
    this.setVz(vz);
    this.setAx(ax);
    this.setAy(ay);
    this.setAz(az);
    this.setM(m);
    this.setWidth(width);
    this.setHeight(height);
    this.setDepth(depth);
  }

  @Override
  public Vector getPos() {
    return position;
  }

  @Override
  public void setPos(Vector pos) {
    this.position = pos;
  }

  @Override
  public double getX() {
    return this.position.getX();
  }

  @Override
  public void setX(double x) {
    double tmpX = this.position.getX();
    if (tmpX != x) {
      this.position.setX(x);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_X, null, x));
    }
  }

  @Override
  public double getY() {
    return this.position.getY();
  }

  @Override
  public void setY(double y) {
    double tmpY = this.position.getY();
    if (tmpY != y) {
      this.position.setY(y);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_Y, null, y));
    }
  }

  @Override
  public double getZ() {
    return this.position.getZ();
  }

  @Override
  public void setZ(double z) {
    double tmpZ = this.position.getZ();
    if (tmpZ != z) {
      this.position.setZ(z);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_Z, null, z));
    }
  }

  @Override
  public Vector getRot() {
    return rotation;
  }

  @Override
  public void setRot(Vector rot) {
    this.rotation = rot;
  }

  @Override
  public double getRotX() {
    return this.rotation.getX();
  }

  @Override
  public void setRotX(double rotationAngleX) {
    double tmpRotX = this.rotation.getX();
    if (tmpRotX != rotationAngleX) {
      this.rotation.setX(rotationAngleX);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_ROTATION_ANGLE_X, null, rotationAngleX));
    }
  }

  @Override
  public double getRotY() {
    return this.rotation.getY();
  }

  @Override
  public void setRotY(double rotationAngleY) {
    double tmpRotY = this.rotation.getY();
    if (tmpRotY != rotationAngleY) {
      this.rotation.setY(rotationAngleY);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_ROTATION_ANGLE_Y, null, rotationAngleY));
    }
  }

  @Override
  public double getRotZ() {
    return this.rotation.getZ();
  }

  @Override
  public void setRotZ(double rotationAngleZ) {
    double tmpRotZ = this.rotation.getZ();
    if (tmpRotZ != rotationAngleZ) {
      this.rotation.setZ(rotationAngleZ);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_ROTATION_ANGLE_Z, null, rotationAngleZ));
    }
  }

  @Override
  public Vector getOmega() {
    return omega;
  }

  @Override
  public void setOmega(Vector omega) {
    this.omega = omega;
  }

  @Override
  public double getOmegaX() {
    return this.omega.getX();
  }

  @Override
  public void setOmegaX(double omegaX) {
    double tmpOmegaX = this.omega.getX();
    if (tmpOmegaX != omegaX) {
      this.omega.setX(omegaX);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_OMEGA_X, null, omegaX));
    }

  }

  @Override
  public double getOmegaY() {
    return this.omega.getY();
  }

  @Override
  public void setOmegaY(double omegaY) {
    double tmpOmegaY = this.omega.getY();
    if (tmpOmegaY != omegaY) {
      this.omega.setY(omegaY);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_OMEGA_Y, null, omegaY));
    }
  }

  @Override
  public double getOmegaZ() {
    return this.omega.getZ();
  }

  @Override
  public void setOmegaZ(double omegaZ) {
    double tmpOmegaZ = this.omega.getZ();
    if (tmpOmegaZ != omegaZ) {
      this.omega.setZ(omegaZ);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_OMEGA_Z, null, omegaZ));
    }
  }

  @Override
  public Vector getV() {
    return velocity;
  }

  @Override
  public void setV(Vector v) {
    this.velocity = v;
  }

  @Override
  public double getVx() {
    return this.velocity.getX();
  }

  @Override
  public void setVx(double vx) {
    double tmpVx = this.velocity.getX();
    if (tmpVx != vx) {
      this.velocity.setX(vx);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_VX, null, vx));
    }
  }

  @Override
  public double getVy() {
    return this.velocity.getY();
  }

  @Override
  public void setVy(double vy) {
    double tmpVy = this.velocity.getY();
    if (tmpVy != vy) {
      this.velocity.setY(vy);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_VY, null, vy));
    }
  }

  @Override
  public double getVz() {
    return this.velocity.getZ();
  }

  @Override
  public void setVz(double vz) {
    double tmpVz = this.velocity.getZ();
    if (tmpVz != vz) {
      this.velocity.setZ(vz);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_VZ, null, vz));
    }
  }

  @Override
  public Vector getA() {
    return acceleration;
  }

  @Override
  public void setA(Vector a) {
    this.acceleration = a;
  }

  @Override
  public double getAx() {
    return this.acceleration.getX();
  }

  @Override
  public void setAx(double ax) {
    double tmpAx = this.acceleration.getX();
    if (tmpAx != ax) {
      this.acceleration.setX(ax);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_AX, null, ax));
    }
  }

  @Override
  public double getAy() {
    return this.acceleration.getY();
  }

  @Override
  public void setAy(double ay) {
    double tmpAy = this.acceleration.getY();
    if (tmpAy != ay) {
      this.acceleration.setY(ay);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_AY, null, ay));
    }
  }

  @Override
  public double getAz() {
    return this.acceleration.getZ();
  }

  @Override
  public void setAz(double az) {
    double tmpAz = this.acceleration.getZ();
    if (tmpAz != az) {
      this.acceleration.setZ(az);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_AZ, null, az));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#getM()
   */
  public double getM() {
    return mass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#setM(double)
   */
  public void setM(double m) {
    if (this.mass != m) {
      this.mass = m;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_M, null, this.mass));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#getWidth()
   */
  public double getWidth() {
    return width;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#setWidth(double)
   */
  public void setWidth(double width) {
    if (this.width != width) {
      this.width = width;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_WIDTH, null, this.width));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#getHeight()
   */
  public double getHeight() {
    return height;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#setHeight(double)
   */
  public void setHeight(double height) {
    if (this.height != height) {
      this.height = height;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_HEIGHT, null, this.height));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#getDepth()
   */
  public double getDepth() {
    return depth;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.model.envsim.PhysicalObject#setDepth(double)
   */
  public void setDepth(double depth) {
    if (this.depth != depth) {
      this.depth = depth;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_DEPTH, null, this.depth));
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code materialType}.
   * 
   * 
   * 
   * @return the {@code materialType}.
   */
  @Override
  public MaterialType getMaterialType() {
    return materialType;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code materialType}.
   * 
   * 
   * 
   * @param materialType
   *          The {@code materialType} to set.
   */
  @Override
  public void setMaterialType(MaterialType materialType) {
    this.materialType = materialType;
  }

  @Override
  public Shape2D getShape2D() {
    return this.shape2D;
  }

  @Override
  public void setShape2D(Shape2D shape2D) {
    if (this.shape2D != shape2D) {
      this.shape2D = shape2D;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_SHAPE2D, null, this.shape2D));
    }
  }

  @Override
  public Shape3D getShape3D() {
    return this.shape3D;
  }

  @Override
  public void setShape3D(Shape3D shape3D) {
    if (this.shape3D != shape3D) {
      this.shape3D = shape3D;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_SHAPE3D, null, this.shape3D));
    }
  }

  @Override
  public Vector getAlpha() {
    return alpha;
  }

  @Override
  public void setAlpha(Vector alpha) {
    this.alpha = alpha;
  }

  @Override
  public double getAlphaX() {
    return this.alpha.getX();
  }

  @Override
  public void setAlphaX(double alphaX) {
    double tmpAlphaX = this.alpha.getX();
    if (tmpAlphaX != alphaX) {
      this.alpha.setX(alphaX);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_ALPHA_X, null, alphaX));
    }
  }

  @Override
  public double getAlphaY() {
    return this.alpha.getY();
  }

  @Override
  public void setAlphaY(double alphaY) {
    double tmpAlphaY = this.alpha.getY();
    if (tmpAlphaY != alphaY) {
      this.alpha.setY(alphaY);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_ALPHA_Y, null, alphaY));
    }
  }

  @Override
  public double getAlphaZ() {
    return this.alpha.getZ();
  }

  @Override
  public void setAlphaZ(double alphaZ) {
    double tmpAlphaZ = this.alpha.getZ();
    if (tmpAlphaZ != alphaZ) {
      this.alpha.setZ(alphaZ);

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_ALPHA_Z, null, alphaZ));
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code getPoints} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public String getPoints() {
    return this.points;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code setPoints} from super class
   * 
   * 
   * 
   * @param points
   */
  @Override
  public void setPoints(String points) {
    if (!this.points.equals(points)) {
      this.points = points;

      this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
          this, Physical.PROP_POINTS, null, this.points));
    }
  }

  @Override
  public PhysicalObject clone() {
    PhysicalObject result = new PhysicalObject();
    result.setId(this.getId());
    result.setName(new String(this.getName()));
    result.setType(new String(this.getType()));
    result.setX(this.position.getX());
    result.setY(this.position.getY());
    result.setZ(this.position.getZ());
    result.setRotX(this.rotation.getX());
    result.setRotY(this.rotation.getY());
    result.setRotZ(this.rotation.getZ());
    result.setVx(this.velocity.getX());
    result.setVy(this.velocity.getY());
    result.setVz(this.velocity.getZ());
    result.setAx(this.acceleration.getX());
    result.setAy(this.acceleration.getY());
    result.setAz(this.acceleration.getZ());
    result.setOmegaX(this.omega.getX());
    result.setOmegaY(this.omega.getY());
    result.setOmegaZ(this.omega.getZ());
    result.setAlphaX(this.alpha.getX());
    result.setAlphaY(this.alpha.getY());
    result.setAlphaZ(this.alpha.getZ());
    result.setM(this.mass);
    result.setWidth(this.width);
    result.setHeight(this.height);
    result.setDepth(this.depth);
    result.setPoints(new String(this.points));

    return result;
  }

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = super.getProperties();
		properties.put(PROP_ALPHA_X, this.getAlphaX());
		properties.put(PROP_ALPHA_Y, this.getAlphaY());
		properties.put(PROP_ALPHA_Z, this.getAlphaZ());
		properties.put(PROP_AX, this.getAx());
		properties.put(PROP_AY, this.getAy());
		properties.put(PROP_AZ, this.getAz());
		properties.put(PROP_DEPTH, this.getDepth());
		properties.put(PROP_HEIGHT, this.getHeight());
		properties.put(PROP_M, this.getM());
		properties.put(PROP_MATERIALTYPE, this.getMaterialType());
		properties.put(PROP_OMEGA_X, this.getOmegaX());
		properties.put(PROP_OMEGA_Y, this.getOmegaY());
		properties.put(PROP_OMEGA_Z, this.getOmegaZ());
		properties.put(PROP_POINTS, this.getPoints());
		properties.put(PROP_ROTATION_ANGLE_X, this.getRotX());
		properties.put(PROP_ROTATION_ANGLE_Y, this.getRotY());
		properties.put(PROP_ROTATION_ANGLE_Z, this.getRotZ());
		properties.put(PROP_SHAPE2D, this.getShape2D());
		properties.put(PROP_SHAPE3D, this.getShape3D());
		properties.put(PROP_VX, this.getVx());
		properties.put(PROP_VY, this.getVy());
		properties.put(PROP_VZ, this.getVz());
		properties.put(PROP_WIDTH, this.getWidth());
		properties.put(PROP_X, this.getX());
		properties.put(PROP_Y, this.getY());
		properties.put(PROP_Z, this.getZ());
		return properties;
	}
}
