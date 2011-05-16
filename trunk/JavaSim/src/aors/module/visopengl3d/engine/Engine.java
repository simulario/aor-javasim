package aors.module.visopengl3d.engine;

import java.awt.Point;
import java.io.File;
import java.nio.IntBuffer;
//import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import aors.GeneralSpaceModel.SpaceType;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.module.visopengl3d.gui.VisualizationPanel;
import aors.module.visopengl3d.shape.DisplayInfo;
//import aors.module.visopengl3d.shape.Positioning;
import aors.module.visopengl3d.shape.Shape3D;
//import aors.module.visopengl3d.shape.ShapeType;
import aors.module.visopengl3d.shape.View;
import aors.module.visopengl3d.space.component.SpaceComponent;
import aors.module.visopengl3d.space.model.GridSpaceModel;
import aors.module.visopengl3d.space.model.SpaceModel;
import aors.module.visopengl3d.space.view.Face;
import aors.module.visopengl3d.space.view.GridSpaceView;
import aors.module.visopengl3d.space.view.Skybox;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.space.view.TwoDimSpaceView;
import aors.module.visopengl3d.utility.Offset;
import aors.module.visopengl3d.utility.TextureLoader;
import aors.space.Space;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.gl2.GLUT;
import com.sun.opengl.util.texture.Texture;

/**
 * 2D rendering engine, containing methods to initialize rendering, handle
 * resizes and display a single frame.
 * 
 * @author Sebastian Mucha
 * @since March 16th, 2010
 * 
 */
public class Engine implements GLEventListener {

  // OpenGL Utility Library
  private GLU glu = new GLU();

  // OpenGL Utility Toolkit
  private GLUT glut = new GLUT();

  // Project directory
  private File projectDirectory;

  // Drawing area
  private Offset drawingArea;

  // Event handlers
  private KeyboardEventHandler keyboardEvtHandler = new KeyboardEventHandler();
  private MouseEventHandler mouseEvtHandler = new MouseEventHandler();

  // Visualization's space model
  private SpaceModel spaceModel;

  // Object map
  private Map<Long, Objekt> objMap;

  // Object view map
  private Map<Long, View> viewMap;

  // Pick point
  private Point pickPoint = new Point();

  // Selection buffer size
  private final int SELECTION_BUFFER_SIZE = 512;

  // Flag indicating if the picking mode is enabled
  private boolean pickingMode;

  // Visualization panel
  private VisualizationPanel visPanel;

  // Scene camera model
  private Camera2D camera;

  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = (GL2) drawable.getGL();

    // Clear color and depth buffer
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    // Reset model view matrix stack
    gl.glLoadIdentity();

    // Apply camera transformations
    camera.scroll(gl);

    // Display the space model & objects
    if (spaceModel != null) {
      if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
        if (spaceModel.isRecompile()) {
          spaceModel.compileDisplayList(gl, glu);
          spaceModel.setRecompile(false);
        }
      }
      spaceModel.display(gl, glu);
      
      Skybox skybox = spaceModel.getSpaceView().getSkybox();
      if(skybox != null) {
    	double[] cameraPosition = {camera.getX(), camera.getY(), camera.getZ()};
      	skybox.setPosition(cameraPosition);
      	skybox.display(gl, glu);
      }

