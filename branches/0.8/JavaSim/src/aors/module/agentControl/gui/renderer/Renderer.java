package aors.module.agentControl.gui.renderer;

import aors.module.agentControl.gui.GUIComponent;
import aors.module.agentControl.gui.interaction.EventMediator;
import org.w3c.dom.Document;

/**
 * This interface specifys the minimum requirements for a renderer.
 * @author Thomas Grundmann
 */
public interface Renderer {

	/**
	 * Returns the gui component created by the renderer.
	 * @return the gui component
	 */
	public GUIComponent getGUIComponent();

	/**
	 * Renders the given document that is located at the given uri and uses the
	 * given event mediator for communcation.
	 * @param sourceDocument
	 * @param sourceURI
	 * @param eventMediator
	 */
	public void doRender(Document sourceDocument, String sourceURI,
		EventMediator eventMediator);
}