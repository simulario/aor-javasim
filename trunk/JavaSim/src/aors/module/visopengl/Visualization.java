package aors.module.visopengl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.media.opengl.awt.GLCanvas;

import org.w3c.dom.Node;

import aors.GeneralSpaceModel;
import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.evt.ControllerEvent;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.logger.model.SimulationStep;
import aors.model.envevt.EnvironmentEvent;
import aors.module.Module;
import aors.module.visopengl.engine.Engine;
import aors.module.visopengl.engine.UpdateManager;
import aors.module.visopengl.gui.DescriptionPanel;
import aors.module.visopengl.gui.GUIComponent;
import aors.module.visopengl.gui.SpaceModelPanel;
import aors.module.visopengl.gui.VisualizationPanel;
import aors.module.visopengl.lang.LanguageManager;
import aors.module.visopengl.shape.View;
import aors.module.visopengl.space.model.SpaceModel;
import aors.module.visopengl.space.model.TwoDimSpaceModel;
import aors.module.visopengl.space.view.SpaceView;
import aors.module.visopengl.xml.XMLReader;

/**
 * Base component of the visualization module.
 * 
 * @author Sebastian Mucha
 * @since March 16th, 2010
 * 
 */
public class Visualization implements Module {
  // module temporarily folder
  public static final String localTmpPath = "visOpenGL";

  // GUI component
  private GUIComponent gui = new GUIComponent(this);

  // the visualization panel reference - to lower function calls
  private VisualizationPanel visPanel = gui.getContent()
      .getVisualizationPanel();

  // the GLCanvas reference - to lower function calls
  private GLCanvas canvas = gui.getContent().getDrawingPanel().getCanvas();

  // Engine
  private Engine engine = gui.getContent().getDrawingPanel().getEngine();

  // XML document reader
  private XMLReader reader;

  // the view definition XML DOM nodes
  Map<String, Node> views = null;

  // Project directory
  private File projectDirectory;

  // default activation flag -views defined or not
  private boolean defaultActivation = true;

  // Update manager
  private UpdateManager uptManager = new UpdateManager();

  // Current simulation step
  private long currentSimulationStep;

  // the time collector for one second
  private long oneSecondTime = 0;

  // display frames per second
  private int fps = 0;

  // the simulation step start time
  long simStepStartTime = 0;

  @Override
  public Object getGUIComponent() {
    return gui;
  }

  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    SimulationStep currentStep = simulationStepEvent.getSimulationStep();
    long stepTime = currentStep.getStepTime();
    visPanel.updateSimStepLabel(stepTime);

    // process current step
    uptManager.processStep(currentStep);

    // Render a frame
    canvas.display();

    // get the end rendering time
    long endTime = (new Date()).getTime();

