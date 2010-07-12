package aors.module.agentControl.gui.views;

import aors.model.Event;
import aors.model.agtsim.proxy.agentControl.AgentControlInitializer;
import aors.module.agentControl.controller.ModuleAgentController;
import aors.module.agentControl.gui.GUIComponent;
import aors.util.Pair;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.Renderer;
import aors.module.agentControl.gui.renderer.RendererFactory;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;

/**
 * View to control an agent.
 * @author Thomas Grundmann
 */
public class ControlView extends InteractiveView {

  private static final long serialVersionUID = 1L;
	
	/**
	 * The agent controller to that the view belogs.
	 */
	private ModuleAgentController agentController;

	private Map<String, Set<Pair<String, String>>> mouseEvents;

	/**
	 * Initializes and creates the view for the given agent controller.
	 * @param agentControlInitializer
	 * @param agentController
	 * @param projectPath
	 * @param lang
	 */
  public ControlView(AgentControlInitializer agentControlInitializer,
		ModuleAgentController agentController, String projectPath, String lang) {
		super();

		// connects controller with view
		this.agentController = agentController;
		this.agentController.setControlView(this);

		this.mouseEvents = agentControlInitializer.getMouseEvents();

		// create the view's content
		this.guiComponent = this.createContent(
			agentControlInitializer.getAgentType(), projectPath, lang);

		// adds the key listeners
		this.addKeyListeners(agentControlInitializer.getKeyEvents());
	}

	/**
	 * Creates the view's content.
	 * @param agentType
	 * @param projectPath
	 * @param lang
	 */
	private GUIComponent createContent(String agentType, String projectPath,
		String lang) {

		// constructs path to the view data
		String sep = File.separator;
		if(agentType.endsWith("AgentSubject")) {
			agentType = agentType.substring(0, agentType.lastIndexOf("AgentSubject"));
		}
		String guiPath = projectPath + sep + "src" + sep + "interaction" +
			sep + "agentcontrol" + sep + agentType + "_" + lang + ".gui";

		// renders the view
		Renderer renderer = RendererFactory.getInstance().createRenderer();
		try {
			File document = new File(guiPath);
			renderer.doRender(new DOMOutputter().output(new SAXBuilder()
				.build(document)),document.toURI().toString(), this.getEventMediator());
		} catch(JDOMException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return renderer.getGUIComponent();
	}

	/**
	 * Returns the set of mouseevents for a sender.
	 * @param senderName
	 * @return the set of mouse events
	 */
	@Override
	protected Set<Pair<String, String>> getMouseEvents(String senderName) {
		return this.mouseEvents.get(senderName);
	}

	/**
	 * Notifies the view wether to update the gui with new data or to add a new
	 * iteraction event to the controller.
	 * @param evt
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// data from gui components to controller
		if(evt != null && Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName()) &&
			evt.getNewValue() instanceof Sender.ValueMap) {
			this.agentController.addUserAction((Sender.ValueMap)evt.getNewValue());
			return;
		}

		// data from controller to gui components
		if(evt != null && evt.getSource().equals(this.agentController)) {
			if(evt.getNewValue() instanceof Event) {
				System.out.println(evt.getNewValue());
				return;
			}
			this.getEventMediator().propertyChange(evt);
		}
	}
}