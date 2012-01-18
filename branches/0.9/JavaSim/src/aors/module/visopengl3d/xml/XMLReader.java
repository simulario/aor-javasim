package aors.module.visopengl3d.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aors.GeneralSpaceModel.SpaceType;
import aors.module.visopengl3d.lang.LanguageManager;
import aors.module.visopengl3d.shape.Cube;
import aors.module.visopengl3d.shape.Cuboid;
import aors.module.visopengl3d.shape.Cone;
import aors.module.visopengl3d.shape.Cylinder;
import aors.module.visopengl3d.shape.DisplayInfo;
import aors.module.visopengl3d.shape.Mesh;
import aors.module.visopengl3d.shape.Pyramid;
import aors.module.visopengl3d.shape.RegularTriangularPrism;
import aors.module.visopengl3d.shape.Shape3D;
import aors.module.visopengl3d.shape.Shape3DMap;
import aors.module.visopengl3d.shape.ShapePropertyVisualizationMap;
import aors.module.visopengl3d.shape.Sphere;
import aors.module.visopengl3d.shape.Tetrahedra;
import aors.module.visopengl3d.shape.View;
import aors.module.visopengl3d.space.view.Alignment;
import aors.module.visopengl3d.space.view.Face;
import aors.module.visopengl3d.space.view.GridSpaceView;
import aors.module.visopengl3d.space.view.MapType;
import aors.module.visopengl3d.space.view.OneDimSpaceView;
import aors.module.visopengl3d.space.view.PropertyMap;
import aors.module.visopengl3d.space.view.Skybox;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.space.view.TwoDimSpaceView;
import aors.module.visopengl3d.utility.Color;
import aors.util.XMLLoader;

/**
 * With the help of this classes methods its possible to read certain
 * information directly from an XML document.
 * 
 * @author Sebastian Mucha, Susanne Sch�lzel
 * @since January 3rd, 2010
 * 
 */
public class XMLReader {

  // Project directory
  private File projectDirectory;

  // String constant for the "UserInterface" node
  private static final String USER_INTERFACE = "UserInterface";

  // String constant for the "AnimationUI" node
  private static final String ANIMATION_UI = "AnimationUI";

  // String constant for the "DisplayDescription" node
  private static final String DISPLAY_DESCRIPTION = "DisplayDescription";

  // the subelements for DisplayDescription, namely HtmlText
  private static final String DISPLAY_DESCRIPTION_HTML_TEXT = "HtmlText";

  // the xml:lang attribute name
  private static final String XML_LANG_ATTR = "xml:lang";

  // String constant for the "Views" node
  private static final String VIEWS = "Views";

  /**
   * Creates a new XMLReader instance and loads the XML document.
   * 
   * @param dom
   * @param projectDirectory
   */
  public XMLReader(Document dom, File projectDirectory) {
    XMLLoader.loadXML(dom);
    this.projectDirectory = projectDirectory;
  }

  /**
   * Returns the "Views" node of the simulation description.
   */
  private Node searchViewsNode() {
    // "Views" node
    Node views = null;

    // Search for the "UserInterface" node
    NodeList ui = XMLLoader.getNodeList(USER_INTERFACE);

    if (ui != null) {
      if (ui.getLength() > 0) {
        // Retrieve child nodes of "UserInterface"
        NodeList uiChildNodes = ui.item(0).getChildNodes();

        // Search for the "AnimationUI" node inside "UserInterface"
        for (int i = 0; i < uiChildNodes.getLength(); i++) {
          if (uiChildNodes.item(i).getNodeName().equals(ANIMATION_UI)) {
            // Retrieve child nodes of "AnimationUI"
            NodeList auiChildNodes = uiChildNodes.item(i).getChildNodes();

            // Search for the "Views" node inside "AnimationUI"
            for (int j = 0; j < auiChildNodes.getLength(); j++) {
              if (auiChildNodes.item(j).getNodeName().equals(VIEWS)) {
                views = auiChildNodes.item(j);
              }
            }
          }
        }
      }
    }

    return views;
  }

  /**
   * Returns the space view. All data is read directly from the simulation
   * description.
   * 
   * @param spaceType
   */
  public SpaceView getSpaceView(SpaceType spaceType) {
    // Space view
    SpaceView spaceView = null;

    // Canvas color
    Color canvasColor = null;

    // Retrieve the "Views" node
    Node views = searchViewsNode();

    if (views != null) {
      // Retrieve child nodes of the "Views" node
      NodeList viewsChildNodes = views.getChildNodes();

      for (int i = 0; i < viewsChildNodes.getLength(); i++) {
        if (viewsChildNodes.item(i).getNodeName().equals(SpaceView.SPACE_VIEW)) {
          // Retrieve the attributes of a "SpaceView" node
          NamedNodeMap attributes = viewsChildNodes.item(i).getAttributes();

          // Color flag
          boolean colorSet = false;

          // Retrieve the name and value of each attribute
          for (int k = 0; k < attributes.getLength(); k++) {
            String name = attributes.item(k).getNodeName();
            String value = attributes.item(k).getNodeValue();

            // Check which attributes where found and read the
            // values
            if (name.equals(SpaceView.CANVAS_COLOR)) {
              canvasColor = new Color(value);
              colorSet = true;
            }

            else if (name.equals(SpaceView.CANVAS_COLOR_RGB) && !colorSet) {
              canvasColor = new Color(value);
            }
          }

          // Retrieve child nodes of the "SpaceView" node
          NodeList spaceViewChildNodes = viewsChildNodes.item(i)
              .getChildNodes();

          for (int j = 0; j < spaceViewChildNodes.getLength(); j++) {
            /*
             * When the space view node matches the space models space type, it
             * will be associated to the space model.
             */
            if (spaceViewChildNodes.item(j).getNodeName().equals(
                OneDimSpaceView.ONE_DIMENSIONAL)) {
              if (spaceType.equals(SpaceType.OneD)) {
                spaceView = getOneDimSpaceView(spaceViewChildNodes.item(j));
                spaceView.setSkybox(getSkybox(spaceViewChildNodes.item(j)));
              }
            }

            else if (spaceViewChildNodes.item(j).getNodeName().equals(
                GridSpaceView.TWO_DIMENSIONAL_GRID)) {
              if (spaceType.equals(SpaceType.TwoDGrid)) {
                spaceView = getGridSpaceView(spaceViewChildNodes.item(j));
                spaceView.setPropertyMaps(getPropertyMaps(spaceViewChildNodes
                    .item(j)));
                spaceView.setSkybox(getSkybox(spaceViewChildNodes.item(j)));
              }
            }

            else if (spaceViewChildNodes.item(j).getNodeName().equals(
                TwoDimSpaceView.TWO_DIMENSIONAL)) {
              if (spaceType.equals(SpaceType.TwoD)
            		  || spaceType.equals(SpaceType.TwoDLateralView)) {
                spaceView = getTwoDimSpaceView(spaceViewChildNodes.item(j));
                spaceView.setSkybox(getSkybox(spaceViewChildNodes.item(j)));
              }
            }
          }
        }
      }
    }

    if (spaceView == null) {
      // No space view was created, get a default one
      spaceView = getDefaultSpaceView(spaceType);
    }

    // Set the canvas color member of the space view object
    if (canvasColor != null) {
      spaceView.setCanvasColor(canvasColor);
    }

    return spaceView;
  }

