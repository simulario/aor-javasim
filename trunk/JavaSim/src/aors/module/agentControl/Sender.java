package aors.module.agentControl;

import java.util.HashMap;

public interface Sender extends InteractiveComponent {

	public final static String SEND_PROPERTY_NAME = "__send";

  public String getValue();

	public static class ValueMap extends HashMap<String, String> {
		private final static long serialVersionUID = 1L;
	}
}