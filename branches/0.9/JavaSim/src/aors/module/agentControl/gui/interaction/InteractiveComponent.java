package aors.module.agentControl.gui.interaction;

import aors.module.agentControl.gui.GUIComponent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

/**
 * This interface represents an interactive component.
 * An interactive component can react on user actions and communicates via a
 * mediator with other gui components.
 * @author Thomas Grundmann
 */
public interface InteractiveComponent {

	/**
	 * Returns the interactive component's gui component.
	 * @return the gui component
	 */
	public GUIComponent getGUIComponent();

	/**
	 * Adds a mouse listener to the component.
	 * @param mouseListener
	 */
	public void addMouseListener(MouseListener mouseListener);

	/**
	 * Adds a key listener to the component.
	 * @param keyListener
	 */
	public void addKeyListener(KeyListener keyListener);

	/**
	 * Checks if the component can get the focus.
	 * @return <code>true</code> if the component can get the focus
	 */
	public abstract boolean isFocusable();

	/**
	 * Announces the component to be focusable or not.
	 * @param focusable
	 */
	public abstract void setFocusable(boolean focusable);

	/**
	 * The component request the focus.
	 * @return <code>true</code> if the components gets the focus
	 */
	public abstract boolean requestFocusInWindow();

	/**
	 * Returns the mediator that the component uses for communication.
	 * @return
	 */
	public EventMediator getEventMediator();
}