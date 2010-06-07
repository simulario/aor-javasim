package aors.module.visopengl;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Node;

import aors.GeneralSpaceModel;
import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.DataBus;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.data.java.SimulationEvent;
import aors.data.java.SimulationStepEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.module.Module;
import aors.module.evt.ModuleEvent;
import aors.module.visopengl.engine.Engine;
import aors.module.visopengl.engine.UpdateManager;
import aors.module.visopengl.gui.DescriptionPanel;
import aors.module.visopengl.gui.GUIComponent;
import aors.module.visopengl.gui.SpaceModelPanel;
import aors.module.visopengl.gui.VisualizationThread;
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

  // GUI component
  private GUIComponent gui = new GUIComponent(this);

  // Engine
  private Engine engine = gui.getContent().getDrawingPanel().getEngine();

  // Visualization thread
  private VisualizationThread visThread;

  // XML document reader
  private XMLReader reader;

  // Project directory
  private File projectDirectory;

  // default activation flag -views defined or not
  private boolean defaultActivation = true;

  // Update manager
  private UpdateManager uptManager = new UpdateManager();

  // Current simulation step
  private long currentSimulationStep;

  // keep an instance of the data-bus object from initial state
  private DataBus dataBus = null;

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

    // Add a simulation step into the step buffer
    visThread.getBuffer().add(simulationStepEvent.getSimulationStep());
  }

  @Override
  public void simulationStepStart(long stepNumber) {
    currentSimulationStep = stepNumber;
  }

  @Override
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {
  }

  @Override
  public void simulationEnded() {
    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Make sure all steps where displayed
    while (!visThread.getBuffer().isEmpty()) {
      continue;
    }

    // activate the on/off feature
    this.engine.getVisPanel().setEnabledOnOffFeature(true);

    // Stop the visualization thread
    visThread.setVisRunning(false);

    // Make sure the thread is released only after it has died
    while (visThread.isAlive()) {
      continue;
    }

    // Update labels
    gui.getContent().getVisualizationPanel().updateFrameRateLabel(0);
    gui.getContent().getVisualizationPanel().updateSimStepLabel(0);
    gui.getContent().getVisualizationPanel().updateBufferSizeLabel(0);

    // Release the visualization thread
    visThread = null;
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

    // Create an XML document reader
    reader = new XMLReader(initialState.getSimulationDescription().getDom(),
        projectDirectory);

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

      // System.out.println("No space model defined, using default one.");
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

    // Set up the update manager
    uptManager.reset();
    Map<String, Node> views = reader.readViews();

    if ((views == null || views.size() < 1)
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
    uptManager.setViewNodeMap(views);
    uptManager.initializeObjects(initialState, reader);

    // Set up the engine
    engine.setProjectDirectory(projectDirectory);
    engine.setSpaceModel(sm);
    engine.setVisPanel(gui.getContent().getVisualizationPanel());
    engine.setObjMap(uptManager.getObjMap());
    engine.setViewMap(uptManager.getViewMap());

    // store the data-bus
    this.dataBus = initialState.getDatabus();
  }

  @Override
  public void simulationPaused(boolean pauseState) {
    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Update the visualization threads pause state
    visThread.setVisPaused(pauseState);
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

    // module is inactive
    if (!this.isModulEnabled()) {
      return;
    }

    // Create and start the visualization thread
    visThread = new VisualizationThread(gui);
    visThread.setUptManager(uptManager);
    visThread.start();
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

    if (visThread != null) {
      if (visThread.isVisRunning()) {
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

  /**
   * Send module event to the module event listeners.
   * 
   * @param moduleEvent
   *          the event to be sent to listeners
   */
  public void notifyVisualizationModuleEvent(ModuleEvent moduleEvent) {
    if (this.dataBus == null) {
      System.out
          .println("Visualization: Warning! Visualization module can't send module events!");
      return;
    }
    // notify listeners about the event
    this.dataBus.notifyModuleEvent(moduleEvent);
  }
}
