package aors.module.agentControl.gui.views;

import aors.model.agtsim.AgentSubject;
import aors.module.agentControl.AgentController;
import aors.module.agentControl.InteractiveComponent;
import aors.module.agentControl.gui.GUIManager;
import aors.module.agentControl.gui.renderer.AORSPanel;
import aors.module.agentControl.gui.renderer.AORSReplacedElementFactory;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JComponent;

public class ControlView implements View, InteractiveComponent {

  private static final long serialVersionUID = 1L;

	private AORSPanel guiComponent;

  public ControlView(GUIManager gui, AgentController<? extends AgentSubject>
		controllableAgent) {		
		String sep = File.separator;
		String type = controllableAgent.getSubject().getType();
		if(type.endsWith("AgentSubject")) {
			type = type.substring(0, type.lastIndexOf("AgentSubject"));
		}
		String projectPath = gui.getProjectPath();
		if(projectPath == null) {
			projectPath = ".";
		}
		String guiPath = gui.getProjectPath() + sep + "src" + sep + "interaction" +
			sep + "agentcontrol" + sep + type + ".gui";

		this.guiComponent = new AORSPanel();
		controllableAgent.setKeyListeners(this);
		this.guiComponent.getSharedContext().setReplacedElementFactory(
			new AORSReplacedElementFactory(controllableAgent.getMediator()));
		this.guiComponent.setDocument(new File(guiPath).toURI().toString());
	}


	@Override
	public JComponent getGUIComponent() {
		return this.guiComponent;
	}

		@Override
	public void addMouseListener(MouseListener mouseListener) {
		this.guiComponent.addMouseListener(mouseListener);
	}

	@Override
	public void addKeyListener(KeyListener keyListener) {
		this.guiComponent.addKeyListener(keyListener);
	}

	@Override
	public boolean isFocusable() {
		return this.guiComponent.isFocusable();
	}

	@Override
	public void setFocusable(boolean focusable) {
		this.guiComponent.setFocusable(focusable);
	}

	@Override
	public boolean requestFocusInWindow() {
		return this.guiComponent.requestFocusInWindow();
	}
}