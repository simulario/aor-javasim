package aors.gui.swing;

import java.awt.event.ActionListener;

public class ToolBarFile extends ToolBar {

  private static final long serialVersionUID = -2319733389594318542L;

  public ToolBarFile(ActionListener actionListener) {
    super(actionListener);
    this.setName(Menu.PROJECT);

    this.addButton(Menu.Item.NEW, Menu.Item.NEW_IMAGE);
    this.addButton(Menu.Item.OPEN, Menu.Item.OPEN_IMAGE);
    this.addButton(Menu.Item.SAVE, Menu.Item.SAVE_IMAGE);
    // this.addButton(Menu.Item.EXIT, Menu.Item.EXIT_IMAGE);
    this.addButton(Menu.Item.EXTERNAL_XML_EDITOR,
        Menu.Item.EXTERNAL_XML_EDITOR_IMAGE);
    this.addButton(Menu.Item.EXTERNAL_LOG_VIEWER,
        Menu.Item.EXTERNAL_LOG_VIEWER_IMAGE);
  }

}
