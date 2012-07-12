package aors.module.visopengl3d.space.model;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.SpaceType;
import aors.module.visopengl3d.space.component.SpaceComponent;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.utility.Offset;
import aors.module.visopengl3d.xml.XMLReader;

/**
 * Base class of a space model.
 * 
 * @author Sebastian Mucha, Susanne Schölzel
 * @since March 17th, 2010
 * 
 */
public abstract class SpaceModel {

  // General space model
  protected GeneralSpaceModel generalSpaceModel;

  // Space type
  protected SpaceType spaceType;

  // Dimensions
  protected double xMax, yMax;

  // Multiplicity
  protected int multiplicity = 1;

  // Flag indicating that the display list needs to be recompiled
  protected boolean recompile;

  // Flag indicating that the space model was initialized
  protected boolean initialized;

  // Space components
  protected ArrayList<SpaceComponent> spaceComponents = new ArrayList<SpaceComponent>();

  // Drawing area
  protected Offset drawingArea;
  
  protected Offset usedDrawingArea;

  // Display list
  protected int displayList = -1;

  /**
   * Creates a space model. It is initialized through the simulations general
   * space model and the XML simulation description.
   * 
   * @param generalSpaceModel
   * @param reader
   */
  public static SpaceModel createSpaceModel(
      GeneralSpaceModel generalSpaceModel, XMLReader reader) {
    SpaceModel spaceModel = null;

    // Create a one dimensional space model
    if (generalSpaceModel.getSpaceType().equals(SpaceType.OneD)) {
      spaceModel = new OneDimSpaceModel();
    }

    // Create a two dimensional, continuous space model
    else if (generalSpaceModel.getSpaceType().equals(SpaceType.TwoD)) {
      spaceModel = new TwoDimSpaceModel();
    }
    
    // Create a two dimensional, continuous lateral space model
    else if (generalSpaceModel.getSpaceType().equals(SpaceType.TwoDLateralView)) {
          spaceModel = new TwoDimLateralViewSpaceModel();
        }

    // Create a two dimensional, discrete space model
    else if (generalSpaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
      spaceModel = new GridSpaceModel();
    }

    // Get space model attributes
    if (spaceModel != null) {
      spaceModel.setGeneralSpaceModel(generalSpaceModel);
      spaceModel.setMultiplicity(generalSpaceModel.getMultiplicity());
      spaceModel.setxMax(generalSpaceModel.getXMax());
      spaceModel.setyMax(generalSpaceModel.getYMax());
      
      if (spaceModel.getSpaceType().equals(SpaceType.OneD)) {
        spaceModel.setyMax(spaceModel.getxMax());
      }
      
      // Get the space view
      spaceModel.setSpaceView(reader.getSpaceView(spaceModel.getSpaceType()));
    }

    return spaceModel;
  }

  /**
   * Displays the space model.
   * 
   * @param gl
   * @param glu
   */
  public void display(GL2 gl, GLU glu) {
    if (displayList != -1) {
      gl.glCallList(displayList);
    }
  }

  /**
   * Compiles the space model's display list.
   * 
   * @param gl
   * @param glu
   */
  public void compileDisplayList(GL2 gl, GLU glu) {
	  
    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    gl.glNewList(displayList, GL2.GL_COMPILE);
    // Save attribute states
    gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

    for (SpaceComponent comp : spaceComponents) {
      comp.display(gl, glu);
    }

    // Restore attribute states
    gl.glPopAttrib();
    gl.glEndList();
  }

  /**
   * Initializes or reinitializes the space model.
   * 
   * @param gl
   * @param glu
   */
  public abstract void initializeSpaceModel(GL2 gl, GLU glu);

  public SpaceType getSpaceType() {
    return spaceType;
  }

  public void setSpaceType(SpaceType spaceType) {
    this.spaceType = spaceType;
  }

  public abstract SpaceView getSpaceView();

  public abstract void setSpaceView(SpaceView spaceView);

  public double getxMax() {
    return xMax;
  }

  public void setxMax(double xMax) {
    this.xMax = xMax;
  }

  public double getyMax() {
    return yMax;
  }

  public void setyMax(double yMax) {
    this.yMax = yMax;
  }

  public int getMultiplicity() {
    return multiplicity;
  }

  public void setMultiplicity(int multiplicity) {
    this.multiplicity = multiplicity;
  }

  public ArrayList<SpaceComponent> getSpaceComponents() {
    return spaceComponents;
  }

  public void setSpaceComponents(ArrayList<SpaceComponent> spaceComponents) {
    this.spaceComponents = spaceComponents;
  }

  public Offset getDrawingArea() {
    return drawingArea;
  }

  public void setDrawingArea(Offset drawingArea) {
    this.drawingArea = drawingArea;
  }
  
  public Offset getUsedDrawingArea() {
    return usedDrawingArea;
  }

  public void setUsedDrawingArea(Offset usedDrawingArea) {
    this.usedDrawingArea = usedDrawingArea;
  }

  public int getDisplayList() {
    return displayList;
  }

  public void setDisplayList(int displayList) {
    this.displayList = displayList;
  }

  public GeneralSpaceModel getGeneralSpaceModel() {
    return generalSpaceModel;
  }

  public void setGeneralSpaceModel(GeneralSpaceModel generalSpaceModel) {
    this.generalSpaceModel = generalSpaceModel;
  }

  public boolean isRecompile() {
    return recompile;
  }

  public void setRecompile(boolean recompile) {
    this.recompile = recompile;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

}
