package aors.gui.swing;

import java.awt.event.ActionListener;

public class ToolBarSimDescription extends ToolBar {

  private static final long serialVersionUID = 4199528842039063494L;

  public ToolBarSimDescription(ActionListener actionListener) {
    super(actionListener);
    this.setName(Menu.SIMULATION_DESCRIPTION);

    this.addButton(Menu.Item.RUN, Menu.Item.RUN_IMAGE);

  }

}