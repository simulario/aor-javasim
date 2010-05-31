package aors.module.agentControl.gui;

import aors.model.agtsim.AgentSubject;
import aors.module.GUIModule;
import aors.module.Module;
import aors.module.agentControl.AgentController;
import aors.module.agentControl.ModuleController;
import aors.module.agentControl.gui.views.ControlView;
import aors.module.agentControl.gui.views.DefaultView;
import aors.module.agentControl.gui.views.SelectionView;
import aors.module.agentControl.gui.views.View;
import java.util.Map;
import java.util.Set;
import javax.swing.JScrollPane;

public class GUIManager extends JScrollPane implements GUIModule {

  private final static long serialVersionUID = 1L;
  private ModuleController controller;

	private DefaultView defaultView;
	private SelectionView selectionView;
	private Map<String, ControlView> controlViews;
//	private JComponent vizGui;
	private JScrollPane controlMenu;

	private View currentView;

	public GUIManager(ModuleController controller) {
    super();
		this.controller = controller;
		this.controlMenu = new JScrollPane();
		controlMenu.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    controlMenu.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.defaultView = new DefaultView();
		reset();
	}

//  public GUIManager(ModuleController controller, Object vizGui) {
//		this();
//		this.vizGui = (JComponent)vizGui;
//	}

	public void reset() {
		this.selectionView = null;
		if(this.controlViews != null) {
			this.controlViews.clear();
			this.controlViews = null;
		}
		this.currentView = null;
		this.setDefaultView();
	}

  @Override
  public Module getBaseComponent() {
    return this.controller;
  }

	public void setControlledAgent(long id) {
		controller.setControlledAgent(id);
	}

	public String getProjectPath() {
		return controller.getProjectPath();
	}
	
	public void setDefaultView() {
		this.currentView = this.defaultView;
		this.setViewportView();
	}

	public void setSelectionView() {
		Set<AgentController<? extends AgentSubject>> agentControllers =
			controller.getAgentControllers();
		if(!agentControllers.isEmpty()) {
			this.selectionView = new SelectionView(this, agentControllers);
		}
		if(this.selectionView == null) {
			setDefaultView();
		} else {
			this.currentView = this.selectionView;
			this.setViewportView();
		}
	}

	public void setControlView(AgentController<? extends AgentSubject>
		controlledAgent) {
		ControlView view = new ControlView(this, controlledAgent);
		if(view == null) {
			setDefaultView();
		}
		this.currentView = view;
		this.setViewportView();
	}

	public View getCurrentView() {
		return this.currentView;
	}

	private void setViewportView() {
		this.setViewportView(currentView.getGUIComponent());
	}
}