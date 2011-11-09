package aors.module.agentControl.gui;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

/**
 * Represents a gui component.
 * @autor Thomas Grundmann
 */
public interface GUIComponent {

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
}