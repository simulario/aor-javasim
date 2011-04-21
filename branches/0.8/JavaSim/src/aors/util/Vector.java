package aors.util;

/**
 * Class implements a vector for up to three dimensions.
 * 
 * @author Mircea Diaconescu
 * 
 */
public class Vector {

  // the x component of the vector
  public double x;

  // the y component of the vector
  public double y;

  // the z component of the vector
  public double z;

  /**
   * Default constructor
   */
  public Vector() {
    this.x = 0;
    this.y = 0;
    this.z = 0;
  }

  /**
   * @return the x component of the vector
   */
  public double getX() {
    return x;
  }

  /**
   * @param x
   *          the new x value for the x component of the vector
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * @return the y component of the vector
   */
  public double getY() {
    return y;
  }

  /**
   * @param y
   *          the new y value for the y component of the vector
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * @return the z component of the vector
   */
  public double getZ() {
    return z;
  }

  /**
   * @param z
   *          the new z value for the z component of the vector
   */
  public void setZ(double z) {
    this.z = z;
  }

  /**
   * Create a m vector that is a copy of the vector given as parameter
   * 
   * @param v
   *          the vector that will be the source of the new one
   */
  public Vector(Vector v) {
    x = v.x;
    y = v.y;
    z = v.z;
  }

  /**
   * Create a vector under the form (x,0,0)
   * 
   * @param x
   *          the x component of the vector.
   */
  public Vector(double x) {
    this.x = (Double.isNaN(x)) ? 0 : x;
    this.y = 0;
    this.z = 0;
  }

  /**
   * Create a vector under the form (x,y,0)
   * 
   * @param x
   *          the x component of the vector.
   * 
   * @param y
   *          the y component of the vector.
   */
  public Vector(double x, double y) {
    this.x = (Double.isNaN(x)) ? 0 : x;
    this.y = (Double.isNaN(y)) ? 0 : y;
    this.z = 0;
  }

  /**
   * Create a vector under the form (x,y,z)
   * 
   * @param x
   *          the x component of the vector.
   * 
   * @param y
   *          the y component of the vector.
   * @param z
   *          the z component of the vector.
   */
  public Vector(double x, double y, double z) {
    this.x = (Double.isNaN(x)) ? 0 : x;
    this.y = (Double.isNaN(y)) ? 0 : y;
    this.z = (Double.isNaN(z)) ? 0 : z;
  }

  /**
   * Get length (scalar) of this vector.
   * 
   * @return Length of this vector
   */
  public double getLength() {
    return Math.sqrt(getScalar(this));
  }

  /**
   * Get scalar product of this vector and vector v2
   * 
   * @param v2
   *          Second vector
   * @return Scalar product
   */
  public double getScalar(Vector v2) {
    return (this.x * v2.x + this.y * v2.y + this.z * v2.z);
  }

  /**
   * Get angle in degrees between this vector and the vector given as parameter.
   * 
   * @param v2
   *          the second vector
   * @return angle in degrees
   */
  public double getAngle(Vector v2) {
    double angle = this.getScalar(v2)
        / (Math.sqrt(this.getScalar(this)) * Math.sqrt(v2.getScalar(v2)));
    angle = Math.toDegrees(Math.acos(angle));
    return angle;
  }

  /**
   * Get distance vector between this vector and another vector given as
   * parameter
   * 
   * @param v2
   *          the second vector
   * @return the distance result vector
   */
  public Vector getDistanceVector(Vector v2) {
    Vector dv = new Vector(x - v2.x, y - v2.y, z - v2.z);
    return dv;
  }

  /**
   * Multiply this vector with the given scalar value.
   * 
   * @param m
   *          Value
   * @return This vector
   */
  public Vector mul(double m) {
    x *= m;
    y *= m;
    z *= m;
    return this;
  }

  /**
   * Divide this vector by the given scalar value.
   * 
   * @param d
   *          Value
   * @return This vector
   */
  public Vector div(double d) {
    x /= d;
    y /= d;
    z /= d;
    return this;
  }

  /**
   * Sum the vector with another vector.
   * 
   * @param v
   *          the second vector
   * @return This vector
   */
  public Vector add(Vector v) {
    x += v.x;
    y += v.y;
    z += v.z;
    return this;
  }

  /**
   * Subtract given vector from this vector.
   * 
   * @param v
   *          the second vector
   * @return This vector
   */
  public Vector sub(Vector v) {
    x -= v.x;
    y -= v.y;
    z -= v.z;
    return this;
  }

  /**
   * Determine if every component of this vector is equal to every component of
   * given vector.
   * 
   * @param v
   *          Second vector
   * @return true, if both vectors are equal. false, otherwise
   */
  public boolean equals(Vector v) {
    return x == v.x && y == v.y && z == v.z;
  }

  /**
   * Normalize this vector (divide vector by it's length).
   */
  public void normalize() {
    double len = getLength();
    if (len > 0) {
      div(len);
    }
  }

  @Override
  public String toString() {
    String s = "Vector3D(" + x + ", " + y + ", " + z + ")";
    return s;
  }
}
