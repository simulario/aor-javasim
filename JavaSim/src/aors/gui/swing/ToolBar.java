package aors.gui.swing;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public abstract class ToolBar extends JToolBar {

  /**
	 * 
	 */
  private static final long serialVersionUID = -2644033158871431747L;
  protected ActionListener actionListener;

  /**
   * 
   * Create a new {@code ToolBar}.
   * 
   * @param actionListener
   */
  public ToolBar(ActionListener actionListener) {
    this.actionListener = actionListener;
  }

  protected void addButton(String actionCommand, String imageName) {

    JButton button;
    Image image = ImageLoader.loadImage(imageName);

    if (image != null) {
      button = new JButton(new ImageIcon(image));
    } else {
      button = new JButton(actionCommand);
    }
    button.setToolTipText(actionCommand);
    button.setActionCommand(actionCommand);
    button.setName(actionCommand);
    button.addActionListener(this.actionListener);
    this.add(button);
  }

  protected void addButton(String name, String actionCommand, String imageName) {

    JButton button;
    Image image = ImageLoader.loadImage(imageName);

    if (image != null) {
      button = new JButton(new ImageIcon(image));
    } else {
      button = new JButton(actionCommand);
    }
    button.setToolTipText(actionCommand);
    button.setActionCommand(actionCommand);
    button.setName(name);
    button.addActionListener(this.actionListener);
    this.add(button);
  }

  protected JButton getButtonByActionCommand(String actionCommand) {

    for (Component c : this.getComponents()) {
      if (c != null) {
        if (c instanceof JButton) {
          JButton button = (JButton) c;
          if (button.getActionCommand().equals(actionCommand)) {
            // System.out.println("Found Toolbar Button: " +
            // button.getActionCommand());
            return button;
          }
        }
      }
    }

    return new JButton();
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param name
   * @param status
   */
  public void enableButton(String name, boolean status) {
    Object obj;

    for (Iterator<Component> i = Arrays.asList(this.getComponents()).iterator(); i
        .hasNext();) {
      obj = i.next();
      JButton button;
      if (obj instanceof JButton) {
        button = (JButton) obj;
        if (button.getName().equals(name)) {
          button.setEnabled(status);
          break;
        }
      }
    }
  }

  public void switchButton(String name, String actionCommand, String imageName) {
    Object obj;

    for (Iterator<Component> i = Arrays.asList(this.getComponents()).iterator(); i
        .hasNext();) {
      obj = i.next();
      JButton button;
      if (obj instanceof JButton) {
        button = (JButton) obj;
        if (button.getName().equals(name)) {
          Image image = ImageLoader.loadImage(imageName);
          if (image != null) {
            button.setIcon(new ImageIcon(image));
          }
          button.setToolTipText(actionCommand);
          button.setActionCommand(actionCommand);
          button.setName(actionCommand);
          break;
        }
      }
    }

  }

}
