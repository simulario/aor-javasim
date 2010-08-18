package aors.module.agentControl.gui.views;

import aors.module.agentControl.gui.GUIComponent;
import aors.util.Pair;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.InteractiveComponent;
import aors.module.agentControl.gui.interaction.Receiver;
import aors.module.agentControl.gui.interaction.Sender;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A view that is interactive.
 * An interactive view also serves as a receiver for other interactive
 * components.
 * @author Thomas Grundmann
 */
public abstract class InteractiveView implements View,
	InteractiveComponent, Receiver {

	/**
	 * The view's gui component.
	 */
	protected GUIComponent guiComponent;

	/**
	 * The view's event mediator.
	 */
	private EventMediator eventMediator;

	/**
	 * Instantiates the view.
	 */
	public InteractiveView() {
		this.guiComponent = null;
		this.eventMediator = new EventMediator(this);
	}

	/**
	 * Returns the view's gui component.
	 * @return the gui component
	 */
	@Override
	public GUIComponent getGUIComponent() {
		return this.guiComponent;
	}

	/**
	 * Returns the view's mediator.
	 * @return the event mediator
	 */
	@Override
	public EventMediator getEventMediator() {
		return this.eventMediator;
	}

	/**
	 * Checks if the component can get the focus.
	 * @return <code>true</code> if the component can get the focus
	 */
	@Override
	public boolean isFocusable() {
		return this.guiComponent != null && this.guiComponent.isFocusable();
	}

	/**
	 * Announces the component to be focusable or not.
	 * @param focusable
	 */
	@Override
	public void setFocusable(boolean focusable) {
		if(this.guiComponent != null) {
			this.guiComponent.setFocusable(focusable);
		}
	}

	/**
	 * The component request the focus.
	 * @return <code>true</code> if the components gets the focus
	 */
	@Override
	public boolean requestFocusInWindow() {
		return this.guiComponent != null && this.guiComponent.requestFocusInWindow();
	}

	/**
	 * Adds a key listener to the component.
	 * @param keyListener
	 */
	@Override
	public void addKeyListener(KeyListener keyListener) {
		if(this.guiComponent != null) {
			this.guiComponent.addKeyListener(keyListener);
		}
	}

	/**
	 * Adds a mouse listener to the component.
	 * @param mouseListener
	 */
	@Override
	public void addMouseListener(MouseListener mouseListener) {
		if(this.guiComponent != null) {
			this.guiComponent.addMouseListener(mouseListener);
		}
	}

	/**
	 * Map of codes for the keyboard keys.
	 */
	private final static Map<String, Integer> keyCodes = initKeyCodes();

	/**
	 * Creates an unmodifiable map of key codes.
	 * @return the map
	 */
	private static Map<String, Integer> initKeyCodes() {
		Map<String, Integer> newKeyCodes = new HashMap<String, Integer>();
		newKeyCodes.put("Down", KeyEvent.VK_DOWN);
		newKeyCodes.put("Left", KeyEvent.VK_LEFT);
		newKeyCodes.put("Right", KeyEvent.VK_RIGHT);
		newKeyCodes.put("Up", KeyEvent.VK_UP);
		newKeyCodes.put("Enter", KeyEvent.VK_ENTER);
		newKeyCodes.put("F1", KeyEvent.VK_F1);
		newKeyCodes.put("F2", KeyEvent.VK_F2);
		newKeyCodes.put("F3", KeyEvent.VK_F3);
		newKeyCodes.put("F4", KeyEvent.VK_F4);
		newKeyCodes.put("F5", KeyEvent.VK_F5);
		newKeyCodes.put("F6", KeyEvent.VK_F6);
		newKeyCodes.put("F7", KeyEvent.VK_F7);
		newKeyCodes.put("F8", KeyEvent.VK_F8);
		newKeyCodes.put("F9", KeyEvent.VK_F9);
		newKeyCodes.put("F10", KeyEvent.VK_F10);
		newKeyCodes.put("F11", KeyEvent.VK_F11);
		newKeyCodes.put("F12", KeyEvent.VK_F12);
		newKeyCodes.put("Insert", KeyEvent.VK_INSERT);
		newKeyCodes.put("Del", KeyEvent.VK_DELETE);
		newKeyCodes.put("Backspace", KeyEvent.VK_BACK_SPACE);
		newKeyCodes.put("PageDown", KeyEvent.VK_PAGE_DOWN);
		newKeyCodes.put("PageUp", KeyEvent.VK_PAGE_UP);
		newKeyCodes.put("Tab", KeyEvent.VK_TAB);
		newKeyCodes.put("Esc", KeyEvent.VK_ESCAPE);
		newKeyCodes.put("Spacebar", KeyEvent.VK_SPACE);
		newKeyCodes.put("#", KeyEvent.VK_NUMBER_SIGN);
		newKeyCodes.put("+", KeyEvent.VK_PLUS);
		newKeyCodes.put("-", KeyEvent.VK_MINUS);
		newKeyCodes.put("0", KeyEvent.VK_0);
		newKeyCodes.put("1", KeyEvent.VK_1);
		newKeyCodes.put("2", KeyEvent.VK_2);
		newKeyCodes.put("3", KeyEvent.VK_3);
		newKeyCodes.put("4", KeyEvent.VK_4);
		newKeyCodes.put("5", KeyEvent.VK_5);
		newKeyCodes.put("6", KeyEvent.VK_6);
		newKeyCodes.put("7", KeyEvent.VK_7);
		newKeyCodes.put("8", KeyEvent.VK_8);
		newKeyCodes.put("9", KeyEvent.VK_9);
		newKeyCodes.put("A", KeyEvent.VK_A);
		newKeyCodes.put("B", KeyEvent.VK_B);
		newKeyCodes.put("C", KeyEvent.VK_C);
		newKeyCodes.put("D", KeyEvent.VK_D);
		newKeyCodes.put("E", KeyEvent.VK_E);
		newKeyCodes.put("F", KeyEvent.VK_F);
		newKeyCodes.put("G", KeyEvent.VK_G);
		newKeyCodes.put("H", KeyEvent.VK_H);
		newKeyCodes.put("I", KeyEvent.VK_I);
		newKeyCodes.put("J", KeyEvent.VK_J);
		newKeyCodes.put("K", KeyEvent.VK_K);
		newKeyCodes.put("L", KeyEvent.VK_L);
		newKeyCodes.put("M", KeyEvent.VK_M);
		newKeyCodes.put("N", KeyEvent.VK_N);
		newKeyCodes.put("O", KeyEvent.VK_O);
		newKeyCodes.put("P", KeyEvent.VK_P);
		newKeyCodes.put("Q", KeyEvent.VK_Q);
		newKeyCodes.put("R", KeyEvent.VK_R);
		newKeyCodes.put("S", KeyEvent.VK_S);
		newKeyCodes.put("T", KeyEvent.VK_T);
		newKeyCodes.put("U", KeyEvent.VK_U);
		newKeyCodes.put("V", KeyEvent.VK_V);
		newKeyCodes.put("W", KeyEvent.VK_W);
		newKeyCodes.put("X", KeyEvent.VK_X);
		newKeyCodes.put("Y", KeyEvent.VK_Y);
		newKeyCodes.put("Z", KeyEvent.VK_Z);
		return Collections.unmodifiableMap(newKeyCodes);
	}

	/**
	 * Adds key listeners for a set of keyboard events.
	 * @param keyEvents
	 */
	public void addKeyListeners(Set<Pair<String, String>> keyEvents) {

		// to be able to process key events the interactive component has to be
		// focusable
		if(!this.isFocusable()) {
			this.setFocusable(true);
		}

		// to be able to process a key event the interactive component has to have
		// the focus
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				InteractiveView.this.requestFocusInWindow();
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});

		// adds the listeners
		for(Pair<String, String> keyEvent : keyEvents) {
			String[] pressedKeys = keyEvent.value1.split("_");
			final int keyCode = keyCodes.get(pressedKeys[0]);
			final boolean shiftPressed = Boolean.valueOf(pressedKeys[1]);
			final boolean conrolPressed = Boolean.valueOf(pressedKeys[2]);
			final String action = keyEvent.value2;

			this.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {}

				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == keyCode && (e.isShiftDown() == shiftPressed) &&
						(e.isControlDown() == conrolPressed)) {
						InteractiveView.this.eventMediator.propertyChange(
							new PropertyChangeEvent(InteractiveView.this,
							Sender.SEND_PROPERTY_NAME, null, action));
						return;
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {}
			});
		}
	}

	/**
	 * Returns the mouse events for a sender.
	 * @param senderName
	 * @return the sender's mouse events.
	 */
	protected abstract Set<Pair<String, String>> getMouseEvents(String senderName);

	/**
	 * Adds mouse listeners for a sender.
	 * @param senderName
	 * @param sender
	 */
	public void addMouseListeners(String senderName, final Sender sender) {
		Set<Pair<String, String>> mouseEvents = this.getMouseEvents(senderName);
		if(mouseEvents == null) {
			return;
		}

		for(Pair<String, String> mouseEvent : mouseEvents) {
			final String eventType = mouseEvent.value1;
			final String action = mouseEvent.value2;

			sender.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(("click".equals(eventType) && e.getClickCount() == 1) ||
						("dblclick".equals(eventType) && e.getClickCount() == 2)) {
						sender.getEventMediator().propertyChange(new PropertyChangeEvent(
							sender, Sender.SEND_PROPERTY_NAME, null, action));
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if("mousedown".equals(eventType)) {
						sender.getEventMediator().propertyChange(new PropertyChangeEvent(
							sender, Sender.SEND_PROPERTY_NAME, null, action));
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if("mouseup".equals(eventType)) {
						sender.getEventMediator().propertyChange(new PropertyChangeEvent(
							sender, Sender.SEND_PROPERTY_NAME, null, action));
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}
			});
		}
	}
}