      if (objMap != null) {
        if (!objMap.isEmpty()) {
          if (pickingMode) {
            visPanel.updateObjectIDLabel(pick(gl, glu));
            displayObjects(gl, glu);
            pickingMode = false;
          } else {
            displayObjects(gl, glu);
          }
        }
      }
    }
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    GL2 gl = (GL2) drawable.getGL();

    // Set the clearing color
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    // Enable back face culling
    gl.glEnable(GL2.GL_CULL_FACE);
    gl.glCullFace(GL2.GL_BACK);

    // Enable alpha blending
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    // Enable depth testing
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glDepthFunc(GL2.GL_LEQUAL);
    
    // Set the shading model to smooth shading
    gl.glShadeModel(GL2.GL_SMOOTH);
    
    // Color of global ambient light
    float[] globalAmbient = {0.4f, 0.4f, 0.4f, 1.0f};
    
    // Position and colors of light source 0
    float[] lightPosition = {1.0f, 1.0f, 1.0f, 0.0f};
    //float[] lightAmbient = {0.3f, 0.3f, 0.3f, 1.0f};
    float[] lightDiffuse = {0.8f, 0.8f, 0.8f, 1.0f};
    float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
    
    // Specular material properties
    float[] specularRef = {1.0f, 1.0f, 1.0f, 1.0f};
    
    // Set global ambient light
    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmbient, 0);
    
    // Set position and colors of light source 0
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
    //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0);
    
    // Set specular material properties and shininess
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specularRef, 0);
    gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 20);
    
    // Enable color tracking
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    // Set ambient and diffuse material properties to follow glColor values  
    gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
    
    // Multiply texture color by primitive color, so that textured geometry appears lit
    gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
    // Apply specular highlights after texturing
    gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
    
    // Enable lighting
    gl.glEnable(GL2.GL_LIGHTING);
    // Enable light source 0
    gl.glEnable(GL2.GL_LIGHT0);
    
    // Enable texture support
    gl.glEnable(GL2.GL_TEXTURE);

    // Prepare the space model
    if (spaceModel != null && drawingArea != null) {
      prepareSpaceModel(gl);
    }

    // Prepare objects that are present in the initial state
    if (objMap != null) {
      prepareObjects(gl);
    }

    // Create the camera model
    camera = new Camera2D();

    // Set up event handlers
    mouseEvtHandler.setEngine(this);
    keyboardEvtHandler.setCamera(camera);
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width,
      int height) {
    GL2 gl = (GL2) drawable.getGL();

    // Border around the drawing area
    final double BORDER = 40;

    // Set the viewport to window dimensions
    gl.glViewport(x, y, width, height);

    // Reset projection matrix stack
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();

    // Establish an orthogonal clipping volume
    //gl.glOrtho(-width / 2, width / 2, -height / 2, height / 2, -100, 1);

    // ratio of width to height
    double aspect = ((double)width)/((double)height);
    // Define a perspective viewing volume
    glu.gluPerspective(45, aspect, 1, 1000);
    // Set the camera position
    glu.gluLookAt(0, 0, 400, 0, 0, 0, 0, 1, 0);
    
    // Reset model view matrix stack
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();

    // Initialize the drawing area
    drawingArea = new Offset((-width / 2) + BORDER, (-height / 2) + BORDER,
        (width / 2) - BORDER, (height / 2) - BORDER);

    // Reinitialize
    init(drawable);
  }

  /**
   * Prepares the space model for display.
   * 
   * @param gl
   */
  private void prepareSpaceModel(GL2 gl) {
    // Adjust drawing area (find correct pixel values)
    double w_tmp = 0;
    double h_tmp = 0;
    double w_px = 0;
    double h_px = 0;

    if (spaceModel.getGeneralSpaceModel() == null) {
      spaceModel.setxMax(drawingArea.getWidth());
      spaceModel.setyMax(drawingArea.getHeight());
    }

    w_tmp = drawingArea.getWidth() / spaceModel.getxMax();
    h_tmp = drawingArea.getHeight() / spaceModel.getyMax();

    double proportion = Math.min(w_tmp, h_tmp);

    w_px = proportion * spaceModel.getxMax();
    h_px = proportion * spaceModel.getyMax();

    drawingArea = new Offset(-w_px / 2.0, -h_px / 2.0, w_px / 2.0, h_px / 2.0);

    // Set the space models drawing area
    spaceModel.setDrawingArea(drawingArea);

    // Get the space type
    SpaceType type = spaceModel.getSpaceType();

    if (type.equals(SpaceType.TwoD)) {
      // Get the space view
      TwoDimSpaceView spaceView = (TwoDimSpaceView) spaceModel.getSpaceView();

      // Load background texture
      if (spaceView.getBackgroundImgFilename() != null
          && spaceView.getBackgroundImg() == null) {
        spaceView.setBackgroundImg(loadTexture(spaceView
            .getBackgroundImgFilename()));
      }

      // Initialize the space model
      spaceModel.initializeSpaceModel(gl, glu);
    }

    else if (type.equals(SpaceType.TwoDGrid)) {
      // Get the space view
      GridSpaceView spaceView = (GridSpaceView) spaceModel.getSpaceView();

      // Load background texture
      if (spaceView.getBackgroundImgFilename() != null
          && spaceView.getBackgroundImg() == null) {
        spaceView.setBackgroundImg(loadTexture(spaceView
            .getBackgroundImgFilename()));
      }

      // Initialize the space model
      spaceModel.initializeSpaceModel(gl, glu);

      // Apply initial property map
      if (spaceView.getPropertyMaps() != null && !spaceModel.isInitialized()) {
        ((GridSpaceModel) spaceModel).applyPropertyMaps();
      }
    }

    else {
      // Initialize the space model
      spaceModel.initializeSpaceModel(gl, glu);
    }

    // Set the clear color
    double[] clear = spaceModel.getSpaceView().getCanvasColor().getColor();
    gl.glClearColor((float) clear[0], (float) clear[1], (float) clear[2], 1);

    // Create display list
    spaceModel.compileDisplayList(gl, glu);
    spaceModel.setInitialized(true);
    
    
    Skybox skybox = spaceModel.getSpaceView().getSkybox();
    if(skybox != null) {
    	Class<?> faceClass = Face.class;
		for (Face face : (Face[])faceClass.getEnumConstants())  {
			String textureFilename = skybox.getTextureFilename(face);
			
			if(textureFilename != null) {
				skybox.setTexture(face, loadTexture(textureFilename)); 
			}
		}
    	
    	skybox.generateDisplayList(gl, glu);
    }
  }

  /**
   * Prepares objects for display.
   * 
   * @param gl
   */
  private void prepareObjects(GL2 gl) {
    // Get all objects as a collection (to be able to iterate over it)
    Collection<Objekt> objCollection = objMap.values();

    for (Objekt obj : objCollection) {
      // Get the objects view
      View view = viewMap.get(obj.getId());

      if (view != null) {
        // Get the associated shape
        Shape3D shape = view.getShape();

        if (shape != null) {
          // Load texture
          if (shape.getTextureFilename() != null && shape.getTexture() == null) {
            shape.setTexture(loadTexture(shape.getTextureFilename()));
            shape.setRecompile(true);
          }
        }

        // Load embedded views textures
        if (view.getAttachedList() != null) {
          for (View embeddedView : view.getAttachedList()) {
            if (embeddedView.getShape() != null) {
              // Load texture
              if (embeddedView.getShape().getTextureFilename() != null
                  && embeddedView.getShape().getTexture() == null) {
                embeddedView.getShape().setTexture(
                    loadTexture(embeddedView.getShape().getTextureFilename()));
                embeddedView.getShape().setRecompile(true);
              }
            }

            if (embeddedView.getAttachedList() != null) {
              for (View embeddedView2 : embeddedView.getAttachedList()) {
                if (embeddedView2.getShape() != null) {
                  // Load texture
                  if (embeddedView2.getShape().getTextureFilename() != null
                      && embeddedView2.getShape().getTexture() == null) {
                    embeddedView2.getShape().setTexture(
                        loadTexture(embeddedView2.getShape()
                            .getTextureFilename()));
                    embeddedView2.getShape().setRecompile(true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Loads and returns textures from the file system.
   * 
   * @param filename
   */
  private Texture loadTexture(String filename) {
    // Search in the project's media directory
    String path = projectDirectory.getPath() + File.separator + "media"
        + File.separator + "images" + File.separator + filename;

    // Check if the file is existing in the project directory
    if (new File(path).isFile()) {
      return TextureLoader.load(path);
    } else {
      // Search in the global media directory
      path = System.getProperty("user.dir") + File.separator + "media"
          + File.separator + "images" + File.separator + filename;

      // Check if the file is existing in the global directory
      if (new File(path).isFile()) {
        return TextureLoader.load(path);
      } else {
        System.out.println("Warning: Could not find image " + filename);
      }
    }

    return null;
  }

  /**
   * Displays all objects.
   * 
   * @param gl
   * @param glu
   */
  private void displayObjects(GL2 gl, GLU glu) {
    // Get all objects as a collection (to be able to iterate over it)
    Collection<Objekt> objCollection = objMap.values();

    /*
     * When new objects where added to the object list it might be necessary to
     * load their textures.
     */
    prepareObjects(gl);

    /*
     * When the space model is a grid, we'll have to determine how many objects
     * are in one cell and their positions in this cell.
     */
    try {
      if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
        GridSpaceModel gsm = (GridSpaceModel) spaceModel;

        for (Objekt obj : objCollection) {
          if (obj instanceof PhysicalAgentObject
              || obj instanceof PhysicalObject) {
            gsm.updateObjectsPerCell((Physical) obj, 1);
          }
        }

        // Calculate object positions inside of cells
        gsm.calculateCellObjectPositions();
      }

      // Display all objects
      for (Objekt obj : objCollection) {
        if (obj instanceof PhysicalAgentObject || obj instanceof PhysicalObject) {
          // Display physical agents and physical objects
          gl.glPushName((int) obj.getId());
          displayPhysical((Physical) obj, gl, glu);
          gl.glPopName();

          /*
           * If the space model is a gird, decrease the object count of the
           * cell, the physical was displayed in.
           */
          if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
            GridSpaceModel gsm = (GridSpaceModel) spaceModel;
            gsm.updateObjectsPerCell((Physical) obj, 0);
          }
        } else {
          // Display non-physical agents or objects
          gl.glPushName((int) obj.getId());
          displayNonPhysical(obj, gl, glu);
          gl.glPopName();
        }
      }

      /*
       * When the space model is a grid, after all objects have been displayed
       * the objects per cell have to be reseted.
       */
      if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
        GridSpaceModel gsm = (GridSpaceModel) spaceModel;
        gsm.resetObjectsPerCell();
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out
          .println("Visualization Error: Wrong component index! Check startCountingWithZero ...");
      return;
    }
  }

  /**
   * Displays all physical objects.
   * 
   * @param phy
   * @param gl
   * @param glu
   */
  private void displayPhysical(Physical phy, GL2 gl, GLU glu) {
    // Get the physicals view
    View view = viewMap.get(phy.getId());

    if (view != null) {
      if (view.getShape() != null) {
        Shape3D shape = view.getShape();

        // Parse the point string for polygons or polylines
/*        if (shape.getType().equals(ShapeType.Polygon)
            || shape.getType().equals(ShapeType.Polyline)) {
          if (shape.isParsePointString()) {
            shape.setPointList(parsePointString(phy.getPoints()));
          }
        }
*/
        // Get the display info
        DisplayInfo displayInfo = view.getDisplayInfo();

        /*
         * Check if the physicals ID or name has to be displayed and set it, if
         * necessary.
         */
        if (displayInfo.isDisplayID()) {
          displayInfo.setId(Long.toString(phy.getId()));
        }

        if (displayInfo.isDisplayName()) {
          displayInfo.setName(phy.getName());
        }

        // Apply changes of physical attributes to the display info
        displayInfo.applyProperty(phy);

        // Get the space component index the physical is placed onto
        int index = getSpaceComponentIndex((int) phy.getX(), (int) phy.getY());

        // Check if the space component index is valid
        if (index < 0 || index > spaceModel.getSpaceComponents().size() - 1) {
          System.out.println("Visualization Warning: Invalid component index!");
          return;
        }

        // Update shape dimensions
        updateShapeDimensions(index, shape, phy);

        // Check if the shape needs to be recompiled
        if (shape.isRecompile()) {
          shape.generateDisplayList(gl, glu);
          shape.setRecompile(false);
        }

        // Get the physical's position in world coordinate space
        double[] pos = getPhysicalObjectPosition(index, phy);

        // Don't display if the position is somehow undefined
        if (pos == null) {
          return;
        }

        // Set the display info's position
        if (displayInfo.isEnabled()) {
          displayInfo.setX(pos[0]);
          displayInfo.setZ(pos[2]);

/*          if (shape.getHeight() > 0) {
            if (shape.getPositioning().equals(Positioning.CenterCenter)
                || shape.getPositioning().equals(Positioning.LeftCenter)
                || shape.getPositioning().equals(Positioning.RightCenter)) {
              displayInfo.setY(pos[1] + (shape.getHeight() / 2) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterBottom)
                || shape.getPositioning().equals(Positioning.LeftBottom)
                || shape.getPositioning().equals(Positioning.RightBottom)) {
              displayInfo.setY(pos[1] + (shape.getHeight()) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterTop)
                || shape.getPositioning().equals(Positioning.LeftTop)
                || shape.getPositioning().equals(Positioning.RightTop)) {
              displayInfo.setY(pos[1] + 3);
            }
          } else {
            if (shape.getPositioning().equals(Positioning.CenterCenter)
                || shape.getPositioning().equals(Positioning.LeftCenter)
                || shape.getPositioning().equals(Positioning.RightCenter)) {
              displayInfo.setY(pos[1] + (shape.getWidth() / 2) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterBottom)
                || shape.getPositioning().equals(Positioning.LeftBottom)
                || shape.getPositioning().equals(Positioning.RightBottom)) {
              displayInfo.setY(pos[1] + (shape.getWidth()) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterTop)
                || shape.getPositioning().equals(Positioning.LeftTop)
                || shape.getPositioning().equals(Positioning.RightTop)) {
              displayInfo.setY(pos[1] + 3);
            }
          }
*/
        }

        // Get the physical's rotation
        double rot = getPhysicalObjectRotation(index, phy);

        // Display only if the object is visible
        if (view.isVisible()) {
          // Display the physical object
          displaySingleObject(pos, rot, shape, gl, glu);

          if (view.getAttachedList() != null) {
            for (View embeddedView : view.getAttachedList()) {
              double[] embeddedOffset = null;
              embeddedOffset = displayEmbeddedView(embeddedView, shape, pos,
                  null, rot, gl, glu);

              if (embeddedView.getAttachedList() != null) {
                for (View embeddedView2 : embeddedView.getAttachedList()) {
                  displayEmbeddedView(embeddedView2, embeddedView.getShape(),
                      pos, embeddedOffset, rot, gl, glu);
                }
              }
            }
          }

          // Draw the display info
          if (displayInfo.isEnabled()) {
            displayInfo.display(gl, glut);
          }
        }
      }
    }
  }

  private double[] displayEmbeddedView(View embeddedView, Shape3D parent,
      double[] pos, double[] offset, double rot, GL2 gl, GLU glu) {
    // Get the shape
    Shape3D embeddedShape = embeddedView.getShape();

    if (embeddedShape != null) {

      mapEmbedded(embeddedShape, parent);
      /*
       * Check if the shape was changed by a ShapePropertyVisualizationMap  and regenerate its
       * display list if necessary.
       */
      if (embeddedShape.isRecompile()) {
        embeddedShape.generateDisplayList(gl, glu);
        embeddedShape.setRecompile(false);
      }

      // Check if the shape's display list was generated at all
      if (embeddedShape.getDisplayList() == -1) {
        embeddedShape.generateDisplayList(gl, glu);
      }

      double[] embeddedOffset = new double[2];

      // Map position
      if (offset != null) {
        if (embeddedShape.isOffsetXRelative()) {
          embeddedOffset[0] = offset[0] + (embeddedShape.getOffsetX() / 100)
              * parent.getWidth();
        } else {
          embeddedOffset[0] = offset[0] + embeddedShape.getOffsetX();
        }

        if (embeddedShape.isOffsetYRelative()) {
          embeddedOffset[1] = offset[1] + (embeddedShape.getOffsetY() / 100)
              * parent.getHeight();
        } else {
          embeddedOffset[1] = offset[1] + embeddedShape.getOffsetY();
        }
      } else {
        if (embeddedShape.isOffsetXRelative()) {
          embeddedOffset[0] = (embeddedShape.getOffsetX() / 100)
              * parent.getWidth();
        } else {
          embeddedOffset[0] = embeddedShape.getOffsetX();
        }

        if (embeddedShape.isOffsetYRelative()) {
          embeddedOffset[1] = (embeddedShape.getOffsetY() / 100)
              * parent.getHeight();
        } else {
          embeddedOffset[1] = embeddedShape.getOffsetY();
        }
      }

      // Display embedded shape
      displayEmbeddedObject(pos, embeddedOffset, rot, embeddedShape,
          embeddedView.getAttachedLabel(), gl, glu);

      return embeddedOffset;
    }

    return null;
  }

  /**
   * Displays all non-physical objects.
   * 
   * @param obj
   * @param gl
   * @param glu
   */
  private void displayNonPhysical(Objekt obj, GL2 gl, GLU glu) {
    // Get the non-physicals view
    View view = viewMap.get(obj.getId());

    if (view != null) {
      // Get the associated shape
      Shape3D shape = view.getShape();

      if (shape != null) {
        // Get the display info
        DisplayInfo displayInfo = view.getDisplayInfo();

        /*
         * Check if the physicals ID or name has to be displayed and set it, if
         * necessary.
         */
        if (displayInfo.isDisplayID()) {
          displayInfo.setId(Long.toString(obj.getId()));
        }

        if (displayInfo.isDisplayName()) {
          displayInfo.setName(obj.getName());
        }

        mapNonPhysicals(shape);

        /*
         * Check if the shape was changed by a ShapePropertyVisualizationMap  and regenerate
         * its display list if necessary.
         */
        if (shape.isRecompile()) {
          shape.generateDisplayList(gl, glu);
          shape.setRecompile(false);
        }

        // Check if the shape's display list was generated at all
        if (shape.getDisplayList() == -1) {
          shape.generateDisplayList(gl, glu);
        }

        // Get the non-physicals position
        double[] pos = getNonPhysicalObjectPosition(shape);

        // Check if the object position is valid
        if (pos[0] < spaceModel.getDrawingArea().x1
            || pos[0] > spaceModel.getDrawingArea().x2
            || pos[1] < spaceModel.getDrawingArea().y1
            || pos[1] > spaceModel.getDrawingArea().y2) {
          System.out.println("Visualization Warning: Object with ID "
              + obj.getId() + " is outside of the visible area!");
          return;
        }

        // Set the display info's position
        if (displayInfo.isEnabled()) {
          displayInfo.setX(pos[0]);
          displayInfo.setZ(pos[2]);
/*
          if (shape.getHeight() > 0) {
            if (shape.getPositioning().equals(Positioning.CenterCenter)
                || shape.getPositioning().equals(Positioning.LeftCenter)
                || shape.getPositioning().equals(Positioning.RightCenter)) {
              displayInfo.setY(pos[1] + (shape.getHeight() / 2) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterBottom)
                || shape.getPositioning().equals(Positioning.LeftBottom)
                || shape.getPositioning().equals(Positioning.RightBottom)) {
              displayInfo.setY(pos[1] + (shape.getHeight()) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterTop)
                || shape.getPositioning().equals(Positioning.LeftTop)
                || shape.getPositioning().equals(Positioning.RightTop)) {
              displayInfo.setY(pos[1] + 3);
            }
          } else {
            if (shape.getPositioning().equals(Positioning.CenterCenter)
                || shape.getPositioning().equals(Positioning.LeftCenter)
                || shape.getPositioning().equals(Positioning.RightCenter)) {
              displayInfo.setY(pos[1] + (shape.getWidth() / 2) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterBottom)
                || shape.getPositioning().equals(Positioning.LeftBottom)
                || shape.getPositioning().equals(Positioning.RightBottom)) {
              displayInfo.setY(pos[1] + (shape.getWidth()) + 3);
            } else if (shape.getPositioning().equals(Positioning.CenterTop)
                || shape.getPositioning().equals(Positioning.LeftTop)
                || shape.getPositioning().equals(Positioning.RightTop)) {
              displayInfo.setY(pos[1] + 3);
            }
          }
*/
        }

        // Display only if the object is visible
        if (view.isVisible()) {
          // Display the non-physical
          displaySingleObject(pos, 0, shape, gl, glu);

          if (view.getAttachedList() != null) {
            for (View embeddedView : view.getAttachedList()) {
              double[] embeddedOffset = null;
              embeddedOffset = displayEmbeddedView(embeddedView, shape, pos,
                  null, 0, gl, glu);

              if (embeddedView.getAttachedList() != null) {
                for (View embeddedView2 : embeddedView.getAttachedList()) {
                  displayEmbeddedView(embeddedView2, embeddedView.getShape(),
                      pos, embeddedOffset, 0, gl, glu);
                }
              }
            }
          }

          // Draw the display info
          if (displayInfo.isEnabled()) {
            displayInfo.display(gl, glut);
          }
        }
      }
    }
  }

  /**
   * Maps the dimensions of a non-physical object to fit properly into the
   * drawing area.
   * 
   * @param shape
   * @param gl
   */
  private void mapNonPhysicals(Shape3D shape) {
//    if (!shape.getType().equals(ShapeType.Polygon)
//        && !shape.getType().equals(ShapeType.Polyline)) {
      if (shape.isHeightRelative()) {
        shape.setHeight((shape.getRelativeHeight() / 100)
            * spaceModel.getDrawingArea().getHeight());
      }

      if (shape.isWidthRelative()) {
        shape.setWidth((shape.getRelativeWidth() / 100)
            * spaceModel.getDrawingArea().getWidth());
      }
/*
    } else if (shape.getType().equals(ShapeType.Polygon)
        || shape.getType().equals(ShapeType.Polyline)) {
      // Determine the width and height of a polygon
      double minX = 0;
      double maxX = 0;
      double minY = 0;
      double maxY = 0;

      if (shape.getPointList() != null) {
        if (!shape.getPointList().isEmpty()) {
          minX = shape.getPointList().get(0)[0];
          maxX = shape.getPointList().get(0)[0];
          minY = shape.getPointList().get(0)[1];
          maxY = shape.getPointList().get(0)[1];

          for (double[] point : shape.getPointList()) {
            if (point[0] < minX) {
              minX = point[0];
            }

            if (point[0] > maxX) {
              maxX = point[0];
            }

            if (point[1] < minY) {
              minY = point[1];
            }

            if (point[1] > maxY) {
              maxY = point[1];
            }
          }
        }
      }

      shape.setWidth(maxX - minX);
      shape.setHeight(maxY - minY);
    }
*/
  }

  private void mapEmbedded(Shape3D embeddedShape, Shape3D parent) {
//    if (!embeddedShape.getType().equals(ShapeType.Polygon)
//        && !embeddedShape.getType().equals(ShapeType.Polyline)) {
      if (embeddedShape.isHeightRelative()) {
        embeddedShape.setHeight((embeddedShape.getRelativeHeight() / 100)
            * parent.getHeight());
        embeddedShape.setRecompile(true);
      }

      if (embeddedShape.isWidthRelative()) {
        embeddedShape.setWidth((embeddedShape.getRelativeWidth() / 100)
            * parent.getWidth());
        embeddedShape.setRecompile(true);
      }
/*
    } else if (embeddedShape.getType().equals(ShapeType.Polygon)
        || embeddedShape.getType().equals(ShapeType.Polyline)) {
      // Determine the width and height of a polygon
      double minX = 0;
      double maxX = 0;
      double minY = 0;
      double maxY = 0;

      if (embeddedShape.getPointList() != null) {
        if (!embeddedShape.getPointList().isEmpty()) {
          minX = embeddedShape.getPointList().get(0)[0];
          maxX = embeddedShape.getPointList().get(0)[0];
          minY = embeddedShape.getPointList().get(0)[1];
          maxY = embeddedShape.getPointList().get(0)[1];

          for (double[] point : embeddedShape.getPointList()) {
            if (point[0] < minX) {
              minX = point[0];
            }

            if (point[0] > maxX) {
              maxX = point[0];
            }

            if (point[1] < minY) {
              minY = point[1];
            }

            if (point[1] > maxY) {
              maxY = point[1];
            }
          }
        }
      }

      embeddedShape.setWidth(maxX - minX);
      embeddedShape.setHeight(maxY - minY);
    }
*/
  }

  /**
   * Returns the position of a non-physical object in world coordinates.
   * 
   * @param shape
   */
  private double[] getNonPhysicalObjectPosition(Shape3D shape) {
    double[] position = new double[3];
    double x = 0;
    double y = 0;

    if (shape.isxRelative()) {
      x = (shape.getRelativeX() / 100) * spaceModel.getDrawingArea().getWidth();
    } else {
      x = shape.getX();
    }

    if (shape.isyRelative()) {
      y = (shape.getRelativeY() / 100)
          * spaceModel.getDrawingArea().getHeight();
    } else {
      y = shape.getY();
    }

    position[0] = drawingArea.x1 + x;
    position[1] = drawingArea.y1 + y;
    position[2] = 0;

    return position;
  }

  /**
   * Returns the index of the space component an object is placed onto.
   * 
   * @param x
   * @param y
   */
  private int getSpaceComponentIndex(int x, int y) {
    if (spaceModel.getSpaceType().equals(SpaceType.OneD)) {
      int result = y - Space.ORDINATEBASE;
      return (result >= 0 ? result : 0);
    }

    else if (spaceModel.getSpaceType().equals(SpaceType.TwoD)) {
      return 0;
    }
    
    else if (spaceModel.getSpaceType().equals(SpaceType.TwoDLateralView)) {
        return 0;
    }

    else if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
      int result = (y - Space.ORDINATEBASE) * (int) spaceModel.getxMax()
          + (x - Space.ORDINATEBASE);
      return (result >= 0 ? result : 0);
    }

    return -1;
  }

  private void updateShapeDimensions(int index, Shape3D shape, Physical phy) {
    // Get the space component the physical is placed onto
    SpaceComponent comp = spaceModel.getSpaceComponents().get(index);

    /*
     * Update the shape's dimensions (and those of embedded shapes) if they have
     * changed and regenerate the shape's display list if necessary.
     */
/*    if (shape.getType().equals(ShapeType.Rectangle)
        || shape.getType().equals(ShapeType.Square)
        || shape.getType().equals(ShapeType.Triangle)
        || shape.getType().equals(ShapeType.Circle)
        || shape.getType().equals(ShapeType.Ellipse)
        || shape.getType().equals(ShapeType.RegularPolygon)) {*/
      /*
       * Map the dimensions of the physical (which are in space coordinate
       * system) into the world coordinate system.
       */
      double mappedWidth = comp.getObjectWidth(phy.getWidth());
      double mappedHeight = comp.getObjectHeight(phy.getHeight());

      if (shape.getWidth() != mappedWidth || shape.getHeight() != mappedHeight) {
        // Set the new dimensions
        shape.setWidth(mappedWidth);
        shape.setHeight(mappedHeight);

        // Update embedded shapes as well
        if (shape.getAttachedShape() != null) {

          if (shape.getAttachedShape().getAttachedShape() != null) {

          }
        }

        shape.setRecompile(true);
      }
//    }
/*
    if (shape.getType().equals(ShapeType.Polygon)
        || shape.getType().equals(ShapeType.Polyline)) {
      if (shape.isParsePointString()) {
        // Determine the width and height of a polygon
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;

        if (shape.getPointList() != null) {
          if (!shape.getPointList().isEmpty()) {
            minX = shape.getPointList().get(0)[0];
            maxX = shape.getPointList().get(0)[0];
            minY = shape.getPointList().get(0)[1];
            maxY = shape.getPointList().get(0)[1];

            for (double[] point : shape.getPointList()) {
              if (point[0] < minX) {
                minX = point[0];
              }

              if (point[0] > maxX) {
                maxX = point[0];
              }

              if (point[1] < minY) {
                minY = point[1];
              }

              if (point[1] > maxY) {
                maxY = point[1];
              }
            }

            double width = maxX - minX;
            double height = maxY - minY;

            
            // Map the dimensions of the physical (which are in space coordinate
            // system) into the world coordinate system.
            
            double mappedWidth = comp.getObjectWidth(width);
            double mappedHeight = comp.getObjectHeight(height);

            if (shape.getWidth() != mappedWidth
                || shape.getHeight() != mappedHeight) {
              shape.setWidth(mappedWidth);
              shape.setHeight(mappedHeight);

              // Determine the scale factor
              double scaleWidth = 1;
              double scaleHeight = 1;

              if (shape.getWidth() != 0) {
                scaleWidth = mappedWidth / width;
              }

              if (shape.getHeight() != 0) {
                scaleHeight = mappedHeight / height;
              }

              // Scale each vector
              for (double[] point : shape.getPointList()) {
                point[0] = point[0] * scaleWidth;
                point[1] = point[1] * scaleHeight;
              }

              // Update embedded shapes as well
              if (shape.getAttachedShape() != null) {

                if (shape.getAttachedShape().getAttachedShape() != null) {

                }
              }

              shape.setRecompile(true);
              shape.setParsePointString(false);
            }
          }
        }
      }
    }
*/
  }

  /**
   * Returns the physical objects position in world coordinate space.
   * 
   * @param index
   * @param phy
   */
  private double[] getPhysicalObjectPosition(int index, Physical phy) {
    // Get the space component the physical is placed onto
    SpaceComponent comp = spaceModel.getSpaceComponents().get(index);

    // Get the mapped position
    double[] pos = comp.getWorldCoordinates(phy.getX(), phy.getY());

    if (pos != null) {
      // Add the z index
      pos[2] = phy.getZ();
    }

    return pos;
  }

  /**
   * Returns the rotation of a physical object.
   * 
   * @param index
   * @param phy
   */
  private double getPhysicalObjectRotation(int index, Physical phy) {
    // Get the space component the physical is placed onto
    SpaceComponent comp = spaceModel.getSpaceComponents().get(index);

    // Get the rotation that is dependent on the space model
    double rotation = comp.getRotation(phy.getX(), phy.getY());

    // Add the rotation that comes from the physical itself
    rotation += phy.getRotZ();

    return rotation;
  }

  /**
   * Displays a single object, either physical or non-physical.
   * 
   * @param position
   * @param rotation
   * @param shape
   * @param gl
   * @param glu
   */
  private void displaySingleObject(double[] position, double rotation,
      Shape3D shape, GL2 gl, GLU glu) {
    // Save the current model view matrix
    gl.glPushMatrix();

    // Apply matrix translation
    gl.glTranslated(position[0], position[1], position[2]);

    // Apply matrix rotation
    gl.glRotated(rotation, 0.0f, 0.0f, 1.0f);

    // Display the shape
    shape.display(gl, glu);

    // Restore the model view matrix
    gl.glPopMatrix();
  }

  private void displayEmbeddedObject(double[] position, double[] offset,
      double rotation, Shape3D shape, String label, GL2 gl, GLU glu) {
    // Save the current model view matrix
    gl.glPushMatrix();

    // Apply matrix translation
    gl.glTranslated(position[0], position[1], position[2]);

    // Apply matrix rotation
    gl.glRotated(rotation, 0.0f, 0.0f, 1.0f);

    gl.glTranslated(offset[0], offset[1], 0);

    // Display the shape
    shape.display(gl, glu);

    // Display the label of an embedded view
    if (label != null) {
      gl.glColor3d(0, 0, 0);

      gl.glPushMatrix();
      gl.glLoadIdentity();

/*      if (shape.getPositioning().equals(Positioning.CenterCenter)
          || shape.getPositioning().equals(Positioning.LeftCenter)
          || shape.getPositioning().equals(Positioning.RightCenter)) {
        gl.glRasterPos2d(position[0] + offset[0], position[1] + offset[1]
            - shape.getHeight() / 2 - 15);
      } else if (shape.getPositioning().equals(Positioning.CenterBottom)
          || shape.getPositioning().equals(Positioning.LeftBottom)
          || shape.getPositioning().equals(Positioning.RightBottom)) {
        gl.glRasterPos2d(position[0] + offset[0], position[1] + offset[1] - 15);
      } else if (shape.getPositioning().equals(Positioning.CenterTop)
          || shape.getPositioning().equals(Positioning.LeftTop)
          || shape.getPositioning().equals(Positioning.RightTop)) {
        gl.glRasterPos2d(position[0] + offset[0], position[1] + offset[1]
            - shape.getHeight() - 15);
      }
*/
      glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, label);

      gl.glPopMatrix();
    }

    // Restore the model view matrix
    gl.glPopMatrix();
  }

  /**
   * Returns the ID of an object that was selected by a mouse click.
   * 
   * @param gl
   * @param glu
   */
  private int pick(GL2 gl, GLU glu) {
    // Set up the selection buffer
    int selectionBufferArray[] = new int[SELECTION_BUFFER_SIZE];
    IntBuffer selectionBuffer = BufferUtil.newIntBuffer(SELECTION_BUFFER_SIZE);

    // Number of hits
    int hits = 0;

    // Get viewport dimensions
    int viewport[] = new int[4];
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

    // Create the selection buffer and switch into selection mode
    gl.glSelectBuffer(SELECTION_BUFFER_SIZE, selectionBuffer);
    gl.glRenderMode(GL2.GL_SELECT);

    // Initialize the name stack
    gl.glInitNames();
    gl.glPushName(-1);

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();

    // Create a small 1x1 pixel picking region around the cursor
    glu.gluPickMatrix(pickPoint.x, viewport[3] - pickPoint.y - 1, 1, 1,
        viewport, 0);

    // Set up the projection matrix
    gl.glOrtho(-viewport[2] / 2, viewport[2] / 2, -viewport[3] / 2,
        viewport[3] / 2, 1, -100);

    // Draw all objects
    displayObjects(gl, glu);

    // Restore the old matrix settings
    gl.glPopMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glFlush();

    // Get the number of hits
    hits = gl.glRenderMode(GL2.GL_RENDER);

    // Get the content of the selection buffer
    selectionBuffer.get(selectionBufferArray);

    return processHits(hits, selectionBufferArray);
  }

  /**
   * Returns the ID of the topmost element inside of the selection buffer.
   * 
   * @param hits
   * @param selectionBuffer
   */
  private int processHits(int hits, int selectionBuffer[]) {
    if (hits > 0) {
      /*
       * Have a look at the content of the selection buffer to understand this
       * formula.
       */
      int pos = (hits * 4) + ((hits * (hits + 1)) / 2);

      return selectionBuffer[pos - 1];
    } else {
      return 0;
    }
  }

  /**
   * Parses the point string for polygons and polylines into a array list of
   * vertices.
   * 
   * @param str
   */
/*  private ArrayList<double[]> parsePointString(String str) {
    ArrayList<double[]> pointList = new ArrayList<double[]>();
*/
    // Check if the point string is valid
    // NOTE: check the regular expression while does not works fine...
    /*
     * if (!str.matches(
     * "[-]?[\\d]+[,][-]?[\\d]+([,][-]?[\\d]+){0,1}([\\s]{1}[-]?[\\d]+[,][-]?[\\d]+([,][-]?[\\d]+){0,1})+"
     * )) { System.out
     * .println("Visualization Error: Point description is not valid! (" + str +
     * ")"); return null; }
     */
/*
    // get the points list
    String[] points = str.split(" ");

    if (points == null || points.length < 1) {
      System.out.println("Visualization warning: points list is empty!");
    }

    // split split a points in its coordinate components
    for (int i = 0; i < points.length; i++) {
      double[] coordinates = new double[3];

      // replace any existing space
      points[i] = points[i].trim();

      // was just a space...
      if (points[i].length() < 1) {
        continue;
      }

      // replace all spaces with nothing
      points[i] = points[i].replaceAll(" ", "");

      // now split the coordinates and extract each coordinate
      String[] coordinatesStr = points[i].split(",");

      // translate coordinates form string to double
      coordinates[0] = (coordinatesStr.length > 0
          && coordinatesStr[0].length() > 0 ? Double.valueOf(coordinatesStr[0])
          : 0.0d);
      coordinates[1] = (coordinatesStr.length > 1
          && coordinatesStr[1].length() > 0 ? Double.valueOf(coordinatesStr[1])
          : 0.0d);
      coordinates[2] = (coordinatesStr.length > 2
          && coordinatesStr[2].length() > 0 ? Double.valueOf(coordinatesStr[2])
          : 0.0d);

      // Add the point to the result list
      pointList.add(coordinates);
    }
   
    return pointList;
  }
*/

  public KeyboardEventHandler getKeyboardEvtHandler() {
    return keyboardEvtHandler;
  }

  public MouseEventHandler getMouseEvtHandler() {
    return mouseEvtHandler;
  }

  public SpaceModel getSpaceModel() {
    return spaceModel;
  }

  public void setSpaceModel(SpaceModel spaceModel) {
    this.spaceModel = spaceModel;
  }

  public Map<Long, Objekt> getObjMap() {
    return objMap;
  }

  public void setObjMap(Map<Long, Objekt> objMap) {
    this.objMap = objMap;
  }

  public Map<Long, View> getViewMap() {
    return viewMap;
  }

  public void setViewMap(Map<Long, View> viewMap) {
    this.viewMap = viewMap;
  }

  public File getProjectDirectory() {
    return projectDirectory;
  }

  public void setProjectDirectory(File projectDirectory) {
    this.projectDirectory = projectDirectory;
  }

  public Point getPickPoint() {
    return pickPoint;
  }

  public void setPickPoint(Point pickPoint) {
    this.pickPoint = pickPoint;
  }

  public boolean isPickingMode() {
    return pickingMode;
  }

  public void setPickingMode(boolean pickingMode) {
    this.pickingMode = pickingMode;
  }

  public VisualizationPanel getVisPanel() {
    return visPanel;
  }

  public void setVisPanel(VisualizationPanel visPanel) {
    this.visPanel = visPanel;
  }

}
