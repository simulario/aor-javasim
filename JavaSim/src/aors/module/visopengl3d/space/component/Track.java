package aors.module.visopengl3d.space.component;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.engine.TessellatedPolygon;
import aors.module.visopengl3d.shape.Cylinder;
import aors.module.visopengl3d.space.model.OneDimSpaceModel;
import aors.module.visopengl3d.space.view.Alignment;
import aors.module.visopengl3d.utility.Color;
import aors.space.Space;


/**
 * A track is a component of a one dimensional space model. It can either be
 * aligned vertically, horizontally or circular.
 * 
 * @author Sebastian Mucha, Susanne Schölzel
 * @since March 4th, 2010
 * 
 */
public class Track implements SpaceComponent {

  // Space model
  private OneDimSpaceModel spaceModel;

  // Alignment of the track
  private Alignment alignment;

  /*
   * With respect to the alignment these points correspond either to the two end
   * points of the track (vertical/horizontal alignment) or to the centers of
   * the circles (circular alignment).
   */
  private double x1, y1;
  private double x2, y2;

  // Radius of the circles (used for circular alignment)
  private double radius;

  // Stroke width of the track in world dimension (pixel)
  private double trackWidth;

  // Color of the track
  private Color trackColor;

  private double largestSpaceLength;
  private double globalRatio;

  /*
   * With respect to the alignment this length refers either to the whole length
   * of the track (vertical/horizontal) or to the length of the line segment
   * (circular alignment) in world dimension.
   */
  private double worldLength;

  // Circumference of the half circles in world dimension
  private double worldCircumference;

  /*
   * With respect to the alignment this length refers either to the whole length
   * of the track (vertical/horizontal) or to the length of the line segment
   * (circular alignment) in space model dimensions.
   */
  private double spaceLength;

  // Circumference of the half circles in space dimension
  private double spaceCircumference;

  // Distance in percent between each track
  private double distancePercentage;

  /**
   * Creates a new Track instance that is either vertically or horizontally
   * aligned.
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  public Track(double x1, double y1, double x2, double y2) {
    // Store the positions of the tracks end points
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  /**
   * Creates a new Track instance with circular alignment.
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @param radius
   */
  public Track(double x1, double y1, double x2, double y2, double radius) {
    // Store the positions of the tracks end points
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;

    // Store the half circles radius
    this.radius = radius;
  }

  /**
   * Sets up the tracks dimensions.
   * 
   * @param length
   */
  public void setupTrackDimensions(double length) {
    // Set up the tracks dimensions
    if (alignment.equals(Alignment.horizontal)) {
      worldLength = x2 - x1;
      spaceLength = length;
      largestSpaceLength = spaceLength;
    } else if (alignment.equals(Alignment.vertical)) {
      worldLength = y2 - y1;
      spaceLength = length;
      largestSpaceLength = spaceLength;
    } else {
      worldLength = x2 - x1;
      worldCircumference = (Math.PI / 2) * 2 * radius;

      spaceLength = (globalRatio / 2) * (length / 2);
      spaceCircumference = (1 - (globalRatio / 2)) * (length / 2);
    }
  }

  /**
   * Draws a track that is aligned either vertically or horizontally.
   * 
   * @param gl
   * @param glu
   */
  private void drawTrack(GL2 gl, GLU glu) {
	 	//Create and initialize new cylinder which should represent the track
	    Cylinder cylinder = new Cylinder();
	    cylinder.setWidth(trackWidth);	    
	    cylinder.setFill(trackColor);
	    cylinder.setZ(0);

    
	    if (alignment.equals(Alignment.horizontal)) {
	    	cylinder.setHeight(x2-x1);
	    	cylinder.setX(x1 + (x2-x1)/2);
	    	cylinder.setY(y1);
	    	cylinder.setRotZ(90);
	    } else {
	    	cylinder.setHeight(y2-y1);
	    	cylinder.setX(x1);
	    	cylinder.setY(y1 + (y2-y1)/2);
	    }
    
	    // generate display list for cylinder and draw it
	    cylinder.generateDisplayList(gl, glu);
	    cylinder.display(gl, glu);
  }

  /**
   * Draws a track that is aligned circular.
   * 
   * @param gl
   * @param glu
   */
  private void drawCircularTrack(GL2 gl, GLU glu) {
    // List storing the vertices of the tracks contour (outside)
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    // List storing the vertices of the tracks contour (inside)
    ArrayList<double[]> inContour = new ArrayList<double[]>();

    // Create contour lists
    createTrackContour(outContour, radius + (trackWidth / 2));

    if (radius - (trackWidth / 2) > 0)
      createTrackContour(inContour, radius - (trackWidth / 2));
    else
      return;

    // Draw the track as a tessellated polygon
    drawTessellatedCircularTrack(outContour, inContour, gl, glu);
  }

