package aors.module.agentControl.gui;

import aors.model.agtsim.proxy.agentcontrol.CoreAgentController;
import aors.module.GUIModule;
import aors.module.Module;
import aors.module.agentControl.AgentController;
import aors.module.agentControl.ModuleController;
import aors.module.agentControl.gui.views.ControlView;
import aors.module.agentControl.gui.views.DefaultView;
import aors.module.agentControl.gui.views.SelectionView;
import aors.module.agentControl.gui.views.View;
import java.io.FileNotFoundException;
import java.util.Map;
import javax.swing.JScrollPane;

/**
 * This class is the module's main gui class. It serves as a view for the
 * module's controller but for the entire gui it has the role of a controller.
 * @author Thomas Grundmann
 */
public class GUIController extends JScrollPane implements GUIModule {

  private final static long serialVersionUID = 1L;

	/**
	 * Reference to the module's main class.
	 */
	private ModuleController moduleController;

	/**
	 * The current view that is displayed.
	 */
	private View currentView;

	/**
	 * The default view that shall be displayed.
	 */
	private DefaultView defaultView;

	/**
	 * The selection view that shall be displayed.
	 */
	private SelectionView selectionView;

	/**
	 * The control view that shall be displayed.
	 */
	private ControlView controlView;

	/**
	 * The agent controller for the controlled agent.
	 */
	private AgentController controlledAgentController;

	private String controlViewLanguage;

	/*******************************************************/
	/*** Constructor and methods inherite from GUIModule ***/
	/*******************************************************/

	/**
	 * Instantiates the class.
	 * @param moduleController
	 */
	public GUIController(ModuleController moduleController) {
    super();
		this.moduleController = moduleController;
		this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
		reset();
	}

  @Override
  public Module getBaseComponent() {
    return this.moduleController;
  }

	/****************************************************/
	/*** methods responsible for displaying the views ***/
	/****************************************************/

	/**
	 * Resets the views as well as the reference to the controlled agent's
	 * controller.
	 */
	public void reset() {
		this.controlledAgentController = null;

		this.defaultView = new DefaultView();
		this.selectionView = null;
		this.controlView = null;

		this.showDefaultView();
	}

	/**
	 * Upates the current view.
	 */
	public void update() {

		// if not visible, do nothing
		if(!this.isVisible()) {
			return;
		}
		
		// if there is no controller, show default view - should never happen
		if(this.moduleController == null) {
			this.showDefaultView();
			return;
		}
		
		// if there is no controlled agent, show selection view
		if(this.controlledAgentController == null) {
			this.showSelectionView();
			return;
		}

		// otherwise show control view
		this.showControlView();
	}

	/**
	 * Sets the default view as the one to be shown.
	 */
	private void showDefaultView() {
		this.currentView = this.defaultView;
		this.setViewportView();
	}

	/**
	 * Creates an new selection view based on the known controllable agents and
	 * sets it as the view to be shown. If there is no selection view available
	 * the current view remains unchanged. If the current view is null the
	 * default view will be set.
	 */
	private void showSelectionView() {

		// creates the new selection view
		if(this.moduleController != null) {
			Map<Long, CoreAgentController> agentControllers =
				moduleController.getAgentControllers();
			if(agentControllers != null && !agentControllers.isEmpty()) {
				this.selectionView = new SelectionView(this, agentControllers);
			}
		}

		// if the selection view is available, set it as the current one
		if(this.selectionView != null) {
			this.currentView = selectionView;
			this.setViewportView();
			return;
		}

		//if the current view is null, set the default view
		if(this.currentView == null) {
			showDefaultView();
			return;
		}

		// nothing is done, so the current view is unchanged
	}

	/**
	 * Creates an new control view for the controlled agent if there is none. If
	 * there is already a control view, this one will be shown. If there is no
	 * control view available the current view remains unchanged. If the current
	 * view is null the default view will be set.
	 */
	private void showControlView() {

		// creates a new control view if necessary
		if(this.controlView == null) {
			try {
				this.controlView = new ControlView(this.controlledAgentController,
					this.moduleController.getProjectPath(), this.controlViewLanguage);
			} catch(FileNotFoundException e) {
			}
		}

		// if the control view is available, show it
		if(this.controlView != null) {
			this.currentView = this.controlView;
			this.setViewportView();
			return;
		}

		//if the current view is null, set the default view
		if(this.currentView == null) {
			showDefaultView();
			return;
		}

		// nothing is done, so the current view is unchanged
	}

	/**
	 * Sets the current view's gui component as the new viewport view so that it
	 * will be shown.
	 */
	private void setViewportView() {
		this.setViewportView(currentView.getGUIComponent());
	}

	/**
	 * Sets the visiblity state of this component and updates the view.
	 * @param aFlag
	 */
	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		this.update();
	}

	/**
	 * Sets the controlled agent's controller based on the agent's id and shows
	 * the corresponding control view.
	 * @param id
	 * @param lang
	 */
	public void setControlledAgentController(long id, String lang) {
		if(this.moduleController != null) {
			Map<Long, CoreAgentController> agentControllers =	this.moduleController.
				getAgentControllers();
			if(agentControllers != null) {
				this.controlledAgentController = new AgentController(agentControllers.get(id));
				this.controlViewLanguage = lang;
				this.showControlView();
			}
		}
	}
}