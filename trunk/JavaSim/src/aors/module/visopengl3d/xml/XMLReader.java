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
import aors.module.visopengl3d.shape.Circle;
import aors.module.visopengl3d.shape.DisplayInfo;
import aors.module.visopengl3d.shape.Ellipse;
import aors.module.visopengl3d.shape.PolyLine;
import aors.module.visopengl3d.shape.Polygon;
import aors.module.visopengl3d.shape.Positioning;
import aors.module.visopengl3d.shape.Rectangle;
import aors.module.visopengl3d.shape.RegularPolygon;
import aors.module.visopengl3d.shape.Shape2D;
import aors.module.visopengl3d.shape.Shape2DMap;
import aors.module.visopengl3d.shape.ShapePropertyVisualizationMap;
import aors.module.visopengl3d.shape.Square;
import aors.module.visopengl3d.shape.Triangle;
import aors.module.visopengl3d.shape.View;
import aors.module.visopengl3d.space.view.Alignment;
import aors.module.visopengl3d.space.view.GridSpaceView;
import aors.module.visopengl3d.space.view.MapType;
import aors.module.visopengl3d.space.view.OneDimSpaceView;
import aors.module.visopengl3d.space.view.PropertyMap;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.space.view.TwoDimSpaceView;
import aors.module.visopengl3d.utility.Color;
import aors.util.XMLLoader;

