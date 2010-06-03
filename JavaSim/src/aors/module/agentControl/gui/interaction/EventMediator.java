package aors.module.agentControl.gui.interaction;

import aors.module.agentControl.gui.views.InteractiveView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;

public class EventMediator implements PropertyChangeListener {

  private Map<String, Set<PropertyChangeListener>> receivers;
  private Map<String, Sender> senders;
	private InteractiveView<? extends JComponent> interactiveView;

	public EventMediator() {
		this.receivers = new Hashtable<String, Set<PropertyChangeListener>>();
    this.senders = new Hashtable<String, Sender>();
	}

	public EventMediator(InteractiveView<? extends JComponent> interactiveView) {
    this();
		this.interactiveView = interactiveView;
		this.addReceiver(Sender.SEND_PROPERTY_NAME, this.interactiveView, null);
	}

	@Override
  public void propertyChange(PropertyChangeEvent evt) {
		if(Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName())) {

			// get all values from the gui
			Sender.ValueMap valuesToSend = new Sender.ValueMap();
			valuesToSend.put(Sender.SEND_PROPERTY_NAME, evt.getNewValue().toString());
			for(String valueName : senders.keySet()) {
				valuesToSend.put(valueName, senders.get(valueName).getValue());
			}

			//update the event
			evt = new PropertyChangeEvent(this, Sender.SEND_PROPERTY_NAME, null,
				valuesToSend);
		}

    if(receivers != null) {
      if(receivers.get(evt.getPropertyName()) != null) {
        for(PropertyChangeListener pcl : receivers.get(evt.getPropertyName())) {
          if(!pcl.equals(evt.getSource())) {
            pcl.propertyChange(evt);
          }
        }
      }
    }
  }

  public void addReceiver(String property, PropertyChangeListener receiver,
		String initialValue) {
    if(property != null && property.length()>0) {

			//receiver aufnehmen
			if(!receivers.containsKey(property)) {
        receivers.put(property, new HashSet<PropertyChangeListener>());
      }
      receivers.get(property).add(receiver);
    }
  }

  public boolean addSender(String property, Sender sender) {
    if(property != null && property.length()>0) {
      if(!senders.containsKey(property)) {
        senders.put(property, sender);
				if(interactiveView != null) {
					interactiveView.addMouseListener(property, sender);
				}
        return true;
      }
    }
    return false;
  } 
}