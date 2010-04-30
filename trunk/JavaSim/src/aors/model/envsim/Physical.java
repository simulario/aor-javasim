package aors.model.envsim;

import aors.util.Vector;

public interface Physical {

  /**
   * Constant that defines the String name of the x property
   */
  public static final String PROP_X = "x";

  /**
   * Constant that defines the String name of the y property
   */
  public static final String PROP_Y = "y";

  /**
   * Constant that defines the String name of the z property
   */
  public static final String PROP_Z = "z";

  /**
   * Constant that defines the String name of the xAngle property
   */
  public static final String PROP_ROTATION_ANGLE_X = "rotationAngleX";

  /**
   * Constant that defines the String name of the yAngle property
   */
  public static final String PROP_ROTATION_ANGLE_Y = "rotationAngleY";

  /**
   * Constant that defines the String name of the zAngle property
   */
  public static final String PROP_ROTATION_ANGLE_Z = "rotationAngleZ";

  /**
   * Constant that defines the String name of the vx property
   */
  public static final String PROP_VX = "vx";

  /**
   * Constant that defines the String name of the vy property
   */
  public static final String PROP_VY = "vy";

  /**
   * Constant that defines the String name of the vz property
   */
  public static final String PROP_VZ = "vz";

  /**
   * Constant that defines the String name of the ax property
   */
  public static final String PROP_AX = "ax";

  /**
   * Constant that defines the String name of the ay property
   */
  public static final String PROP_AY = "ay";

  /**
   * Constant that defines the String name of the az property
   */
  public static final String PROP_AZ = "az";

  /**
   * Constant that defines the String name of the omegaX property
   */
  public static final String PROP_OMEGA_X = "omegaX";

  /**
   * Constant that defines the String name of the omegaY property
   */
  public static final String PROP_OMEGA_Y = "omegaY";

  /**
   * Constant that defines the String name of the omegaZ property
   */
  public static final String PROP_OMEGA_Z = "omegaZ";

  /**
   * Constant that defines the String name of the alphaX property
   */
  public static final String PROP_ALPHA_X = "alphaX";

  /**
   * Constant that defines the String name of the alphaY property
   */
  public static final String PROP_ALPHA_Y = "alphaY";

  /**
   * Constant that defines the String name of the alphaZ property
   */
  public static final String PROP_ALPHA_Z = "alphaZ";

  /**
   * Constant that defines the String name of the m (mass) property
   */
  public static final String PROP_M = "m";

  /**
   * Constant that defines the String name of the width property
   */
  public static final String PROP_WIDTH = "width";

  /**
   * Constant that defines the String name of the height property
   */
  public static final String PROP_HEIGHT = "height";

  /**
   * Constant that defines the String name of the depth property
   */
  public static final String PROP_DEPTH = "depth";

  /**
   * Constant that defines the String name of the materialType
   */
  public static final String PROP_MATERIALTYPE = "materialType";

  /**
   * Constant that defines the String name of the shape2D
   */
  public static final String PROP_SHAPE2D = "shape2D";

  /**
   * Constant that defines the String name of the shape3D
   */
  public static final String PROP_SHAPE3D = "shape3D";

  /**
   * Constant that defines the String name of the shape3D
   */
  public static final String PROP_POINTS = "points";

  /**
   * Constant that defines the String name of the perceptionRadius property
   */
  public static final String PROP_PERCEPTION_RADIUS = "perceptionRadius";

  /**
   * NOTICE: We use this interface as a supertype of every implementation of
   * PhysicalAgentObject and PhysicalObject and need getName() and getId() from
   * Entity
   * 
   */
  public abstract String getName();

  public abstract void setName(String name);

  public abstract long getId();

  public abstract void setId(long id);

  public abstract String getType();

  /**
   * Gets the position vector
   * 
   * @return the vector representing the 3D position in space
   */
  public abstract Vector getPos();

