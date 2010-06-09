package aors.module.agentControl.gui.views;

import aors.model.agtsim.proxy.agentcontrol.CoreAgentController;
import aors.module.agentControl.gui.GUIController;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.model.agtsim.proxy.agentcontrol.Pair;
import aors.model.dataTypes.AORSInteger;
import aors.model.dataTypes.AORSString;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.AORSPanel;
import aors.module.agentControl.gui.renderer.AORSReplacedElementFactory;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
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

	public SelectionView(GUIController guiController, Map<Long,
		CoreAgentController> agentControllers) {
		super(new AORSPanel());
		this.guiController = guiController;

		this.eventMediator = new EventMediator(this);
		this.eventMediator.addReceiver("controlledAgentId", this, null);

		this.getGUIComponent().getSharedContext().setReplacedElementFactory(
			new AORSReplacedElementFactory(this.eventMediator));

		createContent(agentControllers);
	}

	private void createContent(Map<Long, CoreAgentController>
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

		// heading
		body.addContent(new Element("h1").addContent("Agent Selection"));

		//explaination
		body.addContent(new Element("p").addContent("Please select the " +
			"agent you want to control from the table below."));

		//table containing controllable agents
		Element tableWrapper = new Element("div").setAttribute("style",
			"display: inline-block;");
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
		headRow.addContent(new Element("th").addContent("language").
			setAttribute((Attribute)headCellStyle.clone()));

		table.addContent(headRow);

		//datarows
		Attribute dataStyle = new Attribute("style",
			"padding: 2px; " +
			"border: 1px solid black; ");

		CoreAgentController coreAgentController;
		for(long agentId : controllableAgents.keySet()) {
			coreAgentController = controllableAgents.get(agentId);

			String defaultLanguage = coreAgentController.getDefaultUILanguage();

			Element dataRow = new Element("tr");
			Attribute style = dataStyle;

			Element button = new Element("td").addContent(
				new Element("button").setAttribute("name", "select").
				setAttribute("title", "take control"));

			dataRow.addContent(new Element("td").addContent(button).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(coreAgentController.getAgentName()).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				new Element("textfield").setAttribute("name", "id").setAttribute(
				"initialValue", String.valueOf(agentId)).setAttribute("readonly", "true").
				setAttribute("style", "display: inline-block;").setAttribute("type",
				"Integer")).setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				coreAgentController.getAgentType()).setAttribute((Attribute)style.clone()));

			Element langSelection = new Element("select");
			langSelection.setAttribute("name", "lang").setAttribute("type", "String");
			for(String lang : coreAgentController.getUILanguages()) {
				Element option = new Element("option").setAttribute("value",
					lang).addContent(lang);
				if(lang.equals(defaultLanguage)) {
					option.setAttribute("selected", "selected");
				}
				langSelection.addContent(option);
			}
			dataRow.addContent(new Element("td").addContent(langSelection).
				setAttribute((Attribute)style.clone()));

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
		if("select".equals(senderName)) {
			Set<Pair<String, String>> events = new HashSet<Pair<String, String>>();
			events.add(new Pair<String, String>("click", "select"));
			return events;
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null && Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName()) &&
			evt.getNewValue() instanceof Sender.ValueMap) {
			Sender.ValueMap values = (Sender.ValueMap)evt.getNewValue();
			this.guiController.setControlledAgentController(AORSInteger.valueOf(
				values.get("id")).getValue(), AORSString.valueOf(values.get("lang")).
				getValue());
		}
	}
}