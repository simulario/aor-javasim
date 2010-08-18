package aors.module.agentControl.gui.renderer.flyingsaucer;

import aors.module.agentControl.gui.GUIComponent;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.Renderer;
import aors.module.agentControl.gui.renderer.AbstractRendererFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.ProcessingInstruction;
import org.jdom.filter.ContentFilter;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;

/**
 * Creates a renderer using the flying saucer framework.
 * @author Thomas Grundmann
 */
public class FlyingSaucerRendererFactory implements AbstractRendererFactory {

	/**
	 * Instantiates the factory.
	 */
	public FlyingSaucerRendererFactory() {}

	/**
	 * Instantiates the factory with no parameters.
	 * @return the instance
	 */
	public FlyingSaucerRendererFactory instantiate() {
		return new FlyingSaucerRendererFactory();
	}

	/**
	 * Creates a new instance of the renderer.
	 * @return the new instance or <code>null</code> if the renderet could not
	 *         have be instantiated
	 */
	public Renderer createRenderer() {
		return this.new FlyingSaucerAdapter();
	}

	/**
	 * Adapter for the flying saucer renderer.
	 */
	private class FlyingSaucerAdapter implements Renderer {

		private final static String XML_STYLESHEET_PI = "xml-stylesheet";
		
		/**
		 * The gui component created by the renderer.
		 */
		private AORSPanel GUIComponent;

		/**
		 * Map of styles for a given URI.
		 */
		private Map<String, Element> styleMap;

		/**
		 * Instantiates the adapter.
		 */
		public FlyingSaucerAdapter() {
			this.GUIComponent = new AORSPanel();
			this.styleMap = new HashMap<String, Element>();
		}

		/**
		 * Returns the gui component created by the renderer.
		 * @return the gui component
		 */
		public GUIComponent getGUIComponent() {
			return this.GUIComponent;
		}

		/**
		 * Renders the given document that is located at the given uri and uses the
		 * given event mediator for communcation.
		 * @param sourceDocument
		 * @param sourceURI
		 * @param eventMediator
		 */
		public void doRender(Document sourceDocument, String sourceURI,
			EventMediator eventMediator) {
			if(sourceDocument == null) {
				return;
			}
			if(eventMediator == null) {
				eventMediator = new EventMediator(null);
			}

			// jdom-document for modifications
			org.jdom.Document domDocument = new DOMBuilder().build(sourceDocument);

			// gets the xml-stylesheet processing instruction
			ProcessingInstruction xml_stylesheet_pi = null;
			ContentFilter piFilter = new ContentFilter(false);
			piFilter.setPIVisible(true);
			@SuppressWarnings("unchecked")
			List<ProcessingInstruction> pis = domDocument.getContent(piFilter);
			for(ProcessingInstruction pi : pis) {
				if(FlyingSaucerAdapter.XML_STYLESHEET_PI.equals(pi.getTarget())) {
					xml_stylesheet_pi = pi;
					break;
				}
			}

			// if the stylemap has no entry for the given sourceURI, add a new entry
			if(!this.styleMap.containsKey(sourceURI)) {

				// adds an entry for the current uri
				this.styleMap.put(sourceURI, null);


				// if there is a stylesheet processing instruction, remove it from the
				// document, create a style-element based on its content and update the
				// map
				if(xml_stylesheet_pi != null) {

					// gets the processing instruction's pseudo attribute values
					String type = xml_stylesheet_pi.getPseudoAttributeValue("type");
					String href = xml_stylesheet_pi.getPseudoAttributeValue("href");
					if(!URI.create(href).isAbsolute()) {
						href = (sourceURI.substring(0, sourceURI.lastIndexOf("/")) +
							"/" + href);
					}

					// reads the style data
					StringBuilder styleData = new StringBuilder();
					try {
						Scanner styleScanner = new Scanner(new File(URI.create(href)));
						while(styleScanner.hasNext()) {
							styleData.append(styleScanner.nextLine());
						}
						styleScanner.close();
					} catch(FileNotFoundException exception) {
						//do nothing
					}

					// adds the new style data to the style map
					this.styleMap.put(sourceURI, new Element("style").setAttribute("type",
						type).setAttribute("style", "display: none").setText(styleData.
						toString()));
				}
			}

			// if there is a stylesheet processing instruction, remove it
			domDocument.removeContent(xml_stylesheet_pi);

			// if style data are available for the document, add the style element
			Element styleData = this.styleMap.get(sourceURI);
			if(styleData != null) {
				domDocument = new org.jdom.Document().setRootElement(domDocument.
					detachRootElement().addContent((Element)styleData.clone()));
			}
			
			// update the sourceDocument
			try {
				sourceDocument = new DOMOutputter().output(domDocument);
			} catch(JDOMException exception) {
				exception.printStackTrace();
				//do nothing
			}

			// render the document
			AORSPanel panel = new AORSPanel();
			panel.getSharedContext().setReplacedElementFactory(
				new AORSReplacedElementFactory(eventMediator));
			panel.setDocument(sourceDocument, sourceURI, new AORSNamespaceHandler());
			this.GUIComponent = panel;
		}
	}
}