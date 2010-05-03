package aors.module.visopengl.engine;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellatorCallback;

/**
 * The TesselationCallback class is implementing all methods needed to perform
 * polygon tessellation.
 * 
 * @author Sebastian Mucha
 * @since February 17th, 2010
 * 
 */
public class TessellationCallback implements GLUtessellatorCallback {

  // OpenGL pipeline object
  private GL2 gl;

  // OpenGL Utility library object
  private GLU glu;

  public TessellationCallback(GL2 gl, GLU glu) {
    this.gl = gl;
    this.glu = glu;
  }

  @Override
  public void begin(int type) {
    gl.glBegin(type);
  }

  @Override
  public void beginData(int type, Object polygonData) {
  }

  @Override
  public void combine(double[] coords, Object[] data, float[] weight,
      Object[] outData) {

    double[] combinedData = new double[9];

    // Get vertex coordinates
    combinedData[0] = coords[0];
    combinedData[1] = coords[1];
    combinedData[2] = coords[2];

    // Get color coordinates
    combinedData[3] = weight[0] * ((double[]) data[0])[3] + weight[1]
        * ((double[]) data[1])[3] + weight[2] * ((double[]) data[2])[3]
        + weight[3] * ((double[]) data[3])[3];

    combinedData[4] = weight[0] * ((double[]) data[0])[4] + weight[1]
        * ((double[]) data[1])[4] + weight[2] * ((double[]) data[2])[4]
        + weight[3] * ((double[]) data[3])[4];

    combinedData[5] = weight[0] * ((double[]) data[0])[5] + weight[1]
        * ((double[]) data[1])[5] + weight[2] * ((double[]) data[2])[5]
        + weight[3] * ((double[]) data[3])[5];

    combinedData[6] = weight[0] * ((double[]) data[0])[6] + weight[1]
        * ((double[]) data[1])[6] + weight[2] * ((double[]) data[2])[6]
        + weight[3] * ((double[]) data[3])[6];

    // Get the texture coordinates
    combinedData[7] = weight[0] * ((double[]) data[0])[7] + weight[1]
        * ((double[]) data[1])[7] + weight[2] * ((double[]) data[2])[7]
        + weight[3] * ((double[]) data[3])[7];

    combinedData[8] = weight[0] * ((double[]) data[0])[8] + weight[1]
        * ((double[]) data[1])[8] + weight[2] * ((double[]) data[2])[8]
        + weight[3] * ((double[]) data[3])[8];

    // Hand over the combined data to the drawing mechanism
    outData[0] = combinedData;
  }

  @Override
  public void combineData(double[] coords, Object[] data, float[] weight,
      Object[] outData, Object polygonData) {
  }

  @Override
  public void edgeFlag(boolean boundaryEdge) {
  }

  @Override
  public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
  }

  @Override
  public void end() {
    gl.glEnd();
  }

  @Override
  public void endData(Object polygonData) {
  }

  @Override
  public void error(int errnum) {
    // Get the error description from the error code
    String errorString = glu.gluErrorString(errnum);

    // Print out the error and shut down
    System.out.println("Tesselation Error: " + errorString);
    System.exit(0);
  }

  @Override
  public void errorData(int errnum, Object polygonData) {
  }

  @Override
  public void vertex(Object vertexData) {
    if (vertexData instanceof double[]) {
      double[] data = (double[]) vertexData;

      // Set the drawing color
      gl.glColor4d(data[3], data[4], data[5], data[6]);

      // Apply texture
      gl.glTexCoord2d(data[7], data[8]);

      // Draw the vertex
      gl.glVertex2d(data[0], data[1]);
    }
  }

  @Override
  public void vertexData(Object vertexData, Object polygonData) {
  }

}
