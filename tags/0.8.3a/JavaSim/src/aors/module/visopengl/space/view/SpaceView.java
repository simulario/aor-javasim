package aors.module.visopengl.space.view;

import java.util.ArrayList;

import aors.module.visopengl.utility.Color;

/**
 * Associated view of a space model.
 * 
 * @author Sebastian Mucha
 * @since March 17th, 2010
 * 
 */
public abstract class SpaceView {

  // SpaceView node
  public static final String SPACE_VIEW = "SpaceView";

  // SpaceView node attributes
  public static final String CANVAS_COLOR = "canvasColor";
  public static final String CANVAS_COLOR_RGB = "canvasColorRGB";

  // Canvas color
  protected Color canvasColor = Color.BLACK;

  // Property maps (only for grids)
  protected ArrayList<PropertyMap> propertyMaps;

  public Color getCanvasColor() {
    return canvasColor;
  }

  public void setCanvasColor(Color canvasColor) {
    this.canvasColor = canvasColor;
  }

  public ArrayList<PropertyMap> getPropertyMaps() {
    return propertyMaps;
  }

  public void setPropertyMaps(ArrayList<PropertyMap> propertyMaps) {
    this.propertyMaps = propertyMaps;
  }
}
