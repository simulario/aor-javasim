package aors.module.agentControl.gui.renderer.flyingsaucer;

import aors.module.agentControl.gui.GUIComponent;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.Renderer;
import aors.module.agentControl.gui.renderer.AbstractRendererFactory;
import org.w3c.dom.Document;

/**
 * Creates a renderer using the flying saucer framework.
 * @author Thomas Grundmann
 */
public class FlyingSaucerRendererFactory implements AbstractRendererFactory {

	/**
	 * Instantiates the factory.
	 */
	public FlyingSaucerRendererFactory() {}

	/**
	 * Instantiates the factory with no parameters.
	 * @return the instance
	 */
	public FlyingSaucerRendererFactory instantiate() {
		return new FlyingSaucerRendererFactory();
	}

	/**
	 * Creates a new instance of the renderer.
	 * @return the new instance or <code>null</code> if the renderet could not
	 *         have be instantiated
	 */
	public Renderer createRenderer() {
		return this.new FlyingSaucerAdapter();
	}

	/**
	 * Adapter for the flying saucer renderer.
	 */
	private class FlyingSaucerAdapter implements Renderer {

		/**
		 * The gui component created by the renderer.
		 */
		AORSPanel GUIComponent;

		/**
		 * Instantiates the adapter.
		 */
		public FlyingSaucerAdapter() {
			this.GUIComponent = new AORSPanel();
		}

		/**
		 * Returns the gui component created by the renderer.
		 * @return the gui component
		 */
		public GUIComponent getGUIComponent() {
			return this.GUIComponent;
		}

		/**
		 * Renders the given document that is located at the given uri and uses the
		 * given event mediator for communcation.
		 * @param sourceDocument
		 * @param sourceURI
		 * @param eventMediator
		 */
		public void doRender(Document sourceDocument, String sourceURI,
			EventMediator eventMediator) {
			if(sourceDocument == null) {
				return;
			}
			if(eventMediator == null) {
				eventMediator = new EventMediator(null);
			}
			this.GUIComponent.getSharedContext().setReplacedElementFactory(
				new AORSReplacedElementFactory(eventMediator));
			this.GUIComponent.setDocument(sourceDocument, sourceURI);
		}
	}
}