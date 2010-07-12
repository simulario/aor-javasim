package aors.module.agentControl.controller;

import aors.module.agentControl.gui.views.ControlView;
import java.util.Map;

/**
 * Interface for the agent controller's module part that is used by the gui (the
 * mvc view).
 * @author Thomas Grundmann
 */
public interface ModuleAgentController {

	/**
	 * Sets the controller's control view.
	 * @param controlView
	 */
	public void setControlView(ControlView controlView);

	/**
	 * Adds the given set of values to the list of user actions that shall be
	 * performed at the step's end.
	 * @param values
	 */
	public void addUserAction(Map<String, String> values);
}