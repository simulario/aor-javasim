package aors.module.agentControl.gui.views;

import aors.module.agentControl.gui.GUIComponent;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The default view for the agent control module.
 * @author Thomas Grundmann
 */
public class DefaultView implements View {
  private static final long serialVersionUID = 1L;

	/**
	 * The views gui component.
	 */
	private SimplePanel guiComponent;

	/**
	 * Instantiates the view.
	 */
  public DefaultView() {
		this.guiComponent = new SimplePanel(new FlowLayout());
    this.guiComponent.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    this.guiComponent.setAlignmentY(JPanel.CENTER_ALIGNMENT);
    this.guiComponent.add(new JLabel("There is no agent that can be controlled!"));
  }

	/**
	 * Returns the view's gui component.
	 * @return the gui component
	 */
	@Override
	public GUIComponent getGUIComponent() {
		return this.guiComponent;
	}

	/**
	 * Represents a {@link JPanel} as a {@link GUIComponent}.
	 */
	private class SimplePanel extends JPanel implements GUIComponent {
		private final static long serialVersionUID = -1;

		/**
		 * Instantiates the panel.
		 * @param layoutManager
		 */
		SimplePanel(LayoutManager layoutManager) {
			super(layoutManager);
		}
	}
}