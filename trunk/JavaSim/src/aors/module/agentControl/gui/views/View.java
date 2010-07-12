package aors.module.agentControl.gui.views;

import aors.module.agentControl.gui.GUIComponent;

/**
 * This interface represents a view of the agent control gui.
 * @author Thomas Grundmann
 */
public interface View {

	/**
	 * Returns the view's gui component.
	 * @return the gui component
	 */
	public GUIComponent getGUIComponent();
}