  /**
   * Returns a one dimensional space view as read from the XML simulation
   * description.
   * 
   * @param node
   */
  private SpaceView getOneDimSpaceView(Node node) {
    // Create the OneDimSpaceView instance
    OneDimSpaceView spaceView = new OneDimSpaceView();

    // Retrieve the attributes of the node
    NamedNodeMap attributes = node.getAttributes();

    // Color flags
    boolean trackColorSet = false;

    /*
     * Retrieve the name and value of each attribute and initialize the class
     * members accordingly.
     */
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      // Check which attributes where found and initialize the members
      if (name.equals(OneDimSpaceView.MODE))
        spaceView.setAlignment(Alignment.valueOf(value));

      else if (name.equals(OneDimSpaceView.TRACK_COLOR)) {
        spaceView.setTrackColor(new Color(value));
        trackColorSet = true;
      }

      else if (name.equals(OneDimSpaceView.TRACK_COLOR_RGB) && !trackColorSet) {
        spaceView.setTrackColor(new Color(value));
      }

      else if (name.equals(OneDimSpaceView.TRACK_WIDTH)) {
        // Check if the track width is relative or absolute
        if (value.contains("%")) {
          spaceView.setRelativeTrackWidth(Double.valueOf(value.substring(0,
              value.length() - 1)));
        } else if (value.contains("px")) {
          spaceView.setAbsoluteTrackWidth(Double.valueOf(value.substring(0,
              value.length() - 2)));
        }
      }
    }

