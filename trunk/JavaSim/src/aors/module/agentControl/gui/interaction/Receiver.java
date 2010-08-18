package aors.module.agentControl.gui.interaction;

import java.beans.PropertyChangeListener;

/**
 * Interface that represents a gui component that acts as a receiver.
 * A receiver is a gui components that can receive values from the simulator.
 * @author Thomas Grundmann
 */
public interface Receiver extends PropertyChangeListener {

	/**
	 * The gui component's attribute that holds the name of the property whose
	 * value is received by the component.
	 */
	public final static String RECEIVER_ATTRIBUTE = "property";
}