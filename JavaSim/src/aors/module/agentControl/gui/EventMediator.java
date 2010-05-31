package aors.module.agentControl.gui;

import aors.model.agtsim.AgentSubject;
import aors.module.agentControl.AgentController;
import aors.module.agentControl.Sender;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class EventMediator implements PropertyChangeListener {

  private Map<String, Set<PropertyChangeListener>> receivers;
  private Map<String, Sender> senders;
	private AgentController<? extends AgentSubject> controller;
//	private Map<String, Object> values;

	public EventMediator() {
    this.receivers = new Hashtable<String, Set<PropertyChangeListener>>();
    this.senders = new Hashtable<String, Sender>();
//		this.values = new Hashtable<String, Object>();
	}

	public EventMediator(AgentController<? extends AgentSubject> controller) {
		this();
		this.controller = controller;
		this.addReceiver(Sender.SEND_PROPERTY_NAME, controller, null);
	}

  public void addReceiver(String property, PropertyChangeListener receiver,
		String initialValue) {
    if(property != null && property.length()>0) {

			//receiver aufnehmen
			if(!receivers.containsKey(property)) {
        receivers.put(property, new HashSet<PropertyChangeListener>());
      }
      receivers.get(property).add(receiver);

			//andere receiver informieren
//			if(initialValue != null) {
//				for(PropertyChangeListener pcl : receivers.get(property)) {
//					pcl.propertyChange(new PropertyChangeEvent(this, property, null,
//						initialValue));
//				}
//			}

//			if(values.containsKey(property)) {
//				receiver.propertyChange(new PropertyChangeEvent(this, property, null, values.get(property)));
//			}
    }
  }

  public boolean addSender(String property, Sender sender) {
    if(property != null && property.length()>0) {
      if(!senders.containsKey(property)) {
        senders.put(property, sender);
				if(controller != null) {
					controller.setMouseListeners(property, sender);
				}
        return true;
      }
    }
    return false;
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

//      listeners = receivers.get("__revalidate");
//      if(listeners != null) {
//        for(PropertyChangeListener receiver : listeners) {
//          if(!receiver.equals(evt.getSource())) {
//            receiver.propertyChange(new PropertyChangeEvent(this, "__revalidate", null, true));
//          }
//        }
//      }
    }
  }

//  @Override
//  public String toString() {
//		String string = "EVENTMEDIATOR(" + super.toString() + ") : \n";
//		Set<String> mixed = new HashSet<String>();
//		mixed.addAll(receivers.keySet());
//		mixed.addAll(values.keySet());
//		for(String key : mixed) {
//			string += "\t("+key+", "+values.get(key)+", "+receivers.get(key)+")\n";
//		}
//		return string;
//  }
}