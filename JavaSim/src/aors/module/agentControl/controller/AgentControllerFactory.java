package aors.module.agentControl.controller;

import aors.model.agtsim.proxy.agentControl.AgentControlInitializer;

/**
 * Interface for the controller factories.
 * @author Thomas Grundmann
 */
public interface AgentControllerFactory {

//	/**
//	 * Instantiates the factory with no parameters.
//	 * @return the instance
//	 */
//	public AgentControllerFactory instantiate();

	/**
	 * Creates a new agent controller.
	 * @param agentControlInitializer
	 * @return the new instance or <code>null</code> if the controller could not
	 *         have be instantiated
	 */
	public AgentController createController(AgentControlInitializer
		agentControlInitializer);
}