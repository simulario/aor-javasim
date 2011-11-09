package aors.module.agentControl.gui.renderer.flyingsaucer;

import aors.module.agentControl.gui.interaction.Receiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.simple.xhtml.XhtmlNamespaceHandler;

public class AORSNamespaceHandler extends XhtmlNamespaceHandler {
	
	public AORSNamespaceHandler() {
		super();
	}

	@Override
	public StylesheetInfo[] getStylesheets(Document doc) {
		List<StylesheetInfo> stylesheets = new ArrayList<StylesheetInfo>(
			Arrays.asList(super.getStylesheets(doc)));
		NodeList styleElements = doc.getElementsByTagName("style");
		for(int i = 0; i < styleElements.getLength(); i++) {
			if(styleElements.item(i) instanceof Element) {
				stylesheets.add(readStyleElement((Element)styleElements.item(i)));
			}
		}
		return stylesheets.toArray(new StylesheetInfo[stylesheets.size()]);
	}

	@Override
  public String getNonCssStyling(Element e) {
		if(e.getNodeName().equals("div")) {
			for(String classAttributeValue : e.getAttribute("class").split("\\s+")) {
				if("__perceptionlist".equals(classAttributeValue.trim())) {
					Attr perceptionlistName = e.removeAttributeNode(e.getAttributeNode(
						Receiver.RECEIVER_ATTRIBUTE));
					Element updateableArea = e.getOwnerDocument().createElement(
						"updateableArea");
					updateableArea.setAttributeNode(perceptionlistName);
					updateableArea.setAttribute("style",
						"display:block; height: 100%; width: 100%;");
					e.appendChild(updateableArea);
				}
			}
		}
		return super.getNonCssStyling(e);
	}
}