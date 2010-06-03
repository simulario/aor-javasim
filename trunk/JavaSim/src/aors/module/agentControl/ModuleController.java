package aors.module.agentControl;

import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.data.java.SimulationEvent;
import aors.data.java.SimulationStepEvent;
import aors.model.agtsim.AgentSubject;
import aors.model.envevt.EnvironmentEvent;
import aors.module.Module;
import aors.module.agentControl.gui.GUIController;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the module's main class. It receives all the messsages from
 * the simulator about its state and is responsible for the module's
 * initialization.
 * @author Thomas Grundmann
 */
public class ModuleController implements Module {

	/**
	 * Reference to the current instance of this controller. For each running
	 * AOR simulator there is at most one instance of this class.
	 */
	private static ModuleController instance = null;

	/**
	 * Reference to the project directory.
	 */
	private File projectDirectory;	

	/**
	 * Reference to the base class of the module's gui.
	 */
	private GUIController gui;

	/**
	 * This variable is a switch to solve the multiple initializiation problem.
	 * Its value is alternated with each new simulation initilization. Using this
	 * variable it can be ensured that the actual set of initialized agents is
	 * used.
	 */
	private boolean initIdentifier;
	
	/**
	 * This map stores all agent controllers, that were initialized during the
	 * current simulation.
	 */
	private Map<Boolean, Map<Long, AgentController<? extends AgentSubject>>>
		agentControllers;


	/*****************************************************************/
	/*** Constructors and methods to get an instance of this class ***/
	/*****************************************************************/

	/**
	 * Instantiates the class.
	 */
	public ModuleController() {		
		ModuleController.instance = this;

		this.projectDirectory = null;
		this.gui = new GUIController(this);
		
		this.initIdentifier = true;
		this.agentControllers = new HashMap<Boolean, Map<Long, AgentController
			<? extends AgentSubject>>>();
	}

	/**
	 * Instantiales the class with a reference to the visualization module's
	 * main class.
	 * For the moment this constructor just ignores the referenced module.
	 * @param vizualisationModule
	 */
	public ModuleController(Module vizualisationModule) {
		this();
	}

	/**
	 * Returns the current instance of the module controller.
	 * @return the module controller
	 */
	public static ModuleController getInstance() {
		return ModuleController.instance;
	}


	/**********************************/
	/*** methods related to the gui ***/
	/**********************************/

	/**
	 * Returns the reference to the module's gui component.
	 * @return the module's gui component
	 */
	@Override
	public GUIController getGUIComponent() {
		return this.gui;
	}

	/**
	 * Returns the project path if there is one or <code>null</code>
	 * @return the project path or <code>null</code> if no path is known
	 */
	public String getProjectPath() {
		if(projectDirectory != null) {
			return projectDirectory.getPath();
		}
		return null;
	}

	/**
	 * Updates the reference to the project's directory.
	 * @param projectDirectory
	 */
	@Override
	public void simulationProjectDirectoryChanged(File projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	/****************************************************/
	/*** methods related to simulation initialization ***/
	/****************************************************/

	/**
	 * Notifies that the simulation was initialized.
	 * @param initialState
	 */
	@Override
	public void simulationInitialize(InitialState initialState) {

		/* When a simulation is initialized, each agent controller that is
		 * initialized during that phase registers itself with this class. If
		 * for some reason the current initialization directly follows a previous
		 * one (without running the simulation in between) both the controllers that
		 * were created in the previous step and the ones that were created in the
		 * current step would be mixed. To avoid that problem we use the switch
		 * initIdentifier as a key: Every agent controller that was added during the
		 * current initialization has the value of initIdentifier as key and any
		 * agent controller that was added during the previous initialization has
		 * the value of !initIdentifier as its key. Since these references are no
		 * longer needed they are removed, so that we can use !initIdentifier as
		 * key value for all the agent controllers that would register with this
		 * class in a following initialization phase.
		 */
		this.initIdentifier = !this.initIdentifier;
		this.agentControllers.remove(this.initIdentifier);
	}

	/**
	 * Notifies that the simulation was started.
	 */
	@Override
	public void simulationStarted() {

		/* Since the simulation is started now we need access to the registered
		 * agent controllers. So the switch's value hat to be changed to refer to
		 * the current entries.
		 */
		this.initIdentifier = !this.initIdentifier;

		/* Notifies the gui that it should update, because a new simulation has
		 * started.
		 */
		this.gui.update();
	}


	/**
	 * Notifies that the simulation was stoped.
	 */
	@Override
	public void simulationEnded() {

		/* With the end of the simulation we no longer need the registered agent
		 * controllers so we can change the switch's value back.
		 */
		this.agentControllers.remove(this.initIdentifier);

		/* Notifies the gui that it should update, because the current simulation
		 * has ended.
		 */
		this.gui.reset();
	}

	/**********************************************************************/
  /*** methods related to registration and usage of agent controllers ***/
	/**********************************************************************/

	/**
	 * Registers an agent controller with this class.
	 * @param agentController
	 */
	public void registerAgentController(AgentController<? extends AgentSubject>
		agentController) {
		if(!this.agentControllers.containsKey(initIdentifier)) {
			this.agentControllers.put(initIdentifier,
				new HashMap<Long, AgentController<? extends AgentSubject>>());
		}
		this.agentControllers.get(initIdentifier).put(agentController.getAgentId(),
			agentController);
	}

	/**
	 * Unregisters an agent controller from this class.
	 * @param agentController
	 */
	public void unregisterAgentController(AgentController<? extends AgentSubject>
		agentController) {
		if(this.agentControllers.containsKey(initIdentifier)) {
			this.agentControllers.get(initIdentifier).remove(agentController.
				getAgentId());
		}
	}

	/**
	 * Return all registered agent controllers identified by their agent's id.
	 * @return the map of agent controllers
	 */
	public Map<Long, AgentController<? extends AgentSubject>>
		getAgentControllers() {
		return this.agentControllers.get(initIdentifier);
	}

	/********************************************/
	/*** unused methods inherited from Module ***/
	/********************************************/

	@Override
	public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {}

	@Override
	public void objektInitEvent(ObjektInitEvent objInitEvent) {}

	@Override
	public void simulationDomOnlyInitialization(SimulationDescription
		simulationDescription) {}

	@Override
	public void simulationEnvironmentEventOccured(EnvironmentEvent
		environmentEvent) {}

	@Override
	public void simulationInfosEvent(SimulationEvent simulationEvent) {}


	@Override
	public void simulationPaused(boolean pauseState) {}

	@Override
	public void simulationStepStart(long stepNumber) {
//		if(this.controlledAgent != null) {
//			controlledAgent.updateView();
//		}
	}

	@Override
	public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
//		if(controlledAgent != null) {
//			controlledAgent.performUserActions();
//		}
//		System.out.println("=======");
	}
}