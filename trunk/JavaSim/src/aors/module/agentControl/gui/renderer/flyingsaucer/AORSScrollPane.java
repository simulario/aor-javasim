package aors.module.agentControl.gui.renderer.flyingsaucer;

import aors.module.agentControl.gui.GUIComponent;
import org.xhtmlrenderer.simple.FSScrollPane;

public class AORSScrollPane extends FSScrollPane implements GUIComponent {
	private static final long serialVersionUID = 1L;

	public AORSScrollPane(AORSPanel panel) {
		super(panel);
	}
}