  /**
   * Sets up a list of vertices describing the contour of a circular aligned
   * polygon.
   * 
   * @param contour
   * @param radius
   */
  private void createTrackContour(ArrayList<double[]> contour, double radius) {
    // Create vertices for the left half circle
    for (int alpha = 90; alpha <= 270; alpha++) {
      // A contours vertex (coordinates, color, texture)
      double vertex[] = new double[9];

      // Apply color
      vertex[3] = trackColor.getRed();
      vertex[4] = trackColor.getGreen();
      vertex[5] = trackColor.getBlue();
      vertex[6] = trackColor.getAlpha();

      // Apply texture coordinates (no texture)
      vertex[7] = vertex[8] = 0;

      // Calculate the coordinates of a point lying on the half circle
      double u, v;

      u = Math.cos(alpha * (Math.PI / 180)) * radius;
      v = Math.sin(alpha * (Math.PI / 180)) * radius;

      // Apply coordinates
      vertex[0] = x1 + u;
      vertex[1] = y1 + v;
      vertex[2] = 0;

      // Add the vertex to the contour list
      contour.add(vertex);
    }

    // Create vertices for the right half circle
    for (int alpha = 270; alpha <= 450; alpha++) {
      // A contours vertex (coordinates, color, texture)
      double vertex[] = new double[9];

      // Apply color
      vertex[3] = trackColor.getRed();
      vertex[4] = trackColor.getGreen();
      vertex[5] = trackColor.getBlue();
      vertex[6] = trackColor.getAlpha();

      // Apply texture coordinates (no texture)
      vertex[7] = vertex[8] = 0;

      // Calculate the coordinates of a point lying on the half circle
      double u, v;

      u = Math.cos(alpha * (Math.PI / 180)) * radius;
      v = Math.sin(alpha * (Math.PI / 180)) * radius;

      // Apply coordinates
      vertex[0] = x2 + u;
      vertex[1] = y2 + v;
      vertex[2] = 0;

      // Add the vertex to the contour list
      contour.add(vertex);
    }
  }

  /**
   * Draws a circular track as a tessellated polygon.
   * 
   * @param outContour
   * @param inContour
   * @param gl
   *          .
   * @param glu
   */
  private void drawTessellatedCircularTrack(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour, GL2 gl, GLU glu) {
    // Initialize tessellation
    TessellatedPolygon circularTrack = new TessellatedPolygon();
    circularTrack.init(gl, glu);
    circularTrack.setWindingRule(GLU.GLU_TESS_WINDING_ODD);

    // Draw the polygon
    circularTrack.beginPolygon();
    circularTrack.beginContour();
    circularTrack.renderContour(outContour);
    circularTrack.endContour();
    circularTrack.beginContour();
    circularTrack.renderContour(inContour);
    circularTrack.endContour();
    circularTrack.endPolygon();
    circularTrack.end();
  }

  @Override
  public void display(GL2 gl, GLU glu) {
    if (alignment.equals(Alignment.horizontal)
        || alignment.equals(Alignment.vertical)) {
      drawTrack(gl, glu);
    } else {
      drawCircularTrack(gl, glu);
    }
  }

  @Override
  public double getObjectHeight(double height) {
    return (worldLength * height) / largestSpaceLength;
  }

  @Override
  public double getObjectWidth(double width) {
    return (worldLength * width) / largestSpaceLength;
  }

  @Override
  public double getRotation(double x, double y) {
    // Map to horizontal or vertical alignment
    if (alignment == Alignment.horizontal || alignment == Alignment.vertical) {
      return 0;
    }

    // Map to circular alignment
    else {
      // Bottom line segment
      if (x < spaceLength) {
        return 0;
      }

      // Right half circle
      else if (x < spaceLength + spaceCircumference) {
        // Length of the arc (from the beginning of the half circle too x)
        double arcLength = x - spaceLength;

        // Ratio of the arc length compared to the circumference
        double ratio = arcLength / spaceCircumference;

        // Length of the arc in world coordinates
        double arcWorldLength = ratio * worldCircumference;

        // Angle of the arc in degrees
        return (arcWorldLength * 360) / (2 * Math.PI * radius);
      }

      // Top line segment
      else if (x < 2 * spaceLength + spaceCircumference) {
        return 180;
      }

      // Left half circle
      else {
        // Length of the arc (from the beginning of the half circle too x)
        double arcLength = x - (2 * spaceLength - spaceCircumference);

        // Ratio of the arc length compared to the circumference
        double ratio = arcLength / spaceCircumference;

        // Length of the arc in world coordinates
        double arcWorldLength = ratio * worldCircumference;

        // Angle of the arc in degrees
        return (arcWorldLength * 360) / (2 * Math.PI * radius) + 180;
      }
    }
  }

