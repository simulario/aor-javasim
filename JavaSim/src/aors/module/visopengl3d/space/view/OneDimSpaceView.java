package aors.module.visopengl3d.space.view;

import aors.module.visopengl3d.utility.Color;

/**
 * One dimensional space view.
 * 
 * @author Sebastian Mucha
 * @since March 17th, 2010
 * 
 */
public class OneDimSpaceView extends SpaceView {

  // OneDimensional node
  public static final String ONE_DIMENSIONAL = "OneDimensionalSpaceView3D";

  // OneDimensional node attributes
  public static final String MODE = "mode";
  public static final String TRACK_COLOR = "trackColor";
  public static final String TRACK_COLOR_RGB = "trackColorRGB";
  public static final String TRACK_WIDTH = "trackWidth";

  // Track color
  private Color trackColor = Color.WHITE;

  // Track alignment
  private Alignment alignment = Alignment.horizontal;

  // Absolute and relative track width
  private double absoluteTrackWidth;
  private double relativeTrackWidth;
  
  private double trackHeight = 15;

  public Color getTrackColor() {
    return trackColor;
  }

  public void setTrackColor(Color trackColor) {
    this.trackColor = trackColor;
  }

  public Alignment getAlignment() {
    return alignment;
  }

  public void setAlignment(Alignment alignment) {
    this.alignment = alignment;
  }

  public double getAbsoluteTrackWidth() {
    return absoluteTrackWidth;
  }

  public void setAbsoluteTrackWidth(double absoluteTrackWidth) {
    this.absoluteTrackWidth = absoluteTrackWidth;
  }

  public double getRelativeTrackWidth() {
    return relativeTrackWidth;
  }

  public void setRelativeTrackWidth(double relativeTrackWidth) {
    this.relativeTrackWidth = relativeTrackWidth;
  }
  
  public double getTrackHeight() {
    return trackHeight;
    }

  public void setTrackHeight(double trackHeight) {
    this.trackHeight = trackHeight;
  }
}
