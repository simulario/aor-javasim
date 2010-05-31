package aors.module.agentControl.gui.views;

import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DefaultView implements View {
  private static final long serialVersionUID = 1L;
	private JPanel guiComponent;
 
  public DefaultView() {
		guiComponent = new JPanel(new FlowLayout());
    guiComponent.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    guiComponent.setAlignmentY(JPanel.CENTER_ALIGNMENT);
    guiComponent.add(new JLabel("There is no agent that can be controlled!"));
  }

	@Override
	public JComponent getGUIComponent() {
		return this.guiComponent;
	}
}