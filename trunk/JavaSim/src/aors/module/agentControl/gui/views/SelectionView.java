package aors.module.agentControl.gui.views;

import aors.model.agtsim.proxy.agentControl.AgentControlInitializer;
import aors.module.agentControl.gui.GUIController;
import aors.util.Pair;
import aors.model.dataTypes.AORSInteger;
import aors.model.dataTypes.AORSString;
import aors.module.agentControl.gui.GUIComponent;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.Renderer;
import aors.module.agentControl.gui.renderer.RendererFactory;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;

/**
 * View to select an agent that shall be controlled.
 * @author Thomas Grundmann
 */
public class SelectionView extends InteractiveView {

	private static final long serialVersionUID = 1L;

	/**
	 * The gui controller to that the view belogs.
	 */
	private GUIController guiController;

	/**
	 * Initializes and creates the view for the given agent controllers.
	 * @param guiController
	 * @param agentControlInitializers
	 */
	public SelectionView(GUIController guiController, Map<Long,
		AgentControlInitializer> agentControlInitializers) {
		super();
		this.guiController = guiController;

		// register the view as receiver for the agent selection
		this.getEventMediator().addReceiver("controlledAgentId", this, null);

		// create the view's content
		this.guiComponent = createContent(agentControlInitializers);
	}

	/**
	 * Creates the view's content.
	 * @param agentControlInitializers
	 */
	private GUIComponent createContent(Map<Long, AgentControlInitializer>
		agentControlInitializers) {

		// body that contains the content
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

		// explaination
		body.addContent(new Element("p").addContent("Please select the " +
			"agent you want to control from the table below."));

		// table containing controllable agents
		Element tableWrapper = new Element("div").setAttribute("style",
			"display: inline-block;");
		Element table = new Element("table").setAttribute("style",
			"margin: auto;" +
			"border: 1px solid black; " +
			"border-spacing: 2px; " +
			"background-color: grey;");
		body.addContent(tableWrapper.addContent(table));

		// headrow
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

		// datarows
		Attribute dataStyle = new Attribute("style",
			"padding: 2px; " +
			"border: 1px solid black; ");

		// creates a row for each controllable agent
		AgentControlInitializer agentControlInitializer;
		for(long agentId : agentControlInitializers.keySet()) {
			agentControlInitializer = agentControlInitializers.get(agentId);

			String defaultLanguage = agentControlInitializer.getDefaultUILanguage();

			Element dataRow = new Element("tr");
			Attribute style = dataStyle;

			Element button = new Element("td").addContent(
				new Element("button").setAttribute("name", "select" + agentId).
				setAttribute("title", "take control"));

			dataRow.addContent(new Element("td").addContent(button).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(agentControlInitializer.getAgentName()).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				new Element("textfield").setAttribute("name", "id" + agentId).
				setAttribute("initialValue", String.valueOf(agentId)).
				setAttribute("readonly", "true").setAttribute("style",
				"display: inline-block;").setAttribute("type", "Integer")).
				setAttribute((Attribute)style.clone()));
			dataRow.addContent(new Element("td").addContent(
				agentControlInitializer.getAgentType()).setAttribute((Attribute)style.clone()));

			Element langSelection = new Element("select");
			langSelection.setAttribute("name", "lang" + agentId).
				setAttribute("type", "String");
			for(String lang : agentControlInitializer.getUILanguages()) {
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

		// renders the document
		Renderer renderer = RendererFactory.getInstance().createRenderer();
		try {
			renderer.doRender(new DOMOutputter().output(new Document().
				setRootElement(body)), null, this.getEventMediator());
		} catch(JDOMException e) {
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
		if(senderName.startsWith("select")) {
			Set<Pair<String, String>> events = new HashSet<Pair<String, String>>();
			events.add(new Pair<String, String>("click", senderName));
			return events;
		}
		return null;
	}

	/**
	 * Notifies the view about the selection of the agent that shall be
	 * controlled.
	 * @param evt
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt != null && Sender.SEND_PROPERTY_NAME.equals(evt.getPropertyName()) &&
			evt.getNewValue() instanceof Sender.ValueMap) {
			Sender.ValueMap values = (Sender.ValueMap)evt.getNewValue();
			String senderSuffix = values.get(Sender.SEND_PROPERTY_NAME).substring(6);
			this.guiController.setControlledAgentController(
				AORSInteger.valueOf(values.get("id" + senderSuffix)),
				AORSString.valueOf(values.get("lang" + senderSuffix)));
		}
	}
}