    return spaceView;
  }

  /**
   * Returns a two dimensional, discrete space view as read from the XML
   * simulation description
   * 
   * @param node
   */
  private SpaceView getGridSpaceView(Node node) {
    // Create the GridSpaceView instance
    GridSpaceView spaceView = new GridSpaceView();

    // Retrieve the attributes of the node
    NamedNodeMap attributes = node.getAttributes();

    // Color flags
    boolean fill1Set = false;
    boolean fill2Set = false;
    boolean strokeSet = false;
    boolean backgroundColorSet = false;

    /*
     * Retrieve the name and value of each attribute and initializes the class
     * members accordingly.
     */
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      // Check which attributes where found and initialize the members
      if (name.equals(GridSpaceView.FILL1)) {
        spaceView.setFill1(new Color(value));
        fill1Set = true;
      }

      else if (name.equals(GridSpaceView.FILL2)) {
        spaceView.setFill2(new Color(value));
        fill2Set = true;
      }

      else if (name.equals(GridSpaceView.BACKGROUND_COLOR)) {
        spaceView.setBackgroundColor(new Color(value));
        backgroundColorSet = true;
      }

      else if (name.equals(GridSpaceView.BACKGROUND_IMG)) {
        spaceView.setBackgroundImgFilename(value);
      }

      else if (name.equals(GridSpaceView.STROKE)) {
        spaceView.setStroke(new Color(value));
        strokeSet = true;
      }

      else if (name.equals(GridSpaceView.STROKE_WIDTH)) {
        // Check if the stroke width is relative or absolute
        if (value.contains("%")) {
          spaceView.setRelativeStrokeWidth(Double.valueOf(value.substring(0,
              value.length() - 1)));
        } else if (value.contains("px")) {
          spaceView.setAbsoluteStrokeWidth(Double.valueOf(value.substring(0,
              value.length() - 2)));
        }
      }

      else if (name.equals(GridSpaceView.FILL1_RGB) && !fill1Set) {
        spaceView.setFill1(new Color(value));
      }

      else if (name.equals(GridSpaceView.FILL2_RGB) && !fill2Set) {
        spaceView.setFill2(new Color(value));
      }

      else if (name.equals(GridSpaceView.STROKE_RGB) && !strokeSet) {
        spaceView.setStroke(new Color(value));
      }

      else if (name.equals(GridSpaceView.BACKGROUND_COLOR_RGB)
          && !backgroundColorSet) {
        spaceView.setBackgroundColor(new Color(value));
      }
    }

    return spaceView;
  }

  /**
   * Returns a two dimensional, continuous space view as read from the XML
   * simulation description.
   */
  private SpaceView getTwoDimSpaceView(Node node) {
    // Create a TwoDimSpaceView instance
    TwoDimSpaceView spaceView = new TwoDimSpaceView();

    // Retrieve the attributes of the node
    NamedNodeMap attributes = node.getAttributes();

    // Color flags
    boolean backgroundColorSet = false;
    boolean borderColorSet = false;

    /*
     * Retrieve the name and value of each attribute and initializes the class
     * members accordingly.
     */
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      // Check which attributes where found and initialize the members
      if (name.equals(TwoDimSpaceView.BACKGROUND_COLOR)) {
        spaceView.setBackgroundColor(new Color(value));
        backgroundColorSet = true;
      }

      else if (name.equals(TwoDimSpaceView.BACKGROUND_IMG)) {
        spaceView.setBackgroundImgFilename(value);
      }

      else if (name.equals(TwoDimSpaceView.BORDER_COLOR)) {
        spaceView.setBorderColor(new Color(value));
        borderColorSet = true;
      }

      else if (name.equals(TwoDimSpaceView.BACKGROUND_COLOR_RGB)
          && !backgroundColorSet) {
        spaceView.setBackgroundColor(new Color(value));
      }

      else if (name.equals(TwoDimSpaceView.BORDER_COLOR_RGB) && !borderColorSet) {
        spaceView.setBorderColor(new Color(value));
      }
    }
    
    readGlobalCamera(node, spaceView);
    
    return spaceView;
  }

  /**
   * Returns, with respect to the space model type, a default space view.
   * 
   * @param spaceType
   */
  private SpaceView getDefaultSpaceView(SpaceType spaceType) {
    if (spaceType.equals(SpaceType.OneD)) {
      return new OneDimSpaceView();
    }

    else if (spaceType.equals(SpaceType.TwoDGrid)) {
      return new GridSpaceView();
    }

    else if (spaceType.equals(SpaceType.TwoD)
        || spaceType.equals(SpaceType.TwoDLateralView)) {
      return new TwoDimSpaceView();
    }

    else {
      return null;
    }
  }
  
  /*
   * Returns skybox of the SpaceView3D as read from the XML
   * simulation description.
   * 
   * @param node SpaceView3D node
   * 
   * @return skybox
   */
  private Skybox getSkybox(Node node) {
	
	Skybox skybox = null;
	
	// Retrieve child nodes of the "SpaceView3D" node
	NodeList childNodes = node.getChildNodes();

	// Search for Skybox node and if necessary initialize Skybox
	for (int i = 0; i < childNodes.getLength(); i++) {
	  if (childNodes.item(i).getNodeName().equals(Skybox.SKYBOX)) {
	    skybox = new Skybox();
	    // Retrieve the attributes of a "Skybox" node
	    NamedNodeMap skyboxAttributes = childNodes.item(i).getAttributes();
	        
	    /*
	     * Retrieve the name and value of each attribute and initialize the class
	     * members accordingly.
	     */
	    for (int j = 0; j < skyboxAttributes.getLength(); j++) {
	      String name = skyboxAttributes.item(j).getNodeName();
	      String value = skyboxAttributes.item(j).getNodeValue();

	      // Check which attributes where found and initialize the members
	      if (name.equals(Skybox.TOP))
	        skybox.setTextureFilename(Face.top, value);

	      else if (name.equals(Skybox.BOTTOM)) {
	        skybox.setTextureFilename(Face.bottom, value);
	      }
	          
	      else if (name.equals(Skybox.LEFT)) {
	        skybox.setTextureFilename(Face.left, value);
	      }
	          
	      else if (name.equals(Skybox.RIGHT)) {
	        skybox.setTextureFilename(Face.right, value);
	      }
	          
	      else if (name.equals(Skybox.FRONT)) {
	        skybox.setTextureFilename(Face.front, value);
	      }
	          
	      else if (name.equals(Skybox.BACK)) {
	        skybox.setTextureFilename(Face.back, value);
	      }
	        	  
	    }
	  }
	}
	
	return skybox;
  }
  
  
  /*
   * Sets the global camera as read from the XML simulation description.
   * 
   * @param node SpaceView3D node
   * 
   */
  private void readGlobalCamera(Node node, TwoDimSpaceView spaceView) {
	
	// Retrieve child nodes of the "SpaceView3D" node
	NodeList childNodes = node.getChildNodes();

	// Search for GlobalCamera node and if necessary initialize the global camera parameters eyePosition, lookAt and upVector in TwoDimSpaceView
	for (int i = 0; i < childNodes.getLength(); i++) {
	  if (childNodes.item(i).getNodeName().equals(TwoDimSpaceView.GLOBAL_CAMERA)) {
	    
		spaceView.setHasGlobalCameraPosition(true);  
		  
	    // Retrieve the attributes of a "GlobalCamera" node
	    NamedNodeMap globalCameraAttributes = childNodes.item(i).getAttributes();
	        
	    /*
	     * Retrieve the name and value of each attribute and initialize the class
	     * members accordingly.
	     */
	    for (int j = 0; j < globalCameraAttributes.getLength(); j++) {
	      String name = globalCameraAttributes.item(j).getNodeName();
	      String value = globalCameraAttributes.item(j).getNodeValue();
	      value = value.trim();

	      // Check which attributes where found and initialize the members
	      if (name.equals(TwoDimSpaceView.EYE_POSITION)) {
	        double[] eyePosition = new double[3];
	        
	        String[] splittedString = value.split(" ");
	        
	        int count = 0;
	        for (int k=0; k<splittedString.length; k++) {
	        	if(!splittedString[k].equals("") && count <= 2) {
	        		eyePosition[count] = Double.valueOf(splittedString[k]);
	        		count++;
	        	}
	        }
	        
	        spaceView.setEyePosition(eyePosition);
	      }

	      else if (name.equals(TwoDimSpaceView.LOOK_AT)) {
	    	double[] lookAt = new double[3];
		        
		    String[] splittedString = value.split(" ");
		        
		    int count = 0;
		    for (int k=0; k<splittedString.length; k++) {
		        if(!splittedString[k].equals("") && count <= 2) {
		        	lookAt[count] = Double.valueOf(splittedString[k]);
		        	count++;
		        }
		    }
		    
		    spaceView.setLookAt(lookAt);
	      }
	          
	      else if (name.equals(TwoDimSpaceView.UP_VECTOR)) {
	    	double[] upVector = new double[3];
		        
		    String[] splittedString = value.split(" ");
		        
		    int count = 0;
		    for (int k=0; k<splittedString.length; k++) {
		        if(!splittedString[k].equals("") && count <= 2) {
		        	upVector[count] = Double.valueOf(splittedString[k]);
		        	count++;
		        }
		    }
		    
		    spaceView.setUpVector(upVector);
	      }	  
	    }
	  }
	}

  }

  /**
   * Returns an association of a node to an ID or type. The node is an element
   * inside of the simulation description, where the view definition for a
   * certain ID or type can be found.
   */
  public Map<String, Node> readViews() {
    Map<String, Node> map = new HashMap<String, Node>();

    // Search for the "Views" node
    Node views = searchViewsNode();

    if (views != null) {
      // Retrieve child nodes of the "Views" node
      NodeList childNodes = views.getChildNodes();

      for (int k = 0; k < childNodes.getLength(); k++) {
        // Search for the "PhysicalObjectView" nodes inside "Views"
        if (childNodes.item(k).getNodeName().equals(View.PHYSICAL_OBJECT_VIEW)) {
          addPhysicalObjectViewNode(childNodes.item(k), map);
        }

        // Search for the "ObjectView" node inside "Views"
        if (childNodes.item(k).getNodeName().equals(View.OBJECT_VIEW)) {
          addObjectViewNode(childNodes.item(k), map);
        }
      }
    }

    return map;
  }

  /**
   * Adds an physical object view node into a hash map together with its
   * associated ID or type.
   * 
   * @param node
   * @param map
   */
  private void addPhysicalObjectViewNode(Node node, Map<String, Node> map) {
    // Keys
    String type = null;
    long startID = 0;
    long endID = 0;
    long id = 0;

    // Flags signaling that a key was found
    boolean foundKeyByID = false;
    boolean foundKeyByRange = false;
    boolean foundKeyByType = false;

    // Retrieve attributes of "PhysicalObjectView"
    NamedNodeMap attributes = node.getAttributes();

    // Search for the key inside the attributes (order of priority: ID,
    // Range,
    // Type)
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(View.PHYSICAL_OBJECT_TYPE)) {
        type = value;
        foundKeyByType = true;
      }

      if (name.equals(View.PHYSICAL_OBJECT_START_ID)) {
        startID = Long.valueOf(value);
      }

      if (name.equals(View.PHYSICAL_OBJ_END_ID)) {
        endID = Long.valueOf(value);
        foundKeyByRange = true;
      }

      if (name.equals(View.PHYSICAL_OBJECT_ID_REF)) {
        id = Long.valueOf(value);
        foundKeyByID = true;
      }
    }

    // Add the node into the hash map
    if (foundKeyByID) {
      map.put(Long.toString(id), node);
    } else if (foundKeyByRange) {
      for (long i = startID; i <= endID; i++) {
        map.put(Long.toString(i), node);
      }
    } else if (foundKeyByType) {
      map.put(type, node);
    }
  }

  /**
   * Adds an object view node into a hash map together with its associated ID or
   * type.
   * 
   * @param node
   * @param map
   */
  private void addObjectViewNode(Node node, Map<String, Node> map) {
    // Keys
    String type = null;
    long startID = 0;
    long endID = 0;
    long id = 0;

    // Flags signaling that a key was found
    boolean foundKeyByID = false;
    boolean foundKeyByRange = false;
    boolean foundKeyByType = false;

    // Retrieve attributes of "ObjectView"
    NamedNodeMap attributes = node.getAttributes();

    // Search for the key inside the attributes (order of priority: ID,
    // Range,
    // Type)
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(View.OBJECT_TYPE)) {
        type = value;
        foundKeyByType = true;
      }

      if (name.equals(View.OBJECT_START_ID)) {
        startID = Long.valueOf(value);
      }

      if (name.equals(View.OBJECT_END_ID)) {
        endID = Long.valueOf(value);
        foundKeyByRange = true;
      }

      if (name.equals(View.OBJECT_ID_REF)) {
        id = Long.valueOf(value);
        foundKeyByID = true;
      }
    }

    // Add the node into the hash map
    if (foundKeyByID) {
      map.put(Long.toString(id), node);
    } else if (foundKeyByRange) {
      for (long i = startID; i <= endID; i++) {
        map.put(Long.toString(i), node);
      }
    } else if (foundKeyByType) {
      map.put(type, node);
    }
  }

  /**
   * Returns a view that was defined by a PhysicalObjectView node.
   * 
   * @param node
   */
  public View readPhysicalObjectView(Node node) {
    View view = null;
    Shape3D topLevelShape = null;
    ArrayList<View> attachedList = new ArrayList<View>();
    DisplayInfo displayInfo = new DisplayInfo();
    Shape3DMap shape3DMap = null;

    // Retrieve all attributes of the "PhysicalObjectView" node
    NamedNodeMap attributes = node.getAttributes();

    for (int j = 0; j < attributes.getLength(); j++) {
      String name = attributes.item(j).getNodeName();
      String value = attributes.item(j).getNodeValue();

      if (name.equals(DisplayInfo.DISPLAY_ID)) {
        displayInfo.setDisplayID(Boolean.valueOf(value));
        displayInfo.setEnabled(true);
      }

      else if (name.equals(DisplayInfo.DISPLAY_NAME)) {
        displayInfo.setDisplayName(Boolean.valueOf(value));
        displayInfo.setEnabled(true);
      }
    }

    // Retrieve child nodes of "PhysicalObjectView"
    NodeList childNodes = node.getChildNodes();

    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals(Shape3D.PHYSICAL_SHAPE_3D)) {
        // Read a "PhysicalShape3D" node
        topLevelShape = readPhysicalShape3D(childNodes.item(i));
      }

      if (childNodes.item(i).getNodeName().equals(DisplayInfo.DISPLAY_INFO)) {
        // Read a "DisplayInfo" node
        readDisplayInfo(childNodes.item(i), displayInfo);
      }

      if (childNodes.item(i).getNodeName().equals(
          Shape3DMap.PHYSICAL_SHAPE3D_MAP)) {
        // Read a "PhysicalShape3DMap" node
        shape3DMap = readShape3DMap(childNodes.item(i));
      }
    }

    if (topLevelShape != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.ATTACHED_SHAPE)) {
          // Get attached shapes
          attachedList.add(getAttachedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape3D(topLevelShape);
      view.setAttachedList(attachedList);
      view.setDisplayInfo(displayInfo);
    }

    if (shape3DMap != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.ATTACHED_SHAPE)) {
          // Get attached shapes
          attachedList.add(getAttachedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape3DMap(shape3DMap);
      view.setAttachedList(attachedList);
      view.setDisplayInfo(displayInfo);
    }

    return view;
  }

  private void readDisplayInfo(Node node, DisplayInfo info) {
    // Retrieve all attributes of the "DisplayInfo" node
    NamedNodeMap attributes = node.getAttributes();

    for (int j = 0; j < attributes.getLength(); j++) {
      String name = attributes.item(j).getNodeName();
      String value = attributes.item(j).getNodeValue();

      if (name.equals(DisplayInfo.CONTENT)) {
        info.setContent(value);
        info.setEnabled(true);
      }

      else if (name.equals(DisplayInfo.PROPERTY)) {
        info.setProperty(value);
        info.setEnabled(true);
      }
    }
  }

  /**
   * Reads a shape definition from a "PhysicalShape3D" node.
   * 
   * @param node
   */
  private Shape3D readPhysicalShape3D(Node node) {
    Shape3D shape = null;

    // Retrieve child nodes of "PhysicalShape3D"
    NodeList childNodes = node.getChildNodes();

    // Search for the actual shape definitions
    for (int i = 0; i < childNodes.getLength(); i++) {
      // Found "Cube" node
      if (childNodes.item(i).getNodeName().equals(Shape3D.CUBE)) {
        // Read all data from "Cube" node
        shape = readCube(childNodes.item(i));
      }

      // Found "Cuboid" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.CUBOID)) {
        // Read all data from "Cuboid" node
        shape = readCuboid(childNodes.item(i));
      }

      // Found "Cone" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.CONE)) {
        // Read all data from "Cone" node
        shape = readCone(childNodes.item(i));
      }

      // Found "Cylinder" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.CYLINDER)) {
        // Read all data from "Cylinder" node
        shape = readCylinder(childNodes.item(i));
      }

      // Found "Mesh" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.MESH)) {
        // Read all data from "Mesh" node
        shape = readMesh(childNodes.item(i));
      }

      // Found "Pyramid" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.PYRAMID)) {
        // Read all data from "Pyramid" node
        shape = readPyramid(childNodes.item(i));
      }

      // Found "RegularTriangularPrism" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.REGULAR_TRIANGULAR_PRISM)) {
        // Read all data from "RegularTriangularPrism" node
        shape = readRegularTriangularPrism(childNodes.item(i));
      }

      // Found "Sphere" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.SPHERE)) {
        // Read all data from "Sphere" node
        shape = readSphere(childNodes.item(i));
      }
        
      // Found "Tetrahedra" node
      else if (childNodes.item(i).getNodeName().equals(Shape3D.TETRAHEDRA)) {
        // Read all data from "Tetrahedra" node
        shape = readTetrahedra(childNodes.item(i));
      }
    }

    return shape;
  }

  /**
   * Returns a view that was defined by an ObjectView node.
   * 
   * @param node
   */
  public View readObjectView(Node node) {
    View view = null;
    Shape3D topLevelShape = null;
    ArrayList<View> attachedList = new ArrayList<View>();
    DisplayInfo displayInfo = new DisplayInfo();
    Shape3DMap shape3DMap = null;

    // Retrieve all attributes of the "ObjectView" node
    NamedNodeMap attributes = node.getAttributes();

    for (int j = 0; j < attributes.getLength(); j++) {
      String name = attributes.item(j).getNodeName();
      String value = attributes.item(j).getNodeValue();

      if (name.equals(DisplayInfo.DISPLAY_ID)) {
        displayInfo.setDisplayID(Boolean.valueOf(value));
        displayInfo.setEnabled(true);
      }

      else if (name.equals(DisplayInfo.DISPLAY_NAME)) {
        displayInfo.setDisplayName(Boolean.valueOf(value));
        displayInfo.setEnabled(true);
      }
    }

    // Retrieve child nodes of "ObjectView"
    NodeList childNodes = node.getChildNodes();

    // Search for the "Shape3D" node inside "ObjectView"
    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals(Shape3D.SHAPE_3D)) {
        // Read a "Shape3D" node
        topLevelShape = readShape3D(childNodes.item(i));
      }

      if (childNodes.item(i).getNodeName().equals(DisplayInfo.DISPLAY_INFO)) {
    	// Read a "DisplayInfo" node
        readDisplayInfo(childNodes.item(i), displayInfo);
      }

      if (childNodes.item(i).getNodeName().equals(Shape3DMap.SHAPE3D_MAP)) {
        // Read a "Shape3dVisualizationMap" node
        shape3DMap = readShape3DMap(childNodes.item(i));
      }
    }

    if (topLevelShape != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.ATTACHED_SHAPE)) {
          // Get attached shapes
          attachedList.add(getAttachedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape3D(topLevelShape);
      view.setAttachedList(attachedList);
      view.setDisplayInfo(displayInfo);
    }

    if (shape3DMap != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.ATTACHED_SHAPE)) {
          // Get attached shapes
          attachedList.add(getAttachedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape3DMap(shape3DMap);
      view.setAttachedList(attachedList);
      view.setDisplayInfo(displayInfo);
    }

    return view;
  }

  /**
   * Reads a shape definition from a "Shape3D" node.
   * 
   * @param node
   */
  private Shape3D readShape3D(Node node) {
    Shape3D shape = null;

    // Retrieve child nodes of "Shape3D"
    NodeList childNodes = node.getChildNodes();

    // Search for the actual shape definitions
    for (int j = 0; j < childNodes.getLength(); j++) {
      // Found "Cube" node
      if (childNodes.item(j).getNodeName().equals(Shape3D.CUBE)) {
        // Read all data from "Cube" node
        shape = readCube(childNodes.item(j));
      }

      // Found "Cuboid" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.CUBOID)) {
        // Read all data from "Cuboid" node
        shape = readCuboid(childNodes.item(j));
      }

      // Found "Cone" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.CONE)) {
        // Read all data from "Cone" node
        shape = readCone(childNodes.item(j));
      }

      // Found "Cylinder" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.CYLINDER)) {
        // Read all data from "Cylinder" node
        shape = readCylinder(childNodes.item(j));
      }

      // Found "Mesh" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.MESH)) {
        // Read all data from "Mesh" node
        shape = readMesh(childNodes.item(j));
      }

      // Found "Pyramid" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.PYRAMID)) {
        // Read all data from "Pyramid" node
        shape = readPyramid(childNodes.item(j));
      }

      // Found "RegularTriangularPrism" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.REGULAR_TRIANGULAR_PRISM)) {
        // Read all data from "RegularTriangularPrism" node
        shape = readRegularTriangularPrism(childNodes.item(j));
      }

      // Found "Sphere" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.SPHERE)) {
        // Read all data from "Sphere" node
        shape = readSphere(childNodes.item(j));
      }
      
      // Found "Tetrahedra" node
      else if (childNodes.item(j).getNodeName().equals(Shape3D.TETRAHEDRA)) {
        // Read all data from "Tetrahedra" node
        shape = readTetrahedra(childNodes.item(j));
      }
    }

    if (shape != null) {
      // Retrieve all attributes of the "Shape3D" node
      NamedNodeMap attributes = node.getAttributes();

      for (int j = 0; j < attributes.getLength(); j++) {
        String name = attributes.item(j).getNodeName();
        String value = attributes.item(j).getNodeValue();

        if (name.equals(Shape3D.X)) {
          // Check if the position is relative or absolute
          if (value.contains("%")) {
            shape.setX(Double.valueOf(value.substring(0, value.length() - 1)));
            shape.setRelativeX(Double.valueOf(value.substring(0,
                value.length() - 1)));
            shape.setxRelative(true);
          } else if (value.contains("px")) {
            shape.setX(Double.valueOf(value.substring(0, value.length() - 2)));
          } else {
            shape.setX(Double.valueOf(value));
          }
        }

        else if (name.equals(Shape3D.Y)) {
          // Check if the position is relative or absolute
          if (value.contains("%")) {
            shape.setY(Double.valueOf(value.substring(0, value.length() - 1)));
            shape.setRelativeY(Double.valueOf(value.substring(0,
                value.length() - 1)));
            shape.setyRelative(true);
          } else if (value.contains("px")) {
            shape.setY(Double.valueOf(value.substring(0, value.length() - 2)));
          } else {
            shape.setY(Double.valueOf(value));
          }
        }
        
        else if (name.equals(Shape3D.Z)) {
          // Check if the position is relative or absolute
          if (value.contains("%")) {
            shape.setZ(Double.valueOf(value.substring(0, value.length() - 1)));
            shape.setRelativeZ(Double.valueOf(value.substring(0,
                value.length() - 1)));
             shape.setzRelative(true);
          } else if (value.contains("px")) {
            shape.setZ(Double.valueOf(value.substring(0, value.length() - 2)));
          } else {
            shape.setZ(Double.valueOf(value));
          }
        }
        
        else if (name.equals(Shape3D.ROT_X))
          shape.setRotX(Double.valueOf(value));
        
        else if (name.equals(Shape3D.ROT_Y))
          shape.setRotY(Double.valueOf(value));
               
        else if (name.equals(Shape3D.ROT_Z))
          shape.setRotZ(Double.valueOf(value));
        
      }
    }

    return shape;
  }

  /**
   * Reads common shape attributes.
   * 
   * @param node
   * @param shape
   */
  private void readShapeAttributes(Node node, Shape3D shape) {
    // Retrieve all attributes
    NamedNodeMap attributes = node.getAttributes();

    // Color flag
    boolean fillSet = false;

    for (int i = 0; i < attributes.getLength(); i++) {
      // Get attribute name and value
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(Shape3D.FILL)) {
        shape.setFill(new Color(value));
        fillSet = true;
      }
      
      else if (name.equals(Shape3D.FILL_RGB) && !fillSet) {
        shape.setFill(new Color(value));
      }

      else if (name.equals(Shape3D.FILL_OPACITY))
        shape.setFillOpacity(Double.valueOf(value));

      else if (name.equals(Shape3D.TEXTURE)) {
        shape.setTextureFilename(value);
      }

      else if (name.equals(Shape3D.FILE)) {
        shape.setMeshFilename(value);
      }
      
      else if (name.equals(Shape3D.WIDTH)) {
        // Check if the width is relative or absolute
        if (value.contains("%")) {
          shape
              .setWidth(Double.valueOf(value.substring(0, value.length() - 1)));
          shape.setRelativeWidth(Double.valueOf(value.substring(0, value
              .length() - 1)));
          shape.setWidthRelative(true);
        } else if (value.contains("px")) {
          shape
              .setWidth(Double.valueOf(value.substring(0, value.length() - 2)));
        } else {
          shape.setWidth(Double.valueOf(value));
        }
      }

      else if (name.equals(Shape3D.HEIGHT)) {
        // Check if the height is relative or absolute
        if (value.contains("%")) {
          shape.setHeight(Double
              .valueOf(value.substring(0, value.length() - 1)));
          shape.setRelativeHeight(Double.valueOf(value.substring(0, value
              .length() - 1)));
          shape.setHeightRelative(true);
        } else if (value.contains("px")) {
          shape.setHeight(Double
              .valueOf(value.substring(0, value.length() - 2)));
        } else {
          shape.setHeight(Double.valueOf(value));
        }
      }
      
      else if (name.equals(Shape3D.DEPTH)) {
        // Check if the depth is relative or absolute
        if (value.contains("%")) {
          shape.setDepth(Double
              .valueOf(value.substring(0, value.length() - 1)));
          shape.setRelativeDepth(Double.valueOf(value.substring(0, value
              .length() - 1)));
          shape.setDepthRelative(true);
        } else if (value.contains("px")) {
          shape.setDepth(Double
              .valueOf(value.substring(0, value.length() - 2)));
        } else {
          shape.setDepth(Double.valueOf(value));
        }
      }

      else if (name.equals(Shape3D.R)) {
        // Check if the value is relative or absolute
        if (value.contains("%")) {
          double radius = Double
              .valueOf(value.substring(0, value.length() - 1));
          shape.setWidth(radius * 2);
          shape.setRelativeWidth(radius * 2);
          shape.setWidthRelative(true);
        } else if (value.contains("px")) {
          double radius = Double
              .valueOf(value.substring(0, value.length() - 2));
          shape.setWidth(radius * 2);
        } else {
          shape.setWidth(Double.valueOf(value) * 2);
        }
      }
    }

    // Read possible shape property maps
    shape.setPropertyMaps(getPropertyMaps(node));
  }

  /**
   * Reads all data from a "Cube" node.
   * 
   * @param node
   */
  private Shape3D readCube(Node node) {
    // Create a new Cube instance
    Cube cube = new Cube();

    // Get shape attributes
    readShapeAttributes(node, cube);

    return cube;
  }
  
  /**
   * Reads all data from a "Cuboid" node.
   * 
   * @param node
   */
  private Shape3D readCuboid(Node node) {
    // Create a new Cuboid instance
    Cuboid cuboid = new Cuboid();

    // Get shape attributes
    readShapeAttributes(node, cuboid);

    return cuboid;
  }
  
  /**
   * Reads all data from a "Cone" node.
   * 
   * @param node
   */
  private Shape3D readCone(Node node) {
    // Create a new Cone instance
    Cone cone = new Cone();

    // Get shape attributes
    readShapeAttributes(node, cone);

    return cone;
  }
  
  /**
   * Reads all data from a "Cylinder" node.
   * 
   * @param node
   */
  private Shape3D readCylinder(Node node) {
    // Create a new Cylinder instance
	Cylinder cylinder = new Cylinder();

    // Get shape attributes
    readShapeAttributes(node, cylinder);

    return cylinder;
  }
  
  /**
   * Reads all data from a "Mesh" node.
   * 
   * @param node
   */
  private Shape3D readMesh(Node node) {
    // Create a new Mesh instance
	Mesh mesh = new Mesh();

    // Get shape attributes
    readShapeAttributes(node, mesh);

    return mesh;
  }
  
  /**
   * Reads all data from a "Pyramid" node.
   * 
   * @param node
   */
  private Shape3D readPyramid(Node node) {
    // Create a new Pyramid instance
	Pyramid pyramid = new Pyramid();

    // Get shape attributes
    readShapeAttributes(node, pyramid);

    return pyramid;
  }
  
  /**
   * Reads all data from a "RegularTriangularPrism" node.
   * 
   * @param node
   */
  private Shape3D readRegularTriangularPrism(Node node) {
    // Create a new RegularTriangularPrism instance
	RegularTriangularPrism regularTriangularPrism = new RegularTriangularPrism();

    // Get shape attributes
    readShapeAttributes(node, regularTriangularPrism);

    return regularTriangularPrism;
  }
  
  /**
   * Reads all data from a "Sphere" node.
   * 
   * @param node
   */
  private Shape3D readSphere(Node node) {
    // Create a new Sphere instance
	Sphere sphere = new Sphere();

    // Get shape attributes
    readShapeAttributes(node, sphere);

    return sphere;
  }
  
  /**
   * Reads all data from a "Tetrahedra" node.
   * 
   * @param node
   */
  private Shape3D readTetrahedra(Node node) {
    // Create a new Tetrahedra instance
	Tetrahedra tetrahedra = new Tetrahedra();

    // Get shape attributes
    readShapeAttributes(node, tetrahedra);

    return tetrahedra;
  }

  /**
   * Returns a PropertyMap list. Property maps can be retrieved for two
   * dimensional, discrete space models and for shapes.
   * 
   * @param node
   */
  private ArrayList<PropertyMap> getPropertyMaps(Node node) {
    // PropertyMap list
    ArrayList<PropertyMap> propertyMaps = new ArrayList<PropertyMap>();

    // Retrieve child nodes of node
    NodeList childNodes = node.getChildNodes();

    if (childNodes.getLength() > 0) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        /*
         * Search for the GridCellPropertyVisualizationMap or ShapePropertyVisualizationMap
         * nodes.
         */
        if (childNodes.item(i).getNodeName().equals(
            PropertyMap.GRID_PROPERTY_MAP)
            || childNodes.item(i).getNodeName().equals(
                ShapePropertyVisualizationMap.SHAPE_PROPERTY_MAP)) {
          // Create a PropertyMap instance
          PropertyMap pm = new PropertyMap();

          /*
           * Retrieve the attributes of a GridCellPropertyVisualizationMap or
           * ShapePropertyVisualizationMap node.
           */
          NamedNodeMap attributes = childNodes.item(i).getAttributes();

          /*
           * Retrieve the name and value of each attribute and initialize the
           * class members accordingly.
           */
          for (int j = 0; j < attributes.getLength(); j++) {
            String name = attributes.item(j).getNodeName();
            String value = attributes.item(j).getNodeValue();

            // Check which attributes where found and initialize the
            // members
            if (name.equals(PropertyMap.CELL_PROPERTY))
              pm.setVisualPropertyName(value);

            else if (name.equals(PropertyMap.SHAPE_PROPERTY))
              pm.setVisualPropertyName(value);

            else if (name.equals(PropertyMap.PROPERTY))
              pm.setPropertyName(value);

            else if (name.equals(PropertyMap.MAP_TYPE)) {
              if (value.equals("caseWise"))
                pm.setMapping(MapType.caseWise);
              else if (value.equals("enumerationMap"))
                pm.setMapping(MapType.enumerationMap);
              else if (value.equals("equalityCaseWise"))
                pm.setMapping(MapType.equalityCaseWise);
              else if (value.equals("polynomial"))
                pm.setMapping(MapType.polynomial);
            }

            else if (name.equals(PropertyMap.A0))
              pm.setA0(value);

            else if (name.equals(PropertyMap.A1))
              pm.setA1(value);

            else if (name.equals(PropertyMap.A2))
              pm.setA2(value);

            else if (name.equals(PropertyMap.A3))
              pm.setA3(value);

            else if (name.equals(PropertyMap.V0))
              pm.setV0(value);

            else if (name.equals(PropertyMap.V1))
              pm.setV1(value);

            else if (name.equals(PropertyMap.V2))
              pm.setV2(value);

            else if (name.equals(PropertyMap.V3))
              pm.setV3(value);

            else if (name.equals(PropertyMap.V4))
              pm.setV4(value);
          }

          // Add the shape property map into the list
          propertyMaps.add(pm);
        }
      }
    }

    return propertyMaps;
  }

  /**
   * Reads the definition of an attached shape.
   * 
   * @param node
   */
  private View getAttachedShape(Node node) {
    View attachedView = new View();

    ArrayList<View> attachedList = new ArrayList<View>();

    double offsetX = 0;
    double offsetY = 0;
    double offsetZ = 0;
    boolean offsetXRelative = false;
    boolean offsetYRelative = false;
    boolean offsetZRelative = false;

    /*
     * Retrieve the attributes of the "AttachedShape3D" node
     */
    NamedNodeMap attributes = node.getAttributes();

    /*
     * Retrieve the name and value of each attribute and initialize the class
     * members accordingly.
     */
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(Shape3D.OFFSET_X)) {
        if (value.contains("%")) {
          offsetX = Double.valueOf(value.substring(0, value.length() - 1));
          offsetXRelative = true;
        } else if (value.contains("px")) {
          offsetX = Double.valueOf(value.substring(0, value.length() - 2));
        } else {
          offsetX = Double.valueOf(value);
        }
      }

      else if (name.equals(Shape3D.OFFSET_Y)) {
        if (value.contains("%")) {
          offsetY = Double.valueOf(value.substring(0, value.length() - 1));
          offsetYRelative = true;
        } else if (value.contains("px")) {
          offsetY = Double.valueOf(value.substring(0, value.length() - 2));
        } else {
          offsetY = Double.valueOf(value);
        }
      }
      
      else if (name.equals(Shape3D.OFFSET_Z)) {
        if (value.contains("%")) {
          offsetZ = Double.valueOf(value.substring(0, value.length() - 1));
          offsetZRelative = true;
        } else if (value.contains("px")) {
          offsetZ = Double.valueOf(value.substring(0, value.length() - 2));
        } else {
          offsetZ = Double.valueOf(value);
        }
      }

      else if (name.equals(View.ATTACHED_SHAPE_LABEL)) {
        attachedView.setAttachedLabel(value);
      }
    }

    // Retrieve child nodes of "AttachedShape3D"
    NodeList childNodes = node.getChildNodes();

    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals(Shape3D.PHYSICAL_SHAPE_3D)) {
        attachedView.setShape3D(readPhysicalShape3D(childNodes.item(i)));
      }

      if (childNodes.item(i).getNodeName().equals(Shape3D.SHAPE_3D)) {
        attachedView.setShape3D(readShape3D(childNodes.item(i)));
      }

      if (childNodes.item(i).getNodeName().equals(
          Shape3DMap.PHYSICAL_SHAPE3D_MAP)
          || childNodes.item(i).getNodeName().equals(Shape3DMap.SHAPE3D_MAP)) {
        attachedView.setShape3DMap(readShape3DMap(childNodes.item(i)));
      }

      if (childNodes.item(i).getNodeName().equals(View.ATTACHED_SHAPE)) {
        attachedList.add(getAttachedShape(childNodes.item(i)));
      }
    }

    if (attachedView.getShape3DMap() != null) {
      Collection<Shape3D> collection = attachedView.getShape3DMap().getMap()
          .values();

      for (Shape3D shape : collection) {
        shape.setOffsetX(offsetX);
        shape.setOffsetY(offsetY);
        shape.setOffsetZ(offsetZ);
        shape.setOffsetXRelative(offsetXRelative);
        shape.setOffsetYRelative(offsetYRelative);
        shape.setOffsetZRelative(offsetZRelative);
      }
    }

    else if (attachedView.getShape() != null) {
      attachedView.getShape().setOffsetX(offsetX);
      attachedView.getShape().setOffsetY(offsetY);
      attachedView.getShape().setOffsetZ(offsetZ);
      attachedView.getShape().setOffsetXRelative(offsetXRelative);
      attachedView.getShape().setOffsetYRelative(offsetYRelative);
      attachedView.getShape().setOffsetZRelative(offsetZRelative);
    }

    attachedView.setAttachedList(attachedList);

    return attachedView;
  }

  /*public ArrayList<double[]> readPointString(String str) {
    ArrayList<double[]> pointList = new ArrayList<double[]>();

    // Check if the point string is valid
    if (!str
        .matches("[-]?[\\d]+([.]){0,1}[\\d]*[,][-]?[\\d]+([.]){0,1}[\\d]*([,][-]?[\\d]+([.]){0,1}[\\d]*){0,1}([\\s]{1}[-]?[\\d]+([.]){0,1}[\\d]*[,][-]?[\\d]+([.]){0,1}[\\d]*([,][-]?[\\d]+([.]){0,1}[\\d]*){0,1})+")) {
      System.out
          .println("Visualization Error: Point description is not valid!");
      return null;
    }

    // Determine the number of points specified
    int numberOfPoints = 1;

    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == ' ') {
        numberOfPoints++;
      }
    }

    int startA = 0;
    int endA = 0;

    for (int i = 0; i < numberOfPoints; i++) {
      double[] vertex = new double[3];
      vertex[2] = 0;

      if (i < numberOfPoints - 1) {
        // Get the substring for one point
        endA = str.indexOf(' ', startA);
        String pointStr = str.substring(startA, endA);
        startA = endA + 1;

        // Get the number of components of one point
        int numberOfComponents = 1;

        for (int j = 0; j < pointStr.length(); j++) {
          if (pointStr.charAt(j) == ',') {
            numberOfComponents++;
          }
        }

        // Get each components value
        int startB = 0;
        int endB = 0;

        for (int j = 0; j < numberOfComponents; j++) {
          if (j < numberOfComponents - 1) {
            endB = pointStr.indexOf(',', startB);
            vertex[j] = Double.valueOf(pointStr.substring(startB, endB));
            startB = endB + 1;
          } else {
            vertex[j] = Double.valueOf(pointStr.substring(startB));
          }
        }

        // Add the vertex to the list
        pointList.add(vertex);
      } else {
        // Get the substring for the last point
        String pointStr = str.substring(startA);

        // Get the number of components of one point
        int numberOfComponents = 1;

        for (int j = 0; j < pointStr.length(); j++) {
          if (pointStr.charAt(j) == ',') {
            numberOfComponents++;
          }
        }

        // Get each components value
        int startB = 0;
        int endB = 0;

        for (int j = 0; j < numberOfComponents; j++) {
          if (j < numberOfComponents - 1) {
            endB = pointStr.indexOf(',', startB);
            vertex[j] = Double.valueOf(pointStr.substring(startB, endB));
            startB = endB + 1;
          } else {
            vertex[j] = Double.valueOf(pointStr.substring(startB));
          }
        }

        // Add the vertex to the list
        pointList.add(vertex);
      }
    }

    return pointList;
  }*/

  private Shape3DMap readShape3DMap(Node node) {
    Shape3DMap s3dm = new Shape3DMap();

    // Retrieve all attributes of the "Shape3dVisualizationMap" or "PhysicalShape3dVisualizationMap"
    // node
    NamedNodeMap attributes = node.getAttributes();

    for (int i = 0; i < attributes.getLength(); i++) {
      // Get attribute name and value
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(Shape3DMap.PROPERTY)) {
        s3dm.setPropertyName(value);
      }
    }

    // Retrieve child nodes the "Shape3dVisualizationMap" or "PhysicalShape3dVisualizationMap" node
    NodeList childNodes = node.getChildNodes();

    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals(Shape3DMap.CASE)) {
        String propertyValue = null;
        Shape3D shape = null;

        // Retrieve all attributes of the "Case" node
        NamedNodeMap caseAttributes = childNodes.item(i).getAttributes();

        for (int j = 0; j < attributes.getLength(); j++) {
          // Get attribute name and value
          String attributeName = caseAttributes.item(j).getNodeName();
          String attributeValue = caseAttributes.item(j).getNodeValue();

          if (attributeName.equals(Shape3DMap.VALUE)) {
            if (attributeValue.matches("[\\d]+")) {
              propertyValue = String.valueOf(Double.valueOf(attributeValue));
            }

            else if (attributeValue.matches("[\\d]+[.][\\d]+")) {
              propertyValue = String.valueOf(Double.valueOf(attributeValue));
            }

            else {
              propertyValue = attributeValue;
            }
          }
        }

        // Retrieve child nodes of "Case" node
        NodeList caseChildNodes = childNodes.item(i).getChildNodes();

        for (int j = 0; j < caseChildNodes.getLength(); j++) {
          if (caseChildNodes.item(j).getNodeName().equals(
              Shape3D.PHYSICAL_SHAPE_3D)) {
            // Read a "PhysicalShape3D" node
            shape = readPhysicalShape3D(caseChildNodes.item(j));
          }

          if (caseChildNodes.item(j).getNodeName().equals(Shape3D.SHAPE_3D)) {
            // Read a "Shape3D" node
            shape = readShape3D(caseChildNodes.item(j));
          }
        }

        if (shape != null && propertyValue != null) {
          s3dm.getMap().put(propertyValue, shape);
        }
      }
    }

    return s3dm;
  }

  /**
   * Get the simulation description content that is used for showing some infos
   * in the visualization tab.
   * 
   * @return the content of the element.
   */
  public String getSimulationDescriptionInfo() {
    // "Views" node
    String descriptionContent = "";

    // Search for the "UserInterface" node
    NodeList descNodes = XMLLoader.getNodeList(DISPLAY_DESCRIPTION);

    if (descNodes != null && descNodes.getLength() > 0) {
      // must be just one such element node
      Node childDescNode = descNodes.item(descNodes.getLength() - 1);

      // get the content for the right language
      String lang = LanguageManager.getCurrentLanguageCode();

      int n = childDescNode.getChildNodes().getLength();
      Node resultHTMLTextNode = null;
      Node resultHTMLTextDefaultNode = null;
      for (int i = 0; i < n; i++) {
        Node node = childDescNode.getChildNodes().item(i);
        if (node.getLocalName() != null
            && node.getLocalName().equals(DISPLAY_DESCRIPTION_HTML_TEXT))
          if (node.hasAttributes()) {
            Node xmlLang = node.getAttributes().getNamedItem(XML_LANG_ATTR);
            if (node.hasAttributes() && xmlLang != null
                && xmlLang.getNodeValue().equals(lang)) {
              resultHTMLTextNode = node;
              break;
            }
          }
          // no attribute
          else {
            resultHTMLTextDefaultNode = node;
          }
      }

      try {
        if (resultHTMLTextNode != null) {
          descriptionContent = serializeNode(resultHTMLTextNode, "");
        } else if (resultHTMLTextDefaultNode != null) {
          descriptionContent = serializeNode(resultHTMLTextDefaultNode, "");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return descriptionContent;
  }

  /**
   * <p>
   * This will serialize a DOM <code>Node</code> to the supplied
   * <code>String</code>. Please note that is a special serialization that drops
   * any used name-spaces and also do not parse special elements such as
   * instruction processing (<?...?>) and Doc Types.
   * </p>
   * 
   * @param node
   *          DOM <code>Node</code> to serialize.
   * @param indentLevel
   *          current indentation.
   */
  public String serializeNode(Node node, String indentLevel) throws IOException {

    String lineSeparator = "\n";
    String indent = "";
    String result = "";

    // Determine action based on node type
    switch (node.getNodeType()) {
    case Node.DOCUMENT_NODE:
      // recurse on each child
      NodeList nodes = node.getChildNodes();
      if (nodes != null) {
        for (int i = 0; i < nodes.getLength(); i++) {
          result += serializeNode(nodes.item(i), "");
        }
      }
      break;

    case Node.ELEMENT_NODE:
      String name = node.getNodeName();
      if (name.contains(":")) {
        name = name.substring(name.indexOf(":") + 1);
      }
      result += indentLevel + "<" + name;
      NamedNodeMap attributes = node.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++) {
        Node current = attributes.item(i);
        result += " " + current.getNodeName() + "=\"" + current.getNodeValue()
            + "\"";
      }
      result += ">";

      // recurse on each child
      NodeList children = node.getChildNodes();
      if (children != null) {
        if ((children.item(0) != null)
            && (children.item(0).getNodeType() == Node.ELEMENT_NODE)) {

          result += lineSeparator;
        }
        for (int i = 0; i < children.getLength(); i++) {
          result += serializeNode(children.item(i), indentLevel + indent);
        }
        if ((children.item(0) != null)
            && (children.item(children.getLength() - 1).getNodeType() == Node.ELEMENT_NODE)) {

          result += indentLevel;
        }
      }

      result += "</" + name + ">";
      result += lineSeparator;
      break;

    case Node.TEXT_NODE:
      String val = node.getNodeValue();
      val = val.trim();
      if (val.lastIndexOf("\n") == val.length() - 1 && val.length() > 1) {
        val = val.substring(0, val.length() - 1);
      }
      result += val.replace("\n", "<br />");

      break;

    case Node.CDATA_SECTION_NODE:
      result += "<![CDATA[" + node.getNodeValue() + "]]>";
      break;

    case Node.ENTITY_REFERENCE_NODE:
      result += "&" + node.getNodeName() + ";";
      break;
    }

    return result;
  }

  public File getProjectDirectory() {
    return projectDirectory;
  }

  public void setProjectDirectory(File projectDirectory) {
    this.projectDirectory = projectDirectory;
  }
}