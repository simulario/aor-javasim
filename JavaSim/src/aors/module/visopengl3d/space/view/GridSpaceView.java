package aors.module.visopengl3d.space.view;

import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.Texture;

/**
 * Two dimensional, discrete space view.
 * 
 * @author Sebastian Mucha
 * @since March 17th, 2010
 * 
 */
public class GridSpaceView extends SpaceView {

  // TwoDimensionalGrid node
  public static final String TWO_DIMENSIONAL_GRID = "TwoDimensionalGridSpaceView3D";

  // TwoDimensionalGrid node attributes
  public static final String FILL1 = "fill1";
  public static final String FILL1_RGB = "fill1RGB";
  public static final String FILL2 = "fill2";
  public static final String FILL2_RGB = "fill2RGB";
  public static final String STROKE = "stroke";
  public static final String STROKE_RGB = "strokeRGB";
  public static final String STROKE_WIDTH = "strokeWidth";
  public static final String BACKGROUND_IMG = "backgroundImage";
  public static final String BACKGROUND_COLOR = "backgroundColor";
  public static final String BACKGROUND_COLOR_RGB = "backgroundColorRGB";

  // Background color
  private Color backgroundColor = Color.BLACK;
  
  // Fill colors
  private Color fill1;
  private Color fill2;

  // Stroke color
  private Color stroke = Color.WHITE;

  // Absolute and relative stroke width
  private double absoluteStrokeWidth;
  private double relativeStrokeWidth;

  // Background image
  private Texture backgroundImg;
  private String backgroundImgFilename;
  
  private double cellHeight = 10;

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public Color getFill1() {
    return fill1;
  }

  public void setFill1(Color fill1) {
    this.fill1 = fill1;
  }

  public Color getFill2() {
    return fill2;
  }

  public void setFill2(Color fill2) {
    this.fill2 = fill2;
  }

  public Color getStroke() {
    return stroke;
  }

  public void setStroke(Color stroke) {
    this.stroke = stroke;
  }

  public double getAbsoluteStrokeWidth() {
    return absoluteStrokeWidth;
  }

  public void setAbsoluteStrokeWidth(double absoluteStrokeWidth) {
    this.absoluteStrokeWidth = absoluteStrokeWidth;
  }

  public double getRelativeStrokeWidth() {
    return relativeStrokeWidth;
  }

  public void setRelativeStrokeWidth(double relativeStrokeWidth) {
    this.relativeStrokeWidth = relativeStrokeWidth;
  }

  public Texture getBackgroundImg() {
    return backgroundImg;
  }

  public void setBackgroundImg(Texture backgroundImg) {
    this.backgroundImg = backgroundImg;
  }

  public String getBackgroundImgFilename() {
    return backgroundImgFilename;
  }

  public void setBackgroundImgFilename(String backgroundImgFilename) {
    this.backgroundImgFilename = backgroundImgFilename;
  }
  
  public double getCellHeight() {
    return cellHeight;
  }

  public void setCellHeight(double cellHeight) {
    this.cellHeight = cellHeight;
  }
}
