package aors.module.agentControl.gui.interaction;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public interface InteractiveComponent {
	public void addMouseListener(MouseListener mouseListener);
	public void addKeyListener(KeyListener keyListener);
	public abstract boolean isFocusable();
	public abstract void setFocusable(boolean focusable);
	public abstract boolean requestFocusInWindow();

	public EventMediator getEventMediator();
}