package aors.module.agentControl.gui.interaction;

import aors.module.agentControl.gui.views.InteractiveView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for the communication between gui components.
 * @author Thomas Grundmann
 */
public class EventMediator implements PropertyChangeListener {

	/**
	 * Map of receiver components per receiver element.
	 */
  private Map<String, Set<Receiver>> receivers;

	/**
	 * Map of sender components.
	 */
  private Map<String, Sender> senders;

	/**
	 * The interactive view that is the host of the gui.
	 */
	private InteractiveView interactiveView;

	/**
	 * Initializes the class.
	 * @param interactiveView
	 */
	public EventMediator(InteractiveView interactiveView) {
		this.receivers = new Hashtable<String, Set<Receiver>>();
    this.senders = new Hashtable<String, Sender>();
		this.interactiveView = interactiveView;
		if(this.interactiveView != null) {
			this.addReceiver(Sender.SEND_PROPERTY_NAME, this.interactiveView,	null);
		}
	}

	/**
	 * Notifes all receivers for a given property if this property changes.
	 * @param evt
	 */
	@Override
  public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		// receives a sender request
		if(Sender.SEND_PROPERTY_NAME.equals(propertyName)) {

			// get all values from the gui
			Sender.ValueMap valuesToSend = new Sender.ValueMap();
			valuesToSend.put(Sender.SEND_PROPERTY_NAME, evt.getNewValue().toString());
			for(String valueName : this.senders.keySet()) {
				valuesToSend.put(valueName, senders.get(valueName).getValue());
			}

			//update the event
			evt = new PropertyChangeEvent(this, Sender.SEND_PROPERTY_NAME, null,
				valuesToSend);
		}

		// propagate the event to all receivers for the specified property
    if(this.receivers != null) {
      if(this.receivers.get(propertyName) != null) {
        for(Receiver receiver : this.receivers.get(propertyName)) {
          if(!receiver.equals(evt.getSource())) {
            receiver.propertyChange(evt);
          }
        }
      }
    }
  }

	/**
	 * Adds a receiver for the given property with the give initial value.
	 * @param property
	 * @param receiver
	 * @param initialValue
	 */
  public void addReceiver(String property, Receiver receiver,
		String initialValue) {
    if(property != null && property.length()>0) {
			if(!this.receivers.containsKey(property)) {
       this.receivers.put(property, new HashSet<Receiver>());
      }
      this.receivers.get(property).add(receiver);
    }
  }

	/**
	 * Adds a sender for the given property.
	 * @param property
	 * @param sender
	 * @return <code>true</code> if the sender was added
	 */
  public boolean addSender(String property, Sender sender) {
    if(property != null && property.length()>0) {
      if(!senders.containsKey(property)) {
        senders.put(property, sender);
				if(this.interactiveView != null) {
					this.interactiveView.addMouseListeners(property, sender);
				}
        return true;
      }
    }
    return false;
  } 
}