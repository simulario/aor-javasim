package aors.gui.swing;

import java.awt.event.ActionListener;

import javax.swing.JButton;

@Deprecated
public class Button extends JButton {

  private static final long serialVersionUID = 1501626230971372520L;

  public Button(ActionListener actionListener, String text, String actionCommand) {
    this.addActionListener(actionListener);
    this.setActionCommand(actionCommand);
    this.setText(text);
  }

  protected final static String OK = "Ok";
  protected final static String CANCEL = "Cancel";

  /**
   * Action commands, in order to distinguish an "OK" in dialog A or B. This
   * specifies more or less the context of button clicks.
   */
  protected final static String AC_SYSTEM_EXIT_OK = "System Exit";
  protected final static String AC_SYSTEM_EXIT_CANCEL = "System Exit Cancel";

}
