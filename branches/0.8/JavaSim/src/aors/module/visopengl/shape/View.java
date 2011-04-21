package aors.module.visopengl.shape;

import java.util.ArrayList;

/**
 * The View class is representing an actual view of a physical or non-physical
 * object. This means it contains a description of how an object will be
 * displayed on screen.
 * 
 * @author Sebstian Mucha
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

  // EmbeddedView
  private ArrayList<View> embeddedList;
  
  // Flag indicating if the object associated to this view will be displayed
  private boolean visible;
  
  // Label for embedded views
  private String embeddedLabel;

  // String constant for the "PhysicalObjectView" and "ObjectView" node
  public static final String PHYSICAL_OBJECT_VIEW = "PhysicalObjectView";
  public static final String OBJECT_VIEW = "ObjectView";
  public static final String EMBEDDED_VIEW = "AttachedShape2D";

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
  
  // String constants for attributes of an "EmbeddedView" node
  public static final String EMB_VIEW_LABEL = "label";

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

  public ArrayList<View> getEmbeddedList() {
    return embeddedList;
  }

  public void setEmbeddedList(ArrayList<View> embeddedList) {
    this.embeddedList = embeddedList;
  }

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getEmbeddedLabel() {
		return embeddedLabel;
	}

	public void setEmbeddedLabel(String embeddedLabel) {
		this.embeddedLabel = embeddedLabel;
	}

}
