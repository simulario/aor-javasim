package aors.module.agentControl;

import aors.model.agtsim.AgentSubject;
import aors.model.agtsim.ReactionRule;
import aors.model.envevt.PerceptionEvent;
import aors.model.intevt.InternalEvent;
import aors.module.agentControl.gui.EventMediator;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AgentController<T extends AgentSubject>
	extends aors.model.agtsim.AgentSubject.AgentController
	implements PropertyChangeListener {

	private static ModuleController moduleController;
	protected T agentSubject;
	private List<InternalEvent> userInteractionEvents;
	
	private Set<Pair<String, String>> keyEvents;
	private Map<String, Set<Pair<String, String>>> mouseEvents;
	private EventMediator mediator;
	private Map<String, Integer> keyCodes;
	protected Set<String> suspendedRules;

	public AgentController(T agentSubject) {
		super(agentSubject);
		this.agentSubject = agentSubject;

		moduleController = ModuleController.getInstance();
		moduleController.addAgentController(this);

		this.userInteractionEvents = new ArrayList<InternalEvent>();
		this.keyEvents = new HashSet<Pair<String, String>>();
		this.mouseEvents = new HashMap<String, Set<Pair<String, String>>>();
		this.mediator = new EventMediator(this);

		this.suspendedRules = new HashSet<String>();

		initKeyCodes();
	}

	private void initKeyCodes() {
		this.keyCodes = new HashMap<String, Integer>();
		this.keyCodes.put("Down", KeyEvent.VK_DOWN);
		this.keyCodes.put("Left", KeyEvent.VK_LEFT);
		this.keyCodes.put("Right", KeyEvent.VK_RIGHT);
		this.keyCodes.put("Up", KeyEvent.VK_UP);
		this.keyCodes.put("Enter", KeyEvent.VK_ENTER);
		this.keyCodes.put("F1", KeyEvent.VK_F1);
		this.keyCodes.put("F2", KeyEvent.VK_F2);
		this.keyCodes.put("F3", KeyEvent.VK_F3);
		this.keyCodes.put("F4", KeyEvent.VK_F4);
		this.keyCodes.put("F5", KeyEvent.VK_F5);
		this.keyCodes.put("F6", KeyEvent.VK_F6);
		this.keyCodes.put("F7", KeyEvent.VK_F7);
		this.keyCodes.put("F8", KeyEvent.VK_F8);
		this.keyCodes.put("F9", KeyEvent.VK_F9);
		this.keyCodes.put("F10", KeyEvent.VK_F10);
		this.keyCodes.put("F11", KeyEvent.VK_F11);
		this.keyCodes.put("F12", KeyEvent.VK_F12);
		this.keyCodes.put("Insert", KeyEvent.VK_INSERT);
		this.keyCodes.put("Del", KeyEvent.VK_DELETE);
		this.keyCodes.put("Backspace", KeyEvent.VK_BACK_SPACE);
		this.keyCodes.put("PageDown", KeyEvent.VK_PAGE_DOWN);
		this.keyCodes.put("PageUp", KeyEvent.VK_PAGE_UP);
		this.keyCodes.put("Tab", KeyEvent.VK_TAB);
		this.keyCodes.put("Esc", KeyEvent.VK_ESCAPE);
		this.keyCodes.put("Spacebar", KeyEvent.VK_SPACE);
		this.keyCodes.put("#", KeyEvent.VK_NUMBER_SIGN);
		this.keyCodes.put("+", KeyEvent.VK_PLUS);
		this.keyCodes.put("-", KeyEvent.VK_MINUS);
		this.keyCodes.put("0", KeyEvent.VK_0);
		this.keyCodes.put("1", KeyEvent.VK_1);
		this.keyCodes.put("2", KeyEvent.VK_2);
		this.keyCodes.put("3", KeyEvent.VK_3);
		this.keyCodes.put("4", KeyEvent.VK_4);
		this.keyCodes.put("5", KeyEvent.VK_5);
		this.keyCodes.put("6", KeyEvent.VK_6);
		this.keyCodes.put("7", KeyEvent.VK_7);
		this.keyCodes.put("8", KeyEvent.VK_8);
		this.keyCodes.put("9", KeyEvent.VK_9);
		this.keyCodes.put("A", KeyEvent.VK_A);
		this.keyCodes.put("B", KeyEvent.VK_B);
		this.keyCodes.put("C", KeyEvent.VK_C);
		this.keyCodes.put("D", KeyEvent.VK_D);
		this.keyCodes.put("E", KeyEvent.VK_E);
		this.keyCodes.put("F", KeyEvent.VK_F);
		this.keyCodes.put("G", KeyEvent.VK_G);
		this.keyCodes.put("H", KeyEvent.VK_H);
		this.keyCodes.put("I", KeyEvent.VK_I);
		this.keyCodes.put("J", KeyEvent.VK_J);
		this.keyCodes.put("K", KeyEvent.VK_K);
		this.keyCodes.put("L", KeyEvent.VK_L);
		this.keyCodes.put("M", KeyEvent.VK_M);
		this.keyCodes.put("N", KeyEvent.VK_N);
		this.keyCodes.put("O", KeyEvent.VK_O);
		this.keyCodes.put("P", KeyEvent.VK_P);
		this.keyCodes.put("Q", KeyEvent.VK_Q);
		this.keyCodes.put("R", KeyEvent.VK_R);
		this.keyCodes.put("S", KeyEvent.VK_S);
		this.keyCodes.put("T", KeyEvent.VK_T);
		this.keyCodes.put("U", KeyEvent.VK_U);
		this.keyCodes.put("V", KeyEvent.VK_V);
		this.keyCodes.put("W", KeyEvent.VK_W);
		this.keyCodes.put("X", KeyEvent.VK_X);
		this.keyCodes.put("Y", KeyEvent.VK_Y);
		this.keyCodes.put("Z", KeyEvent.VK_Z);
	}

	public T getSubject() {
		return this.agentSubject;
	}

	public EventMediator getMediator() {
		return this.mediator;
	}

	public void updateView() {
		Map<String, Object> properties = this.agentSubject.getBeliefProperties();
		if(properties != null) {
			for(String property : properties.keySet()) {
				mediator.propertyChange(new PropertyChangeEvent(this, property, null,
					properties.get(property)));
			}
		}
	}

	@Override
	public void performUserActions() {
		List<InternalEvent> oldInteractionEvents = this.userInteractionEvents;
		this.userInteractionEvents = new ArrayList<InternalEvent>();
		for(InternalEvent internalEvent : oldInteractionEvents) {
			this.processInternalEvent(internalEvent);
		}
	}

	@Override
	public void setNewEvents(List<PerceptionEvent> perceptionEvents) {
//		for(PerceptionEvent event : perceptionEvents) {
//			System.out.println(event);
//		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null && Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName()) &&
			evt.getNewValue() instanceof Sender.ValueMap) {
			Sender.ValueMap eventData = (Sender.ValueMap)evt.getNewValue();
			String eventName = eventData.get(Sender.SEND_PROPERTY_NAME);
			this.userInteractionEvents.add(this.createEvent(
				this.getCurrentSimulationStep()+1, eventName, eventData));
		}
	}

	public abstract InternalEvent createEvent(long occurrenceTime,
		String eventName, Map<String, String> eventData);

	protected void addKeyEvent(String keyName, String action) {
		this.keyEvents.add(new Pair<String, String>(keyName, action));
	}

	public void setKeyListeners(final InteractiveComponent sender) {
		if(!sender.isFocusable()) {
			sender.setFocusable(true);
		}

		sender.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				sender.requestFocusInWindow();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		for(Pair<String, String> keyEvent : this.keyEvents) {
			final int keyCode = keyCodes.get(keyEvent.value1);
			final String action = keyEvent.value2;

			sender.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == keyCode) {
						mediator.propertyChange(new PropertyChangeEvent(sender,
							Sender.SEND_PROPERTY_NAME, null, action));
						return;
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}
			});
		}
	}

	protected void addMouseEvent(String sender, String eventType, String action) {
		if(!this.mouseEvents.containsKey(sender)) {
			this.mouseEvents.put(sender, new HashSet<Pair<String, String>>());
		}
		this.mouseEvents.get(sender).add(new Pair<String, String>(eventType, action));
	}

	public void setMouseListeners(String senderName,
		final InteractiveComponent sender) {
		if(this.mouseEvents.containsKey(senderName)) {
			for(Pair<String, String> mouseEvent : this.mouseEvents.get(senderName)) {
				final String eventType = mouseEvent.value1;
				final String action = mouseEvent.value2;

				sender.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						if(("click".equals(eventType) && e.getClickCount() == 1) ||
							("dblclick".equals(eventType) && e.getClickCount() == 2)) {
							mediator.propertyChange(new PropertyChangeEvent(sender,
								Sender.SEND_PROPERTY_NAME, null, action));
						}
					}

					@Override
					public void mousePressed(MouseEvent e) {
						if("mousedown".equals(eventType)) {
							mediator.propertyChange(new PropertyChangeEvent(sender,
								Sender.SEND_PROPERTY_NAME, null, action));
						}
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						if("mouseup".equals(eventType)) {
							mediator.propertyChange(new PropertyChangeEvent(sender,
								Sender.SEND_PROPERTY_NAME, null, action));
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

	@Override
	public boolean ruleIsSuspended(ReactionRule reactionRule) {
		return this.agentIsControlled &&
			this.suspendedRules.contains(reactionRule.getName());
	}
	
	private class Pair<T, N> {

		public T value1;
		public N value2;

		public Pair(T value1, N value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public String toString() {
			return "(" + this.value1.toString() + ", " + this.value2.toString() + ")";
		}
	}
}
