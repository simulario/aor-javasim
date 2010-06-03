package aors.module.agentControl.gui.views;

import aors.model.agtsim.AgentSubject;
import aors.module.agentControl.AgentController;
import aors.module.agentControl.gui.GUIController;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.InteractiveComponent.Pair;
import aors.module.agentControl.gui.renderer.AORSPanel;
import aors.module.agentControl.gui.renderer.AORSReplacedElementFactory;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.Set;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;

public class SelectionView extends InteractiveView<AORSPanel> {

	private static final long serialVersionUID = 1L;

	private GUIController guiController;

	public SelectionView(GUIController guiController,
		Map<Long, AgentController<? extends AgentSubject>> agentControllers) {
		super(new AORSPanel());
		this.guiController = guiController;

		this.eventMediator = new EventMediator(this);
		this.eventMediator.addReceiver("controlledAgentId", this, null);

		this.getGUIComponent().getSharedContext().setReplacedElementFactory(
			new AORSReplacedElementFactory(this.eventMediator));

		createContent(agentControllers);
	}

	private void createContent(Map<Long, AgentController<? extends AgentSubject>>
		controllableAgents) {

		//body that contains the content
		Element body = new Element("body").setAttribute("style",
			"position: fixed; " +
			"top: 0; " +
			"left: 0; " +
			"height: 100%; " +
			"width: 100%; " +
			"margin: 0; " +
			"padding: 0; " +
			"text-align: center; " +
			"background-color:silver;");

		//heading
		Element heading = new Element("h1").addContent("Agent Selection");
		body.addContent(heading);

		//explaination
		Element explaination = new Element("p").addContent("Please select the " +
			"agent you want to control from the table below.");
		body.addContent(explaination);

		//table containing controllable agents
		Element tableWrapper = new Element("div").setAttribute("style", "display: inline-block;");
		Element table = new Element("table").setAttribute("style",
			"margin: auto;" +
			"border: 1px solid black; " +
			"border-spacing: 2px; " +
			"background-color: grey;");
		body.addContent(tableWrapper.addContent(table));

		//headrow
		Element headRow = new Element("tr").setAttribute("style",
			"background-color: black; " +
			"border: 1px solid black;");

		Attribute headCellStyle = new Attribute("style",
			"padding: 2px; " +
			"font-weight: bold; " +
			"color: white;");

		headRow.addContent(new Element("th").addContent("select").
			setAttribute((Attribute)headCellStyle.clone()));
		headRow.addContent(new Element("th").addContent("name").
			setAttribute((Attribute)headCellStyle.clone()));
		headRow.addContent(new Element("th").addContent("id").
			setAttribute((Attribute)headCellStyle.clone()));
		headRow.addContent(new Element("th").addContent("type").
			setAttribute((Attribute)headCellStyle.clone()));

		table.addContent(headRow);

		//datarows
		Attribute dataStyle = new Attribute("style",
			"padding: 2px; " +
			"border: 1px solid black; ");

		AgentController<? extends AgentSubject> agentController;
		for(long agentId : controllableAgents.keySet()) {
			agentController = controllableAgents.get(agentId);

			Element dataRow = new Element("tr");
			Attribute style = dataStyle;

			Element radioButton = new Element("td").addContent(
				new Element("radiobutton").setAttribute("name", "controlledAgentId").
				setAttribute("slot", "controlledAgentId").
				setAttribute("value", String.valueOf(agentId)).
				setAttribute("style", "display: block; margin: auto;"));

			dataRow.addContent(new Element("td").addContent(radioButton).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(agentController.getAgentName()).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				String.valueOf(agentId)).setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				agentController.getAgentType()).setAttribute((Attribute)style.clone()));

			table.addContent(dataRow);
		}

		try {
			this.getGUIComponent().setDocument(new DOMOutputter().output(
				new Document().setRootElement(body)));
		} catch(JDOMException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Set<Pair<String, String>> getMouseEvents(String senderName) {
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if("controlledAgentId".equals(evt.getPropertyName()) &&
			evt.getNewValue() != null) {
			try {
				this.guiController.setControlledAgentController(Long.valueOf(evt.
					getNewValue().toString()));
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
}
