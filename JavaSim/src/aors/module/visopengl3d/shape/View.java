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

  // Three dimensional shape
  private Shape3D shape3D;

  // ShapeMap
  private Shape3DMap shape3DMap;

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
  public static final String ATTACHED_SHAPE = "AttachedShape3D";

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
  
  // String constants for attributes of an "AttachedShape3D" node
  public static final String ATTACHED_SHAPE_LABEL = "label";

  public Shape3D getShape() {
    return shape3D;
  }

  public void setShape3D(Shape3D shape3D) {
    this.shape3D = shape3D;
  }

  public DisplayInfo getDisplayInfo() {
    return displayInfo;
  }

  public void setDisplayInfo(DisplayInfo displayInfo) {
    this.displayInfo = displayInfo;
  }

  public Shape3DMap getShape3DMap() {
    return shape3DMap;
  }

  public void setShape3DMap(Shape3DMap shape3dMap) {
    shape3DMap = shape3dMap;
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