  /**
   * Set a new position vector
   * 
   * @param pos
   *          the new position vector
   */
  public abstract void setPos(Vector pos);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code x}.
   * 
   * 
   * 
   * @return the {@code x}. dimension of this physical object
   */
  public abstract double getX();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code x}.
   * 
   * 
   * 
   * @param x
   *          Set the {@code x} dimension of this physical object.
   */
  public abstract void setX(double x);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code y}.
   * 
   * 
   * 
   * @return the {@code y} dimension of this physical object.
   */
  public abstract double getY();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code y}.
   * 
   * 
   * 
   * @param y
   *          Set the {@code y} dimension of this physical object.
   */
  public abstract void setY(double y);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code z}.
   * 
   * 
   * 
   * @return the {@code z} dimension of this physical object.
   */
  public abstract double getZ();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code z}.
   * 
   * 
   * 
   * @param z
   *          Set the {@code z} dimension of this physical object.
   */
  public abstract void setZ(double z);

  /**
   * Get the rotation vector.
   */
  public abstract Vector getRot();

  /**
   * Set a new rotation angle vector
   * 
   * @param rot
   *          the new rotation angle vector
   */
  public abstract void setRot(Vector rot);

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code rotationAngleX}.
   * 
   * 
   * 
   * @param rotationAngleX
   *          Set the {@code rotationAngleX} rotationAngleX rotation around the
   *          G center.
   */
  public abstract void setRotationAngleX(double rotationAngleX);

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code rotationAngleY}.
   * 
   * 
   * 
   * @param rotationAngleY
   *          Set the {@code rotationAngleY} rotationAngleY rotation around the
   *          G center.
   */
  public abstract void setRotationAngleY(double rotationAngleY);

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code rotationAngleZ}.
   * 
   * 
   * 
   * @param rotationAngleZ
   *          Set the {@code rotationAngleZ} rotationAngleZ rotation around the
   *          G center.
   */
  public abstract void setRotationAngleZ(double rotationAngleZ);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code rotationAngleX}.
   * 
   * 
   * 
   * @return the {@code rotationAngleX} rotationAngleX rotation around the G
   *         center.
   */
  public abstract double getRotationAngleX();

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code rotationAngleY}.
   * 
   * 
   * 
   * @return the {@code rotationAngleY} rotationAngleY rotation around the G
   *         center.
   */
  public abstract double getRotationAngleY();

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code rotationAngleZ}.
   * 
   * 
   * 
   * @return the {@code rotationAngleZ} rotationAngleZ rotation around the G
   *         center.
   */
  public abstract double getRotationAngleZ();

  /**
   * Set a new alpha vector.
   * 
   * @param alpha
   *          the new alpha vector.
   */
  public abstract void setAlpha(Vector alpha);

  /**
   * Get the alpha vector
   * 
   * @return the angular acceleration vector (alpha)
   */
  public abstract Vector getAlpha();

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Get the {@code alphaX}.
   * 
   * 
   * 
   * @return the <@code alphaX} alphaX angular acceleration on the x axis
   */
  public abstract double getAlphaX();

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Get the {@code alphaY}.
   * 
   * 
   * 
   * @return the <@code alphaY} alphaY angular acceleration on the y axis
   */
  public abstract double getAlphaY();

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Get the {@code alphaZ}.
   * 
   * 
   * 
   * @return the <@code alphaZ} alphaZ angular acceleration on the z axis
   */
  public abstract double getAlphaZ();

  /**
   * Set a new omega vector.
   * 
   * @param alpha
   *          the new omega vector.
   */
  public abstract void setOmega(Vector omega);

  /**
   * Get the omega vector
   * 
   * @return the angular velocity vector (omega)
   */
  public abstract Vector getOmega();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code omegaX}.
   * 
   * 
   * 
   * @param omegaX
   *          {@code omegaX} the angular velocity on X axis.
   */
  public abstract void setOmegaX(double omegaX);

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code omegaY}.
   * 
   * 
   * @param omegaY
   *          {@code omegaY} the angular velocity on Y axis.
   */
  public abstract void setOmegaY(double omegaY);

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code omegaZ}.
   * 
   * 
   * @param omegaZ
   *          {@code omegaZ} the angular velocity on Z axis.
   */
  public abstract void setOmegaZ(double omegaZ);

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Set the {@code alphaX}.
   * 
   * 
   * 
   * @param alphaX
   * @return
   */
  public abstract void setAlphaX(double alphaX);

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Set the {@code alphaY}.
   * 
   * 
   * 
   * @param alphaX
   * @return
   */
  public abstract void setAlphaY(double alphaY);

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: Set the {@code alphaZ}.
   * 
   * 
   * 
   * @param alphaX
   * @return
   */
  public abstract void setAlphaZ(double alphaZ);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code omegaX}
   * 
   * 
   * 
   * @return the {@code omegaX} the angular velocity on X axis.
   */
  public abstract double getOmegaX();

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code omegaY}
   * 
   * 
   * 
   * @return the {@code omegaY} the angular velocity on Y axis.
   */
  public abstract double getOmegaY();

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code omegaZ}
   * 
   * 
   * 
   * @return the {@code omegaZ} the angular velocity on Z axis.
   */
  public abstract double getOmegaZ();

  /**
   * Set a new velocity vector.
   * 
   * @param v
   *          the new velocity vector.
   */
  public abstract void setV(Vector v);

  /**
   * Get the velocity vector
   * 
   * @return the velocity vector
   */
  public abstract Vector getV();

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code vx}, the velocity in x dimension of this physical
   * object.
   * 
   * 
   * 
   * @return the {@code vx}, the velocity in x dimension of this physical
   *         object.
   */
  public abstract double getVx();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code vx}.
   * 
   * 
   * 
   * @param vx
   *          Set the {@code vx}, the velocity in x dimension of this physical
   *          object.
   */
  public abstract void setVx(double vx);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code vy}.
   * 
   * 
   * 
   * @return the {@code vy}, the velocity in y dimension of this physical
   *         object.
   */
  public abstract double getVy();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code vy}.
   * 
   * 
   * 
   * @param vy
   *          Set the {@code vy}, the velocity in y dimension of this physical
   *          object.
   */
  public abstract void setVy(double vy);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code vz}, the velocity in z dimension of this physical
   * object.
   * 
   * 
   * 
   * @return the {@code vz}, the velocity in z dimension of this physical
   *         object.
   */
  public abstract double getVz();

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code vz}.
   * 
   * 
   * 
   * @param vz
   *          Set the {@code vz}, the velocity in z dimension of this physical
   *          object.
   */
  public abstract void setVz(double vz);

  /**
   * Get the
   * 
   * @code{ax , the acceleration in x dimension of this physical object.
   * 
   * @return the
   * @code{ax , the acceleration in x dimension of this physical object.
   */
  public abstract double getAx();

  /**
   * Set the ax, the acceleration in x dimension of this physical object.
   * 
   * @param ax
   *          The {@code ax}, the acceleration in x dimension of this physical
   *          object.
   */
  public abstract void setAx(double ax);

  /**
   * Get the
   * 
   * @code{ay , the acceleration in y dimension of this physical object.
   * 
   * @return the
   * @code{ay , the acceleration in y dimension of this physical object.
   */
  public abstract double getAy();

  /**
   * Set the {@code ay}, the acceleration in y dimension of this physical
   * object.
   * 
   * @param ay
   *          Set the ay, the acceleration in x dimension of this physical
   *          object.
   */
  public abstract void setAy(double ay);

  /**
   * Get the
   * 
   * @code{az , the acceleration in z dimension of this physical object.
   * 
   * @return the
   * @code{az , the acceleration in z dimension of this physical object.
   */
  public abstract double getAz();

  /**
   * Set the az, the acceleration in z dimension of this physical object.
   * 
   * @param az
   *          Set the az, the acceleration in x dimension of this physical
   *          object.
   */
  public abstract void setAz(double az);

  /**
   * Get the
   * 
   * @code{m , the mass of this physical object.
   * 
   * @return the
   * @code{m , the mass of this physical object.
   */
  public abstract double getM();

  /**
   * Set the m.
   * 
   * @param m
   *          Set the mass of this physical object.
   */
  public abstract void setM(double m);

  /**
   * Get the
   * 
   * @code{width .
   * 
   * @return the
   * @code{width .
   */
  public abstract double getWidth();

  /**
   * Set the width.
   * 
   * @param width
   *          The width to set.
   */
  public abstract void setWidth(double width);

  /**
   * Get the
   * 
   * @code{height .
   * 
   * @return the
   * @code{height .
   */
  public abstract double getHeight();

  /**
   * Set the height.
   * 
   * @param height
   *          The height to set.
   */
  public abstract void setHeight(double height);

  /**
   * Get the
   * 
   * @code{depth .
   * 
   * @return the
   * @code{depth .
   */
  public abstract double getDepth();

  /**
   * Set the depth.
   * 
   * @param depth
   *          The depth to set.
   */
  public abstract void setDepth(double depth);

  /**
   * Set the materialType
   * 
   * @param materialType
   */
  public abstract void setMaterialType(MaterialType materialType);

  public abstract MaterialType getMaterialType();

  /**
   * Set the shape2D
   * 
   * @param shape2D
   */
  public abstract void setShape2D(Shape2D shape2D);

  public abstract Shape2D getShape2D();

  /**
   * Set the shape3D
   * 
   * @param shape3D
   */
  public abstract void setShape3D(Shape3D shape3D);

  public abstract Shape3D getShape3D();

  /**
   * Set the points
   */
  public abstract void setPoints(String points);

  public abstract String getPoints();

  /**
   * Get the acceleration vector
   * 
   * @return the acceleration vector
   */
  public abstract Vector getA();

  /**
   * Set a new acceleration vector
   */
  public abstract void setA(Vector a);
}