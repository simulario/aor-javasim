package aors.module.agentControl.gui.views;

import aors.model.Entity;
import aors.model.Event;
import aors.model.agtsim.agentControl.AgentControlInitializer;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.Physical;
import aors.module.agentControl.controller.ModuleAgentController;
import aors.module.agentControl.gui.GUIComponent;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.Receiver;
import aors.util.Pair;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.Renderer;
import aors.module.agentControl.gui.renderer.RendererFactory;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
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

	/**
	 * The mouse events for this view.
	 */
	private Map<String, Set<Pair<String, String>>> mouseEvents;

	/**
	 * URI representing the path to the .gui-file
	 */
	private String sourceURI;

	/**
	 * The renderer used to render this view.
	 */
	private Renderer renderer;

	/**
	 * Set of all perception list names
	 */
	private Set<String> perceptionlistNames;

	/**
	 * Map of all perceptions for a perception list (identified by its name)
	 */
	private Map<String, Set<Element>> perceptions;

	/**
	 * Map of already perceived entities and their mediators and gui components
	 */
	private Map<Long, Pair<EventMediator, Map<String, GUIComponent>>>
		perceivedEntities;

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
	
		this.perceptions = new HashMap<String, Set<Element>>();
		this.perceptionlistNames = new HashSet<String>();
		this.perceivedEntities = new HashMap<Long, Pair<EventMediator,
			Map<String, GUIComponent>>>();

		// connects the controller with the view
		this.agentController = agentController;
		this.agentController.setControlView(this);

		// get the mouse events
		this.mouseEvents = agentControlInitializer.getMouseEvents();

		// create the view's content
		this.renderer = RendererFactory.getInstance().createRenderer();
		this.sourceURI = calculateSourceURI(agentControlInitializer, projectPath,
			lang);
		this.guiComponent = this.createGUI();

		// adds the key listeners
		this.addKeyListeners(agentControlInitializer.getKeyEvents());
	}

		/**
	 * Calculates the URI that represents the .gui-file.
	 * @param agentControlInitializer
	 * @param projectPath
	 * @param lang
	 * @return the URI
	 */
	private String calculateSourceURI(AgentControlInitializer
		agentControlInitializer, String projectPath, String lang) {
		String guiPath = agentControlInitializer.getAgentSubjectFacade().
			getAgentType();
		if(guiPath.endsWith("AgentSubject")) {
			guiPath = guiPath.substring(0, guiPath.lastIndexOf("AgentSubject"));
		}
		guiPath = projectPath + File.separator + "src" + File.separator +
			"interaction" + File.separator + "agentcontrol" + File.separator +
			guiPath + "_" + lang + ".gui";
		return new File(guiPath).toURI().toString();
	}

	/**
	 * Creates the gui for this view.
	 * @return the {@link GUIComponent} representing the view
	 */
	private GUIComponent createGUI() {

		// dom document representing the .gui-file
		Document domDocument = null;
		try {
			domDocument = new SAXBuilder().build(new URL(this.sourceURI));
		} catch(IOException e) {
			e.printStackTrace();
			return this.renderer.getGUIComponent();
		} catch(JDOMException exception) {
			exception.printStackTrace();
			return this.renderer.getGUIComponent();
		}

		// intermediate map for later replacements
		Map<Element, Element> perceptionlistsToBeReplaced = new HashMap<Element,
			Element>();

		// gets the perception lists and puts them with their perceptions into the
		// perceptions perceptionlistsToBeReplaced
		@SuppressWarnings("unchecked")
		Iterator<Element> perceptionlists = domDocument.getDescendants(
			new ElementFilter("perceptionlist"));
		int perceptionlistCount = 0;
		while(perceptionlists.hasNext()) {
			Element perceptionlist = perceptionlists.next();

			// calculets the perception list's name and adds it to the set of
			// perception list names
			perceptionlistCount++;
			String perceptionlistName = "__perceptionlist" + perceptionlistCount;
			this.perceptionlistNames.add(perceptionlistName);

			// creates a div element that replaces the perceptionlist element
			Element div = new Element("div");

			// copies all attributes, adds a class identifying the div as a
			// perceptionlist and sets the RECEIVER_ATTRIBUTE
			@SuppressWarnings("unchecked")
			List<Attribute> attributes = perceptionlist.getAttributes();
			for(Attribute attribute : attributes) {
				div.setAttribute((Attribute)attribute.clone());
			}
			Attribute classAttribute = div.getAttribute("class");
			if(classAttribute == null) {
				classAttribute = new Attribute("class", "");
				div.setAttribute(classAttribute);
			}
			classAttribute.setValue(classAttribute.getValue().concat(
				" __perceptionlist").trim());
			div.setAttribute(Receiver.RECEIVER_ATTRIBUTE, perceptionlistName);

			// put the perceptionlist and its replacement in the intermediate map
			perceptionlistsToBeReplaced.put(perceptionlist, div);

			// gets the perceptionlist's perceptions and put them into the perceptions
			// map
			@SuppressWarnings("unchecked")
			List<Element> perceptionEntries = perceptionlist.removeContent(
				new ElementFilter("perception"));
			for(Element perception : perceptionEntries) {

				// renames the perception element and adds a class identifying it as
				// a perception
				perception.setName("div");
				classAttribute = perception.getAttribute("class");
				if(classAttribute == null) {
					classAttribute = new Attribute("class", "");
				}
				classAttribute.setValue(classAttribute.getValue().concat(
					" __perception").trim());

				// removes the entityType attribute, sets the RECEIVER_ATTRIBUTE
				// pointing to its perceptionlist and adds the element to the
				// perceptions map
				String perceivedEntityName = perception.getAttributeValue("entityType");
				perception.removeAttribute("entityType");
				perception.setAttribute(Receiver.RECEIVER_ATTRIBUTE,
					perceptionlistName);
				if(!this.perceptions.containsKey(perceivedEntityName)) {
					this.perceptions.put(perceivedEntityName, new HashSet<Element>());
				}
				this.perceptions.get(perceivedEntityName).add(perception);
			}

		}

		// replaces perceptionlist element with its div version
		for(Element perceptionlistToBeModified : perceptionlistsToBeReplaced.
			keySet()) {

			// replaces the perceptionlist with its div
			Element parent = perceptionlistToBeModified.getParentElement();
			int index = parent.indexOf(perceptionlistToBeModified);
			parent.removeContent(index);
			parent.addContent(index, perceptionlistsToBeReplaced.get(
				perceptionlistToBeModified));
		}

		// renders the view
		try {
			this.renderer.doRender(new DOMOutputter().output(domDocument),
				this.sourceURI, this.getEventMediator());
		} catch(JDOMException e) {
			e.printStackTrace();
		}

		// return the rendered view
		return this.renderer.getGUIComponent();
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

			// END_OF_PERCEPTIONS
			if(ModuleAgentController.END_OF_PERCEPTIONS.equals(
				evt.getPropertyName())) {
				for(String perceptionlist : this.perceptionlistNames) {
					this.getEventMediator().propertyChange(new PropertyChangeEvent(this,
						perceptionlist, null, evt.getPropertyName()));
				}
				return;
			}

			// simple data
			if(!(evt.getNewValue() instanceof Event)) {
				this.getEventMediator().propertyChange(evt);
				return;
			}
		
			// event
			Event event = (Event)evt.getNewValue();
			Entity entity = event;

			// if the event is a PhysicalObjectPerceptionEvent then get the perceived
			// object
			if(event instanceof PhysicalObjectPerceptionEvent) {
				Physical physicalObject = ((PhysicalObjectPerceptionEvent)event).
					getPerceivedPhysicalObject();
				if(physicalObject instanceof Entity) {
					entity = (Entity)physicalObject;
				}
			}

			// if the event is an InMessageEvent then get the message
			if(event instanceof InMessageEvent) {
				entity = ((InMessageEvent)event).getMessage();
			}

			// create the gui fragments and puts them into the perceived entities map
			// if the entity is perceived the first time
			if(!perceivedEntities.containsKey(entity.getId())) {
				perceivedEntities.put(entity.getId(), this.createGUIFragment(entity));
			} 

			// gets the gui components for the entity
			Pair<EventMediator, Map<String, GUIComponent>> guiPair =
				this.perceivedEntities.get(entity.getId());

			// update the gui fragments
			for(String entityPropertyName : entity.getProperties().keySet()) {
				guiPair.value1.propertyChange(new PropertyChangeEvent(entity,
					entityPropertyName, null, entity.getProperties().
					get(entityPropertyName)));
			}

			//update the view
			for(String perceptionlist : guiPair.value2.keySet()) {
				this.getEventMediator().propertyChange(new PropertyChangeEvent(this,
					perceptionlist, null, guiPair.value2.get(perceptionlist)));
			}
		}
	}
	
	/**
	 * Creates the mediator and gui fragments for a perceived entity.
	 * @param entity
	 * @return the {@link Pair} of the {@link GUIComponent}s representing the
	 *         entity and its {@link EventMediator}
	 */
	private Pair<EventMediator, Map<String, GUIComponent>> createGUIFragment(
		Entity entity) {

		// the mediator for this gui fragment
		EventMediator mediator = new EventMediator(this);

		// map of all perceptionlists having a perception entry for the current
		// entity and the corresponding entity's gui fragements
		Map<String, GUIComponent> perceptionGUIs = new HashMap<String,
			GUIComponent>();

		// create the gui fragments and adds them to the perceptionGUIs map
		for(Element perception : this.perceptions.get(entity.getType())) {

			// constructs the gui fragment's structure
			Element perceptionGUI = new Element("div").setContent(perception.
				cloneContent());
			@SuppressWarnings("unchecked")
			List<Attribute> attributes = perception.getAttributes();
			for(Attribute attribute : attributes) {
				if(!attribute.getName().equals(Receiver.RECEIVER_ATTRIBUTE)) {
					perceptionGUI.setAttribute((Attribute)attribute.clone());
				}
			}

			// renders the gui fragment
			try {
				this.renderer.doRender(new DOMOutputter().output(new Document(
					perceptionGUI)), this.sourceURI,	mediator);
			} catch(JDOMException exception) {
				exception.printStackTrace();
				continue;
			}
			perceptionGUIs.put(perception.getAttributeValue(Receiver.
				RECEIVER_ATTRIBUTE), this.renderer.getGUIComponent());
		}
							
		// returns the entity's mediator and the set of gui fragments
		return new Pair<EventMediator, Map<String, GUIComponent>>(mediator,
			perceptionGUIs);
	}
}