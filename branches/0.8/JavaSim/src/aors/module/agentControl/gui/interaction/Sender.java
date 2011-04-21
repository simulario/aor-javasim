package aors.module.agentControl.gui.interaction;

import java.util.HashMap;

/**
 * Interface that represents a gui component that acts as a send.
 * A sender is a gui components that can sends its value when an user
 * interaction is performed.
 * @author Thomas Grundmann
 */
public interface Sender extends InteractiveComponent {

	/**
	 * The gui components attribute that holds the components sender name.
	 */
	public final static String SENDER_ATTRIBUTE = "name";

	/**
	 * The internal property name that is used to identify a property change
	 * event caused by an user's submission.
	 */
	public final static String SEND_PROPERTY_NAME = "__send";

	/**
	 * Returns the sender's value as string.
	 * @return the value string
	 */
  public String getValue();

	/**
	 * A map of name value pairs.
	 */
	public static class ValueMap extends HashMap<String, String> {
		private final static long serialVersionUID = 1L;
	}
}