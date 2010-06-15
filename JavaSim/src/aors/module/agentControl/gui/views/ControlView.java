package aors.module.agentControl.gui.views;

import aors.module.agentControl.AgentController;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.util.Pair;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.AORSPanel;
import aors.module.agentControl.gui.renderer.AORSReplacedElementFactory;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

public class ControlView extends InteractiveView<AORSPanel> {

  private static final long serialVersionUID = 1L;
	
	private AgentController agentController;

  public ControlView(AgentController agentController,
		String projectPath, String lang) throws FileNotFoundException {
		super(new AORSPanel());

		this.eventMediator = new EventMediator(this);
		this.agentController = agentController;
		this.agentController.setControlView(this);

		this.addKeyListeners(this.agentController.getKeyEvents());

		String sep = File.separator;
		String type = this.agentController.getAgentType();
		if(type.endsWith("AgentSubject")) {
			type = type.substring(0, type.lastIndexOf("AgentSubject"));
		}
		String guiPath = projectPath + sep + "src" + sep + "interaction" +
			sep + "agentcontrol" + sep + type + "_" + lang + ".gui";

		this.getGUIComponent().getSharedContext().setReplacedElementFactory(
			new AORSReplacedElementFactory(this.eventMediator));
		this.getGUIComponent().setDocument(new File(guiPath).toURI().toString());
	}

	@Override
	protected Set<Pair<String, String>> getMouseEvents(String senderName) {
		return this.agentController.getMouseEvents().get(senderName);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// data from gui components to controller
		if(evt != null && Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName()) &&
			evt.getNewValue() instanceof Sender.ValueMap) {
			this.agentController.addUserInteractionEvent((Sender.ValueMap)evt.getNewValue());
			return;
		}

		// data from controller to gui components
		if(evt != null && evt.getSource().equals(this.agentController)) {
			this.eventMediator.propertyChange(evt);
		}
	}
}