    // compute and update FPS
    this.oneSecondTime += (endTime - simStepStartTime);
    if (this.oneSecondTime > 1000) {
      visPanel.updateFrameRateLabel(this.fps);
      this.oneSecondTime = 0;
      this.fps = 0;
    } else {
      this.fps++;
    }
  }

  @Override
  public void simulationStepStart(long stepNumber) {
    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // get the sim start time
    simStepStartTime = (new Date()).getTime();

    currentSimulationStep = stepNumber;
  }

  @Override
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {

    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Create an XML document reader
    reader = new XMLReader(simulationDescription.getDom(), projectDirectory);

    // read views
    this.views = reader.readViews();

    // update manager knows now the view nodes definitions
    uptManager.reset();
    uptManager.setViewNodeMap(views);
  }

  @Override
  public void simulationEnded() {
    // disable the on/off feature
    this.engine.getVisPanel().setEnabledOnOffFeature(true);

    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Update labels
    gui.getContent().getVisualizationPanel().updateFrameRateLabel(0);
    gui.getContent().getVisualizationPanel().updateSimStepLabel(0);
  }

  @Override
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent) {
  }

  @Override
  public void simulationInfosEvent(SimulationEvent simulationEvent) {
  }

  @Override
  public void simulationInitialize(InitialState initialState) {
    // by default the module is active
    this.defaultActivation = true;

    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Try to create and initialize the space model
    SpaceModel sm = null;
    SpaceView sw = null;

    if (initialState.getSpaceModel() == null) {
      // Don't display the space model panel
      gui.getContent().getSpaceModelPanel().setVisible(false);

      // Create a two dimensional, continuous space model
      sm = new TwoDimSpaceModel();
      sw = reader.getSpaceView(sm.getSpaceType());
      sm.setSpaceView(sw);
    }

    else {
      // Update the space model panel
      SpaceModelPanel panel = gui.getContent().getSpaceModelPanel();
      GeneralSpaceModel gsm = initialState.getSpaceModel();
      panel.updateSpaceTypeLabel(gsm.getSpaceType());
      panel.updateGeometryLabel(gsm.getGeometry());
      panel.updateDistanceUnitLabel(gsm.getSpatialDistanceUnit());
      panel.updateMultiplicityLabel(gsm.getMultiplicity());
      panel.updateXMaxLabel(gsm.getXMax());
      panel.updateYMaxLabel(gsm.getYMax());

      // Create the space model
      sm = SpaceModel.createSpaceModel(gsm, reader);
    }

    // for any reason, there are no views (should be already created while are
    // created on notification of DOM initialization).
    if (this.views == null) {
      this.views = reader.readViews();
    }

    if ((this.views == null || this.views.size() < 1)
        && (sm.getSpaceView().getPropertyMaps() == null || sm.getSpaceView()
            .getPropertyMaps().size() < 1)) {
      sm = null;
      DescriptionPanel descPanel = new DescriptionPanel();
      descPanel.setDescriptionData(reader.getSimulationDescriptionInfo());
      gui.setViewportView(descPanel);
      this.defaultActivation = false;
    } else {
      // Create a viewport
      gui.setViewportView(gui.getContent());
      gui.getContent()
          .setDescriptionData(reader.getSimulationDescriptionInfo());
    }

    uptManager.setSpaceModel(sm);
    uptManager.setViewNodeMap(this.views);
    uptManager.initializeObjects(initialState, reader);

    // Set up the engine
    engine.setProjectDirectory(projectDirectory);
    engine.setSpaceModel(sm);
    engine.setVisPanel(gui.getContent().getVisualizationPanel());
    engine.setObjMap(uptManager.getObjMap());
    engine.setViewMap(uptManager.getViewMap());
  }

  @Override
  public void simulationPaused(boolean pauseState) {
    // nothing to do here
  }

  @Override
  public void simulationProjectDirectoryChanged(File projectDirectory) {
    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    this.projectDirectory = projectDirectory;
  }

  @Override
  public void simulationStarted() {
    // disable the on/off feature
    this.engine.getVisPanel().setEnabledOnOffFeature(false);
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Mark object as destroyed
    uptManager.destroyObject(objektDestroyEvent.getSource(),
        currentSimulationStep);
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Initialize the newly created object
    Long id = uptManager.initializeCreatedObject(objInitEvent.getSource(),
        reader);

    /*
     * Add the newly created objects view into the map storing views and the
     * simulation step they have to become visible at.
     */
    if (id != null) {
      if (uptManager.getCreatedMap().get(currentSimulationStep) != null) {
        if (uptManager.getViewMap().get(id) != null) {
          uptManager.getCreatedMap().get(currentSimulationStep).add(
              uptManager.getViewMap().get(id));
        }
      } else {
        if (uptManager.getViewMap().get(id) != null) {
          ArrayList<View> list = new ArrayList<View>();
          list.add(uptManager.getViewMap().get(id));
          uptManager.getCreatedMap().put(currentSimulationStep, list);
        }
      }
    }
  }

  /**
   * Utility method to check if the module must or must not be active.
   * 
   * @return true if the module must be active, false otherwise.
   */
  private boolean isModulEnabled() {
    if (engine.getVisPanel() == null) {
      engine.setVisPanel(gui.getContent().getVisualizationPanel());
    }

    boolean enabled = this.engine.getVisPanel().getEnableVisThread()
        .isSelected();

    return enabled && this.defaultActivation;
  }

  @Override
  public void notifyEvent(ControllerEvent event) {
    // nothing to do here
  }

  /**
   * Got notification about language change, so refresh the GUI.
   * 
   * @param languageCode
   *          the new language code
   * @param country
   *          the country for this language
   */
  public void notifyLanguageChange(String languageCode, String country) {
    LanguageManager.changeLanguage(languageCode, country);
    
    gui.getContent().refreshGUI();

    // update canvas - problems with SWING GUI updates...
    canvas.invalidate();
    canvas.repaint();

   canvas.display();
   canvas.requestFocus();

  }
}
