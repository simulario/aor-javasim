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
import aors.module.agentControl.gui.GUIManager;
import aors.util.jar.JarUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModuleController implements Module {

	private GUIManager gui;
	private File projectDirectory;

	private boolean initIdentifier;
	private Map<Boolean, Set<AgentController<? extends AgentSubject>>> agentControllers;
	private AgentController<? extends AgentSubject> controlledAgent;

	private static ModuleController instance = null;

	public ModuleController() {
		if (instance != null) {
			instance.reset();
		}
		ModuleController.instance = this;

		this.gui = new GUIManager(this);
		this.projectDirectory = null;

		this.agentControllers = new HashMap<Boolean, Set<AgentController<? extends AgentSubject>>>();
		this.initIdentifier = true;
		this.controlledAgent = null;

		// initialize module libraries
		initModuleLibraries();
	}

	/**
	 * Initialize the module libraries by unpacking the Jars, loading and
	 * setting class paths.
	 */
	private void initModuleLibraries() {
		// local path in the temporarily directory for this module
		String localTmpPath = "agentControlModule";

		// path to jar
		String jarPath = System.getProperty("user.dir") + File.separator
				+ "modules" + File.separator + "agentControlModule.jar";

		// extract the jar files for sound module
		try {
			JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
					"core-renderer-r8.jar");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// add this path in the library path...
		JarUtil.setLibraryPath(localTmpPath);

		// load jars from that temporarily directory
		JarUtil.loadJar(localTmpPath, "core-renderer-r8.jar");
	}

	public ModuleController(Module vizualisationModule) {
		this();
	}

	private void reset() {
	}

	public static ModuleController getInstance() {
		return ModuleController.instance;
	}

	@Override
	public GUIManager getGUIComponent() {
		return this.gui;
	}

	@Override
	public void simulationInitialize(InitialState initialState) {
		this.initIdentifier = !this.initIdentifier;
		this.agentControllers.remove(this.initIdentifier);
	}

	@Override
	public void simulationStarted() {
		this.initIdentifier = !this.initIdentifier;
		gui.setSelectionView();
	}

	@Override
	public void simulationEnded() {
		this.initIdentifier = !this.initIdentifier;
		this.gui.reset();
	}

	@Override
	public void simulationStepStart(long stepNumber) {
		if (this.controlledAgent != null) {
			controlledAgent.updateView();
		}
	}

	@Override
	public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
		// if(controlledAgent != null) {
		// controlledAgent.performUserActions();
		// }
		// System.out.println("=======");
	}

	@Override
	public void simulationDomOnlyInitialization(
			SimulationDescription simulationDescription) {
	}

	@Override
	public void simulationEnvironmentEventOccured(
			EnvironmentEvent environmentEvent) {
	}

	@Override
	public void simulationInfosEvent(SimulationEvent simulationEvent) {
	}

	@Override
	public void simulationPaused(boolean pauseState) {
	}

	@Override
	public void simulationProjectDirectoryChanged(File projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	@Override
	public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
	}

	@Override
	public void objektInitEvent(ObjektInitEvent objInitEvent) {
	}

	public void setControlledAgent(long id) {
		this.controlledAgent = null;

		for (AgentController<? extends AgentSubject> controller : this.agentControllers
				.get(initIdentifier)) {
			if (controller.getSubject().getId() == id) {
				this.controlledAgent = controller;
				this.gui.setControlView(this.controlledAgent);
				this.controlledAgent.setAgentIsControlled(true);
				break;
			}
		}
	}

	public String getProjectPath() {
		if (projectDirectory != null) {
			return projectDirectory.getPath();
		}
		return null;
	}

	public void addAgentController(
			AgentController<? extends AgentSubject> agentController) {
		if (!this.agentControllers.containsKey(initIdentifier)) {
			this.agentControllers.put(initIdentifier,
					new HashSet<AgentController<? extends AgentSubject>>());
		}
		this.agentControllers.get(initIdentifier).add(agentController);
	}

	public Set<AgentController<? extends AgentSubject>> getAgentControllers() {
		return this.agentControllers.get(initIdentifier);
	}
}