/**
 * With the help of this classes methods its possible to read certain
 * information directly from an XML document.
 * 
 * @author Sebastian Mucha
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
              }
            }

            else if (spaceViewChildNodes.item(j).getNodeName().equals(
                GridSpaceView.TWO_DIMENSIONAL_GRID)) {
              if (spaceType.equals(SpaceType.TwoDGrid)) {
                spaceView = getGridSpaceView(spaceViewChildNodes.item(j));
                spaceView.setPropertyMaps(getPropertyMaps(spaceViewChildNodes
                    .item(j)));
              }
            }

            else if (spaceViewChildNodes.item(j).getNodeName().equals(
                TwoDimSpaceView.TWO_DIMENSIONAL)) {
              if (spaceType.equals(SpaceType.TwoD)
                  || spaceType.equals(SpaceType.TwoDLateralView)) {
                spaceView = getTwoDimSpaceView(spaceViewChildNodes.item(j));
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
    Shape2D topLevelShape = null;
    ArrayList<View> embeddedList = new ArrayList<View>();
    DisplayInfo displayInfo = new DisplayInfo();
    Shape2DMap shape2DMap = null;

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
      if (childNodes.item(i).getNodeName().equals(Shape2D.PHYSICAL_SHAPE_2D)) {
        // Read a "PhysicalShape2D" node
        topLevelShape = readPhysicalShape2D(childNodes.item(i));
      }

      if (childNodes.item(i).getNodeName().equals(DisplayInfo.DISPLAY_INFO)) {
        // Read a "DisplayInfo" node
        readDisplayInfo(childNodes.item(i), displayInfo);
      }

      if (childNodes.item(i).getNodeName().equals(
          Shape2DMap.PHYSICAL_SHAPE2D_MAP)) {
        // Read a "PhysicalShape2DMap" node
        shape2DMap = readShape2DMap(childNodes.item(i));
      }
    }

    if (topLevelShape != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.EMBEDDED_VIEW)) {
          // Get embedded view
          embeddedList.add(getEmbeddedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape2D(topLevelShape);
      view.setEmbeddedList(embeddedList);
      view.setDisplayInfo(displayInfo);
    }

    if (shape2DMap != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.EMBEDDED_VIEW)) {
          // Get embedded shapes
          embeddedList.add(getEmbeddedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape2DMap(shape2DMap);
      view.setEmbeddedList(embeddedList);
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
   * Reads a shape definition from a "PhysicalShape2D" node.
   * 
   * @param node
   */
  private Shape2D readPhysicalShape2D(Node node) {
    Shape2D shape = null;

    // Retrieve child nodes of "PhysicalShape2D"
    NodeList childNodes = node.getChildNodes();

    // Search for the actual shape definitions
    for (int i = 0; i < childNodes.getLength(); i++) {
      // Found "Rectangle" node
      if (childNodes.item(i).getNodeName().equals(Shape2D.RECTANGLE)) {
        // Read all data from "Rectangle" node
        shape = readRectangle(childNodes.item(i));
      }

      // Found "Square" node
      else if (childNodes.item(i).getNodeName().equals(Shape2D.SQUARE)) {
        // Read all data from "Square" node
        shape = readSquare(childNodes.item(i));
      }

      // Found "Circle" node
      else if (childNodes.item(i).getNodeName().equals(Shape2D.CIRCLE)) {
        // Read all data from "Circle" node
        shape = readCircle(childNodes.item(i));
      }

      // Found "Triangle" node
      else if (childNodes.item(i).getNodeName().equals(Shape2D.TRIANGLE)) {
        // Read all data from "Circle" node
        shape = readTriangle(childNodes.item(i));
      }

      // Found "Ellipse" node
      else if (childNodes.item(i).getNodeName().equals(Shape2D.ELLIPSE)) {
        // Read all data from "Ellipse" node
        shape = readEllipse(childNodes.item(i));
      }

      // Found "RegularPolygon" node
      else if (childNodes.item(i).getNodeName().equals(Shape2D.REGULAR_POLYGON)) {
        // Read all data from "RegularPolygon" node
        shape = readRegularPolygon(childNodes.item(i));
      }

      // Found "Polygon" node
      else if (childNodes.item(i).getNodeName().equals(Shape2D.POLYGON)) {
        // Read all data from "Polygon" node
        shape = readPolygon(childNodes.item(i));
      }

      // Found "PolyLine" node
      else if (childNodes.item(i).getNodeName().equals(Shape2D.POLYLINE)) {
        // Read all data from "PolyLine" node
        shape = readPolyLine(childNodes.item(i));
      }
    }

    return shape;
  }

  /**
   * Returns a view that was defined by a ObjectView node.
   * 
   * @param node
   */
  public View readObjectView(Node node) {
    View view = null;
    Shape2D topLevelShape = null;
    ArrayList<View> embeddedList = new ArrayList<View>();
    DisplayInfo displayInfo = new DisplayInfo();
    Shape2DMap shape2DMap = null;

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

    // Search for the "Shape2D" node inside "ObjectView"
    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals(Shape2D.SHAPE_2D)) {
        // Read a "Shape2D" node
        topLevelShape = readShape2D(childNodes.item(i));
      }

      if (childNodes.item(i).getNodeName().equals(DisplayInfo.DISPLAY_INFO)) {
        readDisplayInfo(childNodes.item(i), displayInfo);
      }

      if (childNodes.item(i).getNodeName().equals(Shape2DMap.SHAPE2D_MAP)) {
        // Read a "Shape2dVisualizationMap" node
        shape2DMap = readShape2DMap(childNodes.item(i));
      }
    }

    if (topLevelShape != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.EMBEDDED_VIEW)) {
          // Get embedded shapes
          embeddedList.add(getEmbeddedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape2D(topLevelShape);
      view.setEmbeddedList(embeddedList);
      view.setDisplayInfo(displayInfo);
    }

    if (shape2DMap != null) {
      for (int i = 0; i < childNodes.getLength(); i++) {
        if (childNodes.item(i).getNodeName().equals(View.EMBEDDED_VIEW)) {
          // Get embedded shapes
          embeddedList.add(getEmbeddedShape(childNodes.item(i)));
        }
      }

      view = new View();
      view.setShape2DMap(shape2DMap);
      view.setEmbeddedList(embeddedList);
      view.setDisplayInfo(displayInfo);
    }

    return view;
  }

  /**
   * Reads a shape definition from a "Shape2D" node.
   * 
   * @param node
   */
  private Shape2D readShape2D(Node node) {
    Shape2D shape = null;

    // Retrieve child nodes of "Shape2D"
    NodeList childNodes = node.getChildNodes();

    // Search for the actual shape definitions
    for (int j = 0; j < childNodes.getLength(); j++) {
      // Found "Rectangle" node
      if (childNodes.item(j).getNodeName().equals(Shape2D.RECTANGLE)) {
        // Read all data from "Rectangle" node
        shape = readRectangle(childNodes.item(j));
      }

      // Found "Square" node
      else if (childNodes.item(j).getNodeName().equals(Shape2D.SQUARE)) {
        // Read all data from "Square" node
        shape = readSquare(childNodes.item(j));
      }

      // Found "Circle" node
      else if (childNodes.item(j).getNodeName().equals(Shape2D.CIRCLE)) {
        // Read all data from "Circle" node
        shape = readCircle(childNodes.item(j));
      }

      // Found "Triangle" node
      else if (childNodes.item(j).getNodeName().equals(Shape2D.TRIANGLE)) {
        // Read all data from "Triangle" node
        shape = readTriangle(childNodes.item(j));
      }

      // Found "Ellipse" node
      else if (childNodes.item(j).getNodeName().equals(Shape2D.ELLIPSE)) {
        // Read all data from "Ellipse" node
        shape = readEllipse(childNodes.item(j));
      }

      // Found "RegularPolygon" node
      else if (childNodes.item(j).getNodeName().equals(Shape2D.REGULAR_POLYGON)) {
        // Read all data from "RegularPolygon" node
        shape = readRegularPolygon(childNodes.item(j));
      }

      // Found "Polygon" node
      else if (childNodes.item(j).getNodeName().equals(Shape2D.POLYGON)) {
        // Read all data from "Polygon" node
        shape = readPolygon(childNodes.item(j));
      }

      // Found "PolyLine" node
      else if (childNodes.item(j).getNodeName().equals(Shape2D.POLYLINE)) {
        // Read all data from "PolyLine" node
        shape = readPolyLine(childNodes.item(j));
      }
    }

    if (shape != null) {
      // Retrieve all attributes of the "Shape2D" node
      NamedNodeMap attributes = node.getAttributes();

      for (int j = 0; j < attributes.getLength(); j++) {
        String name = attributes.item(j).getNodeName();
        String value = attributes.item(j).getNodeValue();

        if (name.equals(Shape2D.X)) {
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

        else if (name.equals(Shape2D.Y)) {
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
  private void readShapeAttributes(Node node, Shape2D shape) {
    // Retrieve all attributes
    NamedNodeMap attributes = node.getAttributes();

    // Color flag
    boolean fillSet = false;
    boolean strokeSet = false;

    for (int i = 0; i < attributes.getLength(); i++) {
      // Get attribute name and value
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(Shape2D.FILL)) {
        shape.setFill(new Color(value));
        fillSet = true;
      }

      else if (name.equals(Shape2D.STROKE)) {
        shape.setStroke(new Color(value));
        strokeSet = true;
      }

      else if (name.equals(Shape2D.FILL_OPACITY))
        shape.setFillOpacity(Double.valueOf(value));

      else if (name.equals(Shape2D.STROKE_OPACITY))
        shape.setStrokeOpacity(Double.valueOf(value));

      else if (name.equals(Shape2D.STROKE_WIDTH))
        shape.setStrokeWidth(Double.valueOf(value));

      else if (name.equals(Shape2D.TEXTURE)) {
        shape.setTextureFilename(value);
      }

      else if (name.equals(Shape2D.WIDTH)) {
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

      else if (name.equals(Shape2D.HEIGHT)) {
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

      else if (name.equals(Shape2D.R)) {
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

      else if (name.equals(Shape2D.RX)) {
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

      else if (name.equals(Shape2D.RY)) {
        // Check if the value is relative or absolute
        if (value.contains("%")) {
          double radius = Double
              .valueOf(value.substring(0, value.length() - 1));
          shape.setHeight(radius * 2);
          shape.setRelativeHeight(radius * 2);
          shape.setHeightRelative(true);
        } else if (value.contains("px")) {
          double radius = Double
              .valueOf(value.substring(0, value.length() - 2));
          shape.setHeight(radius * 2);
        } else {
          shape.setHeight(Double.valueOf(value) * 2);
        }
      }

      else if (name.equals(Shape2D.NUMBER_OF_POINTS)) {
        shape.setNumberOfPoints(Double.valueOf(value));
      }

      else if (name.equals(Shape2D.FILL_RGB) && !fillSet) {
        shape.setFill(new Color(value));
      }

      else if (name.equals(Shape2D.STROKE_RGB) && !strokeSet) {
        shape.setStroke(new Color(value));
      }

      else if (name.equals(Shape2D.POSITIONING)) {
        // Determine the positioning
        if (value.equals(Positioning.CenterBottom.name())) {
          shape.setPositioning(Positioning.CenterBottom);
        }

        else if (value.equals(Positioning.CenterCenter.name())) {
          shape.setPositioning(Positioning.CenterCenter);
        }

        else if (value.equals(Positioning.CenterTop.name())) {
          shape.setPositioning(Positioning.CenterTop);
        }

        else if (value.equals(Positioning.LeftBottom.name())) {
          shape.setPositioning(Positioning.LeftBottom);
        }

        else if (value.equals(Positioning.LeftCenter.name())) {
          shape.setPositioning(Positioning.LeftCenter);
        }

        else if (value.equals(Positioning.LeftTop.name())) {
          shape.setPositioning(Positioning.LeftTop);
        }

        else if (value.equals(Positioning.RightBottom.name())) {
          shape.setPositioning(Positioning.RightBottom);
        }

        else if (value.equals(Positioning.RightCenter.name())) {
          shape.setPositioning(Positioning.RightCenter);
        }

        else if (value.equals(Positioning.RightTop.name())) {
          shape.setPositioning(Positioning.RightTop);
        }
      }

      else if (name.equals(Shape2D.POINTS)) {
        shape.setPointList(readPointString(value));
      }
    }

    // Read possible shape property maps
    shape.setPropertyMaps(getPropertyMaps(node));
  }

  /**
   * Reads all data from a "Rectangle" node.
   * 
   * @param node
   */
  private Shape2D readRectangle(Node node) {
    // Create a new Rectangle instance
    Rectangle rectangle = new Rectangle();

    // Get shape attributes
    readShapeAttributes(node, rectangle);

    return rectangle;
  }

  /**
   * Reads all data from a "Square" node.
   * 
   * @param node
   */
  private Shape2D readSquare(Node node) {
    // Create a new Square instance
    Square square = new Square();

    // Get shape attributes
    readShapeAttributes(node, square);

    return square;
  }

  /**
   * Reads all data from a "Circle" node.
   * 
   * @param node
   */
  private Shape2D readCircle(Node node) {
    // Create a new Circle instance
    Circle circle = new Circle();

    // Get shape attributes
    readShapeAttributes(node, circle);

    return circle;
  }

  /**
   * Reads all data from a "Triangle" node.
   * 
   * @param node
   */
  private Shape2D readTriangle(Node node) {
    // Create a new Triangle instance
    Triangle triangle = new Triangle();

    // Get shape attributes
    readShapeAttributes(node, triangle);

    return triangle;
  }

  /**
   * Reads all data from a "Ellipse" node.
   * 
   * @param node
   */
  private Shape2D readEllipse(Node node) {
    // Create a new Ellipse instance
    Ellipse ellipse = new Ellipse();

    // Get shape attributes
    readShapeAttributes(node, ellipse);

    return ellipse;
  }

  /**
   * Reads all data from a "RegularPolygon" node.
   * 
   * @param node
   */
  private Shape2D readRegularPolygon(Node node) {
    // Create a new RegularPolygon instance
    RegularPolygon regPoly = new RegularPolygon();

    // Get shape attributes
    readShapeAttributes(node, regPoly);

    return regPoly;
  }

  /**
   * Reads all data from a "Polygon" node.
   * 
   * @param node
   */
  private Shape2D readPolygon(Node node) {
    // Create a new Polygon instance
    Polygon poly = new Polygon();

    // Get shape attributes
    readShapeAttributes(node, poly);

    return poly;
  }

  /**
   * Reads all data from a "PolyLine" node.
   * 
   * @param node
   */
  private Shape2D readPolyLine(Node node) {
    // Create a new PolyLine instance
    PolyLine polyLine = new PolyLine();

    // Get shape attributes
    readShapeAttributes(node, polyLine);

    return polyLine;
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
   * Reads the definition of an embedded shape.
   * 
   * @param node
   */
  private View getEmbeddedShape(Node node) {
    View embeddedView = new View();

    ArrayList<View> embeddedList = new ArrayList<View>();

    double offsetX = 0;
    double offsetY = 0;
    boolean offsetXRelative = false;
    boolean offsetYRelative = false;

    /*
     * Retrieve the attributes of the "EmbeddedView" node
     */
    NamedNodeMap attributes = node.getAttributes();

    /*
     * Retrieve the name and value of each attribute and initialize the class
     * members accordingly.
     */
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(Shape2D.OFFSET_X)) {
        if (value.contains("%")) {
          offsetX = Double.valueOf(value.substring(0, value.length() - 1));
          offsetXRelative = true;
        } else if (value.contains("px")) {
          offsetX = Double.valueOf(value.substring(0, value.length() - 2));
        } else {
          offsetX = Double.valueOf(value);
        }
      }

      else if (name.equals(Shape2D.OFFSET_Y)) {
        if (value.contains("%")) {
          offsetY = Double.valueOf(value.substring(0, value.length() - 1));
          offsetYRelative = true;
        } else if (value.contains("px")) {
          offsetY = Double.valueOf(value.substring(0, value.length() - 2));
        } else {
          offsetY = Double.valueOf(value);
        }
      }

      else if (name.equals(View.EMB_VIEW_LABEL)) {
        embeddedView.setEmbeddedLabel(value);
      }
    }

    // Retrieve child nodes of "EmbeddedView"
    NodeList childNodes = node.getChildNodes();

    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals(Shape2D.PHYSICAL_SHAPE_2D)) {
        embeddedView.setShape2D(readPhysicalShape2D(childNodes.item(i)));
      }

      if (childNodes.item(i).getNodeName().equals(Shape2D.SHAPE_2D)) {
        embeddedView.setShape2D(readShape2D(childNodes.item(i)));
      }

      if (childNodes.item(i).getNodeName().equals(
          Shape2DMap.PHYSICAL_SHAPE2D_MAP)
          || childNodes.item(i).getNodeName().equals(Shape2DMap.SHAPE2D_MAP)) {
        embeddedView.setShape2DMap(readShape2DMap(childNodes.item(i)));
      }

      if (childNodes.item(i).getNodeName().equals(View.EMBEDDED_VIEW)) {
        embeddedList.add(getEmbeddedShape(childNodes.item(i)));
      }
    }

    if (embeddedView.getShape2DMap() != null) {
      Collection<Shape2D> collection = embeddedView.getShape2DMap().getMap()
          .values();

      for (Shape2D shape : collection) {
        shape.setOffsetX(offsetX);
        shape.setOffsetY(offsetY);
        shape.setOffsetXRelative(offsetXRelative);
        shape.setOffsetYRelative(offsetYRelative);
      }
    }

    else if (embeddedView.getShape() != null) {
      embeddedView.getShape().setOffsetX(offsetX);
      embeddedView.getShape().setOffsetY(offsetY);
      embeddedView.getShape().setOffsetXRelative(offsetXRelative);
      embeddedView.getShape().setOffsetYRelative(offsetYRelative);
    }

    embeddedView.setEmbeddedList(embeddedList);

    return embeddedView;
  }

  public ArrayList<double[]> readPointString(String str) {
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
  }

  private Shape2DMap readShape2DMap(Node node) {
    Shape2DMap s2dm = new Shape2DMap();

    // Retrieve all attributes of the "Shape2dVisualizationMap" or "PhysicalShape2DMap"
    // node
    NamedNodeMap attributes = node.getAttributes();

    for (int i = 0; i < attributes.getLength(); i++) {
      // Get attribute name and value
      String name = attributes.item(i).getNodeName();
      String value = attributes.item(i).getNodeValue();

      if (name.equals(Shape2DMap.PROPERTY)) {
        s2dm.setPropertyName(value);
      }
    }

    // Retrieve child nodes the "Shape2dVisualizationMap" or "PhysicalShape2DMap" node
    NodeList childNodes = node.getChildNodes();

    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals(Shape2DMap.CASE)) {
        String propertyValue = null;
        Shape2D shape = null;

        // Retrieve all attributes of the "Case" node
        NamedNodeMap caseAttributes = childNodes.item(i).getAttributes();

        for (int j = 0; j < attributes.getLength(); j++) {
          // Get attribute name and value
          String attributeName = caseAttributes.item(j).getNodeName();
          String attributeValue = caseAttributes.item(j).getNodeValue();

          if (attributeName.equals(Shape2DMap.VALUE)) {
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
              Shape2D.PHYSICAL_SHAPE_2D)) {
            // Read a "PhysicalShape2D" node
            shape = readPhysicalShape2D(caseChildNodes.item(j));
          }

          if (caseChildNodes.item(j).getNodeName().equals(Shape2D.SHAPE_2D)) {
            // Read a "Shape2D" node
            shape = readShape2D(caseChildNodes.item(j));
          }
        }

        if (shape != null && propertyValue != null) {
          s2dm.getMap().put(propertyValue, shape);
        }
      }
    }

    return s2dm;
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
