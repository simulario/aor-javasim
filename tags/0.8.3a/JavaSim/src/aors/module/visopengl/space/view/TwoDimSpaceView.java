package aors.module.visopengl.space.view;

import aors.module.visopengl.utility.Color;

import com.sun.opengl.util.texture.Texture;

/**
 * Two dimensional, continuous space view.
 * 
 * @author Sebastian Mucha
 * @since March 17th, 2010
 * 
 */
public class TwoDimSpaceView extends SpaceView {

  // TwoDimensional node
  public static final String TWO_DIMENSIONAL = "TwoDimensionalSpaceView2D";

  // TwoDimensional node attributes
  public static final String BACKGROUND_IMG = "backgroundImage";
  public static final String BACKGROUND_COLOR = "backgroundColor";
  public static final String BACKGROUND_COLOR_RGB = "backgroundColorRGB";
  public static final String BORDER_COLOR = "borderColor";
  public static final String BORDER_COLOR_RGB = "borderColorRGB";

  // Background color
  private Color backgroundColor = Color.BLACK;

  // Border color
  private Color borderColor = Color.WHITE;

  // Background image
  private Texture backgroundImg;
  private String backgroundImgFilename;

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
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
}
