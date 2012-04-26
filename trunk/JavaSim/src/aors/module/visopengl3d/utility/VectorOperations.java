package aors.module.visopengl3d.utility;


/**
 * The VectorOperations class implements common vector operations for vectors given as arrays of double.
 * 
 * @author Susanne Schölzel
 * @since April 26th, 2012
 * 
 */
public class VectorOperations {
  /**
   * Normalizes a vector by dividing its components through its length
   * 
   * @param v
   *      vector, which should be normalized, as an array
   */
  public static void normalize(double[] v) {
    double length = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
    v[0] /= length;
    v[1] /= length;
    v[2] /= length;
  }
  
  public static double[] normalizedVector(double[] v) {
    double length = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
    double[] normalizedVector = {v[0] / length,
                                 v[1] / length,
                                 v[2] / length};
    return normalizedVector;
  }
  
  public static double scalarProduct(double[] vector1, double[] vector2) {
    double scalarProduct = vector1[0]*vector2[0] + vector1[1]*vector2[1] + vector1[2]*vector2[2];
    return scalarProduct;
  }
  
  public static double vectorLength(double[] v) {
    double length = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
    return length;
  }
  
  public static double cosAngleBetweenVectors(double[] vector1, double[] vector2) {
    double scalarProduct = scalarProduct(vector1, vector2);
    double cosAngle = scalarProduct / (vectorLength(vector1)*vectorLength(vector2));
    return cosAngle;
  }
  
  public static double[] crossProduct(double[] vector1, double[] vector2) {
    double[] crossProduct = {
      vector1[1]*vector2[2] - vector1[2]*vector2[1],
      vector1[2]*vector2[0] - vector1[0]*vector2[2],
      vector1[0]*vector2[1] - vector1[1]*vector2[0]
    };
    return crossProduct;
  }
  
  public static double[] addVectors(double[] vector1, double[] vector2) {
    double[] result = {
      vector1[0] + vector2[0],
      vector1[1] + vector2[1],
      vector1[2] + vector2[2],
    }; 
    return result;
  }
  
  public static double[] subtractVectors(double[] vector1, double[] vector2) {
    double[] result = {
      vector1[0] - vector2[0],
      vector1[1] - vector2[1],
      vector1[2] - vector2[2],
    }; 
    return result;
  }
  
  public static double[] multScalarWithVector(double scalar, double[] vector) {
    double[] result = {
      scalar * vector[0],
      scalar * vector[1],
      scalar * vector[2],
    }; 
    return result;
  }
}
