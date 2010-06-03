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

	public static class Pair<M, N> {

		public final M value1;
		public final N value2;

		public Pair(M value1, N value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public String toString() {
			return "(" + this.value1 + ", " + this.value2 + ")";
		}
	}
}