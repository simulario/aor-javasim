package aors.physim.util;

/**
 * Class implements a vector for up to three dimensions.
 * 
 * @author Stefan Boecker
 * 
 */
public class Vector {

  public double x;
  public double y;
  public double z;

  public Vector() {
  }

  public Vector(Vector v) {
    x = v.x;
    y = v.y;
    z = v.z;
  }

  public Vector(double x) {
    this.x = (Double.isNaN(x)) ? 0 : x;
    this.y = 0;
    this.z = 0;
  }

  public Vector(double x, double y) {
    this.x = (Double.isNaN(x)) ? 0 : x;
    this.y = (Double.isNaN(y)) ? 0 : y;
    this.z = 0;
  }

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
   * Get angle between this vector and v2 in degrees.
   * 
   * @param v2
   *          Second vector
   * @return Angle in degrees
   */
  public double getAngle(Vector v2) {
    double angle = this.getScalar(v2)
        / (Math.sqrt(this.getScalar(this)) * Math.sqrt(v2.getScalar(v2)));
    angle = Math.toDegrees(Math.acos(angle));
    return angle;
  }

  /**
   * Get distance vector between this vector and v2
   * 
   * @param v2
   *          Second vector
   * @return Distance vector
   */
  public Vector getDistanceVector(Vector v2) {
    Vector dv = new Vector(x - v2.x, y - v2.y, z - v2.z);
    return dv;
  }

  /**
   * Multiply this vector with given value.
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
   * Divide this vector by given value.
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
   * Add vector v to this vector.
   * 
   * @param v
   *          Other vector
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
   *          Second vector
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    String s = "(" + x + ", " + y + ", " + z + ")";
    return s;
  }
}
