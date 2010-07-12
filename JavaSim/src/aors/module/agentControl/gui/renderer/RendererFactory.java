package aors.module.agentControl.gui.renderer;

import aors.module.agentControl.gui.renderer.flyingsaucer.FlyingSaucerRendererFactory;

/**
 * Factory to create a new renderer instance.
 * @author Thomas Grundmann
 */
public abstract class RendererFactory implements AbstractRendererFactory {

	/**
	 * The instance of the facory.
	 */
	private static AbstractRendererFactory instance;

	/**
	 * The concrete factory.
	 */
	private AbstractRendererFactory concreteFactory;

	/**
	 * Instantiates the factory.
	 */
	private RendererFactory(AbstractRendererFactory concreteFactory) {
		this.concreteFactory = concreteFactory;
	}

	/**
	 * Instantiates the factory with no parameters.
	 * @return the instance
	 */
	public AbstractRendererFactory instantiate() {
		return getInstance();
	}

	/**
	 * Sets the factory instance. Once set the instance can never be changed.
	 * @param rendererFactory
	 */
	public static void setInstance(AbstractRendererFactory rendererFactory) {
		if(instance == null && rendererFactory != null) {
			instance = rendererFactory;
		}
	}
	/**
	 * Returns the factory instance. If no instance exists, a default
	 * implementation is used.
	 * @return the instance
	 */
	public static AbstractRendererFactory getInstance() {
		if(instance == null) {
			setInstance(new FlyingSaucerRendererFactory());
		}
		return instance;
	}

	/**
	 * Creates a new instance of the renderer.
	 * @return the new instance or <code>null</code> if the renderer could not
	 *         have be instantiated
	 */
	@Override
	public Renderer createRenderer() {
		return this.concreteFactory.createRenderer();
	}
}