package aors.module.agentControl.gui.renderer;

/**
 * Interface for the renderer factories.
 * @author Thomas Grundmann
 */
public interface AbstractRendererFactory {

	/**
	 * Instantiates the factory with no parameters.
	 * @return the instance
	 */
	public AbstractRendererFactory instantiate();

	/**
	 * Creates a new instance of the renderer.
	 * @return the new instance or <code>null</code> if the renderer could not
	 *         have be instantiated
	 */
	public Renderer createRenderer();
}