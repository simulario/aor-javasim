package aors.module.visopengl3d.shape;

import java.util.ArrayList;


/**
 * The View class is representing an actual view of a physical or non-physical
 * object. This means it contains a description of how an object will be
 * displayed on screen.
 * 
 * @author Sebstian Mucha, Susanne Schölzel
 * @since January 19th, 2010
 * 
 */
public class View {

  // Two dimensional shape
  private Shape2D shape2D;

  // ShapeMap
  private Shape2DMap shape2DMap;

  // Display info
  private DisplayInfo displayInfo;

  // AttachedViews
  private ArrayList<View> attachedList;
  
  // Flag indicating if the object associated to this view will be displayed
  private boolean visible;
  
  // Label for attached views
  private String attachedLabel;

  // String constant for the "PhysicalObjectView" and "ObjectView" node
  public static final String PHYSICAL_OBJECT_VIEW = "PhysicalObjectView";
  public static final String OBJECT_VIEW = "ObjectView";
  public static final String ATTACHED_VIEW = "AttachedShape2D";

  // String constants for attributes of a "PhysicalObjectView" node
  public static final String PHYSICAL_OBJECT_TYPE = "physicalObjectType";
  public static final String PHYSICAL_OBJECT_ID_REF = "physicalObjectIdRef";
  public static final String PHYSICAL_OBJECT_START_ID = "physicalObjectStartID";
  public static final String PHYSICAL_OBJ_END_ID = "physicalObjectEndID";

  // String constants for attributes of a "ObjectView" node
  public static final String OBJECT_TYPE = "objectType";
  public static final String OBJECT_ID_REF = "objectIdRef";
  public static final String OBJECT_START_ID = "objectStartID";
  public static final String OBJECT_END_ID = "objectEndID";
  
  // String constants for attributes of an "AttachedView" node
  public static final String ATT_VIEW_LABEL = "label";

  public Shape2D getShape() {
    return shape2D;
  }

  public void setShape2D(Shape2D shape2D) {
    this.shape2D = shape2D;
  }

  public DisplayInfo getDisplayInfo() {
    return displayInfo;
  }

  public void setDisplayInfo(DisplayInfo displayInfo) {
    this.displayInfo = displayInfo;
  }

  public Shape2DMap getShape2DMap() {
    return shape2DMap;
  }

  public void setShape2DMap(Shape2DMap shape2dMap) {
    shape2DMap = shape2dMap;
  }

  public ArrayList<View> getAttachedList() {
    return attachedList;
  }

  public void setAttachedList(ArrayList<View> attachedList) {
    this.attachedList = attachedList;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public String getAttachedLabel() {
    return attachedLabel;
  }

  public void setAttachedLabel(String attachedLabel) {
    this.attachedLabel = attachedLabel;
  }

}
