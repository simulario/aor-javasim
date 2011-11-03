package aors.module.visopengl3d.space.view;

import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.Texture;

/**
 * Two dimensional, continuous space view.
 * 
 * @author Sebastian Mucha, Susanne Schölzel
 * @since March 17th, 2010
 * 
 */
public class TwoDimSpaceView extends SpaceView {

  // TwoDimensional node
  public static final String TWO_DIMENSIONAL = "TwoDimensionalSpaceView3D";

  // TwoDimensional node attributes
  public static final String BACKGROUND_IMG = "backgroundImage";
  public static final String BACKGROUND_COLOR = "backgroundColor";
  public static final String BACKGROUND_COLOR_RGB = "backgroundColorRGB";
  public static final String BORDER_COLOR = "borderColor";
  public static final String BORDER_COLOR_RGB = "borderColorRGB";
  
  public static final String GLOBAL_CAMERA = "GlobalCamera";
	
  public static final String EYE_POSITION = "eyePosition";
  public static final String LOOK_AT = "lookAt";
  public static final String UP_VECTOR = "upVector";


  // Background color
  private Color backgroundColor = Color.BLACK;

  // Border color
  private Color borderColor = Color.WHITE;

  // Background image
  private Texture backgroundImg;
  private String backgroundImgFilename;
  
  private double[] eyePosition = new double[3];
  private double[] lookAt = new double[3];
  private double[] upVector = new double[3];
  
  private boolean hasGlobalCameraPosition = false;
  

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
  
  public double[] getEyePosition() {	
	return eyePosition;
  }
	  
  public void setEyePosition(double[] eyePosition) {
	this.eyePosition = eyePosition;
  }
	  
  public double[] getLookAt() {
	return lookAt;
  }
	  
  public void setLookAt(double[] lookAt) {
	this.lookAt = lookAt;
  }
  
  public double[] getUpVector() {
	return upVector;
  }
  
  public void setUpVector(double[] upVector) {
	this.upVector = upVector;
  }
  
  public boolean getHasGlobalCameraPosition() {
	return hasGlobalCameraPosition;
  }

  public void setHasGlobalCameraPosition(boolean hasGlobalCameraPosition) {
	this.hasGlobalCameraPosition = hasGlobalCameraPosition;
  }
}
