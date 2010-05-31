package aors.module.agentControl.gui.views;

import aors.model.agtsim.AgentSubject;
import aors.module.agentControl.AgentController;
import aors.module.agentControl.gui.GUIManager;
import aors.module.agentControl.gui.EventMediator;
import aors.module.agentControl.gui.renderer.AORSPanel;
import aors.module.agentControl.gui.renderer.AORSReplacedElementFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import javax.swing.JComponent;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;

public class SelectionView implements View, PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private EventMediator eventMediator;
	private GUIManager gui;
	private AORSPanel guiComponent;

	public SelectionView(GUIManager gui,
		Set<AgentController<? extends AgentSubject>> agentControllers) {
		this.gui = gui;

		this.eventMediator = new EventMediator();
		this.eventMediator.addReceiver("controlledAgentId", this, null);

		guiComponent = new AORSPanel();
		guiComponent.getSharedContext().setReplacedElementFactory(
			new AORSReplacedElementFactory(this.eventMediator));

		createContent(agentControllers);
	}

	private void createContent(Set<AgentController<? extends AgentSubject>> controllableAgents) {

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

		AgentSubject subject = null;
		for(AgentController<? extends AgentSubject> agentController : controllableAgents) {
			subject = agentController.getSubject();

			long id = subject.getId();
			Element dataRow = new Element("tr");
			Attribute style = dataStyle;

			Element radioButton = new Element("td").addContent(
				new Element("radiobutton").setAttribute("name", "controlledAgentId").
				setAttribute("slot", "controlledAgentId").
				setAttribute("value", String.valueOf(id)).
				setAttribute("style", "display: block; margin: auto;"));

			dataRow.addContent(new Element("td").addContent(radioButton).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(subject.getName()).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				String.valueOf(id)).setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				subject.getType()).setAttribute((Attribute)style.clone()));

			table.addContent(dataRow);
		}

		try {
			guiComponent.setDocument(new DOMOutputter().output(new Document().setRootElement(body)));
		} catch(JDOMException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JComponent getGUIComponent() {
		return this.guiComponent;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if("controlledAgentId".equals(evt.getPropertyName())) {
		}
		try {
			gui.setControlledAgent(Long.valueOf(evt.getNewValue().toString()));
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
