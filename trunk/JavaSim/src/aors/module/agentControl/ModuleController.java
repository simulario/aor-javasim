package aors.module.agentControl;

import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.data.java.SimulationEvent;
import aors.data.java.SimulationStepEvent;
import aors.model.agtsim.proxy.agentControl.AgentControlBroker;
import aors.model.agtsim.proxy.agentControl.AgentControlListener;
import aors.model.agtsim.proxy.agentControl.CoreAgentController;
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
public class ModuleController implements Module, AgentControlListener {

	/**
	 * The project's path.
	 */
	private String projectPath;

	/**
	 * Reference to the base class of the module's gui.
	 */
	private GUIController guiController;

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
	private Map<Boolean, Map<Long, CoreAgentController>> agentControllers;

	/*******************/
	/*** constructor ***/
	/*******************/

	/**
	 * Instantiates the controller and registers itself at the
	 * {@link AgentControlBroker} as {@link AgentControlListener}.
	 */
	public ModuleController() {
		this.projectPath = null;
		this.guiController = new GUIController(this);
		this.initIdentifier = true;
		this.agentControllers = new HashMap<Boolean, Map<Long,
			CoreAgentController>>();

		// registers itself at the agent control broker
		AgentControlBroker.getInstance().addAgentControlListener(this);
	}

//	/**
//	 * Instantiales the class with a reference to the visualization module's
//	 * main class.
//	 * For the moment this constructor just ignores the referenced module.
//	 * @param vizualisationModule
//	 */
//	public ModuleController(Module vizualisationModule) {
//		this();
//	}

	/**********************************/
	/*** methods related to the gui ***/
	/**********************************/

	/**
	 * Returns the reference to the module's gui component.
	 * @return the module's gui component
	 */
	@Override
	public GUIController getGUIComponent() {
		return this.guiController;
	}

	/**
	 * Returns the project path if there is one or <code>null</code>
	 * @return the project path or <code>null</code> if no path is known
	 */
	public String getProjectPath() {
		return this.projectPath;
	}

	/**
	 * Updates the reference to the project's directory.
	 * @param projectDirectory
	 */
	@Override
	public void simulationProjectDirectoryChanged(File projectDirectory) {
		if(projectDirectory != null) {
			this.projectPath = projectDirectory.getPath();
		}
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
		this.guiController.update();
	}

	/**
	 * Notifies that the simulation was stoped.
	 */
	@Override
	public void simulationEnded() {

		/* With the end of the simulation we no longer need the registered agent
		 * controllers so we can remove then values.
		 */
		this.agentControllers.remove(this.initIdentifier);

		/* Notifies the gui that it should reset, because the current simulation
		 * has ended.
		 */
		this.guiController.reset();
	}

	/**********************************************************************/
  /*** methods related to registration and usage of agent controllers ***/
	/**********************************************************************/

	/**
	 * Registers an agent controller with this class.
	 * @param agentController
	 */
	@Override
	public void agentControllerInitialized(CoreAgentController agentController) {
		if(!this.agentControllers.containsKey(this.initIdentifier)) {
			this.agentControllers.put(this.initIdentifier,
				new HashMap<Long, CoreAgentController>());
		}
		this.agentControllers.get(this.initIdentifier).put(
			agentController.getAgentId(), agentController);
	}

	/**
	 * Return all registered agent controllers identified by their agent's id.
	 * @return the map of agent controllers
	 */
	public Map<Long, CoreAgentController>	getAgentControllers() {
		return this.agentControllers.get(this.initIdentifier);
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
	public void simulationStepStart(long stepNumber) {}

	@Override
	public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {}
}