  @Override
  public double[] getWorldCoordinates(double x, double yP) {
    double vertex[] = new double[3];
    
    double y= yP- Space.ORDINATEBASE;

    // Map to horizontal alignment
    if (alignment == Alignment.horizontal) {
      vertex[0] = x1 + ((worldLength * x) / spaceLength);
      vertex[1] = y1;
    }

    // Map to vertical alignment
    if (alignment == Alignment.vertical) {
      vertex[0] = x1;
      vertex[1] = y1 + ((worldLength * x) / spaceLength);
    }

    // Map to circular alignment
    if (alignment == Alignment.circular) {
      // Bottom line segment
      if (x < spaceLength) {
        vertex[0] = x1 + ((worldLength * x) / spaceLength);
        //y1 - radius ?
        vertex[1] = -radius;
      }

      // Right half circle
      else if (x < spaceLength + spaceCircumference) {
        // Length of the arc (from the beginning of the half circle too x)
        double arcLength = x - spaceLength;

        // Ratio of the arc length compared to the circumference
        double ratio = arcLength / spaceCircumference;

        // Length of the arc in world coordinates
        double arcWorldLength = ratio * worldCircumference;

        // Angle of the arc in degrees
        double alpha = (arcWorldLength * 360) / (2 * Math.PI * radius);

        // Angle of the arc in radiant
        double alphaRadiant = (alpha + 270) / (180 / Math.PI);

        // Calculate the u and v position on the arc
        double u = x2 + (Math.cos(alphaRadiant) * radius);
        double v = y2 + (Math.sin(alphaRadiant) * radius);

        // Scale the coordinates (important only for inner tracks)
        if (y == 0) {
          vertex[0] = u * (1 - (y * distancePercentage));
          vertex[1] = v * (1 - (y * distancePercentage));
        } else {
          vertex[0] = u * (1 - ((y - 1) * distancePercentage));
          vertex[1] = v * (1 - ((y - 1) * distancePercentage));
        }
      }

      // Top line segment
      else if (x < 2 * spaceLength + spaceCircumference) {
        vertex[0] = x2
            - (worldLength * (x - spaceLength - spaceCircumference) / spaceLength);
        //y1 + radius ?
        vertex[1] = radius;
      }

      // Left half circle
      else {
        // Length of the arc (from the beginning of the half circle too x)
        double arcLength = x - (2 * spaceLength - spaceCircumference);

        // Ratio of the arc length compared to the circumference
        double ratio = arcLength / spaceCircumference;

        // Length of the arc in world coordinates
        double arcWorldLength = ratio * worldCircumference;

        // Angle of the arc in degrees
        double alpha = (arcWorldLength * 360) / (2 * Math.PI * radius);

        // Angle of the arc in radiant
        double alphaRadiant = (alpha + 90) / (180 / Math.PI);

        // Calculate the u and v position on the arc
        double u = x1 + (Math.cos(alphaRadiant) * radius);
        double v = y1 + (Math.sin(alphaRadiant) * radius);

        // Scale the coordinates (important only for inner tracks)
        if (y == 0) {
          vertex[0] = u * (1 - (y * distancePercentage));
          vertex[1] = v * (1 - (y * distancePercentage));
        } else {
          vertex[0] = u * (1 - ((y - 1) * distancePercentage));
          vertex[1] = v * (1 - ((y - 1) * distancePercentage));
        }
      }
    }

    return vertex;
  }

  public Alignment getAlignment() {
    return alignment;
  }

  public void setAlignment(Alignment alignment) {
    this.alignment = alignment;
  }

  public double getTrackWidth() {
    return trackWidth;
  }

  public void setTrackWidth(double trackWidth) {
    this.trackWidth = trackWidth;
  }

  public Color getTrackColor() {
    return trackColor;
  }

  public void setTrackColor(Color trackColor) {
    this.trackColor = trackColor;
  }

  public double getDistancePercentage() {
    return distancePercentage;
  }

  public void setDistancePercentage(double distancePercentage) {
    this.distancePercentage = distancePercentage;
  }

  public double getGlobalRatio() {
    return globalRatio;
  }

  public void setGlobalRatio(double globalRatio) {
    this.globalRatio = globalRatio;
  }

  public double getLargestSpaceLength() {
    return largestSpaceLength;
  }

  public void setLargestSpaceLength(double largestSpaceLength) {
    this.largestSpaceLength = largestSpaceLength;
  }

  public OneDimSpaceModel getSpaceModel() {
    return spaceModel;
  }

  public void setSpaceModel(OneDimSpaceModel spaceModel) {
    this.spaceModel = spaceModel;
  }
}
